package cc.julong.server.tcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import cc.julong.common.RealTAction;
import cc.julong.server.state.DataInfo;

/**
 * �ɼ������ʼ��
 * 
 * @author zengming
 * 
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
  // static
  public ScheduledExecutorService executor = Executors
      .newSingleThreadScheduledExecutor();

  public ServerInitializer(ScheduledExecutorService executor) {
    this.executor = executor;
  }

  // 0表示不限制，单位byte，刷新间隔为1秒
  private GlobalTrafficShapingHandler trafficHandler = new GlobalTrafficShapingHandler(
      executor, 0, 0, 1000);

  @Override
  public void initChannel(SocketChannel ch) throws Exception {
    ch.pipeline().addLast("traffic", trafficHandler);

    DataInfo data = new DataInfo();
    String filePath = RealTAction.getInitBean().getFilesRoot();
    data.setFilePath(filePath);

    // 闲置触发处理
    ch.pipeline().addLast(new IdleTriggeredHandler());
    ch.pipeline().addLast("main", new MainHandler(data));
    ch.pipeline().addLast("writeTimeoutHandler", new WriteTimeoutHandler(120));
    ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(120));
    ch.pipeline().addLast("timeout", new TimeoutExceptHandler());

  }

}
