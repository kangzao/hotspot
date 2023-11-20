package org.promote.hotspot.client.core.server;

import java.util.List;

/**
 * @author enping.jep
 * @date 2023/11/15 14:09
 **/
public class ServerInfoChangeEvent {
    private List<String> addresses;

    public ServerInfoChangeEvent(List<String> addresses) {
        this.addresses = addresses;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }
}
