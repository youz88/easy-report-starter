package com.github.youz.report.web.dto;

import com.github.youz.report.enums.BusinessType;
import lombok.Data;

@Data
public class ImportFileDTO {

    /**
     * 操作用户 id
     */
    private Long userId;

    /**
     * 导入上下文参数
     */
    private String context;

    /**
     * 业务类型
     *
     * @see BusinessType
     */
    private Integer businessType;

}
