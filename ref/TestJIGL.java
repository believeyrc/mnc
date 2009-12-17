import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import jigl.image.ColorModelNotSupportedException;
import jigl.image.ColorModelUnknownException;
import jigl.image.ImageNotSupportedException;
import jigl.image.UnknownImageTypeException;
import jigl.image.io.IllegalPBMFormatException;
import net.sourceforge.jiu.codecs.CodecMode;
import net.sourceforge.jiu.codecs.PNGCodec;
import net.sourceforge.jiu.data.PixelImage;
import net.sourceforge.jiu.geometry.Resample;
import net.sourceforge.jiu.geometry.ResampleFilter;
import net.sourceforge.jiu.geometry.ScaleReplication;
import net.sourceforge.jiu.gui.awt.ToolkitLoader;
import net.sourceforge.jiu.ops.MissingParameterException;
import net.sourceforge.jiu.ops.OperationFailedException;

import com.mortennobel.imagescaling.ResampleOp;

public class TestJIGL {
	public static void main(String[] args) throws IOException, ImageNotSupportedException, IllegalPBMFormatException,
			InterruptedException, UnknownImageTypeException, ColorModelNotSupportedException, ColorModelUnknownException, MissingParameterException, OperationFailedException {
		jiu();
		imagescaling();
	}

	private static void jiu() {
		try {
		String infile = "C:\\Documents and Settings\\Administrator\\桌面\\img\\0202-0.JPG";
		
		PixelImage bi = ToolkitLoader.loadViaToolkitOrCodecs(infile);
		int ow = bi.getWidth();
		int oh = bi.getHeight();
		int height = 120;
		int width = 100;
		Resample resample = new Resample();
		resample.setFilter(Resample.FILTER_TYPE_B_SPLINE);
		ResampleFilter filter = resample.getFilter();
		filter.setSamplingRadius(filter.getRecommendedSamplingRadius() * 50);
//		ScaleReplication scaleReplication = new ScaleReplication();
		float scale = Math.min(1.0f * width / ow, 1.0f * height / oh);
//		scaleReplication.setSize((int) (ow * scale), (int) (oh * scale));
//		scaleReplication.setInputImage(bi);
//		scaleReplication.process();
//		PixelImage outputImage = scaleReplication.getOutputImage();
		resample.setSize((int) (ow * scale), (int) (oh * scale));
		resample.setInputImage(bi);
		resample.process();
		PixelImage outputImage = resample.getOutputImage();
		
		PNGCodec codec = new PNGCodec();
		String ofile = "C:\\Documents and Settings\\Administrator\\桌面\\img\\0202-6.jpg";
		codec.setFile(ofile, CodecMode.SAVE);
		codec.setImage(outputImage);
		codec.process();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void imagescaling() throws IOException {
		String infile = "C:\\Documents and Settings\\Administrator\\桌面\\img\\0202-0.jpg";
//		ImageInputStreamJAI ia = new ImageInputStreamJAI(infile);
//		Image image = ia.read();
//		Scale scale = new Scale(100, 120);
//		Image apply = scale.apply(image);
		String ofile = "C:\\Documents and Settings\\Administrator\\桌面\\img\\0202-5.jpg";
//		ImageOutputStreamJAI io = new ImageOutputStreamJAI(ofile);
//		io.writeJPEG(apply);
		BufferedImage bi = ImageIO.read(new File(infile));
		int ow = bi.getWidth();
		int oh = bi.getHeight();
		int height = 120;
		int width = 100;
		float scale = Math.min(1.0f * width / ow, 1.0f * height / oh);
		ResampleOp resampleOp = new ResampleOp((int) (ow * scale), (int) (oh * scale));
		BufferedImage rescaledTomato = resampleOp.filter(bi, null);
		ImageIO.write(rescaledTomato, "JPG", new File(ofile));
	}
}
