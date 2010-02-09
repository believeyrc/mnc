import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ImageMagick {
	public static void thumbnail(String in, String out, int width, int height) {
		String command = String.format("convert -sample %sX%s %s %s", width, height, in, out);
		invokeCommand(command);
	}

	public static void liquidRescale(String in, String out, int width, int height) {
		String command = String.format("convert -liquid-rescale %sX%s %s %s", width, height, in, out);
		invokeCommand(command);
	}

	public static void resize(String in, String out, int width, int height) {
		String command = String.format("convert -resize %sX%s %s %s", width, height, in, out);
		invokeCommand(command);
	}

	public static void flip(String in, String out) {
		String command = String.format("convert -flip %s %s", in, out);
		invokeCommand(command);
	}

	public static void transpose(String in, String out) {
		String command = String.format("convert -transpose %s %s", in, out);
		invokeCommand(command);
	}

	public static void transverse(String in, String out) {
		String command = String.format("convert -transverse %s %s", in, out);
		invokeCommand(command);
	}

	public static void flop(String in, String out) {
		String command = String.format("convert -flop %s %s", in, out);
		invokeCommand(command);
	}

	public static void rotate(String in, String out, int angle) {
		String command = String.format("convert -rotate %s %s %s", angle, in, out);
		invokeCommand(command);
	}
	public static void roll(String in, String out, int xoffset,int yoffset) {
		String command = String.format("convert -roll %s %s %s %s", xoffset,yoffset, in, out);
		invokeCommand(command);
	}

	private static void invokeCommand(String command) {
		try {
			Process e = Runtime.getRuntime().exec("cmd");
			final BufferedReader br = new BufferedReader(new InputStreamReader(e.getInputStream(), "GBK"));
			final BufferedReader berrr = new BufferedReader(new InputStreamReader(e.getErrorStream(), "GBK"));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(e.getOutputStream()));
			bw.write(command);
			bw.write("\n");
			bw.write("\n");
			bw.write("exit");
			bw.write("\n");
			bw.write("\n");
			bw.write("\n");

			bw.flush();
			Thread ot = new Thread() {
				@Override
				public void run() {
					String line = null;
					try {
						while ((line = br.readLine()) != null) {
							System.out.println("st:" + line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			ot.start();
			Thread et = new Thread() {
				@Override
				public void run() {
					String line = null;
					try {
						while ((line = berrr.readLine()) != null) {
							System.out.println("err:" + line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			et.start();
			et.join();
			ot.join();
			System.out.println(e.exitValue());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		rotate("o.jpg", "o.thumb.jpg",90);
	}
}
