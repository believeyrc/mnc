// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

// Referenced classes of package com.threed.jpct:
//            FrameBuffer

public interface IPostProcessor {

	public abstract void init(FrameBuffer framebuffer);

	public abstract void process();

	public abstract void dispose();

	public abstract boolean isInitialized();
}
