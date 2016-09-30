package cc.julong.server.tcp.julong;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteOrder;
import java.util.Date;
import java.util.List;

import cc.julong.common.RealTAction;
import cc.julong.log.Log;
import cc.julong.server.state.DataInfo;
import cc.julong.server.tcp.AbstractHandler;
import cc.julong.server.util.MsgUtils;

public class SayCanCelHandler extends AbstractHandler {

	public SayCanCelHandler(DataInfo data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		in.skipBytes(JLHandler.HEADER_LENGTH);
		in = in.order(ByteOrder.LITTLE_ENDIAN);

		final int size = (int) in.readUnsignedInt() - JLHandler.HEADER_LENGTH
				- JLHandler.MSG_INFO_LENGTH;

		final DataInfo d = this.getData();

		Log.Info("TcpServer", "say cancel");

		ctx.pipeline().addLast(new ByteToMessageDecoder() {
			@Override
			protected void decode(ChannelHandlerContext ctx, ByteBuf in,
					List<Object> out) throws Exception {

				if (in.readableBytes() < size) {
					return;
				}

				byte[] cmd = new byte[2];

				try {

					in.getBytes(JLHandler.VERSION_LENGTH, cmd);

					// 调用业务取消接口
					RealTAction.CancelReal();

					in.clear();

					int ng = 0xF0;
					ByteBuf b = MsgUtils.getMsg2Send(cmd, ng, 0);

					MsgUtils.sendMssageBytes(ctx, JLHandler.HEADER, b);
					ctx.pipeline().addLast(JLFactory.getDefault(d));
					ctx.pipeline().remove(this);

				} catch (Exception e) {
					Log.Info("ERROR： 调用cancel接口时出错，信息： " + e.getMessage());

					in.clear();

					int ng = 0xE0;
					ByteBuf b = MsgUtils.getMsg2Send(cmd, ng, 0);

					MsgUtils.sendMssageBytes(ctx, JLHandler.HEADER, b);
					ctx.pipeline().addLast(JLFactory.getDefault(d));
					ctx.pipeline().remove(this);
				}
			}
		});

		ctx.pipeline().remove(this);
	}

}
