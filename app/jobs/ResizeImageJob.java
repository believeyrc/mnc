package jobs;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.jhlabs.image.ScaleFilter;

import play.jobs.Job;

/**
 *
 * @author Administrator
 */
public class ResizeImageJob extends Job{
	private File file;
	private int width,height;
	public ResizeImageJob(File file){
		this(file,800,600);
	}
	public ResizeImageJob(File file,int width,int height){
		this.file = file;
		this.width = width;
		this.height = height;
	}
	@Override
	public File doJobWithResult() throws Exception {
		BufferedImage bi = ImageIO.read(file);
		int ow = bi.getWidth();int oh = bi.getHeight();
		float scale = Math.min(1.0f*this.width/ow, 1.0f*this.height/oh);
		ScaleFilter scaleFilter = new ScaleFilter((int) (ow*scale), (int) (oh*scale));
		BufferedImage dest = new BufferedImage((int) (ow*scale), (int) (oh*scale), BufferedImage.TYPE_3BYTE_BGR);
		scaleFilter.filter(bi, dest);
		File ofile = new File(this.file.getParent(),String.format("%1s%2sx%3s.jpg", this.file.getName(),this.width,this.height));
		ImageIO.write(dest, "JPG", ofile);
		return ofile;
	}
}
