package org.promote.hotspot.client.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.promote.hotspot.client.ClientContext;

import java.util.concurrent.TimeUnit;

/**
 * @author enping.jep
 * @date 2023/10/25 21:57
 **/
public class CaffeineBuilder {

    public static Cache<String, Object> cache(int duration) {
        return cache(128, ClientContext.CAFFEINE_SIZE, duration);
    }

    public static Cache<String, Object> cache() {
        return cache(128, ClientContext.CAFFEINE_SIZE, 60);
    }

    /**
     * 构建所有来的要缓存的key getCache
     */
    public static Cache<String, Object> cache(int minSize, int maxSize, int expireSeconds) {
        return Caffeine.newBuilder()
                .initialCapacity(minSize)//初始大小
                .maximumSize(maxSize)//最大数量
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)//过期时间
                .build();
    }
}
