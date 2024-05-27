package com.github.youz.report.web.vo;

import com.github.youz.report.model.ReportTask;
import lombok.Data;

@Data
public class ReportInfoVO {

    /**
     * 将ReportTask对象组装为ReportInfoVO对象
     *
     * @param reportTask 要进行组装的ReportTask对象
     * @return 组装后的ReportInfoVO对象，如果组装失败则返回null
     */
    public static ReportInfoVO assemblyData(ReportTask reportTask) {
        return null;
    }
}
