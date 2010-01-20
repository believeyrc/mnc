package controllers;

import models.Photo;
import models.Sets;
import models.User;

import java.util.Date;
import java.util.List;

public class Setv extends Basez {
	public static void listMySets(String username, Long photoId) {
		User user = User.find("byFullname", username).first();
		Photo photo = Photo.findById(photoId);
		if (user != null) {
			List<Sets> sets = Sets.find("byAuthor", user).fetch();
			render(sets, photoId, photo);
		}
	}

	public static void mySets(String username) {
		User user = User.find("byFullname", username).first();
		if (user != null) {
			List<Sets> sets = Sets.find("byAuthor", user).fetch();
			render(sets);
		}
	}

	public static void viewSets(String username, Long setsId) {
		Sets sets = Sets.findById(setsId);
        int page = 1;
        int pageSize = 12;
        long totalCount = sets.countPhotos();
        List<Photo> photos = sets.listPhotos(pageSize, page-1);
        render(sets,photos,page,pageSize,totalCount);
	}

	public static void viewPhotosInSets(String username, Long setsId,int page) {
		Sets sets = Sets.findById(setsId);
        long totalCount = sets.countPhotos();
        int pageSize = 12;
        List<Photo> photos = sets.listPhotos(pageSize, page-1);
        render("Setv/viewSets.html",sets,photos,page,pageSize,totalCount);
	}

    public static void viewSetsOfPhoto(Long setsid, Long photoid){
        Sets sets = Sets.findById(setsid);
        Photo photo = Photo.findById(photoid);
        Photo previous = sets.previousPhotoInSets(photo);
        Photo next = sets.nextPhotoInSets(photo);
        render("Setv/_viewSetsOfPhoto.html",sets,photo,previous,next);
    }
}