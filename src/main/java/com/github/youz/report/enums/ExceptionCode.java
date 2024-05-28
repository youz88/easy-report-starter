package com.github.youz.report.enums;

import com.github.youz.report.exception.ReportException;
import com.github.youz.report.util.ApplicationContextUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.MessageSource;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * 异常码
 */
@Getter
@AllArgsConstructor
public enum ExceptionCode {

    // 10000-19999 导出错误
    EXPORT_HANDLER_EMPTY(10001),

    EXPORT_NO_MATCH_HANDLER(10002),

    EXPORT_DATA_EMPTY(10003),

    ;

    /**
     * 错误码
     */
    private final int code;

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
        if (number == null || number.intValue() <= 0) {
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
        if (collection == null || collection.isEmpty()) {
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
        if (map == null || map.isEmpty()) {
            throw new ReportException(this, errorMessage());
        }
    }

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    private String errorMessage() {
        return ApplicationContextUtil.getBean(MessageSource.class)
                .getMessage(String.valueOf(code), null, Locale.getDefault());
    }
}
