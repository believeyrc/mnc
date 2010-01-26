package classicsgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Kittipong {
	public static enum DIR {
		L, R, T, B
	};

	private int gridwidth = 8, gridheight = 7;
	private int typeCount = 9;
	private int[][] grid;

	public void clear() {
		for (int y = 0; y < gridheight; y++) {
			for (int x = 0; x < gridwidth; x++) {
				grid[y][x] = -1;
			}
		}
	}

	public void init() {
		if (grid == null)
			grid = new int[gridheight][gridwidth];
		for (int y = 0; y < gridheight; y++) {
			for (int x = 0; x < gridwidth; x++) {
				grid[y][x] = generateType();
			}
		}
	}

	private int generateType() {
		return random.nextInt(typeCount);
	}

	public void print() {
		for (int y = 0; y < gridheight; y++) {
			for (int x = 0; x < gridwidth; x++) {
				System.out.print(" " + grid[y][x]);
			}
			System.out.println();
		}
	}

	public static class Mark {
		public int x1 = -1, y1 = -1, x2 = -1, y2 = -1, type = -1;

		@Override
		public String toString() {
			return String.format("%s:[%s,%s]-[%s,%s]", type, x1, y1, x2, y2);
		}
	}

	public void exchange(int y1, int x1, int y2, int x2) {
		int t = grid[y1][x1];
		grid[y1][x1] = grid[y2][x2];
		grid[y2][x2] = t;
	}

	public void clearMark(Mark mark) {
		for (int y = mark.y1; y <= mark.y2; y++) {
			for (int x = mark.x1; x <= mark.x2; x++) {
				grid[y][x] = -1;
			}
		}
	}

	public void flowFrom(DIR dir) {
		if (dir == DIR.T) {
			for (int x = 0; x < gridwidth; x++) {
				int c = 0;
				for (int y = gridheight - 1; y >= 0; y--) {
					if (grid[y][x] == -1) {
						c++;
					} else {
						if (c > 0) {
							grid[y + c][x] = grid[y][x];
						}
					}
				}
				for (int i = c - 1; i >= 0; i--) {
					grid[i][x] = -1;// generateType();
				}
			}
		}
		if (dir == DIR.B) {
			for (int x = 0; x < gridwidth; x++) {
				int c = 0;
				for (int y = 0; y < gridheight; y++) {
					if (grid[y][x] == -1) {
						c++;
					} else {
						if (c > 0) {
							grid[y - c][x] = grid[y][x];
						}
					}
				}
				for (int i = 0; i < c; i++) {
					grid[gridheight-i-1][x] = -1;// generateType();
				}
			}
		}
	}

	public List<Mark> check() {
		List<Mark> result = new ArrayList<Mark>();
		for (int y = 0; y < gridheight; y++) {
			Mark mark = new Mark();// type y1,x1 y2 x2;
			mark.y1 = y;
			mark.y2 = y;
			for (int x = 0; x < gridwidth; x++) {
				if (mark.type == grid[y][x]) {
					mark.x2 = x;
				} else {
					if (mark.x2 - mark.x1 >= 2) {
						result.add(mark);
						mark = new Mark();
						mark.y1 = y;
						mark.y2 = y;
					}
					mark.x1 = x;
					mark.type = grid[y][x];
				}
			}
			if (mark.x2 - mark.x1 >= 2) {
				result.add(mark);
			}
		}
		for (int x = 0; x < gridwidth; x++) {
			Mark mark = new Mark();// type y1,x1 y2 x2;
			mark.x1 = x;
			mark.x2 = x;
			for (int y = 0; y < gridheight; y++) {
				if (mark.type == grid[y][x]) {
					mark.y2 = y;
				} else {
					if (mark.y2 - mark.y1 >= 2) {
						result.add(mark);
						mark = new Mark();
						mark.x1 = x;
						mark.x2 = x;
					}
					mark.y1 = y;
					mark.type = grid[y][x];
				}
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
