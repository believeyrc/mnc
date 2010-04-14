package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;
import play.modules.search.Field;
import play.modules.search.Indexed;

@Indexed
@Entity
public class Photo extends Model {
	public Photo(){
		
	}
	public Photo(String caption,Date uploadAt,String filePath){
		this.caption = caption;
		this.uploadAt = uploadAt;
		this.filePath = filePath;
	}
	@Required
    @ManyToOne
    @Field
    public User author;
	public String filePath;
	/**
	 * 700x700
	 */
	public String prefPath;
	/**
	 * 75x75
	 */
	public String thumbPath;
	@Field
	public String caption;
    @Field    
	@Lob
	public String description;
	public Date uploadAt;
	/**
	 * 240x240
	 */
	public String thumb2Path;
	/**
	 * 查看权限
	 */
	public int privView = 7;
	/**
	 * 查看权限
	 */
	public int privComment = 7;
	/**
	 * 查看权限
	 */
	public int privMeta = 7;
	
	public String toString(){
		return this.caption + ":" +filePath;
	}
	
    public long countOfUser() {
    	return Photo.count(" author = ? ", author);
    }
    public Photo previous() {
    	return Photo .find(" author = ? and id > ? order by id asc", author, id).first();
    }
     
    public Photo next() {
    	return Photo .find(" author = ? and id < ? order by id desc", author, id).first();
    }
	public static List<Photo> findByUser(String username) {
		return Photo.find("author.fullname = ? order by uploadAt desc",username).fetch();
	}
	
}