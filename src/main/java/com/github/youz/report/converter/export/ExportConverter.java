package com.github.youz.report.converter.export;

import java.lang.reflect.Field;

/**
 * 导出对象转换
 */
public interface ExportConverter {

    /**
     * 是否支持该数据类型
     *
     * @param field  目标属性
     * @param target 目标对象
     * @return 是否
     */
    boolean supports(Field field, Object target);

    /**
     * 转换为excel显示字段
     *
     * @param field  目标属性
     * @param target 目标对象
     * @return 显示字段
     */
    Object convert2Excel(Field field, Object target);
}
