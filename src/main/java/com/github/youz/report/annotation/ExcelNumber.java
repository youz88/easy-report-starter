package com.github.youz.report.annotation;

import java.lang.annotation.*;

/**
 * 数字校验
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface ExcelNumber {

    /**
     * 最小数字
     */
    long min() default 0;

    /**
     * 最大数字
     */
    long max() default Long.MAX_VALUE;

    /**
     * 错误提示
     */
    String value() default "";
}
