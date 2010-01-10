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
		}
		if(params.get("username")!=null){
			renderArgs.put("currentuser", params.get("username"));
		}
	}
	protected static User getCurrentUser() {
		return (User) renderArgs.get("user");
	}
	protected static String getFamilyCode() {
		return params.get("family");
	}
	
}