package controllers;

import models.Role;
import play.mvc.With;

import javax.persistence.Entity;

/**
 * User: Administrator
 * Date: 2009-12-6
 * Time: 20:58:08
 */
@Check("ROLE_ADMIN")
@With(Secure.class)
public class Roles extends CRUD {
}
