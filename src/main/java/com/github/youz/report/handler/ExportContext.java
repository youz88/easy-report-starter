package com.github.youz.report.handler;

import com.github.youz.report.web.dto.ExportFileDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 导出上下文
 */
@Data
@Accessors(chain = true)
public class ExportContext {

    /**
     * 导出文件ID
     */
    private Long id;

    /**
     * 总数
     */
    private Long total;

    /**
     * 导出参数
     */
    private String params;

    /**
     * 导出字段名称和顺序
     */
    private List<String> fieldNames;

    public static ExportContext build(ExportFileDTO reqDTO) {
        return new ExportContext()
                .setParams(reqDTO.getParams())
                .setFieldNames(reqDTO.getFieldNames());
    }
}
