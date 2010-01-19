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
	@OneToMany(fetch= FetchType.EAGER)
	public List<Photo> photos;
	@OneToOne 
	public Photo cover;
	public String name;
	public Date createDate;
	@OneToOne
	public User author;
	public long countPhotos(){
		return Photo.count("select count(p) from Photo p , Sets as s join s.photos as sp where sp.id = p.id and s.id = ?" , this.id);
	}
	/*
	 * 分页获取照片
	 */
	public List<Photo> listPhotos(int pagesize, int page){
		return Photo.find(" select p from Photo p , Sets s join s.photos as sp where sp.id = p.id and s.id = ? order by p.id ",this.id).from(pagesize*page).fetch(pagesize);
	}
	public Photo nextPhotoInSets(Photo photo){
		return Photo.find(" select p from Photo p , Sets s join s.photos as sp where sp.id = p.id and p.id > ? order by p.id ", photo.id).first();
	}
	public Photo previousPhotoInSets(Photo photo){
		return Photo.find(" select p from Photo p , Sets s join s.photos as sp where sp.id = p.id and p.id < ? order by p.id ", photo.id).first();
	}
}
