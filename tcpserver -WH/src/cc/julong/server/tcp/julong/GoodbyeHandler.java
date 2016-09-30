package cc.julong.server.tcp.julong;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteOrder;
import java.util.Date;
import java.util.List;

import cc.julong.log.Log;
import cc.julong.server.state.DataInfo;
import cc.julong.server.tcp.AbstractHandler;

public class GoodbyeHandler extends AbstractHandler {

  public GoodbyeHandler(DataInfo data) {
    super(data);
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
      throws Exception {

    // 40 40 4A 4C 2C 00 00 00 0D 00 A2 00 4C 00 56 00 56 00 30 00 30 00 30
    // 00 30 00 31 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00

    in.skipBytes(JLHandler.HEADER_LENGTH);
    in = in.order(ByteOrder.LITTLE_ENDIAN);
    final int size = (int) in.readUnsignedInt() - JLHandler.HEADER_LENGTH
        - JLHandler.MSG_INFO_LENGTH;
    final DataInfo d = this.getData();

    Log.Info("goodbay decoder");

    ctx.pipeline().addLast(new ByteToMessageDecoder() {
      @Override
      protected void decode(ChannelHandlerContext ctx, ByteBuf in,
          List<Object> out) throws Exception {
        if (in.readableBytes() < size) {
          return;
        }
        d.setEndDate(new Date());
        in.clear();
        System.out.println("close");
        ctx.close();
      }
    });
    ctx.pipeline().remove(this);
  }

}
