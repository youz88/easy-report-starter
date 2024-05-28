package com.github.youz.report.handler.order;

import com.github.youz.report.enums.BusinessType;
import com.github.youz.report.handler.ExportContext;
import com.github.youz.report.handler.ExportHandler;
import com.github.youz.report.handler.ExportTotal;
import org.springframework.stereotype.Component;

/**
 * 订单导出
 */
@Component
public class OrderExportHandler implements ExportHandler {
    @Override
    public ExportTotal total(ExportContext context) {
        long total = 10L;
        return new ExportTotal()
                .setTotal(total)
                .setExecType(resolveExecutionType(total))
                .setFileName(businessType().name());
    }

    @Override
    public String handler(ExportContext context) {
        return null;
    }

    @Override
    public BusinessType businessType() {
        return BusinessType.ORDER;
    }
}
