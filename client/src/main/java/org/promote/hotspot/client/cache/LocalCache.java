package org.promote.hotspot.client.cache;

/**
 * @author enping.jep
 * @date 2023/10/25 21:46
 **/
public interface LocalCache {
    Object get(String key);

    Object get(String key, Object defaultValue);

    void delete(String key);

    void set(String key, Object value);

    void set(String key, Object value, long expire);

    void removeAll();
}
