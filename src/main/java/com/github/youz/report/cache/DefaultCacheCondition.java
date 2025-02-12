package com.github.youz.report.cache;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.env.Environment;

/**
 * 判断是否匹配默认缓存条件
 */
public class DefaultCacheCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        return !env.containsProperty("spring.redis.host");
    }
}
