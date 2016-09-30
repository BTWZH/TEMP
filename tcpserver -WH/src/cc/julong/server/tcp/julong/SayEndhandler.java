package cc.julong.server.tcp.julong;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cc.julong.common.RealTAction;
import cc.julong.log.Log;
import cc.julong.server.state.DataInfo;
import cc.julong.server.tcp.AbstractHandler;
import cc.julong.server.util.MsgUtils;
import cc.julong.server.util.Utils;

public class SayEndhandler extends AbstractHandler {

	public SayEndhandler(DataInfo data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		in.skipBytes(JLHandler.HEADER_LENGTH);
		in = in.order(ByteOrder.LITTLE_ENDIAN);

		final int size = (int) in.readUnsignedInt() - JLHandler.HEADER_LENGTH - JLHandler.MSG_INFO_LENGTH;

		final DataInfo d = this.getData();

		Log.Info("TcpServer", "say end");

		ctx.pipeline().addLast(new ByteToMessageDecoder() {
			@Override
			protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

				if (in.readableBytes() < size) {
					return;
				}

				byte[] cmd = new byte[2];

				try {

					in.getBytes(JLHandler.VERSION_LENGTH, cmd);

					byte[] allMsg = MsgUtils.getBytes(in, JLHandler.CMD_LENGTH + JLHandler.VERSION_LENGTH, 300);

					// 信息
					String msg = new String(allMsg, "utf-8").replaceAll("[^A-Za-z_0-9\u4e00-\u9fa5$-./]*", "");

					// 客户的账号、户名、流水号、业务时间(14位字符串),操作员号,操作员姓名,交易金额

					// 业务流水
					String seq = msg.split("\\$")[0];

					// 卡号
					String cardNo = getValues(msg, 1);

					// 姓名
					String name = getValues(msg, 2);

					// 业务时间
					String date = getValues(msg, 3);

					// 操作员号
					String oper = getValues(msg, 4);

					// 操作员姓名
					String operName = getValues(msg, 5);

					// 交易金额
					String amm = getValues(msg, 6).replace(",", "");
					String amount = (int) (Double.parseDouble(amm) * 100) + "";

					// 业务ID
					String bizId = getValues(msg, 7);
					// 证件号
					String paperNo = getValues(msg, 8);

					// log
					Log.Info("msg :" + msg);
					Log.Info("seq :" + seq);
					Log.Info("cardNo :" + cardNo);
					Log.Info("name :" + name);
					Log.Info("date :" + date);
					Log.Info("oper :" + oper);
					Log.Info("operName :" + operName);
					Log.Info("amount :" + amount);
					Log.Info("bizId :" + bizId);
					Log.Info("paperNo :" + paperNo);

					// 交易流水号_卡号$户名$证件号_操作员编号_柜台号_交易金额

					// 文件的后续名拼接
					// String fileLast = cardNo + "$" + name + "$0_" + oper +
					// "_"
					// + operName + "_" + amount;

					String hhmmss = convertDateToString("HH:mm:ss", new Date());
					
					if (date.length() == 8) {
						date = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
					}
					
					Date dt = convertStringToDate("yyyy-MM-dd HH:mm:ss", date + " " + hhmmss);

					// 调用业务结束接口
					RealTAction.EndReal(seq, dt, cardNo, name, oper, operName, amount, bizId,paperNo);

					in.clear();

					int ng = 0xF0;
					ByteBuf b = MsgUtils.getMsg2Send(cmd, ng, 0);

					MsgUtils.sendMssageBytes(ctx, JLHandler.HEADER, b);
					ctx.pipeline().addLast(JLFactory.getDefault(d));
					ctx.pipeline().remove(this);

				} catch (Exception e) {
					Log.Info("ERROR： 调用end接口时出错，信息： " + e.getMessage());

					in.clear();

					int ng = 0xE0;
					ByteBuf b = MsgUtils.getMsg2Send(cmd, ng, 0);

					MsgUtils.sendMssageBytes(ctx, JLHandler.HEADER, b);
					ctx.pipeline().addLast(JLFactory.getDefault(d));
					ctx.pipeline().remove(this);
				}
			}
		});

		ctx.pipeline().remove(this);
	}

	/**
	 * ���ָ�����ڸ�ʽ��Date����ʱ��
	 * 
	 * @param aMask
	 *            ���ڸ�ʽ
	 * @param strDate
	 *            ʱ��
	 * @return ָ�����ڸ�ʽ��Date����ʱ��
	 * @throws ParseException
	 */
	private Date convertStringToDate(String aMask, String strDate) throws ParseException {
		SimpleDateFormat df = null;
		Date date = null;

		df = new SimpleDateFormat(aMask);

		date = df.parse(strDate);

		return date;
	}

	/**
	 * ���ָ�����ڸ�ʽ��Date����ʱ��
	 * 
	 * @param aMask
	 *            ���ڸ�ʽ
	 * @param strDate
	 *            ʱ��
	 * @return ָ�����ڸ�ʽ��Date����ʱ��
	 * @throws ParseException
	 */
	private String convertDateToString(String aMask, Date date) throws ParseException {
		SimpleDateFormat df = null;
		String strDate = "";

		df = new SimpleDateFormat(aMask);

		strDate = df.format(date);

		return strDate;
	}

	private String getValues(String msg, int index) {
		// 操作员编号
		String value = "0";
		try {
			value = msg.split("\\$")[index];

			if (Utils.isNullOrEmpty(value)) {
				value = "0";
			}
		} catch (Exception e) {
			Log.Info("分割参数失败 :" + index + ",原因: " + e.getMessage());
		}
		return value;

	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String date = "2015-09-07";
		String msg1 = new String(date.getBytes(), "utf-8").replaceAll("[^A-Za-z_0-9\u4e00-\u9fa5$-./]*", "");
		System.out.println(msg1);
		String amount = "333,33.33";
		String msg = new String(amount.getBytes(), "utf-8").replaceAll("[^A-Za-z_0-9\u4e00-\u9fa5$-./]*", "");
		// 交易金额
		String a = msg.replace(",", "");
		String amount1 = (int) (Double.parseDouble(a) * 100) + "";

		System.out.println(amount1);

	}
}
