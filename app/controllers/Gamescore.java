package controllers;

import java.util.List;

import models.GameScore;
import play.mvc.Controller;

public class Gamescore extends Controller {
	public static void submit() {
		new GameScore(params.get("name"), Long.valueOf(params.get("score"))).save();
	}

	public static void honorRoll() {
		List<GameScore> all = GameScore.all().fetch();
		renderJSON(all);
	}
}
