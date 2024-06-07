package com.github.youz.report.imports.check;

import com.github.youz.report.annotation.ImportLength;
import com.github.youz.report.enums.ExceptionCode;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 字段长度校验
 */
@Component
public class ImportLengthCheck extends AbstractImportCheck {

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(ImportLength.class);
    }

    @Override
    public void customCheck(Field field, String value) {
        ImportLength length = field.getAnnotation(ImportLength.class);
        ExceptionCode.IMPORT_FAIL_CUSTOM_MSG.assertIsTrue(value.length() >= length.min() && value.length() <= length.max(), length.value());
    }
}
