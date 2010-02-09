package controllers;

import java.util.List;

import models.Channel;

public class Channelz extends Basez {
	public static void list(String username) {
		List<Channel> channels = Channel.listByAuthor(getVisitedUser());
		render(channels);
	}

	public static void newchannel() {
		render();
	}

	public static void savechannel(String name) {
		validation.required(name);
		validation.minSize(name, 1);
		if (validation.hasErrors()) {
			params.flash();
			newchannel();
		}
		Channel channel = new Channel(getLoginUser(), name);
		channel.save();
		list(getLoginUser().fullname);
	}
}
