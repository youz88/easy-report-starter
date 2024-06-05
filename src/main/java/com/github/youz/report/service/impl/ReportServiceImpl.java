package com.github.youz.report.service.impl;

import com.github.youz.report.config.ExportProperties;
import com.github.youz.report.constant.ReportConst;
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
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.service.ReportService;
import com.github.youz.report.util.ExcelExportUtil;
import com.github.youz.report.util.JsonUtil;
import com.github.youz.report.util.MultipartFileUtil;
import com.github.youz.report.web.dto.ExportFileDTO;
import com.github.youz.report.web.dto.ImportFileDTO;
import com.github.youz.report.web.dto.ReportListDTO;
import com.github.youz.report.web.vo.ImportFileVO;
import com.github.youz.report.web.vo.PageVO;
import com.github.youz.report.web.vo.ReportInfoVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportTaskData reportTaskData;

    private final ExportProperties exportProperties;

    private final CompositeExportHandler compositeExportHandler;

    @Override
    public ImportFileVO importCloudFile(ImportFileDTO reqDTO) {
        // TODO 限制重复导入
        String localFilePath = MultipartFileUtil.downloadFile(reqDTO.getUploadFilePath());
        return importFile(reqDTO, localFilePath, MultipartFileUtil.getFileName(reqDTO.getUploadFilePath()));
    }

    @Override
    public ImportFileVO importLocalFile(MultipartFile file, ImportFileDTO reqDTO) {
        // TODO 限制重复导入
        String localFilePath = MultipartFileUtil.downloadFile(file);
        return importFile(reqDTO, localFilePath, MultipartFileUtil.getFileName(file.getOriginalFilename()));
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
        return ReportInfoVO.assemblyData(reportTask, exportProperties.isUploadCloud());
    }

    @Override
    public PageVO<ReportInfoVO> fileList(ReportListDTO reqDTO) {
        Page<ReportTask> pageInfo = reportTaskData.pageInfo(reqDTO);
        return ReportInfoVO.assemblyData(pageInfo, exportProperties.isUploadCloud());
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
     * @param reqDTO        导入文件所需的请求参数
     * @param localFilePath 本地文件路径
     * @param fileName      文件名
     * @return 导入文件的VO对象
     */
    private ImportFileVO importFile(ImportFileDTO reqDTO, String localFilePath, String fileName) {
        ExceptionCode.DOWNLOAD_FAIL.assertIsTrue(StringUtil.isNotBlank(localFilePath));

        // 初始化报表任务
        ReportTask reportTask = JsonUtil.convert(reqDTO, ReportTask.class)
                .setOpType(OperationType.IMPORTS.getCode())
                .setExecType(ExecutionType.ASYNC.getCode())
                .setStatus(ReportStatus.WAIT.getCode())
                .setFileName(fileName)
                .setLocalFilePath(localFilePath)
                .setContext(JsonUtil.toJson(ImportContext.build(reqDTO)));

        // 保存报表任务
        reportTaskData.insert(reportTask);
        return ImportFileVO.assemblyData(reportTask);
    }
}
