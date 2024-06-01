package com.github.youz.report.export.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 预导出结果
 */
@Data
@Accessors(chain = true)
public class PreExportResult {

    /**
     * 总数
     */
    private Long total;

    /**
     * 执行类型
     */
    private Integer execType;

    /**
     * 分片索引(数据量大而分割为子任务时索引)
     */
    private Integer slicedIndex;

    /**
     * 目录名
     */
    private String directoryName;

    /**
     * 文件名
     */
    private String fileName;

}
