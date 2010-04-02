package controllers;

import models.Connection;
import models.User;

public class Connectionz extends Basez {
	public static void connectInfo(String one, String two) {
		Connection my = Connection.find("( one.fullname = ? and two.fullname = ? )", one, two).first();
		Connection other = Connection.find("( one.fullname = ? and two.fullname = ? )", two, one).first();
		System.out.println(my.type);
		render(my,other);
	}

	public static void connect(String one, String two, int type) {
		User oneUser = User.find("byFullname", one).first();
		User twoUser = User.find("byFullname", two).first();
		System.out.println("==============");
		System.out.println(type);
		if (oneUser != null && twoUser != null) {
			Connection connection = Connection.find("( one.fullname = ? and two.fullname = ? )", one, two).first();
			if (connection == null) {
				connection = new Connection();
				connection.one = oneUser;
				connection.two = twoUser;
				connection.type = type;
				connection.save();
			}else {
				if(type != connection.type) {
					connection.type = type;
					connection.save();
				}
			}
		}
	}
}
