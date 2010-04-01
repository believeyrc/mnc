package controllers;

import models.User;
import play.mvc.Before;
import play.mvc.Controller;

public abstract class Basez extends Controller {
	@Before
	static void addDefaults() {
		if (session.contains("username")) {
			//当前登录用户
			User user = User.find("byEmail", session.get("username")).first();
			renderArgs.put("user", user);
			// default set current
			if (params.get("username") == null) {
				renderArgs.put("currentuser", user.fullname);
				User visitedUser = User.find("byEmail", session.get("username")).first();
				renderArgs.put("visitedUser", visitedUser);
			}
		}
		if (params.get("username") != null) {
			renderArgs.put("currentuser", params.get("username"));
			User user = User.find("byFullname", params.get("username")).first();
			renderArgs.put("visitedUser", user);
		}
	}

	protected static User getLoginUser() {
		return (User) renderArgs.get("user");
	}

	protected static String getVisitedUsername() {
		return (String) renderArgs.get("currentuser");
	}
	protected static User getVisitedUser() {
		return (User) renderArgs.get("visitedUser");
	}

}