package controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jobs.PostThingJob;

import models.Photo;
import models.Responses;
import models.User;
import models.Thing.TYPE;

import org.apache.commons.io.FileUtils;

import play.Play;
import play.i18n.Messages;
import play.libs.Crypto;
import utils.BalloonUtil;
import utils.ImageUtil;
import utils.PhotoUploaderUtil;

public class Photoz extends Basez {

	public static void photos(String family, int offset) {
		if (offset <= 0)
			offset = 0;
		int pageSize = 12;
		long totalCount = Photo.count(" author.family.code = ? ", family);
		List<Photo> photos = Photo.find(" author.family.code = ? order by id desc", family).from(offset).fetch(pageSize);
		render(photos, offset, totalCount, pageSize);
	}

	public static void carousel(String family) {
		List<Photo> photos = Photo.find(" author.family.code = ? order by id desc", family).fetch();
		render(photos);
	}

	public static void galleria(String family) {
		List<Photo> photos = Photo.find(" author.family.code = ? order by id desc", family).fetch();
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

	public static void rotateRight(Long id) throws IOException {
		Photo photo = Photo.findById(id);
		String file = photo.prefPath;
		ImageUtil.saveJPEG(ImageUtil.roate(ImageUtil.load(new File(file)), (float) (Math.PI / 2)), new File(file));
		PhotoUploaderUtil.updateThumbFrom(photo,photo.prefPath);
	}

	public static void rotateLeft(Long id) throws IOException {
		Photo photo = Photo.findById(id);
		String file = photo.prefPath;
		ImageUtil.saveJPEG(ImageUtil.roate(ImageUtil.load(new File(file)), -(float) (Math.PI / 2)), new File(file));
		PhotoUploaderUtil.updateThumbFrom(photo,photo.prefPath);
	}

	public static void sayHello(Long id, String content, int x, int y,int w,int h) throws IOException {
		Photo photo = Photo.find("id = ? and author = ? ", id, getCurrentUser()).first();
		if (photo != null) {
			System.out.println(content);
			String path = photo.prefPath;
			File file = new File(path);
			BufferedImage img = ImageUtil.load(file);
			BalloonUtil.addEllipseBalloon(img, x, y, w, h, utils.BalloonUtil.EllipseBalloon.TYPE.LB, content);
			ImageUtil.saveJPEG(img, file);
			PhotoUploaderUtil.updateThumbFrom(photo,photo.prefPath);
		}
	}

	@Check("ROLE_USER")
	public static void caption(Long id, String caption) {
		Photo photo = Photo.find("id = ? and author = ? ", id, getCurrentUser()).first();
		if (photo != null) {
			photo.caption = caption;
			photo.save();
			Photo temp = new Photo();
			temp.caption = photo.caption;
			temp.id = photo.id;
			renderJSON(photo);
		}
	}
	public static void revert(Long id) {
		Photo photo = Photo.find("id = ? and author = ? ", id, getCurrentUser()).first();
		if (photo != null) {
			PhotoUploaderUtil.updateThumbFrom(photo,photo.filePath);
		}
	}
	public static void viewPhoto(Long id) {
		Photo photo = Photo.findById(id);
		List<Responses> responses = Responses.find(" photo = ? order by postedAt asc", photo).fetch();
		render(photo, responses);
	}

	public static void responses(Long id, String content) {
		Photo photo = Photo.findById(id);
		User user = getCurrentUser();
		Responses responses = new Responses(photo, user, content);
		responses.save();
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

	@Check("ROLE_USER")
	public static void remove(String family, Long id) {
		Photo photo = Photo.find("id = ? and author = ? ", id, getCurrentUser()).first();
		if (photo != null) {
			if (photo.filePath != null)
				FileUtils.deleteQuietly(new File(photo.filePath));
			if (photo.thumbPath != null)
				FileUtils.deleteQuietly(new File(photo.thumbPath));
			if (photo.thumb2Path != null)
				FileUtils.deleteQuietly(new File(photo.thumb2Path));
			if (photo.prefPath != null)
				FileUtils.deleteQuietly(new File(photo.prefPath));
			photo.delete();
			renderJSON("{msg:'ok'}");
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
				new PostThingJob(Security.connected(), Messages.get("uploadNewImage", getCurrentUser().fullname, photo.id, getCurrentUser().family.code), TYPE.PHOTO).in(1);
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