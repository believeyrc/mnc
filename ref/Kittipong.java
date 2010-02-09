package classicsgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import classicsgame.Kittipong.Change.Action;

public class Kittipong {
	private static volatile int count = 0;

	public static class Box {
		public int id = count++;
		public int type;
		public int x, y;

		public Box(int x, int y, int type) {
			this.x = x;
			this.y = y;
			this.type = type;
		}
	}

	public static class Change {
		public static enum Action {
			ADD, REMOVE, EXCHANGE, MOVETO
		};

		public Box box1, box2;
		public Action action;

		public Change(Action a, Box b) {
			this.action = a;
			this.box1 = b;
		}

		public Change(Action exchange, Box box1, Box box2) {
			this.action = exchange;
			this.box1 = box1;
			this.box2 = box2;
		}
	}

	public static enum DIR {
		L, R, T, B
	};

	private int gridwidth = 8, gridheight = 7;
	private int typeCount = 9;
	private Box[][] grid;

	public List<Change> clear() {
		List<Change> res = new ArrayList<Change>();
		if (grid != null)
			for (int y = 0; y < gridheight; y++) {
				for (int x = 0; x < gridwidth; x++) {
					res.add(new Change(Action.REMOVE, grid[y][x]));
					grid[y][x] = null;
				}
			}
		return res;
	}

	public List<Change> init() {
		List<Change> res = new ArrayList<Change>();
		if (grid == null)
			grid = new Box[gridheight][gridwidth];
		for (int y = 0; y < gridheight; y++) {
			for (int x = 0; x < gridwidth; x++) {
				Box box = new Box(x, y, generateType());
				grid[y][x] = box;
				res.add(new Change(Action.ADD, box));
			}
		}
		return res;
	}

	protected int generateType() {
		return random.nextInt(typeCount);
	}

	public void print() {
		for (int y = 0; y < gridheight; y++) {
			for (int x = 0; x < gridwidth; x++) {
				Box box = grid[y][x];
				System.out.print(" " + (box == null ? " " : box.type));
			}
			System.out.println();
		}
	}

	private static class Mark {
		public int x1 = -1, y1 = -1, x2 = -1, y2 = -1, type = -1;

		@Override
		public String toString() {
			return String.format("%s:[%s,%s]-[%s,%s]", type, x1, y1, x2, y2);
		}
	}

	public Box getByXY(int x, int y) {
		return grid[y][x];
	}

	public boolean isNeighber(Box box1, Box box2) {
		return box1 != null && box2 != null && Math.abs(box1.x - box2.x) + Math.abs(box1.y - box2.y) == 1;
	}

	int[][] checkMatirx = { { 0, -2, 1 }, { 0, -1, 2 }, { 0, +1, 2 }, { 0, +2, 1 }, { -2, 0, 1 }, { -1, 0, 2 }, { +1, 0, 2 }, { +2, 0, 1 } };

	private boolean checkBounds(int y, int x) {
		return 0 <= x && x < gridwidth && 0 <= y && y < gridheight;
	}

	public boolean test(Box box1, Box box2) {
		System.out.println("test");
		if (box1.type == box2.type)
			return false;
		int count1 = 0, count2 = 0;
		System.out.println(box1.y + "," + box1.x);
		for (int i = 0; i < checkMatirx.length; i++) {
			int[] m = checkMatirx[i];
			int y = box1.y + m[0];
			int x = box1.x + m[1];
			if (checkBounds(y, x)) {
				System.out.println(y + "," + x + "," + grid[y][x].type + "," + box2.type);
				if (grid[y][x].type == box2.type) {
					count1 += m[2];
				}
			}
		}
		System.out.println("count1" + count1);
		if (count1 >= 3)
			return true;
		System.out.println(box2.y + "," + box2.x);
		for (int i = 0; i < checkMatirx.length; i++) {
			int[] m = checkMatirx[i];
			int y = box2.y + m[0];
			int x = box2.x + m[1];
			if (checkBounds(y, x)) {
				System.out.println(y + "," + x + "," + grid[y][x].type + "," + box1.type);
				if (grid[y][x].type == box1.type) {
					count2 += m[2];
				}
			}
		}
		System.out.println("count2" + count2);
		if (count2 >= 3)
			return true;
		return false;
	}

	public List<Change> exchange(Box box1, Box box2) {
		List<Change> res = new ArrayList<Change>();
		if (Math.abs(box1.x - box2.x) + Math.abs(box1.y - box2.y) == 1) {
			Box t = grid[box1.y][box1.x];
			grid[box1.y][box1.x] = grid[box2.y][box2.x];
			grid[box2.y][box2.x] = t;
			int tx = box1.x;
			box1.x = box2.x;
			box2.x = tx;
			int ty = box1.y;
			box1.y = box2.y;
			box2.y = ty;
			res.add(new Change(Action.EXCHANGE, box1, box2));
		}
		return res;
	}

	private List<Change> clearMark(Mark mark) {
		List<Change> res = new ArrayList<Change>();
		for (int y = mark.y1; y <= mark.y2; y++) {
			for (int x = mark.x1; x <= mark.x2; x++) {
				res.add(new Change(Action.REMOVE, grid[y][x]));
				grid[y][x] = null;
			}
		}
		return res;
	}

	public List<Change> flowFrom(DIR dir) {
		List<Change> res = new ArrayList<Change>();
		if (dir == DIR.T) {
			for (int x = 0; x < gridwidth; x++) {
				int c = 0;
				for (int y = gridheight - 1; y >= 0; y--) {
					if (grid[y][x] == null) {
						c++;
					} else {
						if (c > 0) {
							Box box = grid[y][x];
							grid[y + c][x] = box;
							box.x = x;
							box.y = y + c;
							res.add(new Change(Action.MOVETO, box));
						}
					}
				}
				for (int i = c - 1; i >= 0; i--) {
					Box box = new Box(x, i, generateType());
					res.add(new Change(Action.ADD, box));
					grid[i][x] = box;
				}
			}
		}
		if (dir == DIR.B) {
			for (int x = 0; x < gridwidth; x++) {
				int c = 0;
				for (int y = 0; y < gridheight; y++) {
					if (grid[y][x] == null) {
						c++;
					} else {
						if (c > 0) {
							Box box = grid[y][x];
							grid[y + c][x] = box;
							box.x = x;
							box.y = y - c;
							res.add(new Change(Action.MOVETO, box));
						}
					}
				}
				for (int i = 0; i < c; i++) {
					Box box = new Box(x, gridheight - i - 1, generateType());
					res.add(new Change(Action.ADD, box));
					grid[gridheight - i - 1][x] = box;
				}
			}
		}
		return res;
	}

	public List<Change> checkAndClear() {
		List<Change> res = new ArrayList<Change>();
		List<Mark> check = check();
		for (Mark mark : check) {
			res.addAll(clearMark(mark));
		}
		return res;
	}

	private List<Mark> check() {
		List<Mark> result = new ArrayList<Mark>();
		if (grid != null)
			for (int y = 0; y < gridheight; y++) {
				Mark mark = new Mark();// type y1,x1 y2 x2;
				mark.y1 = y;
				mark.y2 = y;
				for (int x = 0; x < gridwidth; x++) {
					Box box = grid[y][x];
					if (box != null)
						if (mark.type == box.type) {
							mark.x2 = x;
						} else {
							if (mark.x2 - mark.x1 >= 2) {
								result.add(mark);
								mark = new Mark();
								mark.y1 = y;
								mark.y2 = y;
							}
							mark.x1 = x;
							mark.type = box.type;
						}
					else
						mark = new Mark();
				}
				if (mark.x2 - mark.x1 >= 2) {
					result.add(mark);
				}
			}
		if (grid != null)
			for (int x = 0; x < gridwidth; x++) {
				Mark mark = new Mark();// type y1,x1 y2 x2;
				mark.x1 = x;
				mark.x2 = x;
				for (int y = 0; y < gridheight; y++) {
					Box box = grid[y][x];
					if (box != null)
						if (mark.type == box.type) {
							mark.y2 = y;
						} else {
							if (mark.y2 - mark.y1 >= 2) {
								result.add(mark);
								mark = new Mark();
								mark.x1 = x;
								mark.x2 = x;
							}
							mark.y1 = y;
							mark.type = box.type;
						}
					else
						mark = new Mark();
				}
				if (mark.y2 - mark.y1 >= 2) {
					result.add(mark);
				}
			}
		return result;
	}

	private Random random = new Random();

	public static void main(String[] args) {
		Kittipong kittipong = new Kittipong();
		kittipong.init();
		kittipong.print();
		List<Mark> res = kittipong.check();
		// result
		for (Mark mark : res) {
			System.out.println(mark);
			kittipong.clearMark(mark);
		}
		System.out.println("flow===");
		kittipong.flowFrom(Kittipong.DIR.B);
		kittipong.print();
	}
}
