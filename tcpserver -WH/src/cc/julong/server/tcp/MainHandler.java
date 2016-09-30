package cc.julong.server.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import cc.julong.server.state.DataInfo;

public class MainHandler extends AbstractHandler {

    public final int MSG_HEADER_LENGTH = 4;

    public final int BOC_MSG_LENGTH = 8; // 中行协议报文长度

    public final int BOC_MSG_TYPE = 4; // 中行协议报文类型

    public MainHandler(DataInfo data) {
        super(data);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
            List<Object> out) throws Exception {
        if (in.readableBytes() < MSG_HEADER_LENGTH) {
            return;
        }
        byte[] bytes = new byte[MSG_HEADER_LENGTH];
        in.getBytes(0, bytes);

        // 中行报文类型
        byte[] bytesMsgType = new byte[BOC_MSG_TYPE];

        in.getBytes(BOC_MSG_LENGTH, bytesMsgType);

        TcpFactory factory = Protocols.getProtocol(bytes,bytesMsgType);
        ByteToMessageDecoder decoder = factory.getMain(this.getData());

        ctx.pipeline().addLast(decoder);
        ctx.pipeline().remove(this);

    }

}
