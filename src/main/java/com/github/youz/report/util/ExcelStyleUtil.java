package com.github.youz.report.util;

import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import com.github.youz.report.constant.ReportConst;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表样式工具类
 */
public class ExcelStyleUtil {

    /**
     * 生成excel样式
     *
     * @return excel样式
     */
    public static HorizontalCellStyleStrategy createExcelStyle() {
        return createExcelStyle(Boolean.FALSE);
    }

    /**
     * 生成excel样式
     *
     * @param wrapped 是否自动换行
     * @return excel样式
     */
    public static HorizontalCellStyleStrategy createExcelStyle(Boolean wrapped) {
        WriteCellStyle headCellStyle = new WriteCellStyle();
        headCellStyle.setWrapped(Boolean.FALSE);
        WriteCellStyle contentCellStyle = new WriteCellStyle();
        contentCellStyle.setWrapped(wrapped);
        contentCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        contentCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentCellStyle.setBorderRight(BorderStyle.THIN);
        contentCellStyle.setBorderLeft(BorderStyle.THIN);
        contentCellStyle.setBorderTop(BorderStyle.THIN);
        contentCellStyle.setBorderBottom(BorderStyle.THIN);
        return new HorizontalCellStyleStrategy(headCellStyle, contentCellStyle);
    }

    /**
     * 固定列宽设置
     *
     * @return 设置
     */
    public static ReportColumnWidthStyleStrategy createAutoColumn() {
        return createAutoColumn(40);
    }

    /**
     * 固定列宽设置
     *
     * @param columnWidth 列宽
     * @return 设置
     */
    public static ReportColumnWidthStyleStrategy createAutoColumn(Integer columnWidth) {
        return new ReportColumnWidthStyleStrategy(columnWidth);
    }

    /**
     * 报表列宽样式
     */
    public static class ReportColumnWidthStyleStrategy extends AbstractColumnWidthStyleStrategy {

        private final Map<Integer, Map<Integer, Integer>> cache = MapUtils.newHashMapWithExpectedSize(8);

        private final Integer maxColumnWidth;

        public ReportColumnWidthStyleStrategy(Integer columnWidth) {
            this.maxColumnWidth = columnWidth;
        }

        @Override
        protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell,
                                      Head head,
                                      Integer relativeRowIndex, Boolean isHead) {
            boolean needSetWidth = isHead || !CollectionUtils.isEmpty(cellDataList);
            if (!needSetWidth) {
                return;
            }
            Map<Integer, Integer> maxColumnWidthMap = cache.computeIfAbsent(writeSheetHolder.getSheetNo(), key -> new HashMap<>(16));
            Integer columnWidth = dataLength(cellDataList, cell, isHead);
            if (columnWidth < ReportConst.ZER0) {
                return;
            }
            if (columnWidth > maxColumnWidth) {
                columnWidth = maxColumnWidth;
            }
            Integer maxColumnWidth = maxColumnWidthMap.get(cell.getColumnIndex());
            if (maxColumnWidth == null || columnWidth > maxColumnWidth) {
                maxColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
                writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
            }
        }

        /**
         * 计算数据长度
         *
         * @param cellDataList 数据列表
         * @param cell         当前表格
         * @param isHead       是否表头
         * @return 长度
         */
        private Integer dataLength(List<WriteCellData<?>> cellDataList, Cell cell, Boolean isHead) {
            if (isHead) {
                return cell.getStringCellValue().getBytes(StandardCharsets.UTF_8).length;
            }
            WriteCellData<?> cellData = cellDataList.get(ReportConst.ZER0);
            CellDataTypeEnum type = cellData.getType();
            if (type == null) {
                return -1;
            }
            switch (type) {
                case STRING:
                    return cellData.getStringValue().getBytes(StandardCharsets.UTF_8).length;
                case BOOLEAN:
                    return cellData.getBooleanValue().toString().getBytes(StandardCharsets.UTF_8).length;
                case NUMBER:
                    return cellData.getNumberValue().toString().getBytes(StandardCharsets.UTF_8).length;
                default:
                    return -1;
            }
        }
    }
}
