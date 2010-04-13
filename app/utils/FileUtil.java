package utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import play.Logger;
import play.Play;

public class FileUtil {
	public static void moveUploadTo(File upload, String to) {
		try {
			String staticpath = Play.configuration.getProperty("staticpath", "");
			File ofile = new File(staticpath + to);
			FileUtils.moveFile(upload, ofile);
		} catch (IOException e) {
			Logger.error("move file error ", e);
		}
	}

	public static String getPath(String file) {
		return new File(file).getParent()+File.separator;
	}
	public static void main(String[] args) {
		System.out.println(getPath("c:/test/a"));
	}
}
