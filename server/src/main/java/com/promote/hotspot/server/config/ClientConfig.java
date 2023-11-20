package com.promote.hotspot.server.config;

import com.promote.hotspot.server.netty.client.ClientChangeListener;
import com.promote.hotspot.server.netty.client.IClientChangeListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author enping.jep
 * @date 2023/11/15 17:33
 **/
@Configuration
public class ClientConfig {
    @Bean
    public IClientChangeListener clientChangeListener() {
        return new ClientChangeListener();
    }
}
