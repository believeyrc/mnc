import java.util.Properties;

import javax.imageio.ImageIO;

import play.Play;

import utils.ImageUtil;

public class TestGif {
	public static void main(String[] args) {
		Play.configuration = new Properties();
		ImageUtil.saveJPEG(ImageUtil.thumbnail(ImageUtil
				.grab("http://farm5.static.flickr.com/4043/4413839495_e404cd5643.jpg"), 240, 240, false, false), "240.jpg");
	}
}
