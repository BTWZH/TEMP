package cc.julong.server.tcp.julong;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteOrder;
import java.util.Date;
import java.util.List;

import cc.julong.log.Log;
import cc.julong.server.state.DataInfo;
import cc.julong.server.tcp.AbstractHandler;
import cc.julong.server.util.MsgUtils;

public class HelloHandler extends AbstractHandler {

	public HelloHandler(DataInfo data) {
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

		Log.Info("hello decoder");

		ctx.pipeline().addLast(new ByteToMessageDecoder() {
			@Override
			protected void decode(ChannelHandlerContext ctx, ByteBuf in,
					List<Object> out) throws Exception {
				if (in.readableBytes() < size) {
					return;
				}

				// ����������
				byte[] machine = MsgUtils.getBytes(in, JLHandler.CMD_LENGTH
						+ JLHandler.VERSION_LENGTH, 28);

				// �����
				String bankNo = MsgUtils.getMsg(in, 0, 8).replaceAll(
						"[^A-Za-z_0-9/]*", "");

				// TODO
				byte[] bbb = new byte[2];
				in.getBytes(40, bbb);

				byte[] inbyte = new byte[46];
				in.getBytes(0, inbyte);

				// // TODO
				// Log.Info("TcpServer", "���ܵ���in : " + new String(inbyte,
				// "iso-8859-1"));
				// // TODO
				// Log.Info("TcpServer", "MachType��in�е����� λ�� : 40");
				// // TODO
				// Log.Info("TcpServer", "[byte[]]MachType :" + bbb[0] + "," +
				// bbb[1]);
				// // TODO
				// Log.Info("TcpServer", "[String]MachType :"
				// + new String(bbb, "iso-8859-1"));

				// �豸����
				String machineType = MsgUtils.getMsg(in, 0, 2).replaceAll(
						"[^A-Za-z_0-9/]*", "");

				// ������
				String machineNo = new String(machine, "iso-8859-1")
						.replaceAll("[^A-Za-z_0-9/]*", "");

				d.setMachineinfo(machineNo);
				d.setBeginDate(new Date());
				d.setBankNo(bankNo);
				d.setMachineType(machineType);
				in.clear();

				ByteBuf b = Unpooled.buffer();
				b.writeBytes(JLHandler.VERSION);
				b.writeBytes(new byte[] { 0x00, (byte) 0x00 });
				b.writeBytes(new byte[] { (byte) 0xF0, 0x00 });
				b.writeBytes(machine);
				b.writeBytes(new byte[] { 0x00, (byte) 0x00 });
				b.writeBytes(new byte[] { 0x00, (byte) 0x00 });

				MsgUtils.sendMssageBytes(ctx, JLHandler.HEADER, b);
				ctx.pipeline().addLast(JLFactory.getDefault(d));
				ctx.pipeline().remove(this);
			}
		});
		ctx.pipeline().remove(this);
	}

}
