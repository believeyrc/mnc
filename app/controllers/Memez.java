package controllers;

import java.util.Date;
import java.util.List;

import jobs.PostThingJob;

import models.Family;
import models.Meme;
import models.Thing;
import models.User;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.mvc.Before;

public class Memez extends Basez {

	public static void index(String family) {
		doPrepare(0, 10, family);
	}

	private static void doPrepare(int offset, int pageSize, String family) {
		Long totalCount = Meme.count(" family.code = ? ", family);
		List<Meme> olderPosts = null;
		olderPosts = Meme.find(" family.code = ? order by updatedAt desc", family).from(offset).fetch(pageSize);
		render("Memez/index.html", olderPosts, totalCount, offset, pageSize);

	}

	public static void history(int offset, int pageSize, String family) {
		doPrepare(offset, pageSize, family);
	}

	public static void theirs(String family) {
		Long totalCount = Thing.count(" toFamily.code = ?", family);
		List<Thing> things = Thing.find(" toFamily.code = ?", family).fetch(10);
		int pageSize = 0;
		int offset = 10;
		render(things, totalCount, offset, pageSize);
	}

	static void saveMeme(@Required(message = "Content is required") @MinSize(10) String content) {
		User user = User.find("byEmail", Security.connected()).first();
		Meme meme = new Meme(content, new Date(), user);
		meme.save();
	}

	@Check("login")
	public static void save(@Required(message = "Content is required") @MinSize(10) String content, String family) {
		saveMeme(content);
		new PostThingJob(Security.connected(), content, Thing.TYPE.MEME).in(5);
		index(family);
	}

}
