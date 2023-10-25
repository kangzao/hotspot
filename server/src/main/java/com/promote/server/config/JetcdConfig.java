package com.promote.server.config;

import lombok.extern.slf4j.Slf4j;
import org.promote.hotspot.client.test.hotspot.common.etcd.JetcdClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author enping.jep
 * @date 2023/10/23 21:33
 **/
@Configuration
@Slf4j
public class JetcdConfig {
    @Value("${etcd.server}")
    private String etcdServer;

    @Bean
    public JetcdClient client() {
        return new JetcdClient(etcdServer);
    }
}


