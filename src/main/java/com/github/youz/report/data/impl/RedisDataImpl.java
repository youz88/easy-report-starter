package com.github.youz.report.data.impl;

import com.github.youz.report.constant.CacheConst;
import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.data.RedisData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisDataImpl implements RedisData {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Boolean setIfAbsent(String key, Object value, Long seconds) {
        return redisTemplate.opsForValue().setIfAbsent(key, 1, Duration.ofSeconds(seconds));
    }

    @Override
    public Boolean importLock(String key) {
        return setIfAbsent(key, ReportConst.ZER0, Duration.ofSeconds(CacheConst.REPORT_IMPORT_TTL).getSeconds());
    }

    @Override
    public void importUnlock(String key) {
        redisTemplate.delete(key);
    }
}
