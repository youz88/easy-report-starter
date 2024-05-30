package com.github.youz.report.converter.export;


import com.github.youz.report.annotation.DateTimeFormat;
import com.github.youz.report.util.DateUtil;

import java.lang.reflect.Field;

/**
 * 导出日期转换器
 */
public class ExportDateConverter implements ExportConverter {

    @Override
    public boolean supports(Field field, Object target) {
        return field.isAnnotationPresent(DateTimeFormat.class) && target instanceof Number;
    }

    @Override
    public Object convert2Excel(Field field, Object target) {
        return DateUtil.format(((Number)target).longValue(), field.getAnnotation(DateTimeFormat.class).value());
    }

}
