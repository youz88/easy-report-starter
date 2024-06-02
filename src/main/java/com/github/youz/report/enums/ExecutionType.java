package com.github.youz.report.enums;

import com.github.youz.report.constant.ReportConst;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

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

    /**
     * 根据编码获取对应的描述信息
     *
     * @param code 编码
     * @return 描述信息
     */
    public static String getMessageByCode(int code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode() == code)
                .map(ExecutionType::getMessage).findAny()
                .orElse(ReportConst.EMPTY);
    }
}
