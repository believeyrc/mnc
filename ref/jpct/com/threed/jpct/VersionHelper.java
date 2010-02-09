// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.awt.Color;

// Referenced classes of package com.threed.jpct:
//            Object3D, IRenderer

interface VersionHelper {

	public abstract int getAlpha(Color color);

	public abstract void compile(Object3D object3d, IRenderer irenderer);

	public abstract long getTime();
}
