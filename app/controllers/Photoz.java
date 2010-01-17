package controllers;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Photo;
import models.Responses;
import models.User;

import org.apache.commons.io.FileUtils;

import play.Play;
import play.libs.Crypto;
import play.mvc.With;
import utils.BalloonUtil;
import utils.ImageUtil;
import utils.PhotoUploaderUtil;

@With(Secure.class)
public class Photoz extends Basez {

	public static void rotateRight(Long id) throws IOException {
		Photo photo = Photo.findById(id);
		//ImageMagick.rotate(photo.prefPath, photo.prefPath, -90);
        ImageUtil.saveJPEG(ImageUtil.roate(ImageUtil.load(photo.prefPath),(float)(Math.PI /2)),photo.prefPath);
		PhotoUploaderUtil.updateThumbFrom(photo, photo.prefPath);
	}

	public static void rotateLeft(Long id) throws IOException {
		Photo photo = Photo.findById(id);
		//ImageMagick.rotate(photo.prefPath, photo.prefPath, 90);
        ImageUtil.saveJPEG(ImageUtil.roate(ImageUtil.load(photo.prefPath),-(float)(Math.PI /2)), photo.prefPath);
		PhotoUploaderUtil.updateThumbFrom(photo, photo.prefPath);
	}

	public static void sayHello(Long id, String content, int x, int y, int w, int h) throws IOException {
		Photo photo = Photo.find("id = ? and author = ? ", id, getLoginUser()).first();
		if (photo != null) {
			System.out.println(content);
			String path = photo.prefPath;
			BalloonUtil.addEllipseBalloon(path, x, y, w, h, utils.BalloonUtil.EllipseBalloon.TYPE.LB, content);
			PhotoUploaderUtil.updateThumbFrom(photo, photo.prefPath);
		}
	}

	public static void caption(Long id, String val) {
		Photo photo = Photo.find("id = ? and author = ? ", id, getLoginUser()).first();
		if (photo != null) {
			photo.caption = val;
			photo.save();
//			Photo temp = new Photo();
//			temp.caption = photo.caption;
//			temp.id = photo.id;
			renderJSON(val);
		}
	}

	public static void desc(Long id, String val) {
		Photo photo = Photo.find("id = ? and author = ? ", id, getLoginUser()).first();
		if (photo != null) {
			photo.description = val;
			photo.save();
//			Photo temp = new Photo();
//			temp.description = photo.description;
//			temp.id = photo.id;
			renderJSON(val);
		}
	}
	
	public static void revert(Long id) {
		Photo photo = Photo.find("id = ? and author = ? ", id, getLoginUser()).first();
		if (photo != null) {
			PhotoUploaderUtil.updateThumbFrom(photo, photo.filePath);
		}
	}
	
	public static void responses(Long id, String content) {
		Photo photo = Photo.findById(id);
		User user = getLoginUser();
		Responses responses = new Responses(photo, user, content);
		responses.save();
		Photov.viewPhoto(getVisitedUser(),id);
	}

	@Check("ROLE_ADMIN")
	public static void normalize() {
		List<Photo> photos = Photo.all().fetch();
		for (Photo photo : photos) {
			try {
				System.out.println("start normalize " + photo.filePath);
				PhotoUploaderUtil.updateThumbnails(photo);
				System.out.println("normalize " + photo.filePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Check("ROLE_USER")
	public static void remove(String family, Long id) {
		Photo photo = Photo.find("id = ? and author = ? ", id, getLoginUser()).first();
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

	public static void prepareUpload() {
		render();
	}

	public static void upload(String id, File upload) {
		try {
			restroreSession();
			String pathForPhoto = PhotoUploaderUtil.getPathForPhoto();
			String staticpath = Play.configuration.getProperty("staticpath","");
			File ofile = new File(staticpath+pathForPhoto);
			FileUtils.moveFile(upload, ofile);
			Photo photo = new Photo(upload.getName(), new Date(), pathForPhoto);
			photo.prefPath = PhotoUploaderUtil.getPathForLarge(pathForPhoto);
			photo.thumbPath = PhotoUploaderUtil.getPathForSmall(pathForPhoto);
			photo.thumb2Path = PhotoUploaderUtil.getPathForMidle(pathForPhoto);
			photo.author = User.find("byEmail", Security.connected()).first();
			photo.save();
			PhotoUploaderUtil.updateThumbnails(photo);
			// new PostThingJob(Security.connected(),
			// Messages.get("uploadNewImage", getCurrentUser().fullname,
			// photo.id, getCurrentUser().family.code), TYPE.PHOTO).in(1);
			renderText("{'err':'','msg':'/%s','original':'/%s','thumb':'/%s','pref':'/%s'}", photo.prefPath, photo.filePath, photo.thumbPath, photo.prefPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void restroreSession() throws UnsupportedEncodingException {
		String checkuser = params.get("checkuser");
		System.out.println(checkuser);
		if (checkuser == null)
			return;
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