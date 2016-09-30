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
			// ����ԭʼ�������������
			ByteArrayOutputStream dos = new ByteArrayOutputStream();
			// ���ļ�ͷ�ı�����ֵ
			int bfType = 'B' + 'M' * 256; // λͼ�ļ����ͣ�0��1�ֽڣ�
			int bfSize = 1078 + width * height;// bmp�ļ��Ĵ�С��2��5�ֽڣ�
			int bfReserved1 = 0;// λͼ�ļ������֣�����Ϊ0��6-7�ֽڣ�
			int bfReserved2 = 0;// λͼ�ļ������֣�����Ϊ0��8-9�ֽڣ�
			int bfOffBits = 1078;// �ļ�ͷ��ʼ��λͼʵ������֮����ֽڵ�ƫ������10-13�ֽڣ�

			// �������ݵ�ʱ��Ҫע��������������ڴ���Ҫռ�����ֽڣ�
			// Ȼ����ѡ����Ӧ��д�뷽�������������Լ��������������
			// �����ļ�ͷ����
			dos.write(changeByte(bfType), 0, 2); // ����λͼ�ļ�����'BM'
			dos.write(changeByte(bfSize), 0, 4); // ����λͼ�ļ���С
			dos.write(changeByte(bfReserved1), 0, 2);// ����λͼ�ļ�������
			dos.write(changeByte(bfReserved2), 0, 2);// ����λͼ�ļ�������
			dos.write(changeByte(bfOffBits), 0, 4);// ����λͼ�ļ�ƫ����

			// ����Ϣͷ�ı�����ֵ
			int biSize = 40;// ��Ϣͷ������ֽ�����14-17�ֽڣ�
			int biWidth = width;// λͼ�Ŀ�18-21�ֽڣ�
			int biHeight = height;// λͼ�ĸߣ�22-25�ֽڣ�
			int biPlanes = 1; // Ŀ���豸�ļ��𣬱�����1��26-27�ֽڣ�
			int biBitcount = 8;// ÿ�����������λ����28-29�ֽڣ���������1λ��˫ɫ����4λ��16ɫ����8λ��256ɫ������24λ�����ɫ��֮һ��
			int biCompression = 0;// λͼѹ�����ͣ�������0����ѹ������30-33�ֽڣ���1��BI_RLEBѹ�����ͣ���2��BI_RLE4ѹ�����ͣ�֮һ��
			int biSizeImage = width * height;// ʵ��λͼͼ��Ĵ�С��������ʵ�ʻ��Ƶ�ͼ���С��34-37�ֽڣ�
			int biXPelsPerMeter = 0;// λͼˮƽ�ֱ��ʣ�ÿ����������38-41�ֽڣ��������ϵͳĬ��ֵ
			int biYPelsPerMeter = 0;// λͼ��ֱ�ֱ��ʣ�ÿ����������42-45�ֽڣ��������ϵͳĬ��ֵ
			int biClrUsed = 0;// λͼʵ��ʹ�õ���ɫ���е���ɫ����46-49�ֽڣ������Ϊ0�Ļ���˵��ȫ��ʹ����
			int biClrImportant = 0;// λͼ��ʾ��������Ҫ����ɫ��(50-53�ֽ�)�����Ϊ0�Ļ���˵��ȫ����Ҫ

			// ��Ϊjava�Ǵ�˴洢����ôҲ����˵ͬ�����������
			// ��������ǰ�С�˶�ȡ��������ǲ��ı���ֽ����ݵ�˳��Ļ�����ô�����Ͳ���������ȡ��
			// �������ȵ��÷�����int����ת��Ϊ���byte���ݣ����Ұ�С�˴洢��˳��

			// ������Ϣͷ����
			dos.write(changeByte(biSize), 0, 4);// ������Ϣͷ���ݵ����ֽ���
			dos.write(changeByte(biWidth), 0, 4);// ����λͼ�Ŀ�
			dos.write(changeByte(biHeight), 0, 4);// ����λͼ�ĸ�
			dos.write(changeByte(biPlanes), 0, 2);// ����λͼ��Ŀ���豸����
			dos.write(changeByte(biBitcount), 0, 2);// ����ÿ������ռ�ݵ��ֽ���
			dos.write(changeByte(biCompression), 0, 4);// ����λͼ��ѹ������
			dos.write(changeByte(biSizeImage), 0, 4);// ����λͼ��ʵ�ʴ�С
			dos.write(changeByte(biXPelsPerMeter), 0, 4);// ����λͼ��ˮƽ�ֱ���
			dos.write(changeByte(biYPelsPerMeter), 0, 4);// ����λͼ�Ĵ�ֱ�ֱ���
			dos.write(changeByte(biClrUsed), 0, 4);// ����λͼʹ�õ�����ɫ��
			dos.write(changeByte(biClrImportant), 0, 4);// ����λͼʹ�ù�������Ҫ����ɫ��
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
