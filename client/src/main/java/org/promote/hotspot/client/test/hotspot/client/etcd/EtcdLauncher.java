package org.promote.hotspot.client.test.hotspot.client.etcd;

import io.etcd.jetcd.KeyValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.promote.hotspot.client.test.hotspot.client.ClientContext;
import org.promote.hotspot.client.test.hotspot.common.config.ConfigConstant;
import org.promote.hotspot.client.test.hotspot.common.etcd.JetcdClient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author enping.jep
 * @date 2023/10/24 15:29
 **/
@Slf4j
public class EtcdLauncher {

    public void launch() {

        fetchServerInfo(); //定时拉取服务端信息
    }


    /**
     * 定时拉取服务端信息
     */
    private void fetchServerInfo() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //开启拉取etcd的worker信息，如果拉取失败，则定时继续拉取
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info(getClass() + ":trying to connect to etcd and fetch worker info");
            fetch();
        }, 0, 30, TimeUnit.SECONDS);
    }

    private void fetch() {

        JetcdClient jetcdClient = EtcdConfigFactory.getJetcdClient();
        //获取所有server的地址
        List<KeyValue> keyValues = jetcdClient.getPrefix(ConfigConstant.serversPath + ClientContext.APP_NAME);

        if (CollectionUtils.isEmpty(keyValues)) {
            keyValues = jetcdClient.getPrefix(ConfigConstant.serversPath + "default");
        }

        if (CollectionUtils.isEmpty(keyValues)) {
            log.warn(getClass() + "server ip is null!");
        }

        List<String> addresses = new ArrayList<>();

        if (keyValues != null) {
            for (KeyValue keyValue : keyValues) {
                //value里放的是ip地址
                String ipPort = keyValue.getValue().toString(StandardCharsets.UTF_8);
                log.info(ipPort);
                addresses.add(ipPort);
            }
        }
    }
}
