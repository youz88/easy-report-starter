package com.github.youz.report;

import com.github.youz.report.config.ReportProperties;
import com.github.youz.report.util.ApplicationContextUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 配置类
 */
@Configuration
@EnableScheduling
@MapperScan("com.github.youz.report.repository")
@ComponentScan(basePackages = "com.github.youz.report")
@EnableConfigurationProperties(ReportProperties.class)
public class ReportAutoConfiguration {

    @Bean
    public ApplicationContextUtil applicationContextUtil() {
        return new ApplicationContextUtil();
    }

}
