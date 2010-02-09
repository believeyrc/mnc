// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.awt.Component;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.PixelFormat;

// Referenced classes of package com.threed.jpct:
//            AWTDisplayList, Camera, IThreadBuffer, Logger, 
//            BufferedMatrix, Matrix, GLBase, AWTGLRenderer, 
//            VisList

class AWTJPCTCanvas extends AWTGLCanvas implements IThreadBuffer {

	AWTJPCTCanvas() throws Exception {
		renderer = null;
		list = null;
		onceList = null;
		curList = 0;
		lock = new Object();
		paintObserver = false;
		result = null;
	}

	AWTJPCTCanvas(AWTGLRenderer awtglrenderer) throws Exception {
		renderer = null;
		list = null;
		onceList = null;
		curList = 0;
		lock = new Object();
		paintObserver = false;
		result = null;
		init(awtglrenderer);
	}

	AWTJPCTCanvas(AWTGLRenderer awtglrenderer, PixelFormat pixelformat) throws Exception {
		super(pixelformat);
		renderer = null;
		list = null;
		onceList = null;
		curList = 0;
		lock = new Object();
		paintObserver = false;
		result = null;
		init(awtglrenderer);
	}

	public void setSamples(int i) {
	}

	protected void init(AWTGLRenderer awtglrenderer) {
		renderer = awtglrenderer;
		list = new AWTDisplayList[2];
		list[0] = new AWTDisplayList();
		list[1] = new AWTDisplayList();
		onceList = new AWTDisplayList();
	}

	public void exceptionOccurred(LWJGLException lwjglexception) {
		if (lwjglexception.getMessage().indexOf("No support for") != -1)
			try {
				Field field = (org.lwjgl.opengl.AWTGLCanvas.class).getDeclaredField("pixel_format");
				field.setAccessible(true);
				PixelFormat pixelformat = (PixelFormat) field.get(this);
				if (pixelformat == null || pixelformat.getSamples() != 0) {
					Logger.log("No support for multi sampling...trying to recover!", 1);
					field.set(this, new PixelFormat());
					Field field1 = (org.lwjgl.opengl.AWTGLCanvas.class).getDeclaredField("reentry_count");
					Field field2 = (org.lwjgl.opengl.AWTGLCanvas.class).getDeclaredField("peer_info");
					field1.setAccessible(true);
					field2.setAccessible(true);
					field2.set(this, ((Object) ((Object[]) null)));
				} else {
					Logger.log(lwjglexception.getMessage(), 0);
				}
			} catch (Exception exception) {
				Logger.log("Unable to recover: " + exception.getMessage(), 0);
			}
		else
			Logger.log(lwjglexception.getMessage(), 0);
	}

	public void dispose() {
		setEnabled(false);
	}

	public final void add(int i, Object obj) {
		list[curList].add(i, obj);
	}

	public void add(Camera camera, int ai[]) {
		Object aobj[] = new Object[2];
		Camera camera1 = new Camera();
		camera1.setBack(camera.getBack().cloneMatrix());
		camera1.setPosition(camera.getPosition());
		camera1.setFOVLimits(camera.getMinFOV(), camera.getMaxFOV());
		camera1.setFOV(camera.getFOV());
		aobj[0] = camera1;
		int ai1[] = new int[ai.length];
		for (int i = 0; i < ai1.length; i++)
			ai1[i] = ai[i];

		aobj[1] = ai1;
		add(21, ((Object) (aobj)));
	}

	public final void addOnce(int i, Object obj) {
		onceList.addOnce(i, obj);
	}

	public Object getLock() {
		return lock;
	}

	public final void add(VisList vislist, int i, int j, int k) {
		list[curList].add(vislist, i, j, k);
	}

	public void enableRenderTarget() {
		list[curList].enableRenderTarget();
	}

	public void disableRenderTarget() {
		list[curList].disableRenderTarget();
	}

	public boolean hasRenderTarget() {
		return list[curList].hasRenderTarget();
	}

	public final void setColor(int i) {
		list[curList].setColor(i);
	}

	public final AWTDisplayList getDisplayList() {
		return list[curList];
	}

	public final void fillInstances() {
		synchronized (lock) {
			list[curList].fillInstances();
		}
	}

	public final void switchList() {
		synchronized (lock) {
			list[curList].switchBuffers();
			curList ^= 1;
			list[curList].reset();
		}
	}

	public final boolean hasBeenPainted() {
		return paintObserver;
	}

	public final void observePainting() {
		paintObserver = false;
	}

	public final Object[] getPaintResults() {
		if (hasBeenPainted()) {
			Object aobj[] = result;
			result = null;
			return aobj;
		} else {
			return null;
		}
	}

	public void paintGL() {
		try {
			makeCurrent();
			if (!renderer.isDisposed()) {
				if (!renderer.isInitialized())
					renderer.init();
				renderer.execute(12, null);
				synchronized (lock) {
					int i = curList ^ 1;
					AWTDisplayList awtdisplaylist = list[i];
					int j = awtdisplaylist.count;
					for (int k = 0; k < j; k++) {
						int l = awtdisplaylist.mode[k];
						switch (l) {
						case 3: // '\003'
							renderer.executeGL(awtdisplaylist, k);
							break;

						case 2: // '\002'
							renderer.drawVertexArray(awtdisplaylist, k);
							break;

						case 1: // '\001'
							renderer.drawStrip(awtdisplaylist, k);
							break;

						case 0: // '\0'
							renderer.drawPolygon(awtdisplaylist, k);
							break;

						case 5: // '\005'
							renderer.drawWireframe(awtdisplaylist, k);
							break;
						}
					}

					boolean flag = paintObserver;
					if (onceList.count != 0) {
						boolean flag1 = false;
						for (int i1 = 0; i1 < onceList.count; i1++) {
							int j1 = onceList.mode[i1];
							if (j1 == 4 && !flag) {
								result = renderer.executeGL(onceList, i1);
								onceList.delete(i1);
								flag1 = true;
							}
						}

						paintObserver = flag1;
					}
				}
			}
			renderer.execute(13, null);
			swapBuffers();
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private static final long serialVersionUID = 1L;
	private AWTGLRenderer renderer;
	private AWTDisplayList list[];
	private AWTDisplayList onceList;
	private int curList;
	private Object lock;
	private boolean paintObserver;
	private Object result[];
}
