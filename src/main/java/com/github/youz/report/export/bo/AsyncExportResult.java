package com.github.youz.report.export.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 异步导出结果
 */
@Data
@Accessors(chain = true)
public class AsyncExportResult {

    /**
     * 本地文件路径
     */
    String localFilePath;
}
