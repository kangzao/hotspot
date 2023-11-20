package org.promote.hotspot.client.netty;

import io.netty.channel.Channel;

/**
 * @author enping.jep
 * @date 2023/11/15 12:49
 **/
public class ChannelInactiveEvent {
    private Channel channel;

    public ChannelInactiveEvent(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
