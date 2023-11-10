package org.promote.hotspot.client.core;

import java.util.List;

/**
 * @author enping.jep
 * @date 2023/11/09 20:55
 **/
public interface IKeyCollector<T, V> {

    /**
     * 锁定后的返回值
     */
    List<V> lockAndGetResult();

    /**
     * 输入的参数
     */
    void collect(T t);

    void finishOnce();
}
