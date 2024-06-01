package com.github.youz.report.export.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 导出数据对象
 */
@Data
@Accessors(chain = true)
public class ExportData {

    /**
     * 表体
     */
    private List<?> dataList;

    /**
     * 组装导出数据对象
     *
     * @param abstractExport 抽象导出业务对象
     * @return 组装后的导出数据对象
     */
    public static ExportData assemblyData(AbstractExportModel abstractExport) {
        return new ExportData()
                .setDataList(abstractExport.getDataList());
    }
}
