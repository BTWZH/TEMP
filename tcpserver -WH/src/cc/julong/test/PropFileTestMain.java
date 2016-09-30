package cc.julong.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

public class PropFileTestMain {
	// 根据key读取value
	public static String readValue(String filePath, String key) {
		Properties props = new Properties();
		try {
			InputStream in = PropFileTestMain.class.getResourceAsStream("/" + filePath);

			// InputStream in = new BufferedInputStream(new FileInputStream(
			// filePath));
			props.load(in);
			String value = props.getProperty(key);
			System.out.println(key + value);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 读取properties的全部信息
	public static void readProperties(String filePath) {
		Properties props = new Properties();
		try {
			// InputStream in = new BufferedInputStream(new FileInputStream(
			// filePath));
			InputStream in = PropFileTestMain.class.getResourceAsStream("/" + filePath);
			props.load(in);
			Enumeration en = props.propertyNames();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String Property = props.getProperty(key);
				System.out.println(key + Property);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 写入properties信息
	public static void writeProperties(String filePath, String parameterName,
			String parameterValue) {
		Properties prop = new Properties();
		try {

			InputStream in = PropFileTestMain.class.getResourceAsStream("/" + filePath);
			prop.load(in);

			// 调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
			// 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
			OutputStream fos = new FileOutputStream(Thread.currentThread()
					.getContextClassLoader().getResource("").getPath()
					+ filePath);
			prop.setProperty(parameterName, parameterValue);
			// 以适合使用 load 方法加载到 Properties 表中的格式，
			// 将此 Properties 表中的属性列表（键和元素对）写入输出流
			prop.store(fos, "Update '" + parameterName + "' value");
			//prop.store(fos, "Update '" + 123123123 + "' value");
			fos.close();
		} catch (IOException e) {
			System.err.println("Visit " + filePath + " for updating "
					+ parameterName + " value error");
		}
	}

	public static void main(String[] args) {

		readValue("sql.properties", "test");
		writeProperties("sql.properties", "age", "22");
		writeProperties("sql.properties", "age111", "11122");
		writeProperties("sql.properties", "age111", "33333");
		readProperties("sql.properties");
		System.out.println(PropFileTestMain.class.getClassLoader());
		System.out.println(System.getProperty("user.dir"));
		try {
			System.out.println(new File("").getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getContextClassLoader()
				.getResource("").getPath());
	}
}
