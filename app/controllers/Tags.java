package controllers;
 
import play.mvc.*;
@Check("ROLE_ADMIN")
@With(Secure.class) 
public class Tags extends CRUD {    
}
