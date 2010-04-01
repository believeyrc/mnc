package models;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

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
	//3 家人
	public int type = 0;
}
