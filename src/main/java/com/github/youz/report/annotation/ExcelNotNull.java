package com.github.youz.report.annotation;

import java.lang.annotation.*;

/**
 * 字段非空校验
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface ExcelNotNull {

    /**
     * 错误提示
     */
    String value();
}
