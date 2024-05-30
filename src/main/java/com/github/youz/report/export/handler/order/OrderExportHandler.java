package com.github.youz.report.export.handler.order;

import com.github.youz.report.data.MockRemoteData;
import com.github.youz.report.enums.BusinessType;
import com.github.youz.report.export.bo.common.AbstractExportBO;
import com.github.youz.report.export.bo.common.ExportContext;
import com.github.youz.report.export.bo.common.ExportData;
import com.github.youz.report.export.bo.common.ExportHead;
import com.github.youz.report.export.bo.order.OrderBO;
import com.github.youz.report.export.handler.AbstractDataAssemblyExportHandler;
import com.github.youz.report.web.dto.order.OrderRespDTO;
import com.github.youz.report.web.vo.PageVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 订单导出
 */
@Component
@RequiredArgsConstructor
public class OrderExportHandler extends AbstractDataAssemblyExportHandler {

    private final MockRemoteData mockRemoteData;

    @Override
    protected long queryTotal(String queryParam) {
        return 100L;
    }

    @Override
    public BusinessType businessType() {
        return BusinessType.ORDER;
    }

    @Override
    protected ExportHead handleHead(ExportContext context) {
        // 组装表头
        AbstractExportBO abstractExport = OrderBO.assemblyHead(context);

        // 返回表头对象
        return ExportHead.assemblyData(abstractExport, context);
    }

    @Override
    protected ExportData handleData(ExportContext context) {
        // 查询商户列表
        PageVO<OrderRespDTO> pageDTO = mockRemoteData.orderPageInfo(context.getQueryParam(), context.getPageNum(), context.getPageSize());
        if (Objects.isNull(pageDTO) || CollectionUtils.isEmpty(pageDTO.getList())) {
            return null;
        }

        // 组装表体
        AbstractExportBO abstractExport = OrderBO.assemblyData(pageDTO.getList(), context);

        // 返回表体对象
        return ExportData.assemblyData(abstractExport);
    }
}
