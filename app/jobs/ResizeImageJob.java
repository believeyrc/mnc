package jobs;

import java.io.File;

import play.jobs.Job;
import utils.ImageUtil;

/**
 *
 * @author Administrator
 */
public class ResizeImageJob extends Job<File>{
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
		File ofile = new File(this.file.getParent(),String.format("%1s%2sx%3s.jpg", this.file.getName(),this.width,this.height));
		ImageUtil.saveJPEG(ImageUtil.scale(ImageUtil.load(file), width, height),ofile);
		return ofile;
	}
}
