package com.github.youz.report.export.bo.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * 动态列
 */
@Data
@Accessors(chain = true)
public class DynamicColumnBO {

    /**
     * 表头
     */
    private String[] head;

    /**
     * 数据
     */
    private Object data;

    public String[] getHead() {
        return Objects.isNull(head) ? null : head.clone();
    }

    public void setHead(String[] head) {
        this.head = Objects.isNull(head) ? null : head.clone();
    }

    /**
     * 组装导出模板子项
     *
     * @param isRenderHead 是否渲染表头
     * @param data         表体
     * @param head         表头
     * @return 模板子项
     */
    public static DynamicColumnBO init(Boolean isRenderHead, Object data, String... head) {
        DynamicColumnBO item = new DynamicColumnBO()
                .setData(data);
        if (isRenderHead) {
            item.setHead(head);
        }
        return item;
    }

}
