package com.github.youz.report.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 操作类型
 */
@Getter
@RequiredArgsConstructor
public enum OperationType {

    IMPORTS(1, "导入"),

    EXPORT(2, "导出"),

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
