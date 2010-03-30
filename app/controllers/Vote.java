package controllers;
import play.cache.Cache;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

public class Vote extends Basez {
	public static void vote(){		
		render();
	}
	public static void checkcode(){
		HttpClient client = new HttpClient();
		String id = java.util.UUID.randomUUID().toString();
		Cache.set(id, client, "10mn");
	}
}