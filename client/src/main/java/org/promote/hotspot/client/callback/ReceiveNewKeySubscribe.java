package org.promote.hotspot.client.callback;

import com.google.common.eventbus.Subscribe;
import org.promote.hotspot.common.model.HotKeyModel;

/**
 * 监听有新key推送事件
 *
 * @author enping.jep
 * @date 2023/10/26 17:35
 **/
public class ReceiveNewKeySubscribe {

    private ReceiveNewKeyListener receiveNewKeyListener = new DefaultNewKeyListener();

    @Subscribe
    public void newKeyComing(ReceiveNewKeyEvent event) {
        HotKeyModel hotKeyModel = event.getModel();
        if (hotKeyModel == null) {
            return;
        }
        //收到新key推送
        if (receiveNewKeyListener != null) {
            receiveNewKeyListener.newKey(hotKeyModel);
        }
    }
}
