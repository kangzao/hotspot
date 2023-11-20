package com.promote.hotspot.server.netty.client;

import io.netty.channel.ChannelHandlerContext;

/**
 * 对客户端的管理(新来、断线)
 *
 * @author enping.jep
 * @date 2023/11/15 15:07
 **/
public interface IClientChangeListener {
    /**
     * 发现新连接
     */
    void newClient(String appName, String channelId, ChannelHandlerContext ctx);

    /**
     * 客户端掉线
     */
    void loseClient(ChannelHandlerContext ctx);
}
