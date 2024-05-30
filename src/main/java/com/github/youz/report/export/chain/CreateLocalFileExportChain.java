package com.github.youz.report.export.chain;

import com.github.youz.report.data.ReportTaskData;
import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.export.handler.CompositeExportHandler;
import com.github.youz.report.export.handler.ExportBusinessHandler;
import com.github.youz.report.model.ReportTask;
import com.mybatisflex.core.util.UpdateEntity;
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

    private final ReportTaskData reportTaskData;

    private final CompositeExportHandler compositeExportHandler;

    @Override
    void customHandler(ReportTask reportTask) {
        ExportBusinessHandler handler = compositeExportHandler.getHandler(reportTask.getBusinessType());

        // 定时任务执行导出
        handler.asyncExport(reportTask);

        // 更新任务的状态为生成本地文件成功 & 临时文件路径
        ReportTask update = UpdateEntity.of(ReportTask.class, reportTask.getId())
                .setStatus(ReportStatus.LOCAL_FILE_SUCCESS.getCode());
        reportTaskData.updateById(update);
    }

    @Override
    public void failBack(ReportTask reportTask, Exception e) {
        // 更新任务的状态为执行失败 & 执行时间
        ReportTask update = UpdateEntity.of(ReportTask.class, reportTask.getId())
                .setStatus(ReportStatus.EXECUTION_FAILED.getCode())
                .setErrorMsg(e.getMessage());
        reportTaskData.updateById(update);
    }
}
