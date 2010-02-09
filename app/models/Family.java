package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

import org.hibernate.annotations.Type;


/**
 * User: Administrator
 * Date: 2009-12-6
 * Time: 19:50:06
 */
@Entity
public class Family extends Model {
    public String name;
    public String slogan;
    public String code;
    public String image;
    @Override
    public String toString() {
        return name;    
    }
}
