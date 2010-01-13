package models;
 
import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
import play.data.validation.*;
 
@Entity
public class User extends Model {
    @Email
    @Required
    public String email;
    @Required
    @Password
    public String password;
    public String fullname;
    public String screenname;
    public String avatar;
    public boolean isAdmin;
    @ManyToMany
    public List<Role> roles;
    @ManyToOne(targetEntity = Family.class,cascade=CascadeType.ALL)
    public Family family;
    
    public User(String email, String password, String fullname) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
    }
     public static User connect(String email, String password) {
            return find("byEmailAndPassword", email, password).first();
    }
    public String toString() {
        return fullname;
    }


}
