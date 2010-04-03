package controllers;

import java.util.List;

import models.Photo;
import models.Post;

import org.apache.commons.lang.StringUtils;

import play.modules.search.Search;

/**
 * User: Administrator Date: 2010-1-23 Time: 13:28:10
 */
public class Searchv extends Basez {
	public static void search() {
		String sq = params.get("q");
		int page = params._contains("page") ? Integer.parseInt(params.get("page")) : 1;
		int pageSize = params._contains("pageSize") ? Integer.parseInt(params.get("pageSize")) : 12;

		if (!StringUtils.isEmpty(sq)) {
			Search.Query q = null;
			List<Search.QueryResult> result = null;
			long totalCount = 0;
			if (StringUtils.equals(params.get("t"), "post")) {
				q = Search.search("title:" + sq + " content:" + sq, Post.class);
				q.page((page - 1) * pageSize, pageSize).reverse();

			} else {
				q = Search.search("caption:" + sq + " description:" + sq, Photo.class);
				q.page((page - 1) * pageSize, pageSize).reverse();
			}
			result = q.executeQuery(true);
			totalCount = q.count();
			params.flash();
			render(result, totalCount, page, pageSize);
		} else {
			render(null, 0, page, pageSize);
		}
	}
}
