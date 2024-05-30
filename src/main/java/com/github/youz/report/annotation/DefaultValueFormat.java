package com.github.youz.report.annotation;

import java.lang.annotation.*;

/**
 * 默认值注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface DefaultValueFormat {

    /**
     * 默认值
     *
     * @return 默认值
     */
    String value() default "";
}
