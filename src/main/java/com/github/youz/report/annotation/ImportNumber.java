package com.github.youz.report.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 数字校验
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
@ImportNull
public @interface ImportNumber {

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

    /**
     * 是否校验空值
     */
    @AliasFor(
            annotation = ImportNull.class, attribute = "checkNull"
    )
    boolean checkNull() default false;
}
