package com.github.youz.report.export.handler.order;

import com.github.youz.report.enums.BusinessType;
import com.github.youz.report.export.handler.AbstractDataAssemblyExportHandler;
import com.github.youz.report.export.model.ExportContext;
import com.github.youz.report.export.model.ExportData;
import com.github.youz.report.export.model.ExportHead;
import org.springframework.stereotype.Component;

/**
 * 订单导出
 */
@Component
public class OrderExportHandler extends AbstractDataAssemblyExportHandler {

    @Override
    protected long queryTotal(String queryParam) {
        return 10L;
    }

    @Override
    public BusinessType businessType() {
        return BusinessType.ORDER;
    }

    @Override
    protected ExportHead handleHead(ExportContext context) {
        return new ExportHead();
    }

    @Override
    protected ExportData handleData(ExportContext context) {
        return new ExportData();
    }
}
