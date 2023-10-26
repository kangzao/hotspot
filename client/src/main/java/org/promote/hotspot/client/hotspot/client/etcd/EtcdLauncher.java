package org.promote.hotspot.client.hotspot.client.etcd;

import io.etcd.jetcd.KeyValue;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.promote.hotspot.client.hotspot.client.ClientContext;
import org.promote.hotspot.client.hotspot.client.callback.ReceiveNewKeyEvent;
import org.promote.hotspot.client.hotspot.client.eventbus.EventBusCenter;
import org.promote.hotspot.client.hotspot.client.rule.RuleChangeEvent;
import org.promote.hotspot.client.hotspot.common.config.ConfigConstant;
import org.promote.hotspot.client.test.hotspot.common.etcd.JetcdClient;
import org.promote.hotspot.client.test.hotspot.common.model.HotKeyModel;
import org.promote.hotspot.client.test.hotspot.common.tool.FastJsonUtils;
import org.promote.hotspot.common.rule.KeyRule;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * etcd启动类
 *
 * @author enping.jep
 * @date 2023/10/24 15:29
 **/
@Slf4j
public class EtcdLauncher {

    public void launch() {
        fetchServerInfo(); //定时拉取服务端信息
        fetchRule();//拉取规则信息
    }


    /**
     * 拉取规则信息
     */
    private void fetchRule() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //开启拉取etcd的server信息，如果拉取失败，则定时继续拉取
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info(getClass() + "：trying to connect to etcd and fetch rule info");
            boolean success = fetchRuleFromEtcd();
            if (success) {
                //从etcd里面获取该appName手动配置的热key
                fetchExistHotKey();
                //如果成功获取，则关闭线程池
                scheduledExecutorService.shutdown();
            }

        }, 0, 5, TimeUnit.SECONDS);
    }


    /**
     * 从etcd获取规则
     */
    private boolean fetchRuleFromEtcd() {
        JetcdClient jetcdClient = EtcdConfigFactory.getJetcdClient();
        try {
            List<KeyRule> ruleList = new ArrayList<>();
            //从etcd获取自己的rule
            String rules = jetcdClient.get(ConfigConstant.rulePath + ClientContext.APP_NAME);
            if (StringUtils.isEmpty(rules)) {
                log.warn(getClass() + "rule is empty");
                //会清空本地缓存队列
                notifyRuleChange(ruleList);
                return true;
            }
            ruleList = FastJsonUtils.toList(rules, KeyRule.class);
            notifyRuleChange(ruleList);
            return true;
        } catch (Exception e) {
            log.error(getClass() + ":fetch rule failure, please check the rule info in etcd");
            return false;
        }

    }

    private void notifyRuleChange(List<KeyRule> rules) {
        EventBusCenter.getInstance().post(new RuleChangeEvent(rules));//通知机制,向订阅者发送消息
    }

    /**
     * 定时拉取服务端信息
     */
    private void fetchServerInfo() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //开启拉取etcd的worker信息，如果拉取失败，则定时继续拉取
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info(getClass() + ":trying to connect to etcd and fetch worker info");
            fetchServer();
        }, 0, 30, TimeUnit.SECONDS);
    }

    private void fetchServer() {

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

    /**
     * 启动后先拉取已存在的热key（来自于手工添加的目录）
     */
    private void fetchExistHotKey() {
        log.info(getClass() + ":begin fetch exist hotKey from etcd ----");
        try {
            //获取所有热key
            List<KeyValue> handKeyValues = EtcdConfigFactory.getJetcdClient().getPrefix(ConfigConstant.hotKeyPath + ClientContext.APP_NAME);

            for (KeyValue keyValue : handKeyValues) {
                String key = keyValue.getKey().toString(StandardCharsets.UTF_8).replace(ConfigConstant.hotKeyPath + ClientContext.APP_NAME + "/", "");
                HotKeyModel model = new HotKeyModel();
                model.setRemove(false);
                model.setKey(key);
                EventBusCenter.getInstance().post(new ReceiveNewKeyEvent(model));
            }
        } catch (Exception ex) {
            //etcd连不上
            log.error(getClass() + ":etcd connected fail. Check the etcd address!!!");
        }

    }
}
