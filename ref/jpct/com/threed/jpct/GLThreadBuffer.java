// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.threed.jpct;

// Referenced classes of package com.threed.jpct:
//            AWTDisplayList, Camera, IThreadBuffer, BufferedMatrix,
//            Matrix, Logger, GLHelper, GLBase,
//            Config, AWTGLRenderer, VisList

class GLThreadBuffer extends Thread implements IThreadBuffer {

	GLThreadBuffer(AWTGLRenderer awtglrenderer) throws Exception {
		renderer = null;
		list = null;
		onceList = null;
		curList = 0;
		lock = new Object();
		paintObserver = false;
		result = null;
		xs = 0;
		ys = 0;
		xe = 0;
		ye = 0;
		samples = 0;
		hasToDispose = false;
		hasToRender = false;
		sleeping = false;
		init(awtglrenderer);
	}

	protected void init(AWTGLRenderer awtglrenderer) {
		renderer = awtglrenderer;
		list = new AWTDisplayList[2];
		list[0] = new AWTDisplayList();
		list[1] = new AWTDisplayList();
		onceList = new AWTDisplayList();
		setDaemon(true);
		setPriority(6);
		start();
	}

	public void setSamples(int i) {
		samples = i;
	}

	public Object getLock() {
		return lock;
	}

	public void setBounds(int i, int j, int k, int l) {
		xe = k;
		xs = i;
		ye = l;
		ys = j;
		hasToRender = true;
	}

	public void repaint() {
		synchronized (lock) {
			hasToRender = true;
			interrupt();
		}
	}

	public void dispose() {
		hasToDispose = true;
		interrupt();
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

	public final void add(VisList vislist, int i, int j, int k) {
		list[curList].add(vislist, i, j, k);
	}

	public final void setColor(int i) {
		list[curList].setColor(i);
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
			hasToRender = true;
			if (sleeping)
				interrupt();
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

	public void run() {
		Logger.log("Display thread initialized!", 2);
		try {
			int i = 0;
			while (i == 0) {
				if ((!(this.hasToRender)) && (!(this.hasToDispose)))
					try {
						this.sleeping = true;
						Thread.sleep(100L);
						this.sleeping = false;
					} catch (Exception localException2) {
					}
				if (this.hasToDispose) {
					GLHelper.dispose();
					i = 1;
					this.hasToDispose = false;
					this.hasToRender = false;
				}
				if ((!(this.hasToRender)) || (this.xe == 0))
					continue;
				if (!(this.renderer.isDisposed())) {
					if (!(this.renderer.isInitialized())) {
						boolean bool1 = GLHelper.init(this.xe - this.xs, this.ye - this.ys, Config.glColorDepth,
								Config.glZBufferDepth, this.samples);
						if (!(bool1)) {
							Logger.log("Error initializing display thread!", 0);
							return;
						}
						this.renderer.init();
					}
					this.renderer.execute(12, null);
					synchronized (this.lock) {
						this.hasToRender = false;
						int j = this.curList ^ 0x1;
						AWTDisplayList localAWTDisplayList = this.list[j];
						int k = localAWTDisplayList.count;
						int i1;
						for (int l = 0; l < k; ++l) {
							i1 = localAWTDisplayList.mode[l];
							switch (i1) {
							case 3:
								this.renderer.executeGL(localAWTDisplayList, l);
								break;
							case 2:
								this.renderer.drawVertexArray(localAWTDisplayList, l);
								break;
							case 1:
								this.renderer.drawStrip(localAWTDisplayList, l);
								break;
							case 0:
								this.renderer.drawPolygon(localAWTDisplayList, l);
								break;
							case 5:
								this.renderer.drawWireframe(localAWTDisplayList, l);
							case 4:
							}
						}
						boolean flag2 = paintObserver;
						if (onceList.count != 0) {
							boolean flag3 = false;
							for (int i2 = 0; i2 < onceList.count; i2++) {
								int j1 = onceList.mode[i2];
								if (j1 == 4 && !flag2) {
									result = renderer.executeGL(onceList, i2);
									onceList.delete(i2);
									flag3 = true;
								}
							}

							paintObserver = flag3;
						}
					}
				} else {
					i = 1;
				}
				this.renderer.execute(13, null);
				this.renderer.endState();
				GLHelper.swap();
			}
		} catch (Exception localException1) {
			throw new RuntimeException(localException1);
		}
		Logger.log("Display thread terminated!", 2);
	}

	private static final long serialVersionUID = 1L;
	private static final int PRIORITY = 6;
	private AWTGLRenderer renderer;
	private AWTDisplayList list[];
	private AWTDisplayList onceList;
	private int curList;
	private Object lock;
	private boolean paintObserver;
	private Object result[];
	private int xs;
	private int ys;
	private int xe;
	private int ye;
	private int samples;
	private boolean hasToDispose;
	private boolean hasToRender;
	private boolean sleeping;
}
