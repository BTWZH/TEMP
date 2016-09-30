package cc.julong.server.tcp;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;

//import cc.julong.transfer.core.transferFactory;
//import cc.julong.transfer.model.TransferType;
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
import cc.julong.log.Log;

/**
 * �ɼ�����
 * 
 * @author zengming
 * 
 */
public class ATcpServer implements Runnable {
	io.netty.bootstrap.ServerBootstrap b;

	private int port;

	/**
	 * @return port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            Ҫ���õ� port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	public static boolean isRun = false;

	public ATcpServer() {
		Protocols.loadProtocols();
	}

	public ATcpServer(int port) {
		this.port = port;
		Protocols.loadProtocols();
	}

	public ATcpServer(int port, String path) {
		this.port = port;
	}

	private ScheduledExecutorService executor = Executors
			.newSingleThreadScheduledExecutor();

	public void start() {
		isRun = true;
		new Thread(this).start();
	}

	public boolean isRun() {
		return isRun;
	}

	public void stop() {
		b.group().shutdownGracefully();

		while (!b.group().isTerminated()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		executor.shutdown();

		while (!executor.isTerminated()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		isRun = false;
		// bossGroup.shutdownGracefully();
		// workerGroup.shutdownGracefully();
	}

	@Override
	public void run() {
		// Configure the server.

		io.netty.channel.EventLoopGroup bossGroup = new io.netty.channel.nio.NioEventLoopGroup();
		io.netty.channel.EventLoopGroup workerGroup = new io.netty.channel.nio.NioEventLoopGroup();

		try {
			//
			// ��ʼ���ɼ�������
			b = new io.netty.bootstrap.ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(
							io.netty.channel.socket.nio.NioServerSocketChannel.class)
					.option(io.netty.channel.ChannelOption.SO_BACKLOG, 128)
					.childHandler(new ServerInitializer(executor));

			// Start the server.
			io.netty.channel.ChannelFuture f = b.bind(port).sync();

			Log.Info("tcpserver start");
			Log.Info("---init end---");

			// Wait until the server socket is closed.
			f.channel().closeFuture().sync();

		} catch (RejectedExecutionException e) {
			// monitor
			// transferFactory.setErrDoneCountAdd(TransferType.GATHER.toString());
			// transferFactory.setErrorMsg(TransferType.GATHER.toString(),
			// e.getMessage());

			Log.Info(e.toString());
			isRun = false;

		} catch (Throwable e) {
			// monitor
			// transferFactory.setErrDoneCountAdd(TransferType.GATHER.toString());
			// transferFactory.setErrorMsg(TransferType.GATHER.toString(),
			// e.getMessage());

			Log.Info(e.toString());
			isRun = false;
		} finally {
			// Shut down all event loops to terminate all threads.
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();

			Log.Info("tcpserver stop");
		}
	}

	public static void main(String[] args) {
		int port = 8234;
		// Log.Info("TcpServer","111");
		// org.apache.log4j.Logger.getLogger(ATcpServer.class).error("123");
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			// �ɼ�ϵͳ����˿�
			port = 8234;
		}

		// ʵʱ����

		// RealTBean bean = new RealTBean();
		//
		// bean.setBankNo("12345678");
		// bean.setBusiType("ATM");
		// bean.setMach("TVG00001");
		// bean.setMachType("XX");
		// bean.setFileMaxRecord(10000);

		// RealTAction.StartReal(bean);

		// ���òɼ�������
		ATcpServer at = new ATcpServer(port);

		at.start();

		// Thread.sleep(5000);

		// at.stop();
	}
}
