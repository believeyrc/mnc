package controllers;

import models.Family;
import play.mvc.With;

/**
 * User: Administrator
 * Date: 2009-12-6
 * Time: 21:19:05
 */
@Check("ROLE_ADMIN")
@With(Secure.class)
@CRUD.For(Family.class)
public class Families extends CRUD{
}
