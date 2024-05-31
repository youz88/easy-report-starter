package com.github.youz.report.service.impl;

import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.data.ReportTaskData;
import com.github.youz.report.enums.ExceptionCode;
import com.github.youz.report.enums.OperationType;
import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.export.bo.common.ExportContext;
import com.github.youz.report.export.bo.common.PreExportResult;
import com.github.youz.report.export.handler.CompositeExportHandler;
import com.github.youz.report.export.handler.ExportBusinessHandler;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.service.ReportService;
import com.github.youz.report.util.ExcelExportUtil;
import com.github.youz.report.util.JsonUtil;
import com.github.youz.report.web.dto.ExportFileDTO;
import com.github.youz.report.web.dto.ImportFileDTO;
import com.github.youz.report.web.dto.ReportListDTO;
import com.github.youz.report.web.vo.ImportFileVO;
import com.github.youz.report.web.vo.PageVO;
import com.github.youz.report.web.vo.ReportInfoVO;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportTaskData reportTaskData;

    private final CompositeExportHandler compositeExportHandler;

    @Override
    public ImportFileVO importFile(ImportFileDTO reqDTO) {
        return null;
    }

    @Override
    public void exportFile(ExportFileDTO reqDTO, HttpServletResponse response) {
        // 获取导出处理器
        ExportBusinessHandler handler = compositeExportHandler.getHandler(reqDTO.getBusinessType());

        // 获取总条数 & 构建导出上下文
        PreExportResult preExportResult = handler.preExport(reqDTO.getQueryParam());

        // 校验总条数
        ExceptionCode.EXPORT_DATA_EMPTY.assertGtZero(preExportResult.getTotal());

        // 初始化报表任务
        ReportTask reportTask = JsonUtil.convert(reqDTO, ReportTask.class)
                .setOpType(OperationType.EXPORT.getCode())
                .setStatus(ReportStatus.WAIT.getCode())
                .setExecType(preExportResult.getExecType())
                .setFileName(preExportResult.getFileName())
                .setSlicedIndex(preExportResult.getSlicedIndex())
                .setContext(JsonUtil.toJson(ExportContext.build(preExportResult, reqDTO)));

        // 保存报表任务
        reportTaskData.insert(reportTask);

        // 任务拆分
        slicedReportTask(reportTask);

        // 执行导出
        ExcelExportUtil.webExport(handler, reportTask, response);
    }

    @Override
    public ReportInfoVO fileInfo(Long id) {
        return ReportInfoVO.assemblyData(reportTaskData.selectById(id));
    }

    @Override
    public PageVO<ReportInfoVO> fileList(ReportListDTO reqDTO) {
        Page<ReportTask> pageInfo = reportTaskData.pageInfo(reqDTO);
        return ReportInfoVO.assemblyData(pageInfo);
    }

    /**
     * 将报表任务进行拆分并插入数据库
     *
     * @param reportTask 报表任务对象
     */
    private void slicedReportTask(ReportTask reportTask) {
        // 任务拆分, 判断是否需要拆分
        if (reportTask.getSlicedIndex() <= ReportConst.ONE) {
            return;
        }

        // 任务拆分, 初始化异步切片任务
        List<ReportTask> slicedTaskList = IntStream.range(ReportConst.ZER0, reportTask.getSlicedIndex())
                .mapToObj(chunk -> {
                    int slicedIndex = chunk + ReportConst.ONE;
                    return JsonUtil.convert(reportTask, ReportTask.class)
                            .setId(null)
                            .setPid(reportTask.getId())
                            .setSlicedIndex(slicedIndex)
                            .setFileName(reportTask.getFileName() + ReportConst.UNDER_LINE_SYMBOL + slicedIndex);
                }).collect(Collectors.toList());

        // 批量插入切片任务
        reportTaskData.batchInsert(slicedTaskList);
    }
}
