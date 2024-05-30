package com.github.youz.report.export.chain;

import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.repository.ReportTaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * 导出完成
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class CompletedExportChain extends AbstractExportChain {

    private final ReportTaskMapper reportTaskMapper;

    @Override
    void customHandler(ReportTask reportTask) {
        // 更新任务的状态为已完成 & 完成时间
        reportTask.setStatus(ReportStatus.COMPLETED.getCode())
                .setCompleteTime(Instant.now().getEpochSecond());
        reportTaskMapper.update(reportTask);
    }

}
