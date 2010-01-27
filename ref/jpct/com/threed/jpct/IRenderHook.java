// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

public interface IRenderHook {

	public abstract void beforeRendering(int i);

	public abstract void afterRendering(int i);

	public abstract void onDispose();

	public abstract boolean repeatRendering();
}
