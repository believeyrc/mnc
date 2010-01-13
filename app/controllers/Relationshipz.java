package controllers;

import java.util.List;

import models.Family;
import models.Relation;
import models.RelationShip;
import models.User;
import models.Relation.TYPE;
import play.data.validation.Validation;

public class Relationshipz extends Basez {
	public static void unfollow(String family, String follower) {
		RelationShip rs = new RelationShip();
		rs.fromFamily = Family.find("code = ?", family).first();
		rs.toFamily = Family.find("code = ?", follower).first();
		RelationShip.delete("fromFamily = ? and toFamily = ?", rs.fromFamily, rs.toFamily);
		Familyz.neighbours(family);
	}

	public static void follow(String family, String follower) {
		RelationShip rs = new RelationShip();
		rs.fromFamily = Family.find("code = ?", family).first();
		rs.toFamily = Family.find("code = ?", follower).first();
		long count = RelationShip.count("fromFamily = ? and toFamily = ?", rs.fromFamily, rs.toFamily);
		if (count == 0) {
			rs.save();
		}
		if (count > 1) {
			RelationShip.delete("fromFamily = ? and toFamily = ?", rs.fromFamily, rs.toFamily);
			rs.save();
		}
		String url = flash.get("url");
		if (url == null) {
			url = "/";
		}
		redirect(url);
	}

	public static void saveRelationship(String username, String... types) {
		validation.required(username);
		if (getLoginUser().fullname.equals(username)) {
			Validation.addError("username", "addYourSelfError", username);
		}
		User user = User.find("byFullname", username).first();
		for (String a : types) {
			TYPE type = Relation.TYPE.valueOf(a);
			long count = Relation.count("one = ? and two = ? and type = ?", getLoginUser(), user, type);
			if (count > 1)
				Relation.delete("one = ? and two = ? and type = ?", getLoginUser(), user, type);
			if (count != 1)
				new Relation(getLoginUser(), user, type).save();
		}
		relationship(username);
	}

	public static void relationship(String username) {
		List<Object> relations = Relation.find("one = ? and two = ?", getLoginUser(), User.find("byFullname", username).first()).fetch();
		render(relations);
	}
}
