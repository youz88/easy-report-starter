package com.github.youz.report.util;


import com.github.youz.report.constant.ReportConst;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 日期工具类
 */
public class DateUtil {

    /**
     * 毫秒时间戳起始长度
     */
    private static final Integer MILLISECOND_LENGTH = 13;

    /**
     * 返回当前时间的Unix时间戳（秒）
     *
     * @return 当前时间的Unix时间戳（秒）
     */
    public static long now() {
        return Instant.now().getEpochSecond();
    }

    /**
     * 格式化日期
     *
     * @param timestamp 精确到秒
     * @param patten    格式
     * @return 格式化时间
     */
    public static String format(Long timestamp, String patten) {
        if (timestamp == null || timestamp <= 0) {
            return ReportConst.MINUS_SYMBOL;
        }

        // 将timestamp转换为LocalDateTime
        LocalDateTime localDateTime = timestamp.toString().length() >= MILLISECOND_LENGTH
                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
                : LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());

        // 返回格式化后的日期字符串
        return localDateTime.format(DateTimeFormatter.ofPattern(patten));
    }

}
