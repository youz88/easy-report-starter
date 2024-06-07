package com.github.youz.report;

import com.github.youz.report.config.ReportProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.github.youz.report.repository")
@EnableConfigurationProperties(value = {ReportProperties.class})
public class EasyReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyReportApplication.class, args);
    }

}
