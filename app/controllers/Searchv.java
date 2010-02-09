package controllers;

import models.Photo;
import play.modules.search.Search;

import java.util.List;

/**
 * User: Administrator
 * Date: 2010-1-23
 * Time: 13:28:10
 */
public class Searchv extends Basez {
    public static void search(){
        String sq = params.get("q");
        int page = params._contains("page") ? Integer.parseInt(params.get("page")): 1;
        int pageSize = params._contains("pageSize") ? Integer.parseInt(params.get("pageSize")): 12;

        Search.Query q = Search.search("caption:"+ sq +" description:"+sq, Photo.class);
        q.orderBy("caption").page((page - 1) *pageSize,pageSize).reverse();
        List<Search.QueryResult> photos = q.executeQuery(true);
        long totalCount = q.count();
        params.flash();
        render(photos,totalCount,page, pageSize);
    }
}
