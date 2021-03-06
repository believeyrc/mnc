package controllers;

import java.io.File;
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
import play.Play;
import play.libs.Crypto;
import utils.FileUtil;
import utils.ImageUtil;
import utils.PhotoUploaderUtil;

public class Photov extends Basez {
    public static void home(String username) {
	    int	 page = 1;
		int pageSize = 12;
		long totalCount = Photo.countOfUser(username);
		List<Photo> photos = Photo.find(" author.fullname = ? order by id desc", username).from(pageSize * (page - 1)).fetch(pageSize);
		render("Photov/photos.html",photos, page, totalCount, pageSize);
    }
	public static void photos(String username, int page) {
		if (page <= 1)
			page = 1;
		int pageSize = 12;
		long totalCount = Photo.countOfUser(username);
		List<Photo> photos = Photo.find(" author.fullname = ? order by id desc", username).from(pageSize * (page - 1)).fetch(pageSize);
		render(photos, page, totalCount, pageSize);
	}
	public static void photosWith(String username, Long id) {
		int pageSize = 12;
		long totalCount = Photo.countOfUser(username);
		long position = Photo.count(" author.fullname = ? and id > ? order by id desc ",username, id);
		int page = (int) (position/pageSize)+1;
		List<Photo> photos = Photo.find(" author.fullname = ? order by id desc", username).from(pageSize * (page - 1)).fetch(pageSize);
		render("Photov/photos.html",photos, page, totalCount, pageSize);
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

    public static void viewPhoto(String username, Long id) {
        Photo photo = Photo.findById(id);
        List<Responses> responses = Responses.loadPhotoResponses(photo);
        render(photo, responses);
    }
   public static void viewPhotoInSets(String username, Long photoid, Long setsid) {
        Photo photo = Photo.findById(photoid);       
        List<Responses> responses = Responses.loadPhotoResponses(photo);
        boolean nostream = true;
        render("Photov/viewPhoto.html",photo, responses, setsid, nostream);
    }
   
   public static void streamPrevious(String username,Long id) {
	   Photo photo = Photo.findById(id);
	   long totalCount = photo.countOfUser();
	   Photo previous = photo.previous();
	   Photo next = photo;
	   render("Photov/streamInfo.html",photo, totalCount, previous, next);
   }
   
   public static void streamInfo(String username,Long id) {
	   Photo photo = Photo.findById(id);
	   long totalCount = photo.countOfUser();
	   Photo previous = photo.previous();
	   Photo next = photo.next();
	   render("Photov/streamInfo.html",photo, totalCount, previous, next);
   }
   
   public static void streamNext(String username,Long id) {
	   Photo photo = Photo.findById(id);
	   long totalCount = photo.countOfUser();
	   Photo previous = photo;
	   Photo next = photo.next();
	   render("Photov/streamInfo.html",photo, totalCount, previous, next);
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
	public static void upload(File upload) {
		try {
			restroreSession();
			String pathForPhoto = PhotoUploaderUtil.getPathForPhoto() + FileUtil.getExtend(upload);
			ImageUtil.moveUploadTo(upload, pathForPhoto);
			Photo photo = new Photo(upload.getName(), new Date(), pathForPhoto);
			photo.prefPath = PhotoUploaderUtil.getPathForLarge(pathForPhoto);
			photo.thumbPath = PhotoUploaderUtil.getPathForSmall(pathForPhoto);
			photo.thumb2Path = PhotoUploaderUtil.getPathForMidle(pathForPhoto);
			photo.author = User.find("byEmail", Security.connected()).first();
			photo.save();
			PhotoUploaderUtil.updateThumbnails(photo);
			renderText("{'err':'','msg':'/%s','original':'/%s','thumb':'/%s','pref':'/%s'}", photo.prefPath, photo.filePath, photo.thumbPath, photo.prefPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void restroreSession() throws UnsupportedEncodingException {
		String checkuser = params.get("checkuser");
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
    static Pattern sessionParser = Pattern.compile("\u0000([^:]*):([^\u0000]*)\u0000");
}