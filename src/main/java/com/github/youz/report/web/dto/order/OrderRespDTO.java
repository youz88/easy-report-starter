package com.github.youz.report.web.dto.order;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 订单响应DTO
 */
@Data
@Accessors(chain = true)
public class OrderRespDTO {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 下单时间
     */
    private Long orderTime;

    /**
     * 订单状态名称
     */
    private String statusName;

    /**
     * 订单金额
     */
    private Long amount;
}
