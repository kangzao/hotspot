package org.promote.hotspot.client.core;

import org.promote.hotspot.common.model.HotKeyModel;

/**
 * @author enping.jep
 * @date 2023/11/09 20:56
 **/
public class DefaultKeyHandler {
    private IKeyPusher iKeyPusher = new NettyKeyPusher();

    private IKeyCollector<HotKeyModel, HotKeyModel> iKeyCollector = new TurnKeyCollector();

    private IKeyCollector<KeyHotModel, KeyCountModel> iKeyCounter = new TurnCountCollector();


    public IKeyPusher keyPusher() {
        return iKeyPusher;
    }

    public IKeyCollector<HotKeyModel, HotKeyModel> keyCollector() {
        return iKeyCollector;
    }

    public IKeyCollector<KeyHotModel, KeyCountModel> keyCounter() {
        return iKeyCounter;
    }
}
