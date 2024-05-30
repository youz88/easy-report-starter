package com.github.youz.report.web.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 分页返回结果
 */
@Data
@Accessors(chain = true)
public class PageVO<T> implements Serializable {

    /**
     * 当前页码
     */
    private Long pageNum;

    /**
     * 每页记录数
     */
    private Long pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 列表数据
     */
    private List<T> list;

}
