package controllers;

import models.Photo;
import models.Sets;
import models.User;

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

	/**
	 * 浏览相册中的照片
	 * @param username
	 * @param setsId
	 * @param page
	 */
	public static void viewSetsWith(String username, Long setsId,long id) {
		Sets sets = Sets.findById(setsId);
		long totalCount = sets.countPhotos();
		int pageSize = 12;
		long pos = sets.positionOfPhoto(id);
		int page = (int) (pos/pageSize+1);
		List<Photo> photos = sets.listPhotos(pageSize, page-1);
		render("Setv/viewSets.html",sets,photos,page,pageSize,totalCount);
	}
	
    /**
     * 浏览相册中的照片
     * @param username
     * @param setsId
     * @param page
     */
	public static void viewPhotosInSets(String username, Long setsId,int page) {
		Sets sets = Sets.findById(setsId);
        long totalCount = sets.countPhotos();
        int pageSize = 12;
        List<Photo> photos = sets.listPhotos(pageSize, page-1);
        render("Setv/viewSets.html",sets,photos,page,pageSize,totalCount);
	}

    /**
     * 查询某一个照片在某一个相片中的位置
     * @param setsid
     * @param photoid
     */
    public static void viewSetsOfPhoto(Long setsid, Long photoid){
        Sets sets = Sets.findById(setsid);
        Photo photo = Photo.findById(photoid);
        Photo previous = sets.previousPhotoInSets(photo);
        Photo next = sets.nextPhotoInSets(photo);
        render("Setv/_viewSetsOfPhoto.html",sets,photo,previous,next);
    }
    /**
     * 上一个图
     * @param setsid
     * @param photoid
     */
    public static void previous(Long setsid, Long photoid){
        Sets sets = Sets.findById(setsid);
        Photo photo = Photo.findById(photoid);
        Photo previous = sets.previousPhotoInSets(photo);
        Photo next = photo;
        render("Setv/_viewSetsOfPhoto.html",sets,photo,previous,next);
    }
    /**
     * 下一个图片
     * @param setsid
     * @param photoid
     */
    public static void next(Long setsid, Long photoid){
    	Sets sets = Sets.findById(setsid);
    	Photo photo = Photo.findById(photoid);
    	Photo next = sets.nextPhotoInSets(photo);
    	Photo previous = photo;
    	render("Setv/_viewSetsOfPhoto.html",sets,photo,previous,next);
    }
    /**
     * 查询照片所在的所有相册
     * @param photoid
     */
    public static void setsInfoOfPhoto(Long photoid, Long setsid){
        List<Sets> insets = Sets.listSetsOfPhoto(photoid);
        Photo photo = Photo.findById(photoid);
        render("Setv/_setsInfoOfPhoto.html",insets,photo, setsid);
    }
}