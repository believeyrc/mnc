package controllers;

import models.User;
import play.mvc.Controller;
import utils.RobotMessager;

public class Messenger extends Controller {
	public static void send(long id, String text) {
		User user = User.findById(id);
		if (user != null) {
			RobotMessager.sendMessage(user.email, text);
			index();
		}
	}

	public static void index() {
		render();
	}
}
