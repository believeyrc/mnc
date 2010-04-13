package utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;

import play.Logger;
import play.Play;

import com.jhlabs.image.CropFilter;
import com.jhlabs.image.RotateFilter;
import com.mortennobel.imagescaling.DimensionConstrain;
import com.mortennobel.imagescaling.ResampleOp;

public class ImageUtil {
	public static void moveUploadTo(File upload, String to) {
		FileUtil.moveUploadTo(upload, to);
	}

	public static BufferedImage load(String file) {
		String staticpath = Play.configuration.getProperty("staticpath", "");
		BufferedImage bi;
		try {
			bi = ImageIO.read(new File(staticpath + file));
			return bi;
		} catch (IOException e) {
			Logger.error(e, "load image failed.");
		}
		return null;
	}

	public static boolean saveJPEG(BufferedImage img, String file) {
		String staticpath = Play.configuration.getProperty("staticpath", "");
		try {
			File ofile = new File(staticpath + file);
			if (!ofile.getParentFile().exists())
				ofile.getParentFile().mkdirs();
			ImageIO.write(img, "JPG", ofile);
			return true;
		} catch (IOException e) {
			Logger.error(e, "load image failed.");
		}
		return false;
	}

	public static BufferedImage scale(BufferedImage img, int width, int height) {
		int ow = img.getWidth();
		int oh = img.getHeight();
		float scale = Math.min(1.0f * width / ow, 1.0f * height / oh);
		if (scale > 1.0)
			return img;
		ResampleOp resampleOp = new ResampleOp((int) (ow * scale), (int) (oh * scale));
		BufferedImage rescaledTomato = resampleOp.filter(img, null);
		return rescaledTomato;
	}
	/**
	 * 
	 * @param img
	 * @param width 宽度
	 * @param height 高度
	 * @param enlargeImage 假如原图片不够大的时候是否要增大图片 true会放大原图片
	 * @param absolution 是否是绝对化，true，不保持原有比例，false会保持原有比例。
	 * @return
	 */
	public static BufferedImage thumbnail(BufferedImage img, int width, int height, boolean enlargeImage, boolean absolution) {
		ResampleOp resampleOp = null;
		if (absolution) {
			resampleOp = new ResampleOp(DimensionConstrain.createAbsolutionDimension(width, height));
		} else {
			resampleOp = new ResampleOp(DimensionConstrain.createMaxDimension(width, height, !enlargeImage));
		}
		BufferedImage rescaledTomato = resampleOp.filter(img, null);
		return rescaledTomato;
	}

	public static BufferedImage roate(BufferedImage src, float angle) {
		BufferedImage dst = new BufferedImage(src.getHeight(), src.getWidth(), src.getType());
		RotateFilter rotateFilter = new RotateFilter(angle);
		rotateFilter.filter(src, dst);
		return dst;
	}

	public static BufferedImage crop(BufferedImage src, int x, int y, int w, int h) {
		CropFilter cropFilter = new CropFilter(x, y, w, h);
		BufferedImage dst = cropFilter.filter(src, null);
		return dst;
	}

	public static BufferedImage grab(String src) {
		try {
			HttpClient client = new HttpClient();
			GetMethod get = new GetMethod(src);
			client.executeMethod(get);
			BufferedImage bi = ImageIO.read(get.getResponseBodyAsStream());
			return bi;
		} catch (HttpException e) {
			Logger.error("grab image error", e);
		} catch (IOException e) {
			Logger.error("grab image error", e);
		}
		return null;
	}
}
