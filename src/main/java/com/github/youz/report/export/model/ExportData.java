package com.github.youz.report.export.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 导出数据
 */
@Data
@Accessors(chain = true)
public class ExportData {

    /**
     * 表体
     */
    private List<List<Object>> dataList;
}
