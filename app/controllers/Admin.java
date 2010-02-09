package controllers;
 
 
import java.util.List;

import jobs.PostThingJob;

import models.Post;
import models.Tag;
import models.Thing;
import models.User;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
 
@With(Secure.class)
public class Admin extends Controller {
    
    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.fullname);
        }
    }
 
    public static void index() {
        List<Post> posts = Post.find("author.email = ? order by postedAt desc", Security.connected()).fetch();
        render(posts);
    }
    public static void form(Long id) {
        if(id != null) {
            Post post = Post.findById(id);
            render(post);
        }
        render();
    }
   
}
