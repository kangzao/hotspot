package org.promote.hotspot.client.pusher;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.promote.hotspot.client.ClientContext;
import org.promote.hotspot.client.collector.HotCollector;
import org.promote.hotspot.client.collector.HotCollectorFactory;
import org.promote.hotspot.client.model.KeyHotModel;
import org.promote.hotspot.common.model.HotKeyModel;
import org.promote.hotspot.common.model.KeyCountModel;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时推送一批key到server
 *
 * @author enping.jep
 * @date 2023/11/09 20:52
 **/
public class PushScheduleLauncher {
    public static void startHotKeyPush(Long period) {
        if (period == null || period <= 0) {
            period = 500L;
        }
        @SuppressWarnings("PMD.ThreadPoolCreationRule")
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setDaemon(true).setNameFormat("hotkey-pusher-service-executor").build());
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            HotCollector<HotKeyModel, HotKeyModel> hotCollector = HotCollectorFactory.getHotKeyCollector();
            //这里相当于每0.5秒，通过netty来给worker来推送收集到的热key的信息，主要是一些热key的元数据信息(热key来源的app和key的类型和是否是删除事件，还有该热key的上报次数)
            //热key在每次上报的时候都会生成一个全局的唯一id，热key每次上报的创建时间是在netty发送的时候来生成，同一批次的热key时间是相同的
            List<HotKeyModel> hotKeyModels = hotCollector.lockAndGetResult();
            if (CollectionUtils.isNotEmpty(hotKeyModels)) {
                //积攒了半秒的key集合，按照hash分发到不同的server
                HotPusherFactory.getHotPusher().sendHotKey(ClientContext.APP_NAME, hotKeyModels);
            }
        }, 0, period, TimeUnit.MILLISECONDS);
    }


    public static void startHotCountPush(Integer period) {
        if (period == null || period <= 0) {
            period = 10;
        }
        @SuppressWarnings("PMD.ThreadPoolCreationRule")
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("hotcount-pusher-service-executor").build());
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            HotCollector<KeyHotModel, KeyCountModel> collectHK = HotCollectorFactory.getHotCountColletor();
            List<KeyCountModel> keyCountModels = collectHK.lockAndGetResult();
            if (CollectionUtils.isNotEmpty(keyCountModels)) {
                //积攒了10秒的数量，按照hash分发到不同的worker
                HotPusherFactory.getHotPusher().sendHotCount(ClientContext.APP_NAME, keyCountModels);
            }
        }, 0, period, TimeUnit.SECONDS);
    }
}
