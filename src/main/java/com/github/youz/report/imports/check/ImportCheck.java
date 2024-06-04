package com.github.youz.report.imports.check;

import java.lang.reflect.Field;

/**
 * 导入检查
 */
public interface ImportCheck {

    /**
     * 是否支持
     */
    boolean support(Field field);

    /**
     * 检查
     */
    void check(Field field, String value);
}
