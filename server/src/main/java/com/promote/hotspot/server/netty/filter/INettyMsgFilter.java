package com.promote.hotspot.server.netty.filter;

import io.netty.channel.ChannelHandlerContext;
import org.promote.hotspot.common.model.HotKeyMsg;

/**
 * 对netty来的消息，进行过滤处理
 * @author enping.jep
 * @date 2023/11/15 15:12
 **/
public interface INettyMsgFilter {
    boolean chain(HotKeyMsg message, ChannelHandlerContext ctx);
}
