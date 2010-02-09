package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Channel extends Model {
	public Date updateDate;
	@ManyToOne
	public User author;
	public String name;

	public Channel(User author, String name) {
		updateDate = new Date();
		this.author = author;
		this.name = name;
	}

	public List<Message> getMessages() {
		return Message.find("channel = ? order by longTime asc", this).fetch();
	}

	public List<Message> getMessagesAfter(long longtime) {
		long count = Message.count("channel = ? and longtime > ? ", this, longtime);
		System.out.println(count);
		int from = 0;
		if(count>10)
			from = (int) (count -10);
		return Message.find("channel = ? and longtime > ? order by longTime asc", this, longtime).from(from ).fetch(10);
	}
	public List<Message> getMessagesBefore(long longtime) {
		long count = Message.count("channel = ? and longtime < ? ", this, longtime);
		System.out.println("before:"+count);
		int from = 0;
		if(count>10)
			from = 10;
		return Message.find("channel = ? and longtime < ? order by longTime desc", this, longtime).from(from ).fetch(10);
	}
	public static List<Channel> listByAuthor(User user) {
		return Channel.find("byAuthor", user).fetch();
	}
}
