package com.github.youz.report.enums;

import com.github.youz.report.constant.ReportConst;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 操作类型
 */
@Getter
@RequiredArgsConstructor
public enum OperationType {

    IMPORTS(1, "导入"),

    EXPORT(2, "导出"),

    ;

    /**
     * 编码
     */
    private final int code;

    /**
     * 描述
     */
    private final String message;

    /**
     * 根据编码获取对应的描述信息
     *
     * @param code 编码
     * @return 描述信息
     */
    public static String getMessageByCode(int code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode() == code)
                .map(OperationType::getMessage).findAny()
                .orElse(ReportConst.EMPTY);
    }

}
