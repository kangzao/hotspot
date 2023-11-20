package com.promote.hotspot.server.launcher;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.promote.hotspot.common.config.ConfigConstant;
import org.promote.hotspot.common.etcd.JetcdClient;
import org.promote.hotspot.common.tool.IpUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 服务端启动类
 *
 * @author enping.jep
 * @date 2023/10/19 15:05
 **/

@Slf4j
@Component
public class EtcdLauncher {

    @Resource
    private JetcdClient jetcdClient;

    @Value("${etcd.serverPath}")
    private String serverPath;

    @Value("${local.address}")
    private String localAddress;

    @Value("${netty.port}")
    private int port;

    @PostConstruct
    public void heartBeat() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //            log.info("开始心跳检测");
        //和etcd保持心跳连接
        scheduledExecutorService.scheduleAtFixedRate(this::uploadServerInfo, 0, 5, TimeUnit.SECONDS);
    }


    /**
     * 每隔8s，重新进行一次上报操作
     */
    public void uploadServerInfo() {
        jetcdClient.putWithLease(buildKey(), buildValue(), 8);
    }

    /**
     * @return server端在etcd中的目录信息
     */
    private String buildKey() {
        String hostName = IpUtils.getHostName();
        return ConfigConstant.serversPath + serverPath + "/" + hostName;
    }

    /**
     * @return 本机地址和端口
     */
    private String buildValue() {
        String ip;

        if (StringUtils.isNotEmpty(localAddress)) {
            ip = localAddress;
        } else {
            ip = IpUtils.getIp();
        }
        return ip + ":" + port;
    }

}
