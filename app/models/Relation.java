package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Relation extends Model {
	public static enum TYPE {
		MYSIDE, FREIND, FAMILY
	};

	public Relation(User one, User two, TYPE type) {
		this.one = one;
		this.two = two;
		this.type = type;
	}

	@ManyToOne
	public User one;
	@ManyToOne
	public User two;
	public TYPE type;
}
