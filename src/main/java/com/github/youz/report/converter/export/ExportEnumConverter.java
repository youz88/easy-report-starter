package com.github.youz.report.converter.export;

import com.github.youz.report.annotation.EnumFormat;
import com.github.youz.report.constant.ReportConst;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * 枚举
 */
public class ExportEnumConverter implements ExportConverter {

    @Override
    public boolean supports(Field field, Object target) {
        return field.isAnnotationPresent(EnumFormat.class);
    }

    @Override
    public Object convert2Excel(Field field, Object target) {
        // 获取注解中指定的枚举类型
        EnumFormat enumFormat = field.getAnnotation(EnumFormat.class);
        Class<? extends Enum<?>> baseEnum = enumFormat.value();

        // 获取枚举类型的所有枚举常量
        Enum<?>[] enumConstants = baseEnum.getEnumConstants();
        return Arrays.stream(enumConstants)
                .filter(item -> {
                    // 根据注解中的keyMethodName查找方法
                    Method method = ReflectionUtils.findMethod(baseEnum, enumFormat.keyMethodName());
                    if (Objects.isNull(method)) {
                        return false;
                    }

                    // 调用方法获取key值
                    Object key = ReflectionUtils.invokeMethod(method, item);
                    return Objects.nonNull(key) && key.toString().equalsIgnoreCase(target.toString());
                }).map(item -> {
                    // 根据注解中的valueMethodName查找方法
                    Method method = ReflectionUtils.findMethod(baseEnum, enumFormat.valueMethodName());
                    if (Objects.isNull(method)) {
                        return ReportConst.MINUS_SYMBOL;
                    }

                    // 调用方法获取value值
                    Object value = ReflectionUtils.invokeMethod(method, item);
                    return Objects.isNull(value) ? ReportConst.MINUS_SYMBOL : value.toString();
                }).findAny()
                .orElse(ReportConst.MINUS_SYMBOL);
    }
}
