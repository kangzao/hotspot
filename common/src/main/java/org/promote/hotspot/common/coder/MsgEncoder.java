package org.promote.hotspot.common.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.promote.hotspot.common.model.HotKeyMsg;
import org.promote.hotspot.common.tool.Constant;
import org.promote.hotspot.common.tool.ProtostuffUtils;

/**
 * @author enping.jep
 * @date 2023/11/15 12:49
 **/
public class MsgEncoder extends MessageToByteEncoder {

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) {
        if (in instanceof HotKeyMsg) {
            byte[] bytes = ProtostuffUtils.serialize(in);
            byte[] delimiter = Constant.DELIMITER.getBytes();

            byte[] total = new byte[bytes.length + delimiter.length];
            System.arraycopy(bytes, 0, total, 0, bytes.length);
            System.arraycopy(delimiter, 0, total, bytes.length, delimiter.length);

            out.writeBytes(total);
        }
    }

}
