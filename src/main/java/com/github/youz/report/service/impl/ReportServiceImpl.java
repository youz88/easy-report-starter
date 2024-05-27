package com.github.youz.report.service.impl;

import com.github.youz.report.model.ReportTask;
import com.github.youz.report.model.table.ReportTaskTableDef;
import com.github.youz.report.repository.ReportTaskMapper;
import com.github.youz.report.service.ReportService;
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

    @Override
    public ImportFileVO importFile(ImportFileDTO reqDTO) {
        return null;
    }

    @Override
    public ExportFileVO exportFile(ExportFileDTO reqDTO) {
        return null;
    }

    @Override
    public ReportInfoVO fileInfo(Long id) {
        return ReportInfoVO.assemblyData(reportTaskMapper.selectOneById(id));
    }

    @Override
    public Page<ReportListVO> fileList(ReportListDTO reqDTO) {
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
