package com.github.youz.report.annotation;

import java.lang.annotation.*;

/**
 * 数字格式化注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MoneyFormat {

    /**
     * 根据模板格式化(例如value:"%.2f", 2.123456789 格式化后为 "2.12")
     *
     * @return 格式化
     */
    String value() default "";

    /**
     * 金额符号前缀
     *
     * @return 符号
     */
    String amountSymbol() default "¥";

}
