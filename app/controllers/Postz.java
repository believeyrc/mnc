package controllers;

import java.util.List;

import models.Post;
import models.Responses;
import models.Tag;
import models.User;
import play.cache.Cache;
import play.data.validation.Required;
import play.libs.Images;

public class Postz extends Basez {

	public static void index(String username) {
		doPrepare(0, 10, username);
	}

	private static void doPrepare(int offset, int pageSize, String username) {
		Long totalCount = Post.count(" author.fullname = ?", username);
		Post frontPost = null;
		List<Post> olderPosts = null;
		if (offset == 0) {
			frontPost = Post.find(" author.fullname = ? order by postedAt desc", username).first();
			olderPosts = Post.find(" author.fullname = ? order by postedAt desc", username).from(1).fetch(pageSize - 1);
		} else {
			olderPosts = Post.find(" author.fullname = ? order by postedAt desc", username).from(offset).fetch(pageSize);
		}
		render("Postz/index.html", frontPost, olderPosts, totalCount, offset, pageSize);
	}

	public static void history(int offset, int pageSize, String username) {
		doPrepare(offset, pageSize, username);
	}

	public static void show(String username,Long id) {
		Post post = Post.findById(id);
		List<Responses> responses = Responses.find("byPost", post).fetch();
		render(post,responses);
	}

	public static void postComment(Long postId, @Required(message = "A message is required") String content) {
		Post post = Post.findById(postId);
		Responses responses = new Responses(null, getLoginUser(), content);
		responses.post = post;
		responses.save();
		show(post.author.fullname,postId);
	}

	public static void form(String fullname,Long id) {
		if (id != null) {
			Post post = Post.findById(id);
			render(post);
		}
		render();
	}

	public static void save(Long id, String title, String content, String tags) {
		Post post;
		if (id == null) {
			// Create post
			User author = User.find("byEmail", Security.connected()).first();
			post = new Post(author, title, content);
		} else {
			// Retrieve post
			post = Post.findById(id);
			// Edit
			post.title = title;
			post.content = content;
			post.tags.clear();
		}
		// Set tags list
		for (String tag : tags.split("\\s+")) {
			if (tag.trim().length() > 0) {
				post.tags.add(Tag.findOrCreateByName(tag));
			}
		}
		// Validate
		validation.valid(post);
		if (validation.hasErrors()) {
			render("Postz/form.html", post);
		}
		// Save
		boolean newPost = isNewPost(post);
		post.save();
		if (newPost) {
			// new PostThingJob(new Thing(Security.connected(),)).in(5);
		}
		show(post.author.fullname,post.id);
	}

	private static boolean isNewPost(Post post) {
		return post.id == null;
	}

	public static void captcha(String id) {
		try {
			Images.Captcha captcha = Images.captcha();
			captcha.setBackground("#1261e0");
			String code = captcha.getText("#8261e0");
			Cache.set(id, code, "10mn");
			renderBinary(captcha);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void listTagged(String tag, String fullname) {
		List<Post> posts = Post.findTaggedWith(tag, fullname);
		render(tag, posts);
	}

}