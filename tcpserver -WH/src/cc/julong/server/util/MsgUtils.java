package cc.julong.server.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;

import cc.julong.server.tcp.julong.JLHandler;

public class MsgUtils {
	private static ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

	public static byte[] getBytes(ByteBuf bf, int index, int size) {
		if (bf.readableBytes() < size || bf.readableBytes() < index) {
			return null;
		}
		bf.skipBytes(index);
		byte[] bytes = new byte[size];
		bf.readBytes(bytes);
		return bytes;
	}

	/**
	 * 
	 * @param bf
	 * @param index
	 * @param size
	 * @return
	 */
	public static int getInt32(ByteBuf bf, int index, int size) {
		if (bf.readableBytes() < size || bf.readableBytes() < index) {
			return 0;
		}
		bf.skipBytes(index);
		byte[] bytes = new byte[size];
		bf.readBytes(bytes);
		int result = getValFromBytes(bytes, 0, bytes.length);
		return result;
	}

	public static int getValFromBytes(byte[] bytes, int offset, int length) {
		int val = 0;
		if (byteOrder == ByteOrder.BIG_ENDIAN) {
			for (int i = 0; i < length; i++) {
				val += (bytes[i + offset] & 0xFF) << i * 8;
			}
		} else {
			for (int i = length - 1; i >= 0; i--) {
				val += (bytes[i + offset] & 0xFF) << i * 8;
			}
		}
		return val;
	}

	/**
	 * ��ȡָ�����ȵı��Ĳ�ת��Ϊ�ַ���,���ƶ�ָ��
	 * 
	 * @param bf
	 * @param index
	 * @param size
	 * @return
	 */

	public static String getMsgNoSkip(ByteBuf bf, int index, int size) {
		if (bf.readableBytes() < size || bf.readableBytes() < index) {
			return null;
		}

		byte[] bytes = new byte[size];
		bf.getBytes(index, bytes);
		String msg = null;
		try {
			msg = new String(bytes, "iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return msg;
	}

	public static String getMsg(ByteBuf bf, int index, int size) {
		if (bf.readableBytes() < size || bf.readableBytes() < index) {
			return null;
		}

		bf.skipBytes(index);
		byte[] bytes = new byte[size];
		bf.readBytes(bytes);
		String msg = null;
		try {
			msg = new String(bytes, "iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return msg;
	}

	public static ChannelFuture sendMssageBytes(ChannelHandlerContext ctx,
			byte[] header, byte[] bytes) {
		if (header == null || bytes == null) {
			return null;
		}
		int length = header.length + bytes.length + 4;
		ByteBuf b = Unpooled.buffer(length);
		b = b.order(ByteOrder.LITTLE_ENDIAN);
		b.writeBytes(header);
		b.writeInt(length);
		b.writeBytes(bytes);
		ChannelFuture future = ctx.writeAndFlush(b);
		return future;
	}

	public static ChannelFuture sendMessageStr(ChannelHandlerContext ctx,
			StringBuffer str) {

		ChannelFuture future = ctx.writeAndFlush(str.toString());

		return future;
	}

	public static ChannelFuture sendMssageBytes(ChannelHandlerContext ctx,
			byte[] header, ByteBuf bytes) {
		if (header == null || bytes == null) {
			return null;
		}
		int length = header.length + bytes.readableBytes() + 4;
		ByteBuf b = Unpooled.buffer(length);
		b = b.order(ByteOrder.LITTLE_ENDIAN);
		b.writeBytes(header);
		b.writeInt(length);
		b.writeBytes(bytes);

		ChannelFuture future = ctx.writeAndFlush(b);
		return future;
	}

	public static ChannelFuture sendMssageBytes(ChannelHandlerContext ctx,
			byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		ByteBuf b = Unpooled.buffer(bytes.length);
		ChannelFuture future = ctx.writeAndFlush(b);
		return future;
	}

	public static ChannelFuture sendMssageBytes(ChannelHandlerContext ctx,
			ByteBuf bytes) {
		if (bytes == null) {
			return null;
		}
		ChannelFuture future = ctx.writeAndFlush(bytes);
		return future;
	}

	// ��˻��ͻظ���Ϣ
	public static ChannelFuture sendMssageto(ChannelHandlerContext ctx,
			String code, String content) {
		if (null == code) {
			return null;
		}
		try {
			code = new String(code.getBytes(), "iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ByteBuf b = null;
		if (content == null || content.isEmpty()) {
			b = Unpooled.buffer(code.length());
			b.writeBytes(code.getBytes());
		} else {
			try {
				content = new String(content.getBytes(), "iso-8859-1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			b = Unpooled.buffer(code.length() + 4 + content.getBytes().length);
			b = b.order(ByteOrder.LITTLE_ENDIAN);
			b.writeBytes(code.getBytes());
			b.writeInt(content.getBytes().length);
			b.writeBytes(content.getBytes());
		}

		ChannelFuture future = ctx.writeAndFlush(b);
		return future;
	}

	/**
	 * julong回复协议
	 * 
	 * @param machine
	 * @param e4
	 * @return
	 */
	public static ByteBuf getMsg2Send(byte[] machine, int nt) {
		ByteBuf b = Unpooled.buffer();
		b.writeBytes(JLHandler.VERSION);
		b.writeBytes(new byte[] { 0x00, (byte) 0x00 });
		b.writeBytes(new byte[] { (byte) nt, 0x00 });
		b.writeBytes(machine);
		b.writeBytes(new byte[] { 0x00, (byte) 0x00 });
		b.writeBytes(new byte[] { 0x00, (byte) 0x00 });
		return b;
	}

	/**
	 * 武汉农商行回复协议
	 * 
	 * @param machine
	 * @param e4
	 * @return
	 */
	public static ByteBuf getMsg2Send(byte[] cmd, int nt, int amountCount) {
		ByteBuf b = Unpooled.buffer();
		b.writeBytes(JLHandler.VERSION);
		b.writeBytes(cmd);
		b.writeBytes(new byte[] { (byte) nt, 0x00 });
		b.writeInt(amountCount);
		b.writeBytes(new byte[] { 0x00, (byte) 0x00 });

		return b;
	}

	/**
	 * @param machine
	 * @param e4
	 * @return
	 */
	public static ByteBuf getMsg2SendSynchro(byte[] machine, int nt,
			byte[] serverTime) {
		ByteBuf b = Unpooled.buffer();
		b.writeBytes(JLHandler.VERSION);
		b.writeBytes(new byte[] { 0x10, (byte) 0x00 });
		b.writeBytes(new byte[] { (byte) nt, 0x00 });
		b.writeBytes(machine);
		b.writeBytes(serverTime);
		b.writeBytes(new byte[] { 0x00, (byte) 0x00 });
		return b;
	}
}
