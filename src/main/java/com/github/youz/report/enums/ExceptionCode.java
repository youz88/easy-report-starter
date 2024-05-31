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

    // 10000-19999 导出错误
    /**
     * 导出处理器不能为空
     */
    EXPORT_HANDLER_EMPTY(10001),

    /**
     * 未找到业务匹配的导出处理器
     */
    EXPORT_NO_MATCH_BUSINESS_HANDLER(10002),

    /**
     * 导出数据为空
     */
    EXPORT_DATA_EMPTY(10003),

    /**
     * 下载文件失败
     */
    EXPORT_DOWNLOAD_FAIL(10004),

    /**
     * 未找到匹配的导出链路
     */
    EXPORT_NO_MATCH_CHAIN(10005),

    /**
     * 创建文件目录失败
     */
    EXPORT_MKDIR_FAIL(10006),

    /**
     * 文件上传云失败
     */
    EXPORT_UPLOAD_FAIL(10007),

    /**
     * 生成压缩文件失败
     */
    EXPORT_COMPRESSED_FAIL(10008),

    // 20000-29999 导入错误
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
     * @throws ReportException 如果flag不为true，则抛出异常
     */
    public void assertTrue(boolean flag) {
        if (!flag) {
            throw new ReportException(this, errorMessage());
        }
    }

    /**
     * 抛出异常
     *
     * @throws ReportException 抛出自定义异常，包含当前对象和错误信息
     */
    public void throwException() {
        throw new ReportException(this, errorMessage());
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
