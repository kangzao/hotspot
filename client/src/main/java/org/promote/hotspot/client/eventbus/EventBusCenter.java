package org.promote.hotspot.client.eventbus;

import com.google.common.eventbus.EventBus;

/**
 * 事件总线
 * EventBus 是 Google Guava 提供的消息发布-订阅类库，是设计模式中的观察者模式（生产/消费者编程模型）
 * 的优雅实现，消息通知负责人通过 EventBus 去注册/注销观察者，最后由消息通知负责人给观察者发布消息。
 * 对于事件监听和发布订阅模式，EventBus 是一个非常优雅和简单解决方案，我们不用创建复杂的类和接口层次结构。
 *
 * @author enping.jep
 * @date 2023/10/25 19:01
 **/
@SuppressWarnings("UnstableApiUsage")
public class EventBusCenter {
    private static final EventBus eventBus = new EventBus();

    private EventBusCenter() {

    }

    public static EventBus getInstance() {
        return eventBus;
    }

    /**
     * 注册事件监听器
     *
     * @param obj
     */
    public static void register(Object obj) {
        eventBus.register(obj);
    }

    public static void unregister(Object obj) {
        eventBus.unregister(obj);
    }

    /**
     * 向订阅者发送消息
     *
     * @param obj
     */
    public static void post(Object obj) {
        eventBus.post(obj);
    }
}
