package org.promote.hotspot.client.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.java.Log;
import org.promote.hotspot.client.netty.NettyClient;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author enping.jep
 * @date 2023/11/16 14:50
 **/
@Log
public class ServerRetryConnector {
    /**
     * 定时去重连没连上的server
     */
    public static void retryConnectServers() {
        @SuppressWarnings("PMD.ThreadPoolCreationRule")
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setDaemon(true).setNameFormat("server-retry-connector-service-executor").build());
        //开启拉取etcd的worker信息，如果拉取失败，则定时继续拉取
        scheduledExecutorService.scheduleAtFixedRate(ServerRetryConnector::reConnectServers, 30, 30, TimeUnit.SECONDS);
    }

    private static void reConnectServers() {
        List<String> nonList = ServerInfoHolder.getNonConnectedServers();
        if (nonList.size() == 0) {
            return;
        }
        log.info("trying to reConnect to these workers :" + nonList);
        NettyClient.getInstance().connect(nonList);
    }
}
