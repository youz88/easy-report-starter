package com.github.youz.report.enums;

import com.github.youz.report.constant.ReportConst;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 报表状态
 */
@Getter
@RequiredArgsConstructor
public enum ReportStatus {

    WAIT(0, "待执行"),

    EXECUTION(5, "执行中"),

    LOCAL_FILE_SUCCESS(10, "生成本地文件成功"),

    CHECK(15, "数据校验中"),

    IMPORTING(20, "数据导入中"),

    UNDONE(99, "未完成"),

    COMPLETED(100, "已完成"),

    EXECUTION_FAILED(150, "执行失败"),

    FAILED_UPLOAD_FILE(151, "上传文件失败"),

    IMPORT_FAIL(152, "导入失败"),
    ;

    /**
     * 业务编码
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
                .map(ReportStatus::getMessage).findAny()
                .orElse(ReportConst.EMPTY);
    }
}
