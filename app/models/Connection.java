package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.PreUpdate;

import play.db.jpa.Model;
@Entity
public class Connection extends Model{
	@OneToOne
	public User one;
	@OneToOne
	public User two;
	//0 默认值
	//1 自己人
	//2 朋友
	//4 家人
	//8 家人
	//3自己人+朋友
	//5自己人+家人
	//7自己人+朋友+家人
	public int type = 0;
	public Date updatedAt;
	@PreUpdate
	protected  void onUpdated() {
		updatedAt = new Date();
	}
}
