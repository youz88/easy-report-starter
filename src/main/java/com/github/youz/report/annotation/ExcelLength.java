package com.github.youz.report.annotation;

import java.lang.annotation.*;

/**
 * 字段长度校验
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface ExcelLength {

    /**
     * 最小长度
     */
    int min() default 0;

    /**
     * 最大长度
     */
    int max() default Integer.MAX_VALUE;

    /**
     * 错误提示
     */
    String value() default "";
}
