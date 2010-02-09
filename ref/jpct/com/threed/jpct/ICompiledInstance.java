// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

interface ICompiledInstance {

	public abstract String getKey();

	public abstract int getTreeID();

	public abstract int getPolyIndex();

	public abstract int getStageCount();

	public abstract void fill();

	public abstract void compileToDL();

	public abstract boolean isFilled();
}
