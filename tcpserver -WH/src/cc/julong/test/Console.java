/*
 * Copyright 2004-2011 H2 Group. Multiple-Licensed under the H2 License,
 * Version 1.0, and under the Eclipse Public License, Version 1.0
 * (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package cc.julong.test;

//## AWT begin ##
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.SystemColor;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;

/**
 * Starts the H2 Console (web-) server, as well as the TCP and PG server.
 * 
 * @h2.resource
 * 
 * @author Thomas Mueller, Ridvan Agar
 */
public class Console implements
// ## AWT begin ##
    ActionListener, MouseListener, WindowListener {

  // ## AWT begin ##
  private Frame frame;
  private boolean trayIconUsed;
  private Font font;
  private Button startBrowser;
  private TextField urlText;
  private Object tray;
  private Object trayIcon;
  // ## AWT end ##
  private boolean isWindows;
  private long lastOpen;

  public static void main(String... args) {
    new Console().runTool(args);
  }

  /**
   * This tool starts the H2 Console (web-) server, as well as the TCP and PG
   * server. For JDK 1.6, a system tray icon is created, for platforms that
   * support it. Otherwise, a small window opens.
   * 
   * @param args
   *          the command line arguments
   */

  private static String getProperty(String name) {
    try {
      return System.getProperty(name);
    }
    catch (Exception e) {
      // SecurityException
      // applets may not do that - ignore
      return null;
    }
  }

  /**
   * INTERNAL
   */
  public static String getStringSetting(String name, String defaultValue) {
    String s = getProperty(name);
    return s == null ? defaultValue : s;
  }

  public void runTool(String... args2) {
    isWindows = getStringSetting("os.name", "").startsWith("Windows");
    loadFont();
    try {
      if (!createTrayIcon()) {
        showWindow();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    // ## AWT end ##

    // start browser in any case (even if the server is already running)
    // because some people don't look at the output,
    // but are wondering why nothing happens

    try {
      openBrowser("http://www.baidu.com");
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    while (true) {
      try {
        Thread.sleep(200);
      }
      catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

  /**
   * INTERNAL. Stop all servers that were started using the console.
   */
  public void shutdown() {

    // ## AWT begin ##
    if (frame != null) {
      frame.dispose();
      frame = null;
    }
    if (trayIconUsed) {
      try {
        // tray.remove(trayIcon);
        callMethod(tray, "remove", trayIcon);
      }
      catch (Exception e) {
        // ignore
      }
      finally {
        trayIcon = null;
        tray = null;
        trayIconUsed = false;
      }
      System.gc();
    }
    // ## AWT end ##
    System.exit(0);
  }

  // ## AWT begin ##
  private void loadFont() {
    if (isWindows) {
      font = new Font("Dialog", Font.PLAIN, 11);
    }
    else {
      font = new Font("Dialog", Font.PLAIN, 12);
    }
  }

  private boolean createTrayIcon() {
    try {
      // SystemTray.isSupported();
      boolean supported = (Boolean) callStaticMethod("java.awt.SystemTray.isSupported");
      if (!supported) { return false; }
      PopupMenu menuConsole = new PopupMenu();
      MenuItem itemConsole = new MenuItem("H2 Console");
      itemConsole.setActionCommand("console");
      itemConsole.addActionListener(this);
      itemConsole.setFont(font);
      menuConsole.add(itemConsole);
      MenuItem itemStatus = new MenuItem("Status");
      itemStatus.setActionCommand("status");
      itemStatus.addActionListener(this);
      itemStatus.setFont(font);
      menuConsole.add(itemStatus);
      MenuItem itemExit = new MenuItem("Exit");
      itemExit.setFont(font);
      itemExit.setActionCommand("exit");
      itemExit.addActionListener(this);
      menuConsole.add(itemExit);

      // tray = SystemTray.getSystemTray();
      tray = callStaticMethod("java.awt.SystemTray.getSystemTray");

      // Dimension d = tray.getTrayIconSize();
      Dimension d = (Dimension) callMethod(tray, "getTrayIconSize");

      String iconFile;
      if (d.width >= 24 && d.height >= 24) {
        iconFile = "/cc/julong/test/book.png";
      }
      else if (d.width >= 22 && d.height >= 22) {
        iconFile = "/cc/julong/test/book.png";
      }
      else {
        iconFile = "/cc/julong/test/book.png";
      }

      java.net.URL imgURL = Console.class.getResource(iconFile);
      Image icon = Toolkit.getDefaultToolkit().createImage(imgURL);

      // trayIcon = new TrayIcon(image, "H2 Database Engine",
      // menuConsole);
      trayIcon = newInstance("java.awt.TrayIcon", icon, "H2 Database Engine",
          menuConsole);

      // trayIcon.addMouseListener(this);
      callMethod(trayIcon, "addMouseListener", this);

      // tray.add(trayIcon);
      callMethod(tray, "add", trayIcon);

      this.trayIconUsed = true;

      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  private void showWindow() {
    if (frame != null) { return; }
    frame = new Frame("H2 Console");
    frame.addWindowListener(this);
    java.net.URL imgURL = Console.class.getResource("/cc/julong/test/book.png");
    Image image = Toolkit.getDefaultToolkit().createImage(imgURL);

    if (image != null) {
      frame.setIconImage(image);
    }
    frame.setResizable(false);
    frame.setBackground(SystemColor.control);

    GridBagLayout layout = new GridBagLayout();
    frame.setLayout(layout);

    // the main panel keeps everything together
    Panel mainPanel = new Panel(layout);

    GridBagConstraints constraintsPanel = new GridBagConstraints();
    constraintsPanel.gridx = 0;
    constraintsPanel.weightx = 1.0D;
    constraintsPanel.weighty = 1.0D;
    constraintsPanel.fill = GridBagConstraints.BOTH;
    constraintsPanel.insets = new Insets(0, 10, 0, 10);
    constraintsPanel.gridy = 0;

    GridBagConstraints constraintsButton = new GridBagConstraints();
    constraintsButton.gridx = 0;
    constraintsButton.gridwidth = 2;
    constraintsButton.insets = new Insets(10, 0, 0, 0);
    constraintsButton.gridy = 1;
    constraintsButton.anchor = GridBagConstraints.EAST;

    GridBagConstraints constraintsTextField = new GridBagConstraints();
    constraintsTextField.fill = GridBagConstraints.HORIZONTAL;
    constraintsTextField.gridy = 0;
    constraintsTextField.weightx = 1.0;
    constraintsTextField.insets = new Insets(0, 5, 0, 0);
    constraintsTextField.gridx = 1;

    GridBagConstraints constraintsLabel = new GridBagConstraints();
    constraintsLabel.gridx = 0;
    constraintsLabel.gridy = 0;

    Label label = new Label("H2 Console URL:", Label.LEFT);
    label.setFont(font);
    mainPanel.add(label, constraintsLabel);

    urlText = new TextField();
    urlText.setEditable(false);
    urlText.setFont(font);
    urlText.setText("www.google.com");
    if (isWindows) {
      urlText.setFocusable(false);
    }
    mainPanel.add(urlText, constraintsTextField);

    startBrowser = new Button("Start Browser");
    startBrowser.setFocusable(false);
    startBrowser.setActionCommand("console");
    startBrowser.addActionListener(this);
    startBrowser.setFont(font);
    mainPanel.add(startBrowser, constraintsButton);
    frame.add(mainPanel, constraintsPanel);

    int width = 300, height = 120;
    frame.setSize(width, height);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setLocation((screenSize.width - width) / 2,
        (screenSize.height - height) / 2);
    try {
      frame.setVisible(true);
    }
    catch (Throwable t) {
      // ignore
      // some systems don't support this method, for example IKVM
      // however it still works
    }
    try {
      // ensure this window is in front of the browser
      frame.setAlwaysOnTop(true);
      frame.setAlwaysOnTop(false);
    }
    catch (Throwable t) {
      // ignore
    }
  }

  private void startBrowser() {

    String url = "http://www.baidu.com";
    if (urlText != null) {
      urlText.setText(url);
    }
    long now = System.currentTimeMillis();
    if (lastOpen == 0 || lastOpen + 100 < now) {
      lastOpen = now;
      try {
        openBrowser(url);
      }
      catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

  // ## AWT end ##

  public static void openBrowser(String url) throws Exception {
    try {
      String osName = getStringSetting("os.name", "linux").toLowerCase();
      Runtime rt = Runtime.getRuntime();
      String browser = System.getProperty("h2.browser");
      if (browser != null) {
        if (browser.startsWith("call:")) {
          browser = browser.substring("call:".length());
          callStaticMethod(browser, url);
        }
        else if (browser.indexOf("%url") >= 0) {
          String[] args = arraySplit(browser, ',', false);
          
          for (int i = 0; i < args.length; i++) {
            args[i] = replaceAll(args[i], "%url", url);
          }
          rt.exec(args);
        }
        else if (osName.indexOf("windows") >= 0) {
          rt.exec(new String[] {
              "cmd.exe", "/C", browser, url });
        }
        else {
          rt.exec(new String[] {
              browser, url });
        }
        return;
      }
      try {
        Class<?> desktopClass = Class.forName("java.awt.Desktop");
        // Desktop.isDesktopSupported()
        Boolean supported = (Boolean) desktopClass.getMethod(
            "isDesktopSupported").invoke(null, new Object[0]);
        URI uri = new URI(url);
        if (supported) {
          // Desktop.getDesktop();
          Object desktop = desktopClass.getMethod("getDesktop").invoke(null,
              new Object[0]);
          // desktop.browse(uri);
          desktopClass.getMethod("browse", URI.class).invoke(desktop, uri);
          return;
        }
      }
      catch (Exception e) {
        // ignore
      }
      if (osName.indexOf("windows") >= 0) {
        rt.exec(new String[] {
            "rundll32", "url.dll,FileProtocolHandler", url });
      }
      else if (osName.indexOf("mac") >= 0 || osName.indexOf("darwin") >= 0) {
        // Mac OS: to open a page with Safari, use "open -a Safari"
        Runtime.getRuntime().exec(new String[] {
            "open", url });
      }
      else {
        String[] browsers = {
            "firefox", "mozilla-firefox", "mozilla", "konqueror", "netscape",
            "opera" };
        boolean ok = false;
        for (String b : browsers) {
          try {
            rt.exec(new String[] {
                b, url });
            ok = true;
            break;
          }
          catch (Exception e) {
            // ignore and try the next
          }
        }
        if (!ok) {
          // No success in detection.
          throw new Exception("Browser detection failed and system property "
              + " not set");
        }
      }
    }
    catch (Exception e) {
      throw new Exception("Failed to start a browser to open the URL " + url
          + ": " + e.getMessage());
    }
  }

  /**
   * INTERNAL
   */
  // ## AWT begin ##
  @Override
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if ("exit".equals(command)) {
      shutdown();
    }
    else if ("console".equals(command)) {
      startBrowser();
    }
    else if ("status".equals(command)) {
      showWindow();
    }
    else if (startBrowser == e.getSource()) {
      // for some reason, IKVM ignores setActionCommand
      startBrowser();
    }
  }

  // ## AWT end ##

  /**
   * INTERNAL
   */
  // ## AWT begin ##
  @Override
  public void mouseClicked(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON1) {
      startBrowser();
    }
  }

  // ## AWT end ##

  /**
   * INTERNAL
   */
  // ## AWT begin ##
  @Override
  public void mouseEntered(MouseEvent e) {
    // nothing to do
  }

  // ## AWT end ##

  /**
   * INTERNAL
   */
  // ## AWT begin ##
  @Override
  public void mouseExited(MouseEvent e) {
    // nothing to do
  }

  // ## AWT end ##

  /**
   * INTERNAL
   */
  // ## AWT begin ##
  @Override
  public void mousePressed(MouseEvent e) {
    // nothing to do
  }

  // ## AWT end ##

  /**
   * INTERNAL
   */
  // ## AWT begin ##
  @Override
  public void mouseReleased(MouseEvent e) {
    // nothing to do
  }

  // ## AWT end ##

  /**
   * INTERNAL
   */
  // ## AWT begin ##
  @Override
  public void windowClosing(WindowEvent e) {
    if (trayIconUsed) {
      frame.dispose();
      frame = null;
    }
    else {
      shutdown();
    }
  }

  // ## AWT end ##

  /**
   * INTERNAL
   */
  // ## AWT begin ##
  @Override
  public void windowActivated(WindowEvent e) {
    // nothing to do
  }

  // ## AWT end ##

  /**
   * INTERNAL
   */
  // ## AWT begin ##
  @Override
  public void windowClosed(WindowEvent e) {
    // nothing to do
  }

  // ## AWT end ##

  /**
   * INTERNAL
   */
  // ## AWT begin ##
  @Override
  public void windowDeactivated(WindowEvent e) {
    // nothing to do
  }

  // ## AWT end ##

  /**
   * INTERNAL
   */
  // ## AWT begin ##
  @Override
  public void windowDeiconified(WindowEvent e) {
    // nothing to do
  }

  // ## AWT end ##

  /**
   * INTERNAL
   */
  // ## AWT begin ##
  @Override
  public void windowIconified(WindowEvent e) {
    // nothing to do
  }

  // ## AWT end ##

  /**
   * INTERNAL
   */
  // ## AWT begin ##
  @Override
  public void windowOpened(WindowEvent e) {
    // nothing to do
  }

  // ## AWT end ##
  public static Object callStaticMethod(String classAndMethod, Object... params)
      throws Exception {
    int lastDot = classAndMethod.lastIndexOf('.');
    String className = classAndMethod.substring(0, lastDot);
    String methodName = classAndMethod.substring(lastDot + 1);
    return classMethodInternal(methodName, Class.forName(className), null,
        params);
  }

  /**
   * Calls an instance method via reflection. This will try to use the method
   * where the most parameter classes match exactly (this algorithm is simpler
   * than the one in the Java specification, but works well for most cases).
   * 
   * @param instance
   *          the instance on which the call is done
   * @param methodName
   *          a string with the method name
   * @param params
   *          the method parameters
   * @return the return value from this call
   */
  public static Object callMethod(Object instance, String methodName,
      Object... params) throws Exception {
    return classMethodInternal(methodName, instance.getClass(), instance,
        params);
  }

  private static Object classMethodInternal(String methodName, Class<?> clazz,
      Object instance, Object... params) throws Exception {
    Method best = null;
    int bestMatch = 0;
    boolean isStatic = instance == null;
    for (Method m : clazz.getMethods()) {
      if (Modifier.isStatic(m.getModifiers()) == isStatic
          && m.getName().equals(methodName)) {
        int p = match(m.getParameterTypes(), params);
        if (p > bestMatch) {
          bestMatch = p;
          best = m;
        }
      }
    }
    if (best == null) { throw new NoSuchMethodException(methodName); }
    return best.invoke(instance, params);
  }

  private static int match(Class<?>[] params, Object[] values) {
    int len = params.length;
    if (len == values.length) {
      int points = 1;
      for (int i = 0; i < len; i++) {
        Class<?> pc = getNonPrimitiveClass(params[i]);
        Class<?> vc = values[i].getClass();
        if (pc == vc) {
          points++;
        }
        else if (!pc.isAssignableFrom(vc)) { return 0; }
      }
      return points;
    }
    return 0;
  }

  public static Class<?> getNonPrimitiveClass(Class<?> clazz) {
    if (!clazz.isPrimitive()) {
      return clazz;
    }
    else if (clazz == boolean.class) {
      return Boolean.class;
    }
    else if (clazz == byte.class) {
      return Byte.class;
    }
    else if (clazz == char.class) {
      return Character.class;
    }
    else if (clazz == double.class) {
      return Double.class;
    }
    else if (clazz == float.class) {
      return Float.class;
    }
    else if (clazz == int.class) {
      return Integer.class;
    }
    else if (clazz == long.class) {
      return Long.class;
    }
    else if (clazz == short.class) {
      return Short.class;
    }
    else if (clazz == void.class) { return Void.class; }
    return clazz;
  }

  public static Object newInstance(String className, Object... params)
      throws Exception {
    Constructor<?> best = null;
    int bestMatch = 0;
    for (Constructor<?> c : Class.forName(className).getConstructors()) {
      int p = match(c.getParameterTypes(), params);
      if (p > bestMatch) {
        bestMatch = p;
        best = c;
      }
    }
    if (best == null) { throw new NoSuchMethodException(className); }
    return best.newInstance(params);
  }

  /**
   * Replace all occurrences of the before string with the after string.
   * 
   * @param s
   *          the string
   * @param before
   *          the old text
   * @param after
   *          the new text
   * @return the string with the before string replaced
   */
  public static String replaceAll(String s, String before, String after) {
    int next = s.indexOf(before);
    if (next < 0) { return s; }
    StringBuilder buff = new StringBuilder(s.length() - before.length()
        + after.length());
    int index = 0;
    while (true) {
      buff.append(s.substring(index, next)).append(after);
      index = next + before.length();
      next = s.indexOf(before, index);
      if (next < 0) {
        buff.append(s.substring(index));
        break;
      }
    }
    return buff.toString();
  }

  /**
   * Split a string into an array of strings using the given separator. A null
   * string will result in a null array, and an empty string in a zero element
   * array.
   * 
   * @param s
   *          the string to split
   * @param separatorChar
   *          the separator character
   * @param trim
   *          whether each element should be trimmed
   * @return the array list
   */
  public static String[] arraySplit(String s, char separatorChar, boolean trim) {
    if (s == null) { return null; }
    int length = s.length();
    if (length == 0) { return new String[0]; }
    ArrayList<String> list = new ArrayList<String>();
    StringBuilder buff = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      char c = s.charAt(i);
      if (c == separatorChar) {
        String e = buff.toString();
        list.add(trim ? e.trim() : e);
        buff.setLength(0);
      }
      else if (c == '\\' && i < length - 1) {
        buff.append(s.charAt(++i));
      }
      else {
        buff.append(c);
      }
    }
    String e = buff.toString();
    list.add(trim ? e.trim() : e);
    String[] array = new String[list.size()];
    list.toArray(array);
    return array;
  }
}
