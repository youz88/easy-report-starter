package com.github.youz.report.web.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReportListDTO extends PageDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 业务类型(1: 用户, 2: 商品...)
     *
     * @see com.github.youz.report.enums.BusinessType
     */
    private Integer businessType;
}
