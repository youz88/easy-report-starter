package com.github.youz.report.imports.check;

import com.github.youz.report.annotation.ExcelLength;
import com.github.youz.report.enums.ExceptionCode;
import com.mybatisflex.core.util.StringUtil;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 字段长度校验
 */
@Component
public class ImportLengthCheck implements ImportCheck {

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(ExcelLength.class);
    }

    @Override
    public void check(Field field, String value) {
        // 空值不校验
        if (StringUtil.isBlank(value)) {
            return;
        }

        // 长度校验
        ExcelLength length = field.getAnnotation(ExcelLength.class);
        ExceptionCode.IMPORT_FAIL_CUSTOM_MSG.assertIsTrue(value.length() >= length.min() && value.length() <= length.max(), length.value());
    }
}
