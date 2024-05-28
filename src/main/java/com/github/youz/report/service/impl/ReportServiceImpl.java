package com.github.youz.report.service.impl;

import com.github.youz.report.enums.ExceptionCode;
import com.github.youz.report.enums.OperationType;
import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.handler.CompositeExportHandler;
import com.github.youz.report.handler.ExportContext;
import com.github.youz.report.handler.ExportHandler;
import com.github.youz.report.handler.ExportTotal;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.model.table.ReportTaskTableDef;
import com.github.youz.report.repository.ReportTaskMapper;
import com.github.youz.report.service.ReportService;
import com.github.youz.report.util.JsonUtil;
import com.github.youz.report.web.dto.ExportFileDTO;
import com.github.youz.report.web.dto.ImportFileDTO;
import com.github.youz.report.web.dto.ReportListDTO;
import com.github.youz.report.web.vo.ExportFileVO;
import com.github.youz.report.web.vo.ImportFileVO;
import com.github.youz.report.web.vo.ReportInfoVO;
import com.github.youz.report.web.vo.ReportListVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
    public ExportFileVO exportFile(ExportFileDTO reqDTO) {
        // 获取导出处理器
        ExportHandler handler = compositeExportHandler.getHandler(reqDTO.getBusinessType());

        // 构建导出上下文 & 获取总条数
        ExportContext exportContext = ExportContext.build(reqDTO);
        ExportTotal total = handler.total(exportContext);

        // 校验总条数
        ExceptionCode.EXPORT_DATA_EMPTY.assertGtZero(total.getTotal());

        // 初始化报表任务
        ReportTask reportTask = JsonUtil.convert(reqDTO, ReportTask.class)
                .setOpType(OperationType.EXPORT.getCode())
                .setExecType(total.getExecType().getCode())
                .setStatus(ReportStatus.WAIT.getCode())
                .setContext(JsonUtil.toJson(exportContext))
                .setFileName(total.getFileName());

        // 保存报表任务
        reportTaskMapper.insertSelective(reportTask);
        return ExportFileVO.assemblyData(reportTask);
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
}
