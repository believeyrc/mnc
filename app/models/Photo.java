package models;

import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
import play.data.validation.*;

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
	public String toString(){
		return this.caption + ":" +filePath;
	}
}