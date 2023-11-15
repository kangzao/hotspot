package org.promote.hotspot.client.core;

import org.promote.hotspot.common.model.HotKeyModel;

import java.util.List;

/**
 * @author enping.jep
 * @date 2023/11/09 20:57
 **/
public interface IKeyPusher {
    /**
     * 发送待测key
     */
    void send(String appName, List<HotKeyModel> list);

    /**
     * 发送热key访问量
     */
    void sendCount(String appName, List<KeyCountModel> list);
}
