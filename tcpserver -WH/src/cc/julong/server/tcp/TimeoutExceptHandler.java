package cc.julong.server.tcp;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
//import cc.julong.transfer.core.transferFactory;
//import cc.julong.transfer.model.TransferType;

import cc.julong.log.Log;

public class TimeoutExceptHandler extends ChannelDuplexHandler {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if (cause instanceof ReadTimeoutException) {
			Log.Info("TcpServer", ctx.channel().remoteAddress().toString()
					+ "read out time and break of connection");
			// monitor
			// transferFactory.setErrDoneCountAdd(TransferType.GATHER.toString());
			// transferFactory.setErrorMsg(TransferType.GATHER.toString(),
			// ctx.channel().remoteAddress().toString()
			// + "����ʱ�Ͽ�����");
		} else if (cause instanceof WriteTimeoutException) {
			Log.Info("TcpServer", ctx.channel().remoteAddress().toString()
					+ "write out time and break of connection");
			// monitor
			// transferFactory.setErrDoneCountAdd(TransferType.GATHER.toString());
			// transferFactory.setErrorMsg(TransferType.GATHER.toString(),
			// ctx.channel().remoteAddress().toString()
			// + "д��ʱ�Ͽ�����");
		} else {
			super.exceptionCaught(ctx, cause);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
