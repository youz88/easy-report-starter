package com.github.youz.report.imports.check;

import com.github.youz.report.annotation.ImportPhone;
import com.github.youz.report.enums.ExceptionCode;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * 手机号校验
 */
@Component
public class ImportPhoneCheck extends AbstractImportCheck {

    /**
     * 手机号正则匹配
     */
    private static final String PHONE_NUMBER_REGEX = "^1([3-9])[0-9]{9}$";

    @Override
    public boolean support(Field field) {
        return field.isAnnotationPresent(ImportPhone.class);
    }

    @Override
    public void customCheck(Field field, String value) {
        ImportPhone phone = field.getAnnotation(ImportPhone.class);
        ExceptionCode.IMPORT_FAIL_CUSTOM_MSG.assertIsTrue(Pattern.matches(PHONE_NUMBER_REGEX, value), phone.value());
    }

}
