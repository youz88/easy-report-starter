package com.github.youz.report.imports.check;

import java.lang.reflect.Field;

/**
 * 导入检查
 */
public interface ImportCheck {

    /**
     * 判断是否支持给定的字段
     *
     * @param field 待判断的字段
     * @return 如果支持则返回true，否则返回false
     */
    boolean support(Field field);

    /**
     * 检查Field字段的值是否符合要求
     *
     * @param field 需要检查的Field字段
     * @param value 需要检查的字段值
     */
    void check(Field field, String value);
}
