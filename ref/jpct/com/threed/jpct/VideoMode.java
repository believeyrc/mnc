// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;

public class VideoMode implements Serializable {

	public VideoMode(int i, int j, int k, int l, int i1) {
		width = i;
		height = j;
		bpp = k;
		depth = l;
		refresh = i1;
	}

	public String toString() {
		return "Resolution: " + width + "x" + height + "x" + bpp + " -- ZBuffer depth: " + depth + " @ " + refresh + "Hz";
	}

	public boolean equalsExactly(VideoMode videomode) {
		return bpp == videomode.bpp && refresh == videomode.refresh && height == videomode.height
				&& width == videomode.width;
	}

	public boolean equalsAlmost(VideoMode videomode, boolean flag) {
		if (bpp >= videomode.bpp && refresh >= videomode.refresh && height == videomode.height && width == videomode.width) {
			if (flag) {
				videomode.bpp = bpp;
				videomode.refresh = refresh;
			}
			return true;
		} else {
			return false;
		}
	}

	private static final long serialVersionUID = 1L;
	public int width;
	public int height;
	public int bpp;
	public int depth;
	public int refresh;
}
