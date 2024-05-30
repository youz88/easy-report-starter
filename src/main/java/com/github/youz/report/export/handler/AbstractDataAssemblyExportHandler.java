package com.github.youz.report.export.handler;


import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.youz.report.config.ExportProperties;
import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.enums.ExecutionType;
import com.github.youz.report.export.model.*;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.util.ApplicationContextUtil;
import com.github.youz.report.util.ExcelExportUtil;
import com.github.youz.report.util.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
    protected abstract ExportData handleData(ExportContext context);

    @Override
    public PreExportResult preExport(String queryParam) {
        long total = queryTotal(queryParam);
        return new PreExportResult()
                .setTotal(total)
                .setExecType(resolveExecutionType(total))
                .setDirectoryName(UUID.randomUUID().toString().replace(ReportConst.MINUS_SYMBOL, ReportConst.EMPTY))
                .setFileName(businessType().getMessage());
    }

    @Override
    public SyncExportResult syncExport(ExportContext context) {
        // 创建sheet名称
        String sheetName = generateSheetName();

        // 获取表头
        ExportHead exportHead = handleHead(context);

        // 获取表体
        ExportData exportData = handleData(context);

        return new SyncExportResult()
                .setSheetName(sheetName)
                .setHeadList(exportHead.getHeadList())
                .setDataList(exportData.getDataList());
    }

    @Override
    public AsyncExportResult asyncExport(ReportTask reportTask) {
        // 获取任务上下文
        ExportContext context = JsonUtil.toObject(reportTask.getContext(), ExportContext.class);

        // 获取表头
        ExportHead exportHead = handleHead(context);

        return writeData(reportTask, context, exportHead);
    }

    /**
     * 生成报表文件
     *
     * @param reportTask 报表任务
     * @param context    任务上下文
     * @param exportHead 表头
     * @return 临时文件地址
     */
    protected AsyncExportResult writeData(ReportTask reportTask, ExportContext context, ExportHead exportHead) {


        // 构建临时文件路径
        String tempFilePath = buildTempFilePath(context.getPreExportResult());

        // 创建导出excel对象
        try (ExcelWriter writer = ExcelExportUtil.createExcelWriter(tempFilePath)) {
            // 创建sheet对象
            WriteSheet sheet = ExcelExportUtil.createWriteSheet(exportHead.getHeadList(), generateSheetName());

            // 获取导出配置信息
            ExportProperties exportProperties = ApplicationContextUtil.getBean(ExportProperties.class);

            // 初始化开始、结束、分页大小、当前页码
            int start = (reportTask.getSlicedIndex() - 1) * exportProperties.getSlicesTaskMaxSize();
            int end = start + exportProperties.getSlicesTaskMaxSize();
            int pageSize = getPageSize(exportProperties);
            int pageNum = start / pageSize + 1;
            Long total = context.getPreExportResult().getTotal();

            // 分页查询追加表体数据
            ExportData exportData;
            for (int i = start; i < end && i <= total; i += pageSize) {
                // 设置查询参数: 分页大小、当前页码、行号
                context.setRowIndex(i - start + ReportConst.ONE)
                        .setPageNum(pageNum++)
                        .setPageSize(pageSize);

                // 查询导出数据
                exportData = this.handleData(context);

                // 导出数据为空，跳过
                if (exportData == null || CollectionUtils.isEmpty(exportData.getDataList())) {
                    break;
                }

                // 追加行数据
                writer.write(exportData.getDataList(), sheet);

                // 线程睡眠
                sleep();
            }

            // 末尾追加数据
            List<List<?>> totalDataList = appendEndData(context);
            if (CollectionUtils.isNotEmpty(totalDataList)) {
                totalDataList.stream()
                        .filter(CollectionUtils::isNotEmpty)
                        .forEach(data -> writer.write(data, sheet));
            }
        }
        return new AsyncExportResult();
    }

    /**
     * 追加末尾数据
     *
     * @param context 导出上下文
     * @return 末尾数据
     */
    protected List<List<?>> appendEndData(ExportContext context) {
        return Collections.emptyList();
    }

    /**
     * 生成sheet名称
     *
     * @return 返回sheet名称
     */
    protected String generateSheetName() {
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
     * 构建临时文件路径[/root/export/directoryName/fileName.xlsx]
     *
     * @param preExportResult 预导出结果
     * @return 临时文件路径
     */
    private String buildTempFilePath(PreExportResult preExportResult) {
        return String.join(File.separator, ReportConst.EXPORT_ROOT_DIRECTORY, preExportResult.getDirectoryName(), preExportResult.getFileName())
                + ExcelTypeEnum.XLSX.getValue();
    }

    /**
     * 为避免服务频繁调用，查询设置睡眠间隔时间(默认100毫秒)
     */
    private void sleep() {
        long asyncTaskSleepTime = ApplicationContextUtil.getBean(ExportProperties.class).getAsyncTaskSleepTime();
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
}
