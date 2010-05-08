package controllers;

import java.util.Date;
import java.util.List;

import models.Photo;
import models.Sets;
import models.User;

public class Setz extends Basez {
	public static void addPhotoToSets(Long setsId, Long photoId) {
		Sets.addPhotoToSets(setsId, photoId);
        renderJSON(setsId);
	}

	public static void removePhotoFromSets(Long setsId, Long photoId) {
		Sets.removePhotoFromSets(setsId, photoId);
        renderJSON(setsId);
	}

	public static void newset(String username, String newset) {
		Sets sets = new Sets();
		sets.author = getLoginUser();
		sets.createDate = new Date();
		sets.name = newset;
		sets.save();
		renderJSON(sets.getId());
	}
}
