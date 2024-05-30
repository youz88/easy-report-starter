package com.github.youz.report.annotation;

import java.lang.annotation.*;

/**
 * 格式化时间
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DateTimeFormat {

    /**
     * 根据模板格式化
     *
     * @return 格式化
     */
    String value() default "";

}
