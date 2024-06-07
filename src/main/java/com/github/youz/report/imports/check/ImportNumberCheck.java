package com.github.youz.report.imports.check;

import com.github.youz.report.annotation.ImportNumber;
import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.enums.ExceptionCode;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 数字校验
 */
@Component
public class ImportNumberCheck extends AbstractImportCheck {

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(ImportNumber.class);
    }

    @Override
    public void customCheck(Field field, String value) {
        ImportNumber number = field.getAnnotation(ImportNumber.class);
        long numberValue = ReportConst.ZER0;
        try {
            numberValue = Long.parseLong(value);
        } catch (NumberFormatException var4) {
            // ignore
        }
        ExceptionCode.IMPORT_FAIL_CUSTOM_MSG.assertIsTrue(numberValue >= number.min() && numberValue <= number.max(), number.value());
    }
}
