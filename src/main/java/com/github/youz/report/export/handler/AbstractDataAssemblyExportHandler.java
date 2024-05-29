package com.github.youz.report.export.handler;


import com.github.youz.report.config.ExportProperties;
import com.github.youz.report.enums.ExecutionType;
import com.github.youz.report.export.model.*;
import com.github.youz.report.util.ApplicationContextUtil;

import java.util.UUID;

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
                .setDirectoryName(UUID.randomUUID().toString())
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
    public AsyncExportResult asyncExport(ExportContext context) {
        // 获取表头
        ExportHead exportHead = handleHead(context);

//        return writeData(context, exportHead);
        return null;
    }

    /**
     * 生成sheet名称
     *
     * @return 返回sheet名称
     */
    protected String generateSheetName() {
        return businessType().getMessage();
    }

//    /**
//     * 生成报表文件
//     *
//     * @param context    任务上下文
//     * @param exportHead 表头
//     * @return 临时文件地址
//     * @throws IOException io异常
//     */
//    protected String writeData(ExportContext context, ExportHead exportHead) throws Exception {
//        // 组装临时文件路径
//        String tempFilePath = buildTempFilePath(context.getTaskId(), context.getFileName());
//
//        // 创建导出excel对象
//        try (ExcelWriter writer = createExcelWriter(tempFilePath)) {
//            // 创建sheet对象
//            WriteSheet sheet = createWriteSheet(headTaskRespBO.getHead(), this.type().getMessage());
//
//            // 设置开始、结束、分页大小、当前页码
//            int start = (context.getTaskChunk() - 1) * ExportHandlerInterface.ADD_TASK_MAX_DATA_SIZE;
//            int end = start + ExportHandlerInterface.ADD_TASK_MAX_DATA_SIZE;
//            context.setSize(getPageSize());
//            context.setCurrent(start / getPageSize() + 1);
//
//            // 分页查询追加表体数据
//            ExportResultBO dataTaskRespBO;
//            for (int i = start; i < end && i <= total; i += getPageSize()) {
//                // 行号++
//                context.setRowIndex(i - start);
//
//                // 文件导出
//                dataTaskRespBO = this.handleData(context);
//                if (Objects.isNull(dataTaskRespBO) || Objects.isNull(dataTaskRespBO.getData())) {
//                    break;
//                }
//
//                writer.write(dataTaskRespBO.getData(), sheet);
//
//                // 页码++
//                context.setCurrent(context.getCurrent() + 1);
//
//                // 线程睡眠
//                sleep(context.getTargetTypeEnum());
//            }
//
//            // 最后添加总计
//            List<?> totalData = headTaskRespBO.getData();
//            if (CollectionUtils.isNotEmpty(totalData)) {
//                writer.write(totalData, sheet);
//            }
//        }
//        return tempFilePath;
//    }
//
//    /**
//     * 构建sheet样式
//     *
//     * @param builder sheet构建器
//     * @return 构建器
//     */
//    protected ExcelWriterSheetBuilder buildSheetStyle(ExcelWriterSheetBuilder builder) {
//        return builder.registerWriteHandler(ExcelUtil.createExcelStyle())
//                .registerWriteHandler(ExcelUtil.createAutoColumn());
//    }
//
//    /**
//     * 获取分页大小
//     */
//    protected Long getPageSize() {
//        return ExportHandlerInterface.DEFAULT_PAGE_SIZE;
//    }
//
//    /**
//     * 生成临时文件路径[/root/export/taskId/fileName.xlsx]
//     *
//     * @param taskId   任务ID
//     * @param fileName 文件名称
//     * @return 临时文件路径
//     */
//    private String buildTempFilePath(Long taskId, String fileName) {
//        return String.join(File.separator, BizConst.EXPORT_ROOT_DIRECTORY, taskId.toString(), fileName) + ExcelTypeEnum.XLSX.getValue();
//    }
//
//    /**
//     * 睡眠
//     *
//     * @param targetType 同步状态
//     */
//    private void sleep(ReportTargetTypeEnum targetType) throws InterruptedException {
//        if (Objects.isNull(targetType) || ReportTargetTypeEnum.SYNC == targetType) {
//            return;
//        }
//
//        // 如果为异步导出，为避免服务频繁调用，睡眠100毫秒
//        TimeUnit.MILLISECONDS.sleep(ASYNC_SLEEP_TIME);
//    }

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
