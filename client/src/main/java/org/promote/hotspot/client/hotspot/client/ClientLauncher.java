package org.promote.hotspot.client.hotspot.client;

import org.promote.hotspot.client.hotspot.client.etcd.EtcdConfigFactory;
import org.promote.hotspot.client.hotspot.client.etcd.EtcdLauncher;

/**
 * client的启动类
 *
 * @author enping.jep
 * @date 2023/10/24 16:15
 **/
public class ClientLauncher {
    private String etcdServer;

    /**
     * 推送key的间隔(毫秒)，推送越快，探测的越密集，会越快探测出来，但对client资源消耗相应增大
     */
    private Long pushPeriod;
    /**
     * caffeine的最大容量，默认给5万
     */
    private int caffeineSize;

    /**
     * client启动各类功能
     */
    public void startPipeline() {
        //设置本地缓存大小
        EtcdConfigFactory.buildConfigCenter(etcdServer);//设置etcd地址，初始化JetcdClient，先连接上etcd，然后再监听
        EtcdLauncher launcher = new EtcdLauncher();
        launcher.launch();//开启与etcd相关的监听
    }


    public ClientLauncher(String appName) {
        if (appName == null) {
            throw new NullPointerException("APP_NAME cannot be null!");
        }
        ClientContext.APP_NAME = appName;
    }

    public static class Builder {

        private String appName;
        private String etcdServer;
        private Long pushPeriod;
        private int caffeineSize = 200000;

        public Builder() {
        }

        public Builder setAppName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder setCaffeineSize(int caffeineSize) {
            if (caffeineSize < 128) {
                caffeineSize = 128;
            }
            this.caffeineSize = caffeineSize;
            return this;
        }

        public Builder setEtcdServer(String etcdServer) {
            this.etcdServer = etcdServer;
            return this;
        }

        public Builder setPushPeriod(Long pushPeriod) {
            this.pushPeriod = pushPeriod;
            return this;
        }

        public ClientLauncher build() {
            ClientLauncher ClientLauncher = new ClientLauncher(appName);
            ClientLauncher.etcdServer = etcdServer;
            ClientLauncher.pushPeriod = pushPeriod;
            ClientLauncher.caffeineSize = caffeineSize;

            return ClientLauncher;
        }

    }
}
