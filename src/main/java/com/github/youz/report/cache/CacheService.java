package com.github.youz.report.cache;

public interface CacheService {

    /**
     * 锁定指定的键<br>
     * 仅用了最简单是实现方式, 因为添加了锁失效时间, 所以这里并未过多考虑死锁问题, 因是并发量不高且允许短暂等待的业务场景
     *
     * @param key 需要锁定的键
     */
    Boolean lock(String key);

    /**
     * 解锁指定的键
     *
     * @param key 需要解锁的键
     */
    void unlock(String key);
}
