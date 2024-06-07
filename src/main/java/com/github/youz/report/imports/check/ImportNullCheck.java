package com.github.youz.report.imports.check;

import com.github.youz.report.annotation.ImportNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 非空校验
 */
@Component
public class ImportNullCheck extends AbstractImportCheck {

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(ImportNull.class);
    }

    @Override
    public void customCheck(Field field, String value) {

    }
}
