package cc.julong.server.state;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * �˻����
 * 
 * @author zengming
 * 
 */
public class DataInfo {

    private List<TraceLog> traces;// ��Ųɼ���Ϣ

    private String ip;// �豸iP�˿�

    private Date beginDate = new Date();// ��ʼʱ��

    private Date endDate;// ����ʱ��

    private String machineType; // ��������

    private Map<String, Object> parms = new HashMap<String, Object>();

    /**
     * �����š�������Ϣ
     */
    private String machineinfo;

    private String bankNo;

    private String busiType; // ҵ������

    private String codeMsg; // ҵ����Ϣ

    private String fileName; // �ļ����

    private String filePath; // �ļ��洢·��

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCodeMsg() {
        return codeMsg;
    }

    public void setCodeMsg(String codemsg) {
        this.codeMsg = codemsg;
    }

    /**
     * @return busiType
     */
    public String getBusiType() {
        return busiType;
    }

    /**
     * @param busiType
     *            Ҫ���õ� busiType
     */
    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public Map<String, Object> getParms() {
        return parms;
    }

    public void setParms(Map<String, Object> parms) {
        this.parms = parms;
    }

    public List<TraceLog> getTrace() {
        return traces;
    }

    public void addTrace(TraceLog trace) {
        traces.add(trace);
    }

    public void addTrace(String msg, TraceState stat) {
        traces.add(new TraceLog(new Date(), msg, stat));
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMachineinfo() {
        return machineinfo;
    }

    public void setMachineinfo(String machineinfo) {
        this.machineinfo = machineinfo;
    }

}
