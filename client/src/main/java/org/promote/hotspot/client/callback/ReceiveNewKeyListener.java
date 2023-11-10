package org.promote.hotspot.client.callback;

import org.promote.hotspot.client.test.hotspot.common.model.HotKeyModel;

/**
 * 客户端监听到有newKey事件
 * @author enping.jep
 * @date 2023/10/26 17:36
 **/
public interface ReceiveNewKeyListener {
    void newKey(HotKeyModel hotKeyModel);
}
