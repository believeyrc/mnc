package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import play.db.jpa.Model;
@Entity
public class Message extends Model {
	public String content;
	@OneToOne(optional=true)
	public User author;
	public String nickname;
	public Date updatedDate;
	@OneToOne
	public Channel channel;
	public long longTime;
	public Message() {
		updatedDate = new Date();
		longTime = updatedDate.getTime();
	}
}
