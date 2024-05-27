package com.github.youz.report.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 业务类型
 */
@Getter
@RequiredArgsConstructor
public enum BusinessType {

    ORDER(1, "订单"),

    GOODS(2, "商品"),

    ;

    /**
     * 业务编码
     */
    private final int code;

    /**
     * 描述
     */
    private final String message;
}
