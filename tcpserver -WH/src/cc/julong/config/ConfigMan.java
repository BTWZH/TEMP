package cc.julong.config;

import java.io.File;

import cc.julong.log.Log;

/**
 * 配置文件基类
 * 
 * @author Administrator
 *
 */
public class ConfigMan {
	// 打包模式
	private static String path = new ConfigMan().getClass().getProtectionDomain().getCodeSource().getLocation()
			.toString().replaceAll("%20", " ").replace("file:/", "").replace("tcpserver.jar", "");
	// 工程模式
//	 private static String path = new
//	 File(ConfigMan.class.getResource("/").getPath()).getParent().toString()
//	 .replaceAll("%20", " ") + "/";

	private static LocalConf localConf = new LocalConf(path + "ccms.conf");

	public static String getPath() {
		return path;
	}

	public static void setPath(String path) {
		ConfigMan.path = path;

		Log.Info("ConfigMan.path : " + new ConfigMan().getClass().getProtectionDomain().getCodeSource().getLocation()
				.toString().replaceAll("%20", " ").replace("file:/", "").replace("tcpserver.jar", ""));
	}

	public static void setLocalConf(LocalConf localConf) {
		ConfigMan.localConf = localConf;
	}

	public static String getRootPath() {
		return path;
	}

	public static LocalConf getLocalConf() {
		return localConf;
	}

	public static void reload() {

		/*
		 * localConf.reload(); clearConf.reload(); databaseConf.reload();
		 * ftpConf.reload(); exportConf.reload(); currencyConf.reload();
		 */

	}

}
