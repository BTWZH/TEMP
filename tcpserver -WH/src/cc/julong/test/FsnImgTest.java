package cc.julong.test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jhlabs.image.UnsharpFilter;
import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;

public class FsnImgTest {

	public static void binaryImage() throws IOException {
		File file = new File("e:/1.bmp");
		BufferedImage src2 = ImageIO.read(file);
		
		BufferedImage src = new BufferedImage(src2.getWidth(),
				src2.getHeight(), BufferedImage.TYPE_INT_ARGB);
		UnsharpFilter c = new UnsharpFilter();
		c.setAmount(0.8f);
		c.filter(src2, src);
	
		// File newFile2 = new File("e:/xxx2.bmp");
		// ImageIO.write(src, "jpg", newFile2);

		ResampleOp resampleOp = new ResampleOp(320, 32);
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);
		BufferedImage image = resampleOp.filter(src, null);
		int width = image.getWidth();
		int height = image.getHeight();

		BufferedImage grayImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_BINARY);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb = image.getRGB(i, j);
				grayImage.setRGB(i, j, rgb);
			}
		}

		// ResampleOp resampleOp = new ResampleOp(320, 32);
		// resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.VerySharp);

		File newFile = new File("e:/1bi2n8.bmp");
		ImageIO.write(grayImage, "bmp", newFile);

	}

	public static void test2() throws Throwable, IOException {
		File file = new File("e:\\1.bmp");
		BufferedImage img = ImageIO.read(new FileInputStream(file));
		ImageIO.write(img, "tif", new FileOutputStream(new File("e:\\1.tif")));
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			binaryImage();
			// grayImage();
			// test2();
			// Image imagesoure = new Image();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

class Image {
	int width;
	int height;
	int area = width * height;

	// BufferedImage outBinary=img;
	public Image() throws IOException {
		File file = new File("e:/1.bmp");
		BufferedImage img = ImageIO.read(file);
		BufferedImage outBinary = img;
		width = img.getWidth();
		height = img.getHeight();
		int area = width * height;
		int gray[][] = new int[width][height];
		int u = 0;// 灰度平均值
		@SuppressWarnings("unused")
        int graybinary;
		int graysum = 0;
		int graymean = 0;
		int grayfrontmean = 0;
		int graybackmean = 0;
		Color color;
		int pixl[][] = new int[width][height];
		int pixelsR;
		int pixelsG;
		int pixelsB;
		int pixelGray;
		@SuppressWarnings("unused")
        int T = 0;
		int front = 0;
		int back = 0;
		for (int i = 1; i < width; i++) { // 不算边界行和列，为避免越界
			for (int j = 1; j < height; j++) {
				pixl[i][j] = img.getRGB(i, j);
				color = new Color(pixl[i][j]);
				pixelsR = color.getRed();// R空间
				pixelsB = color.getBlue();// G空间
				pixelsG = color.getGreen();// B空间
				pixelGray = (int) (0.3 * pixelsR + 0.59 * pixelsG + 0.11 * pixelsB);// 计算每个坐标点的灰度
				gray[i][j] = (pixelGray << 16) + (pixelGray << 8) + (pixelGray);
				graysum += pixelGray;
			}
		}
		graymean = graysum / area;// 整个图的灰度平均值
		u = graymean;
		System.out.println(u);
		for (int i = 0; i < width; i++) // 计算整个图的二值化阈值
		{
			for (int j = 0; j < height; j++) {
				if (((gray[i][j]) & (0x0000ff)) < graymean) {
					graybackmean += ((gray[i][j]) & (0x0000ff));
					back++;
				} else {
					grayfrontmean += ((gray[i][j]) & (0x0000ff));
					front++;
				}
			}
		}
		int frontvalue = grayfrontmean / front;// 前景中心
		int backvalue = graybackmean / back;// 背景中心
		float G[] = new float[frontvalue - backvalue + 1];// 方差数组
		int s = 0;
		System.out.println(front);
		System.out.println(frontvalue);
		System.out.println(backvalue);
		for (int i1 = backvalue; i1 < frontvalue + 1; i1++)// 以前景中心和背景中心为区间采用大津法算法
		{
			back = 0;
			front = 0;
			grayfrontmean = 0;
			graybackmean = 0;
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					if (((gray[i][j]) & (0x0000ff)) < (i1 + 1)) {
						graybackmean += ((gray[i][j]) & (0x0000ff));
						back++;
					} else {
						grayfrontmean += ((gray[i][j]) & (0x0000ff));
						front++;
					}
				}
			}
			grayfrontmean = grayfrontmean / front;
			graybackmean = graybackmean / back;
			G[s] = (((float) back / area) * (graybackmean - u)
					* (graybackmean - u) + ((float) front / area)
					* (grayfrontmean - u) * (grayfrontmean - u));
			s++;
		}
		float max = G[0];
		int index = 0;
		for (int i = 1; i < frontvalue - backvalue + 1; i++) {
			if (max < G[i]) {
				max = G[i];
				index = i;
			}
		}
		// System.out.println(G[index]);
		// System.out.println(index);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (((gray[i][j]) & (0x0000ff)) < (index + backvalue)) {
					outBinary.setRGB(i, j, 0x000000);
				} else {
					outBinary.setRGB(i, j, 0xffffff);
				}
			}
		}
		File f = new File("e:/3.jpg");
		ResampleOp resampleOp = new ResampleOp(320, 32);
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.VerySharp);
		BufferedImage rescaled = resampleOp.filter(outBinary, null);
		 File newFile = new File("e:/1bi2n2.bmp");
		ImageIO.write(rescaled, "jpg", f);
	}
 }
