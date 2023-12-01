package org.promote.hotspot.client.model;

import lombok.Data;

/**
 * @author enping.jep
 * @date 2023/11/09 21:01
 **/
@Data
public class KeyHotModel {

    private String key;

    private boolean isHot;

    public KeyHotModel(String key, boolean isHot) {
        this.key = key;
        this.isHot = isHot;
    }
}
