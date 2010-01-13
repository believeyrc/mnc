package controllers;

import java.util.Date;
import java.util.List;

import models.Photo;
import models.Sets;
import models.User;

public class Setz extends Basez {
	public static void listMySets(String username, Long photoId) {
		User user = User.find("byFullname", username).first();
		Photo photo = Photo.findById(photoId);
		if (user != null) {
			List<Sets> sets = Sets.find("byAuthor", user).fetch();
			render(sets,photoId,photo);
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
		render(sets);
	}

	public static void addPhotoToSets(Long setsId, Long photoId) {
		Sets sets = Sets.findById(setsId);
		Photo photo = Photo.findById(photoId);
		if (sets != null && photo != null) {
			sets.photos.add(photo);
			if(sets.cover==null)
				sets.cover = photo;
			sets.save();
		}
	}

	public static void removePhotoFromSets(Long setsId, Long photoId) {
		Sets sets = Sets.findById(setsId);
		Photo photo = Photo.findById(photoId);
		if (sets != null && photo != null && sets.photos.contains(photo)) {
			sets.photos.remove(photo);
			sets.save();
		}
	}
	
	
	public static void newset(String username, String newset) {
		Sets sets = new Sets();
		sets.author = getCurrentUser();
		sets.createDate = new Date();
		sets.name = newset;
		sets.save();
		renderJSON(sets.getId());
	}
}
