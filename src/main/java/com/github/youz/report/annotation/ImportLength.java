package com.github.youz.report.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 字段长度校验
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
@ImportNull
public @interface ImportLength {

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

    /**
     * 是否校验空值
     */
    @AliasFor(
            annotation = ImportNull.class, attribute = "checkNull"
    )
    boolean checkNull() default false;
}
