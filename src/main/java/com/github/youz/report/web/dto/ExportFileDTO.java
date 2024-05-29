package com.github.youz.report.web.dto;

import com.github.youz.report.enums.BusinessType;
import lombok.Data;

import java.util.List;

@Data
public class ExportFileDTO {

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 业务类型
     *
     * @see BusinessType
     */
    private Integer businessType;

    /**
     * 导出查询参数
     */
    private String queryParam;

    /**
     * 导出字段名称和顺序
     */
    private List<String> fieldNames;
}
