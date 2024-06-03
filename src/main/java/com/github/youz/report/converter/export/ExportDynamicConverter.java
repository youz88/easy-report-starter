package com.github.youz.report.converter.export;

import com.github.youz.report.export.bo.DynamicColumn;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 动态数据
 */
public class ExportDynamicConverter implements ExportConverter {

    @Override
    public boolean supports(Field field, Object target) {
        return field.getType().isAssignableFrom(List.class) || target instanceof DynamicColumn;
    }

    @Override
    public Object convert2Excel(Field field, Object target) {
        if (target instanceof DynamicColumn) {
            DynamicColumn dynamic = (DynamicColumn) target;
            return dynamic.getData();
        } else {
            List<DynamicColumn> dynamicList = (List<DynamicColumn>) target;
            return dynamicList.stream().map(DynamicColumn::getData).collect(Collectors.toList());
        }
    }
}
