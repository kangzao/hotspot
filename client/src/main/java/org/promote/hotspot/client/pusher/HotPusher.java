package org.promote.hotspot.client.pusher;

import org.promote.hotspot.common.model.HotKeyModel;
import org.promote.hotspot.common.model.KeyCountModel;

import java.util.List;

/**
 * 热点数据推送接口
 *
 * @author enping.jep
 * @date 2023/11/29 14:00
 **/
public interface HotPusher {

    /**
     * 发送待测key
     */
    void sendHotKey(String appName, List<HotKeyModel> list);

    /**
     * 发送热key访问量
     */
    void sendHotCount(String appName, List<KeyCountModel> list);
}
