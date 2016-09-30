package cc.julong.server.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import cc.julong.server.exception.ServerException;
import cc.julong.server.state.DataInfo;

public abstract class AbstractHandler extends ByteToMessageDecoder {

    private DataInfo data;

    public AbstractHandler(DataInfo data) {
        this.data = data;
    }

    /**
     * 在server端捕获异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        if (cause.getCause() instanceof ServerException) {
            ServerException jle = (ServerException) cause.getCause();
            jle.handleException(ctx, data);
        }
    }

    public DataInfo getData() {
        return data;
    }

    public void setData(DataInfo data) {
        this.data = data;
    }

}
