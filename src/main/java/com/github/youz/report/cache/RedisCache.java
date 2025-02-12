package com.github.youz.report.cache;

import com.github.youz.report.constant.CacheConst;
import com.github.youz.report.constant.ReportConst;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@ConditionalOnClass(RedisTemplate.class)
public class RedisCache implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Boolean lock(String key) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, ReportConst.ZER0,
                Duration.ofSeconds(CacheConst.REPORT_IMPORT_TTL));
        return Objects.nonNull(success) && success;
    }

    @Override
    public void unlock(String key) {
        redisTemplate.delete(key);
    }
}
