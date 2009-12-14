package jobs;

import java.util.Date;
import java.util.List;

import models.Meme;
import models.RelationShip;
import models.User;
import models.Thing.TYPE;
import play.Logger;
import play.jobs.Job;

public class PostThingJob extends Job {
	private String from;
	private String content;
	private TYPE type;
	public PostThingJob(String from,String content,TYPE type) {
		this.from = from;
		this.content = content;
		this.type = type;
	}
	@Override
	public void doJob() throws Exception {
		User user = User.find("email=?", from).first();
		List<RelationShip> followers = RelationShip.find("toFamily = ? ", user.family).fetch();
		for (RelationShip rs : followers) {
			Meme meme = new Meme(content, new Date(), user);
			meme.family = rs.fromFamily;
			meme.save();
			Logger.debug("meme has posted to %s", user.family);
		}
	}
}
