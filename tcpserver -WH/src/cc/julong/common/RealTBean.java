package cc.julong.common;

import java.util.Date;

/**
 * 实时采集Bean
 * 
 * @author xp
 * 
 */
public class RealTBean {

  private String mach; // 设备号
  private String machType; // 设备类型
  private String bankNo; // 机构编号
  private String fileName; // 实时采集时设备已生成的文件名
  private int recordCount; // 文件记录数
  private String busiType; // 实时采集时业务类型

  // 金库实时采集用
  private String ip; // 打印机ip
  private int port; // 打印机端口号

  // 画面用
  private String atmNo; // atm编号
  private String boxNo; // 钞箱编号
  private String operNo; // 操作员编号
  private String counterNo;// 柜台号
  private int fileMaxRecord;// 单个文件最大记录数
  
  private Date dtTime;
  
  private String bizId;	//业务id

  /** 用户名 */
  private String user_name;

  /**
   * @return ip
   */
  public String getIp() {
    return ip;
  }

  /**
   * @param ip
   *          要设置的 ip
   */
  public void setIp(String ip) {
    this.ip = ip;
  }

  /**
   * @return port
   */
  public int getPort() {
    return port;
  }

  /**
   * @param port
   *          要设置的 port
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
   * @return atmNo
   */
  public String getAtmNo() {
    return atmNo;
  }

  /**
   * @param atmNo
   *          要设置的 atmNo
   */
  public void setAtmNo(String atmNo) {
    this.atmNo = atmNo;
  }

  /**
   * @return boxNo
   */
  public String getBoxNo() {
    return boxNo;
  }

  /**
   * @param boxNo
   *          要设置的 boxNo
   */
  public void setBoxNo(String boxNo) {
    this.boxNo = boxNo;
  }

  /**
   * @return operNo
   */
  public String getOperNo() {
    return operNo;
  }

  /**
   * @param operNo
   *          要设置的 operNo
   */
  public void setOperNo(String operNo) {
    this.operNo = operNo;
  }

  public String getCounterNo() {
    return counterNo;
  }

  public void setCounterNo(String counterNo) {
    this.counterNo = counterNo;
  }

  /**
   * @return recordCount
   */
  public int getRecordCount() {
    return recordCount;
  }

  /**
   * @param recordCount
   *          要设置的 recordCount
   */
  public void setRecordCount(int recordCount) {
    this.recordCount = recordCount;
  }

  /**
   * @return busiType
   */
  public String getBusiType() {
    return busiType;
  }

  /**
   * @param busiType
   *          要设置的 busiType
   */
  public void setBusiType(String busiType) {
    this.busiType = busiType;
  }

  /**
   * @return mach
   */
  public String getMach() {
    return mach;
  }

  /**
   * @param mach
   *          要设置的 mach
   */
  public void setMach(String mach) {
    this.mach = mach;
  }

  /**
   * @return machType
   */
  public String getMachType() {
    return machType;
  }

  /**
   * @param machType
   *          要设置的 machType
   */
  public void setMachType(String machType) {
    this.machType = machType;
  }

  /**
   * @return bankNo
   */
  public String getBankNo() {
    return bankNo;
  }

  /**
   * @param bankNo
   *          要设置的 bankNo
   */
  public void setBankNo(String bankNo) {
    this.bankNo = bankNo;
  }

  /**
   * @return fileName
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * @param fileName
   *          要设置的 fileName
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * @return fileMaxRecord
   */
  public int getFileMaxRecord() {
    return fileMaxRecord;
  }

  /**
   * @param fileMaxRecord
   *          要设置的 fileMaxRecord
   */
  public void setFileMaxRecord(int fileMaxRecord) {
    this.fileMaxRecord = fileMaxRecord;
  }

  /**
   * @return user_name
   */
  public String getUser_name() {
    return user_name;
  }

  /**
   * @param user_name
   *          要设置的 user_name
   */
  public void setUser_name(String user_name) {
    this.user_name = user_name;
  }

  public Date getDtTime() {
	return dtTime;
}

public void setDtTime(Date dtTime) {
	this.dtTime = dtTime;
}

public String getBizId() {
	return bizId;
}

public void setBizId(String bizId) {
	this.bizId = bizId;
}

public static void main(String[] args) {
    // FIXME 自动生成的方法存根

  }

}
