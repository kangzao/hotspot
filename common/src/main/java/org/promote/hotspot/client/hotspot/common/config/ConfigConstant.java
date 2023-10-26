package org.promote.hotspot.client.hotspot.common.config;

/**
 * @author enping.jep
 * @date 2023/10/23 20:35
 **/
public class ConfigConstant {

    /**
     * 所有的server信息
     */
    public static final String serversPath = "/hotspot/servers/";


    /**
     * 所有的客户端规则（譬如哪个app的哪些前缀的才参与计算）
     */
    public static final String rulePath = "/hotspot/rules/";


    /**
     * 每个app的热key放这里。格式如：hotspot/hotkeys/app1/userA
     */
    public static final String hotKeyPath = "/hotspot/hotkeys/";
}
