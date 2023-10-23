package com.promote.server.launcher;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author enping.jep
 * @date 2023/10/19 15:05
 **/
@Slf4j
@Component
public class EtcdLauncher {

    @PostConstruct
    public void heartBeat() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("开始心跳检测");
            //和etcd保持心跳连接

        }, 0, 5, TimeUnit.SECONDS);

    }

}
