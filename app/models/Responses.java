package models;
 
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Responses extends Model {
    @Required
    public User author;
    @Required
    public Date postedAt;
     
    @Lob
    @Required
    @MaxSize(10000)
    public String content;
    
    @ManyToOne
    @Required
    public Photo photo;
    
    public Responses(Photo  photo, User author, String content) {
        this.photo = photo;
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
    }
 
}