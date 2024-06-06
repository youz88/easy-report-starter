package com.github.youz.report.imports.bo;

import lombok.Data;

/**
 * 导入动态列<br>
 * 注意: <span style="color:'red'">动态列如果是List数组, 则必须在属性末尾定义, 否则暂时无法实现解析</span>
 */
@Data
public class ImportDynamicColumn<T> {

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
    private T data;

    /**
     * 构建一个ImportDynamicColumn对象
     *
     * @param data 导入的数据
     * @param <T>  导入数据的类型
     * @return 返回一个ImportDynamicColumn对象
     */
    public static <T> ImportDynamicColumn<T> build(T data) {
        ImportDynamicColumn<T> dynamicColumn = new ImportDynamicColumn<>();
        dynamicColumn.setData(data);
        return dynamicColumn;
    }
}
