package com.github.youz.report.imports.check;

import com.github.youz.report.annotation.ExcelNumber;
import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.enums.ExceptionCode;
import com.mybatisflex.core.util.StringUtil;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 数字校验
 */
@Component
public class ImportNumberCheck implements ImportCheck {

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(ExcelNumber.class);
    }

    @Override
    public void check(Field field, String value) {
        // 空值不校验
        if (StringUtil.isBlank(value)) {
            return;
        }

        // 数字校验
        ExcelNumber number = field.getAnnotation(ExcelNumber.class);
        long numberValue = ReportConst.ZER0;
        try {
            numberValue = Long.parseLong(value);
        } catch (NumberFormatException var4) {
            // ignore
        }
        ExceptionCode.IMPORT_FAIL_CUSTOM_MSG.assertIsTrue(numberValue >= number.min() && numberValue <= number.max(), number.value());
    }
}
