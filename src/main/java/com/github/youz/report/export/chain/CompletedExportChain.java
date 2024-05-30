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
 * 导出完成
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class CompletedExportChain extends AbstractExportChain {

    private final ReportTaskData reportTaskData;

    @Override
    void customHandler(ReportTask reportTask) {
        // 更新任务的状态为已完成 & 完成时间
        ReportTask update = UpdateEntity.of(ReportTask.class, reportTask.getId())
                .setStatus(ReportStatus.COMPLETED.getCode())
                .setCompleteTime(DateUtil.now());
        reportTaskData.updateById(update);
    }

}
