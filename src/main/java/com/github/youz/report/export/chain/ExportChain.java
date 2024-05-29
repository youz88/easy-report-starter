package com.github.youz.report.export.chain;

import com.github.youz.report.model.ReportTask;

public interface ExportChain {

    /**
     * 处理报表任务并导出数据
     *
     * @param reportTask 报表任务对象
     */
    void handler(ReportTask reportTask);

    /**
     * 设置下一步调用链
     *
     * @param chain 调用链
     * @return 新的调用链
     */
    ExportChain setNext(ExportChain chain);

    /**
     * 获取下一步调用链
     *
     * @return 调用链
     */
    ExportChain getNext();

    /**
     * 失败回退方法
     *
     * @param reportTask 报表任务对象
     */
    void failBack(ReportTask reportTask);
}
