// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.threed.jpct;

import java.awt.*;
import javax.media.opengl.GLCapabilities;

// Referenced classes of package com.threed.jpct:
//            AWTGLRenderer, JOGLCanvas, VideoMode, GLBase,
//            Logger, IThreadBuffer, Config

public class JOGLRenderer extends AWTGLRenderer {

	JOGLRenderer() {
	}

	public void init(int i, int j, int k, int l, int i1) {
		super.init = false;
		try {
			GLCapabilities glcapabilities = getPixelFormatFromConfig();
			if (glcapabilities == null && i1 > 0) {
				DisplayMode displaymode = findMode(i, j, k, l);
				if (displaymode != null)
					try {
						glcapabilities = createPixelFormat(displaymode.getBitDepth(), 0, l, 0, i1);
					} catch (Exception exception1) {
						Logger.log("Number of samples not supported or incorrect video mode!", 0);
						i1 = 0;
					}
				else
					Logger.log("Can't set videomode - try different settings!", 0);
			}
			if (glcapabilities == null)
				super.canvas = new JOGLCanvas(this);
			else
				super.canvas = new JOGLCanvas(this, glcapabilities);
			super.canvas.setBounds(0, 0, i, j);
			super.canvas.setSamples(i1);
			super.xp = i;
			super.yp = j;
		} catch (Exception exception) {
			Logger.log("Can't initialize canvas!", 0);
			exception.printStackTrace();
		}
	}

	public VideoMode[] getAvailableVideoModes() {
		try {
			GraphicsDevice localGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			DisplayMode[] arrayOfDisplayMode = localGraphicsDevice.getDisplayModes();
			VideoMode[] arrayOfVideoMode = new VideoMode[arrayOfDisplayMode.length];
			for (int i = 0; i < arrayOfDisplayMode.length; ++i) {
				if (arrayOfDisplayMode[i] == null)
					continue;
				arrayOfVideoMode[i] = new VideoMode(arrayOfDisplayMode[i].getWidth(), arrayOfDisplayMode[i].getHeight(),
						arrayOfDisplayMode[i].getBitDepth(), Config.glZBufferDepth, arrayOfDisplayMode[i].getRefreshRate());
			}
			return arrayOfVideoMode;
		} catch (Exception localException) {
			Logger.log("Couldn't get available video modes: " + localException.getMessage(), 0);
		}
		return null;
	}

	private DisplayMode findMode(int i, int j, int k, int l) throws Exception {
		int i1 = -1;
		GraphicsDevice graphicsdevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		DisplayMode adisplaymode[] = graphicsdevice.getDisplayModes();
		int j1 = 0;
		do {
			if (j1 >= adisplaymode.length)
				break;
			if (Config.glVerbose)
				Logger.log("mode: " + adisplaymode[j1].getWidth() + "/" + adisplaymode[j1].getHeight() + "/"
						+ adisplaymode[j1].getBitDepth() + "/" + adisplaymode[j1].getRefreshRate(), 2);
			if (adisplaymode[j1].getWidth() == i && adisplaymode[j1].getHeight() == j && adisplaymode[j1].getBitDepth() == k
					&& (Config.glRefresh == 0 || adisplaymode[j1].getRefreshRate() == Config.glRefresh || !Config.glFullscreen)) {
				i1 = j1;
				break;
			}
			j1++;
		} while (true);
		if (i1 == -1) {
			Logger.log("Can't find desired videomode (" + i + " x " + j + " x " + k + ") - searching for alternatives", 2);
			int k1 = 0;
			do {
				if (k1 >= adisplaymode.length)
					break;
				if (adisplaymode[k1].getWidth() == i && adisplaymode[k1].getHeight() == j
						&& adisplaymode[k1].getBitDepth() >= k
						&& (adisplaymode[k1].getRefreshRate() >= Config.glRefresh || !Config.glFullscreen)) {
					i1 = k1;
					break;
				}
				k1++;
			} while (true);
			if (i1 == -1) {
				Logger.log("Can't find alternative videomode (" + i + " x " + j + " x " + k + ") - trying something else", 2);
				int l1 = 0;
				do {
					if (l1 >= adisplaymode.length)
						break;
					if (adisplaymode[l1].getWidth() == i && adisplaymode[l1].getHeight() == j) {
						i1 = l1;
						break;
					}
					l1++;
				} while (true);
			}
			if (i1 == -1) {
				Logger.log("Can't find any suitable videomode!", 0);
				return null;
			}
		}
		return adisplaymode[i1];
	}

	private GLCapabilities getPixelFormatFromConfig() {
		if (Config.glAdditionalConfiguration != null) {
			for (int i = 0; i < Config.glAdditionalConfiguration.length; i++)
				if (Config.glAdditionalConfiguration[i] instanceof GLCapabilities)
					return (GLCapabilities) Config.glAdditionalConfiguration[i];

		}
		return null;
	}

	public static GLCapabilities createPixelFormat(int i, int j, int k, int l, int i1) {
		GLCapabilities glcapabilities = new GLCapabilities();
		glcapabilities.setRedBits(8);
		glcapabilities.setGreenBits(8);
		glcapabilities.setBlueBits(8);
		glcapabilities.setAlphaBits(j);
		glcapabilities.setDepthBits(k);
		glcapabilities.setStencilBits(l);
		glcapabilities.setSampleBuffers(true);
		glcapabilities.setNumSamples(i1);
		return glcapabilities;
	}
}
