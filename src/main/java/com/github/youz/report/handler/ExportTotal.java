package com.github.youz.report.handler;

import com.github.youz.report.enums.ExecutionType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 导出总数据
 */
@Data
@Accessors(chain = true)
public class ExportTotal {

    /**
     * 总数
     */
    private Long total;

    /**
     * 执行类型
     */
    private ExecutionType execType;

    /**
     * 文件名
     */
    private String fileName;

}
