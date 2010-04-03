package controllers;

import models.Photo;
import models.User;
import play.data.validation.Validation;
import play.i18n.Messages;
import utils.ImageUtil;

public class Userz extends Basez {
	public static void register() {
		render();
	}

	public static void account() {
		render(getLoginUser());
	}

	public static void changePassword() {
		render();
	}

    public static void saveScreename(String val) {
        User user = getLoginUser();
        user.screenname = val;
        user.save();
        renderJSON(val);
	}

	public static void changeAvatar(Long id) {
		Photo photo = Photo.findById(id);
		render(photo);
	}

	public static void saveAvatar(Long id, int x, int y, int w, int h) {
		Photo photo = Photo.findById(id);
		User loginUser = getLoginUser();
		loginUser.avatar = "public/avatar/" + loginUser.id + ".jpg";
		loginUser.save();
		ImageUtil.saveJPEG(ImageUtil.thumbnail(ImageUtil.crop(ImageUtil.load(photo.prefPath), x, y, w, h),48,48,true,true), loginUser.avatar);
		account();
	}

	public static void saveNewPassword(String currentPassword, String newpassword, String newpassword2) {
		validation.required(currentPassword);
		validation.required(newpassword);
		validation.required(newpassword2);
		validation.minSize(newpassword, 6);
		validation.equals(newpassword, newpassword2);
		User currentUser = getLoginUser();
		if (User.connect(currentUser.email, currentPassword) == null) {
			Validation.addError("currentPassword", "validation.notMacth", currentPassword);
		}
		if (Validation.hasErrors()) {
			params.flash();
			Validation.keep();
			changePassword();
		} else {
			currentUser.password = newpassword;
			currentUser.save();
			account();
		}
	}

	public static void save(String email, String fullname, String password, String password2, String screenname) {
		fullname = fullname.toLowerCase();

		validation.required(email);
		validation.email(email);
		validation.required(fullname);
		validation.minSize(fullname, 5);
		validation.match(fullname, "[a-z0-9]*");
		validation.required(screenname);
		validation.minSize(screenname, 1);
		validation.required(password);
		validation.minSize(password, 6);
		validation.required(password2);
		validation.equals(password, password2);

		long count = User.count("byEmail", email);
		if (count > 0) {
			Validation.addError("email", Messages.get("validation.exists", email));
		}
		count = User.count("byFullname", fullname);
		if (count > 0) {
			Validation.addError("fullname", Messages.get("validation.exists", fullname));
		}
		if (Validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			Validation.keep(); // keep the errors for the next request
			register();
		}
		User user = new User(email, password, fullname);
		user.save();
		redirect("/");
	}
	
	public static void connections() {
		render();
	}
}
