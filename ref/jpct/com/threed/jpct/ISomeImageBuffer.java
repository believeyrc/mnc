// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.awt.Image;
import java.awt.image.MemoryImageSource;

interface ISomeImageBuffer {

	public abstract void create(int i, int j);

	public abstract Image getImage();

	public abstract MemoryImageSource getSource();

	public abstract int[] getPixels();

	public abstract int getType();
}
