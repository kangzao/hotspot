package com.promote.hotspot.server.netty;

import com.promote.hotspot.server.netty.client.IClientChangeListener;
import com.promote.hotspot.server.netty.filter.INettyMsgFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.java.Log;
import org.promote.hotspot.common.model.HotKeyMsg;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author enping.jep
 * @date 2023/11/15 15:24
 **/
@Log
public class NodeServerHandler extends SimpleChannelInboundHandler<HotKeyMsg> {

    /**
     * 客户端状态监听器
     */
    private IClientChangeListener clientEventListener;
    /**
     * 请自行维护Filter的添加顺序
     */
    private List<INettyMsgFilter> messageFilters = new ArrayList<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HotKeyMsg msg) throws Exception {
        if (msg == null) {
            return;
        }
        for (INettyMsgFilter messageFilter : messageFilters) {
            boolean doNext = messageFilter.chain(msg, ctx);
            if (!doNext) {
                return;
            }
        }
    }

    public void setClientEventListener(IClientChangeListener clientEventListener) {
        this.clientEventListener = clientEventListener;
    }

    public void addMessageFilter(INettyMsgFilter iNettyMsgFilter) {
        if (iNettyMsgFilter != null) {
            messageFilters.add(iNettyMsgFilter);
        }
    }

    public void addMessageFilters(List<INettyMsgFilter> iNettyMsgFilters) {
        if (!CollectionUtils.isEmpty(iNettyMsgFilters)) {
            messageFilters.addAll(iNettyMsgFilters);
        }
    }
}
