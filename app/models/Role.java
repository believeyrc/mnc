package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

/**
 * User: Administrator
 * Date: 2009-12-6
 * Time: 20:29:20
 */
@Entity
public class Role extends Model {
    public String name;
    public String code;

    @Override
    public String toString() {
        return name+"["+code+"]";    
    }
}
