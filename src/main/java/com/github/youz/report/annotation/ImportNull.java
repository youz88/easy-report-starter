package com.github.youz.report.annotation;

import java.lang.annotation.*;

/**
 * 字段非空校验
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Documented
public @interface ImportNull {

    /**
     * 错误提示
     */
    String value() default "";

    /**
     * 校验字段是否为空
     */
    boolean checkNull() default true;

}
