package com.github.youz.report.web.dto.order;

import lombok.Data;

/**
 * 订单请求DTO
 */
@Data
public class OrderReqDTO {

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;
}
