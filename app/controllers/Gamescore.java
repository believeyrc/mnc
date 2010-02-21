package controllers;

import java.io.IOException;

import play.mvc.Controller;

public class Gamescore extends Controller{
	public static void submit() {
//		System.out.println(a);
//		System.out.println(request.action);
		try {
//			System.out.println(request.args);
			System.out.println("============================");
			System.out.println(org.apache.commons.io.IOUtils.toString(request.body));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("============================");
//		System.out.println(request.url);
		System.out.println(params.all().size());
	}
}
