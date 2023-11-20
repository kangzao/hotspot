package com.promote.hotspot.server.netty.client;

import com.promote.hotspot.server.model.AppInfo;
import com.promote.hotspot.server.netty.holder.ClientInfoHolder;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.java.Log;
import org.promote.hotspot.common.tool.NettyIpUtil;

/**
 * @author enping.jep
 * @date 2023/11/15 15:08
 **/
@Log
public class ClientChangeListener implements IClientChangeListener {

    @Override
    public synchronized void newClient(String appName, String channelId, ChannelHandlerContext ctx) {
        log.info("监听事件");
        boolean appExist = false;
        for (AppInfo appInfo : ClientInfoHolder.apps) {
            if (appName.equals(appInfo.getAppName())) {
                appExist = true;
                appInfo.add(ctx);
                break;
            }
        }
        if (!appExist) {
            AppInfo appInfo = new AppInfo(appName);
            ClientInfoHolder.apps.add(appInfo);
            appInfo.add(ctx);
        }
        log.info("new client join");
    }

    @Override
    public void loseClient(ChannelHandlerContext ctx) {
        for (AppInfo appInfo : ClientInfoHolder.apps) {
            appInfo.remove(ctx);
        }
        log.info("client removed " + NettyIpUtil.clientIp(ctx));
    }
}
