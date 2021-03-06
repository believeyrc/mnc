package controllers;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import jobs.GrabImageJob;
import models.Photo;
import models.Responses;
import models.User;

import org.apache.commons.io.FileUtils;

import play.mvc.With;
import utils.BalloonUtil;
import utils.ImageUtil;
import utils.PhotoUploaderUtil;

@With(Secure.class)
public class Photoz extends Basez {

	public static void rotateRight(Long id) throws IOException {
		Photo photo = Photo.findById(id);
		// ImageMagick.rotate(photo.prefPath, photo.prefPath, -90);
		ImageUtil.saveJPEG(ImageUtil.roate(ImageUtil.load(photo.prefPath), (float) (Math.PI / 2)), photo.prefPath);
		PhotoUploaderUtil.updateThumbFrom(photo, photo.prefPath);
	}

	public static void rotateLeft(Long id) throws IOException {
		Photo photo = Photo.findById(id);
		// ImageMagick.rotate(photo.prefPath, photo.prefPath, 90);
		ImageUtil.saveJPEG(ImageUtil.roate(ImageUtil.load(photo.prefPath), -(float) (Math.PI / 2)), photo.prefPath);
		PhotoUploaderUtil.updateThumbFrom(photo, photo.prefPath);
	}

	public static void sayHello(Long id, String content, int x, int y, int w, int h) throws IOException {
		Photo photo = Photo.find("id = ? and author = ? ", id, getLoginUser()).first();
		if (photo != null) {
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
			// Photo temp = new Photo();
			// temp.caption = photo.caption;
			// temp.id = photo.id;
			renderJSON(val);
		}
	}

	public static void desc(Long id, String val) {
		Photo photo = Photo.find("id = ? and author = ? ", id, getLoginUser()).first();
		if (photo != null) {
			photo.description = val;
			photo.save();
			// Photo temp = new Photo();
			// temp.description = photo.description;
			// temp.id = photo.id;
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
		Photov.viewPhoto(getVisitedUsername(), id);
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
	public static void remove(Long id) {
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
			photo.remove();
			renderJSON("{msg:'ok'}");
		}
	}

	public static void prepareUpload() {
		render();
	}

	public static void grab(String urls) {
		String[] aurls = urls.split("\n");
		GrabImageJob grabImageJob = new GrabImageJob(aurls);
		try {
			List<Photo> photos;
			photos = grabImageJob.now().get();
			for (Iterator<Photo> iter = photos.iterator(); iter.hasNext();) {
				Photo photo = (Photo) iter.next();
				photo.author = User.find("byEmail", Security.connected()).first();
				photo.save();
				PhotoUploaderUtil.updateThumbnails(photo);
			}
			renderJSON(photos);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public static void prepareGrab() {
		render();
	}

}