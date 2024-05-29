package com.github.youz.report.export.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 导出文件头
 */
@Data
@Accessors(chain = true)
public class ExportHead {

    /**
     * 表头
     */
    private List<List<String>> headList;
}
