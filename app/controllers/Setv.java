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
		render(sets);
	}	
}