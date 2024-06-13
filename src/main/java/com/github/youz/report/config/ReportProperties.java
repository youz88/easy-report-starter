package com.github.youz.report.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "report")
public class ReportProperties {

    /**
     * 通用配置
     */
    private CommonProperties common;

    /**
     * 导出配置
     */
    private ExportProperties export;

    /**
     * 导入配置
     */
    private ImportProperties imports;

    public ReportProperties() {
        this.common = new CommonProperties();
        this.export = new ExportProperties();
        this.imports = new ImportProperties();
    }

    @Data
    public static class CommonProperties {

        /**
         * 导出文件是否上传到云存储
         */
        private boolean uploadCloud = false;
    }

    @Data
    public static class ImportProperties {

        /**
         * 批量处理行数
         */
        private int batchRow = 100;

        /**
         * 限制最大行数
         */
        private int limitMaxRow = 20000;

    }

    @Data
    public static class ExportProperties {

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
         * 扫描待执行导出任务
         */
        private String scanWaitExecCron = "0 0/2 * * * ?";

        /**
         * 扫描待上传导出任务(仅限状态为上传失败)
         */
        private String scanWaitUploadCron = "0 0/3 * * * ?";
    }
}
