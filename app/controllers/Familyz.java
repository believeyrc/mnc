package controllers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.Family;
import models.Photo;
import models.User;
import play.libs.Codec;
import utils.PhotoUploaderUtil;

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
		String uuid = Codec.UUID();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String tDate = sdf.format(new Date());
		String filePath = "public/upload/" + tDate + "/" + uuid;
		Photo photo = PhotoUploaderUtil.processPhoto(upload);
		if (photo != null) {
			photo.author = User.find("byEmail", Security.connected()).first();
			photo.save();
			Family f = Family.find("code = ?", family).first();
			f.image = photo.thumbPath;
			f.save();
			renderText("{'err':'','msg':'/%s','original':'/%s','thumb':'/%s','pref':'/%s'}", photo.prefPath, photo.filePath, photo.thumbPath, photo.prefPath);
		}
	}
}
