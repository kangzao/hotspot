package org.promote.hotspot.client.pusher;

import io.netty.channel.Channel;
import lombok.extern.java.Log;
import org.promote.hotspot.client.ClientContext;
import org.promote.hotspot.client.server.ServerInfoHolder;
import org.promote.hotspot.common.model.HotKeyModel;
import org.promote.hotspot.common.model.HotKeyMsg;
import org.promote.hotspot.common.model.KeyCountModel;
import org.promote.hotspot.common.model.MessageType;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * netty推送
 *
 * @author enping.jep
 * @date 2023/11/29 14:02
 **/
@Log
public class NettyHotPusher implements HotPusher {
    @Override
    public void sendHotKey(String appName, List<HotKeyModel> list) {
        long now = System.currentTimeMillis();
        Map<Channel, List<HotKeyModel>> map = new HashMap<>();
        for (HotKeyModel model : list) {
            model.setCreateTime(now);
            Channel channel = ServerInfoHolder.chooseChannel(model.getKey());
            if (channel == null) {
                continue;
            }
            List<HotKeyModel> newList = map.computeIfAbsent(channel, k -> new ArrayList<>());
            newList.add(model);
        }

        for (Channel channel : map.keySet()) {
            List<HotKeyModel> batch = map.get(channel);
            HotKeyMsg hotKeyMsg = new HotKeyMsg(MessageType.REQUEST_NEW_KEY, ClientContext.APP_NAME);
            hotKeyMsg.setHotKeyModels(batch);
            try {
                channel.writeAndFlush(hotKeyMsg).sync();
            } catch (InterruptedException e) {
                InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
                log.warning("flush error :" + inetSocketAddress.getAddress().getHostAddress());
            }
        }
    }

    @Override
    public void sendHotCount(String appName, List<KeyCountModel> list) {
        long now = System.currentTimeMillis();
        Map<Channel, List<KeyCountModel>> map = new HashMap<>();
        for (KeyCountModel model : list) {
            model.setCreateTime(now);
            Channel channel = ServerInfoHolder.chooseChannel(model.getRuleKey());
            if (channel == null) {
                continue;
            }
            List<KeyCountModel> newList = map.computeIfAbsent(channel, k -> new ArrayList<>());
            newList.add(model);
        }

        for (Channel channel : map.keySet()) {
            List<KeyCountModel> batch = map.get(channel);
            HotKeyMsg hotKeyMsg = new HotKeyMsg(MessageType.REQUEST_HIT_COUNT, ClientContext.APP_NAME);
            hotKeyMsg.setKeyCountModels(batch);
            try {
                channel.writeAndFlush(hotKeyMsg).sync();
            } catch (InterruptedException e) {
                InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
                log.warning("flush error :" + inetSocketAddress.getAddress().getHostAddress());
            }
        }
    }
}
