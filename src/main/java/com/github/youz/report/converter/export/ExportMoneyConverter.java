package com.github.youz.report.converter.export;


import com.github.youz.report.annotation.MoneyFormat;
import com.github.youz.report.constant.ReportConst;

import java.lang.reflect.Field;

/**
 * 导出金额转换器
 */
public class ExportMoneyConverter implements ExportConverter {

    @Override
    public boolean supports(Field field, Object target) {
        return field.isAnnotationPresent(MoneyFormat.class) && target instanceof Number;
    }

    @Override
    public Object convert2Excel(Field field, Object target) {
        MoneyFormat moneyFormat = field.getAnnotation(MoneyFormat.class);

        // 根据金额的正负判断是否需要添加负号
        double amount = ((Number) target).doubleValue();
        String symbol = amount >= 0 ? ReportConst.EMPTY : ReportConst.MINUS_SYMBOL;

        // 返回由符号、货币符号和格式化后的金额组成的字符串
        return symbol + moneyFormat.amountSymbol() + String.format(moneyFormat.value(), Math.abs(amount));
    }

}
