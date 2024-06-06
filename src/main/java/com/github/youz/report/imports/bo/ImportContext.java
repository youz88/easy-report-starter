package com.github.youz.report.imports.bo;

import com.github.youz.report.model.ReportTask;
import com.github.youz.report.util.JsonUtil;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 导入上下文
 */
@Data
@Accessors(chain = true)
public class ImportContext {

    private Long id;

    /**
     * 本地文件路径
     */
    private String localFilePath;

    /**
     * 业务类型(1: 用户, 2: 商品...)
     *
     * @see com.github.youz.report.enums.BusinessType
     */
    private Integer businessType;

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 构建导入上下文对象
     *
     * @param reportTask 报表任务对象
     * @return 构建的导入上下文对象
     */
    public static ImportContext build(ReportTask reportTask) {
        return JsonUtil.convert(reportTask, ImportContext.class);
    }
}
