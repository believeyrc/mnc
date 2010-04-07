package controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.Photo;
import models.Post;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import utils.RssUtil;

import com.sun.syndication.io.FeedException;

public class Rss extends Basez {
	public static void posts(String username) {
		List<Post> posts = Post.findByUser(username);
		RssUtil rss = RssUtil.start();
		String author = StringUtils.defaultString(getVisitedUser().screenname, getVisitedUser().fullname);
		rss.setAuthor(author);
		rss.setTitle(author + " 的故事");
		rss.setDescription(author + " 的故事").setLink("http://www.jsxnc.com/posts/" + username);
		for (Post post : posts) {
			rss.addItem(post.title, String.format("http://www.jsxnc.com/posts/%s/%s", username, post.id), post.postedAt, post.content, post.tags);
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			rss.render(bos);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FeedException e) {
			e.printStackTrace();
		}
		renderXml(new String(bos.toByteArray()));
	}

	public static void photos(String username) {
		List<Photo> photos = Photo.findByUser(username);
		RssUtil rss = RssUtil.start();
		String author = StringUtils.defaultString(getVisitedUser().screenname, getVisitedUser().fullname);
		rss.setAuthor(author);
		rss.setTitle(author + " 的照片");
		rss.setDescription(author + " 的照片").setLink("http://www.jsxnc.com/photos/" + username);
		Date last = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (Photo photo : photos) {
			if (last != null && DateUtils.isSameDay(last, photo.uploadAt)) {
				String desc = String.format("<div ><h4>%s</h4><br/><a href='http://www.jsxnc.com/photos/%s/%s'><img src='http://static.jsxnc.com/%s'/></a><br/>%s</div>", photo.caption,
						username, photo.id, photo.thumb2Path, StringUtils.defaultString(photo.description));
				rss.appendToLastItem(desc);
			} else {
				String title = String.format("%s %s 上传", author, sdf.format(photo.uploadAt));
				String desc = String.format("<div ><h4>%s</h4><br/><a href='http://www.jsxnc.com/photos/%s/%s'><img src='http://static.jsxnc.com/%s'/></a><br/> %s </div>", photo.caption, username, photo.id,
						photo.thumb2Path, StringUtils.defaultString(photo.description));
				rss.addItem(title, String.format("http://www.jsxnc.com/photos/%s/%s", username, photo.id), photo.uploadAt, desc, null);
			}
			last = photo.uploadAt;
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			rss.render(bos);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FeedException e) {
			e.printStackTrace();
		}
		renderXml(new String(bos.toByteArray()));
	}
}
