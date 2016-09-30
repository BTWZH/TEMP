package cc.julong.log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import cc.julong.server.util.Utils;

public class Log {
	// private static Logger log = LogManager.getLogger(Log.class);
	// private static FileAppender appender = (FileAppender) Logger
	// .getRootLogger().getAppender("logfile");
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	// public static String LogRoot = new
	// File(Log.class.getResource("/").getPath())
	// .getParent().toString().replaceAll("%20", " ")
	// + "/Logs/";
	public final static String LOGROOT = "/logs";
	private static final String writeLine = "\r\n";

	/* public static String LogRoot="Logs/"; */

	private static String getLogRoot() {
		// String result = "";
		//
		// InitParaBean bean = RealTAction.getInitBean();
		// File ff = new File(bean.getFilesRoot());
		// if (ff.isDirectory() && ff.exists()) {
		// result = bean.getFilesRoot() + "/Logs/";
		// }
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		Date dt = new Date();
		String todayAsString = sdf.format(dt);

		Utils.createPath(LOGROOT);

		return LOGROOT + File.separator + "tcpserver" + "_" + todayAsString + ".log";
	}

	public static synchronized void Info(String msg) {

		String logRoot = getLogRoot();
		if (Utils.isNullOrEmpty(logRoot)) {
			return;
		}
		// appender.setFile(new StringBuffer().append(LogRoot)
		// .append(dateFormat.format(new Date())).append(".log")
		// .toString());
		// appender.activateOptions();
		// log.info(msg);

		File f = new File(logRoot);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date dt = new Date();
		String todayAsString = sdf.format(dt);

		RandomAccessFile mm = null;

		try {
			mm = new RandomAccessFile(f, "rw");

			mm.seek(mm.length());

			mm.writeBytes("***" + todayAsString + "   " + msg + writeLine);

		} catch (Exception e) {

		} finally {

			try {
				mm.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("tcpserver: " + msg);
	}

	public static synchronized void Info(String logClass, String msg) {

		String logRoot = getLogRoot();

		if (Utils.isNullOrEmpty(logRoot)) {
			return;
		}

		// appender.setFile(new StringBuffer().append(LogRoot).append(logClass)
		// .append("_").append(dateFormat.format(new Date()))
		// .append(".log").toString());
		// appender.activateOptions();
		Info(msg);
		// System.out.println("tcpserver: " + msg);
	}

	public static synchronized void Info(String logRoot, String logClass,
			String msg) {
		logRoot = getLogRoot();

		if (Utils.isNullOrEmpty(logRoot)) {
			return;
		}

		// appender.setFile(new StringBuffer().append(logRoot).append(logClass)
		// .append("_").append(dateFormat.format(new Date()))
		// .append(".log").toString());
		// appender.activateOptions();
		Info(msg);
		// System.out.println("tcpserver: " + msg);
	}

	public static synchronized void Debug(String msg) {
		// LogRoot = getLogRoot();
		//
		// if (Utils.isNullOrEmpty(LogRoot)) {
		// return;
		// }
		// appender.setFile(new StringBuffer().append(LogRoot)
		// .append(dateFormat.format(new Date())).append(".log").toString());
		// appender.activateOptions();
		// log.debug(msg);
		System.out.println("tcpserver: " + msg);
	}

	public static synchronized void Debug(String logClass, String msg) {
		// LogRoot = getLogRoot();
		//
		// if (Utils.isNullOrEmpty(LogRoot)) {
		// return;
		// }
		// appender.setFile(new StringBuffer().append(LogRoot).append(logClass)
		// .append("_").append(dateFormat.format(new Date())).append(".log")
		// .toString());
		// appender.activateOptions();
		// log.debug(msg);
		System.out.println("tcpserver: " + msg);
	}

	public static synchronized void Debug(String logRoot, String logClass,
			String msg) {
		// LogRoot = getLogRoot();
		//
		// if (Utils.isNullOrEmpty(LogRoot)) {
		// return;
		// }
		// appender.setFile(new StringBuffer().append(logRoot).append(logClass)
		// .append("_").append(dateFormat.format(new Date())).append(".log")
		// .toString());
		// appender.activateOptions();
		// log.debug(msg);
		System.out.println("tcpserver: " + msg);
	}
}
