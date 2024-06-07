package com.github.youz.report.converter.imports;

import com.alibaba.excel.converters.ReadConverterContext;
import com.github.youz.report.constant.ReportConst;
import com.mybatisflex.core.util.StringUtil;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * 数字转换器
 */
public class ImportBigDecimalConverter extends AbstractConverter<BigDecimal> {

    /**
     * 数字正则匹配
     */
    private static final String NUMBER_REGEX = "^[0-9]+$";

    @Override
    public BigDecimal convertToJavaData(ReadConverterContext<?> context) {
        String dataStr = getDataStr(context);
        if (StringUtil.isBlank(dataStr)) {
            return null;
        }

        // (”,“)替换兼容处理
        dataStr = dataStr.replace(ReportConst.COMMA_SYMBOL, ReportConst.EMPTY);

        // 数字校验
        assertTrue(verify(dataStr), context);

        // 数字格式化
        return new BigDecimal(dataStr);
    }

    /**
     * 校验数字
     *
     * @param number 数字
     * @return 是否符合规则
     */
    public Boolean verify(String number) {
        // 去除小数点符号
        String replaceDecimal = number.replace(ReportConst.FULL_STOP_SYMBOL, ReportConst.EMPTY);
        return Pattern.matches(NUMBER_REGEX, replaceDecimal);
    }
}
