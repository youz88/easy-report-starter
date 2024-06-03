package com.github.youz.report.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ConverterKeyBuild;
import com.alibaba.excel.converters.ConverterKeyBuild.ConverterKey;
import com.github.youz.report.converter.export.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表属性转换器加载器
 */
public class ReportConverterLoader {

    /**
     * 导入转换器
     */
    private static Map<ConverterKey, Converter<?>> importConverter;

    /**
     * 导出转换器
     */
    private static List<ExportConverter> exportConverter;

    static {
        initImportConverter();
        initExportConverter();
    }

    /**
     * 初始化导入转换器
     */
    private static void initImportConverter() {
        importConverter = new HashMap<>(16);
    }

    /**
     * 初始化导出转换器
     */
    private static void initExportConverter() {
        exportConverter = new ArrayList<>(16);

        // 添加转换器
        addExportConverter(new ExportDefaultConverter());
        addExportConverter(new ExportDateConverter());
        addExportConverter(new ExportMoneyConverter());
        addExportConverter(new ExportEnumConverter());
        addExportConverter(new ExportDynamicConverter());
    }

    /**
     * 添加导入转换器
     *
     * @param converter 导入转换器
     */
    private static void putImportConverter(Converter<?> converter) {
        importConverter.put(ConverterKeyBuild.buildKey(converter.getClass()), converter);
    }

    /**
     * 添加导出转换器
     *
     * @param converter 导出转换器
     */
    private static void addExportConverter(ExportConverter converter) {
        exportConverter.add(converter);
    }

    public static Map<ConverterKey, Converter<?>> loadImportConverter() {
        return importConverter;
    }

    /**
     * 导出数据转换
     *
     * @param field  目标属性
     * @param target 目标对象
     * @return 转换值
     */
    public static Object exportConverter(Field field, Object target) {
        for (ExportConverter converter : exportConverter) {
            // 如果转换器支持, 返回转换后的Excel格式的值
            if (converter.supports(field, target)) {
                return converter.convert2Excel(field, target);
            }
        }

        // 没有转换器支持，返回原值
        return target;
    }
}
