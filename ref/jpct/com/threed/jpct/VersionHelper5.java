// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.threed.jpct;

import java.awt.Color;
import java.io.Serializable;
import java.lang.reflect.Method;

// Referenced classes of package com.threed.jpct:
//            Object3DCompiler, VersionHelper, Logger, Object3D,
//            IRenderer

class VersionHelper5 implements VersionHelper, Serializable {

	public VersionHelper5() {
		compiler = null;
		m = null;
		Logger.log("Version helper for 1.5+ initialized!", 2);
		try {
			m = (java.lang.System.class).getMethod("nanoTime", null);
		} catch (Exception exception) {
		}
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
		try {
			return (((Long) this.m.invoke(System.class, null)).longValue() / 1000000L);
		} catch (Exception localException) {
		}
		return System.currentTimeMillis();
	}

	private Object3DCompiler compiler;
	private Method m;
}
