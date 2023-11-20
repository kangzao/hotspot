package com.promote.hotspot.server.netty.filter;

import io.netty.channel.ChannelHandlerContext;
import org.promote.hotspot.common.model.HotKeyMsg;
import org.promote.hotspot.common.model.MessageType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 心跳处理
 * 当消息类型为PING，则给对应的client返回PONG
 *
 * @author enping.jep
 * @date 2023/11/16 11:19
 **/
@Component
@Order(1)
public class HeartBeatFilter implements INettyMsgFilter {
    @Override
    public boolean chain(HotKeyMsg message, ChannelHandlerContext ctx) {
        if (MessageType.PING == message.getMessageType()) {
            ctx.writeAndFlush(new HotKeyMsg(MessageType.PONG));
            return false;
        }
        return true;
    }
}
