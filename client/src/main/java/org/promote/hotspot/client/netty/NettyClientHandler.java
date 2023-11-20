package org.promote.hotspot.client.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.java.Log;
import org.apache.commons.collections.CollectionUtils;
import org.promote.hotspot.client.ClientContext;
import org.promote.hotspot.client.callback.ReceiveNewKeyEvent;
import org.promote.hotspot.client.eventbus.EventBusCenter;
import org.promote.hotspot.common.model.HotKeyModel;
import org.promote.hotspot.common.model.HotKeyMsg;
import org.promote.hotspot.common.model.MessageType;

import java.util.Collections;

/**
 * @author enping.jep
 * @date 2023/11/15 11:42
 **/
@Log
@ChannelHandler.Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<HotKeyMsg> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;

            if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                //向服务端发送消息
                ctx.writeAndFlush(new HotKeyMsg(MessageType.PING, ClientContext.APP_NAME));
            }
        }

        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("channelActive:" + ctx.name());
        ctx.writeAndFlush(new HotKeyMsg(MessageType.APP_NAME, ClientContext.APP_NAME));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        //断线了，可能只是client和server断了，但都和etcd没断。也可能是client自己断网了，也可能是server断了
        //发布断线事件。后续10秒后进行重连，根据etcd里的worker信息来决定是否重连，如果etcd里没了，就不重连。如果etcd里有，就重连
        notifyWorkerChange(ctx.channel());
    }

    private void notifyWorkerChange(Channel channel) {
        EventBusCenter.getInstance().post(new ChannelInactiveEvent(channel));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HotKeyMsg msg) {
        if (MessageType.PONG == msg.getMessageType()) {
            log.info("heart beat");
            return;
        }
        if (MessageType.RESPONSE_NEW_KEY == msg.getMessageType()) {
            log.info("receive new key : " + msg);
            if (CollectionUtils.isEmpty(msg.getHotKeyModels())) {
                return;
            }
            for (HotKeyModel model : msg.getHotKeyModels()) {
                EventBusCenter.getInstance().post(new ReceiveNewKeyEvent(model));
            }
        }

    }

}