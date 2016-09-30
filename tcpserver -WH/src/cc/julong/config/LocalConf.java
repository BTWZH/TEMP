package cc.julong.config;

public class LocalConf extends ConfigBase {

	public LocalConf(String propertyFileName) {
		super(propertyFileName);
	}

	/**
	 * 采集端口
	 * 
	 * @return
	 */
	public int getCollectPort() {
		return Integer.valueOf(this.getProperties().getProperty("local.collectPort").trim());
	}

	/**
	 * 是否创建目录
	 * 
	 * @return
	 */
	public boolean getCreateDirAuto() {
		String strVal = this.getProperties().getProperty("local.createDirAuto").toString().trim();
		if (Integer.valueOf(strVal) == 1) {
			return true;

		} else
			return false;

	}

	/**
	 * 采集的fsn文件保存目录
	 * 
	 * @return
	 */
	public String getFilesRoot() {
		String str;
		try {
			str = this.getProperties().getProperty("local.filesRoot").trim();
			str = getChString(str);
		} catch (Exception e) {
			str = "D:/FsnFiles";
		}
		return str;
	}

	/**
	 * ftp轮寻时间
	 * 
	 * @return
	 */
	public int getFtpInterval() {
		return Integer.valueOf(this.getProperties().getProperty("local.ftpInterval").trim());
	}

	/**
	 * 服务器FTP登录密码
	 * 
	 * @return
	 */
	public String getFtpPassWord() {
		String str;
		try {
			str = this.getProperties().getProperty("local.ftpPassWord").trim();
			str = getChString(str);
		} catch (Exception e) {
			str = "ftpuser";
		}
		return str;
	}

	/**
	 * 服务器FTP登录用户名
	 * 
	 * @return
	 */
	public String getFtpUser() {
		String str;
		try {
			str = this.getProperties().getProperty("local.ftpUser").trim();
			str = getChString(str);
		} catch (Exception e) {
			str = "ftpuser";
		}
		return str;
	}

	/**
	 * 服务器FTP 目录所在
	 * 
	 * @return
	 */
	public String getFtpRootPath() {
		String str;
		try {
			str = this.getProperties().getProperty("local.ftpRootPath").trim();
			str = getChString(str);
		} catch (Exception e) {
			str = "/home/ftpuser/julong/fsnfiles";
		}
		return str;
	}

	/**
	 * 服务器FTP ip地址
	 * 
	 * @return
	 */
	public String getFtpServerIp() {
		String str;
		try {
			str = this.getProperties().getProperty("local.ftpServerIp").trim();
			str = getChString(str);
		} catch (Exception e) {
			str = "172.16.3.124";
		}
		return str;
	}

	/**
	 * ftp 端口号
	 * 
	 * @return
	 */
	public int getFtpServerPort() {
		return Integer.valueOf(this.getProperties().getProperty("local.ftpServerPort").trim());
	}

	/**
	 * ftp服务器 主被动模式
	 * 
	 * @return
	 */
	public boolean getPassive() {
		String strVal = this.getProperties().getProperty("local.passive").toString().trim();
		if (Integer.valueOf(strVal) == 1) {
			return true;

		} else
			return false;

	}

	/**
	 * 网点编号
	 * 
	 * @return
	 */
	public String getBankCode() {
		String str;
		try {
			str = this.getProperties().getProperty("local.bankCode").trim();
			str = getChString(str);
		} catch (Exception e) {
			str = "00000000";
		}
		return str;
	}
	
	/**
	 * 服务器字符集
	 * 
	 * @return
	 */
	public String getServerCharset() {
		String str;
		try {
			str = this.getProperties().getProperty("local.serverCharset").trim();
			str = getChString(str);
		} catch (Exception e) {
			str = "ISO-8859-1";
		}
		return str;
	}
	
	/**
	 * 获得支持中文字符串 ， 输入为空时返回空串 lbf 2014-9-30
	 * 
	 * @param val
	 *            字符串
	 * @return 中文编号字符串
	 */
	private String getChString(String val) {
		return getChString(val, "");
	}

	/**
	 * 获得支持中文字符串
	 * 
	 * @param val
	 *            字符串 lbf 2014-9-30
	 * @param defaultString
	 *            输入为空时返回指定默认串
	 * @return 中文编号字符串
	 */
	private String getChString(String val, String defaultString) {
		if (val == null || val.isEmpty()) {
			return defaultString;
		}
		byte[] bytes;
		try {
			bytes = val.getBytes("iso-8859-1");

			String result = new String(bytes, "utf-8");
			return result;
		} catch (Exception e) {

			return val;
		}
	}
}
