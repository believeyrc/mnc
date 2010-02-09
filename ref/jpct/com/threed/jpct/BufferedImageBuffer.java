// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.awt.Image;
import java.awt.image.*;
import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            ISomeImageBuffer, Config, Logger

final class BufferedImageBuffer implements ISomeImageBuffer, Serializable {

	BufferedImageBuffer() {
	}

	public void create(int i, int j) {
		if (!Config.useFramebufferWithAlpha) {
			output = new BufferedImage(i, j, 1);
		} else {
			output = new BufferedImage(i, j, 2);
			Logger.log("Framebuffer supports an alpha channel!", 2);
		}
		pixels = ((DataBufferInt) ((BufferedImage) output).getRaster().getDataBuffer()).getData();
	}

	public Image getImage() {
		return output;
	}

	public MemoryImageSource getSource() {
		return null;
	}

	public int[] getPixels() {
		return pixels;
	}

	public int getType() {
		return 1;
	}

	private static final long serialVersionUID = 1L;
	private int pixels[];
	private Image output;
}
