package cc.julong.server.tcp;

import cc.julong.log.Log;

//import com.archerframework.core.AbstractPlugin;
//import com.archerframework.core.debug.ArcherLog;
//import com.archerframework.core.exception.ArcherException;

public class TcpPlugin {
  ATcpServer server;

  public TcpPlugin(Integer index) {
    // super(index);
  }

  public void onStart() throws Exception {
    Log.Info("TcpServer", "Tcp Plugin staring");
    Protocols.loadProtocols();
    // 采集端服务启动
    if (server == null) {
      server = new ATcpServer(8234);
    }
    server.start();

  }

  public void onStop() throws Exception {
    Log.Info("TcpServer", "Tcp Plugin stoping");
    if (server != null) {
      server.stop();
    }
  }

}
