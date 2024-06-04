package com.github.youz.report.converter.imports;

import com.alibaba.excel.converters.ReadConverterContext;

import java.util.regex.Pattern;

/**
 * 手机号
 */
public class PhoneNumberConverter extends AbstractConverter<String> {

    /**
     * 手机号正则匹配
     */
    private static final String PHONE_NUMBER_REGEX = "^1([3-9])[0-9]{9}$";

    @Override
    public String convertToJavaData(ReadConverterContext<?> context) {
        String dataStr = getDataStr(context);

        // 参数校验
        assertTrue(verify(dataStr), context);
        return dataStr;
    }

    /**
     * 校验手机号
     *
     * @param phoneNumber 手机号
     * @return 是否符合规则
     */
    public Boolean verify(String phoneNumber) {
        return Pattern.matches(PHONE_NUMBER_REGEX, phoneNumber);
    }
}
