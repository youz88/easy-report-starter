package com.github.youz.report.config;

import com.github.youz.report.util.ApplicationContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 */
@Configuration
public class ReportConfig {

    @Bean
    public ApplicationContextUtil applicationContextUtil() {
        return new ApplicationContextUtil();
    }

}
