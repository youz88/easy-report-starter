package com.github.youz.report.export.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 导出文件头
 */
@Data
@Accessors(chain = true)
public class ExportHead {

    /**
     * 表头
     */
    private List<List<String>> headList;

    /**
     * 组装导出表头对象
     *
     * @param abstractExport 抽象导出业务对象
     * @param context        任务上下文
     * @return 导出表头对象
     */
    public static ExportHead assemblyData(AbstractExportModel abstractExport, ExportContext context) {
        // 设置过滤字段, 导出表体时使用
        context.setFieldList(abstractExport.getFieldList());

        // 设置导出表头
        return new ExportHead()
                .setHeadList(abstractExport.getHeadList());
    }
}
