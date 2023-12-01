package org.promote.hotspot.client.filter;

import org.promote.hotspot.client.ClientContext;
import org.promote.hotspot.client.cache.CacheFactory;
import org.promote.hotspot.client.cache.LocalCache;
import org.promote.hotspot.client.model.ValueModel;
import org.promote.hotspot.client.collector.HotCollectorFactory;
import org.promote.hotspot.client.model.KeyHotModel;
import org.promote.hotspot.common.model.HotKeyModel;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author enping.jep
 * @date 2023/11/28 21:10
 **/
public class HotSpotFilter {

    public static boolean isHotKey(String key) {
        try {
            if (!inRule(key)) {
                return false;
            }
            boolean isHot = isHot(key);
            //不是热key直接采集
            if (!isHot) {
                HotCollectorFactory.getHotKeyCollector().collect(buildHotKeyModel(key));
            } else {
                ValueModel valueModel = getValueSimple(key);
                //如果是热key，判断是否过期时间小于2秒，小于2秒的话也采集
                if (isNearExpire(valueModel)) {
                    HotCollectorFactory.getHotKeyCollector().collect(buildHotKeyModel(key));
                }
            }

            //统计计数
            HotCollectorFactory.getHotCountColletor().collect(new KeyHotModel(key, isHot));
            return isHot;
        } catch (Exception e) {
            return false;
        }

    }

    static boolean isHot(String key) {
        return getValueSimple(key) != null;
    }

    /**
     * 仅获取value，如果不存在也不上报热key
     */
    static ValueModel getValueSimple(String key) {
        Object object = getCache(key).get(key);
        if (object == null) {
            return null;
        }
        return (ValueModel) object;
    }

    /**
     * 判断这个key是否在被探测的规则范围内
     */
    private static boolean inRule(String key) {
        return CacheFactory.getCache(key) != null;
    }

    private static LocalCache getCache(String key) {
        return CacheFactory.getNonNullCache(key);
    }

    private static HotKeyModel buildHotKeyModel(String key) {
        HotKeyModel hotKeyModel = new HotKeyModel();
        hotKeyModel.setAppName(ClientContext.APP_NAME);
        LongAdder cnt = new LongAdder();
        cnt.add(1);
        hotKeyModel.setCount(cnt);
        hotKeyModel.setRemove(false);
        hotKeyModel.setKey(key);
        return hotKeyModel;
    }

    /**
     * 是否临近过期
     */
    private static boolean isNearExpire(ValueModel valueModel) {
        //判断是否过期时间小于2秒，小于2秒的话也发送
        if (valueModel == null) {
            return true;
        }
        return valueModel.getCreateTime() + valueModel.getDuration() - System.currentTimeMillis() <= 2000;
    }
}
