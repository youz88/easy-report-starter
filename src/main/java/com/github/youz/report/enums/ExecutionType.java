package com.github.youz.report.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 执行类型
 */
@Getter
@RequiredArgsConstructor
public enum ExecutionType {

    SYNC(1, "同步"),

    ASYNC(2, "异步"),

    ;

    /**
     * 业务编码
     */
    private final int code;

    /**
     * 描述
     */
    private final String message;

    public static boolean isSync(int code) {
        return SYNC.getCode() == code;
    }
}
