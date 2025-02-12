package com.github.youz.report.cache;

import com.github.youz.report.constant.CacheConst;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Guava缓存实现<br>
 * <p color='red'>注意: 此缓存实现仅适合单机使用，不支持集群</p>
 */
@Service
@Conditional(DefaultCacheCondition.class)
public class GuavaCache implements CacheService {

    private final Cache<String, AtomicBoolean> lockCache = CacheBuilder.newBuilder()
            .expireAfterWrite(CacheConst.REPORT_IMPORT_TTL, TimeUnit.SECONDS)
            .build();

    @Override
    public Boolean lock(String key) {
        AtomicBoolean lock = lockCache.getIfPresent(key);
        if (Objects.nonNull(lock)) {
            return false;
        }

        lock = new AtomicBoolean(true);
        lockCache.put(key, lock);
        return true;
    }

    @Override
    public void unlock(String key) {
        lockCache.invalidate(key);
    }
}
