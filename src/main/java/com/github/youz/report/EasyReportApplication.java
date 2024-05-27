package com.github.youz.report;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.github.youz.report.repository")
public class EasyReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyReportApplication.class, args);
    }

}
