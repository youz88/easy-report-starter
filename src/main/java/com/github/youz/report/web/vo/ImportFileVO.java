package com.github.youz.report.web.vo;

import com.github.youz.report.model.ReportTask;
import com.github.youz.report.util.JsonUtil;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ImportFileVO {

    /**
     * 导入文件ID
     */
    private Long id;

    /**
     * 根据ID组装ImportFileVO对象
     *
     * @param reportTask 报表对象
     * @return 组装好的ImportFileVO对象
     */
    public static ImportFileVO assemblyData(ReportTask reportTask) {
        return JsonUtil.convert(reportTask, ImportFileVO.class);
    }
}
