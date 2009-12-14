package controllers;
 
import play.mvc.*;

@Check("ROLE_ADMIN")
@With(Secure.class) 
public class Photos extends CRUD {    
}
