import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class TestHello {
	static String shape = "";

	public static void main(String[] args) throws IOException {
		BufferedImage bi = new BufferedImage(300, 800, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setColor(Color.white);
		g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		g.setColor(Color.green);
		AreaCreate ac = new EllipseBalloon();
		ac.params.put("x", 50);
		ac.params.put("y", 10);
		ac.params.put("w", 200);
		ac.params.put("h", 100);
		ac.params.put("type", "lt");
		g.setColor(Color.red);
		g.draw(ac.create());
		ac.params.put("y", 120);
		ac.params.put("type", "lb");
		g.draw(ac.create());

		ac.params.put("y", 230);
		ac.params.put("type", "rt");
		g.draw(ac.create());

		ac.params.put("y", 340);
		ac.params.put("type", "rb");
		g.draw(ac.create());
		g.dispose();
		ImageIO.write(bi, "jpeg", new File("hello.jpg"));
	}

	static abstract class AreaCreate {
		public Map params = new HashMap();

		public abstract Area create();
	}

	/**
	 * param x,y,w,h,type=lt,lb,rt,rb
	 * 
	 * @author Administrator
	 * 
	 */
	static class EllipseBalloon extends AreaCreate {
		@Override
		public Area create() {
			int w = (Integer) params.get("w");
			int h = (Integer) params.get("h");
			int x = (Integer) params.get("x");
			int y = (Integer) params.get("y");
			String type = (String) params.get("type");
			Ellipse2D ellipse = new Ellipse2D.Double(x, y, w, h);
			Area area = new Area(ellipse);
			Polygon add = null;
			if ("lt".equals(type)) {
				add = new Polygon(new int[] { x, x + (int) (w * 0.25), x + w / 2 }, new int[] { y, y + (int) (h * 0.25),
						y + (int) (h * 0.25) }, 3);
			}
			if ("lb".equals(type)) {
				add = new Polygon(new int[] { x, x + (int) (w * 0.25), x + (int) (w * 0.5) }, new int[] { y + h, y + h - h / 4,
						y + h - h / 4 }, 3);
			}
			if ("rt".equals(type)) {
				add = new Polygon(new int[] { x + w, x + w - w / 4, x + w - w / 2 }, new int[] { y, y + h / 4, y + h / 4 }, 3);
			}
			if ("rb".equals(type)) {
				add = new Polygon(new int[] { x + w, x + w - w / 4, x + w - w / 2 }, new int[] { y + h, y + h - h / 4,
						y + h - h / 4 }, 3);
			}
			if (add != null)
				area.add(new Area(add));
			return area;
		}
	}
}
