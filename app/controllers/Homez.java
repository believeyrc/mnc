package controllers;

import java.util.List;

import models.Photo;
import models.User;

/**
 * User: Administrator Date: 2009-12-6 Time: 22:17:39
 */
public class Homez extends Basez {
	public static void index() {
		User currentUser = getLoginUser();
        List<Photos> lastPhotos ;
		if (currentUser != null) {
			List<Photos>  yourlastPhotos = Photo.find("author = ? order by uploadAt desc", currentUser).fetch(15);
            renderArgs.put("yourlastPhotos",yourlastPhotos);
            lastPhotos = Photo.find("author != ? order by uploadAt desc",currentUser).fetch(15);    
            renderArgs.put("lastPhotos",lastPhotos );
		} else {
            lastPhotos = Photo.find("order by uploadAt desc").fetch(15);
            renderArgs.put("lastPhotos",lastPhotos );    
        }

		render();
	}
}
