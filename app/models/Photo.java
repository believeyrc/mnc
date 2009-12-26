package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

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
	
	public String caption;
	public Date uploadAt;
	/**
	 * 240x240
	 */
	public String thumb2Path;
	public String toString(){
		return this.caption + ":" +filePath;
	}
}