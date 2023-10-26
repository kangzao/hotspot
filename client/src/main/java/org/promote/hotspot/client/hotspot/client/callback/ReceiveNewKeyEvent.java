package org.promote.hotspot.client.hotspot.client.callback;

import lombok.Getter;
import lombok.Setter;
import org.promote.hotspot.client.test.hotspot.common.model.HotKeyModel;

/**
 * @author enping.jep
 * @date 2023/10/26 17:31
 **/
public class ReceiveNewKeyEvent {

    @Getter
    @Setter
    private HotKeyModel model;

    public ReceiveNewKeyEvent(HotKeyModel model) {
        this.model = model;
    }
}
