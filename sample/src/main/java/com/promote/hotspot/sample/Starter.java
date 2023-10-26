package com.promote.hotspot.sample;

import org.promote.hotspot.client.hotspot.client.ClientLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 启动client端
 *
 * @author enping.jep
 * @date 2023/10/25 11:47
 **/
@Component
public class Starter {
    @Value("${etcd.server}")
    private String etcdServer;
    @Value("${spring.application.name}")
    private String appName;

    @PostConstruct
    public void init() {
        ClientLauncher.Builder builder = new ClientLauncher.Builder();//生成器模式
        //将appName和etcd地址注入到ClientLauncher中
        ClientLauncher clientLauncher = builder.setAppName(appName).setEtcdServer(etcdServer).build();
        clientLauncher.startPipeline();
    }
}