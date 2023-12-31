package org.promote.hotspot.client.callback;

import lombok.extern.slf4j.Slf4j;
import org.promote.hotspot.client.cache.CacheFactory;
import org.promote.hotspot.client.model.ValueModel;
import org.promote.hotspot.common.model.HotKeyModel;

/**
 * 收到来自于server的新增key，或者etcd的新增和删除key事件
 *
 * @author enping.jep
 * @date 2023/10/26 17:37
 **/
@Slf4j
public class DefaultNewKeyListener implements ReceiveNewKeyListener {

    @Override
    public void newKey(HotKeyModel hotKeyModel) {
        long now = System.currentTimeMillis();
        //如果key到达时已经过去1秒了，记录一下。手工删除key时，没有CreateTime
        if (hotKeyModel.getCreateTime() != 0 && Math.abs(now - hotKeyModel.getCreateTime()) > 1000) {
            log.warn(getClass() + ":the key comes too late : " + hotKeyModel.getKey() + " now " +
                    +now + " keyCreateAt " + hotKeyModel.getCreateTime());
        }
        if (hotKeyModel.isRemove()) {
            //如果是删除事件，就直接删除
            deleteKey(hotKeyModel.getKey());
            return;
        }
        //已经是热key了，又推过来同样的热key，做个日志记录，并刷新一下
        if (HotKeyStore.isHot(hotKeyModel.getKey())) {
            log.warn(getClass() + ":receive repeat hot key ：" + hotKeyModel.getKey() + " at " + now);
        }
        addKey(hotKeyModel.getKey());
    }

    private void addKey(String key) {
        ValueModel valueModel = ValueModel.defaultValue(key);
        if (valueModel == null) {
            //不符合任何规则
            deleteKey(key);
            return;
        }
        //如果原来该key已经存在了，那么value就被重置，过期时间也会被重置。如果原来不存在，就新增的热key
        HotKeyStore.setValueDirectly(key, valueModel);
    }


    private void deleteKey(String key) {
        CacheFactory.getNonNullCache(key).delete(key);
    }
}
