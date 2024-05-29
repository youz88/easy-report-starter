package com.github.youz.report.enums;

import com.github.youz.report.export.chain.*;
import lombok.Getter;

import java.util.Arrays;

/**
 * 报表导出步骤
 */
@Getter
public enum ReportExportStep {

    WAIT(ReportStatus.WAIT.getCode(), "待执行",
            ExecExportChain.class, CreateLocalFileExportChain.class, UploadCloudFileExportChain.class, CompletedExportChain.class),

    EXECUTION(ReportStatus.EXECUTION.getCode(), "执行中"),

    LOCAL_FILE_SUCCESS(ReportStatus.LOCAL_FILE_SUCCESS.getCode(), "生成本地文件成功"),

    COMPLETED(ReportStatus.COMPLETED.getCode(), "已完成"),

    EXECUTION_FAILED(ReportStatus.EXECUTION_FAILED.getCode(), "执行失败",
            ExecExportChain.class, CreateLocalFileExportChain.class, UploadCloudFileExportChain.class, CompletedExportChain.class),

    FAILED_UPLOAD_FILE(ReportStatus.FAILED_UPLOAD_FILE.getCode(), "上传文件失败",
            UploadCloudFileExportChain.class, CompletedExportChain.class),
    ;

    /**
     * 当前状态编码
     */
    private final int code;

    /**
     * 描述
     */
    private final String message;

    /**
     * 描述
     */
    private final Class<? extends ExportChain>[] chainClasses;

    @SafeVarargs
    ReportExportStep(int code, String message, Class<? extends ExportChain>... chainClasses) {
        this.code = code;
        this.message = message;
        this.chainClasses = chainClasses;
    }

    /**
     * 根据给定的编码获取对应的报表导出步骤
     *
     * @param code 报表导出步骤的编码
     * @return 报表导出步骤的枚举值
     */
    public static ReportExportStep of(int code) {
        ReportExportStep reportExportStep = Arrays.stream(values())
                .filter(step -> step.code == code).findFirst()
                .orElse(null);
        if (reportExportStep == null) {
            ExceptionCode.EXPORT_NO_MATCH_CHAIN.throwException();
        }
        return reportExportStep;
    }
}
