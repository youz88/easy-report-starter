package com.github.youz.report.export.handler;

import com.github.youz.report.enums.BusinessType;
import com.github.youz.report.export.bo.AsyncExportResult;
import com.github.youz.report.export.bo.ExportContext;
import com.github.youz.report.export.bo.PreExportResult;
import com.github.youz.report.export.bo.SyncExportResult;
import com.github.youz.report.model.ReportTask;

public interface ExportBusinessHandler {

    /**
     * 导出前准备操作
     *
     * @param queryParam 查询参数
     * @return 返回预导出结果
     */
    PreExportResult preExport(String queryParam);

    /**
     * 导出 excel 文件
     *
     * @param context 任务上下文
     * @return 临时文件路径
     */
    SyncExportResult syncExport(ExportContext context);

    /**
     * 导出 excel 文件
     *
     * @param reportTask 任务信息
     * @return 临时文件路径
     */
    AsyncExportResult asyncExport(ReportTask reportTask);

    /**
     * 标识导出业务类型
     *
     * @return 导出业务类型
     */
    BusinessType businessType();

}
