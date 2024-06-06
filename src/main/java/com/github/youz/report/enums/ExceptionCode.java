package com.github.youz.report.enums;

import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.exception.ReportException;
import com.github.youz.report.util.ApplicationContextUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.MapUtils;
import org.springframework.context.MessageSource;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * 异常码
 */
@Getter
@AllArgsConstructor
public enum ExceptionCode {

    // 10000-19999 通用错误
    COMMON_UPLOAD_FAIL(10001, MessageCode.COMMON_UPLOAD_FAIL),

    // 20000-29999 导出错误
    EXPORT_NO_MATCH_BUSINESS_HANDLER(10001, MessageCode.EXPORT_NO_MATCH_BUSINESS_HANDLER),

    EXPORT_DATA_EMPTY(10002, MessageCode.EXPORT_DATA_EMPTY),

    EXPORT_FILE_FAIL(10003, MessageCode.EXPORT_FILE_FAIL),

    EXPORT_COMPRESSED_FILE_FAIL(10008, MessageCode.EXPORT_COMPRESSED_FILE_FAIL),

    // 20000-29999 导入错误
    IMPORT_INDEX_REPEATED_FAIL(20001, MessageCode.IMPORT_INDEX_REPEATED_FAIL),

    IMPORT_LIMIT_ROW_FAIL(20002, MessageCode.IMPORT_EXCEED_LIMIT_ROW),

    IMPORT_HEAD_DIFF_TEMPLATE_FAIL(20003, MessageCode.IMPORT_HEAD_DIFF_TEMPLATE),

    IMPORT_FAIL_CUSTOM_MSG(20004, MessageCode.IMPORT_FAIL_CUSTOM_MSG),

    IMPORT_FIELD_FORMAT_FAIL(20005, MessageCode.IMPORT_FIELD_FORMAT_FAIL),

    IMPORT_DOWNLOAD_FAIL(20006, MessageCode.IMPORT_DOWNLOAD_FAIL),

    IMPORT_IN_PROGRESS(20007, MessageCode.IMPORT_IN_PROGRESS),

    ;

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误消息
     */
    private final MessageCode messageCode;

    /**
     * 判断一个对象是否为空，如果为空则抛出异常。
     *
     * @param obj 要判断的对象
     * @throws ReportException 如果obj为null，则抛出ReportException异常
     */
    public void assertNotNull(Object obj) {
        if (obj == null) {
            throw new ReportException(this, errorMessage());
        }
    }

    /**
     * 判断传入的数字是否大于0
     *
     * @param number 需要判断的数字，不能为null或小于等于0
     * @throws ReportException 如果传入的数字为null或小于等于0，则抛出ReportException异常
     */
    public void assertGtZero(Number number) {
        if (number == null || number.intValue() <= ReportConst.ZER0) {
            throw new ReportException(this, errorMessage());
        }
    }

    /**
     * 判断传入的集合是否为空
     *
     * @param collection 需要判断的集合，不能为空或没有元素
     * @throws ReportException 如果传入的集合为空或没有元素，则抛出ReportException异常
     */
    public void assertNotEmpty(Collection<?> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new ReportException(this, errorMessage());
        }
    }

    /**
     * 判断传入的Map对象是否非空
     *
     * @param map 需要判断的Map对象，不能为空或没有键值对
     * @throws ReportException 如果传入的Map对象为空或没有键值对，则抛出ReportException异常
     */
    public void assertNotEmpty(Map<?, ?> map) {
        if (MapUtils.isEmpty(map)) {
            throw new ReportException(this, errorMessage());
        }
    }

    /**
     * 校验传入的boolean值是否为true。
     *
     * @param flag 需要校验的boolean值
     * @param args 附加参数
     * @throws ReportException 如果flag不为true，则抛出异常
     */
    public void assertIsTrue(boolean flag, Object... args) {
        if (!flag) {
            throw new ReportException(this, errorMessage(args));
        }
    }

    /**
     * 抛出异常
     *
     * @throws ReportException 抛出自定义异常，包含当前对象和错误信息
     */
    public void throwException(Object... args) {
        throw new ReportException(this, errorMessage(args));
    }

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    private String errorMessage(Object... args) {
        return ApplicationContextUtil.getBean(MessageSource.class)
                .getMessage(messageCode.getCode(), args, Locale.getDefault());
    }

}
