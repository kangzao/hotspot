package org.promote.hotspot.client.core;

import io.netty.channel.Channel;
import org.promote.hotspot.common.model.HotKeyModel;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将msg推送到netty的pusher
 *
 * @author enping.jep
 * @date 2023/11/09 20:59
 **/
public class NettyKeyPusher implements IKeyPusher {
    @Override
    public void send(String appName, List<HotKeyModel> list) {
        //积攒了半秒的key集合，按照hash分发到不同的worker
        long now = System.currentTimeMillis();

        Map<Channel, List<HotKeyModel>> map = new HashMap<>();
        for (HotKeyModel model : list) {
            model.setCreateTime(now);
            Channel channel = WorkerInfoHolder.chooseChannel(model.getKey());
            if (channel == null) {
                continue;
            }

            List<HotKeyModel> newList = map.computeIfAbsent(channel, k -> new ArrayList<>());
            newList.add(model);
        }

        for (Channel channel : map.keySet()) {
            try {
                List<HotKeyModel> batch = map.get(channel);
                HotKeyMsg hotKeyMsg = new HotKeyMsg(MessageType.REQUEST_NEW_KEY, Context.APP_NAME);
                hotKeyMsg.setHotKeyModels(batch);
                channel.writeAndFlush(hotKeyMsg).sync();
            } catch (Exception e) {
                try {
                    InetSocketAddress insocket = (InetSocketAddress) channel.remoteAddress();
                    JdLogger.error(getClass(), "flush error " + insocket.getAddress().getHostAddress());
                } catch (Exception ex) {
                    JdLogger.error(getClass(), "flush error");
                }

            }
        }
    }

    @Override
    public void sendCount(String appName, List<KeyCountModel> list) {
//积攒了10秒的数量，按照hash分发到不同的worker
        long now = System.currentTimeMillis();
        Map<Channel, List<KeyCountModel>> map = new HashMap<>();
        for (KeyCountModel model : list) {
            model.setCreateTime(now);
            Channel channel = WorkerInfoHolder.chooseChannel(model.getRuleKey());
            if (channel == null) {
                continue;
            }

            List<KeyCountModel> newList = map.computeIfAbsent(channel, k -> new ArrayList<>());
            newList.add(model);
        }

        for (Channel channel : map.keySet()) {
            try {
                List<KeyCountModel> batch = map.get(channel);
                HotKeyMsg hotKeyMsg = new HotKeyMsg(MessageType.REQUEST_HIT_COUNT, Context.APP_NAME);
                hotKeyMsg.setKeyCountModels(batch);
                channel.writeAndFlush(hotKeyMsg).sync();
            } catch (Exception e) {
                try {
                    InetSocketAddress insocket = (InetSocketAddress) channel.remoteAddress();
                    JdLogger.error(getClass(), "flush error " + insocket.getAddress().getHostAddress());
                } catch (Exception ex) {
                    JdLogger.error(getClass(), "flush error");
                }

            }
        }
    }
}
