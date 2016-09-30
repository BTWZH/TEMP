package cc.julong.server.tcp.julong;

import io.netty.channel.ChannelHandlerContext;
import cc.julong.server.exception.ServerException;
import cc.julong.server.state.DataInfo;
//import cc.julong.transfer.core.transferFactory;
//import cc.julong.transfer.model.TransferType;

public class JLException extends ServerException {

  public JLException(String msg) {
    super(msg);
    // monitor
    // transferFactory.setErrDoneCountAdd(TransferType.GATHER.toString());
    // transferFactory.setErrorMsg(TransferType.GATHER.toString(), msg);
  }

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  @Override
  public void handleException(ChannelHandlerContext ctx, DataInfo data) {
    // monitor
    // transferFactory.setErrDoneCountAdd(TransferType.GATHER.toString());
  }

  @Override
  public String getMessage() {
    return super.getMessage();
  }

  @Override
  public String toString() {
    return "JLException -" + getMessage() + "";
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
