package models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;
@Entity
public class Sets extends Model{
	public Sets() {
		photos = new HashSet<Photo>();
	}
	@ManyToMany
	public Set<Photo> photos;
	@ManyToOne 
	public Photo cover;
	public String name;
	public Date createDate;
	@ManyToOne
	public User author;
}
