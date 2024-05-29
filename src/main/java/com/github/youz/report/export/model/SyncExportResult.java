package com.github.youz.report.export.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 同步导出结果
 */
@Data
@Accessors(chain = true)
public class SyncExportResult {

    /**
     * sheet名称
     */
    private String sheetName;

    /**
     * 表头
     */
    private List<List<String>> headList;

    /**
     * 表体
     */
    private List<String> dataList;
}
