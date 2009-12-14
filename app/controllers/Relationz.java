package controllers;

import org.jaxen.function.FloorFunction;

import models.Family;
import models.RelationShip;
import play.mvc.Controller;

public class Relationz extends Controller {
	public static void unfollow(String family, String follower) {
		RelationShip rs = new RelationShip();
		rs.fromFamily = Family.find("code = ?", family).first();
		rs.toFamily = Family.find("code = ?", follower).first();
		RelationShip.delete("fromFamily = ? and toFamily = ?",rs.fromFamily,rs.toFamily);
		Familyz.neighbours(family);
	}
	public static void follow(String family, String follower) {
		RelationShip rs = new RelationShip();
		rs.fromFamily = Family.find("code = ?", family).first();
		rs.toFamily = Family.find("code = ?", follower).first();
		long count = RelationShip.count("fromFamily = ? and toFamily = ?",rs.fromFamily,rs.toFamily);
		if(count == 0){
			rs.save();
		}
		if(count > 1) {
			RelationShip.delete("fromFamily = ? and toFamily = ?",rs.fromFamily,rs.toFamily);
			rs.save();
		}  
		String url = flash.get("url");
		if (url == null) {
			url = "/";
		}
		redirect(url);
	}
}
