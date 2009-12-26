package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;

public class ImageMagick {
	public static void thumbnail(String in, String out, int width, int height) {
		String command = String.format("convert -thumbnail %sX%s \"%s\" \"%s\"", width, height, in, out);
		invokeCommand(command);
	}

	public static void liquidRescale(String in, String out, int width, int height) {
		String command = String.format("convert -liquid-rescale %sX%s \"%s\" \"%s\"", width, height, in, out);
		invokeCommand(command);
	}

	public static void resize(String in, String out, int width, int height) {
		String command = String.format("convert -resize %sX%s \"%s\" \"%s\"", width, height, in, out);
		invokeCommand(command);
	}

	public static void flip(String in, String out) {
		String command = String.format("convert -flip \"%s\" \"%s\"", in, out);
		invokeCommand(command);
	}

	public static void transpose(String in, String out) {
		String command = String.format("convert -transpose \"%s\" \"%s\"", in, out);
		invokeCommand(command);
	}

	public static void transverse(String in, String out) {
		String command = String.format("convert -transverse \"%s\" \"%s\"", in, out);
		invokeCommand(command);
	}

	public static void flop(String in, String out) {
		String command = String.format("convert -flop \"%s\" \"%s\"", in, out);
		invokeCommand(command);
	}

	public static void rotate(String in, String out, int angle) {
		String command = String.format("convert -rotate %s \"%s\" \"%s\"", angle, in, out);
		invokeCommand(command);
	}

	public static void roll(String in, String out, int xoffset, int yoffset) {
		String command = String.format("convert -roll %s %s \"%s\" \"%s\"", xoffset, yoffset, in, out);
		invokeCommand(command);
	}

	public static void drawMVG(String in, String out, String mvg, String text, int x, int y, int w, int h) {
		try {
			String stro = FileUtils.readFileToString(new File(mvg), "UTF-8");
			int vbidx = stro.indexOf("viewbox");
			String vbox = stro.substring(vbidx, stro.indexOf("\n", vbidx));
			int textidx = stro.indexOf("SampleText");
			int textlineBegin = stro.lastIndexOf("\n", textidx);
			
			stro =( stro.substring(0,textlineBegin) +"font 'res\\SIMFANG.TTF'\n" + stro.substring(textlineBegin));
			
			stro = StringUtils.replaceChars(stro, '\r', '\n');
			stro = StringUtils.replaceChars(stro, '\n', ' ');
			stro = StringUtils.replace(stro, "SampleText", new String(text.getBytes("UTF-8")));
			StringBuffer str = new StringBuffer(stro);
			
			
			String[] size = vbox.split(" ");// 0 0 376 229
			System.out.println(vbox);
			System.out.println(size.length);
			double scalew = w / Double.valueOf(size[3]);
			double scaleh = h / Double.valueOf(size[4]);
			str.insert(0, String.format("translate %s,%s scale %s,%s ", x, y, scalew, scaleh));
			String command = String.format("convert -draw \"%s\" \"%s\" \"%s\"", str, in, out);
			invokeCommand(command);

//			String tempTextFile = in + "_text.gif";
//			String textutf8 = in + "_text.utf8";
//			FileUtils.writeStringToFile(new File(textutf8), text, "UTF-8");
//			String textcommand = String.format("convert -size %sx%s -background black -fill white -font \"res\\simfang.ttf\" -pointsize 16 caption:@%s %s ", (int)(w*.8), (int)(h*.8), textutf8, tempTextFile);
//			invokeCommand(textcommand);
//			int yoffset = (int) (y + 1.2);
//			int xoffset = (int) (x * 1.1);
//			String composite = String.format("convert -compose Screen  -geometry %s%s  -composite %s %s %s", xoffset >= 0 ? "+" + xoffset : "" + xoffset, yoffset >= 0 ? "+" + yoffset : "" + yoffset,
//					in, tempTextFile, out);
//			invokeCommand(composite);
			// FileUtils.deleteQuietly(new File(tempTextFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void invokeCommand(String command) {
		try {
			Logger.debug(command);
			String shell = "cmd";
			if(Play.configuration.get("shell")!=null) {
				Play.configuration.get("shell");
			}
			Process e = Runtime.getRuntime().exec(shell);
			final BufferedReader br = new BufferedReader(new InputStreamReader(e.getInputStream(), "GBK"));
			final BufferedReader berrr = new BufferedReader(new InputStreamReader(e.getErrorStream(), "GBK"));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(e.getOutputStream()));
			bw.write(command);
			bw.write("\n");
			bw.write("\n");
			bw.write("exit");
			bw.write("\n");
			bw.write("\n");

			bw.flush();
			Thread ot = new Thread() {
				@Override
				public void run() {
					String line = null;
					try {
						while ((line = br.readLine()) != null) {
							Logger.info("st:" + line);
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
							Logger.error("err:" + line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			et.start();
			et.join();
			ot.join();
			Logger.info("command exit:%s", e.exitValue());
			e.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		rotate("o.jpg", "o.thumb.jpg", 90);
	}
}
