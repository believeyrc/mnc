package models;
 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.modules.search.Field;
import play.modules.search.Indexed;
@Entity
@Indexed
public class Post extends Model {
    @Required
    @Field
    public String title;
    @Required
    public Date postedAt;
    public Date updatedAt;
    
    @Lob
    @Required
    @MaxSize(10000)
    @Field
    public String content;
    
    @Required
    @ManyToOne
    @Field
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
        return Post.find(" author = ? and postedAt < ? order by postedAt desc", author, postedAt).first();
    }
     
    public Post next() {
        return Post.find(" author = ? and postedAt > ? order by postedAt asc", author, postedAt).first();
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
    
    public static List<Post> findByUser(String username){
    	return Post.find("author.fullname = ?",username).fetch();
    }
    
    public String toString() {
            return this.title;
    }
    
    @PreUpdate	
    public void onUpdate() {
    	updatedAt = new Date();
    }
}
