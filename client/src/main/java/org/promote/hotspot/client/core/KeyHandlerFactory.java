package org.promote.hotspot.client.core;

import org.promote.hotspot.common.model.HotKeyModel;

/**
 * @author enping.jep
 * @date 2023/11/09 20:56
 **/
public class KeyHandlerFactory {
    private static final DefaultKeyHandler iKeyHandler = new DefaultKeyHandler();

    private KeyHandlerFactory() {
    }

    public static IKeyPusher getPusher() {
        return iKeyHandler.keyPusher();
    }

    public static IKeyCollector<HotKeyModel, HotKeyModel> getCollector() {
        return iKeyHandler.keyCollector();
    }

    public static IKeyCollector<KeyHotModel, KeyCountModel> getCounter() {
        return iKeyHandler.keyCounter();
    }
}
