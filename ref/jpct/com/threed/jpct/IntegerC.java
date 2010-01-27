// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

class IntegerC {

	IntegerC() {
	}

	public static Integer valueOf(int i) {
		if (i >= -2000 && i <= 1999)
			return CACHE[i + 2000];
		else
			return new Integer(i);
	}

	private static final Integer CACHE[];

	static {
		CACHE = new Integer[4000];
		for (int i = 0; i < CACHE.length; i++)
			CACHE[i] = new Integer(i - 2000);

	}
}
