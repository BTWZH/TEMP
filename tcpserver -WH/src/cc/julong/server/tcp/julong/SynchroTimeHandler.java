package cc.julong.server.tcp.julong;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cc.julong.log.Log;
import cc.julong.server.state.DataInfo;
import cc.julong.server.tcp.AbstractHandler;
import cc.julong.server.util.MsgUtils;

public class SynchroTimeHandler extends AbstractHandler {
	public SynchroTimeHandler(DataInfo data) {
		super(data);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {

		in.skipBytes(JLHandler.HEADER_LENGTH);
		in = in.order(ByteOrder.LITTLE_ENDIAN);

		final int size = (int) in.readUnsignedInt() - JLHandler.HEADER_LENGTH
				- JLHandler.MSG_INFO_LENGTH;

		final DataInfo d = this.getData();

		Log.Info("TcpServer", "Synchro decoder");

		ctx.pipeline().addLast(new ByteToMessageDecoder() {
			@Override
			protected void decode(ChannelHandlerContext ctx, ByteBuf in,
					List<Object> out) throws Exception {

				if (in.readableBytes() < size) {
					return;
				}

				byte[] cmd = new byte[2];
				in.getBytes(JLHandler.VERSION_LENGTH - 1, cmd);

				// d.setBusiType(cmd);

				byte[] machine = MsgUtils.getBytes(in, JLHandler.CMD_LENGTH
						+ JLHandler.VERSION_LENGTH, 28);

				// 机器号
				String tmpmach = new String(machine, "iso-8859-1").replaceAll(
						"[^A-Za-z_0-9/]*", "");

				in.clear();

				String serverTime = new SimpleDateFormat("yyyyMMddHHmmss")
						.format(new Date());

				byte[] bytServerTime = serverTime.getBytes();

				int ng = 0xF0;
				ByteBuf b = MsgUtils.getMsg2SendSynchro(machine, ng,
						bytServerTime);

				MsgUtils.sendMssageBytes(ctx, JLHandler.HEADER, b);
				ctx.pipeline().addLast(JLFactory.getDefault(d));
				ctx.pipeline().remove(this);
			}
		});

		ctx.pipeline().remove(this);
	}
}
