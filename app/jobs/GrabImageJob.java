package jobs;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import models.Photo;
import play.jobs.Job;
import utils.ImageUtil;
import utils.PhotoUploaderUtil;

public class GrabImageJob extends Job<List<Photo>> {
	private String[] urls;

	public GrabImageJob(String[] urls) {
		this.urls = urls;
	}

	@Override
	public List<Photo> doJobWithResult() throws Exception {
		List<Photo> photos = new ArrayList<Photo>();
		for (int i = 0; i < urls.length; i++) {
			String url = urls[i];
			if (StringUtils.isNotEmpty(url)) {
				String name = null;

				if (url.indexOf(" http://") > 0) {
					name = url.substring(0, url.indexOf(" http://")).trim();
					url = url.substring(url.indexOf(" http://") + 1).trim();
				}
				BufferedImage bi = ImageUtil.grab(url);
				String pathForPhoto = PhotoUploaderUtil.getPathForPhoto();
				ImageUtil.saveJPEG(bi, pathForPhoto);
				if (name == null) {
					name = url.substring(url.lastIndexOf('/') + 1);
					if (name.indexOf('?') > 0) {
						name = name.substring(0, name.indexOf('?'));
					}
				}
				Photo photo = new Photo(name, new Date(), pathForPhoto);
				photo.prefPath = PhotoUploaderUtil.getPathForLarge(pathForPhoto);
				photo.thumbPath = PhotoUploaderUtil.getPathForSmall(pathForPhoto);
				photo.thumb2Path = PhotoUploaderUtil.getPathForMidle(pathForPhoto);
				photos.add(photo);
			}
		}
		return photos;
	}
}
