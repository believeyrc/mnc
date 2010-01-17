package controllers;

import java.util.List;

import models.Photo;
import models.Responses;

public class Photov extends Basez {
    public static void home(String username) {
	    int	 page = 1;
		int pageSize = 12;
		long totalCount = Photo.count(" author.fullname = ? ", username);
		List<Photo> photos = Photo.find(" author.fullname = ? order by id desc", username).from(pageSize * (page - 1)).fetch(pageSize);
		render("Photov/photos.html",photos, page, totalCount, pageSize);
    }
	public static void photos(String username, int page) {
		if (page <= 1)
			page = 1;
		int pageSize = 12;
		long totalCount = Photo.count(" author.fullname = ? ", username);
		List<Photo> photos = Photo.find(" author.fullname = ? order by id desc", username).from(pageSize * (page - 1)).fetch(pageSize);
		render(photos, page, totalCount, pageSize);
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

	
	public static void viewPhoto(String username,Long id) {
		Photo photo = Photo.findById(id);
		List<Responses> responses = Responses.find(" photo = ? order by postedAt asc", photo).fetch();
		render(photo, responses);
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
}