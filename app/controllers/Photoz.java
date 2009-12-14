package controllers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import jobs.ResizeImageJob;
import models.Photo;
import models.User;

import org.apache.commons.io.FileUtils;

import play.libs.Codec;

public class Photoz extends Basez {

	public static void photos() {
		List<Photo> photos = Photo.all().fetch();
		render(photos);
	}

	public static void gallery() {
		List<Photo> photos = Photo.all().fetch();
		render(photos);
	}

	public static void galleria(String family) {
		List<Photo> photos = Photo.find(" author.family.code = ? order by uploadAt desc", family).fetch();
		render(photos);
	}

	public static void nextPicture(Long id) {
		if (id == null)
			id = 0L;
		long more = Photo.count("id > ?", id);
		if (more == 0)
			id = 0L;
		Photo photo = Photo.find("id > ? order by id asc", id).first();
		renderText("{ \"more\":%s, \"id\":%s, \"path\":\"%s\" }", more > 1, photo.id, photo.filePath);
	}

	public static void previousPicture(Long id) {
		if (id == null)
			id = 0L;
		long more = Photo.count("id < ?", id);
		if (more == 0)
			id = Long.MAX_VALUE;

		Photo photo = Photo.find("id < ? order by id desc", id).first();
		renderText("{\"more\":%s,\"id\":%s,\"path\":\"%s\"}", more > 1, photo.id, photo.filePath);
	}

	@Check("ROLE_ADMIN")
	public static void normalize() {
		List<Photo> photos = Photo.all().fetch();
		for (Photo photo : photos) {
			try {
				File ofile = new File(photo.filePath);
				ResizeImageJob pref = new ResizeImageJob(ofile, 700, 440);
				ResizeImageJob thumb = new ResizeImageJob(ofile, 80, 60);
				Future<File> prefFuture = pref.now();
				Future<File> thumbFuture = thumb.now();
				File preffile = prefFuture.get();
				File thumbfile = thumbFuture.get();
				String preffilepath = preffile.getPath().replaceAll("\\\\", "/");
				String thumbfilepath = thumbfile.getPath().replaceAll("\\\\", "/");
				photo.prefPath = preffilepath;
				photo.thumbPath = thumbfilepath;
				photo.save();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	public static void remove(String family, Long id) {
		Photo photo = Photo.find("byId", id).first();
		if (photo != null) {
			FileUtils.deleteQuietly(new File(photo.filePath));
			FileUtils.deleteQuietly(new File(photo.thumbPath));
			FileUtils.deleteQuietly(new File(photo.prefPath));
			photo.delete();
		}
	}

	public static void upload(String id, File upload) {
		try {
			String uuid = Codec.UUID();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			String tDate = sdf.format(new Date());
			String filePath = "public/upload/" + tDate + "/" + uuid;
			File ofile = new File(filePath);
			FileUtils.moveFile(upload, ofile);
			ResizeImageJob pref = new ResizeImageJob(ofile, 700, 440);
			ResizeImageJob thumb = new ResizeImageJob(ofile, 80, 60);
			Future<File> prefFuture = pref.now();
			Future<File> thumbFuture = thumb.now();
			File preffile = prefFuture.get();
			File thumbfile = thumbFuture.get();
			String preffilepath = preffile.getPath().replaceAll("\\\\", "/");
			String thumbfilepath = thumbfile.getPath().replaceAll("\\\\", "/");

			Photo photo = new Photo(upload.getName(), new Date(), filePath);
			photo.prefPath = preffilepath;
			photo.thumbPath = thumbfilepath;
			photo.author = User.find("byEmail", Security.connected()).first();
			photo.save();
			renderText("{'err':'','msg':'/%s','original':'/%s','thumb':'/%s','pref':'/%s'}", preffilepath, filePath, thumbfilepath, preffilepath);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}