package cc.julong.server.util;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import cc.julong.common.RealTAction;
import cc.julong.log.Log;
import cc.julong.server.state.DataInfo;
import cc.julong.server.tcp.julong.JLException;

public class FileUtils {

	private static String CNY = "CNY"; // 锟斤拷锟街憋拷识
	private static String versions = "15"; // 锟斤拷位锟芥本

	private static final int bankNoMinSize = 8;
	private static final char bankNoLeftSpace = '0';

	private static final int FK_busiType = 0x01;
	private static final int SK_busiType = 0x02;
	private static final int QK_busiType = 0x03;
	private static final int CK_busiType = 0x04;
	private static final int ATM_busiType = 0x05;
	private static final int HM_busiType = 0x06;
	private static final int CAQK_busiType = 0x07;
	private static final int CACK_busiType = 0x08;

	public static String getFileName(DataInfo info) throws JLException {
		if (info.getMachineinfo() == null) {
			Log.Info("TcpServer", "error,machine is null");
			return null;
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

		if (Utils.isNullOrEmpty(info.getBankNo())) {
			info.setBankNo(RealTAction.getInitBean().getBankCode());
		}

		String bankNo = Utils.leftPad(info.getBankNo(), bankNoMinSize, bankNoLeftSpace);

		String busiType = getBusiType(info.getBusiType(), info.getCodeMsg());

		if (Utils.isNullOrEmpty(busiType))
			busiType = "HM";

		// 锟斤拷锟铰凤拷锟�
		String path = info.getFilePath();

		if (Utils.isNullOrEmpty(path))
			throw new JLException(
					"path is null" + (Utils.isNullOrEmpty(path) ? "path is null" : " path is exest but create false"));

		if (Utils.isNullOrEmpty(info.getMachineinfo()))
			throw new JLException("machine is null");

		try {
			if (info.getBeginDate() == null) {
				info.setBeginDate(new Date());
			}

			if (Utils.isNullOrEmpty(info.getMachineType())) {
				info.setMachineType("AD");
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			String todayAsString = sdf.format(info.getBeginDate());

			Utils.createPath(path);

			path = path + "/" + todayAsString;

			Utils.createPath(path);

			path = path + "/" + info.getMachineinfo();

			Utils.createPath(path);

		} catch (NullPointerException e) {
			Log.Info("TcpServer", "create tcpserver path false");
		} catch (Exception e) {
			Log.Info("TcpServer", e.toString());
		}

		return path + "/" + CNY + versions + "_" + df.format(info.getBeginDate()) + "_" + bankNo + info.getMachineType()
				+ "_" + info.getMachineinfo() + "_" + busiType + ".fsn";
	}

	/**
	 * 获取业务类型
	 * 
	 * @param cmd
	 * @return
	 */
	private static String getBusiType(String cmd, String codeMsg) {

		String ref = cmd;

		try {
			if ("ATM".equals(cmd) || "CAQK".equals(cmd) || "CACK".equals(cmd)) {

				if (Utils.isNullOrEmpty(codeMsg))
					ref += "_" + codeMsg;
			}

		} catch (Exception e) {
			ref = "HM";
		}

		return ref;
	}

	/**
	 * 获取业务类型
	 * 
	 * @param cmd
	 * @return
	 */
	public static String getBusiType(byte[] cmd, String codeMsg) {

		String ref = null;

		try {
			switch (cmd[1]) {
			case (byte) HM_busiType:
				ref = "HM";
				break;
			case (byte) FK_busiType:
				ref = "FK";
				break;
			case (byte) SK_busiType:
				ref = "SK";
				break;
			case (byte) QK_busiType:
				ref = "QK";
				break;
			case (byte) CK_busiType:
				ref = "CK";
				break;
			case (byte) ATM_busiType:
				ref = "ATM";
				if (Utils.isNullOrEmpty(codeMsg))
					break;
				ref += "_" + codeMsg;
				break;
			case (byte) CAQK_busiType:
				ref = "CAQK";
				if (Utils.isNullOrEmpty(codeMsg))
					break;
				ref += "_" + codeMsg;
				break;
			case (byte) CACK_busiType:
				ref = "CACK";
				if (Utils.isNullOrEmpty(codeMsg))
					break;
				ref += "_" + codeMsg;
				break;
			default:
				ref = "HM";
				break;
			}
		} catch (Exception e) {
			ref = "HM";
		}

		return ref;
	}

	/**
	 * 获取fsn文件头
	 * 
	 * @return
	 */
	public static ByteBuffer getStruct(int count) {
		ByteBuffer bb = ByteBuffer.allocate(32);

		byte[] HeadStart = getHeadStart();
		byte[] HeadString = getHeadString();
		byte[] Counter = getCounter(count);
		byte[] HeadEnd = getHeadEnd();

		bb.put(HeadStart);
		bb.put(HeadString);
		bb.put(Counter);
		bb.put(HeadEnd);

		bb.flip();

		return bb;
	}

	public static byte[] intToDWord(int parValue) {
		byte retValue[] = new byte[4];
		retValue[0] = (byte) (parValue & 0x00FF);
		retValue[1] = (byte) ((parValue >> 8) & 0x000000FF);
		retValue[2] = (byte) ((parValue >> 16) & 0x000000FF);
		retValue[3] = (byte) ((parValue >> 24) & 0x000000FF);
		return (retValue);
	}

	private static byte[] getHeadStart() {
		byte[] HeadStart = new byte[] { 0x14, 0x00, 0x0a, 0x00, 0x07, 0x00, 0x1a, 0x00 };
		return HeadStart;
	}

	private static byte[] getHeadString() {
		byte[] HeadString = new byte[] { 0x00, 0x00, 0x01, 0x00, 0x2E, 0x00, 0x53, 0x00, 0x4E, 0x00, 0x6F, 0x00 };

		return HeadString;
	}

	public static byte[] getCounter(int count) {
		return intToDWord(count);
	}

	private static byte[] getHeadEnd() {
		byte[] HeadEnd = new byte[] { 0x00, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00 };
		return HeadEnd;
	}

	/**
	 * 江南银行
	 * 
	 * @param fileName
	 * @param suffix
	 * @return
	 */
	public static boolean jnRemoveSuffix(String fileName, Date dt, String seq) {
		Log.Info("jnRemoveSuffix " + "fileName: " + fileName + "seq: " + seq);
		boolean result = false;
		// 文件名为空
		if (Utils.isNullOrEmpty(fileName)) {
			Log.Info("TcpServer", "fileName: null," + "seq: " + seq);
			return false;
		}
		try {

			File oldFile = new File(fileName);

			// 文件可读,rename && oldFile.canWrite()
			if (oldFile.exists()) {
				// result = "begin rename file: " + fileName;
				// Log.Info("TcpServer", result);
				String rootPath = oldFile.getParent();
				// 新文件名
				String strDt = "";
				try {
					strDt = new SimpleDateFormat("yyyyMMddHHmmss").format(dt);
				} catch (Exception e) {
					// TODO: handle exception
					strDt = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
					Log.Info("TcpServer", "error: time is " + dt);
				}

				Log.Info("TcpServer", "time is " + dt);

				String newFileName = rootPath + File.separator + "CNY15_" + strDt + oldFile.getName().substring(20)
						.replace(".fsn.tmp", (Utils.isNullOrEmpty(seq) ? "" : ("_" + seq)) + ".fsn");

				File newFile = new File(newFileName);

				try {
					// 复制文件
					org.apache.commons.io.FileUtils.copyFile(oldFile, newFile);

					// 删除旧文件
					oldFile.delete();

					// Log.Info("rename " + oldFile.getName() + " success");
					result = true;

				} catch (Exception e) {
					// 文件重命名失败
					Log.Info("rename " + oldFile.getName() + " false");
					// result = "rename " + oldFile.getName() + " false";
				}
			} else {
				// result = "file is not esxet";
				Log.Info("TcpServer", "file is not esxet");
			}

		} catch (Exception e) {
			// return "异常位置2:" +e.getMessage();
			Log.Info(e.getMessage());
		}

		return result;
	}

	/**
	 * 江南银行
	 * 
	 * @param fileName
	 * @param suffix
	 * @return
	 */
	public static boolean jnRemoveSuffix1(String fileName, Date dt, String seq) {
		Log.Info("jnRemoveSuffix1 " + "fileName: " + fileName + "seq: " + seq);
		boolean result = false;
		// 文件名为空
		if (Utils.isNullOrEmpty(fileName)) {
			Log.Info("TcpServer", "fileName: null," + "seq: " + seq);
			return false;
		}
		try {

			File oldFile = new File(fileName);

			// 文件可读,rename && oldFile.canWrite()
			if (oldFile.exists()) {
				// result = "begin rename file: " + fileName;
				// Log.Info("TcpServer", result);
				String rootPath = oldFile.getParent();
				// 新文件名
				String strDt = "";
				try {
					strDt = new SimpleDateFormat("yyyyMMddHHmmss").format(dt);
				} catch (Exception e) {
					// TODO: handle exception
					strDt = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
					Log.Info("TcpServer", "error: time is " + dt);
				}

				Log.Info("TcpServer", "time is " + dt);

				String tmp = oldFile.getName().substring(20);

				String tmpBusiType = tmp.split("\\.")[0].split("_")[3];

				Log.Info("TcpServer", "tmpBusiType : " + tmpBusiType);

				Log.Info("TcpServer", "tmp1 : " + tmp);

				tmp = tmp.replace(tmpBusiType, "HM");

				Log.Info("TcpServer", "tmp2 : " + tmp);

				String newFileName = rootPath + File.separator + "CNY15_" + strDt
						+ tmp.replace(".fsn.tmp", (Utils.isNullOrEmpty(seq) ? "" : ("_" + seq)) + ".fsn");

				File newFile = new File(newFileName);

				try {
					// 复制文件
					org.apache.commons.io.FileUtils.copyFile(oldFile, newFile);

					// 删除旧文件
					oldFile.delete();

					// Log.Info("rename " + oldFile.getName() + " success");
					result = true;

				} catch (Exception e) {
					// 文件重命名失败
					Log.Info("rename " + oldFile.getName() + " false");
					// result = "rename " + oldFile.getName() + " false";
				}
			} else {
				// result = "file is not esxet";
				Log.Info("TcpServer", "file is not esxet");
			}

		} catch (Exception e) {
			// return "异常位置2:" +e.getMessage();
			Log.Info(e.getMessage());
		}

		return result;
	}

	/**
	 * 江南银行
	 * 
	 * @param fileName
	 * @param suffix
	 * @return
	 */
	public static boolean jnRemoveSuffix2(String fileName, Date dt, String seq) {
		Log.Info("jnRemoveSuffix2 " + "fileName: " + fileName + "seq: " + seq);
		boolean result = false;
		// 文件名为空
		if (Utils.isNullOrEmpty(fileName)) {
			Log.Info("TcpServer", "fileName: null," + "seq: " + seq);
			return false;
		}
		try {

			File oldFile = new File(fileName);

			// 文件可读,rename && oldFile.canWrite()
			if (oldFile.exists()) {
				// result = "begin rename file: " + fileName;
				// Log.Info("TcpServer", result);
				String rootPath = oldFile.getParent();
				// 新文件名
				String strDt = "";
				try {
					strDt = new SimpleDateFormat("yyyyMMddHHmmss").format(dt);
				} catch (Exception e) {
					// TODO: handle exception
					strDt = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
					Log.Info("TcpServer", "error: time is " + dt);
				}

				Log.Info("TcpServer", "time is " + dt);

				String tmp = oldFile.getName().substring(20);

				// String tmpBusiType = tmp.split("\\.")[0].split("_")[3];
				//
				// tmp = tmp.replace(tmpBusiType, "HM");

				String newFileName = rootPath + File.separator + "CNY15_" + strDt
						+ tmp.replace(".fsn.tmp", (Utils.isNullOrEmpty(seq) ? "" : ("_" + seq)) + ".fsn");

				File newFile = new File(newFileName);

				try {
					// 复制文件
					org.apache.commons.io.FileUtils.copyFile(oldFile, newFile);

					// 删除旧文件
					oldFile.delete();

					// Log.Info("rename " + oldFile.getName() + " success");
					result = true;

				} catch (Exception e) {
					// 文件重命名失败
					Log.Info("rename " + oldFile.getName() + " false");
					// result = "rename " + oldFile.getName() + " false";
				}
			} else {
				// result = "file is not esxet";
				Log.Info("TcpServer", "file is not esxet");
			}

		} catch (Exception e) {
			// return "异常位置2:" +e.getMessage();
			Log.Info(e.getMessage());
		}

		return result;
	}

	/**
	 * 江南银行
	 * 
	 * @param fileName
	 * @param suffix
	 * @return
	 */
	public static boolean jnRemoveSuffix3(String fileName) {
		while(!RealTAction.isWriteFinished()){
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
			}
		}
		Log.Info("jnRemoveSuffix " + "fileName: " + fileName);
		boolean result = false;
		// 文件名为空
		if (Utils.isNullOrEmpty(fileName)) {
			Log.Info("TcpServer", "fileName: null");
			return false;
		}
		try {

			File oldFile = new File(fileName);

			// 文件可读,rename && oldFile.canWrite()
			if (oldFile.exists()) {
				// result = "begin rename file: " + fileName;
				// Log.Info("TcpServer", result);
				String rootPath = oldFile.getParent();
				// 新文件名

				String tmp = oldFile.getName().substring(6);

				String newFileName = rootPath + File.separator + "CNY15_" + tmp.replace(".fsn.tmp", ".fsn");

				File newFile = new File(newFileName);

				try {
					// 复制文件
					org.apache.commons.io.FileUtils.copyFile(oldFile, newFile);

					// 删除旧文件
					oldFile.delete();

					// Log.Info("rename " + oldFile.getName() + " success");
					result = true;

				} catch (Exception e) {
					// 文件重命名失败
					Log.Info("rename " + oldFile.getName() + " false");
					// result = "rename " + oldFile.getName() + " false";
				}
			} else {
				// result = "file is not esxet";
				Log.Info("TcpServer", "file is not esxet");
			}

		} catch (Exception e) {
			// return "异常位置2:" +e.getMessage();
			Log.Info(e.getMessage());
		}

		return result;
	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean delFile(String fileName) {
		boolean result = false;
		try {

			File file = new File(fileName);

			// 删除文件
			if (file.exists()) {
				if (file.canWrite()) {
					try {
						if (file.delete()) {
							Log.Info("TcpServer", "文件" + file.getName() + "删除成功");
							result = true;
						} else {
							Log.Info("TcpServer", "文件" + file.getName() + "删除失败");
						}
					} catch (Exception e) {
					}
				} else {
					Log.Info("TcpServer", "文件" + file.getName() + "正在被占用");
				}
			} else {
				Log.Info("TcpServer", "文件" + file.getName() + "不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
