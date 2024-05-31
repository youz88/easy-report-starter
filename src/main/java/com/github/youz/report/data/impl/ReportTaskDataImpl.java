package com.github.youz.report.data.impl;

import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.data.ReportTaskData;
import com.github.youz.report.enums.ExecutionType;
import com.github.youz.report.enums.OperationType;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.model.table.ReportTaskTableDef;
import com.github.youz.report.repository.ReportTaskMapper;
import com.github.youz.report.util.DateUtil;
import com.github.youz.report.web.dto.ReportListDTO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ReportTaskDataImpl implements ReportTaskData {

    private final ReportTaskMapper reportTaskMapper;

    @Override
    public ReportTask selectById(Long id) {
        return reportTaskMapper.selectOneById(id);
    }

    @Override
    public Page<ReportTask> pageInfo(ReportListDTO reqDTO) {
        ReportTaskTableDef def = ReportTaskTableDef.REPORT_TASK;
        QueryWrapper query = QueryWrapper.create()
                .and(def.USER_ID.eq(reqDTO.getUserId()));
        if (Objects.nonNull(reqDTO.getBusinessType())) {
            query.and(def.BUSINESS_TYPE.eq(reqDTO.getBusinessType()));
        }
        return reportTaskMapper.paginate(Page.of(reqDTO.getPageNum(), reqDTO.getPageSize()), query);
    }

    @Override
    public void insert(ReportTask reportTask) {
        reportTask.setCreateTime(DateUtil.now())
                .setUpdateTime(DateUtil.now());
        reportTaskMapper.insertSelective(reportTask);
    }

    @Override
    public void batchInsert(List<ReportTask> reportTaskList) {
        reportTaskMapper.insertBatch(reportTaskList);
    }

    @Override
    public void updateById(ReportTask reportTask) {
        UpdateChain.of(ReportTask.class)
                .set(ReportTask::getStatus, reportTask.getStatus(), Objects.nonNull(reportTask.getStatus()))
                .set(ReportTask::getErrorMsg, reportTask.getErrorMsg(), StringUtil.isNotBlank(reportTask.getErrorMsg()))
                .set(ReportTask::getExecTime, reportTask.getExecTime(), Objects.nonNull(reportTask.getExecTime()))
                .set(ReportTask::getCompleteTime, reportTask.getCompleteTime(), Objects.nonNull(reportTask.getCompleteTime()))
                .set(ReportTask::getTempFilePath, reportTask.getTempFilePath(), StringUtil.isNotBlank(reportTask.getTempFilePath()))
                .where(ReportTask::getId).eq(reportTask.getId())
                .update();
    }

    @Override
    public List<ReportTask> scanAsyncExportTask(List<Integer> statuses) {
        ReportTaskTableDef def = ReportTaskTableDef.REPORT_TASK;
        QueryWrapper query = QueryWrapper.create()
                .and(def.PID.eq(ReportConst.ZER0))
                .and(def.OP_TYPE.eq(OperationType.EXPORT.getCode()))
                .and(def.EXEC_TYPE.eq(ExecutionType.ASYNC.getCode()))
                .and(def.STATUS.in(statuses));
        return reportTaskMapper.selectListByQuery(query);
    }

    @Override
    public List<ReportTask> selectSlicedByStatus(Long pid, Integer status) {
        ReportTaskTableDef def = ReportTaskTableDef.REPORT_TASK;
        QueryWrapper query = QueryWrapper.create()
                .select(def.ID, def.TEMP_FILE_PATH)
                .and(def.PID.eq(pid))
                .and(def.STATUS.eq(status));
        return reportTaskMapper.selectListByQuery(query);
    }
}
