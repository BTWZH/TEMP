package cc.julong.transfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class transferFactory {

	private static final String TRANS_GATHER_NAME = "采集"; // 采集进程
	private static final String TRANS_IMPORT_NAME = "导入"; // 导入进程
	private static final String TRANS_EXPORT_NAME = "导出"; // 导出进程
	private static final String TRANS_REPORT_NAME = "上报"; // 上报进程
	private static final String TRANS_DELETE_NAME = "清除"; // 清除进程

	/**
	 * 已正确完成数+1
	 * 
	 * @param value
	 */
	public static void setDoneCountAdd(String value) {

		setDoneCountAdd(value, 1);
	}

	/**
	 * 已正确完成数+n
	 * 
	 * @param value
	 */
	public static void setDoneCountAdd(String value, int delta) {
		// 兼容1.6 lbf 2014-8-22
		List<String> arrLst = Arrays.asList(TRANS_GATHER_NAME,
				TRANS_IMPORT_NAME, TRANS_EXPORT_NAME, TRANS_REPORT_NAME,
				TRANS_DELETE_NAME);
		int arrIndex = arrLst.indexOf(value);

		if (0 == arrIndex) {
			GatHerTransfer.addGatHerDoneCount(delta);
		} else if (1 == arrIndex) {
			ImportTransfer.addImportDoneCount(delta);
		} else if (2 == arrIndex) {
			ExportTransfer.addExportDoneCount(delta);
		} else if (3 == arrIndex) {
			ReportTransfer.addReportDoneCount(delta);
		} else if (4 == arrIndex) {
			DeleteTransfer.addDeleteDoneCount(delta);
		}

	}

	/**
	 * 获取已正确完成数
	 * 
	 * @param value
	 */
	public static int getDoneCount(String value) {
		int result = 0;
		List<String> arrLst = Arrays.asList(TRANS_GATHER_NAME,
				TRANS_IMPORT_NAME, TRANS_EXPORT_NAME, TRANS_REPORT_NAME,
				TRANS_DELETE_NAME);
		int arrIndex = arrLst.indexOf(value);
		if (0 == arrIndex) {
			result = GatHerTransfer.getGatHerDoneCount();
		} else if (1 == arrIndex) {
			result = ImportTransfer.getImportDoneCount();
		} else if (2 == arrIndex) {
			result = ExportTransfer.getExportDoneCount();
		} else if (3 == arrIndex) {
			result = ReportTransfer.getReportDoneCount();
		} else if (4 == arrIndex) {
			result = DeleteTransfer.getDeleteDoneCount();
		}

		return result;
	}

	/**
	 * 未正确完成数+1
	 * 
	 * @param value
	 */
	public static void setErrDoneCountAdd(String value) {

		setErrDoneCountAdd(value, 1);
	}

	/**
	 * 未正确完成数+n
	 * 
	 * @param value
	 */
	public static void setErrDoneCountAdd(String value, int delta) {

		List<String> arrLst = Arrays.asList(TRANS_GATHER_NAME,
				TRANS_IMPORT_NAME, TRANS_EXPORT_NAME, TRANS_REPORT_NAME,
				TRANS_DELETE_NAME);
		int arrIndex = arrLst.indexOf(value);

		if (0 == arrIndex) {
			GatHerTransfer.addGatHerErrDoneCount(delta);
		} else if (1 == arrIndex) {
			ImportTransfer.addImportErrDoneCount(delta);
		} else if (2 == arrIndex) {
			ExportTransfer.addExportErrDoneCount(delta);
		} else if (3 == arrIndex) {
			ReportTransfer.addReportErrDoneCount(delta);
		} else if (4 == arrIndex) {
			DeleteTransfer.addDeleteErrDoneCount(delta);
		}

	}

	/**
	 * 获取未正确完成数
	 * 
	 * @param value
	 */
	public static int getErrDoneCount(String value) {
		int result = 0;
		List<String> arrLst = Arrays.asList(TRANS_GATHER_NAME,
				TRANS_IMPORT_NAME, TRANS_EXPORT_NAME, TRANS_REPORT_NAME,
				TRANS_DELETE_NAME);
		int arrIndex = arrLst.indexOf(value);

		if (0 == arrIndex) {
			result = GatHerTransfer.getGatHerErrDoneCount();
		} else if (1 == arrIndex) {
			result = ImportTransfer.getImportErrDoneCount();
		} else if (2 == arrIndex) {
			result = ExportTransfer.getExportErrDoneCount();
		} else if (3 == arrIndex) {
			result = ReportTransfer.getReportErrDoneCount();
		} else if (4 == arrIndex) {
			result = DeleteTransfer.getDeleteErrDoneCount();
		}

		return result;
	}

	/**
	 * 待完成数+1
	 * 
	 * @param value
	 */
	public static void setUnDoneCountAdd(String name, int value) {
		List<String> arrLst = Arrays.asList(TRANS_GATHER_NAME,
				TRANS_IMPORT_NAME, TRANS_EXPORT_NAME, TRANS_REPORT_NAME,
				TRANS_DELETE_NAME);
		int arrIndex = arrLst.indexOf(name);

		if (0 == arrIndex) {
			GatHerTransfer.setGatHerUnDoneCount(value);
		} else if (1 == arrIndex) {
			ImportTransfer.setImportUnDoneCount(value);
		} else if (2 == arrIndex) {
			ExportTransfer.setExportUnDoneCount(value);
		} else if (3 == arrIndex) {
			ReportTransfer.setReportUnDoneCount(value);
		} else if (4 == arrIndex) {
			DeleteTransfer.setDeleteUnDoneCount(value);
		}

	}

	/**
	 * 获取待完成数
	 * 
	 * @param value
	 */
	public static int getUnDoneCount(String value) {
		int result = 0;
		// 兼容1.6 lbf 2014-8-22
		List<String> arrLst = Arrays.asList(TRANS_GATHER_NAME,
				TRANS_IMPORT_NAME, TRANS_EXPORT_NAME, TRANS_REPORT_NAME,
				TRANS_DELETE_NAME);
		int arrIndex = arrLst.indexOf(value);

		if (0 == arrIndex) {
			result = GatHerTransfer.getGatHerUnDoneCount();
		} else if (1 == arrIndex) {
			result = ImportTransfer.getImportUnDoneCount();
		} else if (2 == arrIndex) {
			result = ExportTransfer.getExportUnDoneCount();
		} else if (3 == arrIndex) {
			result = ReportTransfer.getReportUnDoneCount();
		} else if (4 == arrIndex) {
			result = DeleteTransfer.getDeleteUnDoneCount();
		}

		return result;
	}

	/**
	 * 设置错误信息
	 * 
	 * @param value
	 */
	public static void setErrorMsg(String value, String msg) {
		// 兼容1.6 lbf 2014-8-22
		List<String> arrLst = Arrays.asList(TRANS_GATHER_NAME,
				TRANS_IMPORT_NAME, TRANS_EXPORT_NAME, TRANS_REPORT_NAME,
				TRANS_DELETE_NAME);
		int arrIndex = arrLst.indexOf(value);

		if (0 == arrIndex) {
			GatHerTransfer.addErrMsg(msg);
		} else if (1 == arrIndex) {
			ImportTransfer.addErrMsg(msg);
		} else if (2 == arrIndex) {
			ExportTransfer.addErrMsg(msg);
		} else if (3 == arrIndex) {
			ReportTransfer.addErrMsg(msg);
		} else if (4 == arrIndex) {
			DeleteTransfer.addErrMsg(msg);
		}

	}

	/**
	 * 重置页面上的数值
	 */
	public static void resetAll() {

		GatHerTransfer.ErrMsgClear();
		ImportTransfer.ErrMsgClear();
		ExportTransfer.ErrMsgClear();
		ReportTransfer.ErrMsgClear();
		DeleteTransfer.ErrMsgClear();

		GatHerTransfer.setGatHerUnDoneCount(0);
		GatHerTransfer.setGatHerErrDoneCount(0);
		GatHerTransfer.setGatHerDoneCount(0);

		ImportTransfer.setImportUnDoneCount(0);
		ImportTransfer.setImportErrDoneCount(0);
		ImportTransfer.setImportDoneCount(0);

		ExportTransfer.setExportUnDoneCount(0);
		ExportTransfer.setExportErrDoneCount(0);
		ExportTransfer.setExportDoneCount(0);

		ReportTransfer.setReportUnDoneCount(0);
		ReportTransfer.setReportErrDoneCount(0);
		ReportTransfer.setReportDoneCount(0);

		DeleteTransfer.setDeleteUnDoneCount(0);
		DeleteTransfer.setDeleteErrDoneCount(0);
		DeleteTransfer.setDeleteDoneCount(0);
	}

	/**
	 * 获取进程启动状态
	 */
	public static ConcurrentHashMap<String, String> getRunningStat(
			List<String> lst) {

		// 记录BIOM错误数的集合
		ConcurrentHashMap<String, String> RunningStatMap = new ConcurrentHashMap<String, String>();

		for (String str : lst) {
			RunningStatMap.put(str, getPropValue(str));
		}

		return RunningStatMap;
	}

	/**
	 * 获取properties配置文件参数
	 */
	private static String getPropValue(String key) {

		String value = null;
		Properties prop = new Properties();

		String proPath = transferFactory.class.getResource("/").getPath();

		String websiteURL = (proPath.replace("/build/classes", "")
				.replace("%20", " ").replace("classes/", "") + "ccms.conf")
				.replaceFirst("/", "");

		System.out.println("配置文件路径 :" + websiteURL);

		File file = new File(websiteURL);

		try {

			InputStream in = new FileInputStream(file);

			prop.load(in);

			value = prop.getProperty(key);

			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return value;
	}

	public static void main(String[] args) {

		setUnDoneCountAdd(TransferType.GATHER.toString(), 3);

		System.out.println(GatHerTransfer.getGatHerUnDoneCount());
	}
}
