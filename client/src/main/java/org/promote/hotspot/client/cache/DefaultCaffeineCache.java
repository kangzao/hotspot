package org.promote.hotspot.client.cache;

/**
 * @author enping.jep
 * @date 2023/10/25 21:50
 **/
public class DefaultCaffeineCache extends CaffeineCache {
    public DefaultCaffeineCache(int duration) {
        super(duration);
    }

    public DefaultCaffeineCache() {
        this(60);
    }
}
