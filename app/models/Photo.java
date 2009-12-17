package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Photo extends Model {
	public Photo(String caption,Date uploadAt,String filePath){
		this.caption = caption;
		this.uploadAt = uploadAt;
		this.filePath = filePath;
	}
	@Required
    @ManyToOne
    public User author;
	public String filePath;
	
	public String prefPath;
	public String thumbPath;
	
	public String caption;
	public Date uploadAt;
	public String thumb2Path;
	public String toString(){
		return this.caption + ":" +filePath;
	}
}