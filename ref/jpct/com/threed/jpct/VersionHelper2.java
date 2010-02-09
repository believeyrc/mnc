// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.awt.Color;
import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            Object3DCompiler, VersionHelper, Logger, Object3D, 
//            IRenderer

class VersionHelper2 implements VersionHelper, Serializable {

	public VersionHelper2() {
		compiler = null;
		Logger.log("Version helper for 1.2+ initialized!", 2);
	}

	public int getAlpha(Color color) {
		return color.getAlpha();
	}

	public void compile(Object3D object3d, IRenderer irenderer) {
		if (compiler == null)
			compiler = new Object3DCompiler();
		synchronized (compiler) {
			compiler.compile(object3d, irenderer);
			if (object3d.toStrip)
				object3d.reallyStrip();
		}
	}

	public long getTime() {
		return System.currentTimeMillis();
	}

	private Object3DCompiler compiler;
}
