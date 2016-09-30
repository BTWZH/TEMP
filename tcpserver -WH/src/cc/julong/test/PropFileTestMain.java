package cc.julong.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

public class PropFileTestMain {
	// ����key��ȡvalue
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

	// ��ȡproperties��ȫ����Ϣ
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

	// д��properties��Ϣ
	public static void writeProperties(String filePath, String parameterName,
			String parameterValue) {
		Properties prop = new Properties();
		try {

			InputStream in = PropFileTestMain.class.getResourceAsStream("/" + filePath);
			prop.load(in);

			// ���� Hashtable �ķ��� put��ʹ�� getProperty �����ṩ�����ԡ�
			// ǿ��Ҫ��Ϊ���Եļ���ֵʹ���ַ���������ֵ�� Hashtable ���� put �Ľ����
			OutputStream fos = new FileOutputStream(Thread.currentThread()
					.getContextClassLoader().getResource("").getPath()
					+ filePath);
			prop.setProperty(parameterName, parameterValue);
			// ���ʺ�ʹ�� load �������ص� Properties ���еĸ�ʽ��
			// ���� Properties ���е������б�����Ԫ�ضԣ�д�������
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
