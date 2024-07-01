package com.github.youz.report.export.chain;

import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.model.ReportTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 导出链抽象类
 */
@Slf4j
public abstract class AbstractExportChain implements ExportChain {

    /**
     * 调用链
     */
    protected ExportChain next;

    /**
     * 自定义处理
     *
     * @param reportTask 报表任务对象
     */
    abstract void customHandler(ReportTask reportTask);

    @Override
    public void handler(ReportTask reportTask) {
        try {
            // 自定义处理
            customHandler(reportTask);
        } catch (Exception e) {
            log.error("Export failed", e);

            // 异常处理
            failBack(reportTask, e);
            return;
        }
        if (Objects.isNull(next)) {
            return;
        }

        // 调用下一个处理
        next.handler(reportTask);
    }

    /**
     * 失败回退方法
     *
     * @param reportTask 报表任务对象
     */
    protected void failBack(ReportTask reportTask, Exception e) {

    }

    @Override
    public ExportChain getNext() {
        return next;
    }

    @Override
    public ExportChain setNext(ExportChain next) {
        this.next = next;
        return next;
    }

    /**
     * 判断是否为已拆分的父任务
     *
     * @param reportTask 报表任务对象
     * @return 若为已拆分的主任务则返回true，否则返回false
     */
    public boolean isSlicedParentTask(ReportTask reportTask) {
        return reportTask.getPid() == ReportConst.ZER0 && reportTask.getSlicedIndex() > ReportConst.ONE;
    }
}
