package cc.julong.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import cc.julong.server.util.Utils;

public abstract class ConfigBase {
	private Properties _p;
	private String _propertyFileName;

	public Properties getProperties() {
		return this._p;
	}

	public ConfigBase(String propertyFileName) {

		this(propertyFileName, null);
	}

	public ConfigBase(String propertyFileName, Properties p) {
		File configFile = new File(propertyFileName);
		this._propertyFileName = configFile.getAbsolutePath().replaceAll("%20",
				" ");
		if (Utils.isNullOrEmpty(this._propertyFileName)) {
			return;
		}
		File f = new File(this._propertyFileName);
		checkDirectory(f.getParent());
		try {
			if (!f.exists()) {
				// System.out.println("file:["+this._propertyFileName+"] does not exist!");
				f.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (p == null) {
			this._p = getConfigProperties();
		} else {
			this._p = p;
		}
	}

	private boolean checkDirectory(String dir) {
		try {
			File file = new File(dir);

			if (!file.exists() && !file.isDirectory()) {
				file.mkdirs();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private Properties getConfigProperties() {
		Properties prop = new Properties();
		synchronized (ConfigBase.class) {
			// this.getClass().getClassLoader().getResourceAsStream(propertyFileName);
			try {

				InputStream inputStream = new FileInputStream(
						this._propertyFileName);
				prop.load(inputStream);
				inputStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
		return prop;
	}

	public void Save() {
		try {
			synchronized (ConfigBase.class) {
				OutputStream os = new FileOutputStream(this._propertyFileName);
				this._p.store(os, null);
				os.close();
			}
		} catch (IOException e) {

		}
	}

	public void reload() {

		// this._p=getConfigProperties();
	}
}
