package com.github.youz.report.job;

import com.github.youz.report.enums.ExecutionType;
import com.github.youz.report.enums.OperationType;
import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.model.table.ReportTaskTableDef;
import com.github.youz.report.repository.ReportTaskMapper;
import com.github.youz.report.util.ExcelExportUtil;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReportJob {

    private final ReportTaskMapper reportTaskMapper;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void exportTask() {
        // 查询待执行的任务
        ReportTaskTableDef def = ReportTaskTableDef.REPORT_TASK;
        QueryWrapper query = QueryWrapper.create()
                .and(def.PID.eq(0))
                .and(def.OP_TYPE.eq(OperationType.EXPORT.getCode()))
                .and(def.EXEC_TYPE.eq(ExecutionType.ASYNC.getCode()))
                .and(def.STATUS.in(ReportStatus.WAIT.getCode(), ReportStatus.EXECUTION_FAILED.getCode()));
        List<ReportTask> reportTaskList = reportTaskMapper.selectListByQuery(query);
        if (reportTaskList.isEmpty()) {
            return;
        }

        // 执行任务
        reportTaskList.forEach(ExcelExportUtil::jobExport);
    }
}
