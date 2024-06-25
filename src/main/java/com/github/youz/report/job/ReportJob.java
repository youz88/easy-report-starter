package com.github.youz.report.job;

import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.data.ReportTaskData;
import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.service.ReportService;
import com.github.youz.report.util.ExcelExportUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReportJob {

    private final ReportTaskData reportTaskData;

    private final ReportService reportService;

    /**
     * 扫描待执行导出任务
     */
    @Scheduled(cron = "#{@reportProperties.export.scanWaitExecCron}")
    public void scanWaitExecExportTask() {
        // 扫描待执行导出任务
        List<Integer> statuses = Arrays.asList(ReportStatus.WAIT.getCode(),
                ReportStatus.EXECUTION_FAILED.getCode(), ReportStatus.UNDONE.getCode());
        List<ReportTask> reportTaskList = reportTaskData.scanAsyncExportTask(statuses);
        if (reportTaskList.isEmpty()) {
            return;
        }

        // 执行任务
        reportTaskList.forEach(reportTask -> {
            // 判断当前任务是否为分片任务, 分片任务由于数据量较大，使用异步线程池执行
            if (reportTask.getPid() > ReportConst.ZER0) {
                // 如果是分片任务，则使用异步线程池执行
                reportService.asyncExport(reportTask);
            } else {
                // 同步执行
                ExcelExportUtil.jobExport(reportTask);
            }
        });
    }

    /**
     * 扫描待上传导出任务
     */
    @Scheduled(cron = "#{@reportProperties.export.scanWaitUploadCron}")
    public void scanWaitUploadExportTask() {
        // 扫描待上传导出任务
        List<Integer> statuses = Collections.singletonList(ReportStatus.FAILED_UPLOAD_FILE.getCode());
        List<ReportTask> reportTaskList = reportTaskData.scanAsyncExportTask(statuses);
        if (reportTaskList.isEmpty()) {
            return;
        }

        // 执行任务
        reportTaskList.forEach(ExcelExportUtil::jobExport);
    }
}
