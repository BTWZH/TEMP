// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 2014/10/8 15:47:08
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   RealTAction.java

package cc.julong.common;

import cc.julong.log.Log;
import cc.julong.server.state.DataInfo;
import cc.julong.server.tcp.julong.JLHandler;
import cc.julong.server.util.FileUtils;
import cc.julong.server.util.Utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

// Referenced classes of package cc.julong.common:
//            InitParaBean, RealTBean
/**
 * 网点与金库实时采集公共接口
 * 
 * @author xp <br>
 *         date: 2014-8-22
 */
public class RealTAction {
	/** 实时采集页面设置的设备号 */
	public static ConcurrentHashMap<String, RealTBean> mapMach = new ConcurrentHashMap<String, RealTBean>();
	public static String machNo = "";// 设备号
	private static InitParaBean initBean;// 初始化bean,永久存在内容中
	private static boolean writeFinished = true;
	private static HashSet<String> hshStruct = new HashSet<String>();// 冠字号去重容器

	private static int amountCount = 0; // 金额
	private static int realAmount = 0; //总点钞金额
	public RealTAction() {
	}

	/**
	 * 蓝标业务开始接口
	 * 
	 * @param bankCode
	 *            机构编号
	 * @param busiType
	 *            业务类型
	 * @param bizId
	 *            业务id
	 * @return
	 */
	public static boolean StartReal(String bankCode, String busiType, String bizId) {
		if (bankCode == null || Utils.isNullOrEmpty(bankCode))
			return false;
		if (busiType == null || Utils.isNullOrEmpty(busiType)) {
			return false;
		}
		// log
		Log.Info("startReal " + " bankCode: " + bankCode + " busiType: " + busiType);

		try {
			realAmount = 0;
			amountCount = 0;
			// 每次业务开始清空冠字号码去重容器
			hshStruct.clear();
			// 每次业务开始清空业务容器
			mapMach.clear();
			setMachNo("");

			// find tmp and up
			File file = new File(getInitBean().getFilesRoot());

			if (file.isDirectory()) {
				LinkedList<File> list = new LinkedList<File>();
				findFiles(file, list);

				for (File file2 : list) {
					if (file2.getName().endsWith(JLHandler.FileSuffix)) {

						FileUtils.jnRemoveSuffix1(file2.getPath(), new Date(), "");
					}
				}

				delFoler(file);
			}

			// 创建业务实例
			RealTBean bean = new RealTBean();
			bean.setBankNo(bankCode);
			bean.setBusiType(busiType);
			bean.setMach("jnbank");
			bean.setMachType("AD");
			bean.setFileMaxRecord(100000);
			bean.setDtTime(new Date());
			bean.setBizId(bizId);

			if (mapMach.containsKey(bean.getMach()) || mapMach.size() > 0) {
				return false;
			}

			machNo = "jnbank";

			// 添加到内存容器中
			mapMach.put(bean.getMach(), bean);

		} catch (Exception e) {
			Log.Info("StartReal Error " + e.getMessage());
		}
		return true;
	}

	/**
	 * 大金额交易接口
	 * 
	 * @param kunNo
	 * @param busiType
	 * @return
	 */
	public static boolean BigAmount(String kunNo, String busiType) {

		Log.Info("BigAmount");

		if (Utils.isNullOrEmpty(initBean.getFilesRoot())) {
			return false;
		}

		try {
			// CNY15_20150914102234_00050001AD_00000000_KHQK.FSN
			String fileName = "CNY15_" + convertDateToString("yyyyMMddHHmmss", new Date()) + "_"
					+ Utils.leftPad(initBean.getBankCode(), 8, '0') + "AD" + "_" + "00000000" + "_" + busiType + ".fsn";

			// 组建文件名
			String rootPath = initBean.getFilesRoot() + "/" + fileName;

			File file = new File(rootPath);

			RandomAccessFile mm = null;

			try {
				mm = new RandomAccessFile(file, "rw");

				mm.seek(mm.length());
				// 写入指定文件内容
				mm.writeBytes("boun_no=" + kunNo + "\r\n");

			} catch (Exception e) {

			} finally {

				try {
					mm.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 格式化日期
	 * 
	 * @param aMask
	 *            ���ڸ�ʽ
	 * @param strDate
	 *            ʱ��
	 * @return ָ�����ڸ�ʽ��Date����ʱ��
	 * @throws ParseException
	 */
	private static String convertDateToString(String aMask, Date date) throws ParseException {
		SimpleDateFormat df = null;
		String strDate = "";

		df = new SimpleDateFormat(aMask);

		strDate = df.format(date);

		return strDate;
	}

	/**
	 * 删除空文件夹
	 * 
	 * @param f
	 */
	private static void delFoler(File f) {
		try {
			if (f.isDirectory() && isFolerNotNull(f)) {
				for (File file : f.listFiles()) {
					try {
						delFoler(file);

						if (file.isDirectory() && !isFolerNotNull(file)) {
							System.out.println(file.getPath());
							file.delete();
						}
					} catch (Exception e) {
						continue;
					}

				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * foler is or not has file
	 * 
	 * @param f
	 * @return
	 */
	private static boolean isFolerNotNull(File f) throws Exception {
		return f.list().length > 0;
	}

	/**
	 * find files
	 * 
	 * @param path
	 *            path of files
	 * @param list
	 *            file list
	 */
	private static void findFiles(File path, LinkedList<File> list) {
		// files
		File[] files;
		try {
			files = path.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					findFiles(file, list);// ���
				} else {
					list.add(file);// �ռ�
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * get amount count
	 * 
	 * @return
	 */
	public static int GetAmountCount() {
		Log.Info("into GetAmountCount ");
		int result = 0;

		try {
			result = amountCount;

			Log.Info("Success go out GetAmountCount :" + result);
			return result;
		} catch (Exception e) {
			Log.Info("Error GetAmountCount :" + result);
			return -1;
		}
	}

	public static void AmountCountAdd(int amount) {
		amountCount += amount;
	}

	/**
	 * 校验金额是否一致
	 * 
	 * @param amount
	 * @return
	 */
	public static boolean IsAmountAccord(String amount) {
		Log.Info("IsAmountAccord " + "amount: " + amount + "amountCount: " + amountCount);
		boolean result = false;

		try {
			if (Integer.parseInt(amount) == amountCount) {
				result = true;
			}
		} catch (Exception e) {

		}

		return result;
	}

	/**
	 * 蓝标结束接口
	 * 
	 * @param seq
	 *            业务流水号
	 * @param dt
	 *            业务时间
	 * @param cardNo
	 *            交易卡号
	 * @param name
	 *            用户姓名
	 * @param oper
	 *            柜员号
	 * @param operName
	 *            柜员
	 * @param amount
	 *            业务金额
	 * @param bizId
	 *            业务id
	 * @param paperNo
	 *            凭证号
	 * @return
	 */
	public static boolean EndReal(String seq, Date dt, String cardNo, String name, String oper, String operName,
			String amount, String bizId, String paperNo) {
		while(!RealTAction.isWriteFinished()){
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
			}
		}
		Log.Info("EndReal " + "seq: " + seq);
		boolean result = false;
		String fileName = "";
		String tmpId = ""; // 业务id

		if (Utils.isNullOrEmpty(seq))
			seq = "0123456789";
		if (Utils.isNullOrEmpty(cardNo))
			cardNo = "0";
		if (Utils.isNullOrEmpty(name))
			name = "0";
		if (Utils.isNullOrEmpty(oper))
			oper = "0";
		if (Utils.isNullOrEmpty(operName))
			operName = "0";
		if (Utils.isNullOrEmpty(amount))
			amount = "0";
		if (Utils.isNullOrEmpty(bizId))
			bizId = "0";

		// 文件的后续名拼接
		String fileLast = cardNo + "$" + name + "$" + paperNo + "_" + oper + "$" + operName + "_0_" + amount;

		try {
			RealTBean bean = (RealTBean) mapMach.get(getMachNo());

			if (bean != null) {
				fileName = bean.getFileName();
				tmpId = bean.getBizId();
			}
			if (Utils.isNullOrEmpty(tmpId) || Utils.isNullOrEmpty(bizId)) {

				result = false;
			} else {
				if (tmpId.equals(bizId)) {
					// 结束接口的业务id与开始接口的业务id一致,表明是同一个业务
					if (Utils.isNullOrEmpty(fileName)) {
						// 本业务没有点钞的情况,创建空文件
						DataInfo info = new DataInfo();
						info.setBankNo(bean.getBankNo());

						if ("FK".equals(bean.getBusiType())) {
							bean.setBusiType("QK");
						} else if ("SK".equals(bean.getBusiType())) {
							bean.setBusiType("CK");
						}

						info.setBusiType(bean.getBusiType());
						info.setFilePath(initBean.getFilesRoot());
						info.setBeginDate(bean.getDtTime());
						info.setMachineinfo("0");
						info.setMachineType("AD");

						fileName = FileUtils.getFileName(info) + JLHandler.FileSuffix;

						try {
							if (!new File(fileName).exists()) {
								new File(fileName).createNewFile();
							}

						} catch (Exception e) {
							// TODO: handle exception
						}
						result = FileUtils.jnRemoveSuffix2(fileName, dt, seq + "_" + fileLast);

					} else {
						// 业务有临时文件
						result = FileUtils.jnRemoveSuffix(fileName, dt, seq + "_" + fileLast);
					}
				} else {
					// 结束接口的业务id与开始接口的业务id不一致,表明不是同一个业务
					result = FileUtils.jnRemoveSuffix1(fileName, dt, "");
				}
			}
			// 删除业务记录
			CancelReal();

		} catch (Exception e) {
			Log.Info(e.getMessage());
		}

		return result;
	}

	/**
	 * 业务存在
	 * 
	 * @return
	 */
	public static boolean isRealData() {
		boolean result = false;
		if (Utils.isNullOrEmpty(getMachNo()) || !mapMach.containsKey(getMachNo()))
			return result;
		try {
			RealTBean bean = (RealTBean) mapMach.get(getMachNo());
			int count = bean.getRecordCount();
			if (count > 0)
				result = true;
		} catch (Exception e) {
			result = false;

		}

		return result;
	}

	/**
	 * 蓝标取消接口
	 * 
	 * @return
	 */
	public static boolean CancelReal() {
		boolean result = false;

		Log.Info("cancel real " + machNo);

		try {
			RealTBean bean = mapMach.get(machNo);

			if (bean == null) {
				return result;
			}

			String fileName = bean.getFileName();

			if (!Utils.isNullOrEmpty(fileName) && new File(fileName).exists()) {
				if (new File(fileName).getName().endsWith(JLHandler.FileSuffix)) {

					FileUtils.jnRemoveSuffix1(new File(fileName).getPath(), new Date(), "");
				}
			}

			mapMach.clear();
			setMachNo("");
			hshStruct.clear();
			amountCount = 0;
			realAmount = 0;
			result = true;
		} catch (Exception e) {

		}

		return result;
	}

	/**
	 * remove key
	 * 
	 * @param key
	 */
	public static void RemoveReal(String key) {
		mapMach.remove(key);
	}

	/**
	 * get key
	 * 
	 * @param key
	 * @param bean
	 */
	public static void PutReal(String key, RealTBean bean) {

		mapMach.put(key, bean);
	}

	public static ConcurrentHashMap<String, RealTBean> getMap() {
		return mapMach;
	}

	public static HashSet<String> getHshStruct() {
		return hshStruct;
	}

	public static void setHshStruct(HashSet<String> hshStruct) {
		RealTAction.hshStruct = hshStruct;
	}

	public static void setMachNo(String mach) {
		machNo = mach;
	}

	public static String getMachNo() {
		return machNo;
	}

	public static void setInitBean(InitParaBean bean) {
		initBean = bean;
	}

	public static InitParaBean getInitBean() {
		return initBean;
	}

	public static int getRealAmount() {
		Log.Info("into GetRealAmount ");
		int result = 0;

		try {
			result = realAmount;

			Log.Info("Success go out GetRealAmount :" + result);
			return result;
		} catch (Exception e) {
			Log.Info("Error GetRealAmount :" + result);
			return -1;
		}
	}
	public static void realAmountCountAdd(int amount) {
		realAmount += amount;
	}

	public static boolean isWriteFinished() {
		return writeFinished;
	}

	public static void setWriteFinished(boolean writeFinished) {
		RealTAction.writeFinished = writeFinished;
	}
	

}
