package controllers;

import models.User;
import play.mvc.Before;
import play.mvc.Controller;

public abstract class Basez extends Controller {
	@Before
	static void addDefaults() {
		if (session.contains("username")) {
			User user = User.find("byEmail", session.get("username")).first();
			renderArgs.put("user", user);
			//default set current
			renderArgs.put("currentuser", user.fullname);
		}
		if (params.get("username") != null) {
			renderArgs.put("currentuser", params.get("username"));
		}
	}

	protected static User getLoginUser() {
		return (User) renderArgs.get("user");
	}
	protected static String getVisitedUser() {
		return  (String) renderArgs.get("currentuser");
	}

}