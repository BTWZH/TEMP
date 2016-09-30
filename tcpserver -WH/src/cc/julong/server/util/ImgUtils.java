package cc.julong.server.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;

import com.jhlabs.image.UnsharpFilter;

public class ImgUtils {

	public static void createFSNBMP(Object[] ds) {

		@SuppressWarnings("unused")
        Date d = new Date();
		byte[] b1 = getSubData((byte[]) ds[0], 48);
		int width = b1[0] & 0xff;
		int height = b1[1] & 0xff;
		@SuppressWarnings("unused")
        int dir = (b1[2] & 0x30) >> 4;

		try {
			// 创建原始数据输出流对象
			ByteArrayOutputStream dos = new ByteArrayOutputStream();
			// 给文件头的变量赋值
			int bfType = 'B' + 'M' * 256; // 位图文件类型（0—1字节）
			int bfSize = 1078 + width * height;// bmp文件的大小（2—5字节）
			int bfReserved1 = 0;// 位图文件保留字，必须为0（6-7字节）
			int bfReserved2 = 0;// 位图文件保留字，必须为0（8-9字节）
			int bfOffBits = 1078;// 文件头开始到位图实际数据之间的字节的偏移量（10-13字节）

			// 输入数据的时候要注意输入的数据在内存中要占几个字节，
			// 然后再选择相应的写入方法，而不是它自己本身的数据类型
			// 输入文件头数据
			dos.write(changeByte(bfType), 0, 2); // 输入位图文件类型'BM'
			dos.write(changeByte(bfSize), 0, 4); // 输入位图文件大小
			dos.write(changeByte(bfReserved1), 0, 2);// 输入位图文件保留字
			dos.write(changeByte(bfReserved2), 0, 2);// 输入位图文件保留字
			dos.write(changeByte(bfOffBits), 0, 4);// 输入位图文件偏移量

			// 给信息头的变量赋值
			int biSize = 40;// 信息头所需的字节数（14-17字节）
			int biWidth = width;// 位图的宽（18-21字节）
			int biHeight = height;// 位图的高（22-25字节）
			int biPlanes = 1; // 目标设备的级别，必须是1（26-27字节）
			int biBitcount = 8;// 每个像素所需的位数（28-29字节），必须是1位（双色）、4位（16色）、8位（256色）或者24位（真彩色）之一。
			int biCompression = 0;// 位图压缩类型，必须是0（不压缩）（30-33字节）、1（BI_RLEB压缩类型）或2（BI_RLE4压缩类型）之一。
			int biSizeImage = width * height;// 实际位图图像的大小，即整个实际绘制的图像大小（34-37字节）
			int biXPelsPerMeter = 0;// 位图水平分辨率，每米像素数（38-41字节）这个数是系统默认值
			int biYPelsPerMeter = 0;// 位图垂直分辨率，每米像素数（42-45字节）这个数是系统默认值
			int biClrUsed = 0;// 位图实际使用的颜色表中的颜色数（46-49字节），如果为0的话，说明全部使用了
			int biClrImportant = 0;// 位图显示过程中重要的颜色数(50-53字节)，如果为0的话，说明全部重要

			// 因为java是大端存储，那么也就是说同样会大端输出。
			// 但计算机是按小端读取，如果我们不改变多字节数据的顺序的话，那么机器就不能正常读取。
			// 所以首先调用方法将int数据转变为多个byte数据，并且按小端存储的顺序。

			// 输入信息头数据
			dos.write(changeByte(biSize), 0, 4);// 输入信息头数据的总字节数
			dos.write(changeByte(biWidth), 0, 4);// 输入位图的宽
			dos.write(changeByte(biHeight), 0, 4);// 输入位图的高
			dos.write(changeByte(biPlanes), 0, 2);// 输入位图的目标设备级别
			dos.write(changeByte(biBitcount), 0, 2);// 输入每个像素占据的字节数
			dos.write(changeByte(biCompression), 0, 4);// 输入位图的压缩类型
			dos.write(changeByte(biSizeImage), 0, 4);// 输入位图的实际大小
			dos.write(changeByte(biXPelsPerMeter), 0, 4);// 输入位图的水平分辨率
			dos.write(changeByte(biYPelsPerMeter), 0, 4);// 输入位图的垂直分辨率
			dos.write(changeByte(biClrUsed), 0, 4);// 输入位图使用的总颜色数
			dos.write(changeByte(biClrImportant), 0, 4);// 输入位图使用过程中重要的颜色数
			int[] ImClr = new int[256];
			for (int i = 0; i < 256; i++) {
				ImClr[i] = i + (i << 8) + (i << 16);
				dos.write(changeByte(ImClr[i]));
			}

			// dos.write(new byte[1024]);
			dos.write(getSubData((byte[]) ds[0], 48));
			dos.write(getSubData((byte[]) ds[1], 16));
			dos.write(getSubData((byte[]) ds[2], 16));
			dos.write(getSubData((byte[]) ds[3], 16));
			dos.flush();
			byte[] xxx = dos.toByteArray();
			ByteArrayInputStream in = new ByteArrayInputStream(xxx);
			BufferedImage src2 = ImageIO.read(in);

			BufferedImage src = new BufferedImage(src2.getWidth(),
					src2.getHeight(), BufferedImage.TYPE_INT_ARGB);
			UnsharpFilter c = new UnsharpFilter();
			c.setAmount(0.8f);
			c.filter(src2, src);
			// ResampleOp resampleOp = new ResampleOp(320, 32);
			// resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
			// BufferedImage image = resampleOp.filter(src, null);
			BufferedImage image = resizeImage(src, 320, 32);
			BufferedImage rotImage = rotateImage(image, 180);
			BufferedImage grayImage = new BufferedImage(rotImage.getWidth(),
					rotImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
			for (int i = 0; i < image.getWidth(); i++) {
				for (int j = 0; j < rotImage.getHeight(); j++) {
					int rgb = rotImage.getRGB(i, j);
					grayImage.setRGB(i, j, rgb);
				}
			}

			// BufferedImage resultImg = flipImage(grayImage);
			File newFile = new File("e:/" + System.nanoTime() + ".bmp");
			ImageIO.write(grayImage, "bmp", newFile);
			in.close();
			dos.close();
			// System.out.println(new Date().getTime() - d.getTime());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static byte[] getSubData(byte[] bytes, int index) {
		int size = bytes.length - index;
		byte[] result = new byte[size];
		for (int i = 0; i < result.length; i++) {
			result[i] = bytes[index + i];
		}
		return result;
	}

	public static byte[] changeByte(int data) {
		byte b4 = (byte) ((data) >> 24);
		byte b3 = (byte) (((data) << 8) >> 24);
		byte b2 = (byte) (((data) << 16) >> 24);
		byte b1 = (byte) (((data) << 24) >> 24);
		byte[] bytes = { b1, b2, b3, b4 };
		return bytes;
	}

	public static void grayImage() throws IOException {
		File file = new File("e:/1.bmp");
		BufferedImage image = ImageIO.read(file);

		int width = image.getWidth();
		int height = image.getHeight();

		BufferedImage grayImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb = image.getRGB(i, j);
				grayImage.setRGB(i, j, rgb);
			}
		}

		File newFile = new File("e:/5.bmp");
		ImageIO.write(grayImage, "bmp", newFile);
	}

	public static BufferedImage resizeImage(final BufferedImage bufferedimage,
			final int w, final int h) {
		int type = bufferedimage.getColorModel().getTransparency();
		BufferedImage img;
		Graphics2D graphics2d;
		(graphics2d = (img = new BufferedImage(w, h, type)).createGraphics())
				.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2d.drawImage(bufferedimage, 0, 0, w, h, 0, 0,
				bufferedimage.getWidth(), bufferedimage.getHeight(), null);
		graphics2d.dispose();
		return img;
	}

	public static BufferedImage rotateImage(final BufferedImage bufferedimage,
			final int degree) {
		int w = bufferedimage.getWidth();
		int h = bufferedimage.getHeight();
		int type = bufferedimage.getColorModel().getTransparency();
		BufferedImage img;
		Graphics2D graphics2d;
		(graphics2d = (img = new BufferedImage(w, h, type)).createGraphics())
				.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
		graphics2d.drawImage(bufferedimage, 0, 0, null);
		graphics2d.dispose();
		return img;
	}

	public static BufferedImage flipImage(final BufferedImage bufferedimage) {
		int w = bufferedimage.getWidth();
		int h = bufferedimage.getHeight();
		BufferedImage img;
		Graphics2D graphics2d;
		(graphics2d = (img = new BufferedImage(w, h, bufferedimage
				.getColorModel().getTransparency())).createGraphics())
				.drawImage(bufferedimage, 0, 0, w, h, w, 0, 0, h, null);
		graphics2d.dispose();
		return img;
	}
}
