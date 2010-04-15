package controllers;

import java.util.List;

import models.Photo;
import models.Post;
import models.User;
import play.modules.search.Search;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Admin extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byEmail", Security.connected()).first();
			renderArgs.put("user", user.fullname);
		}
	}

	public static void index() {
		List<Post> posts = Post.find("author.email = ? order by postedAt desc", Security.connected()).fetch();
		render(posts);
	}

	public static void form(Long id) {
		if (id != null) {
			Post post = Post.findById(id);
			render(post);
		}
		render();
	}

	@Check("ROLE_ADMIN")
	public static void reindexPost() {
		List<Post> allPost = Post.all().fetch();
		for (Post object : allPost) {
			Search.index(object);
			System.out.println(object + " indexed");
		}
	}
	@Check("ROLE_ADMIN")
	public static void reindexPhoto() {
		List<Photo> allPost = Photo.all().fetch();
		for (Photo object : allPost) {
			Search.index(object);
			System.out.println(object + " indexed");
		}
	}
}
