package com.github.youz.report.web.vo;

import com.github.youz.report.model.ReportTask;
import com.github.youz.report.util.JsonUtil;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ExportFileVO {

    private Long id;

    /**
     * 执行类型(0: 未知, 1: 同步, 2: 异步)
     */
    private Integer execType;

    /**
     * 将ReportTask对象转换为ExportFileVO对象
     *
     * @param reportTask 报告任务对象
     * @return 转换后的ExportFileVO对象
     */
    public static ExportFileVO assemblyData(ReportTask reportTask) {
        return JsonUtil.convert(reportTask, ExportFileVO.class);
    }
}
