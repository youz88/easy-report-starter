package com.github.youz.report.export.chain;

import com.github.youz.report.model.ReportTask;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 上传云文件
 */
@Component
@Scope("prototype")
public class UploadCloudFileExportChain extends AbstractExportChain {

    @Override
    void customHandler(ReportTask reportTask) {

    }
}
