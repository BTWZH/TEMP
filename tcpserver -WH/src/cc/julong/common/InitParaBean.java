package cc.julong.common;

/**
 * 采集与FTP上传功能初始化参数
 * 
 * @author julong.xp date:2014-09-29
 */
public class InitParaBean {

	// 共通参数
	private String filesRoot;// 本地文件存放根目录
	private String bankCode;// 网点编号
	private String serverCharset;// 服务器字符集
	// FTP参数
	private String ftpServerIp;// FTP服务器IP地址
	private int ftpServerPort;// FTP服务器端口号
	private String ftpUser; // FTP登陆用户名
	private String ftpPassWord;// FTP登陆密码
	private int ftpInterval; // FTP上传时间间隔
	private String ftpRootPath;// 服务器上文件存放根路径
	private boolean createDirAuto;// 是否在服务器上创建子路径
	private boolean isPassive;// FTP主被动模式
	// TCP采集参数
	private int collectPort;// TCP采集端口
	
	
	/**
	 * @return 本地文件存放根目录
	 */
	public String getFilesRoot() {
		return filesRoot;
	}

	/**
	 * @param filesRoot
	 *            本地文件存放根目录 to set
	 */
	public void setFilesRoot(String filesRoot) {
		this.filesRoot = filesRoot;
	}

	/**
	 * @return FTP服务器IP地址
	 */
	public String getFtpServerIp() {
		return ftpServerIp;
	}

	/**
	 * @param ftpServerIp
	 *            FTP服务器IP地址 to set
	 */
	public void setFtpServerIp(String ftpServerIp) {
		this.ftpServerIp = ftpServerIp;
	}

	/**
	 * @return FTP服务器端口号
	 */
	public int getFtpServerPort() {
		return ftpServerPort;
	}

	/**
	 * @param ftpServerPort
	 *            FTP服务器端口号 to set
	 */
	public void setFtpServerPort(int ftpServerPort) {
		this.ftpServerPort = ftpServerPort;
	}

	/**
	 * @return FTP登陆用户名
	 */
	public String getFtpUser() {
		return ftpUser;
	}

	/**
	 * @param ftpUser
	 *            FTP登陆用户名 to set
	 */
	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}

	/**
	 * @return FTP登陆密码
	 */
	public String getFtpPassWord() {
		return ftpPassWord;
	}

	/**
	 * @param ftpPassWord
	 *            FTP登陆密码 to set
	 */
	public void setFtpPassWord(String ftpPassWord) {
		this.ftpPassWord = ftpPassWord;
	}

	/**
	 * @return FTP上传时间间隔
	 */
	public int getFtpInterval() {
		return ftpInterval;
	}

	/**
	 * @param ftpInterval
	 *            FTP上传时间间隔 to set
	 */
	public void setFtpInterval(int ftpInterval) {
		this.ftpInterval = ftpInterval;
	}

	/**
	 * @return 服务器上文件存放根路径
	 */
	public String getFtpRootPath() {
		return ftpRootPath;
	}

	/**
	 * @param ftpRootPath
	 *            服务器上文件存放根路径 to set
	 */
	public void setFtpRootPath(String ftpRootPath) {
		this.ftpRootPath = ftpRootPath;
	}

	/**
	 * @return 是否在服务器上创建子路径
	 */
	public boolean isCreateDirAuto() {
		return createDirAuto;
	}

	/**
	 * @param createDirAuto
	 *            是否在服务器上创建子路径 to set
	 */
	public void setCreateDirAuto(boolean createDirAuto) {
		this.createDirAuto = createDirAuto;
	}

	/**
	 * @return FTP主被动模式
	 */
	public boolean isPassive() {
		return isPassive;
	}

	/**
	 * @param isPassive
	 *            FTP主被动模式 to set
	 */
	public void setPassive(boolean isPassive) {
		this.isPassive = isPassive;
	}

	/**
	 * @return TCP采集端口号
	 */
	public int getCollectPort() {
		return collectPort;
	}

	/**
	 * @param collectPort
	 *            TCP采集端口号 to set
	 */
	public void setCollectPort(int collectPort) {
		this.collectPort = collectPort;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getServerCharset() {
		return serverCharset;
	}

	public void setServerCharset(String serverCharset) {
		this.serverCharset = serverCharset;
	}

}
