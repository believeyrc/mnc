// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

// Referenced classes of package com.threed.jpct:
//            VisList, Camera, AWTDisplayList

interface IThreadBuffer {

	public abstract void add(int i, Object obj);

	public abstract void addOnce(int i, Object obj);

	public abstract void add(VisList vislist, int i, int j, int k);

	public abstract void add(Camera camera, int ai[]);

	public abstract void setColor(int i);

	public abstract AWTDisplayList getDisplayList();

	public abstract void switchList();

	public abstract void fillInstances();

	public abstract boolean hasBeenPainted();

	public abstract void observePainting();

	public abstract Object[] getPaintResults();

	public abstract void setBounds(int i, int j, int k, int l);

	public abstract void setSamples(int i);

	public abstract void dispose();

	public abstract Object getLock();

	public abstract void repaint();

	public abstract void enableRenderTarget();

	public abstract void disableRenderTarget();

	public abstract boolean hasRenderTarget();
}
