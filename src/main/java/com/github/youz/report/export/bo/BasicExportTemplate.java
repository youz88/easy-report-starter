package com.github.youz.report.export.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.github.youz.report.converter.ReportConverterLoader;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 基础导出模版
 */
@Log4j2
@Data
@Accessors(chain = true)
public class BasicExportTemplate {

    /**
     * 表头
     */
    private List<List<String>> headList;

    /**
     * 表体
     */
    private List<?> dataList;

    /**
     * 过滤字段属性
     */
    private List<Field> fieldList;

    /**
     * 组装固定表头并返回导出模型对象
     *
     * @param tplClass 导出模板类对象
     * @param context  导出上下文对象
     * @return 组装好的导出模型对象
     */
    public static BasicExportTemplate assemblyFixHead(Class<?> tplClass, ExportContext context) {
        // 过滤需要导出的字段
        return new BasicExportTemplate()
                .filterHead(tplClass, context.getFieldNames(), null);
    }

    /**
     * 组装动态表头并返回导出模型对象
     *
     * @param tplInstance 模板实例对象
     * @param context     导出上下文对象
     * @return 组装好的导出模型对象
     */
    public static BasicExportTemplate assemblyDynamicHead(Object tplInstance, ExportContext context) {
        // 记录动态表头模版
        context.setDynamicTemplate(tplInstance);

        // 过滤需要导出的字段 & 组装动态表头
        return new BasicExportTemplate()
                .filterHead(tplInstance.getClass(), context.getFieldNames(), tplInstance);
    }

    /**
     * 组装导出表体并返回导出模型对象
     *
     * @param tplDataList 模板数据列表
     * @param context     导出上下文对象
     * @return 组装好的导出模型对象
     */
    public static BasicExportTemplate assemblyBody(List<?> tplDataList, ExportContext context) {
        // 过滤需要导出的字段
        return new BasicExportTemplate()
                .filterBody(tplDataList, context.getFieldList());
    }

    /**
     * 过滤表头数据
     *
     * @param clazz          模板类类型
     * @param fieldNames     导出字段名称和顺序
     * @param exportTemplate 导出模版对象(含有动态列时需传入)
     * @return 当前对象
     */
    private BasicExportTemplate filterHead(Class<?> clazz, List<String> fieldNames, Object exportTemplate) {
        // 如果为空，则使用默认字段名称列表
        if (CollectionUtils.isEmpty(fieldNames)) {
            fieldNames = crateDefaultFieldNames(clazz);
        }

        // 初始化表头、字段属性
        headList = new ArrayList<>(fieldNames.size());
        fieldList = new ArrayList<>(fieldNames.size());

        // 获取导出字段映射
        Map<String, Field> fieldMap = getExportFields(clazz);

        // 组装表头
        fieldNames.stream()
                .filter(fieldMap::containsKey)
                .forEach(fieldName -> {
                    Field field = fieldMap.get(fieldName);
                    field.setAccessible(true);

                    // 追加表头数据
                    appendHead(headList, field, exportTemplate);

                    // 缓存字段属性
                    fieldList.add(field);
                });
        return this;
    }

    /**
     * 过滤表体数据
     *
     * @param tplDataList     模版数据列表
     * @param filterFieldList 过滤属性
     * @return 当前对象
     */
    private BasicExportTemplate filterBody(List<?> tplDataList, List<Field> filterFieldList) {
        if (CollectionUtils.isEmpty(tplDataList)) {
            return this;
        }

        // 初始化表头、表体
        List<List<Object>> filterData = new ArrayList<>(tplDataList.size());

        // 组装表体
        tplDataList.forEach(d -> {
            List<Object> rowData = new ArrayList<>(filterFieldList.size());
            for (Field field : filterFieldList) {
                appendData(rowData, field, d);
            }
            filterData.add(rowData);
        });

        dataList = filterData;
        return this;
    }

    /**
     * 追加表头数据
     *
     * @param filterHead 表头数组
     * @param field      属性
     * @param target     源数据
     */
    private void appendHead(List<List<String>> filterHead, Field field, Object target) {
        if (field.getType().isAssignableFrom(List.class)) {
            // 动态多表头
            Object obj = ReflectionUtils.getField(field, target);
            if (Objects.isNull(obj)) {
                return;
            }

            //noinspection unchecked
            List<DynamicColumn> items = (List<DynamicColumn>) obj;
            for (DynamicColumn item : items) {
                filterHead.add(new ArrayList<>(Arrays.asList(item.getHead())));
            }
        } else if (field.getType().isAssignableFrom(DynamicColumn.class)) {
            // 动态单表头
            Object obj = ReflectionUtils.getField(field, target);
            if (Objects.isNull(obj)) {
                return;
            }

            DynamicColumn item = (DynamicColumn) obj;
            filterHead.add(new ArrayList<>(Arrays.asList(item.getHead())));
        } else {
            // 普通对象
            filterHead.add(new ArrayList<>(Arrays.asList(field.getAnnotation(ExcelProperty.class).value())));
        }
    }

    /**
     * 追加表体数据
     *
     * @param filterData 属性值数组
     * @param field      属性
     * @param target     源数据
     */
    public void appendData(List<Object> filterData, Field field, Object target) {
        // 获取目标对象的属性值
        Object obj = ReflectionUtils.getField(field, target);

        // 调用转换器进行属性值转换
        Object formatData = ReportConverterLoader.exportConverter(field, obj);

        // 判断转换后的数据是否为列表类型
        if (formatData instanceof List) {
            filterData.addAll((List<?>) formatData);
        } else {
            filterData.add(formatData);
        }
    }

    /**
     * 获取该类所有导出属性字段
     *
     * @param clazz 类对象
     * @return 属性字段
     */
    private Map<String, Field> getExportFields(Class<?> clazz) {
        Map<String, Field> fieldMap = new HashMap<>(16);

        // 当当前类不是Object类的子类时，继续循环
        while (!(clazz.isAssignableFrom(Object.class))) {
            Field[] fields = clazz.getDeclaredFields();
            Arrays.stream(fields)
                    .filter(f -> f.isAnnotationPresent(ExcelProperty.class) && !fieldMap.containsKey(f.getName()))
                    .forEach(f -> fieldMap.put(f.getName(), f));

            // 获取当前类的父类
            clazz = clazz.getSuperclass();
        }
        return fieldMap;
    }

    /**
     * 创建默认导出字段
     *
     * @param clazz 导出类对象
     * @return 导出字段
     */
    private List<String> crateDefaultFieldNames(Class<?> clazz) {
        List<String> fieldNames = new ArrayList<>(20);

        // 当当前类不是Object类的子类时，继续循环
        while (!(clazz.isAssignableFrom(Object.class))) {
            // 获取当前类的所有字段
            Field[] fields = clazz.getDeclaredFields();
            Arrays.stream(fields)
                    .filter(f -> f.isAnnotationPresent(ExcelProperty.class))
                    .forEach(f -> {
                        String fieldName = f.getName();
                        if (fieldNames.contains(fieldName)) {
                            return;
                        }
                        fieldNames.add(fieldName);
                    });

            // 获取当前类的父类
            clazz = clazz.getSuperclass();
        }
        return fieldNames;
    }
}
