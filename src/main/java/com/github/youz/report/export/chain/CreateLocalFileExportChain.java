package com.github.youz.report.export.chain;

import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.export.handler.CompositeExportHandler;
import com.github.youz.report.export.handler.ExportBusinessHandler;
import com.github.youz.report.export.model.AsyncExportResult;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.repository.ReportTaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 创建本地文件
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class CreateLocalFileExportChain extends AbstractExportChain {

    private final ReportTaskMapper reportTaskMapper;

    private final CompositeExportHandler compositeExportHandler;

    @Override
    void customHandler(ReportTask reportTask) {
        ExportBusinessHandler handler = compositeExportHandler.getHandler(reportTask.getBusinessType());
        AsyncExportResult asyncExportResult = handler.asyncExport(reportTask);
    }

    @Override
    public void failBack(ReportTask reportTask, Exception e) {
        // 更新任务的状态为执行失败 & 执行时间
        reportTask.setStatus(ReportStatus.EXECUTION_FAILED.getCode())
                .setErrorMsg(e.getMessage());
        reportTaskMapper.update(reportTask);
    }
}
