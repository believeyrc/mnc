package controllers;

import java.util.List;

import models.Post;
import models.Tag;
import models.User;
import play.Play;
import play.cache.Cache;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.After;
import play.mvc.Before;

public class Postz extends Basez {

	public static void index(String username) {
		doPrepare(0, 10, username);
	}

	private static void doPrepare(int offset, int pageSize, String username) {
		Long totalCount = Post.count(" author.family.code = ?", username);
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

	public static void history(int offset, int pageSize, String family) {
		doPrepare(offset, pageSize, family);
	}

	public static void show(String username,Long id) {
		Post post = Post.findById(id);
		String randomID = Codec.UUID();
		render(post, randomID);
	}

	public static void postComment(Long postId, @Required(message = "Author is required") String author, @Required(message = "A message is required") String content,
			@Required(message = "Please type the code") String code, String randomID) {
		Post post = Post.findById(postId);
		validation.equals(code.toLowerCase(), ((String) Cache.get(randomID)).toLowerCase()).message("Invalid code. Please type it again");

		if (Validation.hasErrors()) {
			render("Postz/show.html", post, author, content, randomID);
		}
		post.addComment(author, content);
		flash.success("Thanks for posting %s", author);
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