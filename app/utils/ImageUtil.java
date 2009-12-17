package utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import play.Logger;

import com.mortennobel.imagescaling.ResampleOp;

public class ImageUtil {
	public static BufferedImage load(File file) {
		BufferedImage bi;
		try {
			bi = ImageIO.read(file);
			return bi;
		} catch (IOException e) {
			Logger.error(e, "load image failed.");
		}
		return null;
	}

	public static boolean saveJPEG(BufferedImage img, File file) {
		BufferedImage bi;
		try {
			ImageIO.write(img, "JPG", file);
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
		if(scale >1.0)
			return img;
		ResampleOp resampleOp = new ResampleOp((int) (ow * scale), (int) (oh * scale));
		BufferedImage rescaledTomato = resampleOp.filter(img, null);
		return rescaledTomato;
	}
}
