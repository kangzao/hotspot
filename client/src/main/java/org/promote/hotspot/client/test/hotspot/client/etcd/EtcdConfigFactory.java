package org.promote.hotspot.client.test.hotspot.client.etcd;

import org.promote.hotspot.client.test.hotspot.common.etcd.JetcdClient;

/**
 * @author enping.jep
 * @date 2023/10/24 15:40
 **/
public class EtcdConfigFactory {
    private static JetcdClient jetcdClient;

    private EtcdConfigFactory() {
    }

    public static JetcdClient getJetcdClient() {
        return jetcdClient;
    }

    public static void buildConfigCenter(String etcdServer) {
        jetcdClient = new JetcdClient(etcdServer);
    }
}
