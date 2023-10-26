package org.promote.hotspot.client.test.hotspot.common.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.promote.hotspot.client.test.hotspot.common.convert.LongAdderSerializer;
import org.promote.hotspot.client.test.hotspot.common.tool.IdGenerator;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author enping.jep
 * @date 2023/10/25 17:14
 **/

public class BaseModel {

    private String id = IdGenerator.generateId();
    /**
     * 创建的时间
     */
    private long createTime;
    /**
     * key的名字
     */
    private String key;
    /**
     * 该key出现的数量，如果一次一发那就是1，累积多次发那就是count
     * 使用 LongAdder 解决 多线程计数不准确的问题
     */
    @JSONField(serializeUsing = LongAdderSerializer.class)
    private LongAdder count;

    @Override
    public String toString() {
        return "BaseModel{" +
                "id='" + id + '\'' +
                ", createTime=" + createTime +
                ", key='" + key + '\'' +
                ", count=" + count +
                '}';
    }

    /**
     * 获取计数总数
     *
     * @return 总数
     */
    public long getCount() {
        return count.sum();
    }

    /**
     * 设置计数
     *
     * @param count 计数 LongAdder 对象
     */
    public void setCount(LongAdder count) {
        this.count = count;
    }

    /**
     * 计数自增指定数量
     *
     * @param count 指定数量
     */
    public void add(long count) {
        this.count.add(count);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
