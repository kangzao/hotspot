package org.promote.hotspot.common.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.promote.hotspot.common.model.HotKeyMsg;
import org.promote.hotspot.common.tool.ProtostuffUtils;

import java.util.List;

/**
 * ByteToMessageDecoder是一种ChannelInboundHandler，可以称为解码器，
 * 负责将byte字节流(ByteBuf)转换成一种Message，Message是应用可以自己定义的一种Java对象。
 *
 * @author enping.jep
 * @date 2023/11/15 11:49
 **/
public class MsgDecoder extends ByteToMessageDecoder {

    /**
     * @param channelHandlerContext the {@link ChannelHandlerContext} which this {@link ByteToMessageDecoder} belongs to
     * @param in                    the {@link ByteBuf} from which to read data 输入
     * @param list                  解码后的有效报文列表，我们需要将解码后的报文添加到这个List中。之所以使用一个List表示，是因为考虑到粘包问题，因此入参的in中可能包含多个有效报文。
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) {
        try {

            byte[] body = new byte[in.readableBytes()];  //传输正常
            in.readBytes(body);

            list.add(ProtostuffUtils.deserialize(body, HotKeyMsg.class));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
