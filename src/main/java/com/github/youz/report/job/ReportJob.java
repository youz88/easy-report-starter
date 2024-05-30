package com.github.youz.report.job;

import com.github.youz.report.data.ReportTaskData;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.util.ExcelExportUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReportJob {

    private final ReportTaskData reportTaskData;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void exportTask() {
        // 扫描待执行导出任务
        List<ReportTask> reportTaskList = reportTaskData.scanExportTask();
        if (reportTaskList.isEmpty()) {
            return;
        }

        // 执行任务
        reportTaskList.forEach(ExcelExportUtil::jobExport);
    }
}
