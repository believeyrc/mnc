// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

// Referenced classes of package com.threed.jpct:
//            Texture

public interface ITextureEffect {

	public abstract void init(Texture texture);

	public abstract void apply(int ai[], int ai1[]);

	public abstract boolean containsAlpha();
}
