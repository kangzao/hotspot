package org.promote.hotspot.client.hotspot.client.cache;

import com.github.benmanes.caffeine.cache.Cache;

/**
 * caffine缓存对localCache的实现
 *
 * @author enping.jep
 * @date 2023/10/25 21:56
 **/
public class CaffeineCache implements LocalCache {

    private Cache<String, Object> cache;

    public CaffeineCache(int duration) {
        this.cache = CaffeineBuilder.cache(duration);
    }

    @Override
    public Object get(String key) {
        return get(key, null);
    }

    @Override
    public Object get(String key, Object defaultValue) {
        Object o = cache.getIfPresent(key);
        if (o == null) {
            return defaultValue;
        }
        return o;
    }

    @Override
    public void delete(String key) {
        cache.invalidate(key);
    }

    @Override
    public void set(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public void set(String key, Object value, long expire) {
        set(key, value);
    }

    @Override
    public void removeAll() {
        cache.invalidateAll();
    }
}
