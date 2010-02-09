// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct.threading;

public interface WorkLoad {

	public abstract void doWork();

	public abstract void error(Exception exception);

	public abstract void done();
}
