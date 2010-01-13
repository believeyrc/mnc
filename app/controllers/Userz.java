package controllers;

import models.User;
import play.i18n.Messages;

public class Userz extends Basez {
	public static void register() {
		render();
	}

	public static void account() {
		render(getCurrentUser());
	}

	public static void changePassword() {
		render();
	}

	public static void saveNewPassword(String currentPassword, String newpassword, String newpassword2) {
		validation.required(currentPassword);
		validation.required(newpassword);
		validation.required(newpassword2);
		validation.minSize(newpassword, 6);
		validation.equals(newpassword, newpassword2);
		User currentUser = getCurrentUser();
		if (User.connect(currentUser.email, currentPassword) == null) {
			validation.addError("currentPassword", "validation.notMacth", currentPassword);
		}
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			changePassword();
		} else {
			currentUser.password = newpassword;
			currentUser.save();
			account();
		}
	}

	public static void save(String email, String fullname, String password, String password2) {
		validation.required(email);
		validation.required(fullname);
		validation.minSize(fullname, 5);
		validation.required(password);
		validation.minSize(password, 6);
		validation.required(password2);
		validation.equals(password, password2);

		long count = User.count("byEmail", email);
		if (count > 0) {
			validation.addError("email", Messages.get("validation.exists", email));
		}
		count = User.count("byFullname", fullname);
		if (count > 0) {
			validation.addError("fullname", Messages.get("validation.exists", fullname));
		}
		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			register();
		}
		User user = new User(email, password, fullname);
		user.save();
		redirect("/");
	}
}
