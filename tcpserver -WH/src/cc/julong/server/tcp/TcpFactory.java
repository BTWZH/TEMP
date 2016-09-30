package cc.julong.server.tcp;

import io.netty.handler.codec.ByteToMessageDecoder;
import cc.julong.server.state.DataInfo;

public abstract class TcpFactory {

    public abstract ByteToMessageDecoder getMain(DataInfo data);

    public abstract boolean isRight(byte[] bytes);

}
