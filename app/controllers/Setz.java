package controllers;

import java.util.Date;
import java.util.List;

import models.Photo;
import models.Sets;
import models.User;

public class Setz extends Basez {
	public static void addPhotoToSets(Long setsId, Long photoId) {
		Sets sets = Sets.findById(setsId);
		Photo photo = Photo.findById(photoId);
		if (sets != null && photo != null) {
            //这里会把所有的照片都取出来一遍
			sets.photos.add(photo);
			if (sets.cover == null)
				sets.cover = photo;
			sets.save();
		}
        renderJSON(sets.id);
	}

	public static void removePhotoFromSets(Long setsId, Long photoId) {
		Sets sets = Sets.findById(setsId);
		Photo photo = Photo.findById(photoId);
		if (sets != null && photo != null && sets.photos.contains(photo)) {
			if (sets.cover!=null && sets.cover.getId() == photo.getId()) {
				sets.cover = null;
			}
			sets.photos.remove(photo);
			sets.save();
		}
        renderJSON(sets.id);
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
