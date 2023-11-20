package com.promote.hotspot.server.netty.filter;

import com.promote.hotspot.server.netty.client.IClientChangeListener;
import io.netty.channel.ChannelHandlerContext;
import org.promote.hotspot.common.model.HotKeyMsg;
import org.promote.hotspot.common.model.MessageType;
import org.promote.hotspot.common.tool.NettyIpUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author enping.jep
 * @date 2023/11/16 11:20
 **/
@Component
@Order(2)
public class AppNameFilter implements INettyMsgFilter {

    @Resource
    private IClientChangeListener clientEventListener;

    @Override
    public boolean chain(HotKeyMsg message, ChannelHandlerContext ctx) {
        if (MessageType.APP_NAME == message.getMessageType()) {
            String appName = message.getAppName();
            if (clientEventListener != null) {
                clientEventListener.newClient(appName, NettyIpUtil.clientIp(ctx), ctx);
            }
            return false;
        }

        return true;
    }
}
