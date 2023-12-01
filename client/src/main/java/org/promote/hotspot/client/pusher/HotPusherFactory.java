package org.promote.hotspot.client.pusher;

/**
 * @author enping.jep
 * @date 2023/11/29 14:28
 **/
public class HotPusherFactory {
    private static final HotPusher hotPusher = new NettyHotPusher();

    public static HotPusher getHotPusher() {
        return hotPusher;
    }
}
