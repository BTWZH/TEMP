package cc.julong.server.tcp.julong;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import cc.julong.server.state.DataInfo;
import cc.julong.server.tcp.AbstractHandler;

public class JLHandler extends AbstractHandler {

	public static final int HEADER_LENGTH = 4;

	public static final int MSG_INFO_LENGTH = 4;

	public static final int VERSION_LENGTH = 2;

	public static final int CMD_LENGTH = 2;

	public static final byte[] HEADER = new byte[] { 0x40, 0x40, 0x4A, 0x4C };

	public static final byte[] VERSION = new byte[] { 0x0D, 0x00 };

	/** fsn后缀名 */
	public static final String FileSuffix = ".tmp";

	/** tmp文件2分钟未操作变成fsn文件 */
	public static final long TMP2FSNTIME = 2L * 60 * 1000;

	public JLHandler(DataInfo data) {
		super(data);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if (in.readableBytes() < (HEADER_LENGTH + MSG_INFO_LENGTH
				+ VERSION_LENGTH + CMD_LENGTH)) {
			return;
		}
		byte[] versionBytes = new byte[VERSION_LENGTH];
		byte[] cmdBytes = new byte[CMD_LENGTH];
		in.getBytes(HEADER_LENGTH + MSG_INFO_LENGTH, versionBytes);
		in.getBytes(HEADER_LENGTH + MSG_INFO_LENGTH + VERSION_LENGTH, cmdBytes);
		int version = versionBytes[0];

		ctx.pipeline().addLast(
				JLFactory.createHandler(cmdBytes, this.getData(), version));
		ctx.pipeline().remove(this);

	}

	/*
	 * 64, 64, 74, 76, 54, 0, 0, 0, 13, 0, -95, 0, 73, 0, 65, 0, 83, 0, 48, 0,
	 * 48, 0, 48, 0, 48, 0, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 48, 48,
	 * 48, 53, 48, 48, 48, 53,
	 * 
	 * 
	 * 0, 0]
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

}
