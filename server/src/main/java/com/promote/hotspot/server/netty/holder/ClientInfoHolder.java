package com.promote.hotspot.server.netty.holder;

import com.promote.hotspot.server.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 保存所有与server连接的客户端信息
 *
 * @author enping.jep
 * @date 2023/11/16 21:49
 **/
public class ClientInfoHolder {
    public static List<AppInfo> apps = new ArrayList<>();
}
