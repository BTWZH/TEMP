package cc.julong.main;

import java.util.Date;

import cc.julong.common.InitParaBean;
import cc.julong.common.RealTAction;
import cc.julong.config.ConfigMan;
import cc.julong.config.LocalConf;
import cc.julong.ftp.UpLoadThread;
import cc.julong.log.Log;
import cc.julong.server.tcp.ATcpServer;
import cc.julong.server.util.Utils;

//import winstone.Launcher;

public class Server {

	/**
	 * 初始化采集以及FTP上报服务
	 */
	public static void init() {
		Log.Info("---init start---");
		try {
			InitParaBean init = RealTAction.getInitBean();

			UpLoadThread upLoadThread = new UpLoadThread();

			upLoadThread.init(init);

			upLoadThread.start();

			Utils.isDayToDelLogs();

			ATcpServer tcp = new ATcpServer(init.getCollectPort());

			tcp.start();

		} catch (Exception e) {
			Log.Info("init error: " + e.getMessage());
		}
	}

	/**
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) {
		try {

			// System.getProperty("java.class.path");

			// ConfigMan.setPath("F:/");

			if (RealTAction.getInitBean() == null) {

				LocalConf lc = ConfigMan.getLocalConf();

				InitParaBean init = new InitParaBean();

				init.setCollectPort(lc.getCollectPort());
				init.setCreateDirAuto(lc.getCreateDirAuto());
				init.setFilesRoot(lc.getFilesRoot());
				init.setFtpInterval(lc.getFtpInterval());
				init.setFtpPassWord(lc.getFtpPassWord());
				init.setFtpRootPath(lc.getFtpRootPath());
				init.setFtpServerIp(lc.getFtpServerIp());
				init.setFtpServerPort(lc.getFtpServerPort());
				init.setFtpUser(lc.getFtpUser());
				init.setPassive(lc.getPassive());
				init.setBankCode(lc.getBankCode());
				init.setServerCharset(lc.getServerCharset());
				RealTAction.setInitBean(init);
			}

			Server.init();

		} catch (Exception e) {
			Log.Info("tcpserver ：------------");
			e.printStackTrace();
			Log.Info(e.getMessage());
		}

		// String a = "\0";
		// byte[] b = a.getBytes();
		// System.out.println(new String(b));
//		 try {
//		 Thread.sleep(6 * 1000L);
//		 } catch (InterruptedException e) {
//		 // TODO Auto-generated catch block
//		 e.printStackTrace();
//		 }

		// 大金额
		// RealTAction.BigAmount("123456789", "KHQK");
		// 开始
		 RealTAction.StartReal("sdf", "QK", "id");
		// 结束

		// 取消
		// RealTAction.CancelReal();
		// 获取采集金额
		//System.out.println(RealTAction.GetAmountCount());
		 try {
		 Thread.sleep(2 * 1000L);
		 } catch (InterruptedException e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
		 }

		// RealTAction.CancelReal();
		//
		// // RealTAction.GetAmountCount();
		// // 交易流水号_卡号$户名$证件号_操作员编号_柜台号_交易金额
		 RealTAction.EndReal("123456", new Date(), "0", "0", "0001", "操作员1",
		 "10000", "id","123");
		 RealTAction.StartReal("sdf", "QK", "id");
		 RealTAction.EndReal("123456", new Date(), "0", "0", "0001", "操作员1",
				 "10000", "id","123");
		 System.out.println(9);

	}
}
