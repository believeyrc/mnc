// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            ITextureEffect, Texture

public class WaterTextureEffect implements ITextureEffect, Serializable {

	public WaterTextureEffect(int i) {
		width = 0;
		height = 0;
		shifter = 0;
		ripples = 0;
		ripples = i;
	}

	public void init(Texture texture) {
		buffer1 = new int[texture.getArraySize()];
		buffer2 = new int[texture.getArraySize()];
		width = texture.getWidth();
		height = texture.getHeight();
		shifter = texture.shifter;
	}

	public void apply(int ai[], int ai1[]) {
		int i = (int) (Math.random() * (double) width * (double) height);
		buffer1[i] = ripples;
		int k = height << shifter - width;
		for (int l = width; l < k; l++) {
			int j = (buffer1[l - 1] + buffer1[l + 1] + buffer1[l - width] + buffer1[l + width] >> 1) - buffer2[l];
			buffer2[l] = j - (j >> 5);
		}

		int i1 = width * height;
		for (int j1 = width; j1 < i1; j1++) {
			int k1 = buffer2[j1 - 1] - buffer2[j1 + 1];
			int l1 = buffer2[j1 - width] - buffer2[j1 + width];
			int i2 = j1 + k1 + (l1 << shifter);
			if (i2 < 0)
				i2 = 0;
			else if (i2 > i1)
				i2 = i1;
			ai[j1] = ai1[i2];
		}

		int ai2[] = buffer1;
		buffer1 = buffer2;
		buffer2 = ai2;
	}

	public boolean containsAlpha() {
		return false;
	}

	private static final long serialVersionUID = 1L;
	private int buffer1[];
	private int buffer2[];
	private int width;
	private int height;
	private byte shifter;
	private int ripples;
}
