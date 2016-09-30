package cc.julong.ftp;

import cc.julong.common.InitParaBean;
import cc.julong.common.RealTAction;
import cc.julong.log.Log;
import cc.julong.server.util.Utils;

/**
 * ����FTP�ϴ�����
 * 
 * @author julong.xp date:2014-09-29
 */
public class UpLoadThread extends Thread {

	/** һ���� */
	private static final int MINUTE = 1000 * 60;
	/** ÿ��FTP�ϴ����ʱ�� */
	private static int TIME = 5 * MINUTE;
	private boolean isRun = true;
	private boolean isStop = false;

	@Override
	public void run() {
		long timer;
		UpLoadCore core = new UpLoadCore(this);

		while (isRun) {

			// ����
			timer = System.currentTimeMillis();

			try {

				// tmp文件2分钟未操作,则自动转成fsn文件
				Utils.removeFileWith2min(RealTAction.getInitBean().getFilesRoot());

				// 是否创建目录
				if (UpLoadFTP.isCreateDirAuto()) {
					core.rootByMap();
				} else {
					core.rootByList();
				}

				Log.Info("*** this time over ***");
				timer = TIME - (System.currentTimeMillis() - timer);

				if (timer > 999) {
					try {
						Log.Info("sleep" + timer / MINUTE + "minute");
						Thread.sleep(timer);
					} catch (InterruptedException e) {
						Log.Info("UpLoad thread sleep error," + e.toString());
					}
				}
			} catch (Exception e) {
				Log.Info("UpLoad thread error," + e.toString());

				timer = TIME - (System.currentTimeMillis() - timer);
				if (timer > 999) {
					try {
						Log.Info("sleep" + timer / MINUTE + "minute" + timer % MINUTE + "sound");
						Thread.sleep(timer);
					} catch (InterruptedException e1) {
						Log.Info("UpLoad thread sleep error," + e1.toString());
					}
				}

				continue;
			}
		}
		isStop = true;
		Log.Info("Upload", "upload stop");
	}

	/**
	 * �˴�Ϊ��ʱ�ϴ�FSN�ļ���FTP�������Ŀɶ������
	 * 
	 * @param time
	 *            �ϴ�FSN�ļ���ѯ�������λ������
	 * @param fileRoot
	 *            ԭʼ�ļ���Ÿ�·��
	 * @param ip
	 *            FTP��������IP
	 * @param port
	 *            FTP�������Ķ˿ں�
	 * @param user
	 *            FTP���������û���
	 * @param pwd
	 *            FTP������������
	 * @param uploadPath
	 *            �ϴ��ļ���Ÿ�·��
	 * @param childPath
	 *            �Ƿ��Զ�������·��
	 */
	public void init(InitParaBean init) {

		// ��λ����
		int num = init.getFtpInterval() * 60 * 1000;
		UpLoadThread.setTime(num);

		// �ļ��ϴ�Ŀ¼
		UpLoadCore.setFileRoot(init.getFilesRoot());

		UpLoadFTP.setFtpServerIp(init.getFtpServerIp());
		UpLoadFTP.setFtpServerPort(init.getFtpServerPort());
		UpLoadFTP.setUser(init.getFtpUser());
		UpLoadFTP.setPwd(init.getFtpPassWord());

		UpLoadFTP.setFtpRootPath(init.getFtpRootPath());
		UpLoadFTP.setCreateDirAuto(init.isCreateDirAuto());
		UpLoadFTP.setSERVER_CHARSET(init.getServerCharset());

		Log.Info("FilesRoot: " + init.getFilesRoot());
		Log.Info("FtpServerIp : " + init.getFtpServerIp());
		Log.Info("FtpServerPort : " + init.getFtpServerPort());
		Log.Info("FtpUser: " + init.getFtpUser());
		Log.Info("FtpPassWord : " + init.getFtpPassWord());
		Log.Info("FtpRootPath: " + init.getFtpRootPath());
		Log.Info("isCreateDirAuto : " + (init.isCreateDirAuto() ? "true" : "false"));
		Log.Info("FtpInterval : " + init.getFtpInterval() + "m");
		Log.Info("bankCode : " + init.getBankCode());
		Log.Info("serverCharSet : " + init.getServerCharset());

	}

	/**
	 * ��ȡÿ��FTP�ϴ����ʱ��
	 */
	protected static int getTime() {
		return TIME;
	}

	/**
	 * ����ÿ��FTP�ϴ����ʱ��
	 */
	protected static void setTime(int time) {
		UpLoadThread.TIME = time;
	}

	/**
	 * ��ȡFTP�Ƿ�ر�
	 */
	protected boolean isStop() {
		return isStop;
	}

	/**
	 * ��ȡFTP����������״̬
	 */
	protected boolean isRun() {
		return isRun;
	}

	/**
	 * ����FTP�����Ƿ�����
	 */
	protected void setRun(boolean isRun) {
		this.isRun = isRun;
	}
}
