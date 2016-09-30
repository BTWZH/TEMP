package cc.julong.server.tcp.julong;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.List;

import cc.julong.log.Log;
import cc.julong.server.state.DataInfo;
import cc.julong.server.tcp.AbstractHandler;
import cc.julong.server.util.FileUtils;
import cc.julong.server.util.MsgUtils;
import cc.julong.server.util.Utils;
//import cc.julong.transfer.core.transferFactory;
//import cc.julong.transfer.model.TransferType;

public class DataHandler extends AbstractHandler {

  public DataHandler(DataInfo data) {
    super(data);

  }

  /**
   * ��client���͹�����request���ж�ByteBuffer����Ĳ���
   */
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
      throws Exception {
    in.skipBytes(JLHandler.HEADER_LENGTH);
    in = in.order(ByteOrder.LITTLE_ENDIAN);
    final int size = (int) in.readUnsignedInt() - JLHandler.HEADER_LENGTH
        - JLHandler.MSG_INFO_LENGTH;
    final DataInfo d = this.getData();

    Log.Info("data decoder");

    ctx.pipeline().addLast(new ByteToMessageDecoder() {
      @Override
      protected void decode(ChannelHandlerContext ctx, ByteBuf in,
          List<Object> out) throws Exception {

        if (in.readableBytes() < size) {
          return;
        }

        // ҵ������
        byte[] cmd = new byte[2];
        in.getBytes(JLHandler.VERSION_LENGTH - 1, cmd);

        d.setBusiType(FileUtils.getBusiType(cmd,""));

        byte[] machine = MsgUtils.getBytes(in, JLHandler.CMD_LENGTH
            + JLHandler.VERSION_LENGTH, 28);

        // ҵ����Ϣ
        String codeMsg = MsgUtils.getMsg(in, 0, 20).replaceAll(
            "[^A-Za-z_0-9/]*", "");

        d.setCodeMsg(codeMsg);

        in.skipBytes(2);
        in.skipBytes(2);
        in.skipBytes(20);

        // �����ļ���
        String fileName = "";

        try {
          fileName = FileUtils.getFileName(d);
        } catch (JLException e) {
          Log.Info(e.getMsg());
        }

        if (Utils.isNullOrEmpty(fileName)) {

          ByteBuf b = Unpooled.buffer();
          b.writeBytes(JLHandler.VERSION);
          b.writeBytes(new byte[] { 0x00, (byte) 0x00 });
          b.writeBytes(new byte[] { (byte) 0xE1, 0x00 });
          b.writeBytes(machine);
          b.writeBytes(new byte[] { 0x00, (byte) 0x00 });
          b.writeBytes(new byte[] { 0x00, (byte) 0x00 });

          MsgUtils.sendMssageBytes(ctx, JLHandler.HEADER, b);
          ctx.pipeline().addLast(JLFactory.getDefault(d));
          ctx.pipeline().remove(this);
          return;
        }

        RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
        FileChannel fc = randomAccessFile.getChannel();
        //
        ByteBuffer buffer = ByteBuffer.allocate(size - 84 - 2 + 8);
        in.readBytes(buffer);
        buffer.flip();
        fc.position(0);
        fc.write(buffer);
        fc.close();
        randomAccessFile.close();

        in.clear();

        // �ɹ���+1
        // monitor
        // transferFactory.setDoneCountAdd(TransferType.GATHER.toString());
        ByteBuf b = Unpooled.buffer();
        b.writeBytes(JLHandler.VERSION);
        b.writeBytes(new byte[] { 0x00, (byte) 0x00 });
        b.writeBytes(new byte[] { (byte) 0xF0, 0x00 });
        b.writeBytes(machine);
        b.writeBytes(new byte[] { 0x00, (byte) 0x00 });
        b.writeBytes(new byte[] { 0x00, (byte) 0x00 });

        MsgUtils.sendMssageBytes(ctx, JLHandler.HEADER, b);
        ctx.pipeline().addLast(JLFactory.getDefault(d));
        ctx.pipeline().remove(this);
      }
    });
    ctx.pipeline().remove(this);

  }
  
  

}
