package models;
 
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.modules.search.Field;
import play.modules.search.Indexed;

@Entity
@Indexed
public class Responses extends Model {
    @Required
    @ManyToOne
    @Field
    public User author;
    @Required
    public Date postedAt;
     
    @Lob
    @Required
    @MaxSize(10000)
    @Field
    public String content;
    
    @ManyToOne(cascade={CascadeType.ALL})
    public Photo photo;
    @ManyToOne(cascade={CascadeType.ALL})
    public Post post;
    public Responses(Photo  photo, User author, String content) {
        this.photo = photo;
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
    }
 
}
