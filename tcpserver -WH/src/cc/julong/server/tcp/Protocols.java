package cc.julong.server.tcp;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import cc.julong.log.Log;
import cc.julong.server.state.Protocol;

//import Log;
//import com.archerframework.core.util.PackageLoader;

public class Protocols {

	private static Map<String, TcpFactory> protocols = new TreeMap<String, TcpFactory>();

	public static void loadProtocols() {

		List<String> list = new ArrayList<String>();

		list.add("cc.julong.server.tcp.julong.JLFactory");
		list.add("cc.julong.server.tcp.julong.RealTimeHandler");

		// List<String> list =
		// com.archerframework.core.util.PackageLoader.getClassInPackage("");
		// TODO
		// Log.Info("TcpServer", "list�� :" + list.size());
		// FIXME
		Protocols p = new Protocols();

		// jar��·��
		// List<String> lstJar = p.getJarClass();

		// list.addAll(lstJar);

		// Log.Info("TcpServer", "Protocols");
		// TODO
		// Log.Info("TcpServer", "lstJar count :" + list.size());

		for (String cls : list) {
			try {
				if (cls.contains("SctpChannel")) {
					int a = 1;
				}
				Class<?> c = Class.forName(cls);
				if (c.isAnnotationPresent(Protocol.class)) {
					String name = c.getAnnotation(Protocol.class).value();
					protocols.put(name.toLowerCase(),
							(TcpFactory) c.newInstance());
					// TODO
					// Log.Info(name.toLowerCase() + " : " + c.newInstance());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static List<String> getClassName(String packageName) {
		String filePath = ClassLoader.getSystemResource("").getPath()
				+ packageName.replace(".", "\\");
		List<String> fileNames = getClassName(filePath, null);
		return fileNames;
	}

	private static List<String> getClassName(String filePath,
			List<String> className) {
		List<String> myClassName = new ArrayList<String>();
		File file = new File(filePath);
		File[] childFiles = file.listFiles();
		for (File childFile : childFiles) {
			if (childFile.isDirectory()) {
				myClassName.addAll(getClassName(childFile.getPath(),
						myClassName));
			} else {
				String childFilePath = childFile.getPath();
				childFilePath = childFilePath.substring(
						childFilePath.indexOf("\\classes") + 9,
						childFilePath.lastIndexOf("."));
				childFilePath = childFilePath.replace("\\", ".");
				myClassName.add(childFilePath);
			}
		}

		return myClassName;
	}

	public static List getClassInJar(File file) {
		List ret = new ArrayList();
		if (!file.exists())
			return ret;
		try {
			FileInputStream fis = new FileInputStream(file);
			JarInputStream jis = new JarInputStream(fis, false);
			for (JarEntry e = null; (e = jis.getNextJarEntry()) != null;) {
				String eName = e.getName();
				if (eName.substring(eName.lastIndexOf(".") + 1)
						.equalsIgnoreCase("class")) {
					ret.add(eName.replace('/', '.').substring(0,
							eName.length() - 6));
					jis.closeEntry();
				}
			}

			jis.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	/*
	 * ��ȡjar�����class��
	 */
	private List<String> getJarClass() {

		List<String> lstJar = new ArrayList<String>();

		try {
			String jarPath = getClass().getProtectionDomain().getCodeSource()
					.getLocation().getPath().replace("%20", " ");
			// TODO
			// Log.Info("TcpServer", "Jar count :" + jarPath);

			File file = new File(jarPath);

			lstJar = getClassInJar(file);
		} catch (Exception e) {
			// Log.Info("TcpServer", "");
		}

		return lstJar;
	}

	public static TcpFactory getProtocol(byte[] bytes, byte[] btsBoc_Msg_Type) {
		Set<String> keys = protocols.keySet();

		for (String key : keys) {

			// julong�ɼ�,���вɼ�
			if (protocols.get(key).isRight(bytes)) {
				return protocols.get(key);
			}

			// ���вɼ�
			if (protocols.get(key).isRight(btsBoc_Msg_Type)) {
				return protocols.get(key);
			}
		}

		return null;
	}

}
