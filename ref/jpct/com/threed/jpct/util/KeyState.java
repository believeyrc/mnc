// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct.util;

public class KeyState {

	KeyState(int i, char c, boolean flag) {
		keyCode = i;
		chr = c;
		state = flag;
	}

	public boolean getState() {
		return state;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public char getChar() {
		return chr;
	}

	public static final boolean PRESSED = true;
	public static final boolean RELEASED = false;
	public static final KeyState NONE = new KeyState(0, ' ', false);
	private int keyCode;
	private boolean state;
	private char chr;

}
