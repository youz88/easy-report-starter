package com.github.youz.report.web.dto;

import lombok.Data;

@Data
public class PageDTO {

    /**
     * 当前页
     */
    private int pageNum = 1;

    /**
     * 页大小
     */
    private int pageSize = 10;
}
