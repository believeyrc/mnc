package utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import jobs.ResizeImageJob;
import models.Photo;

import org.apache.commons.lang.StringUtils;

import play.libs.Codec;

public class PhotoUploaderUtil {
	public static final int SMALL_SIZE = 75;
	public static final int MIDLE_SIZE = 240;
	public static final int LARGE_SIZE = 500;

	public static void updateThumbnails(Photo photo) {
		File ofile = new File(photo.filePath);
		new ResizeImageJob(ofile, LARGE_SIZE, LARGE_SIZE, new File(photo.prefPath)).in(0);
		new ResizeImageJob(ofile, SMALL_SIZE, SMALL_SIZE, new File(photo.thumbPath), false, true).in(0);
		new ResizeImageJob(ofile, MIDLE_SIZE, MIDLE_SIZE, new File(photo.thumb2Path)).in(0);
	}

	public static void updateThumbFrom(Photo photo, String from) {
		File ofile = new File(from);
		if (!StringUtils.equals(photo.prefPath, from))
			new ResizeImageJob(ofile, LARGE_SIZE, LARGE_SIZE, new File(photo.prefPath)).in(0);
		if (!StringUtils.equals(photo.thumbPath, from))
			new ResizeImageJob(ofile, SMALL_SIZE, SMALL_SIZE, new File(photo.thumbPath)).in(0);
		if (!StringUtils.equals(photo.thumb2Path, from))
			new ResizeImageJob(ofile, MIDLE_SIZE, MIDLE_SIZE, new File(photo.thumb2Path)).in(0);
	}

	public static String getPathForSmall(String base) {
		return base + ".75.jpg";
	}

	public static String getPathForMidle(String base) {
		return base + ".240.jpg";
	}

	public static String getPathForLarge(String base) {
		return base + ".700.jpg";
	}

	public static String getPathForPhoto() {
		String uuid = Codec.UUID();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String tDate = sdf.format(new Date());
		String filePath = "public/upload/" + tDate + "/" + uuid;
		return filePath;
	}
}
