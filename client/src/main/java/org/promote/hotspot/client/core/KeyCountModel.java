package org.promote.hotspot.client.core;

import lombok.Data;

/**
 * @author enping.jep
 * @date 2023/11/09 20:57
 **/
@Data
public class KeyCountModel {

    /**
     * 对应的规则名
     */
    private String ruleKey;
    /**
     * 总访问次数
     */
    private int totalHitCount;
    /**
     * 热后访问次数
     */
    private int hotHitCount;
    /**
     * 发送时的时间
     */
    private long createTime;

    @Override
    public String toString() {
        return "KeyCountModel{" +
                "ruleKey='" + ruleKey + '\'' +
                ", totalHitCount=" + totalHitCount +
                ", hotHitCount=" + hotHitCount +
                ", createTime=" + createTime +
                '}';
    }
}
