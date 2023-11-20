package com.promote.hotspot.server.netty.filter;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.java.Log;
import org.promote.hotspot.common.model.HotKeyMsg;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 热点数据处理
 *
 * @author enping.jep
 * @date 2023/11/16 11:22
 **/
@Component
@Order(3)
@Log
public class HotSpotFilter implements INettyMsgFilter {
    @Override
    public boolean chain(HotKeyMsg message, ChannelHandlerContext ctx) {
        return false;
    }
}
