package models;

import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class Meme extends Model {
	public Meme(String content,Date updatedAt,User author){
		this.content = content;
		this.updatedAt = updatedAt;
		this.author = author;
		this.family = author.family;
	}
	@Required
    @ManyToOne
    public User author;
    @Required
    @MaxSize(200)
    public String content;
    @ManyToOne
    public Family family;

	public Date updatedAt;
	public String toString(){
		return this.content + ":" +author;
	}
}