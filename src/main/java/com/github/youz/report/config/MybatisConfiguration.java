package com.github.youz.report.config;

import com.mybatisflex.core.audit.AuditManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis-flex sql 打印配置
 */
@Log4j2
@Configuration
@ConditionalOnProperty(prefix = "spring.profiles", name = "active", havingValue = "dev")
public class MybatisConfiguration {

    public MybatisConfiguration() {
        //开启审计功能
        AuditManager.setAuditEnable(true);

        //设置 SQL 审计收集器
        AuditManager.setMessageCollector(auditMessage ->
                log.info("{},{}ms", auditMessage.getFullSql(), auditMessage.getElapsedTime())
        );
    }
}
