package com.github.youz.report.converter.export;

import com.github.youz.report.export.bo.ExportDynamicColumn;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 动态数据
 */
public class ExportDynamicConverter implements ExportConverter {

    @Override
    public boolean supports(Field field, Object target) {
        return field.getType().isAssignableFrom(List.class) || target instanceof ExportDynamicColumn;
    }

    @Override
    public Object convert2Excel(Field field, Object target) {
        if (target instanceof ExportDynamicColumn) {
            ExportDynamicColumn dynamic = (ExportDynamicColumn) target;
            return dynamic.getData();
        } else {
            List<ExportDynamicColumn> dynamicList = (List<ExportDynamicColumn>) target;
            return dynamicList.stream().map(ExportDynamicColumn::getData).collect(Collectors.toList());
        }
    }
}
