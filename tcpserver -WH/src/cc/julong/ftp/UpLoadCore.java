package cc.julong.ftp;

import java.io.*;
import java.util.*;

import cc.julong.log.Log;
import cc.julong.server.tcp.julong.JLHandler;
import cc.julong.server.util.FileUtils;

/**
 * ftp upload
 * 
 * @author zz_mwq
 */
public class UpLoadCore {

	private static String fileRoot = "E://FSN_TEST/fsn";

	private final UpLoadThread thread;

	@SuppressWarnings("unused")
	private boolean ftpState = true;//

	/**
	 * upload
	 * 
	 * @param thread
	 */
	public UpLoadCore(UpLoadThread thread) {
		this.thread = thread;
		File file;
		file = new File(fileRoot);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
		fileRoot = file.getPath();

		Log.Info("Upload", "fileRoot: " + fileRoot);
	}

	// //////////////////////////////////////////////////////////////////////////////

	private void uploadByMap(Map<String, List<File>> map) {
		UpLoadFTP ftp = new UpLoadFTP();
		// ftp can't login
		if (!ftp.login()) {
			ftpState = false;
			Log.Info("Upload", "FTP login faile");
			return;
		}
		// upload
		Iterator<String> pathIt = map.keySet().iterator();
		String path;
		over: while (pathIt.hasNext()) {
			path = pathIt.next();
			//
			if (!ftp.changeToRootDirectory()) {
				ftpState = false;
				ftp.logout();
				return;
			}
			if (!ftp.changeWorkingDirectory(UpLoadFTP.getFtpRootPath())) {
				ftpState = false;
				ftp.logout();
				return;
			}
			String ftpPath = "";

			String tmpPath = path.replace("\\", "/");

			for (int i = 0; i < tmpPath.split("/").length; i++) {
				if (i >= tmpPath.split("/").length - 3) {
					if (ftpPath.length() > 0) {
						ftpPath += "/";
					}
					ftpPath += tmpPath.split("/")[i];
				}
			}

			if (!ftp.changeWorkingDirectory(ftpPath)) {
				ftpState = false;
				ftp.logout();
				return;
			}
			//
			List<File> list = map.get(path);
			// UpLoadWarn.initTaskCount(list.size());
			Iterator<File> fileIt = list.iterator();

			while (fileIt.hasNext()) {
				upload(ftp, fileIt.next());

				if (!thread.isRun())
					break over;
			}
		}
		ftp.logout();
	}

	/**
	 * find files 2 map
	 * 
	 * @param path
	 * @param map
	 */
	private void filterToMap(File path, Map<String, List<File>> map) {
		if (!thread.isRun())
			return;
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
			filterToMap(file, map);
		}
		//
		files = path.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return false;
				} else {
					return true;
				}
			}
		});
		//
		int c = files.length;
		if (c > 0) {
			List<File> list = new ArrayList<File>();
			for (File file : files) {
				list.add(file);
			}
			map.put(path.getPath(), list);
		}
	}

	/**
	 * 
	 */
	protected void rootByMap() {
		File file = new File(fileRoot);
		if (file.isDirectory()) {
			Map<String, List<File>> map = new LinkedHashMap<String, List<File>>();
			filterToMap(file, map);
			uploadByMap(map);
		} else {
			Log.Info("Upload", "No " + fileRoot + " defined error");
		}
	}

	// //////////////////////////////////////////////////////////////////////////////

	private void uploadByList(LinkedList<File> list) {
		UpLoadFTP ftp = new UpLoadFTP();
		//
		if (!ftp.login()) {
			ftpState = false;
			return;
		}
		//
		if (!ftp.changeWorkingDirectory(UpLoadFTP.getFtpRootPath())) {
			ftpState = false;
			ftp.logout();
			return;
		}
		//
		// UpLoadWarn.initTaskCount(list.size());
		Iterator<File> it = list.iterator();
		while (it.hasNext()) {
			upload(ftp, it.next());
			if (!thread.isRun())
				break;
		}
		ftp.logout();
	}

	private void filterToList(File path, LinkedList<File> list) {
		if (!thread.isRun())
			return;
		File[] files = path.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				filterToList(file, list);// 杩唬
			} else {
				list.add(file);// 鏀堕泦
			}
		}
	}

	protected void rootByList() {
		File file = new File(fileRoot);
		if (file.isDirectory()) {
			LinkedList<File> list = new LinkedList<File>();
			filterToList(file, list);
			uploadByList(list);
		} else {
			Log.Info("Upload", "No " + fileRoot + " defined error");
		}
	}

	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * 
	 * @param dtMill
	 * @param addMinute
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean compareMillAndNow(Date dtMill, int addMinute) {
		Date now = new Date();

		try {
			Calendar millCal = Calendar.getInstance();
			millCal.setTime(dtMill);

			millCal.add(Calendar.MINUTE, addMinute);

			Calendar nowCal = Calendar.getInstance();

			nowCal.setTime(now);

			if (nowCal.after(millCal)) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 * 
	 * @param ftp
	 * @param link
	 * @return
	 */
	private boolean upload(UpLoadFTP ftp, File link) {
		// int addMinutes = 30; //
		File file = new File(link.getPath());

		if (file.getName().endsWith(".tmp")) {
//			if (new Date().getTime() - file.lastModified() > JLHandler.TMP2FSNTIME) {
//				// 超过2分钟tmp = > fsn
//				FileUtils.jnRemoveSuffix(file.getPath(), new Date(), "");
//			}
			return false;
		}

		if (file.getName().endsWith(".log")) {
			return false;
		}

		//
		if (ftp.upLoad(file)) {

			//
			if (file.delete()) {
				Log.Info("Upload", "upload" + file.getPath() + " success");
			} else {
				String msg = "upload " + file.getPath() + " success, but delete file error";
				Log.Info("Upload", msg);
			}

			return true;
		} else {
			Log.Info("Upload", "upload" + file.getPath() + " error");
			return false;
		}
	}

	// //////////////////////////////////////////////////////////////////////////////

	public static String getFileRoot() {
		return fileRoot;
	}

	public static void setFileRoot(String fileRoot) {
		UpLoadCore.fileRoot = fileRoot;
	}

}
