package org.promote.hotspot.common.test.jetcd;

import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.DeleteOption;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.grpc.stub.CallStreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author enping.jep
 * @date 2023/10/20 17:12
 **/
@Slf4j
public class EtcdServiceImpl implements EtcdService {
    private Client client;

    private String endpoints;

    private Object lock = new Object();

    public EtcdServiceImpl(String endpoints) {
        super();
        this.endpoints = endpoints;
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

    /**
     * 新建key-value客户端实例
     *
     * @return
     */
    private KV getKVClient() {
        return getClient().getKVClient();
    }

    private Client getClient() {
        if (null == client) {
            synchronized (lock) {
                if (null == client) {
                    client = Client.builder().endpoints(endpoints.split(",")).build();
                }
            }
        }
        return client;
    }


    @Override
    public void close() {
        client.close();
        client = null;
    }

    @Override
    public void putWithLease(String key, String value) throws Exception {
        AtomicInteger a;
        Lease leaseClient = getClient().getLeaseClient();

        leaseClient.grant(60)
                .thenAccept(result -> {

                    // 租约ID
                    long leaseId = result.getID();

                    log.info("[{}]申请租约成功，租约ID [{}]", key, Long.toHexString(leaseId));

                    // 准备好put操作的client
                    KV kvClient = getClient().getKVClient();

                    // put操作时的可选项，在这里指定租约ID
                    PutOption putOption = PutOption.newBuilder().withLeaseId(leaseId).build();

                    // put操作
                    kvClient.put(bytesOf(key), bytesOf(value), putOption)
                            .thenAccept(putResponse -> {
                                // put操作完成后，再设置无限续租的操作
                                leaseClient.keepAlive(leaseId, new CallStreamObserver<LeaseKeepAliveResponse>() {
                                    @Override
                                    public boolean isReady() {
                                        return false;
                                    }

                                    @Override
                                    public void setOnReadyHandler(Runnable onReadyHandler) {

                                    }

                                    @Override
                                    public void disableAutoInboundFlowControl() {

                                    }

                                    @Override
                                    public void request(int count) {
                                    }

                                    @Override
                                    public void setMessageCompression(boolean enable) {

                                    }

                                    /**
                                     * 每次续租操作完成后，该方法都会被调用
                                     * @param value
                                     */
                                    @Override
                                    public void onNext(LeaseKeepAliveResponse value) {
                                        log.info("[{}]续租完成，TTL[{}]", Long.toHexString(leaseId), value.getTTL());
                                    }

                                    @Override
                                    public void onError(Throwable t) {
                                        log.error("onError", t);
                                    }

                                    @Override
                                    public void onCompleted() {
                                        log.info("onCompleted");
                                    }
                                });
                            });
                });
    }

    @Override
    public Response.Header put(String key, String value) throws Exception {
        return getKVClient().put(bytesOf(key), bytesOf(value)).get().getHeader();
    }

    @Override
    public String getSingle(String key) throws Exception {
        GetResponse getResponse = getKVClient().get(bytesOf(key)).get();

        return getResponse.getCount() > 0 ?
                getResponse.getKvs().get(0).getValue().toString(UTF_8) :
                null;
    }

    @Override
    public GetResponse getRange(String key, GetOption getOption) throws Exception {
        return getKVClient().get(bytesOf(key), getOption).get();
    }

    @Override
    public long deleteSingle(String key) throws Exception {
        return getKVClient().delete(bytesOf(key)).get().getDeleted();
    }

    @Override
    public long deleteRange(String key, DeleteOption deleteOption) throws Exception {
        return getKVClient().delete(bytesOf(key), deleteOption).get().getDeleted();
    }


}
