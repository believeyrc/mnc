package controllers;

import java.util.List;

import models.Connection;


public class Connectionz extends Basez{
	public static void connectInfo(String one , String two) {
		List<Connection> connections = Connection.find("( one.fullname = ? and two.fullname = ? ) or ( one.fullname = ? and two.fullname = ?)", one,two,two,one).fetch();
		render(connections);
	}
}
