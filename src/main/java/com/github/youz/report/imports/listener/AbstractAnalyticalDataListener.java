package com.github.youz.report.imports.listener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.converters.AutoConverter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ConverterKeyBuild;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.github.youz.report.annotation.ExcelCell;
import com.github.youz.report.config.ImportProperties;
import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.converter.ReportConverterLoader;
import com.github.youz.report.enums.ExceptionCode;
import com.github.youz.report.enums.ImportStep;
import com.github.youz.report.imports.bo.BasicImportTemplate;
import com.github.youz.report.imports.bo.ImportInvokeResult;
import com.github.youz.report.imports.check.CompositeImportCheckHandler;
import com.github.youz.report.util.ApplicationContextUtil;
import com.github.youz.report.util.StreamUtil;
import com.mybatisflex.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 导入数据解析监听器
 */
@Getter
@Setter
@Log4j2
public abstract class AbstractAnalyticalDataListener<T extends BasicImportTemplate> extends AnalysisEventListener<Map<Integer, String>> {

    /**
     * 表头起始行(起始值为 1)
     */
    private int headRowIndex;

    /**
     * 表体起始行
     */
    private int bodyRowIndex;

    /**
     * 导入总计条数
     */
    private int total;

    /**
     * 导入数据列表
     */
    private List<T> data;

    /**
     * 源数据（未经过转换器处理） map<数据行, List<源数据属性值列表>>(用于回写失败文件)
     */
    private LinkedHashMap<Integer, Collection<String>> sourceData;

    /**
     * 导入类目标对象
     */
    private Class<T> targetClass;

    /**
     * 表头下标名称属性映射
     */
    private Map<Integer, Field> targetFieldMap;

    /**
     * 父级表头
     */
    private Map<Integer, List<String>> headMap;

    /**
     * 临时文件路径
     */
    private String tempFilePath;

    /**
     * 导入执行方法
     */
    private List<ImportStep> invokeMethods;

    /**
     * 校验失败数据行
     */
    private List<Integer> checkFailRows;

    /**
     * 构造方法
     *
     * @param targetClass 目标类
     */
    public AbstractAnalyticalDataListener(Class<T> targetClass) {
        this.targetClass = targetClass;
        this.data = new ArrayList<>();
        this.sourceData = new LinkedHashMap<>();
        this.checkFailRows = new LinkedList<>();

        // 初始化目标类属性
        this.targetFieldMap = assemblyTargetFieldMap(targetClass);
    }

    /**
     * 获取导入失败模板地址
     *
     * @return 导入失败模板地址
     */
    protected abstract String getFailTemplatePath();

    /**
     * 调用业务方参数校验方法
     *
     * @param data 源数据
     * @return 校验结果
     */
    protected abstract List<ImportInvokeResult.DataStatus> invokeCheckMethod(List<T> data);

    /**
     * 调用业务方导入方法
     *
     * @param data 源数据
     */
    protected abstract void invokeImportMethod(List<T> data);

    /**
     * 追加错误行提示
     *
     * @param rowIndex 行号
     * @param source   源数据
     * @param message  错误信息
     */
    protected abstract void appendFailRow(Integer rowIndex, Collection<String> source, String message);

    /**
     * 该方法仅执行一次,如果表头为多行,则需通过方法{@link AbstractAnalyticalDataListener#invoke}特殊处理
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        // 判断是否为报表起始行
        Integer rowIndex = context.readRowHolder().getRowIndex();
        if (rowIndex != headRowIndex - 1) {
            return;
        }

        // 判断报表行数是否超出限制
        Integer limitMaxRow = getLimitMaxRow();
        Integer approximateTotalRowNumber = context.readSheetHolder().getApproximateTotalRowNumber();
        ExceptionCode.IMPORT_LIMIT_ROW_FAIL.assertIsTrue(approximateTotalRowNumber <= limitMaxRow, limitMaxRow);

        // 初始化表头
        this.headMap = StreamUtil.toMap(headMap.entrySet(), Map.Entry::getKey, entry -> Collections.singletonList(entry.getValue()));

        // 表头校验
        checkHead(headMap);
    }

    @Override
    public void invoke(Map<Integer, String> rowMap, AnalysisContext context) {
        if (filterRowData(rowMap, context)) {
            return;
        }

        // 数据解析前置处理
        analysisBeforeProcess();

        // 数据解析处理
        T target = analysisProcess(rowMap, context);

        // 数据解析后置处理
        analysisAfterProcess(target, rowMap, context);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 处理最后一批数据
        if (!data.isEmpty()) {
            // 导入数据
            invokeImportData();
        }
    }

    /**
     * 校验数据
     */
    protected void invokeImportData() {
        if (invokeMethods.contains(ImportStep.CHECK)) {
            // 远程调用校验方法
            List<ImportInvokeResult.DataStatus> dataStatusList = invokeCheckMethod(data);

            // 导入模板添加信息
            writeTemplate(dataStatusList, sourceData);
        }
        if (invokeMethods.contains(ImportStep.IMPORTS)) {
            // 过滤校验失败数据
            data = data.stream()
                    .filter(d -> !checkFailRows.contains(d.getIndex()))
                    .collect(Collectors.toList());

            // 远程调用导入方法
            invokeImportMethod(data);
        }

        // 处理完成清理
        data.clear();
        sourceData.clear();
    }

    /**
     * 获取最大行数限制
     *
     * @return 最大行数限制
     */
    protected Integer getLimitMaxRow() {
        return ApplicationContextUtil.getBean(ImportProperties.class).getLimitMaxRow();
    }


    /**
     * 获取批量处理的行数
     *
     * @return 批量处理的行数
     */
    protected int getBatchRow() {
        return ApplicationContextUtil.getBean(ImportProperties.class).getBatchRow();
    }

    /**
     * 文件读取
     */
    public void read() {
        try (ExcelReader excelReader = EasyExcel.read(tempFilePath, this).build()) {
            // 构建一个sheet 这里可以指定名字或者no
            ReadSheet readSheet = EasyExcel.readSheet(0)
                    .headRowNumber(getHeadRowIndex())
                    .build();
            // 读取一个sheet
            excelReader.read(readSheet);
        }
    }

    /**
     * 是否过滤该数据行
     *
     * @param rowMap  源数据
     * @param context 解析任务上下文
     * @return 是否过滤
     */
    private Boolean filterRowData(Map<Integer, String> rowMap, AnalysisContext context) {
        Integer rowIndex = context.readRowHolder().getRowIndex();

        // 判断当前行是否为表体起始行
        if (rowIndex >= bodyRowIndex - 1) {
            return Boolean.FALSE;
        }

        // 追加表头行名称
        appendHead(rowMap);
        return Boolean.TRUE;
    }

    /**
     * 数据解析前置处理
     */
    private void analysisBeforeProcess() {
        // 总计 +1
        if (invokeMethods.contains(ImportStep.CHECK)) {
            total++;
        }
    }

    /**
     * 数据解析处理
     *
     * @param rowMap  源数据
     * @param context 解析任务上下文
     * @return 目标数据
     */
    private T analysisProcess(Map<Integer, String> rowMap, AnalysisContext context) {
        // 目标对象
        T target = null;

        for (Map.Entry<Integer, String> entry : rowMap.entrySet()) {
            Integer columnIndex = entry.getKey();
            String value = entry.getValue();

            // 组装属性值
            try {
                target = assemblyFieldValue(columnIndex, target, value, context);
            } catch (Exception e) {
                log.error("导入excel属性解析失败：", e);

                // 追加导入失败模板信息
                appendFailRow(context.readRowHolder().getRowIndex(), rowMap.values(), e.getMessage());
                return null;
            }
        }
        return target;
    }


    /**
     * 数据解析后置处理
     *
     * @param target  目标数据
     * @param rowMap  源数据
     * @param context 解析任务上下文
     */
    private void analysisAfterProcess(T target, Map<Integer, String> rowMap, AnalysisContext context) {
        if (Objects.isNull(target)) {
            return;
        }

        // 记录数据行
        target.setIndex(context.readRowHolder().getRowIndex());

        // 记录解析数据
        data.add(target);

        // 记录源数据
        sourceData.put(target.getIndex(), rowMap.values());

        // 达到BATCH_COUNT了，进行批量处理
        if (data.size() >= getBatchRow()) {
            // 导入数据
            invokeImportData();
        }
    }

    /**
     * 追加错误行提示
     *
     * @param dataStatusList 导入数据返回结果集
     * @param sourceData     导入源数据
     */
    private void writeTemplate(List<ImportInvokeResult.DataStatus> dataStatusList, Map<Integer, Collection<String>> sourceData) {
        Map<Integer, ImportInvokeResult.DataStatus> indexStatusMap = CollectionUtils.isEmpty(dataStatusList)
                ? Collections.emptyMap()
                : StreamUtil.toMap(dataStatusList, ImportInvokeResult.DataStatus::getIndex, Function.identity());

        for (Map.Entry<Integer, Collection<String>> entry : sourceData.entrySet()) {
            Integer rowIndex = entry.getKey();
            Collection<String> rowData = entry.getValue();
            ImportInvokeResult.DataStatus dataStatus = indexStatusMap.get(rowIndex);

            // 校验成功
            if (Objects.isNull(dataStatus) || dataStatus.getSuccess()) {
                continue;
            }

            // 追加错误行
            appendFailRow(rowIndex, rowData, dataStatus.getReason());
        }
    }

    /**
     * 追加表头数据
     *
     * @param rowMap 当前行数据
     */
    private void appendHead(Map<Integer, String> rowMap) {
        rowMap.forEach((k, v) -> {
            List<String> headNames = headMap.get(k);
            // 去除重复表头
            if (!headNames.contains(v)) {
                headNames.add(v);
            }
        });
    }

    /**
     * 组装表体数据
     *
     * @param columnIndex 当前列下标
     * @param target      目标对象
     * @param value       数据值
     * @param context     上下文对象
     * @return 目标对象
     */
    private T assemblyFieldValue(Integer columnIndex, T target, String value, AnalysisContext context) throws Exception {
        // 超出表头列,过滤该值
        if (columnIndex > targetFieldMap.size() - 1) {
            return target;
        }

        // 获取该列属性
        Field field = targetFieldMap.get(columnIndex);
        if (Objects.isNull(field)) {
            return target;
        }

        // 是否已初始化
        if (Objects.isNull(target)) {
            target = targetClass.newInstance();
        }

        // 如果是动态列对象
        if (field.getType().isAssignableFrom(List.class)) {
            Object itemListObj = ReflectionUtils.getField(field, target);
            if (Objects.isNull(itemListObj)) {
                itemListObj = Lists.newArrayList();
                ReflectionUtils.setField(field, target, itemListObj);
            }
            List itemList = (List) itemListObj;

            // 添加动态列对象
            String[] headNames = field.getAnnotation(ExcelCell.class).value();
//            MultistageItemBO multistageItem = MultistageItemBO.init(parseFieldValue(field, value, context), Arrays.asList(headNames));
//            itemList.add(multistageItem);
        } else {
            // 设置对象属性值
            ReflectionUtils.setField(field, target, parseFieldValue(field, value, context));
        }
        return target;
    }

    /**
     * 解析属性字段值
     *
     * @param field   字段
     * @param value   表格值
     * @param context 上下文对象
     * @return 解析结果
     */
    private Object parseFieldValue(Field field, String value, AnalysisContext context) throws Exception {
        // 导入参数校验
        ApplicationContextUtil.getBean(CompositeImportCheckHandler.class).check(field, value);

        // 不需要进行属性转换
        ExcelCell excelCell = field.getAnnotation(ExcelCell.class);
        if (Objects.isNull(excelCell.converter()) || excelCell.converter().isAssignableFrom(AutoConverter.class)) {
            return value;
        }

        // 获取导入转换器实例 & 转换数据
        Class<? extends Converter<?>> converter = excelCell.converter();
        Converter<?> converterInstance = ReportConverterLoader.loadImportConverter().get(ConverterKeyBuild.buildKey(converter));
        ExcelContentProperty excelContentProperty = new ExcelContentProperty();
        excelContentProperty.setField(field);
        if (Objects.isNull(value)) {
            value = ReportConst.EMPTY;
        }
        return converterInstance.convertToJavaData(new ReadConverterContext<>(new ReadCellData<>(value), excelContentProperty, context));
    }

    /**
     * 表头校验
     *
     * @param headMap 表头
     */
    private void checkHead(Map<Integer, String> headMap) {
        for (Map.Entry<Integer, String> entry : headMap.entrySet()) {
            if (StringUtil.isBlank(entry.getValue())) {
                continue;
            }

            // 校验表头名称
            Field field = targetFieldMap.get(entry.getKey());
            String headName = field.getAnnotation(ExcelCell.class).value()[0];
            ExceptionCode.IMPORT_HEAD_DIFF_TEMPLATE_FAIL.assertIsTrue(entry.getValue().equals(headName));
        }
    }

    /**
     * 组装表头下标名称属性映射
     *
     * @param clazz 类对象
     * @return 属性字段
     */
    private Map<Integer, Field> assemblyTargetFieldMap(Class<?> clazz) {
        HashMap<Integer, Field> fieldMap = new HashMap<>(16);
        while (!(clazz.isAssignableFrom(Object.class))) {
            Field[] fields = clazz.getDeclaredFields();
            Arrays.stream(fields)
                    .filter(f -> f.isAnnotationPresent(ExcelCell.class))
                    .forEach(f -> {
                        ExcelCell excelCell = f.getAnnotation(ExcelCell.class);
                        ExceptionCode.IMPORT_INDEX_REPEATED_FAIL.assertIsTrue(!fieldMap.containsKey(excelCell.index()), excelCell.index());

                        // 追加表头下标-属性映射
                        f.setAccessible(Boolean.TRUE);
                        fieldMap.put(excelCell.index(), f);
                    });
            clazz = clazz.getSuperclass();
        }
        return fieldMap;
    }

}
