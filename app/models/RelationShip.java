package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class RelationShip extends Model{
	@ManyToOne
	public Family fromFamily;
	@ManyToOne
	public Family toFamily;
}
