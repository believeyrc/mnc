// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.awt.Color;
import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            VersionHelper, Logger, Object3D, IRenderer

class VersionHelper11 implements VersionHelper, Serializable {

	public VersionHelper11() {
		Logger.log("Version helper for 1.1 initialized!", 2);
	}

	public int getAlpha(Color color) {
		return 255;
	}

	public void compile(Object3D object3d, IRenderer irenderer) {
	}

	public long getTime() {
		return System.currentTimeMillis();
	}
}
