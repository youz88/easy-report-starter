package com.github.youz.report.data;

import com.github.youz.report.model.ReportTask;
import com.github.youz.report.web.dto.ReportListDTO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

public interface ReportTaskData {

    /**
     * 根据报表任务ID查询
     *
     * @param id 任务ID
     * @return 报表任务信息
     */
    ReportTask selectById(Long id);

    /**
     * 分页查询报表任务列表
     *
     * @param reqDTO 报表任务列表查询条件DTO
     * @return 报表任务分页信息
     */
    Page<ReportTask> pageInfo(ReportListDTO reqDTO);

    /**
     * 插入报表任务
     *
     * @param reportTask 报表任务对象
     */
    void insert(ReportTask reportTask);

    /**
     * 根据ID更新
     *
     * @param reportTask 任务对象，包含需要更新的信息
     */
    void updateById(ReportTask reportTask);

    /**
     * 扫描待执行导出任务
     *
     * @return 任务列表
     */
    List<ReportTask> scanExportTask();

}
