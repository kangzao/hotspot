package org.promote.hotspot.client.collector;

import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.promote.hotspot.client.model.KeyHotModel;
import org.promote.hotspot.client.rule.RuleHolder;
import org.promote.hotspot.common.model.KeyCountModel;
import org.promote.hotspot.common.tool.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * 热点数量统计
 * 数量收集器,这里面包含两个map，这里面key是相应的规则,HitCount里面是这个规则的总访问次数和热后访问次数
 *
 * @author enping.jep
 * @date 2023/11/09 21:04
 **/
@Log
public class HotCountCollector implements HotCollector<KeyHotModel, KeyCountModel> {
    /**
     * 存储格式为：
     * pin_20231127120921-> 10
     * <p>
     * key是相应的规则,HitCount里面是这个规则的总访问次数和热后访问次数
     */
    private ConcurrentHashMap<String, HitCount> HIT_MAP_0 = new ConcurrentHashMap<>(512);
    private ConcurrentHashMap<String, HitCount> HIT_MAP_1 = new ConcurrentHashMap<>(512);

    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    private AtomicLong atomicLong = new AtomicLong(0);

    private static final int DATA_CONVERT_SWITCH_THRESHOLD = 5000;

    @Override
    public List<KeyCountModel> lockAndGetResult() {
        //自增后，对应的map就会停止被写入，等待被读取
        atomicLong.addAndGet(1);

        List<KeyCountModel> list;
        if (atomicLong.get() % 2 == 0) {
            list = get(HIT_MAP_1);
            HIT_MAP_1.clear();
        } else {
            list = get(HIT_MAP_0);
            HIT_MAP_0.clear();
        }
        return list;
    }

    /**
     * 每10秒上报一次最近10秒的数据，并且删掉相应的key
     */
    private List<KeyCountModel> get(ConcurrentHashMap<String, HitCount> map) {
        //根据待转换并上报的统计数据的数据量选择是否启用并行参数转换
        if (map.size() > DATA_CONVERT_SWITCH_THRESHOLD) {
            return parallelConvert(map);
        } else {
            return syncConvert(map);
        }
    }

    /**
     * 在数据量足够大的情况下 并行转换可以拥有比串行for循环更好的性能
     *
     * @param map 统计数据
     * @return 待上报数据
     */
    private List<KeyCountModel> parallelConvert(ConcurrentHashMap<String, HitCount> map) {
        return map.entrySet().parallelStream().map(entry -> {
            String key = entry.getKey();
            HitCount hitCount = entry.getValue();
            KeyCountModel keyCountModel = new KeyCountModel();
            keyCountModel.setTotalHitCount((int) hitCount.totalHitCount.sum());
            keyCountModel.setRuleKey(key);
            keyCountModel.setHotHitCount((int) hitCount.hotHitCount.sum());
            return keyCountModel;
        }).collect(Collectors.toList());
    }

    /**
     * 在数据量不大的情况下,使用同步for循环进行数据转换性能也不错
     *
     * @param map 统计数据
     * @return 待上报数据
     */
    private List<KeyCountModel> syncConvert(ConcurrentHashMap<String, HitCount> map) {
        List<KeyCountModel> list = new ArrayList<>(map.size());
        for (Map.Entry<String, HitCount> entry : map.entrySet()) {
            String key = entry.getKey();
            HitCount hitCount = entry.getValue();
            KeyCountModel keyCountModel = new KeyCountModel();
            keyCountModel.setTotalHitCount((int) hitCount.totalHitCount.sum());
            keyCountModel.setRuleKey(key);
            keyCountModel.setHotHitCount((int) hitCount.hotHitCount.sum());
            list.add(keyCountModel);
        }
        return list;
    }

    @Override
    public void collect(KeyHotModel keyHotModel) {
        if (atomicLong.get() % 2 == 0) {
            put(keyHotModel.getKey(), keyHotModel.isHot(), HIT_MAP_0);
        } else {
            put(keyHotModel.getKey(), keyHotModel.isHot(), HIT_MAP_1);
        }
        log.info(keyHotModel.toString());
    }


    public void put(String key, boolean isHot, ConcurrentHashMap<String, HitCount> map) {
        //如key是pin_的前缀，则存储pin_
        String rule = RuleHolder.rule(key);
        //不在规则内的不处理
        if (StringUtils.isEmpty(rule)) {
            return;
        }
        String nowTime = nowTime();

        //rule + 分隔符 + 2020-10-23 21:11:22
        String mapKey = rule + Constant.COUNT_DELIMITER + nowTime;
        //该方法线程安全
        HitCount hitCount = map.computeIfAbsent(mapKey, v -> new HitCount());
        if (isHot) {
            hitCount.hotHitCount.increment();
        }
        hitCount.totalHitCount.increment();
    }

    private String nowTime() {
        Date nowTime = new Date(System.currentTimeMillis());
        SimpleDateFormat sdFormatter = new SimpleDateFormat(FORMAT);
        return sdFormatter.format(nowTime);
    }

    private class HitCount {
        private LongAdder hotHitCount = new LongAdder();
        private LongAdder totalHitCount = new LongAdder();
    }
}
