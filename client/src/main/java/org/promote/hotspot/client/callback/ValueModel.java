package org.promote.hotspot.client.callback;

import lombok.Getter;
import lombok.Setter;
import org.promote.hotspot.client.rule.RuleHolder;
import org.promote.hotspot.client.test.hotspot.common.tool.Constant;

/**
 * @author enping.jep
 * @date 2023/10/26 17:41
 **/
public class ValueModel {

    /**
     * 该热key创建时间
     */
    @Getter
    @Setter
    private long createTime = System.currentTimeMillis();
    /**
     * 本地缓存时间，单位毫秒
     */
    @Getter
    @Setter
    private int duration;
    /**
     * 用户实际存放的value
     */
    @Getter
    @Setter
    private Object value;

    public static ValueModel defaultValue(String key) {
        ValueModel valueModel = new ValueModel();
        int duration = RuleHolder.duration(key);
        if (duration <= 0) {
            //不符合任何规则
            return null;
        }
        //转毫秒
        valueModel.setDuration(duration * 1000);
        valueModel.setValue(Constant.MAGIC_NUMBER);
        return valueModel;
    }
}
