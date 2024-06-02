package com.github.youz.report.export.chain;

import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.data.ReportTaskData;
import com.github.youz.report.enums.ExceptionCode;
import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.export.bo.AsyncExportResult;
import com.github.youz.report.export.bo.ExportContext;
import com.github.youz.report.export.handler.CompositeExportHandler;
import com.github.youz.report.export.handler.ExportBusinessHandler;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.util.JsonUtil;
import com.github.youz.report.util.ZipUtil;
import com.mybatisflex.core.util.StringUtil;
import com.mybatisflex.core.util.UpdateEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        if (isSlicedParentTask(reportTask)) {
            // 分片父任务执行
            slicedParentTaskExecute(reportTask);
        } else {
            // 普通任务执行
            normalTaskExecute(reportTask);
        }
    }

    @Override
    public void failBack(ReportTask reportTask, Exception e) {
        // 更新任务的状态为执行失败 & 记录错误信息
        ReportTask update = UpdateEntity.of(ReportTask.class, reportTask.getId())
                .setStatus(ReportStatus.EXECUTION_FAILED.getCode())
                .setErrorMsg(e.getMessage());
        reportTaskData.updateById(update);
    }

    /**
     * 普通任务执行
     *
     * @param reportTask 报表任务对象
     */
    private void normalTaskExecute(ReportTask reportTask) {
        ExportBusinessHandler handler = compositeExportHandler.getHandler(reportTask.getBusinessType());

        // 定时任务执行导出
        ExportContext exportContext = JsonUtil.toObject(reportTask.getContext(), ExportContext.class);
        AsyncExportResult exportResult = handler.asyncExport(exportContext);
        String localFilePath = exportResult.getLocalFilePath();

        // 主任务需将本地文件压缩
        if (reportTask.getPid() == ReportConst.ZER0) {
            localFilePath = ZipUtil.zipFiles(Collections.singletonList(localFilePath), reportTask.getFileName());
        }

        // 更新任务的状态为生成本地文件成功 & 本地文件路径
        ReportTask update = UpdateEntity.of(ReportTask.class, reportTask.getId())
                .setLocalFilePath(localFilePath)
                .setStatus(ReportStatus.LOCAL_FILE_SUCCESS.getCode());
        reportTaskData.updateById(update);
    }

    /**
     * 执行分片父任务
     *
     * @param reportTask 报表任务对象
     */
    private void slicedParentTaskExecute(ReportTask reportTask) {
        // 查询分片任务列表
        List<ReportTask> slicedList = reportTaskData.selectSlicedByStatus(reportTask.getId(), ReportStatus.COMPLETED.getCode());

        // 分片任务已全部执行成功
        if (reportTask.getSlicedIndex() == slicedList.size()) {
            // 获取分片任务本地文件路径
            List<String> filePaths = slicedList.stream().map(ReportTask::getLocalFilePath).collect(Collectors.toList());

            // zip压缩
            String localFilePath = ZipUtil.zipFiles(filePaths, reportTask.getFileName());
            ExceptionCode.EXPORT_COMPRESSED_FAIL.assertTrue(StringUtil.isNotBlank(localFilePath));

            // 更新任务的状态为生成本地文件成功 & 本地文件路径
            ReportTask update = UpdateEntity.of(ReportTask.class, reportTask.getId())
                    .setLocalFilePath(localFilePath)
                    .setStatus(ReportStatus.LOCAL_FILE_SUCCESS.getCode());
            reportTaskData.updateById(update);
        } else {
            // 中断后续任务执行,需等待子任务执行成功后重新执行
            setNext(null);
        }
    }
}
