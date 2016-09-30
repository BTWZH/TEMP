package cc.julong.server.tcp.julong;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cc.julong.common.RealTAction;
import cc.julong.common.RealTBean;
import cc.julong.log.Log;
import cc.julong.server.state.DataInfo;
import cc.julong.server.tcp.AbstractHandler;
import cc.julong.server.util.FileUtils;
import cc.julong.server.util.MsgUtils;
import cc.julong.server.util.Utils;

/**
 * 网点实时业务采集
 * 
 * @author xp <br>
 *         date 2014-07-01 update 2015-04-16
 */
public class RealTimeHandler extends AbstractHandler {

	/** 人行协议FSN单条记录byte长度 */
	private static final int ALONERECORD = 1644;
	/** fsn文件内记录数针数 */
	private static final int MSGCOUNTERSITE = 20;
	/** 冠字号码长度 */
	private final static int SERINOLENGTH = 10;
	/** 错误的冠字号码 */
	private final static String BUGSERINO = "__________";

	public RealTimeHandler(DataInfo data) {
		super(data);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {

		in.skipBytes(JLHandler.HEADER_LENGTH);
		in = in.order(ByteOrder.LITTLE_ENDIAN);

		final int size = (int) in.readUnsignedInt() - JLHandler.HEADER_LENGTH
				- JLHandler.MSG_INFO_LENGTH;

		final DataInfo d = this.getData();

		Log.Info("TcpServer", "Real decoder");

		ctx.pipeline().addLast(new ByteToMessageDecoder() {
			@Override
			protected void decode(ChannelHandlerContext ctx, ByteBuf in,
					List<Object> out) throws Exception {
				// 文件名
				String fileName = "";

				if (in.readableBytes() < size) {
					return;
				}
				try {
					// 交易类型
					byte[] cmd = new byte[2];
					in.getBytes(JLHandler.VERSION_LENGTH, cmd);

					byte[] machine = MsgUtils.getBytes(in, JLHandler.CMD_LENGTH
							+ JLHandler.VERSION_LENGTH, 28);

					// 机器号
					String mach = new String(machine, "iso-8859-1").replaceAll(
							"[^A-Za-z_0-9/]*", "");

					// 业务信息
					String codeMsg = MsgUtils.getMsg(in, 0, 20).replaceAll(
							"[^A-Za-z_0-9/]*", "");

					// 业务没开始不采集
					if (0 < RealTAction.getMap().size()) {
						RealTBean bean = new RealTBean();
						d.setCodeMsg(codeMsg);

						d.setMachineinfo(mach);

						in.skipBytes(2);
						in.skipBytes(2);
						in.skipBytes(20);

						String MachTmp = RealTAction.getMachNo();

						// 获取业务实例bean
						if (RealTAction.getMap().containsKey("jnbank"))
							bean = (RealTBean) RealTAction.getMap().get(
									"jnbank");
						else
							bean = (RealTBean) RealTAction.getMap()
									.get(MachTmp);

						d.setBankNo(bean.getBankNo());// 机构编号
						d.setMachineType(bean.getMachType());// 设备类型
						d.setBeginDate(bean.getDtTime());// 开始时间

						if (Utils.isNullOrEmpty(bean.getBusiType())) {
							bean.setBusiType("HM");
						}

						if ("FK".equals(bean.getBusiType())) {
							bean.setBusiType("QK");
						} else if ("SK".equals(bean.getBusiType())) {
							bean.setBusiType("CK");
						}

						d.setBusiType(bean.getBusiType());// 业务类型

						// 创建文件名
						if (Utils.isNullOrEmpty(bean.getFileName())) {
							fileName = FileUtils.getFileName(d) + ".tmp";
							bean.setFileName(fileName.replace('\\', '/'));
						} else {
							// 如果业务之前已经点过钱,拿过来直接用
							fileName = bean.getFileName();
						}

						if (Utils.isNullOrEmpty(fileName)) {
							// 创建文件名失败
							int e1 = 0xE1;
							ByteBuf b = MsgUtils.getMsg2Send(machine, e1);

							MsgUtils.sendMssageBytes(ctx, JLHandler.HEADER, b);
							ctx.pipeline().addLast(JLFactory.getDefault(d));
							ctx.pipeline().remove(this);
							return;
						}

						// int count = MsgUtils.getInt32(in, 20, 4);

						in.skipBytes(20);
						byte[] counterBytes = new byte[4];
						in.readBytes(counterBytes);
						int counter = MsgUtils.getValFromBytes(counterBytes, 0,
								3);
						Log.Info("head count： " + counter);
						// FIXME 文件目前记录数
						int fileCount = bean.getRecordCount();

						in.skipBytes(8);

						// ******************试写*********************

						// 记录数总长度
						int msgLengths = size - 32 - 84 - 2 + 8;

						// 报文记录数总数
						int msgCount = msgLengths / ALONERECORD;

						// 存放冠字号信息容器
						HashSet<String> hshStruct = new HashSet<String>();

						// 由于同时只存在一个业务,所以采集去重容器公用
						hshStruct = RealTAction.getHshStruct();

						// 将要写入fsn文件里的记录容器
						List<ByteBuffer> lstByte = new ArrayList<ByteBuffer>();

						int tmpAmount = 0;// 上一张钱的金额

						for (int i = 1; i <= msgCount; i++) {
							// 读取每一行记录 并且读出冠字号与金额
							try {
								// 金额信息
								int amount = 0;
								// 冠字号信息
								String seri_no = "";
								// 纸币类型
								int tfFlag = 0;
								// 每条记录byte
								ByteBuffer bufTmp = ByteBuffer
										.allocate(ALONERECORD);

								in.readBytes(bufTmp);

								byte[] tfFlagBytes = new byte[2];
								byte[] valutaBytes = new byte[2];
								byte[] seriBytes = new byte[24];

								bufTmp.position(4);
								bufTmp.get(tfFlagBytes);

								bufTmp.position(22);
								bufTmp.get(valutaBytes);

								bufTmp.position(26);
								bufTmp.get(seriBytes);

								/*
								 * 0为假币或可疑币，1为真币，2为残币(清分机适用)，3为旧币(清分机适用)；
								 * 4分钞币,5流通币,6 ATM币,7无效币
								 */

								try {
									tfFlag = MsgUtils.getValFromBytes(
											tfFlagBytes, 0, 2);
								} catch (Exception e) {
									Log.Info("Error tfFlag :" + tfFlag);
									tfFlag = 0;
								}

								if (0 == tfFlag) {
									// 可疑币
									amount = 0;
								} else {
									if (1 == tfFlag || 3 == tfFlag
											|| 4 == tfFlag || 5 == tfFlag
											|| 6 == tfFlag) {
										if (0 == amount) {
											amount = tmpAmount;
										}
									}
								}
								// amount = tmpAmount;

								try {
									amount = MsgUtils.getValFromBytes(
											valutaBytes, 0, 2);

								} catch (Exception e) {
									Log.Info("Error amount :" + amount);
								}

								try {
									// 冠字号信息
									seri_no = new String(seriBytes,
											"iso-8859-1").replaceAll(
											"[^A-Za-z_0-9/]*", "");

									seri_no = Utils.leftPad(seri_no,
											SERINOLENGTH, '_');

								} catch (Exception e) {
									Log.Info("Error seri_no :" + seri_no);
									seri_no = BUGSERINO;
								}
								RealTAction.realAmountCountAdd(amount);
								if (BUGSERINO.equals(seri_no)
										|| "----------".equals(seri_no)
										|| seri_no.startsWith("ERROR")
										|| seri_no.startsWith("TILT")
										|| hshStruct.add(seri_no + amount)) {
									// 冠字号去重
									lstByte.add(bufTmp);
									RealTAction.AmountCountAdd(amount);
									if (0 != tfFlag) {
										tmpAmount = amount;
									}
								}
							} catch (Exception e) {
								Log.Info(e.getMessage());
								continue;
							}
						}
						Log.Info(" 点钞金额 =  " + RealTAction.getRealAmount());
						Log.Info(" 实际点钞金额 =  " + RealTAction.GetAmountCount());
						RealTAction.setHshStruct(hshStruct);

						int msgSize = lstByte.size();
						if (0 < msgSize) {
							// 报文
							ByteBuffer bufBody = ByteBuffer.allocate(msgSize
									* ALONERECORD);

							for (ByteBuffer byteTmp : lstByte) {
								bufBody.put(byteTmp.array());
							}

							bufBody.flip();

							try {
								// 生成并追加FSN文件
								createFile(fileName, msgSize, bufBody);

								bean.setRecordCount(fileCount + msgSize);
								RealTAction.PutReal(mach, bean);
								RealTAction.setMachNo(mach);

								// 删除初始业务记录
								if (RealTAction.getMap().containsKey("jnbank"))
									RealTAction.RemoveReal("jnbank");

							} catch (IOException e) {
								Log.Info("TcpServer", "fileName: " + fileName
										+ "create file error:" + e.toString());
							}
						}
					} else {
						// 接收HM文件
						RealTBean bean = new RealTBean();

						bean.setBizId("yellow");// 黄标

						bean.setFileMaxRecord(1000);

						d.setCodeMsg(codeMsg);

						d.setMachineinfo(mach);

						in.skipBytes(2);
						in.skipBytes(2);
						byte[] counterBytes = new byte[4];
						in.readBytes(counterBytes);
						int counter = MsgUtils.getValFromBytes(counterBytes, 0,
								3);
						Log.Info("head count： " + counter);

						// String MachTmp = RealTAction.getMachNo();

						d.setBankNo(RealTAction.getInitBean().getBankCode());
						d.setMachineType(bean.getMachType());
						d.setBeginDate(bean.getDtTime());

						if (Utils.isNullOrEmpty(bean.getBusiType())) {
							bean.setBusiType("HM");
						}

						if ("FK".equals(bean.getBusiType())) {
							bean.setBusiType("QK");
						} else if ("SK".equals(bean.getBusiType())) {
							bean.setBusiType("CK");
						}

						d.setBusiType(bean.getBusiType());

						if (Utils.isNullOrEmpty(bean.getFileName())) {
							fileName = FileUtils.getFileName(d) + ".tmp";
							bean.setFileName(fileName.replace('\\', '/'));
						} else {
							fileName = bean.getFileName();
						}

						if (Utils.isNullOrEmpty(fileName)) {

							int e1 = 0xE1;
							ByteBuf b = MsgUtils.getMsg2Send(machine, e1);

							MsgUtils.sendMssageBytes(ctx, JLHandler.HEADER, b);
							ctx.pipeline().addLast(JLFactory.getDefault(d));
							ctx.pipeline().remove(this);
							return;
						}

						// int count = MsgUtils.getInt32(in, 20, 4);

						in.skipBytes(20);
						in.skipBytes(4);

						// FIXME 文件目前记录数
						int fileCount = bean.getRecordCount();

						in.skipBytes(8);

						// ******************试写*********************

						// 记录数总长度
						int msgLengths = size - 32 - 84 - 2 + 8;

						// 报文记录数总数
						int msgCount = msgLengths / ALONERECORD;

						// 存放冠字号信息容器
						HashSet<String> hshStruct = new HashSet<String>();

						hshStruct = RealTAction.getHshStruct();

						// 记录容器
						List<ByteBuffer> lstByte = new ArrayList<ByteBuffer>();

						int tmpAmount = 0;// 上一张钱的金额

						for (int i = 1; i <= msgCount; i++) {
							// 读取每一行记录 并且读出冠字号与金额
							try {
								// 金额信息
								int amount = 0;
								// 冠字号信息
								String seri_no = "";
								// 纸币类型
								int tfFlag = 0;
								// 每条记录byte
								ByteBuffer bufTmp = ByteBuffer
										.allocate(ALONERECORD);

								in.readBytes(bufTmp);

								byte[] tfFlagBytes = new byte[2];
								byte[] valutaBytes = new byte[2];
								byte[] seriBytes = new byte[24];

								bufTmp.position(4);
								bufTmp.get(tfFlagBytes);

								bufTmp.position(22);
								bufTmp.get(valutaBytes);

								bufTmp.position(26);
								bufTmp.get(seriBytes);

								/*
								 * 0为假币或可疑币，1为真币，2为残币(清分机适用)，3为旧币(清分机适用)；
								 * 4分钞币,5流通币,6 ATM币,7无效币
								 */

								try {
									tfFlag = MsgUtils.getValFromBytes(
											tfFlagBytes, 0, 2);
								} catch (Exception e) {
									Log.Info("Error tfFlag :" + tfFlag);
									tfFlag = 0;
								}

								try {
									amount = MsgUtils.getValFromBytes(
											valutaBytes, 0, 2);

								} catch (Exception e) {
									Log.Info("Error amount :" + amount);

									// if (0 == tfFlag) {
									// // 可疑币
									// amount = 0;
									// } else {
									// if (0 == amount) {
									// amount = tmpAmount;
									// }
									// // amount = tmpAmount;
									// }
								}
								if (0 == tfFlag) {
									// 可疑币
									amount = 0;
								} else {
									if (1 == tfFlag || 3 == tfFlag
											|| 4 == tfFlag || 5 == tfFlag
											|| 6 == tfFlag) {
										if (0 == amount) {
											amount = tmpAmount;
										}
									}
								}
								// amount = tmpAmount;

								try {
									// 冠字号信息
									seri_no = new String(seriBytes,
											"iso-8859-1").replaceAll(
											"[^A-Za-z_0-9/]*", "");

									seri_no = Utils.leftPad(seri_no,
											SERINOLENGTH, '_');

								} catch (Exception e) {
									Log.Info("Error seri_no :" + seri_no);
									seri_no = BUGSERINO;
								}
								RealTAction.realAmountCountAdd(amount);
								if (BUGSERINO.equals(seri_no)
										|| "----------".equals(seri_no)
										|| seri_no.startsWith("ERROR")
										|| seri_no.startsWith("TILT")
										|| hshStruct.add(seri_no + amount)) {
									// 冠字号去重
									lstByte.add(bufTmp);
									RealTAction.AmountCountAdd(amount);
									if (0 != tfFlag) {
										tmpAmount = amount;
									}
								}
							} catch (Exception e) {
								Log.Info(e.getMessage());
								continue;
							}
						}
						Log.Info(" 点钞金额 =  " + RealTAction.getRealAmount());
						Log.Info(" 实际点钞金额 =  " + RealTAction.GetAmountCount());
						RealTAction.setHshStruct(hshStruct);

						int msgSize = lstByte.size();
						if (0 < msgSize) {
							// 报文
							ByteBuffer bufBody = ByteBuffer.allocate(msgSize
									* ALONERECORD);

							for (ByteBuffer byteTmp : lstByte) {
								bufBody.put(byteTmp.array());
							}

							bufBody.flip();

							try {
								// 生成并追加FSN文件
								createFile(fileName, msgSize, bufBody);

								bean.setRecordCount(fileCount + msgSize);
								// RealTAction.PutReal(mach, bean);
								// RealTAction.setMachNo(mach);
								// 删除初始业务记录
								if (RealTAction.getMap().containsKey("jnbank"))
									RealTAction.RemoveReal("jnbank");

							} catch (IOException e) {
								Log.Info("TcpServer", "fileName: " + fileName
										+ "create file error:" + e.toString());
							}
						}
					}

					in.clear();

					int ng = 0xF0;
					ByteBuf b = MsgUtils.getMsg2Send(machine, ng);

					MsgUtils.sendMssageBytes(ctx, JLHandler.HEADER, b);
					ctx.pipeline().addLast(JLFactory.getDefault(d));
					ctx.pipeline().remove(this);

				} catch (Exception e) {
					Log.Info("ERROR： 采集冠字号码信息时出错，信息： " + e.getMessage());
				}
			}
		});
		ctx.pipeline().remove(this);
	}

	/**
	 * ҵ������ת�� String => byte[]
	 * 
	 * @param busiType
	 * @return
	 */
	private byte[] getBusiType(String busiType) {

		byte[] result = new byte[] { 0x00, (byte) 0x06 };

		if (Utils.isNullOrEmpty(busiType))
			return result;

		if ("FK".equals(busiType)) {
			result[1] = (byte) 0x01;
		} else if ("SK".equals(busiType)) {
			result[1] = (byte) 0x02;
		} else if ("QK".equals(busiType)) {
			result[1] = (byte) 0x03;
		} else if ("CK".equals(busiType)) {
			result[1] = (byte) 0x04;
		} else if ("ATM".equals(busiType)) {
			result[1] = (byte) 0x05;
		} else if ("HM".equals(busiType)) {
			result[1] = (byte) 0x06;
		} else if ("CAQK".equals(busiType)) {
			result[1] = (byte) 0x07;
		} else if ("CACK".equals(busiType)) {
			result[1] = (byte) 0x08;
		} else if ("收款".equals(busiType)) {
			result[1] = (byte) 0x02;
		} else if ("付款".equals(busiType)) {
			result[1] = (byte) 0x01;
		} else {
			result[1] = (byte) 0x10;
		}

		return result;
	}

	/**
	 * ����or׷��FSN�ļ�
	 * 
	 * @param fileName
	 *            �ļ����·��
	 * @param count
	 *            ���μ�¼��
	 * @param bufBody
	 *            ������
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void createFile(String fileName, int count, ByteBuffer bufBody)
			throws IOException {
		RealTAction.setWriteFinished(false);
		// ׷���ļ�����
		File file = new File(fileName);
		try {
			Thread.sleep(2*1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
		FileChannel fc = randomAccessFile.getChannel();
		ByteBuffer bufAll;

		long site = 0; // FSN��Ҫ�����λ��

		if (file.exists() && file.length() > 10L) {
			// �Ѵ���,׷��FSN

			// ��¼�����ڱ���������
			site = MSGCOUNTERSITE;

			ByteBuffer bufCount = ByteBuffer.allocate(4);
			fc.read(bufCount, site);

			// �ܹ�������
			count += MsgUtils.getValFromBytes(bufCount.array(), 0, 4);

			// ��д�ļ�ͷ������
			byte[] Counter = FileUtils.getCounter(count);
			bufCount.clear();
			bufCount.put(Counter);
			bufCount.flip();

			fc.write(bufCount, site);

			site = fc.size();
			bufAll = ByteBuffer.allocate(bufBody.array().length);
			bufAll.put(bufBody);
			Log.Info("old File count :" + count);
		} else {
			Log.Info("new File count :" + count);
			// ������,�½�FSN
			bufAll = ByteBuffer.allocate(32 + bufBody.array().length);
			ByteBuffer bufHead = FileUtils.getStruct(count);
			bufAll.put(bufHead.array());
			bufAll.put(bufBody.array());
		}

		bufAll.flip();

		fc.position(site);

		fc.write(bufAll);

		fc.close();

		randomAccessFile.close();
		RealTAction.setWriteFinished(true);
	}

}
