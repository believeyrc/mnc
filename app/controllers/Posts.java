package controllers;
 
import play.*;
import play.mvc.*;

@Check("ROLE_ADMIN")
@With(Secure.class) 
public class Posts extends CRUD {    
}
