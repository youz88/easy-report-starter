package com.github.youz.report.enums;

import com.github.youz.report.util.ApplicationContextUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * 消息码枚举
 */
@Getter
@AllArgsConstructor
public enum MessageCode {

    // 通用
    COMMON_UPLOAD_FAIL("common.upload.fail", "文件上传云失败"),

    // 导出
    EXPORT_FILE_FAIL("export.file.fail", "导出文件失败"),

    EXPORT_HANDLER_EMPTY("export.file.fail", "导出文件失败"),

    EXPORT_NO_MATCH_BUSINESS_HANDLER("export.no.match.business.handler", "未找到业务匹配的导出处理器"),

    EXPORT_DATA_EMPTY("export.data.empty", "导出数据为空"),

    EXPORT_COMPRESSED_FILE_FAIL("export.compressed.file.fail", "生成压缩文件失败"),

    // 导入
    IMPORT_INDEX_REPEATED_FAIL("import.index.repeated.fail", "导入模版类下标重复"),

    IMPORT_EXCEED_LIMIT_ROW("import.exceed.limit.row", "导入数量超过最大限制"),

    IMPORT_HEAD_DIFF_TEMPLATE("import.head.diff.template", "表头与模版不一致"),

    IMPORT_FAIL_CUSTOM_MSG("import.fail.custom.message", "导入失败，自定义错误信息"),

    IMPORT_FIELD_FORMAT_FAIL("import.field.format.fail", "导入属性格式错误"),

    IMPORT_DOWNLOAD_FAIL("import.download.fail", "下载导入文件失败"),

    IMPORT_FAIL_REASON("import.fail.reason", "失败原因"),

    IMPORT_FAIL_FILE_SUFFIX_NAME("import.fail.file.suffix", "失败文件后缀名称"),

    IMPORT_NETWORK_ANOMALY("import.network.anomaly", "网络异常"),

    ;

    private final String code;

    private final String message;

    /**
     * 获取国际化消息
     *
     * @param args 消息中需要替换的参数列表
     * @return 返回国际化后的消息字符串
     */
    public String localMessage(Object... args) {
        return ApplicationContextUtil.getBean(MessageSource.class)
                .getMessage(String.valueOf(code), args, Locale.getDefault());
    }
}
