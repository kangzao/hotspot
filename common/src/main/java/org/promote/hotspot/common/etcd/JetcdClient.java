package org.promote.hotspot.common.etcd;


import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.options.PutOption;
import lombok.extern.slf4j.Slf4j;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 操作etcd
 *
 * @author enping.jep
 * @date 2023/10/19 16:29
 **/

@Slf4j
public class JetcdClient {
    private static Client client;

    private String endPoints;

    public JetcdClient(String endPoints) {
        this.endPoints = endPoints;
    }

    private Client getClient() {
        if (null == client) {
            client = Client.builder().endpoints(endPoints.split(",")).build();
        }
        return client;
    }

    /**
     * 租约形式存储
     *
     * @param key   键
     * @param value 值
     * @param ttl   租期
     */
    public void putWithLease(String key, String value, long ttl) {
        Lease leaseClient = getClient().getLeaseClient();
        leaseClient.grant(ttl).thenAccept(result -> {
            System.out.println(result);
            long leaseId = result.getID();
            log.info("[{}]申请租约成功，租约ID [{}]", key, Long.toHexString(leaseId));
            // 准备好put操作的client
            KV kvClient = getClient().getKVClient();
            PutOption putOption = PutOption.newBuilder().withLeaseId(leaseId).build();
            kvClient.put(bytesOf(key), bytesOf(value), putOption);
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


}
