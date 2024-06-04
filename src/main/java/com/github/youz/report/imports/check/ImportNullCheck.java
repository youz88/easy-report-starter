package com.github.youz.report.imports.check;

import com.github.youz.report.annotation.ExcelNotNull;
import com.github.youz.report.enums.ExceptionCode;
import com.mybatisflex.core.util.StringUtil;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 非空校验
 */
@Component
public class ImportNullCheck implements ImportCheck {

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(ExcelNotNull.class);
    }

    @Override
    public void check(Field field, String value) {
        ExcelNotNull notNull = field.getAnnotation(ExcelNotNull.class);
        ExceptionCode.IMPORT_FAIL_CUSTOM_MSG.assertIsTrue(StringUtil.isNotBlank(value), notNull.value());
    }
}
