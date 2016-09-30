package cc.julong.server.exception;

import io.netty.channel.ChannelHandlerContext;
import cc.julong.server.state.DataInfo;

public abstract class ServerException extends Exception {

	private String msg = null;

	public ServerException(String msg) {
		this.setMsg(msg);
	}

	private static final long serialVersionUID = 1788555124236867311L;

	public abstract void handleException(ChannelHandlerContext ctx,
			DataInfo data);

	public static String getErrMessage(Throwable ex, String str) {
		String msg = ex.getMessage();
		if (msg == null) {
			msg = ex.toString();
		}
		StringBuilder sb = new StringBuilder(msg);
		StackTraceElement[] els = ex.getStackTrace();
		for (StackTraceElement es : els) {
			if (es != null) {
				sb.append(str);
				sb.append(es.toString());
			}
		}
		return sb.toString();
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
