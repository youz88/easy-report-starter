package com.github.youz.report.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 导入任务步骤
 */
@Getter
@AllArgsConstructor
public enum ImportStep {

    CHECK(1, "参数校验"),

    IMPORTS(2, "导入数据"),

    ;

    private final Integer code;

    private final String message;
}
