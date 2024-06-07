package com.github.youz.report.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 手机号校验
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
@ImportNull
public @interface ImportPhone {

    /**
     * 错误提示
     */
    String value();

    /**
     * 是否校验空值
     */
    @AliasFor(
            annotation = ImportNull.class, attribute = "checkNull"
    )
    boolean checkNull() default false;
}
