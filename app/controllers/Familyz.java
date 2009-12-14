package controllers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import jobs.ResizeImageJob;
import models.Family;
import models.Photo;
import models.User;

import org.apache.commons.io.FileUtils;

import play.libs.Codec;

public class Familyz extends Basez {
	public static void neighbours(String family) {
		Family me = Family.find("code = ?", family).first();
		List<Family> followings = Family.find(" select f from Family f, RelationShip r where r.toFamily = f and r.fromFamily = ?", me).fetch();
		List<Family> followers = Family.find(" select f from Family f, RelationShip r where r.fromFamily = f and r.toFamily = ?", me).fetch();
		render(followers, followings);
	}

	public static void updateName(String family, String value) {
		Family me = Family.find("code = ?", family).first();
		me.name = value;
		me.save();
		renderText(value);
	}

	public static void updateSlogan(String family, String value) {
		Family me = Family.find("code = ?", family).first();
		me.slogan = value;
		me.save();
		renderText(value);
	}

	public static void updateImage(String family, File upload) {
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
			
			Family f = Family.find("code = ?", family).first();
			f.image = thumbfilepath;
			f.save();
			
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
