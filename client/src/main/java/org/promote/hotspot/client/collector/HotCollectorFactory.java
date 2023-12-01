package org.promote.hotspot.client.collector;

import org.promote.hotspot.client.model.KeyHotModel;
import org.promote.hotspot.common.model.HotKeyModel;
import org.promote.hotspot.common.model.KeyCountModel;

/**
 * 数据采集工厂
 *
 * @author enping.jep
 * @date 2023/11/29 11:21
 **/
public class HotCollectorFactory {

    /**
     * 热点key采集
     */
    private static final HotCollector<HotKeyModel, HotKeyModel> hotKeyCollector = new HotKeyCollector();

    /**
     * 数量统计
     */
    private static final HotCollector<KeyHotModel, KeyCountModel> hotCountColletor = new HotCountCollector();

    public static HotCollector<HotKeyModel, HotKeyModel> getHotKeyCollector() {
        return hotKeyCollector;
    }

    public static HotCollector<KeyHotModel, KeyCountModel> getHotCountColletor() {
        return hotCountColletor;
    }
}
