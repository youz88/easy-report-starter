package com.github.youz.report.service.impl;

import com.github.youz.report.enums.ExceptionCode;
import com.github.youz.report.enums.OperationType;
import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.export.handler.CompositeExportHandler;
import com.github.youz.report.export.model.ExportContext;
import com.github.youz.report.export.handler.ExportBusinessHandler;
import com.github.youz.report.export.model.PreExportResult;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.model.table.ReportTaskTableDef;
import com.github.youz.report.repository.ReportTaskMapper;
import com.github.youz.report.service.ReportService;
import com.github.youz.report.util.ExcelExportUtil;
import com.github.youz.report.util.JsonUtil;
import com.github.youz.report.web.dto.ExportFileDTO;
import com.github.youz.report.web.dto.ImportFileDTO;
import com.github.youz.report.web.dto.ReportListDTO;
import com.github.youz.report.web.vo.ImportFileVO;
import com.github.youz.report.web.vo.ReportInfoVO;
import com.github.youz.report.web.vo.ReportListVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportTaskMapper reportTaskMapper;

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
                .setContext(JsonUtil.toJson(ExportContext.build(preExportResult, reqDTO)))
                .setCreateTime(Instant.now().getEpochSecond())
                .setUpdateTime(Instant.now().getEpochSecond());

        // 保存报表任务
        reportTaskMapper.insertSelective(reportTask);

        // 执行导出
        ExcelExportUtil.webExport(handler, reportTask, response);
    }

    @Override
    public ReportInfoVO fileInfo(Long id) {
        return ReportInfoVO.assemblyData(reportTaskMapper.selectOneById(id));
    }

    @Override
    public ReportListVO fileList(ReportListDTO reqDTO) {
        ReportTaskTableDef def = ReportTaskTableDef.REPORT_TASK;
        QueryWrapper query = QueryWrapper.create()
                .and(def.USER_ID.eq(reqDTO.getUserId()));
        if (Objects.nonNull(reqDTO.getBusinessType())) {
            query.and(def.BUSINESS_TYPE.eq(reqDTO.getBusinessType()));
        }
        Page<ReportTask> pageInfo = reportTaskMapper.paginate(Page.of(reqDTO.getPageNum(), reqDTO.getPageSize()), query);
        return ReportListVO.assemblyData(pageInfo);
    }

    public static void main(String[] args) {
        System.out.println();
    }
}
