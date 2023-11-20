package org.promote.hotspot.client.core.server;

import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * @author enping.jep
 * @date 2023/11/15 14:22
 **/
public class ServerInfoChangeSubscriber {

    /**
     * 监听worker信息变动
     */
    @Subscribe
    public void connectAll(ServerInfoChangeEvent event) {
        List<String> addresses = event.getAddresses();
        if (addresses == null) {
            addresses = new ArrayList<>();
        }
        ServerInfoHolder.mergeAndConnectNew(addresses);
    }

}
