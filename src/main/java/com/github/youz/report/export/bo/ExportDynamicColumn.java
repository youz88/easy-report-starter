package com.github.youz.report.export.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 动态列
 */
@Data
@Accessors(chain = true)
public class ExportDynamicColumn {

    /**
     * 唯一标识
     */
    private Object uniqueKey;

    /**
     * 表头
     */
    private String[] head;

    /**
     * 数据
     */
    private Object data;

    /**
     * 构建动态模板表头
     *
     * @param uniqueKey 唯一标识, 用于处理表体数据时匹配表头
     * @param head      表头
     * @return 模板子项
     */
    public static ExportDynamicColumn buildHead(Object uniqueKey, String... head) {
        return new ExportDynamicColumn()
                .setHead(head)
                .setUniqueKey(uniqueKey);
    }

    /**
     * 构建动态模板表体
     *
     * @param data 表体
     * @return 模板子项
     */
    public static ExportDynamicColumn buildBody(Object data) {
        return new ExportDynamicColumn()
                .setData(data);
    }

}
