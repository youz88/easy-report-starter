package com.github.youz.report.service.impl;

import com.github.youz.report.config.ReportProperties;
import com.github.youz.report.constant.CacheConst;
import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.data.RedisData;
import com.github.youz.report.data.ReportTaskData;
import com.github.youz.report.enums.ExceptionCode;
import com.github.youz.report.enums.ExecutionType;
import com.github.youz.report.enums.OperationType;
import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.export.bo.ExportContext;
import com.github.youz.report.export.bo.PreExportResult;
import com.github.youz.report.export.handler.CompositeExportHandler;
import com.github.youz.report.export.handler.ExportBusinessHandler;
import com.github.youz.report.imports.bo.ImportContext;
import com.github.youz.report.imports.listener.CompositeImportHandler;
import com.github.youz.report.imports.listener.ImportBusinessListener;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.service.ReportService;
import com.github.youz.report.util.*;
import com.github.youz.report.web.dto.ExportFileDTO;
import com.github.youz.report.web.dto.ImportFileDTO;
import com.github.youz.report.web.dto.ReportListDTO;
import com.github.youz.report.web.vo.ImportFileVO;
import com.github.youz.report.web.vo.PageVO;
import com.github.youz.report.web.vo.ReportInfoVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportTaskData reportTaskData;

    private final RedisData redisData;

    private final ReportProperties reportProperties;

    private final CompositeExportHandler compositeExportHandler;

    private final CompositeImportHandler compositeImportHandler;

    @Override
    public ImportFileVO importCloudFile(ImportFileDTO reqDTO) {
        return importFile(reqDTO, MultipartFileUtil.getFileName(reqDTO.getUploadFilePath()),
                () -> MultipartFileUtil.downloadFile(reqDTO.getUploadFilePath()));
    }

    @Override
    public ImportFileVO importLocalFile(MultipartFile file, ImportFileDTO reqDTO) {
        return importFile(reqDTO, MultipartFileUtil.getFileName(file.getOriginalFilename()),
                () -> MultipartFileUtil.downloadFile(file));
    }

    @Async
    @Override
    public void asyncImport(ReportTask reportTask) {
        // 获取导出处理器
        ImportBusinessListener listener = compositeImportHandler.getListener(reportTask.getBusinessType());
        try {
            // 读取导入文件
            listener.importFile(ImportContext.build(reportTask));
        } catch (Exception e) {
            log.warn("导入文件失败", e);

            // 更新导入任务状态
            ReportTask update = new ReportTask()
                    .setId(reportTask.getId())
                    .setStatus(ReportStatus.IMPORT_FAIL.getCode())
                    .setErrorMsg(e.getMessage())
                    .setCompleteTime(DateUtil.now());
            reportTaskData.updateById(update);
        } finally {
            // 删除导入缓存
            String cacheKey = String.format(CacheConst.REPORT_IMPORT_KEY, reportTask.getUserId(), reportTask.getBusinessType());
            ApplicationContextUtil.getBean(RedisData.class).importUnlock(cacheKey);
        }
    }

    @Override
    public void exportFile(ExportFileDTO reqDTO, HttpServletResponse response) {
        // 获取导出处理器
        ExportBusinessHandler handler = compositeExportHandler.getHandler(reqDTO.getBusinessType());

        // 预导出结果
        PreExportResult preExportResult = handler.preExport(reqDTO.getQueryParam());

        // 校验总条数
        ExceptionCode.EXPORT_DATA_EMPTY.assertGtZero(preExportResult.getTotal());

        // 初始化报表任务
        ExportContext context = ExportContext.build(preExportResult, reqDTO);
        ReportTask reportTask = JsonUtil.convert(reqDTO, ReportTask.class)
                .setOpType(OperationType.EXPORT.getCode())
                .setStatus(ReportStatus.WAIT.getCode())
                .setExecType(preExportResult.getExecType())
                .setFileName(preExportResult.getFileName())
                .setSlicedIndex(preExportResult.getSlicedIndex())
                .setContext(JsonUtil.toJson(context));

        // 保存报表任务
        reportTaskData.insert(reportTask);

        // 任务拆分
        slicedReportTask(reportTask, context);

        // 执行导出
        ExcelExportUtil.webExport(handler, reportTask, response);
    }

    @Override
    public ReportInfoVO fileInfo(Long id) {
        ReportTask reportTask = reportTaskData.selectById(id);
        return ReportInfoVO.assemblyData(reportTask, reportProperties.getCommon().isUploadCloud());
    }

    @Override
    public PageVO<ReportInfoVO> fileList(ReportListDTO reqDTO) {
        Page<ReportTask> pageInfo = reportTaskData.pageInfo(reqDTO);
        return ReportInfoVO.assemblyData(pageInfo, reportProperties.getCommon().isUploadCloud());
    }

    /**
     * 将报表任务进行拆分并插入数据库
     *
     * @param reportTask 报表任务对象
     * @param context    导出上下文参数
     */
    private void slicedReportTask(ReportTask reportTask, ExportContext context) {
        // 任务拆分, 判断是否需要拆分
        if (reportTask.getSlicedIndex() <= ReportConst.ONE) {
            return;
        }

        // 任务拆分, 初始化异步切片任务
        List<ReportTask> slicedTaskList = IntStream.range(ReportConst.ZER0, reportTask.getSlicedIndex())
                .mapToObj(chunk -> {
                    // 分片索引
                    int slicedIndex = chunk + ReportConst.ONE;
                    // 文件名称
                    String slicedFileName = reportTask.getFileName() + ReportConst.UNDER_LINE_SYMBOL + slicedIndex;

                    // 预导出结果
                    ExportContext slicedContext = JsonUtil.convert(context, ExportContext.class)
                            .setSlicedIndex(slicedIndex)
                            .setFileName(slicedFileName);
                    return JsonUtil.convert(reportTask, ReportTask.class)
                            .setId(null)
                            .setPid(reportTask.getId())
                            .setSlicedIndex(slicedIndex)
                            .setFileName(slicedFileName)
                            .setContext(JsonUtil.toJson(slicedContext));
                }).collect(Collectors.toList());

        // 批量插入切片任务
        reportTaskData.batchInsert(slicedTaskList);
    }

    /**
     * 导入文件
     *
     * @param reqDTO                导入文件所需的请求参数
     * @param fileName              文件名
     * @param localFilePathSupplier 用于获取本地文件路径的Supplier对象
     * @return 导入文件的VO对象
     */
    private ImportFileVO importFile(ImportFileDTO reqDTO, String fileName, Supplier<String> localFilePathSupplier) {
        // 导入前检查
        importPreCheck(reqDTO);

        // 获取本地文件路径
        String localFilePath = localFilePathSupplier.get();
        ExceptionCode.IMPORT_DOWNLOAD_FAIL.assertIsTrue(StringUtil.isNotBlank(localFilePath));

        // 初始化报表任务
        ReportTask reportTask = JsonUtil.convert(reqDTO, ReportTask.class)
                .setOpType(OperationType.IMPORTS.getCode())
                .setExecType(ExecutionType.ASYNC.getCode())
                .setStatus(ReportStatus.WAIT.getCode())
                .setFileName(fileName)
                .setLocalFilePath(localFilePath);

        // 保存报表任务
        reportTaskData.insert(reportTask);

        // 异步执行导入任务
        ApplicationContextUtil.getBean(ReportService.class).asyncImport(reportTask);

        // 返回导入文件VO对象
        return ImportFileVO.assemblyData(reportTask);
    }

    /**
     * 导入前检查
     *
     * @param reqDTO 导入文件请求参数
     */
    private void importPreCheck(ImportFileDTO reqDTO) {
        // 校验是否已有导入任务在执行中
        String cacheKey = String.format(CacheConst.REPORT_IMPORT_KEY, reqDTO.getUserId(), reqDTO.getBusinessType());
        ExceptionCode.IMPORT_IN_PROGRESS.assertIsTrue(redisData.importLock(cacheKey));
    }
}
