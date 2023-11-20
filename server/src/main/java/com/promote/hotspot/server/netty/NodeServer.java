package com.promote.hotspot.server.netty;

import com.promote.hotspot.server.netty.client.IClientChangeListener;
import com.promote.hotspot.server.netty.filter.INettyMsgFilter;
import com.promote.hotspot.server.tool.CpuNum;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.java.Log;
import org.promote.hotspot.common.coder.MsgDecoder;
import org.promote.hotspot.common.coder.MsgEncoder;
import org.promote.hotspot.common.tool.Constant;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author enping.jep
 * @date 2023/11/15 15:15
 **/
@Log
public class NodeServer {
    private IClientChangeListener clientChangeListener;
    private List<INettyMsgFilter> messageFilters;

    public void startNettyServer(int port) throws Exception {
        //boss单线程
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(CpuNum.workerCount());
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    /*
                     * ServerBootstrap#handler可以不配置，#childHandler必须配置，否则抛出异常IllegalStateException: childHandler not set
                     * ServerBootstrap#handler的msg是SocketChannel
                     * ServerBootstrap#handler添加的handler实例链，只在客户端连接创建的时候调用（ServerChannel负责创建子Channel），在创建完成之后，不会再调用
                     **/
                    .handler(new LoggingHandler(LogLevel.INFO))
                    /*
                        保持长连接
                        ChannelOption.SO_BACKLOG对应的是tcp/ip协议, listen函数 中的 backlog 参数，用来初始化服务端可连接队列。
                        backlog 指定了内核为此套接口排队的最大连接个数；
                        对于给定的监听套接口，内核要维护两个队列: 未连接队列和已连接队列
                        backlog 的值即为未连接队列和已连接队列的和。
                        服务器TCP内核 内维护了两个队列，称为A(未连接队列)和B(已连接队列)。如果A+B的长度大于Backlog时，新的连接就会被TCP内核拒绝掉。
                        所以，如果backlog过小，就可能出现Accept的速度跟不上，A，B队列满了，就会导致客户端无法建立连接。
                        需要注意的是，backlog对程序的连接数没影响，但是影响的是还没有被Accept取出的连接。
                    **/
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    /*
                     * ChannelOption.SO_KEEPALIVE参数对应于套接字选项中的SO_KEEPALIVE，该参数用于设置TCP连接，当设置该选项以后，
                     * 连接会测试链接的状态，这个选项用于可能长时间没有数据交流的连接。
                     * 当设置该选项以后，如果在两小时(cat /proc/sys/net/ipv4/tcp_keepalive_time)内没有数据的通信时，TCP会自动发送一个活动探测数据报文。
                     */
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 处理网络io事件，如记录日志、对消息编解码等
                    // ServerBootstrap#childHandler的msg，是客户端传输的数据
                    .childHandler(new ChildChannelHandler());
            //绑定端口，同步等待成功  bind 方法返回的 future 用于等待底层网络组件启动完成
            ChannelFuture future = bootstrap.bind(port).sync();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                bossGroup.shutdownGracefully(1000, 3000, TimeUnit.MILLISECONDS);
                workerGroup.shutdownGracefully(1000, 3000, TimeUnit.MILLISECONDS);
            }));
            /*
             * future.channel是获取通道，这里的通道是NioServerSocketChannel,建立连接，创建客户端channel
             * 如果端口连接不上就关闭监听的端口  future.channel().closeFuture()
             * sync表示异步执行
             * 等待服务器监听端口关闭  closeFuture用于等待网络组件关闭完成
             */
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            log.warning("netty stop");
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * handler类
     * ChannelInitializer是一个特殊的ChannelInboundHandler, 当一个Channel被注册到它的EventLoop时, 提供一个简单的方式用于初始化。
     * 提供了一种简单的方式来初始化一个 Channel
     * 它的实现类通常在
     * Bootstrap.handler(ChannelHandler)
     * ServerBootstrap.handler(ChannelHandler)
     * ServerBootstrap.childHandler(ChannelHandler)
     * 的上下文中使用, 用于构建channel的ChannelPipeline
     */
    private class ChildChannelHandler extends ChannelInitializer<Channel> {

        //设置childHandler执行所有的连接请求
        @Override
        protected void initChannel(Channel ch) {
            NodeServerHandler serverHandler = new NodeServerHandler();
            serverHandler.setClientEventListener(clientChangeListener);
            serverHandler.addMessageFilters(messageFilters);
            // 创建一个 ByteBuf 保存特定字节串  ByteBuf是Netty的数据容器，所有网络通信中字节流的传输都是通过ByteBuf完成
            // 新建一个全新的buffer，那么可以使用Unpooled.copiedBuffer
            ByteBuf delimiter = Unpooled.copiedBuffer(Constant.DELIMITER.getBytes());
            ch.pipeline()
                    //定义TCP多个包之间的分隔符，为了更好的做拆包  DelimiterBasedFrameDecoder是一个分隔符解码器，
                    //第一个参数是缓冲区大小，如果长度超标，并且没有找到分隔符则抛异常
                    //如果长度小于MAX_LENGTH，且没有找到分隔符，则缓存收到的消息，直到接收到分隔符，或者超出1024抛出异常
                    .addLast(new DelimiterBasedFrameDecoder(Constant.MAX_LENGTH, delimiter))//ChannelInboundHandler
                    .addLast(new MsgDecoder())//ChannelInboundHandler
                    .addLast(new MsgEncoder())//ChannelOutboundHandler
                    .addLast(serverHandler);//ChannelInboundHandler
        }
    }

    public void setClientChangeListener(IClientChangeListener clientChangeListener) {
        this.clientChangeListener = clientChangeListener;
    }

    public void setMessageFilters(List<INettyMsgFilter> messageFilters) {
        this.messageFilters = messageFilters;
    }
}
