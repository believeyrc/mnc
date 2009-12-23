package utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import jobs.ResizeImageJob;
import models.Photo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import play.libs.Codec;

public class PhotoUploaderUtil {
	public static Photo processPhoto(Photo photo) {
		try {
			File ofile = new File(photo.filePath);
			ResizeImageJob pref = new ResizeImageJob(ofile, 700, 440);
			ResizeImageJob thumb = new ResizeImageJob(ofile, 80, 60);
			ResizeImageJob thumb2 = new ResizeImageJob(ofile, 140, 90);
			Future<File> prefFuture = pref.now();
			Future<File> thumbFuture = thumb.now();
			Future<File> thumb2Future = thumb2.now();
			File preffile = prefFuture.get();
			File thumbfile = thumbFuture.get();
			File thumb2file = thumb2Future.get();
			String preffilepath = preffile.getPath().replaceAll("\\\\", "/");
			String thumbfilepath = thumbfile.getPath().replaceAll("\\\\", "/");
			String thumb2filepath = thumb2file.getPath().replaceAll("\\\\", "/");

			photo.prefPath = preffilepath;
			photo.thumbPath = thumbfilepath;
			photo.thumb2Path = thumb2filepath;
			return photo;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void updateThumbFrom(Photo photo,String from ) {
		File ofile = new File(from);
		if(!StringUtils.equals(photo.prefPath,from))
			new ResizeImageJob(ofile, 700, 440,new File(photo.prefPath)).in(1);
		ResizeImageJob thumb = new ResizeImageJob(ofile, 80, 60,new File(photo.thumbPath));
		ResizeImageJob thumb2 = new ResizeImageJob(ofile, 140, 90,new File(photo.thumb2Path));
		Future<File> thumbFuture = thumb.in(1);
		Future<File> thumb2Future = thumb2.in(1);
	}

	public static Photo processPhoto(File upload) {
		try {
			String uuid = Codec.UUID();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			String tDate = sdf.format(new Date());
			String filePath = "public/upload/" + tDate + "/" + uuid;
			File ofile = new File(filePath);
			FileUtils.moveFile(upload, ofile);
			Photo photo = new Photo(upload.getName(), new Date(), filePath);
			return processPhoto(photo);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

}
