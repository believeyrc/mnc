import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

import jgame.JGColor;
import jgame.JGObject;
import jgame.JGPoint;
import jgame.platform.StdGame;

public class Kitipong extends StdGame {
	public Kitipong() {
		initAppConfig();
	}

	public Kitipong(JGPoint size) {
		initEngine(12 * iw, 8 * ih);
	}

	@Override
	public void initCanvas() {
		setCanvasSettings(12, 8, 54, 64, null, null, null);
	}

	@Override
	public void initGame() {
		setFrameRate(60, 2);
		setSmoothing(true);
		defineMedia("Kitipong.tbl");

		setBGImage("background");
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 8; j++) {
				new Box(i, j);
			}
		}
		setAuthorMessage("Nile Black");

		setCursor(null);
		int[] pixels = new int[16 * 16];
		Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
		Cursor transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisibleCursor");
		setCursor(transparentCursor);
	}

	Box selected = null;

	@Override
	public void doFrame() {
		super.doFrame();
		setColor(JGColor.red);
		swapper.update();
		checker.checkAndUpdate();
		moveObjects();
		if (getMouseButton(1) && !swapper.swapping) {
			int x = (getMouseX() - iw / 2) / iw;
			int y = (getMouseY()) / ih;
			Box box = (Box) getObject("zyobject" + (x) + "-" + (y));
			if (box != null) {
				if (selected != null) {
					if (selected.isNeiboughter(box)) {
						swapper.swap(selected, box);
						selected = null;
					} else if (selected == box) {
						selected.updateSelected(false);
						selected = null;
					} else {
						selected.updateSelected(false);
						selected = box;
						selected.updateSelected(true);
					}
				} else {
					selected = box;
					selected.updateSelected(true);
				}
			}
			clearMouseButton(1);
		}
	}

	int iw = 54, ih = 64;
	static Swapper swapper = new Swapper();
	Checker checker = new Checker();
	@Override
	public void paintFrame() {
		drawImage(getMouseX(), getMouseY(), "cursor");
	}

	public static void main(String[] args) {
		new Kitipong(new JGPoint(800, 600));
	}

	class Checker {
		void checkAndUpdate() {
			for (int y = 0; y < 7; y++) {
				for (int x = 0; x < 8; x++) {
					Box box = (Box) getObject("zyobject"+y+"-"+x);
//					int ctype =  
				}	
			}
		}
	}
	
	static class Swapper {
		Box a, b;
		boolean swapping;

		void swap(Box a, Box b) {
			if (!swapping) {
				swapping = true;
				this.a = a;
				this.b = b;
				this.a.reset();
				this.b.reset();
			}
		}

		private static void changePosition(Box a, Box b) {
			String img = a.getImageName();
			a.setImage(b.getImageName());
			b.setImage(img);
			a.reset();
			b.reset();
		}

		void update() {
			if (a != null && b != null) {
				if (a.gridx - b.gridx > 0) {
					a.x--;
					b.x++;
					if (Math.abs(a.x - b.ox) < 0.1) {
						Swapper.changePosition(a, b);
						reset();
					}
				} else if (a.gridx - b.gridx < 0) {
					a.x++;
					b.x--;
					if (Math.abs(b.x - a.ox) < 0.1) {
						Swapper.changePosition(a, b);
						reset();
					}
				} else if (a.gridy - b.gridy > 0) {
					a.y--;
					b.y++;
					if (Math.abs(b.y - a.oy) < 0.1) {
						Swapper.changePosition(a, b);
						reset();
					}

				} else if (a.gridy - b.gridy < 0) {
					a.y++;
					b.y--;
					if (Math.abs(b.y - a.oy) < 0.1) {
						Swapper.changePosition(a, b);
						reset();
					}
				}
			}
		}

		void reset() {
			swapping = false;
			a = null;
			b = null;
		}
	}

	class Box extends JGObject {
		int state = 0;
		double ox, oy;
		private int gridx;
		private int gridy;
		private int type = 0;
		public Box(int x, int y) {
			super("zyobject" + x + "-" + y, false, // name
					iw / 2 + x * iw, y * ih, 1, // collision ID
					random(1, 9, 1) + ""// name of sprite or animation to use.
			);
			this.ox = this.x;
			this.oy = this.y;
			this.gridx = x;
			this.gridy = y;
			this.type = Integer.parseInt(getImageName());
		}

		@Override
		public void move() {
			if (state == 1) {// selected
				double d = this.y - this.oy;
				if (d > 1) {
					ydir = 1;
				}
				if (d < -4) {
					ydir = -1;
				}
				this.y -= ydir * getGameSpeed() * .5;
			}
		}

		public boolean isNeiboughter(Box t) {
			return (Math.abs(t.gridy - this.gridy) + Math.abs(t.gridx - this.gridx)) == 1;
		}

		public void updateSelected(boolean b) {
			reset();
			state = (b ? 1 : 0);
		}

		private void reset() {
			this.x = this.ox;
			this.y = this.oy;
			state = 0;
		}
	}

	class Select extends JGObject {
		public Select(int x, int y) {
			super("select", false, // name
					x * iw, y * ih, 1, null);
		}

		@Override
		public void paint() {
			setColor(JGColor.red);
			drawOval(this.x, this.y - ih / 2, 50, 40, true, true);
		}

		void updateSelect(int x, int y) {
			this.x = x * iw;
			this.y = y * ih;
		}
	}
}
