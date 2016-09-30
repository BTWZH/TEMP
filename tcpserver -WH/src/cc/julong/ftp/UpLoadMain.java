package cc.julong.ftp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import cc.julong.common.InitParaBean;
import cc.julong.log.Log;

public class UpLoadMain {

  private UpLoadThread upLoadThread;

  public UpLoadThread getUpLoadThread() {
    return upLoadThread;
  }

  public void setUpLoadThread(UpLoadThread upLoadThread) {
    this.upLoadThread = upLoadThread;
  }

  /**
   * �˴�Ϊ��ʱ�ϴ�FSN�ļ���FTP�������Ŀɶ������
   * 
   * @param time
   *          �ϴ�FSN�ļ���ѯ�������λ������
   * @param fileRoot
   *          ԭʼ�ļ���Ÿ�·��
   * @param ip
   *          FTP��������IP
   * @param port
   *          FTP�������Ķ˿ں�
   * @param user
   *          FTP���������û���
   * @param pwd
   *          FTP������������
   * @param uploadPath
   *          �ϴ��ļ���Ÿ�·��
   * @param childPath
   *          �Ƿ��Զ�������·��
   */
  public void init(InitParaBean init) {

    Log.Info("Upload", "uploadd init");

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

//    Log.Info("Upload", "�ϱ��ı���·�� : " + init.getFilesRoot());
//    Log.Info("Upload", "FTP������IP��ַ : " + init.getFtpServerIp());
//    Log.Info("Upload", "FTP�������˿ں� : " + init.getFtpServerPort());
//    Log.Info("Upload", "FTP�������û���: " + init.getFtpUser());
//    Log.Info("Upload", "FTP���������� : " + init.getFtpPassWord());
//    Log.Info("Upload", "FTP��������Ŀ¼: " + init.getFtpRootPath());
//    Log.Info("Upload", "�Ƿ񴴽�Ŀ¼�ṹ : " + (init.isCreateDirAuto() ? "����" : "������"));
//    Log.Info("Upload", "ÿ���ϱ��ļ��ʱ�� : " + init.getFtpInterval() + "����");

  }

  // ���� FTP����
  public String testFTP() {
    return new UpLoadFTP().test();
  }

  // ���� FTP UpLoad ����
  public void start(InitParaBean init) {

    init(init);
    upLoadThread.start();
  }

  /**
   * �ж϶˿��Ƿ�ռ��
   * 
   * @param port
   *          �˿ں�
   * @return true:�˿ڱ�ռ��,false:�˿�δ��ռ��
   */
  private static boolean isLoclePortUsing(int port) {
    boolean flag = true;
    try {
      flag = isPortUsing("127.0.0.1", port);
    } catch (Exception e) {
    }
    return flag;
  }

  private static boolean isPortUsing(String host, int port)
      throws UnknownHostException {
    boolean flag = false;
    InetAddress theAddress = InetAddress.getByName(host);
    try {
      @SuppressWarnings({ "unused", "resource" })
      Socket socket = new Socket(theAddress, port);
      flag = true;
    } catch (IOException e) {
    }
    return flag;
  }

  // ֹͣ FTP UpLoad ����
  public void stop() {
    upLoadThread.setRun(false);
    while (!upLoadThread.isStop()) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  // *************************************************/

  public static void main(String[] args) {
    UpLoadMain main = new UpLoadMain();
    // main.start();

  }

}
