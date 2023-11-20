package org.promote.hotspot.common.model;

import java.util.List;

/**
 * @author enping.jep
 * @date 2023/11/15 11:43
 **/
public class HotKeyMsg {
    private int magicNumber;

    private String appName;

    private MessageType messageType;

    private String body;

    private List<HotKeyModel> hotKeyModels;

    private List<KeyCountModel> keyCountModels;

    public HotKeyMsg(MessageType messageType) {
        this(messageType, null);
    }

    public HotKeyMsg(MessageType messageType, String appName) {
        this.appName = appName;
        this.messageType = messageType;
    }

    public HotKeyMsg() {
    }

    @Override
    public String toString() {
        return "HotKeyMsg{" +
                "magicNumber=" + magicNumber +
                ", appName='" + appName + '\'' +
                ", messageType=" + messageType +
                ", body='" + body + '\'' +
                ", hotKeyModels=" + hotKeyModels +
                ", keyCountModels=" + keyCountModels +
                '}';
    }

    public List<HotKeyModel> getHotKeyModels() {
        return hotKeyModels;
    }

    public void setHotKeyModels(List<HotKeyModel> hotKeyModels) {
        this.hotKeyModels = hotKeyModels;
    }

    public List<KeyCountModel> getKeyCountModels() {
        return keyCountModels;
    }

    public void setKeyCountModels(List<KeyCountModel> keyCountModels) {
        this.keyCountModels = keyCountModels;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
