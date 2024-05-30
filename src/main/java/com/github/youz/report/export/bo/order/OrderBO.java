package com.github.youz.report.export.bo.order;

import com.alibaba.excel.annotation.ExcelProperty;
import com.github.youz.report.annotation.DateTimeFormat;
import com.github.youz.report.annotation.DefaultValueFormat;
import com.github.youz.report.annotation.MoneyFormat;
import com.github.youz.report.export.bo.common.AbstractExportBO;
import com.github.youz.report.export.bo.common.ExportContext;
import com.github.youz.report.util.JsonUtil;
import com.github.youz.report.web.dto.order.OrderRespDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;


/**
 * 订单导出对象
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class OrderBO extends AbstractExportBO {

    /**
     * 构造导出导出表头
     *
     * @param context 导出任务上下文
     * @return 表头
     */
    public static AbstractExportBO assemblyHead(ExportContext context) {
        // 初始化导出对象, 过滤需要导出的字段
        return new OrderBO()
                .filterHead(ExportTemplate.class, context.getFieldNames());
    }

    /**
     * 构造导出导出对象
     *
     * @param sourceList 导出原始数据
     * @param context    导出任务上下文
     * @return 导出对象
     */
    public static AbstractExportBO assemblyData(List<OrderRespDTO> sourceList, ExportContext context) {
        int rowIndex = context.getRowIndex();

        // 1. 组装依赖信息

        // 2. 初始化导出对象
        OrderBO result = new OrderBO();
        List<ExportTemplate> dataList = new ArrayList<>();

        // 3. 格式化导出模板
        for (OrderRespDTO source : sourceList) {
            ExportTemplate exportTemplate = JsonUtil.convert(source, ExportTemplate.class);
            exportTemplate.setIndex(String.valueOf(rowIndex++));
            dataList.add(exportTemplate);
        }

        return result.setDataList(dataList)
                .filterData(context.getFieldList());
    }

    @Data
    @Accessors(chain = true)
    public static class ExportTemplate {

        @ExcelProperty("序号")
        private String index;

        @ExcelProperty("订单号")
        private String orderNo;

        @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
        @ExcelProperty("下单时间")
        private Integer orderTime;

        @DefaultValueFormat("未知")
        @ExcelProperty("订单状态")
        private String statusName;

        @MoneyFormat("%.2f")
        @ExcelProperty("订单金额")
        private Long amount;
    }
}
