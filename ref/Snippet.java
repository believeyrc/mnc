import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jhlabs.image.BicubicScaleFilter;
import com.jhlabs.image.GaussianFilter;

public class Snippet {
	public static void main(String[] args) throws IOException {

		File file = new File("C:\\Documents and Settings\\Administrator\\桌面\\img\\0202-0.jpg");
		BufferedImage bi = ImageIO.read(file);
		int ow = bi.getWidth();
		int oh = bi.getHeight();
		int height = 120;
		int width = 100;
		float scale = Math.min(1.0f * width / ow, 1.0f * height / oh);
		AreaAveragingScaleFilter areaAveragingScaleFilter = new AreaAveragingScaleFilter(width, height);
		FilteredImageSource filteredImageSource = new FilteredImageSource(bi.getSource(), areaAveragingScaleFilter);
		BufferedImage bufferedImage = new BufferedImage((int) (ow * scale), (int) (oh * scale), BufferedImage.TYPE_3BYTE_BGR);
		Graphics graphics = bufferedImage.createGraphics();
		graphics.drawImage(Toolkit.getDefaultToolkit().createImage(filteredImageSource), 0, 0, null);
		graphics.dispose();
		ImageIO.write(bufferedImage, "jpg", new File("C:\\Documents and Settings\\Administrator\\桌面\\img\\0202-4.jpg"));
	}

	private static void userFilter(File file, BufferedImage bi, int ow, int oh, int height, int width, float scale)
			throws IOException {
		BicubicScaleFilter scaleFilter = new BicubicScaleFilter((int) (ow * scale), (int) (oh * scale));
		BufferedImage dest = new BufferedImage((int) (ow * scale), (int) (oh * scale), BufferedImage.TYPE_INT_RGB);
		scaleFilter.filter(bi, dest);
		GaussianFilter blurFilter = new GaussianFilter();
		// dest = blurFilter.filter(dest, null);
		File ofile = new File(file.getParent(), String.format("%1s%2sx%3s.jpg", file.getName(), width, height));
		ImageIO.write(dest, "JPG", ofile);
	}
}