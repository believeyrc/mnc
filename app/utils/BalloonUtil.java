package utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.font.GlyphVector;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import utils.BalloonUtil.EllipseBalloon.TYPE;

public class BalloonUtil {
	public static void addEllipseBalloon(String file, int x, int y, int w, int h, TYPE type, String text) {
		ImageMagick.drawMVG(file, file, "mvg/hello-lt.mvg", text, x, y, w, h);
	}

	private static void useJ2D(BufferedImage bi, int x, int y, int w, int h, TYPE type, String text) {
		AreaCreate ac = new EllipseBalloon();
		ac.params.put("x", x);
		ac.params.put("y", y);
		ac.params.put("w", w);
		ac.params.put("h", h);
		ac.params.put("type", type);
		Graphics2D g2 = bi.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.setStroke(new BasicStroke(1.2f));

		Color background = new Color(0xe1, 0xe1, 0xe1, 0x91);
		Color borderCorlor = Color.WHITE;
		Color textColor = Color.BLACK.brighter();
		Area shape = ac.create();
		if (shape != null) {
			g2.setColor(background);
			g2.fill(shape);
			g2.setStroke(new BasicStroke(1.2f));
			g2.setColor(borderCorlor);
			g2.draw(shape);
			if (text != null) {
				g2.setStroke(new BasicStroke(1.0f));
				g2.setColor(textColor);
				g2.setFont(new Font("Song", Font.PLAIN, 14));
				GlyphVector gv = g2.getFont().createGlyphVector(g2.getFontRenderContext(), text);
				Rectangle2D vb = gv.getVisualBounds();
				g2.translate(x + (w - vb.getWidth()) / 2, y + (h - vb.getHeight()) / 2 + vb.getHeight());
				g2.drawString(text, 0, 0);
			}
		}
		g2.dispose();
	}

	public static abstract class AreaCreate {
		public Map params = new HashMap();

		public abstract Area create();
	}

	/**
	 * param x,y,w,h,type=lt,lb,rt,rb
	 * 
	 * @author Administrator
	 * 
	 */
	public static class EllipseBalloon extends AreaCreate {
		public static enum TYPE {
			LT, LB, RT, RB
		}

		@Override
		public Area create() {
			int w = (Integer) params.get("w");
			int h = (Integer) params.get("h");
			int x = (Integer) params.get("x");
			int y = (Integer) params.get("y");
			TYPE type = (TYPE) params.get("type");
			Ellipse2D ellipse = new Ellipse2D.Double(x, y, w, h);
			Area area = new Area(ellipse);
			Polygon add = null;
			if (TYPE.LT.equals(type)) {
				add = new Polygon(new int[] { x, x + (int) (w * 0.25), x + w / 2 }, new int[] { y, y + (int) (h * 0.25), y + (int) (h * 0.25) }, 3);
			}
			if (TYPE.LB.equals(type)) {
				add = new Polygon(new int[] { x, x + (int) (w * 0.25), x + (int) (w * 0.5) }, new int[] { y + h, y + h - h / 4, y + h - h / 4 }, 3);
			}
			if (TYPE.RT.equals(type)) {
				add = new Polygon(new int[] { x + w, x + w - w / 4, x + w - w / 2 }, new int[] { y, y + h / 4, y + h / 4 }, 3);
			}
			if (TYPE.RB.equals(type)) {
				add = new Polygon(new int[] { x + w, x + w - w / 4, x + w - w / 2 }, new int[] { y + h, y + h - h / 4, y + h - h / 4 }, 3);
			}
			if (add != null)
				area.add(new Area(add));
			return area;
		}
	}
}
