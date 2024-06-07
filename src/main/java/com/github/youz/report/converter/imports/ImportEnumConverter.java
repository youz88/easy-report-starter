package com.github.youz.report.converter.imports;

import com.alibaba.excel.converters.ReadConverterContext;
import com.github.youz.report.annotation.ImportCell;
import com.github.youz.report.enums.ImportEnum;
import com.mybatisflex.core.util.StringUtil;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * 枚举值转换器
 */
public class ImportEnumConverter extends AbstractConverter<Integer> {

    @Override
    public Integer convertToJavaData(ReadConverterContext<?> context) {
        String dataStr = getDataStr(context);
        if (StringUtil.isBlank(dataStr)) {
            return null;
        }

        // 获取枚举类
        Field field = context.getContentProperty().getField();
        ImportCell importCell = field.getAnnotation(ImportCell.class);
        Class<? extends ImportEnum> linkEnum = importCell.linkEnum();

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
