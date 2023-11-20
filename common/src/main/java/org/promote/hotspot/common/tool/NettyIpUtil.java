package org.promote.hotspot.common.tool;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * 从netty连接中读取ip地址
 *
 * @author enping.jep
 * @date 2023/11/16 11:31
 **/
public class NettyIpUtil {
    public static String clientIp(ChannelHandlerContext ctx) {
        try {
            InetSocketAddress inSocket = (InetSocketAddress) ctx.channel()
                    .remoteAddress();
            return inSocket.getAddress().getHostAddress();
        } catch (Exception e) {
            return "未知";
        }

    }
}
