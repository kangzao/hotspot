package com.promote.hotspot.sample.controller;

import org.promote.hotspot.client.filter.HotSpotFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author enping.jep
 * @date 2023/11/28 21:06
 **/

@RestController
@RequestMapping
public class TestController {
    @RequestMapping("/hotKey")
    public Object hotKey(String key) {
        if (!StringUtils.isEmpty(key) && HotSpotFilter.isHotKey(key)) {
            return "isHot";
        } else {
            return "noHot";
        }
    }
}
