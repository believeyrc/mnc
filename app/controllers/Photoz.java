package controllers;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Photo;
import models.User;

import org.apache.commons.io.FileUtils;

import play.Play;
import play.libs.Crypto;
import utils.PhotoUploaderUtil;

public class Photoz extends Basez {

	public static void photos(String family, int offset) {
		if (offset <= 0)
			offset = 0;
		int pageSize = 20;
		long totalCount = Photo.count(" author.family.code = ? order by uploadAt desc", family);
		List<Photo> photos = Photo.find(" author.family.code = ? order by uploadAt desc", family).from(offset).fetch(pageSize);
		render(photos, offset, totalCount, pageSize);
	}

	public static void carousel(String family) {
		List<Photo> photos = Photo.find(" author.family.code = ? order by uploadAt desc", family).fetch();
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

	public static void caption(Long id, String caption) {
		Photo photo = Photo.findById(id);
		photo.caption = caption;
		photo.save();
		renderText(caption);
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
				System.out.println("start normalize " + photo.filePath);
				PhotoUploaderUtil.processPhoto(photo);
				if (photo != null) {
					photo.author = User.find("byEmail", Security.connected()).first();
					photo.save();
				}
				System.out.println("normalize " + photo.filePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void remove(String family, Long id) {
		Photo photo = Photo.find("byId", id).first();
		if (photo != null) {
			FileUtils.deleteQuietly(new File(photo.filePath));
			FileUtils.deleteQuietly(new File(photo.thumbPath));
			FileUtils.deleteQuietly(new File(photo.thumb2Path));
			FileUtils.deleteQuietly(new File(photo.prefPath));
			photo.delete();
		}
	}

	static Pattern sessionParser = Pattern.compile("\u0000([^:]*):([^\u0000]*)\u0000");

	@Check("ROLE_USER")
	public static void upload(String id, File upload) {
		try {
			restroreSession();
			Photo photo = PhotoUploaderUtil.processPhoto(upload);
			if (photo != null) {
				photo.author = User.find("byEmail", Security.connected()).first();
				photo.save();
				renderText("{'err':'','msg':'/%s','original':'/%s','thumb':'/%s','pref':'/%s'}", photo.prefPath, photo.filePath, photo.thumbPath, photo.prefPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void restroreSession() throws UnsupportedEncodingException {
		String checkuser = params.get("checkuser");
		checkuser = checkuser.replaceAll("##", "\u0000");
		checkuser = URLEncoder.encode(checkuser, "utf-8");
		String sign = checkuser.substring(0, checkuser.indexOf("-"));
		String data = checkuser.substring(checkuser.indexOf("-") + 1);
		if (sign.equals(Crypto.sign(data, Play.secretKey.getBytes()))) {
			String sessionData = URLDecoder.decode(data, "utf-8");
			Matcher matcher = sessionParser.matcher(sessionData);
			while (matcher.find()) {
				session.put(matcher.group(1), matcher.group(2));
			}
		}
	}
}