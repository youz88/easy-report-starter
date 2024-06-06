package com.github.youz.report.data;

public interface RedisData {

    /**
     * 设置缓存
     *
     * @param key     缓存key
     * @param value   值
     * @param seconds 缓存失效时间(秒)
     * @return 是否成功
     */
    Boolean setIfAbsent(String key, Object value, Long seconds);

    /**
     * 获取导入加锁状态
     *
     * @param key 缓存key
     * @return 如果获取到导入锁，则返回true；否则返回false
     */
    Boolean importLock(String key);

    /**
     * 导入加锁释放
     *
     * @param key 缓存key
     */
    void importUnlock(String key);
}
