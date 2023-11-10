package org.promote.hotspot.client.rule;

import lombok.Getter;
import lombok.Setter;
import org.promote.hotspot.common.rule.KeyRule;

import java.util.List;

/**
 * @author enping.jep
 * @date 2023/10/25 19:13
 **/
public class RuleChangeEvent {

    @Getter
    @Setter
    private List<KeyRule> keyRules;

    public RuleChangeEvent(List<KeyRule> keyRules) {
        this.keyRules = keyRules;
    }


}
