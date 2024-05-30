package com.github.youz.report.annotation;


import java.lang.annotation.*;

/**
 * 格式化枚举名称
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnumFormat {

    /**
     * 枚举值映射
     *
     * @return 映射枚举
     */
    Class<? extends Enum<?>> value();

    /**
     * key映射-匹配字段方法名称
     *
     * @return 方法名称
     */
    String keyMethodName() default "getCode";

    /**
     * value映射-显示字段方法名称
     *
     * @return 方法名称
     */
    String valueMethodName() default "getMessage";
}
