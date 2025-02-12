package com.github.youz.report.cache;

public interface CacheService {

    /**
     * 锁定指定的键
     *
     * @param key 需要锁定的键
     * @return 如果锁定成功，则返回 true；否则返回 false
     */
    Boolean lock(String key);

    /**
     * 解锁指定的键
     *
     * @param key 需要解锁的键
     */
    void unlock(String key);
}
