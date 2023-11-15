package org.promote.hotspot.client.core;

import org.promote.hotspot.common.model.HotKeyModel;

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
public class PushSchedulerLauncher {
    public static void startPusher(Long period) {
        if (period == null || period <= 0) {
            period = 500L;
        }
        @SuppressWarnings("PMD.ThreadPoolCreationRule")
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("hotkey-pusher-service-executor", true));
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            IKeyCollector<HotKeyModel, HotKeyModel> collectHK = KeyHandlerFactory.getCollector();
            List<HotKeyModel> hotKeyModels = collectHK.lockAndGetResult();
            if (CollectionUtil.isNotEmpty(hotKeyModels)) {
                KeyHandlerFactory.getPusher().send(Context.APP_NAME, hotKeyModels);
                collectHK.finishOnce();
            }

        }, 0, period, TimeUnit.MILLISECONDS);
    }
}
