// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.awt.Component;
import java.lang.reflect.Field;
import javax.media.opengl.*;

// Referenced classes of package com.threed.jpct:
//            AWTDisplayList, Camera, IThreadBuffer, BufferedMatrix, 
//            Matrix, GLBase, AWTGLRenderer, Logger, 
//            JOGLRenderer, VisList

class JOGLCanvas extends GLCanvas implements IThreadBuffer, GLEventListener {

	JOGLCanvas() throws Exception {
		renderer = null;
		list = null;
		onceList = null;
		curList = 0;
		lock = new Object();
		paintObserver = false;
		result = null;
	}

	JOGLCanvas(JOGLRenderer joglrenderer) throws Exception {
		renderer = null;
		list = null;
		onceList = null;
		curList = 0;
		lock = new Object();
		paintObserver = false;
		result = null;
		init(joglrenderer);
		addGLEventListener(this);
	}

	JOGLCanvas(JOGLRenderer joglrenderer, GLCapabilities glcapabilities) throws Exception {
		super(glcapabilities);
		renderer = null;
		list = null;
		onceList = null;
		curList = 0;
		lock = new Object();
		paintObserver = false;
		result = null;
		init(joglrenderer);
		addGLEventListener(this);
	}

	public void setSamples(int i) {
	}

	protected void init(JOGLRenderer joglrenderer) {
		renderer = joglrenderer;
		list = new AWTDisplayList[2];
		list[0] = new AWTDisplayList();
		list[1] = new AWTDisplayList();
		onceList = new AWTDisplayList();
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

	public void display(GLAutoDrawable glautodrawable) {
		try {
			if (!renderer.isDisposed()) {
				injectGL();
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
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public void displayChanged(GLAutoDrawable glautodrawable, boolean flag, boolean flag1) {
	}

	public void init(GLAutoDrawable glautodrawable) {
	}

	public void reshape(GLAutoDrawable glautodrawable, int i, int j, int k, int l) {
	}

	private void injectGL() {
		Class aclass[] = { org.lwjgl.opengl.GL11.class, org.lwjgl.opengl.ARBMultitexture.class,
				org.lwjgl.opengl.EXTFramebufferObject.class, org.lwjgl.opengl.ARBShaderObjects.class,
				org.lwjgl.opengl.GLContext.class };
		javax.media.opengl.GL gl = GLContext.getCurrent().getGL();
		for (int i = 0; i < aclass.length; i++) {
			Class class1 = aclass[i];
			try {
				Field field = class1.getDeclaredField("gl");
				field.set(class1, gl);
			} catch (Exception exception) {
				Logger.log("Unable to inject GL context into the facade!", 0);
			}
		}

	}

	private static final long serialVersionUID = 1L;
	private JOGLRenderer renderer;
	private AWTDisplayList list[];
	private AWTDisplayList onceList;
	private int curList;
	private Object lock;
	private boolean paintObserver;
	private Object result[];
}
