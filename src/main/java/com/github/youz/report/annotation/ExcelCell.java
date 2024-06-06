package com.github.youz.report.annotation;

import com.alibaba.excel.converters.AutoConverter;
import com.alibaba.excel.converters.Converter;
import com.github.youz.report.enums.ImportEnum;

import java.lang.annotation.*;

/**
 * Excel单元格
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelCell {

    /**
     * 表头名称
     *
     * @return 表头名称
     */
    String[] value() default {""};

    /**
     * 当前列下标
     *
     * @return 下标
     */
    int index() default -1;

    /**
     * 是否动态列
     *
     * @return 返回boolean类型，表示是否为动态列，默认为false
     */
    boolean dynamicColumn() default false;

    /**
     * 属性值转换器
     *
     * @return 转换器，用于将属性值从一种类型转换为另一种类型，默认为AutoConverter类
     */
    Class<? extends Converter<?>> converter() default AutoConverter.class;

    /**
     * 枚举值映射
     *
     * @return 返回 ImportEnum 的子类类型，表示枚举值的映射
     */
    Class<? extends ImportEnum> linkEnum() default ImportEnum.class;
}
