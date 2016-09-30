package cc.julong.server.tcp;

import cc.julong.log.Log;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class IdleTriggeredHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
      throws Exception {
    if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
      IdleStateEvent event = (IdleStateEvent) evt;
      if (event.state() == IdleState.READER_IDLE)
        Log.Info(ctx.channel().remoteAddress() + " read idle");
      else if (event.state() == IdleState.WRITER_IDLE)
        Log.Info(ctx.channel().remoteAddress() + " write idle");
      else if (event.state() == IdleState.ALL_IDLE) {
        Log.Info(ctx.channel().remoteAddress() + " all idle");
      }
    }
  }
}
