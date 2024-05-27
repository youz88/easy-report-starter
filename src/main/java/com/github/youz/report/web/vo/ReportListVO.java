package com.github.youz.report.web.vo;

import com.github.youz.report.model.ReportTask;
import com.mybatisflex.core.paginate.Page;
import lombok.Data;

@Data
public class ReportListVO {

    /**
     * 组装数据为ReportListVO的分页信息
     *
     * @param pageInfo ReportTask的分页信息
     * @return 组装后的ReportListVO分页信息
     */
    public static Page<ReportListVO> assemblyData(Page<ReportTask> pageInfo) {
        return null;
    }
}
