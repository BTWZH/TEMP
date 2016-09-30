package cc.julong.server.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import cc.julong.common.RealTAction;
import cc.julong.common.RealTBean;
import cc.julong.log.Log;
import cc.julong.server.tcp.julong.DataHandler;
import cc.julong.server.tcp.julong.JLHandler;

public class Utils {

	// logs 文件保留天数 30l * 24 * 60 * 60 * 1000
	private final static long KEEPDAYS = 2592000000L;
	// tmp 文件保留分钟 2l * 60 * 1000
	private final static long Min10 = 60000L;

	public static boolean bytesEqual(byte[] b1, byte[] b2) {
		if (b1.length != b2.length) {
			return false;
		}
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] != b2[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * �ж��ַ��Ƿ�Ϊ��
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * ��ʽ���ַ�,�����
	 * 
	 * @param content
	 *            ��Ҫ��ʽ���ַ�
	 * @param minLength
	 *            ��󳤶�
	 * @param filling
	 *            ����ַ�
	 * @return ��ʽ������ַ�
	 */
	public static String leftPad(String content, int minLength, char filling) {
		int ln = content.length();
		if (minLength <= ln) {
			return content;
		}
		StringBuffer res = new StringBuffer(minLength);
		int dif = minLength - ln;

		for (int i = 0; i < dif; i++) {
			res.append(filling);
		}

		res.append(content);
		return res.toString();
	}

	/*
	 * ��ȡproperties�����ļ�����
	 */
	public static String getPropValue(String key) {

		String ccmsPath = "ccms.conf";

		String value = null;
		Properties prop = new Properties();

		String proPath = DataHandler.class.getResource("/").getPath();
		// new File(ConfigMan.class.getResource("/")
		// .getPath()).getParent().toString().replaceAll("%20", " ") + "/";
		// String websiteURL = (proPath.replace("/build/classes", "")
		// .replace("%20", " ").replace("classes/", "") + ccmsPath)
		// .replaceFirst("/", "");
		String websiteURL = new File(proPath).getParent().toString().replaceAll("%20", " ") + "/" + ccmsPath;
		// F:/workspace/TcpServer/WebContent/WEB-INF/ccms.conf
		// Log.Info("�ɼ������ļ�·�� :" + websiteURL);
		File file = new File(websiteURL);
		InputStream in;
		try {

			in = new FileInputStream(file);

			prop.load(in);

			value = prop.getProperty(key);

			in.close();

		} catch (IOException e) {
			e.printStackTrace();

		}

		return value;
	}

	/**
	 * @param path
	 */
	public static void createPath(String path) {
		File ff = new File(path);
		// SecurityException
		if (!ff.exists() && !ff.isDirectory()) {
			ff.mkdir();
		}
	}

	private final static int DELLOGSDAY = Calendar.MONDAY;

	/**
	 * 每个周一删除logs文件
	 * 
	 * @return
	 */
	public static void isDayToDelLogs() {
		try {
			Date now = new Date();

			Calendar cal = Calendar.getInstance();
			cal.setTime(now);

			if (cal.get(Calendar.DAY_OF_WEEK) == DELLOGSDAY) {
				// 删除日志
				delLogs();
			}

		} catch (Exception e) {
			Log.Info("清除日志失败，信息： " + e.getMessage());
		}
	}

	/**
	 * 删除30天前的日志文件
	 * 
	 * @param logPath
	 */
	private static void delLogs() {
		// 获取logs文件列表
		File filePath = new File(Log.LOGROOT);

		List<File> lst = new ArrayList<File>();

		filterToList(filePath, lst);

		for (int i = 0; i < lst.size(); i++) {
			// 挨个文件名解析出日期
			try {
				File file = lst.get(i);

				if (isLogHadDel(file.getName())) {
					// del file
					FileUtils.delFile(file.getPath());
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	/**
	 * 文件是否超过30天
	 * 
	 * @param fileName
	 * @return
	 */
	private static boolean isLogHadDel(String fileName) {
		boolean result = false;

		isLogHadDel(fileName, KEEPDAYS);

		return result;
	}

	/**
	 * 文件是否超过30天
	 * 
	 * @param fileName
	 * @return
	 */
	private static boolean isLogHadDel(String fileName, long time) {
		boolean result = false;

		// 文件上的日期
		String fileDate = "";

		if (isNullOrEmpty(fileName)) {
			// fileName is null or " "
			return result;
		}

		try {
			fileDate = fileName.split("_")[1];
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (isNullOrEmpty(fileDate)) {
			// fileDate is null or " "
			return result;
		}

		try {

			// 与当前时间比较，超过一个月删除
			Date now = new Date();

			Date fileTime = getLogsFileDate(fileDate);

			if (now.getTime() - fileTime.getTime() > time) {
				// 超过30天
				result = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 删除2分钟未操作的fsn文件
	 * 
	 * @param logPath
	 */
	public static void removeFileWith2min(String path) {
		// 获取logs文件列表
		File filePath = new File(path);

		List<File> lst = new ArrayList<File>();

		filterToListWithEnd(filePath, lst);

		for (int i = 0; i < lst.size(); i++) {
			// 挨个文件名解析出日期
			try {
				File file = lst.get(i);

				if (isTmpHadRem(file.getPath(), Min10)) {
					RealTBean bean = RealTAction.getMap().get(RealTAction.getMachNo());
					if (bean == null || isNullOrEmpty(bean.getFileName())) {
						FileUtils.jnRemoveSuffix3(file.getPath());
						RealTAction.setHshStruct(new HashSet<String>());
					} else {
						if ("yellow".equals(bean.getBizId())) {
							// 黄标文件
							FileUtils.jnRemoveSuffix3(file.getPath());
							RealTAction.setHshStruct(new HashSet<String>());
							/* --- 武汉农信银行 重复黄边文件名修改 2015/12/04 开始 --- */
							RealTAction.RemoveReal(RealTAction.getMachNo());
							/* --- 武汉江农信银行 重复黄边文件名修改 2015/12/04 开始 --- */
						}
						// if (!new
						// File(bean.getFileName()).getPath().equals(file.getPath()))
						// {
						// // del file
						// FileUtils.jnRemoveSuffix3(file.getPath());
						// }
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	/**
	 * 文件是否超过1分钟
	 * 
	 * @param fileName
	 * @return
	 */
	private static boolean isTmpHadRem(String fileName, long time) {
		boolean result = false;

		// 文件上的日期
		Long fileDate = 0l;

		if (isNullOrEmpty(fileName)) {
			// fileName is null or " "
			return result;
		}

		try {
			fileDate = new File(fileName).lastModified();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			// 与当前时间比较，超过一个月删除
			Date now = new Date();

			if (now.getTime() - fileDate > time) {
				// 超过10分钟
				result = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * String 2 Date
	 * 
	 * @param time
	 * @return
	 */
	private static Date getLogsFileDate(String time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

		Date temp = new Date();
		try {
			temp = dateFormat.parse(time);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return temp;
	}

	/**
	 * find files 2 list
	 * 
	 * @param path
	 * @param map
	 */
	private static void filterToListWithEnd(File path, List<File> list) {

		File[] files;
		// find files
		files = path.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				} else {
					return false;
				}
			}
		});
		// 杩唬
		for (File file : files) {
			filterToListWithEnd(file, list);
		}
		//
		files = path.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return false;
				} else {
					if (file.getName().endsWith(JLHandler.FileSuffix)) {
						return true;
					} else {
						return false;
					}
				}
			}
		});
		//
		int c = files.length;
		if (c > 0) {
			for (File file : files) {
				list.add(file);
			}
		}
	}

	/**
	 * find files 2 list
	 * 
	 * @param path
	 * @param map
	 */
	private static void filterToList(File path, List<File> list) {

		File[] files;
		// find files
		files = path.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				} else {
					return false;
				}
			}
		});
		// 杩唬
		for (File file : files) {
			filterToList(file, list);
		}
		//
		files = path.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return false;
				} else {
					if (file.getName().startsWith("tcpserver")) {
						return true;
					} else {
						return false;
					}
				}
			}
		});
		//
		int c = files.length;
		if (c > 0) {
			for (File file : files) {
				list.add(file);
			}
		}
	}

}
