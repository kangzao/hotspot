package com.promote.hotspot.server.launcher;

import com.promote.hotspot.server.netty.client.IClientChangeListener;
import com.promote.hotspot.server.netty.filter.INettyMsgFilter;
import com.promote.hotspot.server.tool.AsyncPool;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

import com.promote.hotspot.server.netty.NodeServer;

/**
 * @author enping.jep
 * @date 2023/11/15 15:03
 **/
@Log
@Component
public class NodesServerLauncher {

    @Value("${netty.port}")
    private int port;

    @Resource
    private IClientChangeListener iClientChangeListener;
    @Resource
    private List<INettyMsgFilter> messageFilters;

    @PostConstruct
    public void start() {
        AsyncPool.asyncDo(() -> {
            log.info("netty server is starting");
            NodeServer nodeServer = new NodeServer();
            nodeServer.setClientChangeListener(iClientChangeListener);
            nodeServer.setMessageFilters(messageFilters);
            try {
                nodeServer.startNettyServer(port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
