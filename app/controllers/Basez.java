package controllers;

import java.util.List;

import models.Family;
import models.RelationShip;
import models.User;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;

public abstract class Basez extends Controller {
	@Before
	static void addDefaults() {
		if (params.get("family") != null) {
			Family family = Family.find("byCode", params.get("family")).first();
			renderArgs.put("family", family);
			renderArgs.put("blogTitle", family.name);
			renderArgs.put("blogBaseline", family.slogan);
			renderArgs.put("familyLogoImage", family.image);
		} else {
			renderArgs.put("blogTitle", Messages.get("logo"));
			renderArgs.put("blogBaseline", Messages.get("SiteforyourFamily"));
			renderArgs.put("familyLogoImage", Messages.get("familyLogoImage"));
		}
		if (session.contains("username")) {
			User user = User.find("byEmail", Security.connected()).first();
			renderArgs.put("user", user);
		}
		if(renderArgs.get("user")!=null && renderArgs.get("family")!=null) {
			User user = renderArgs.get("user",User.class);
			Family family = renderArgs.get("family",Family.class);
			boolean isFollowed = RelationShip.count("fromFamily = ? and toFamily = ?",user.family,family)==1;
			renderArgs.put("isFollowed", isFollowed);
		}
	}

	protected static String getFamilyCode() {
		return params.get("family");
	}
}