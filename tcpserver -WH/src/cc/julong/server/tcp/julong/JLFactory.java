package cc.julong.server.tcp.julong;

import io.netty.handler.codec.ByteToMessageDecoder;
import cc.julong.log.Log;
import cc.julong.server.state.DataInfo;
import cc.julong.server.state.Protocol;
import cc.julong.server.tcp.TcpFactory;
import cc.julong.server.util.Utils;

@Protocol("julong")
public class JLFactory extends TcpFactory {

	public static ByteToMessageDecoder createHandler(byte[] cmd, DataInfo data,
			int version) {

		Log.Info("Tcpserver", "cmd: " + (int) cmd[0] + "," + (int) cmd[1]
				+ ";version: " + version);

		if (Utils.bytesEqual(cmd, new byte[] { (byte) 0xA1, 0x00 })) {

			return new HelloHandler(data);
		}
		if (cmd[0] >= (byte) 0x01 && cmd[0] <= (byte) 0x08) {
			return new DataHandler(data);
		}
		if (cmd[0] == (byte) 0x09) {
			// 实时
			return new RealTimeHandler(data);
		}
		if (Utils.bytesEqual(cmd, new byte[] { (byte) 0xA2, 0x00 })) {

			return new GoodbyeHandler(data);
		}
		if (cmd[0] == (byte) 0x10) {
			// time synchro
			return new SynchroTimeHandler(data);
		}
		if (Utils.bytesEqual(cmd, new byte[] { (byte) 0xC3, 0x00 })) {
			// say start
			return new SayStartHandler(data);
		}
		if (Utils.bytesEqual(cmd, new byte[] { (byte) 0xC4, 0x00 })) {
			// say end
			return new SayEndhandler(data);
		}
		if (Utils.bytesEqual(cmd, new byte[] { (byte) 0xC5, 0x00 })) {
			// say cancel
			return new SayCanCelHandler(data);
		}
		if (Utils.bytesEqual(cmd, new byte[] { (byte) 0xC6, 0x00 })) {
			// say get amount count
			return new SayGetAmountCounthandler(data);
		}

		Log.Info("not find right");
		
		return null;
	}

	@Override
	public ByteToMessageDecoder getMain(DataInfo data) {
		return getDefault(data);
	}

	public static ByteToMessageDecoder getDefault(DataInfo data) {
		return new JLHandler(data);
	}

	@Override
	public boolean isRight(byte[] bytes) {
		if (Utils.bytesEqual(bytes, JLHandler.HEADER)) {
			return true;
		}
		return false;
	}

}
