package com.github.youz.report.imports.check;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

@Component
public class CompositeImportCheckHandler {

    private final List<ImportCheck> checkList;

    @Autowired
    public CompositeImportCheckHandler(List<ImportCheck> checkList) {
        this.checkList = checkList;
    }

    /**
     * 检查导入值是否合法
     *
     * @param field 字段对象
     * @param value 字段值
     */
    public void check(Field field, String value) {
        for (ImportCheck importCheck : checkList) {
            if (importCheck.support(field)) {
                importCheck.check(field, value);
            }
        }
    }

}

