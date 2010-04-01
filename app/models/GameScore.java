package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class GameScore extends Model {
	public String name;
	public Long score;
	public GameScore(String name,Long score) {
		this.name = name;
		this.score = score;
	}
}
