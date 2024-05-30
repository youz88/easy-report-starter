package com.github.youz.report.export.chain;

import com.github.youz.report.model.ReportTask;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 根导出链
 */
@Component
@Scope("prototype")
public class RootExportChain extends AbstractExportChain {

    @Override
    void customHandler(ReportTask reportTask) {
        // 占位
    }
}
