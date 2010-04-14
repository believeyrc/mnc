package controllers;

import models.Photo;

public class Private extends Basez{
	public static void edit(Long id){
		Photo photo = Photo.findById(id);
		render(photo);
	}
	public static void updateSetting(Long id, int privView,int privComment,int privAddmeta){
		
	}
}
