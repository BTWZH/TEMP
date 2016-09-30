package cc.julong.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import cc.julong.log.Log;

/**
 * FTP操作类
 * 
 * @author zz_mwq
 * @desc 注意事项：客户端的windows防火墙需要关闭
 * @desc 参考资料：www.qqfall.com/?p=29001
 */
public class UpLoadFTP {

	// private static String ip = "127.0.0.1";
	// private static int port = 21;
	// private static String user = "fsn";
	// private static String pwd = "fsn";
	/** FTP服务器的IP */
	private static String ftpServerIp = "172.16.3.50";
	/** FTP服务器的端口号 */
	private static int ftpServerPort = 21;
	/** FTP服务器的用户名 */
	private static String user = "ftpuser";
	/** FTP服务器的密码 */
	private static String pwd = "ftppwd";

	private static String ftpRootPath = "/";
	private static boolean createDirAuto = true;

	private final FTPClient ftpClient = new FTPClient();
	private FileInputStream is;
	/** 本地字符编码 */
	private static String LOCAL_CHARSET = "GBK";

	// FTP协议里面，规定文件名编码为iso-8859-1
	private static String SERVER_CHARSET = "ISO-8859-1";// ISO-8859-1

	

	/**
	 * FTP上传文件
	 * 
	 */
	protected boolean upLoad(File file) {
		String msg;
		try {
			is = new FileInputStream(file);
			String fileName = new String(
					file.getName().getBytes(LOCAL_CHARSET), SERVER_CHARSET);
			boolean isSuccess = ftpClient.storeFile(fileName, is);
			is.close();

			msg = "UpLoad " + file.getPath() + " to "
					+ ftpClient.printWorkingDirectory()
					+ (isSuccess ? " success" : " error");

			if (isSuccess) {

				// 上传成功后改名
				// ftpClient.rename(file.getName(),
				// file.getName().replaceAll(".now",
				// ""));

				Log.Info("Upload", msg);

				return true;
			} else {
				Log.Info("Upload", msg);
				return false;
			}
		} catch (FileNotFoundException e) {
			msg = "No " + file.getPath() + " defined error, " + e.toString();
			Log.Info("UpLoad", msg);
			return false;
		} catch (IOException e) {
			msg = file.getPath() + " error, " + e.toString();
			Log.Info("UpLoad", msg);
			return false;
		}
	}

	// 设置上传路径
	protected boolean changeToRootDirectory() {
		try {
			ftpClient.changeWorkingDirectory("/");
		} catch (IOException e) {
			Log.Info("Upload", "open root directory error, " + e.toString());
			return false;
		}
		Log.Info("Upload", "open root directory");
		return true;
	}

	// 设置上传路径
	protected boolean changeWorkingDirectory(String path) {
		String[] paths = path.split("/");
		boolean isSuccess;
		for (int i = 1; i < paths.length; i++) {
			isSuccess = makeAndOpenPath(paths[i]);
			if (!isSuccess) {
				return false;
			}
		}
		return true;
	}

	// 设置上传路径
	protected boolean changeWorkingDirectory(File bank) {
		boolean isSuccess = makeAndOpenPath(bank.getParentFile().getName());
		if (!isSuccess) {
			return false;
		}
		isSuccess = makeAndOpenPath(bank.getName());
		if (!isSuccess) {
			return false;
		}
		return true;
	}

	private boolean makeAndOpenPath(String name) {
		try {
			System.out.println(ftpClient.printWorkingDirectory());
			boolean isSuccess = ftpClient.changeWorkingDirectory(name);
			if (!isSuccess) {
				isSuccess = ftpClient.makeDirectory(name);
				if (!isSuccess) {
					Log.Info("Upload", "Create ftp path " + name + " in "
							+ ftpClient.printWorkingDirectory() + " error");
					return false;
				}
				Log.Info("Upload", "Create ftp path " + name);
				isSuccess = ftpClient.changeWorkingDirectory(name);
				if (!isSuccess) {
					Log.Info("Upload", "Open ftp path " + name + " in "
							+ ftpClient.printWorkingDirectory() + " error");
					return false;
				}
			}
		} catch (IOException e) {
			Log.Info("Upload",
					"Create or open working directory error, " + e.toString());
			return false;
		}
		Log.Info("Upload", "Open ftp path " + name);
		return true;
	}

	// 登录
	protected boolean login() {
		String msg;
		try {
			ftpClient.connect(ftpServerIp, ftpServerPort);
			if (!ftpClient.login(user, pwd)) {
				msg = "FTP user or password error";
				// UpLoadWarn.addErrorMsg(msg);
				Log.Info("Upload", msg);
				return false;
			}
			ftpClient.setBufferSize(1024);
			if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(
					"OPTS UTF8", "ON"))) {// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
				LOCAL_CHARSET = "UTF-8";
			}
			ftpClient.setControlEncoding(LOCAL_CHARSET);
			// ftpClient.setControlEncoding("GBK");
			ftpClient.enterLocalPassiveMode();// 设置被动模式
			// ftpClient.setFileType(getTransforModule());// 设置传输的模式
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
		} catch (SocketException e) {
			msg = "Login ftp socket error, " + e.toString();
			// UpLoadWarn.addErrorMsg(msg);
			Log.Info("Upload", msg);
			return false;
		} catch (IOException e) {
			msg = "Login ftp io error, " + e.toString();
			// UpLoadWarn.addErrorMsg(msg);
			Log.Info("Upload", msg);
			return false;
		}
		return true;
	}

	// 注销
	protected boolean logout() {
		if (ftpClient.isConnected()) {
			try {
				ftpClient.logout();
				ftpClient.disconnect();
			} catch (IOException e) {
				Log.Info("Upload", "Logout ftp connect error, " + e.toString());
				return false;
			}
		}
		return true;
	}

	// 测试
	protected String test() {
		String msg = "";
		//
		try {
			ftpClient.connect(ftpServerIp, ftpServerPort);
			if (!ftpClient.login(user, pwd)) {
				msg = "FTP user or password error";
				Log.Info("Upload", msg);
				return msg;
			}
			ftpClient.setBufferSize(1024);
			if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(
					"OPTS UTF8", "ON"))) {// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
				LOCAL_CHARSET = "UTF-8";
			}
			ftpClient.setControlEncoding(LOCAL_CHARSET);
			// ftpClient.setControlEncoding("GBK");
			ftpClient.enterLocalPassiveMode();// 设置被动模式
			// ftpClient.setFileType(getTransforModule());// 设置传输的模式
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
		} catch (SocketException e) {
			msg = "Login ftp socket error, " + e.toString();
			Log.Info("Upload", msg);
			return msg;
		} catch (IOException e) {
			msg = "Login ftp io error, " + e.toString();
			Log.Info("Upload", msg);
			return msg;
		}
		//
		// final String name = "test_ftp_" + System.currentTimeMillis();
		// try {
		// boolean isSuccess = ftpClient.changeWorkingDirectory(name);
		// if (!isSuccess) {
		// isSuccess = ftpClient.makeDirectory(name);
		// if (!isSuccess) {
		// msg = "Create ftp path " + name + " in "
		// + ftpClient.printWorkingDirectory() + " error";
		// Log.Info(msg);
		// return msg;
		// }
		// Log.Debug("Create ftp path " + name);
		// isSuccess = ftpClient.changeWorkingDirectory(name);
		// if (!isSuccess) {
		// msg = "Open ftp path " + name + " in "
		// + ftpClient.printWorkingDirectory() + " error";
		// Log.Info(msg);
		// return msg;
		// }
		// }
		// } catch (IOException e) {
		// msg = "Create or open working directory error, " + e.toString();
		// Log.Info(msg);
		// return msg;
		// }
		//
		if (ftpClient.isConnected()) {
			try {
				ftpClient.logout();
				ftpClient.disconnect();
			} catch (IOException e) {
				msg = "Logout ftp connect error, " + e.toString();
				Log.Info("Upload", msg);
				return msg;
			}
		}
		return msg;
	}

	// *************************************************/

	public static final String getFtpServerIp() {
		return ftpServerIp;
	}

	public static final void setFtpServerIp(String ftpServerIp) {
		UpLoadFTP.ftpServerIp = ftpServerIp;
	}

	public static final int getFtpServerPort() {
		return ftpServerPort;
	}

	public static final void setFtpServerPort(int ftpServerPort) {
		UpLoadFTP.ftpServerPort = ftpServerPort;
	}

	public static final String getUser() {
		return user;
	}

	public static final void setUser(String user) {
		UpLoadFTP.user = user;
	}

	public static final String getPwd() {
		return pwd;
	}

	public static final void setPwd(String pwd) {
		UpLoadFTP.pwd = pwd;
	}

	public static String getFtpRootPath() {
		return ftpRootPath;
	}

	public static void setFtpRootPath(String path) {
		Pattern p = Pattern.compile("([/]{1}[a-zA-Z0-9]+)+");
		Matcher m = p.matcher(path);
		if (m.matches()) {
			UpLoadFTP.ftpRootPath = path;
		} else {
			UpLoadFTP.ftpRootPath = "/";
		}
	}

	public static boolean isCreateDirAuto() {
		return createDirAuto;
	}

	public static void setCreateDirAuto(boolean createDirAuto) {
		UpLoadFTP.createDirAuto = createDirAuto;
	}
	
	public static String getSERVER_CHARSET() {
		return SERVER_CHARSET;
	}

	public static void setSERVER_CHARSET(String sERVER_CHARSET) {
		SERVER_CHARSET = sERVER_CHARSET;
	}

	// *************************************************/

	public static void main(String[] args) {
		// UpLoadFTP ftp = new UpLoadFTP();
		// ftp.login();
		// ftp.upLoad(new File("E://fsn/UpLoadFSN.properties"));
		// ftp.logout();

		try {
			String sss = new String("我艹".getBytes("utf-8"), "iso-8859-1");

			System.out.println(sss);

			sss = new String(sss.getBytes("iso-8859-1"), "utf-8");

			System.out.println(sss);

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
