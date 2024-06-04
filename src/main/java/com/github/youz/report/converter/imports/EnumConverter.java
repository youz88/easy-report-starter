package com.github.youz.report.converter.imports;

import com.alibaba.excel.converters.ReadConverterContext;
import com.github.youz.report.annotation.ExcelCell;
import com.github.youz.report.enums.ImportEnum;
import com.mybatisflex.core.util.StringUtil;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * 枚举值转换器
 */
public class EnumConverter extends AbstractConverter<Integer> {

    @Override
    public Integer convertToJavaData(ReadConverterContext<?> context) {
        String dataStr = getDataStr(context);
        if (StringUtil.isBlank(dataStr)) {
            return null;
        }

        // 获取枚举类
        Field field = context.getContentProperty().getField();
        ExcelCell excelCell = field.getAnnotation(ExcelCell.class);
        Class<? extends ImportEnum> linkEnum = excelCell.linkEnum();

        // 获取枚举值
        Integer code = Arrays.stream(linkEnum.getEnumConstants())
                .filter(baseEnum -> baseEnum.getMessage().equals(dataStr))
                .map(ImportEnum::getCode)
                .findAny()
                .orElse(null);

        // 校验
        assertTrue(code != null, context);
        return code;
    }


}
