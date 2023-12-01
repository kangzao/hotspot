package org.promote.hotspot.client.collector;

import java.util.List;

/**
 * @author enping.jep
 * @date 2023/11/09 20:55
 **/
public interface HotCollector<T, V> {

    /**
     * 锁定后的返回值
     */
    List<V> lockAndGetResult();

    /**
     * 采集数据
     */
    void collect(T t);


}
