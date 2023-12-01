package org.promote.hotspot.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.java.Log;
import org.promote.hotspot.client.server.ServerInfoHolder;
import org.promote.hotspot.common.coder.MsgDecoder;
import org.promote.hotspot.common.coder.MsgEncoder;
import org.promote.hotspot.common.tool.Constant;

import java.util.List;

/**
 * @author enping.jep
 * @date 2023/11/15 11:39
 **/
@Log
public class NettyClient {
    private static final NettyClient nettyClient = new NettyClient();

    private final Bootstrap bootstrap;

    public static NettyClient getInstance() {
        return nettyClient;
    }

    private NettyClient() {
        bootstrap = initBootstrap();
    }

    private Bootstrap initBootstrap() {
        //少线程
        EventLoopGroup group = new NioEventLoopGroup(2);

        Bootstrap bootstrap = new Bootstrap();
        NettyClientHandler nettyClientHandler = new NettyClientHandler();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ByteBuf delimiter = Unpooled.copiedBuffer(Constant.DELIMITER.getBytes());
                        ch.pipeline()
                                .addLast(new DelimiterBasedFrameDecoder(Constant.MAX_LENGTH, delimiter))//ChannelInboundHandler
                                .addLast(new MsgDecoder())//ChannelInboundHandler
                                .addLast(new MsgEncoder())
                                //10秒没消息时，就发心跳包过去
                                .addLast(new IdleStateHandler(0, 0, 30))
                                .addLast(nettyClientHandler);
                    }
                });
        return bootstrap;
    }

    public synchronized boolean connect(List<String> addresses) {
        boolean allSuccess = true;
        for (String address : addresses) {
            if (ServerInfoHolder.hasConnected(address)) {
                continue;
            }
            String[] ss = address.split(":");
            try {
                ChannelFuture channelFuture = bootstrap.connect(ss[0], Integer.parseInt(ss[1])).sync();
                Channel channel = channelFuture.channel();
                ServerInfoHolder.put(address, channel);
            } catch (Exception e) {
                log.info("----该worker连不上----" + address);
                ServerInfoHolder.put(address, null);
                allSuccess = false;
            }
        }

        return allSuccess;

        //这一步就阻塞了
//            channelFuture.channel().closeFuture().sync();
        //当server断开后才会走下面的
//            System.out.println("server is down");
    }
}
