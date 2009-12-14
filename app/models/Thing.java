package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import play.db.jpa.Model;

@Entity
public class Thing extends Model {
	public Thing(Family from, String content) {
		this.createdDate = new Date();
		this.fromFamily = from;
		this.content = content;
	}

	public String content;
	public Date createdDate;
	@OneToOne
	public Family fromFamily;
	@OneToOne
	public Family toFamily;
	public TYPE type;

	public String toString() {
		return content;
	}

	public static enum TYPE {
		POST,MEME,PHOTO;
	}
}
