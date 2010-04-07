package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.feed.synd.SyndImageImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

public class RssUtil {
	private SyndFeed feed = new SyndFeedImpl();

	private RssUtil() {
		feed.setFeedType("rss_2.0");
		feed.setEncoding("UTF-8");
		feed.setEntries(new ArrayList());
	}

	public static RssUtil start() {
		RssUtil rssUtil = new RssUtil();
		return rssUtil;
	}

	public RssUtil setTitle(String title) {
		feed.setTitle(title);
		return this;
	}

	public RssUtil setLink(String link) {
		feed.setLink(link);
		return this;
	}

	public RssUtil setDescription(String desc) {
		feed.setDescription(desc);
		return this;
	}

	public RssUtil setImage(String title, String url, String link, String desc) {
		SyndImage img = new SyndImageImpl();
		img.setDescription(desc);
		img.setLink(link);
		img.setTitle(title);
		img.setUrl(url);
		feed.setImage(img);
		return this;
	}

	public RssUtil setAuthor(String author) {
		feed.setAuthor(author);
		return this;
	}

	public RssUtil addItem(String title, String link, Date publishedDate, String content, List categories) {
		SyndEntryImpl entry = new SyndEntryImpl();
		entry.setTitle(title);
		entry.setLink(link);
		if (categories != null) {
			List cates = new ArrayList();
			for (Object object : categories) {
				SyndCategoryImpl cate = new SyndCategoryImpl();
				cate.setName(String.valueOf(object));
				cates.add(cate);
			}
			entry.setCategories(cates);
		}
		entry.setPublishedDate(publishedDate);
		SyndContentImpl contentImpl = new SyndContentImpl();
		contentImpl.setType("text/html");
		contentImpl.setValue(content);
		entry.setDescription(contentImpl);
		feed.getEntries().add(entry);
		return this;
	}

	public void render(OutputStream os) throws IOException, FeedException {
		Writer writer = new OutputStreamWriter(os);
		SyndFeedOutput output = new SyndFeedOutput();
		output.output(feed, writer);
	}
	public void render(String file) throws IOException, FeedException {
		Writer writer = new OutputStreamWriter(new FileOutputStream(file));
		SyndFeedOutput output = new SyndFeedOutput();
		output.output(feed, writer);
		writer.close();
	}
}
