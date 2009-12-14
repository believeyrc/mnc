package controllers;

import java.util.List;

import models.Family;
import play.mvc.Controller;

/**
 * User: Administrator
 * Date: 2009-12-6
 * Time: 22:17:39
 */
public class Homez extends Basez {
    public static void index() {
    	List<Family> families = Family.all().fetch();
    	render(families);
    }
}
