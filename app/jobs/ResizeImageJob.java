package jobs;

import java.io.File;

import play.Play;
import play.jobs.Job;
import utils.ImageMagick;
import utils.ImageUtil;

/**
 * 
 * @author Administrator
 */
public class ResizeImageJob extends Job<File> {
	private File file;
	private int width, height;
	private File outputFile;

	public ResizeImageJob(File file) {
		this(file, 800, 600);
	}

	public ResizeImageJob(File file, int width, int height, File outputFile) {
		this.file = file;
		this.width = width;
		this.height = height;
		this.outputFile = outputFile;
	}

	public ResizeImageJob(File file, int width, int height) {
		this.file = file;
		this.width = width;
		this.height = height;
		this.outputFile = null;
	}

	@Override
	public File doJobWithResult() throws Exception {
		String staticpath = Play.configuration.getProperty("staticpath","");		
		File ofile = outputFile;
		if (outputFile == null)
			ofile = new File(this.file.getParent(), String.format("%1s%2sx%3s.jpg", this.file.getName(), this.width, this.height));
		ImageUtil.saveJPEG(ImageUtil.thumbnail(ImageUtil.load(new File( staticpath+file.getPath() ) ), width, height ), new File( staticpath+outputFile.getPath() ) );
//		ImageMagick.thumbnail(staticpath+file.getPath(), staticpath+outputFile.getPath(), width, height);
		return ofile;
	}
}
