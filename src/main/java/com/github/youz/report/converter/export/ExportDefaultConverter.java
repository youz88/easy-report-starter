package com.github.youz.report.converter.export;


import com.github.youz.report.annotation.DefaultValueFormat;
import com.mybatisflex.core.util.StringUtil;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 默认值转换器
 */
public class ExportDefaultConverter implements ExportConverter {

    @Override
    public boolean supports(Field field, Object target) {
        return field.isAnnotationPresent(DefaultValueFormat.class)
                && (Objects.isNull(target) || StringUtil.isBlank(target.toString()));
    }

    @Override
    public Object convert2Excel(Field field, Object target) {
        return field.getAnnotation(DefaultValueFormat.class).value();
    }
}
