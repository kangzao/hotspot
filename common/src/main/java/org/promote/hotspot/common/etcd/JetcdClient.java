package org.promote.hotspot.common.etcd;


import com.google.protobuf.ByteString;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static io.grpc.Context.key;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 操作etcd
 *
 * @author enping.jep
 * @date 2023/10/19 16:29
 **/

@Slf4j
public class JetcdClient {
    private String endPoints;

    private Object object;

    private final Lease leaseClient;

    private final KV kv;

    private final Watch watch;

    public JetcdClient(String endPoints) {
        Client client = Client.builder().endpoints(endPoints.split(",")).build();
        this.leaseClient = client.getLeaseClient();
        this.kv = client.getKVClient();
        this.watch = client.getWatchClient();
    }

    public String get(String key) {
        GetResponse getResponse = null;
        try {
            getResponse = kv.get(bytesOf(key)).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return getResponse.getCount() > 0 ?
                getResponse.getKvs().get(0).getValue().toString(UTF_8) :
                null;
    }

    /**
     * 租约形式存储
     *
     * @param key   键
     * @param value 值
     * @param ttl   租期
     */
    public void putWithLease(String key, String value, long ttl) {
        leaseClient.grant(ttl).thenAccept(result -> {
            long leaseId = result.getID();
            // log.info("[{}]申请租约成功，租约ID [{}]", key, Long.toHexString(leaseId));
            // 准备好put操作的client
            PutOption putOption = PutOption.newBuilder().withLeaseId(leaseId).build();
            kv.put(bytesOf(key), bytesOf(value), putOption);
        });
    }

    /**
     * 将字符串转为客户端所需的ByteSequence实例
     *
     * @param val
     * @return
     */
    public static ByteSequence bytesOf(String val) {
        return ByteSequence.from(val, UTF_8);
    }

    public List<KeyValue> getPrefix(String key) {
        GetOption getOption = GetOption.newBuilder().withPrefix(bytesOf(key)).build();
        GetResponse getResponse = null;
        try {
            getResponse = kv.get(bytesOf(key), getOption).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return getResponse.getKvs();
    }


    public Watch.Watcher watch(String key, Watch.Listener listener) {
        return watch.watch(bytesOf(key), listener);
    }

    public Watch.Watcher watchPrefix(String key, Watch.Listener listener) {
        WatchOption watchOption = WatchOption.newBuilder().withPrefix(bytesOf(key)).build();
        return watch.watch(bytesOf(key), watchOption, listener);
    }

//    public Watch.Watcher watch(String key) throws Exception {
//        Watch.Listener listener = Watch.listener(watchResponse -> {
//            log.info("收到[{}]的事件", key);
//            // 被调用时传入的是事件集合，这里遍历每个事件
//            watchResponse.getEvents().forEach(watchEvent -> {
//                // 操作类型
//                WatchEvent.EventType eventType = watchEvent.getEventType();
//                // 操作的键值对
//                KeyValue keyValue = watchEvent.getKeyValue();
//                log.info("type={}, key={}, value={}",
//                        eventType,
//                        keyValue.getKey().toString(UTF_8),
//                        keyValue.getValue().toString(UTF_8));
//            });
//        });
//
//        return watch.watch(bytesOf(key), listener);
//    }


}
