package controllers;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import jobs.PostThingJob;

import models.Meme;
import models.Thing;
import models.User;
import play.data.validation.MinSize;
import play.data.validation.Required;

public class Memez extends Basez {

	public static void index(String username) {
		doPrepare(0, 10, username);
	}

	private static void doPrepare(int offset, int pageSize, String username) {
		Long totalCount = Meme.count(" author.fullname = ? ", username);
		List<Meme> olderPosts = null;
		olderPosts = Meme.find(" author.fullname = ? order by updatedAt desc", username).from(offset).fetch(pageSize);
		render("Memez/index.html", olderPosts, totalCount, offset, pageSize);
	}

	public static void history(int offset, int pageSize, String family) {
		doPrepare(offset, pageSize, family);
	}

	public static void theirs(String username) {
		Long totalCount = Thing.count(" toFamily.code = ?", username);
		List<Thing> things = Thing.find(" toFamily.code = ?", username).fetch(10);
		int pageSize = 0;
		int offset = 10;
		render(things, totalCount, offset, pageSize);
	}

	static void saveMeme(@Required(message = "Content is required") @MinSize(10) String content) {
		if(StringUtils.isNotEmpty(content)&&content.length()>1) {
			User user = User.find("byEmail", Security.connected()).first();
			Meme meme = new Meme(content, new Date(), user);
			meme.save();
		}
	}

	@Check("login")
	public static void save(@Required(message = "Content is required") @MinSize(10) String content, String username) {
		saveMeme(content);
		new PostThingJob(Security.connected(), content, Thing.TYPE.MEME).in(5);
		index(username);
	}

}
