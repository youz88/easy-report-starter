package com.github.youz.report.converter.imports;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.github.youz.report.annotation.ExcelCell;
import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.enums.ExceptionCode;

import java.util.Objects;

/**
 * 基础转换类
 */
public abstract class AbstractConverter<T> implements Converter<T> {

    /**
     * 获取表格字符串值
     *
     * @param context 导入上下文
     * @return 字符串值
     */
    public String getDataStr(ReadConverterContext<?> context) {
        String dataStr = context.getReadCellData().getStringValue();
        return Objects.isNull(dataStr) ? ReportConst.EMPTY : dataStr.trim();
    }

    /**
     * 异常提示(true：抛出异常)
     *
     * @param expression 异常判断条件
     * @param context    导入上下文
     */
    public void assertTrue(Boolean expression, ReadConverterContext<?> context) {
        if (!expression) {
            String value = context.getContentProperty().getField().getAnnotation(ExcelCell.class).value()[0];
            ExceptionCode.IMPORT_FIELD_FORMAT_FAIL.throwException(value + "格式错误");
        }
    }

    /**
     * 异常提示(true：抛出异常)
     *
     * @param expression 异常判断条件
     * @param message    提示
     */
    public void assertTrue(Boolean expression, String message) {
        if (!expression) {
            ExceptionCode.IMPORT_FIELD_FORMAT_FAIL.throwException(message);
        }
    }
}
