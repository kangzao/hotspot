package org.promote.hotspot.client.cache;

import org.promote.hotspot.client.rule.RuleHolder;

/**
 * 用户可以自定义cache
 *
 * @author enping.jep
 * @date 2023/10/25 21:49
 **/
public class CacheFactory {

    private static final LocalCache DEFAULT_CACHE = new DefaultCaffeineCache();

    /**
     * 创建一个本地缓存实例
     */
    public static LocalCache build(int duration) {
        return new CaffeineCache(duration);
    }

    public static LocalCache getNonNullCache(String key) {
        LocalCache localCache = getCache(key);
        if (localCache == null) {
            return DEFAULT_CACHE;
        }
        return localCache;
    }

    public static LocalCache getCache(String key) {
        return RuleHolder.findByKey(key);
    }

}
