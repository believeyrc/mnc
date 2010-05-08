package models;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import play.db.jpa.Model;
@Entity
public class Sets extends Model{
	public Sets() {
		photos = new ArrayList<Photo>();
	}
	@OneToMany(fetch= FetchType.LAZY)
	public List<Photo> photos;
	@OneToOne 
	public Photo cover;
	public String name;
	public Date createDate;
	@OneToOne
	public User author;
	
	public List<Photo> getPhotos() {
		return photos;
	}
	
	public long countPhotos(){
		return Photo.count("select count(p) from Photo p , Sets as s join s.photos as sp where sp.id = p.id and s.id = ?" , this.id);
	}
	public long positionOfPhoto(Long photoid) {
		return Photo.count("select count(p) from Photo p , Sets as s join s.photos as sp where sp.id = p.id and s.id = ? and p.id > ?" , this.id, photoid);
	}
	/*
	 * 分页获取照片
	 */
	public List<Photo> listPhotos(int pagesize, int page){
		return Photo.find("select p from Photo p , Sets s join s.photos as sp where sp.id = p.id and s.id = ? order by p.id asc",this.id).from(pagesize*page).fetch(pagesize);
	}
	public Photo nextPhotoInSets(Photo photo){
		return Photo.find("select p from Photo p , Sets s join s.photos as sp where sp.id = p.id and s.id = ? and p.id > ? order by p.id asc", this.id, photo.id).first();
	}
	public Photo previousPhotoInSets(Photo photo){
		return Photo.find("select p from Photo p , Sets s join s.photos as sp where sp.id = p.id and s.id = ? and p.id < ? order by p.id desc", this.id, photo.id).first();
	}
	
	public static List<Sets> listSetsOfPhoto(long photoid){
		return Sets.find("select DISTINCT sets from Sets sets ,in( sets.photos) p where p.id = ?", photoid).fetch();
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
	}
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
	}
}
