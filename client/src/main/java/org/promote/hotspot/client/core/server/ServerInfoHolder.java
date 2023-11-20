package org.promote.hotspot.client.core.server;

import io.netty.channel.Channel;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.promote.hotspot.client.netty.NettyClient;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author enping.jep
 * @date 2023/11/15 11:37
 **/
@Log
public class ServerInfoHolder {
    /**
     * 保存server的ip地址和Channel的映射关系，这是有序的。每次client发送消息时，都会根据该map的size进行hash
     * 如key-1就发送到serverHolder的第1个Channel去，key-2就发到第2个Channel去
     */
    private static final List<Server> SERVER_HOLDER = new CopyOnWriteArrayList<>();

    private ServerInfoHolder() {
    }

    public static List<Server> getservers() {
        return SERVER_HOLDER;
    }

    /**
     * 判断某个server是否已经连接过了
     */
    public static boolean hasConnected(String address) {
        for (Server server : SERVER_HOLDER) {
            if (address.equals(server.address)) {
                return channelIsOk(server.channel);
            }
        }
        return false;
    }

    /**
     * 获取server是存在，但自己没连上的address集合，供重连
     */
    public static List<String> getNonConnectedServers() {
        List<String> list = new ArrayList<>();
        for (Server server : SERVER_HOLDER) {
            //如果没连上，或者连上连接异常的
            if (!channelIsOk(server.channel)) {
                list.add(server.address);
            }
        }
        return list;
    }

    private static boolean channelIsOk(Channel channel) {
        return channel != null && channel.isActive();
    }

    public static Channel chooseChannel(String key) {
        int size = SERVER_HOLDER.size();
        if (StringUtils.isEmpty(key) || size == 0) {
            return null;
        }
        int index = Math.abs(key.hashCode() % size);

        return SERVER_HOLDER.get(index).channel;
    }

    /**
     * 监听到server信息变化后
     * 将新的server信息和当前的进行合并，并且连接新的address
     * address例子：10.12.139.152:11111
     */
    public static void mergeAndConnectNew(List<String> allAddresses) {
        removeNoneUsed(allAddresses);

        //去连接那些在etcd里有，但是list里没有的
        List<String> needConnectServers = newServers(allAddresses);
        if (needConnectServers.size() == 0) {
            return;
        }

        log.info("new servers : " + needConnectServers);

        //再连接，连上后，alue就有值了
        NettyClient.getInstance().connect(needConnectServers);

        Collections.sort(SERVER_HOLDER);
    }

    /**
     * 处理某个server的channel断线事件<p>
     * 可能会导致重复删除
     */
    public static void dealChannelInactive(String address) {
        SERVER_HOLDER.removeIf(server -> address.equals(server.address));
    }

    /**
     * 增加一个新的server
     */
    public synchronized static void put(String address, Channel channel) {
        Iterator<Server> it = SERVER_HOLDER.iterator();
        boolean exist = false;
        while (it.hasNext()) {
            Server server = it.next();
            if (address.equals(server.address)) {
                server.channel = channel;
                exist = true;
                break;
            }
        }
        if (!exist) {
            Server server = new Server();
            server.address = address;
            server.channel = channel;
            SERVER_HOLDER.add(server);
        }

    }

    /**
     * 根据传过来的所有的server地址，返回当前尚未连接的新的server地址集合，用以创建新连接
     */
    private static List<String> newServers(List<String> allAddresses) {
        Set<String> set = new HashSet<>(SERVER_HOLDER.size());
        for (Server server : SERVER_HOLDER) {
            set.add(server.address);
        }

        List<String> list = new ArrayList<>();
        for (String s : allAddresses) {
            if (!set.contains(s)) {
                list.add(s);
            }
        }

        return list;
    }

    /**
     * 移除那些在最新的server地址集里没有的那些
     */
    private static void removeNoneUsed(List<String> allAddresses) {
        for (Server server : SERVER_HOLDER) {
            boolean exist = false;
            //判断现在的server里是否存在，如果当前的不存在，则删掉
            String nowAddress = server.address;
            for (String address : allAddresses) {
                if (address.equals(nowAddress)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                log.info("server remove : " + nowAddress);
                //如果最新地址集里已经没了，就把他关闭掉
                if (server.channel != null) {
                    server.channel.close();
                }
                SERVER_HOLDER.remove(server);
            }
        }
    }

//    public static void main(String[] args) {
//        List<String> list = new CopyOnWriteArrayList<>();
//
//        list.add("1");
//        list.add("4");
//        list.add("2");
//        list.add("3");
//
//        List<String> temp = new ArrayList<>();
//        temp.add("4");
//
//       for (String server : list) {
//            boolean exist = false;
//            for (String address : temp) {
//                if (address.equals(server)) {
//                    exist = true;
//                    break;
//                }
//            }
//            if (!exist) {
//                list.remove(server);
//            }
//        }
//
//        System.out.println(list);
//    }


    private static class Server implements Comparable<Server> {
        private String address;
        private Channel channel;


        @Override
        public int compareTo(Server o) {
            //按address排序
            return this.address.compareTo(o.address);
        }

        @Override
        public String toString() {
            return "Server{" +
                    "address='" + address + '\'' +
                    ", channel=" + channel +
                    '}';
        }
    }
}
