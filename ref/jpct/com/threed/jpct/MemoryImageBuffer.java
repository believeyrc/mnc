// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            ISomeImageBuffer, Config, Logger

final class MemoryImageBuffer implements ISomeImageBuffer, Serializable {

	MemoryImageBuffer() {
	}

	public void create(int i, int j) {
		DirectColorModel directcolormodel = null;
		if (!Config.useFramebufferWithAlpha) {
			directcolormodel = new DirectColorModel(24, 0xff0000, 65280, 255);
		} else {
			directcolormodel = new DirectColorModel(32, 0xff0000, 65280, 255, 0xff000000);
			Logger.log("Framebuffer supports an alpha channel!", 2);
		}
		pixels = new int[i * j];
		source = new MemoryImageSource(i, j, directcolormodel, pixels, 0, i);
		source.setAnimated(true);
		output = Toolkit.getDefaultToolkit().createImage(source);
	}

	public Image getImage() {
		return output;
	}

	public MemoryImageSource getSource() {
		return source;
	}

	public int[] getPixels() {
		return pixels;
	}

	public int getType() {
		return 0;
	}

	private static final long serialVersionUID = 1L;
	private transient MemoryImageSource source;
	private int pixels[];
	private Image output;
}
