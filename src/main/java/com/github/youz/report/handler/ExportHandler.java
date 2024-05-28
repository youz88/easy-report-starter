package com.github.youz.report.handler;

import com.github.youz.report.config.ExportProperties;
import com.github.youz.report.enums.BusinessType;
import com.github.youz.report.enums.ExecutionType;
import com.github.youz.report.util.ApplicationContextUtil;

public interface ExportHandler {

    /**
     * 获取导出触发类型
     *
     * @param context 任务上下文
     * @return 导出触发类型结果集
     */
    ExportTotal total(ExportContext context);

    /**
     * 导出 excel 文件
     *
     * @param context 任务上下文
     * @return 临时文件路径
     */
    String handler(ExportContext context);

    /**
     * 标识导出业务类型
     *
     * @return 导出业务类型
     */
    BusinessType businessType();

    /**
     * 判断是否异步执行导出任务
     *
     * @param total 导出任务总数
     * @return 如果导出任务总数大于异步任务最大数量，则返回true，否则返回false
     */
    default ExecutionType resolveExecutionType(long total) {
        // 获取导出属性配置
        ExportProperties exportProperties = ApplicationContextUtil.getBean(ExportProperties.class);

        // 判断导出任务总数是否大于异步任务最大数量, 如果是，则返回异步执行类型；否则返回同步执行类型
        return total > exportProperties.getAsyncTaskMaxSize() ? ExecutionType.ASYNC : ExecutionType.SYNC;
    }
}
