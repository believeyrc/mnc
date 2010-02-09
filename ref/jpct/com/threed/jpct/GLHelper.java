// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;
import org.lwjgl.opengl.*;

// Referenced classes of package com.threed.jpct:
//            FrameBuffer, Config, Logger

class GLHelper implements Serializable {

	GLHelper() {
	}

	static void swap() {
		if (Display.isCreated())
			synchronized (FrameBuffer.SYNCHRONIZER) {
				if (Config.glForceFinish)
					GL11.glFinish();
				Display.update();
			}
	}

	static void dispose() {
		if (Display.isCreated())
			synchronized (FrameBuffer.SYNCHRONIZER) {
				try {
					Display.destroy();
				} catch (Exception exception) {
					Logger.log("Display shutdown already in progress!", 2);
				}
			}
	}

	static PixelFormat getPixelFormatFromConfig() {
		if (Config.glAdditionalConfiguration != null) {
			for (int i = 0; i < Config.glAdditionalConfiguration.length; i++)
				if (Config.glAdditionalConfiguration[i] instanceof PixelFormat)
					return (PixelFormat) Config.glAdditionalConfiguration[i];

		}
		return null;
	}

	static DisplayMode getDisplayModeFromConfig() {
		if (Config.glAdditionalConfiguration != null) {
			for (int i = 0; i < Config.glAdditionalConfiguration.length; i++)
				if (Config.glAdditionalConfiguration[i] instanceof DisplayMode)
					return (DisplayMode) Config.glAdditionalConfiguration[i];

		}
		return null;
	}

	static DisplayMode findMode(int i, int j, int k, int l) throws Exception {
		int i1 = -1;
		DisplayMode adisplaymode[] = Display.getAvailableDisplayModes();
		int j1 = 0;
		do {
			if (j1 >= adisplaymode.length)
				break;
			if (Config.glVerbose)
				Logger.log("mode: " + adisplaymode[j1].getWidth() + "/" + adisplaymode[j1].getHeight() + "/"
						+ adisplaymode[j1].getBitsPerPixel() + "/" + adisplaymode[j1].getFrequency(), 2);
			if (adisplaymode[j1].getWidth() == i && adisplaymode[j1].getHeight() == j
					&& adisplaymode[j1].getBitsPerPixel() == k
					&& (Config.glRefresh == 0 || adisplaymode[j1].getFrequency() == Config.glRefresh || !Config.glFullscreen)) {
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
						&& adisplaymode[k1].getBitsPerPixel() >= k
						&& (adisplaymode[k1].getFrequency() >= Config.glRefresh || !Config.glFullscreen)) {
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

	static void printDriver() {
		try {
			String s = Display.getAdapter() + "/" + Display.getVersion();
			String s1 = getHardware();
			if (s1 == null)
				s1 = "unknown card";
			if (s.equals("null/null"))
				s = "unknown OpenGL driver";
			if (s.indexOf("intel") != -1 || s1.indexOf("intel") != -1)
				Config.glBatchSize = Math.min(1000, Config.glBatchSize);
			Logger.log("Driver is: " + s + " on " + s1, 2);
		} catch (Throwable throwable) {
			Logger.log("Driver is unknown!", 2);
		}
	}

	static final String getHardware() {
		return GL11.glGetString(7936) + " / " + GL11.glGetString(7937);
	}

	static boolean init(int i, int j, int k, int l, int i1) {
		if (Config.glSkipInitialization)
			return true;
		boolean flag = true;
		boolean flag1 = false;
		DisplayMode displaymode = getDisplayModeFromConfig();
		PixelFormat pixelformat = getPixelFormatFromConfig();
		try {
			if (displaymode == null) {
				displaymode = findMode(i, j, k, l);
				if (displaymode == null)
					flag = false;
			}
			int j1 = displaymode.getBitsPerPixel();
			Logger.log("Current mode:" + displaymode, 2);
			synchronized (FrameBuffer.SYNCHRONIZER) {
				Display.setFullscreen(Config.glFullscreen);
				Display.setDisplayMode(displaymode);
				Display.setTitle(Config.glWindowName);
				Display.setVSyncEnabled(Config.glVSync);
				if (pixelformat == null)
					try {
						Display.create(new PixelFormat(j1, 0, l, 0, i1));
					} catch (Throwable throwable) {
						boolean flag2 = false;
						if (i1 != 0)
							try {
								Logger.log("Number of samples (" + i1 + ") not supported!", 1);
								Display.create(new PixelFormat(j1, 0, l, 0, 0));
								flag2 = true;
							} catch (Throwable throwable1) {
							}
						if (!flag2) {
							Logger.log("ZBuffer depth of " + l + " not supported - trying something else now...!", 1);
							try {
								Display.create(new PixelFormat(j1, 0, 32, 0, 0));
								Logger.log("ZBuffer depth is now set to 32bpp!", 1);
								Config.glZBufferDepth = 32;
							} catch (Throwable throwable2) {
								try {
									Display.create(new PixelFormat(j1, 0, 16, 0, 0));
									Logger.log("ZBuffer depth is now set to 16bpp!", 1);
									Config.glZBufferDepth = 16;
								} catch (Throwable throwable3) {
									try {
										Logger.log("Does this machine actually support OpenGL? Trying everything at lowest settings now!",
												1);
										Display.create(new PixelFormat(16, 0, 16, 0, 0));
									} catch (Exception exception1) {
										if (Config.glUseUnappropriateModes) {
											try {
												Logger.log("Config.glUseUnappropriateModes is true...trying that...", 1);
												Display.create(new PixelFormat());
											} catch (Exception exception2) {
												printDriver();
												Logger.log("...but to no avail!", 0);
												throw exception1;
											}
										} else {
											printDriver();
											Logger.log("Unable to set any valid videomode on this machine!", 0);
											throw exception1;
										}
									}
								}
							}
						}
					}
				else
					Display.create(pixelformat);
			}
		} catch (Exception exception) {
			Logger.log("Can't set videomode - try different settings!", 0);
			if (!(exception instanceof ArrayIndexOutOfBoundsException) && !(exception instanceof NullPointerException))
				exception.printStackTrace();
			flag = false;
		}
		return flag;
	}

	private static final long serialVersionUID = 1L;
}
