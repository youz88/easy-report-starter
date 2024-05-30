package com.github.youz.report.service.impl;

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
                .setContext(JsonUtil.toJson(ExportContext.build(preExportResult, reqDTO)));

        // 保存报表任务
        reportTaskData.insert(reportTask);

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

}
