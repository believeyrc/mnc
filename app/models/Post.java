package models;
 
import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
import play.data.validation.*;
@Entity
public class Post extends Model {
    @Required
    public String title;
    @Required
    public Date postedAt;
    
    @Lob
    @Required
    @MaxSize(10000)
    public String content;
    
    @Required
    @ManyToOne
    public User author;
    @ManyToMany(cascade=CascadeType.ALL)
    public List<Tag> tags;

    @OneToMany(mappedBy="post", cascade=CascadeType.ALL)
    public List<Comment> comments;

    public Post(User author, String title, String content) {
        this.comments = new ArrayList<Comment>();
        this.tags = new ArrayList<Tag>();
        this.author = author;
        this.title = title;
        this.content = content;
        this.postedAt = new Date();
    }
    public Post addComment(String author, String content) {
            Comment newComment = new Comment(this, author, content).save();
            this.comments.add(newComment);
            return this;
    }

    public Post tagItWith(String name) {
      tags.add(Tag.findOrCreateByName(name));
      return this;
    }
        
    public Post previous() {
        return Post.find("postedAt < ? order by postedAt desc", postedAt).first();
    }
     
    public Post next() {
        return Post.find("postedAt > ? order by postedAt asc", postedAt).first();
    }
    
    public static List<Post> findTaggedWith(String tag,String fullname) {
        return Post.find(
            "select distinct p from Post p join p.tags as t where t.name = ? and p.author.fullname = ?", tag,fullname
        ).fetch();
    }

    public static List<Post> findTaggedWith(String... tags) {
        return Post.find(
            "select distinct p.id from Post p join p.tags as t where t.name in (:tags) group by p.id having count(t.id) = :size"
        ).bind("tags", tags).bind("size", tags.length).fetch();
    }

    public String toString() {
            return this.title;
    }
}
