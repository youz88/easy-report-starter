package com.github.youz.report.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "report.import")
public class ImportProperties {

    /**
     * 批量处理行数
     */
    private int batchRow = 100;

    /**
     * 限制最大行数
     */
    private int limitMaxRow = 20000;

}
