package com.github.youz.report.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "report.export")
public class ExportProperties {

    /**
     * 默认分页大小
     */
    private int pageSize = 100;

    /**
     * 切片子任务所需数据最大值
     */
    private int slicesTaskMaxSize = 500000;

    /**
     * 异步任务执行所需数据最大值
     */
    private int asyncTaskMaxSize = 200;

    /**
     * 为避免大量异步任务持续查询对数据库造成压力, 所以可配置查询睡眠间隔时间(毫秒)
     */
    private long asyncTaskSleepTime = 100;

    /**
     * 导出文件是否上传到云存储
     */
    private boolean uploadCloud = false;
}
