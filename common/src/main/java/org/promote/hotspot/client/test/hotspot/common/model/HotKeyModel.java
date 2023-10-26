package org.promote.hotspot.client.test.hotspot.common.model;

import org.promote.hotspot.client.hotspot.common.config.KeyType;

/**
 * @author enping.jep
 * @date 2023/10/25 17:28
 **/
public class HotKeyModel extends BaseModel {
    /**
     * 来自于哪个应用
     */
    private String appName;
    /**
     * key的类型（譬如是接口、热用户、redis的key等）
     */
    private KeyType keyType;
    /**
     * 是否是删除事件
     */
    private boolean remove;

    @Override
    public String toString() {
        return "appName:" + appName + "-key=" + getKey();
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }
}
