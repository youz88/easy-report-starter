package com.github.youz.report.export.chain;

import com.github.youz.report.data.ReportTaskData;
import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.util.DateUtil;
import com.mybatisflex.core.util.UpdateEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 执行导出
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class ExecExportChain extends AbstractExportChain {

    private final ReportTaskData reportTaskData;

    @Override
    void customHandler(ReportTask reportTask) {
        // 更新任务的状态为执行中 & 执行时间
        ReportTask update = UpdateEntity.of(ReportTask.class, reportTask.getId())
                .setStatus(ReportStatus.EXECUTION.getCode())
                .setExecTime(DateUtil.now());
        reportTaskData.updateById(update);
    }
}
