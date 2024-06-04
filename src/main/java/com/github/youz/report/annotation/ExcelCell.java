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
     * 属性值转换器
     *
     * @return 转换器
     */
    Class<? extends Converter<?>> converter() default AutoConverter.class;

    /**
     * 枚举值映射
     *
     * @return 映射枚举
     */
    Class<? extends ImportEnum> linkEnum() default ImportEnum.class;
}
