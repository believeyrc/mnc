// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct.procs;

import org.lwjgl.opengl.GL11;

class ProcsHelper {

	ProcsHelper() {
	}

	public static void toOrtho() {
		GL11.glMatrixMode(5888);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glMatrixMode(5889);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, 1.0D, 1.0D, 0.0D, 0.10000000000000001D, 100D);
	}

	public static void restoreProjection() {
		GL11.glMatrixMode(5889);
		GL11.glPopMatrix();
		GL11.glMatrixMode(5888);
		GL11.glPopMatrix();
	}
}
