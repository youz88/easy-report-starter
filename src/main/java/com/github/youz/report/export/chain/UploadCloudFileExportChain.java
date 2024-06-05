package com.github.youz.report.export.chain;

import com.github.youz.report.config.ExportProperties;
import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.data.ReportTaskData;
import com.github.youz.report.data.UploadCloudData;
import com.github.youz.report.enums.ExceptionCode;
import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.util.ApplicationContextUtil;
import com.mybatisflex.core.util.StringUtil;
import com.mybatisflex.core.util.UpdateEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 上传云文件
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class UploadCloudFileExportChain extends AbstractExportChain {

    private final ReportTaskData reportTaskData;

    private final ExportProperties exportProperties;

    @Override
    void customHandler(ReportTask reportTask) {
        // 配置无需上传云存储 | 当前任务为切片任务
        if (!exportProperties.isUploadCloud() || reportTask.getPid() > ReportConst.ZER0) {
            return;
        }

        // 上传导出文件到云空间, 返回云存储文件路径
        String cloudFilePath = ApplicationContextUtil.getBean(UploadCloudData.class).uploadFile(reportTask.getLocalFilePath());
        ExceptionCode.COMMON_UPLOAD_FAIL.assertIsTrue(StringUtil.isNotBlank(cloudFilePath));

        // 更新任务的上传文件路径
        ReportTask update = UpdateEntity.of(ReportTask.class, reportTask.getId())
                .setUploadFilePath(cloudFilePath);
        reportTaskData.updateById(update);
    }

    @Override
    protected void failBack(ReportTask reportTask, Exception e) {
        // 更新任务的状态为上传失败 & 记录错误信息
        ReportTask update = UpdateEntity.of(ReportTask.class, reportTask.getId())
                .setStatus(ReportStatus.FAILED_UPLOAD_FILE.getCode())
                .setErrorMsg(e.getMessage());
        reportTaskData.updateById(update);
    }
}
