package com.github.youz.report.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 格式化日期类型
 */
@Getter
@AllArgsConstructor
public enum DateFormatType {

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),

    /**
     * yyyy-MM-dd HH:mm
     */
    YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),

    /**
     * yyyy-MM-dd
     */
    YYYY_MM_DD("yyyy-MM-dd"),

    /**
     * yyyy-MM
     */
    YYYY_MM("yyyy-MM"),

    /**
     * yyyyMMddHHmmss
     */
    YYYYMMDDHHMMSS("yyyyMMddHHmmss"),

    /**
     * yyyy-MM-dd_HH_mm_ss
     */
    YYYYMMDD_HH_MM_SS("yyyy-MM-dd_HH_mm_ss"),

    /**
     * yyyyMMddHHmm
     */
    YYYYMMDDHHMM("yyyyMMddHHmm"),

    /**
     * yyyyMMddHHmm
     */
    YYYYMMDDHH_MM("yyyyMMddHH:mm"),

    /**
     * yyyyMMdd
     */
    YYYYMMDD("yyyyMMdd"),

    /**
     * yyyyMM
     */
    YYYYMM("yyyyMM"),

    /**
     * HH:mm
     */
    HH_MM("HH:mm"),

    /**
     * yyyy/MM/dd
     */
    USA_YYYY_MM_DD("yyyy/MM/dd"),

    /**
     * yyyy/M/d
     */
    USA_YYYY_M_D("yyyy/M/d"),

    /**
     * MM/dd/yyyy HH:mm:ss
     */
    USA_MM_DD_YYYY_HH_MM_SS("MM/dd/yyyy HH:mm:ss"),

    /**
     * yyyy/MM/dd HH:mm:ss
     */
    USA_YYYY_MM_DD_HH_MM_SS("yyyy/MM/dd HH:mm:ss"),

    /**
     * yyyy年MM月dd日 HH时mm分ss秒
     */
    CN_YYYY_MM_DD_HH_MM_SS("yyyy年MM月dd日 HH时mm分ss秒"),

    /**
     * yyyy年MM月dd日 HH点
     */
    CN_YYYY_MM_DD_HH("yyyy年MM月dd日 HH点"),

    /**
     * yyyy年MM月dd日 HH点
     */
    CN_YYYY_MM_DD_HH_MM("yyyy年MM月dd日 HH点mm分"),

    /**
     * yyyy年MM月dd日
     */
    CN_YYYY_MM_DD("yyyy年MM月dd日"),

    ;

    private String value;

}
