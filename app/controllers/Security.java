package controllers;
 
import models.*;
 
public class Security extends Secure.Security {
 
    static boolean authentify(String username, String password) {
        return User.connect(username, password) != null;
    }
    static void onDisconnected() {
    	String url = flash.get("url");
        if(url == null) {
            url = "/";
        }
    	redirect(url);
    }
    static void onAuthenticated() {
        String url = flash.get("url");
        if(url == null) {
            url = "/";
        }
        redirect(url);
    }
    static boolean check(String profile) {
        if(profile.startsWith("ROLE_")) {
            User user = User.find("byEmail", connected()).first();
            if(user.isAdmin) return true;
            for(Role role : user.roles){
                if(role.code.equals(profile))
                    return true;
            }
            return false; 
        }else if("login".equals(profile)) {
        	return connected()!=null;
        }
        return false;
    }
//    checkAccess
}
