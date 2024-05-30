package com.github.youz.report.export.chain;

import com.github.youz.report.model.ReportTask;

import java.util.Objects;

/**
 * 导出链抽象类
 */
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
            // 异常处理
            failBack(reportTask, e);
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
}
