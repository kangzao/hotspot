package org.promote.hotspot.client.collector;

import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.promote.hotspot.common.model.HotKeyModel;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 轮流提供读写、暂存key的操作。
 * 上报时譬如采用定时器，每隔0.5秒调度一次push方法。在上报过程中，
 * 不应阻塞写入操作。所以计划采用2个HashMap加一个atomicLong，如奇数时写入map0，为1写入map1，上传后会清空该map。
 *
 * @author enping.jep
 * @date 2023/11/09 21:02
 **/
@Log
public class HotKeyCollector implements HotCollector<HotKeyModel, HotKeyModel> {
    private ConcurrentHashMap<String, HotKeyModel> map0 = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, HotKeyModel> map1 = new ConcurrentHashMap<>();

    private final AtomicLong atomicLong = new AtomicLong(0);

    @Override
    public List<HotKeyModel> lockAndGetResult() {
        //自增后，对应的map就会停止被写入，等待被读取
        atomicLong.addAndGet(1);
        //在读map的时候，不会阻塞写map，两个map同时提供轮流提供读写能力
        List<HotKeyModel> list;
        if (atomicLong.get() % 2 == 0) {
            list = get(map1);
            map1.clear();
        } else {
            list = get(map0);
            map0.clear();
        }
        return list;
    }

    private List<HotKeyModel> get(ConcurrentHashMap<String, HotKeyModel> map) {
        return Lists.newArrayList(map.values());

    }

    @Override
    public void collect(HotKeyModel hotKeyModel) {
        String key = hotKeyModel.getKey();
        if (StringUtils.isEmpty(key)) {
            log.warning("采集热点数据时,key为空");
            return;
        }
        if (atomicLong.get() % 2 == 0) {
            //不存在时返回null并将key-value放入，已有相同key时，返回该key对应的value，并且不覆盖
            HotKeyModel model = map0.putIfAbsent(key, hotKeyModel);
            if (model != null) {
                model.add(hotKeyModel.getCount());
            }
        } else {
            HotKeyModel model = map1.putIfAbsent(key, hotKeyModel);
            if (model != null) {
                model.add(hotKeyModel.getCount());
            }
        }
        log.info(hotKeyModel.toString());
    }

}
