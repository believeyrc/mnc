// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

// Referenced classes of package com.threed.jpct:
//            VisList, FrameBuffer, World, VideoMode, 
//            IPaintListener

public interface IRenderer {

	public abstract void init(int i, int j, int k, int l, int i1);

	public abstract void dispose();

	public abstract void drawPolygon(VisList vislist, int i, FrameBuffer framebuffer, World world);

	public abstract void drawVertexArray(VisList vislist, int i, int j, FrameBuffer framebuffer, World world);

	public abstract void drawStrip(VisList vislist, int i, int j, FrameBuffer framebuffer, World world);

	public abstract void drawWireframe(VisList vislist, int i, int j, FrameBuffer framebuffer, World world);

	public abstract void execute(int i, Object aobj[]);

	public abstract VideoMode[] getAvailableVideoModes();

	public abstract boolean isInitialized();

	public abstract void setPaintListener(IPaintListener ipaintlistener);

	public static final int RENDERER_SOFTWARE = 1;
	public static final int RENDERER_OPENGL = 2;
	public static final int MODE_OPENGL = 1;
	public static final int MODE_LEGACY = 2;
}
