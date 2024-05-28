package com.github.youz.report.web.vo;

import com.github.youz.report.model.ReportTask;
import com.github.youz.report.util.JsonUtil;
import com.mybatisflex.core.paginate.Page;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ReportListVO {

    /**
     * 返回数据列
     */
    private List<ReportInfoVO> dataList;

    /**
     * 总数
     */
    private long totalRow;

    /**
     * 将分页信息组装成ReportListVO对象
     *
     * @param pageInfo 分页信息
     * @return 组装后的ReportListVO对象
     */
    public static ReportListVO assemblyData(Page<ReportTask> pageInfo) {
        return new ReportListVO()
                .setTotalRow(pageInfo.getTotalRow())
                .setDataList(JsonUtil.convertList(pageInfo.getRecords(), ReportInfoVO.class));
    }
}
