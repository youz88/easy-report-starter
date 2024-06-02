package com.github.youz.report.export.handler;


import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.youz.report.config.ExportProperties;
import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.enums.DateFormatType;
import com.github.youz.report.enums.ExecutionType;
import com.github.youz.report.export.bo.*;
import com.github.youz.report.util.ApplicationContextUtil;
import com.github.youz.report.util.ExcelExportUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 导出数据组装抽象类
 */
public abstract class AbstractDataAssemblyExportHandler implements ExportBusinessHandler {

    /**
     * 查询导出总数
     *
     * @param queryParam 查询参数
     * @return 导出总数
     */
    protected abstract long queryTotal(String queryParam);

    /**
     * 组装 excel 表头
     *
     * @param context 任务上下文
     * @return 表头
     */
    protected abstract ExportHead handleHead(ExportContext context);

    /**
     * 组装 excel 表体
     *
     * @param context 任务上下文
     * @return 任务返回DTO
     */
    protected abstract ExportData handleBody(ExportContext context);

    @Override
    public PreExportResult preExport(String queryParam) {
        // 查询导出总数
        long total = queryTotal(queryParam);
        return new PreExportResult()
                .setTotal(total)
                .setExecType(resolveExecutionType(total))
                .setSlicedIndex(resolveSlicedSize(total))
                .setDirectoryName(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatType.YYYYMMDDHHMMSS.getValue())))
                .setFileName(businessType().getMessage());
    }

    @Override
    public SyncExportResult syncExport(ExportContext context) {
        // 创建sheet名称
        String sheetName = generateSheetName(context);

        // 获取表头
        ExportHead exportHead = handleHead(context);

        // 获取表体数据
        List<Object> dataList = assemblyMainData(context);

        return new SyncExportResult()
                .setSheetName(sheetName)
                .setHeadList(exportHead.getHeadList())
                .setDataList(dataList);
    }

    @Override
    public AsyncExportResult asyncExport(ExportContext context) {
        // 获取表头
        ExportHead exportHead = handleHead(context);

        // 生成报表文件
        return writeData(context, exportHead);
    }

    /**
     * 生成sheet名称
     *
     * @param context 导出上下文
     * @return 返回sheet名称
     */
    protected String generateSheetName(ExportContext context) {
        return businessType().getMessage();
    }

    /**
     * 获取分页大小
     *
     * @param exportProperties 导出配置信息
     */
    protected int getPageSize(ExportProperties exportProperties) {
        return exportProperties.getPageSize();
    }

    /**
     * 处理末尾数据并返回ExportData对象
     *
     * @param context 导出上下文
     * @return 末尾数据的ExportData对象，如果无需处理则返回null
     */
    protected ExportData handleEndData(ExportContext context) {
        return null;
    }

    /**
     * 生成报表文件
     *
     * @param context    任务上下文
     * @param exportHead 表头
     * @return 临时文件地址
     */
    private AsyncExportResult writeData(ExportContext context, ExportHead exportHead) {
        // 构建本地文件路径
        String localFilePath = buildLocalFilePath(context.getDirectoryName(), context.getFileName());

        // 创建ExcelWriter对象(try-with-resources)
        try (ExcelWriter writer = ExcelExportUtil.createExcelWriter(localFilePath)) {
            // 创建sheet对象
            WriteSheet sheet = ExcelExportUtil.createWriteSheet(exportHead.getHeadList(), generateSheetName(context));

            // 追加主体数据
            appendMainData(writer, sheet, context);

            // 追加尾部数据
            appendEndData(writer, sheet, context);
        }
        return new AsyncExportResult()
                .setLocalFilePath(localFilePath);
    }

    /**
     * 将主数据追加到ExcelWriter中
     *
     * @param writer  ExcelWriter对象，用于写入数据
     * @param sheet   WriteSheet对象，表示Excel中的一个Sheet页
     * @param context ExportContext对象，包含导出所需的上下文信息
     */
    private void appendMainData(ExcelWriter writer, WriteSheet sheet, ExportContext context) {
        // 获取导出配置信息
        ExportProperties exportProperties = ApplicationContextUtil.getBean(ExportProperties.class);

        // 初始化开始、结束、分页大小、当前页码
        int start = (context.getSlicedIndex() - 1) * exportProperties.getSlicesTaskMaxSize();
        int end = start + exportProperties.getSlicesTaskMaxSize();
        int pageSize = getPageSize(exportProperties);
        int pageNum = start / pageSize + 1;
        Long total = context.getTotal();

        // 分页查询追加表体数据
        ExportData exportData;
        for (int i = start; i < end && i <= total; i += pageSize) {
            // 设置查询参数: 分页大小、当前页码、行号
            context.setRowIndex(i - start + ReportConst.ONE)
                    .setPageNum(pageNum++)
                    .setPageSize(pageSize);

            // 查询导出数据
            exportData = handleBody(context);

            // 导出数据为空，跳过
            if (exportData == null || CollectionUtils.isEmpty(exportData.getDataList())) {
                break;
            }

            // 追加行数据
            writer.write(exportData.getDataList(), sheet);

            // 线程睡眠
            sleep(exportProperties);
        }
    }

    /**
     * 组装导出表体数据
     *
     * @param context 导出上下文
     * @return 导出数据列表
     */
    private List<Object> assemblyMainData(ExportContext context) {
        List<Object> dataList = new ArrayList<>();

        // 初始化分页大小、当前页码、总计
        ExportProperties exportProperties = ApplicationContextUtil.getBean(ExportProperties.class);
        int pageNum = ReportConst.ONE;
        int pageSize = getPageSize(exportProperties);
        long total = context.getTotal();

        // 分页查询追加表体数据
        for (int i = ReportConst.ZER0; i <= total; i += pageSize) {
            // 设置查询参数: 分页大小、当前页码、行号
            context.setRowIndex(i + ReportConst.ONE)
                    .setPageNum(pageNum++)
                    .setPageSize(pageSize);

            // 查询导出数据
            ExportData exportData = handleBody(context);

            // 导出数据为空，跳过
            if (exportData == null || CollectionUtils.isEmpty(exportData.getDataList())) {
                break;
            }

            // 追加表体数据
            dataList.addAll(exportData.getDataList());
        }
        return dataList;
    }

    /**
     * 向ExcelWriter追加末尾数据
     *
     * @param writer  ExcelWriter对象，用于写入数据
     * @param sheet   WriteSheet对象，表示要写入数据的sheet
     * @param context ExportContext对象，包含导出所需的上下文信息
     */
    private void appendEndData(ExcelWriter writer, WriteSheet sheet, ExportContext context) {
        // 查询末尾数据
        ExportData exportData = handleEndData(context);
        if (exportData == null || CollectionUtils.isEmpty(exportData.getDataList())) {
            return;
        }

        // 追加末尾数据
        writer.write(exportData.getDataList(), sheet);
    }

    /**
     * 构建本地文件路径[/root/export/businessTypeName/directoryName/fileName.xlsx]
     *
     * @param directoryName 本地文件目录
     * @param fileName      本地文件名称
     * @return 临时文件路径
     */
    private String buildLocalFilePath(String directoryName, String fileName) {
        return String.join(File.separator, ReportConst.EXPORT_ROOT_PATH,
                businessType().name().toLowerCase(), directoryName, fileName)
                + ExcelTypeEnum.XLSX.getValue();
    }

    /**
     * 为避免服务频繁调用，查询设置睡眠间隔时间(默认100毫秒)
     *
     * @param exportProperties 导出配置信息
     */
    private void sleep(ExportProperties exportProperties) {
        long asyncTaskSleepTime = exportProperties.getAsyncTaskSleepTime();
        if (asyncTaskSleepTime <= 0) {
            return;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(asyncTaskSleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据导出任务总数解析执行类型
     *
     * @param total 导出任务总数
     * @return 返回解析后的执行类型编码，如果导出任务总数大于异步任务最大数量则返回异步执行类型编码，否则返回同步执行类型编码
     */
    private Integer resolveExecutionType(long total) {
        // 获取导出属性配置
        ExportProperties exportProperties = ApplicationContextUtil.getBean(ExportProperties.class);

        // 判断导出任务总数是否大于异步任务最大数量, 如果是，则返回异步执行类型；否则返回同步执行类型
        return total > exportProperties.getAsyncTaskMaxSize() ? ExecutionType.ASYNC.getCode() : ExecutionType.SYNC.getCode();
    }

    /**
     * 解析切片大小
     *
     * @param total 总大小
     * @return 切片数量
     */
    private Integer resolveSlicedSize(long total) {
        // 获取导出属性配置
        ExportProperties exportProperties = ApplicationContextUtil.getBean(ExportProperties.class);

        // 计算切片数量
        int chunkNum = (int) (total / exportProperties.getSlicesTaskMaxSize());
        return chunkNum == 0
                ? ReportConst.ONE
                : total % exportProperties.getSlicesTaskMaxSize() == 0 ? chunkNum : chunkNum + 1;
    }
}
