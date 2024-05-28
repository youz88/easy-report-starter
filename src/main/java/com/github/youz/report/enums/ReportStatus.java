package com.github.youz.report.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 报表状态
 */
@Getter
@RequiredArgsConstructor
public enum ReportStatus {

    WAIT(0, "待执行"),

    EXECUTION(5, "执行中"),

    LOCAL_FILE_SUCCESS(10, "生成本地文件成功"),

    COMPLETED(15, "已完成"),

    EXECUTION_FAILED(50, "执行失败"),

    FAILED_UPLOAD_FILE(55, "上传文件失败"),
    ;

    /**
     * 业务编码
     */
    private final int code;

    /**
     * 描述
     */
    private final String message;
}
