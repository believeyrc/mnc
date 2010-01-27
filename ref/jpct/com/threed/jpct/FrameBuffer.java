// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.threed.jpct;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.io.Serializable;
import java.util.Vector;

import com.threed.jpct.threading.WorkLoad;
import com.threed.jpct.threading.Worker;

// Referenced classes of package com.threed.jpct:
//            VersionHelper, ISomeImageBuffer, IRenderer, IPostProcessor,
//            AWTGLRenderer, BlittingWrapper, LegacyRenderer, SoftGLRenderer,
//            VisListManager, Logger, Config, TextureManager,
//            Texture, IntegerC, ProxyFrameBuffer, Plane,
//            VideoMode, IPaintListener

public class FrameBuffer implements Serializable {
	private static final class Drawer implements WorkLoad {

		public void set(Graphics g1, int i, int j) {
			g = g1;
			yoffset = i;
			xoffset = j;
		}

		public void doWork() {
			g.drawImage(image, xoffset, yoffset + ys, width + xoffset, ye + yoffset, 0, ys, width, ye, null);
		}

		public void done() {
		}

		public void error(Exception exception) {
		}

		private Image image;
		private int ys;
		private int ye;
		private int width;
		private Graphics g;
		private int yoffset;
		private int xoffset;

		public Drawer(Image image1, int i, int j, int k) {
			image = null;
			ys = 0;
			ye = 0;
			width = 0;
			g = null;
			yoffset = 0;
			xoffset = 0;
			image = image1;
			ys = i;
			ye = j;
			width = k;
		}
	}

	private class ClearyLoad implements WorkLoad {

		public void setMode(boolean flag) {
			z = flag;
		}

		public void setSection(int i) {
			div = i;
		}

		public void doWork() {
			int ai[] = zbuffer;
			int ai1[] = pixels;
			int i = ai.length / clearLoads.length;
			int j = i * div;
			int k = j + i;
			if (z) {
				if (!zOnly) {
					for (int l = j; l < k; l++) {
						ai[l] = 0x80000001;
						ai1[l] = color;
					}

				} else {
					for (int i1 = j; i1 < k; i1++)
						ai[i1] = 0x80000001;

				}
			} else {
				oversample(div);
			}
		}

		public void done() {
		}

		public void error(Exception exception) {
		}

		public void setColor(int i, boolean flag) {
			color = i;
			zOnly = flag;
		}

		private boolean z;
		private int div;
		private int color;
		private boolean zOnly;

		private ClearyLoad() {
			z = false;
			div = 0;
			color = 0;
			zOnly = false;
		}

	}

	public FrameBuffer(int i, int j, int k) {
		this(i, j, k, null);
	}

	FrameBuffer(int i, int j, int k, Texture texture) {
		frameCount = 0L;
		emulateOpenGL = false;
		blittingTarget = 0;
		softRend = null;
		glRend = null;
		proxy = null;
		hasRenderTarget = false;
		renderTarget = null;
		displayCycle = 0L;
		canvasMode = false;
		sliStart = null;
		sliTimes = null;
		source = null;
		sourceOut = null;
		bufferType = 0;
		fillingMode = 0;
		fogLookUp = null;
		fogR = -1F;
		fogG = -1F;
		fogB = -1F;
		fogDistance = -1F;
		currentFoggingState = false;
		usedBy = new Vector(2);
		postProcessors = new Vector(1);
		id = null;
		ignoreInit = false;
		versionHelper = null;
		blitWrap = null;
		defaultLock = new Object();
		clearLoads = null;
		singleThreaded = false;
		drawers = null;
		id = new Long(sid);
		sid++;
		boolean flag = texture != null;
		if (flag) {
			k = 0;
			singleThreaded = true;
		}
		initialized = true;
		bbXlL = 0x5f5e0ff;
		bbXrL = 0;
		bbYuL = 0;
		bbYoL = 0x5f5e0ff;
		useBb = true;
		oversampling = k != 0;
		samplingmode = 0;
		int l = i;
		int i1 = j;
		owidth = i;
		oheight = j;
		bbXl = 0;
		bbXr = i;
		bbYu = j;
		bbYo = 0;
		if (k == 1) {
			i <<= 1;
			j <<= 1;
			samplingmode = 1;
		}
		if (k == 2) {
			i >>= 1;
			j >>= 1;
			samplingmode = 2;
		}
		if (k == 3) {
			i = (int) ((float) i * 1.5F);
			j = (int) ((float) j * 1.5F);
			samplingmode = 3;
		}
		if (k >= 20) {
			oversampling = false;
			samplingmode = k;
		}
		length = i * j;
		width = i;
		height = j;
		middleX = (float) width / 2.0F;
		middleY = (float) height / 2.0F;
		zbuffer = new int[length];
		xstart = new int[height];
		xend = new int[height];
		zhigh = new float[height];
		exXstart = new int[height];
		exXend = new int[height];
		exZlow = new float[height];
		exXstart2 = new int[height];
		exXend2 = new int[height];
		exZlow2 = new float[height];
		String s = System.getProperty("java.version");
		if (!flag)
			Logger.log("Java version is: " + s, 2);
		if (s.startsWith("1.1") || s.startsWith("1.0")) {
			if (!flag)
				Logger.log("-> no support for BufferedImage", 2);
			useBufferedImage = false;
			try {
				Class class1 = Class.forName("com.threed.jpct.VersionHelper11");
				versionHelper = (VersionHelper) class1.newInstance();
			} catch (Exception exception) {
				Logger.log(exception.toString(), 0);
			}
		} else {
			if (!flag)
				Logger.log("-> support for BufferedImage", 2);
			useBufferedImage = !Config.neverUseBufferedImage;
			boolean flag1 = false;
			try {
				Float float1 = new Float(s.substring(0, 3));
				if ((double) float1.floatValue() >= 1.5D)
					flag1 = true;
			} catch (Exception exception1) {
			}
			if (flag1)
				try {
					Class class4 = Class.forName("com.threed.jpct.VersionHelper5");
					versionHelper = (VersionHelper) class4.newInstance();
				} catch (Exception exception2) {
					Logger.log(exception2.toString(), 0);
				}
			else
				try {
					Class class5 = Class.forName("com.threed.jpct.VersionHelper2");
					versionHelper = (VersionHelper) class5.newInstance();
				} catch (Exception exception3) {
					Logger.log(exception3.toString(), 0);
				}
		}
		if (!useBufferedImage) {
			if (!flag)
				Logger.log("-> using MemoryImageSource", 2);
		} else if (!flag)
			Logger.log("-> using BufferedImage", 2);
		if (!useBufferedImage) {
			Object obj = null;
			ISomeImageBuffer isomeimagebuffer = null;
			try {
				Class class2 = Class.forName("com.threed.jpct.MemoryImageBuffer");
				isomeimagebuffer = (ISomeImageBuffer) class2.newInstance();
			} catch (Exception exception4) {
				Logger.log(exception4.toString(), 0);
				initialized = false;
			}
			if (initialized) {
				if (oversampling) {
					pixels = new int[length];
					isomeimagebuffer.create(l, i1);
					sourceOut = isomeimagebuffer.getSource();
					pixelsOut = isomeimagebuffer.getPixels();
				} else {
					isomeimagebuffer.create(i, j);
					source = isomeimagebuffer.getSource();
					pixels = isomeimagebuffer.getPixels();
				}
				bufferType = isomeimagebuffer.getType();
				output = isomeimagebuffer.getImage();
			}
		} else {
			Object obj1 = null;
			ISomeImageBuffer isomeimagebuffer1 = null;
			try {
				Class class3 = Class.forName("com.threed.jpct.BufferedImageBuffer");
				isomeimagebuffer1 = (ISomeImageBuffer) class3.newInstance();
			} catch (Exception exception5) {
				Logger.log(exception5.toString(), 0);
				initialized = false;
			}
			if (initialized) {
				if (!oversampling) {
					isomeimagebuffer1.create(i, j);
					pixels = isomeimagebuffer1.getPixels();
				} else {
					isomeimagebuffer1.create(l, i1);
					pixelsOut = isomeimagebuffer1.getPixels();
					pixels = new int[length];
				}
				output = isomeimagebuffer1.getImage();
				bufferType = isomeimagebuffer1.getType();
			}
		}
		if (!flag) {
			ignoreInit = true;
			enableRenderer(1, 1);
			ignoreInit = false;
		}
	}

	public Long getID() {
		return id;
	}

	public static VideoMode[] getVideoModes(int paramInt) {
		switch (paramInt) {
		case 2:
		case 1:
			try {
				IRenderer localIRenderer1 = null;
				if (!(useJOGL))
					localIRenderer1 = (IRenderer) Class.forName("com.threed.jpct.GLRenderer").newInstance();
				else
					localIRenderer1 = (IRenderer) Class.forName("com.threed.jpct.JOGLRenderer").newInstance();
				return localIRenderer1.getAvailableVideoModes();
			} catch (Exception localException1) {
				Logger.log("Error getting video modes for OpenGL: " + localException1.toString(), 0);
				try {
					String str = "com.threed.jpct.SoftGLRenderer";
					IRenderer localIRenderer2 = (IRenderer) Class.forName(str).newInstance();
					return localIRenderer2.getAvailableVideoModes();
				} catch (Exception localException2) {
					Logger.log("Error getting video modes for software: " + localException2.toString(), 0);
				}
			}
		}
		return null;
	}

	public boolean isInitialized() {
		boolean flag = initialized;
		if (softRend != null)
			flag &= softRend.isInitialized();
		if (glRend != null)
			flag &= glRend.isInitialized();
		return flag;
	}

	public void setBlittingTarget(int i) {
		blittingTarget = i;
	}

	public void enableRenderer(int i) {
		enableRenderer(i, 1);
	}

	public Canvas enableGLCanvasRenderer() {
		enableRenderer(2, 1, true);
		Object aobj[] = new Object[1];
		glRend.execute(11, aobj);
		return (Canvas) aobj[0];
	}

	public void enableRenderer(int i, int j) {
		enableRenderer(i, j, false);
	}

	public void setRenderTarget(int i) {
		if (i == -1)
			setRenderTarget(((Texture) (null)));
		else
			setRenderTarget(TextureManager.getInstance().getTextureByID(i));
	}

	public void setRenderTarget(int i, int j, int k, int l, int i1, boolean flag) {
		if (i == -1)
			setRenderTarget(((Texture) (null)));
		else
			setRenderTarget(TextureManager.getInstance().getTextureByID(i), j, k, l, i1, flag);
	}

	public void setRenderTarget(Texture texture) {
		setRenderTarget(texture, -1, -1, -1, -1, true);
	}

	public void setRenderTarget(Texture texture, int i, int j, int k, int l, boolean flag) {
		if (!Config.glUseFBO && texture != null
				&& (texture.getWidth() > getOutputWidth() || texture.getHeight() > getOutputHeight())) {
			Logger.log("Can't render into a texture larger than the current framebuffer!", 0);
			return;
		}
		if (glRend != null) {
			if (texture != null && texture.mipmap)
				texture.setMipmap(false);
			Object aobj[] = new Object[7];
			aobj[0] = texture;
			aobj[1] = this;
			aobj[2] = IntegerC.valueOf(i);
			aobj[3] = IntegerC.valueOf(j);
			aobj[4] = IntegerC.valueOf(k);
			aobj[5] = IntegerC.valueOf(l);
			aobj[6] = Config.booleanValueOf(flag);
			glRend.execute(18, aobj);
		}
		if (softRend != null)
			if (texture != null) {
				if (texture.mipmap)
					texture.setMipmap(false);
				proxy = ProxyFrameBuffer.getInstance(texture, this);
			} else {
				proxy = null;
			}
		if (texture == null)
			hasRenderTarget = false;
		else
			hasRenderTarget = true;
		renderTarget = texture;
	}

	public void removeRenderTarget() {
		if (hasRenderTarget)
			setRenderTarget(((Texture) (null)));
	}

	public void addPostProcessor(IPostProcessor ipostprocessor) {
		if (ipostprocessor.isInitialized())
			Logger.log("Post processor has already been initialized!", 0);
		else
			postProcessors.addElement(ipostprocessor);
	}

	public void removePostProcessor(IPostProcessor ipostprocessor) {
		postProcessors.removeElement(ipostprocessor);
		Object aobj[] = new Object[2];
		aobj[0] = this;
		aobj[1] = ipostprocessor;
		if (glRend != null)
			glRend.execute(17, aobj);
		if (softRend != null)
			ipostprocessor.dispose();
	}

	public void removeAllPostProcessors() {
		for (int i = 0; i < postProcessors.size(); i++)
			removePostProcessor((IPostProcessor) postProcessors.elementAt(i));

	}

	public void runPostProcessors() {
		if (postProcessors == null)
			postProcessors = new Vector(1);
		boolean flag = false;
		if (postProcessors.size() > 0) {
			Object aobj[] = new Object[2];
			aobj[0] = this;
			for (int i = 0; i < postProcessors.size(); i++) {
				IPostProcessor ipostprocessor = (IPostProcessor) postProcessors.elementAt(i);
				aobj[1] = ipostprocessor;
				if (glRend != null)
					glRend.execute(16, aobj);
				if (softRend == null)
					continue;
				if (proxy == null) {
					if (!ipostprocessor.isInitialized())
						ipostprocessor.init(this);
					ipostprocessor.process();
					flag = true;
				} else {
					Logger.log("Can't run a post processor on a software renderer's render target!", 1);
				}
			}

		}
		if (flag)
			refresh();
	}

	public void setPaintListener(IPaintListener ipaintlistener) {
		if (glRend != null)
			glRend.setPaintListener(ipaintlistener);
		if (softRend != null)
			softRend.setPaintListener(ipaintlistener);
	}

	public void setPaintListenerState(boolean flag) {
		Object aobj[] = { Config.booleanValueOf(flag) };
		if (glRend != null)
			glRend.execute(24, aobj);
		if (softRend != null)
			softRend.execute(24, aobj);
	}

	private void enableRenderer(int paramInt1, int paramInt2, boolean paramBoolean) {
		switch (paramInt1) {
		case 1:
			this.emulateOpenGL = (paramInt2 == 1);
			if (this.softRend != null)
				this.softRend.dispose();
			this.drawers = null;
			this.clearLoads = null;
			this.softRend = null;
			try {
				String str1 = "com.threed.jpct.LegacyRenderer";
				if (this.emulateOpenGL)
					str1 = "com.threed.jpct.SoftGLRenderer";
				this.softRend = ((IRenderer) Class.forName(str1).newInstance());
				if ((!(this.oversampling)) && (this.samplingmode >= 20) && (!(this.ignoreInit)))
					Logger.log("Can't use a software renderer on a framebuffer with a hardware sampling mode", 0);
				this.softRend.init(this.owidth, this.oheight, 24, 32, 0);
			} catch (Exception localException1) {
				Logger.log(localException1.toString(), 0);
			}
			removeAllPostProcessors();
			break;
		case 2:
			if (paramInt2 == 2)
				Logger.log("Legacy mode for the OpenGL renderer is no longer supported!", 0);
			if (this.glRend != null)
				this.glRend.dispose();
			this.glRend = null;
			try {
				String str2 = null;
				if (paramBoolean) {
					if (useJOGL) {
						str2 = "com.threed.jpct.JOGLRenderer";
						Logger.log("Using JOGL's GLCanvas", 2);
					} else {
						str2 = "com.threed.jpct.AWTGLRenderer";
						Logger.log("Using LWJGL's AWTGLCanvas", 2);
					}
					this.canvasMode = true;
				} else {
					if (useJOGL) {
						Logger
								.log(
										"Using a native GL renderer isn't possible when using JOGL! Remove glfacade.jar from the classpath!",
										0);
						return;
					}
					if (Config.useMultipleThreads)
						str2 = "com.threed.jpct.AWTGLRenderer";
					else
						str2 = "com.threed.jpct.GLRenderer";
					this.canvasMode = false;
				}
				this.glRend = ((IRenderer) Class.forName(str2).newInstance());
				if ((Config.useMultipleThreads) && (!(paramBoolean)))
					this.glRend.execute(14, new Object[] { Boolean.FALSE });
				if ((this.oversampling) && (this.samplingmode != 0))
					Logger.log("Can't use a hardware renderer on a framebuffer with a software sampling mode", 0);
				this.glRend.init(this.owidth, this.oheight, Config.glColorDepth, Config.glZBufferDepth, this.samplingmode / 10);
			} catch (Exception localException2) {
				Logger.log(localException2.toString(), 0);
			}
			removeAllPostProcessors();
		}
	}

	public void disableRenderer(int i) {
		switch (i) {
		default:
			break;

		case 1: // '\001'
			if (softRend != null) {
				clearLoads = null;
				drawers = null;
				if (blitWrap != null)
					blitWrap.dispose();
				blitWrap = null;
				softRend.dispose();
				softRend = null;
				removeAllPostProcessors();
			}
			break;

		case 2: // '\002'
			if (glRend != null) {
				glRend.dispose();
				glRend = null;
				removeAllPostProcessors();
			}
			break;
		}
	}

	public void dispose() {
		checkListeners();
		removeListeners();
		removeAllPostProcessors();
		if (blitWrap != null)
			blitWrap.dispose();
		if (softRend != null)
			disableRenderer(1);
		clearLoads = null;
		blitWrap = null;
		drawers = null;
	}

	public boolean usesRenderer(int i) {
		switch (i) {
		case 1: // '\001'
			return softRend != null;

		case 2: // '\002'
			return glRend != null;
		}
		return false;
	}

	public int getMaxTextureSize() {
		if (glRend != null) {
			Object aobj[] = new Object[1];
			aobj[0] = "dummy";
			glRend.execute(25, aobj);
			return ((Integer) aobj[0]).intValue();
		} else {
			return 8192;
		}
	}

	public boolean supports(String s) {
		if (s.equals("GL_ARB_texture_env_combine")) {
			boolean flag = true;
			if (softRend != null)
				flag = emulateOpenGL;
			if (glRend != null) {
				Object aobj[] = new Object[1];
				aobj[0] = s;
				glRend.execute(10, aobj);
				flag &= ((Boolean) aobj[0]).booleanValue();
			}
			return flag;
		}
		if (s.equals("GL_ARB_shadow")) {
			boolean flag1 = true;
			if (softRend != null)
				return false;
			if (glRend != null) {
				Object aobj1[] = new Object[1];
				aobj1[0] = s;
				glRend.execute(10, aobj1);
				flag1 = ((Boolean) aobj1[0]).booleanValue();
			}
			return flag1;
		} else {
			return false;
		}
	}

	public int getSamplingMode() {
		return samplingmode;
	}

	public int getOutputWidth() {
		return owidth;
	}

	public int getOutputHeight() {
		return oheight;
	}

	public float getMiddleX() {
		return middleX;
	}

	public float getMiddleY() {
		return middleY;
	}

	public void setBoundingBoxMode(boolean flag) {
		useBb = flag;
	}

	public void clear() {
		if (glRend != null && softRend == null)
			clear(null);
		else
			clear(Color.black);
	}

	public void clearZBufferOnly() {
		if (glRend != null)
			glRend.execute(15, null);
		if (proxy != null)
			proxy.clearZBufferOnly();
		else if (softRend != null)
			clearSoftware(Color.black, true);
	}

	public void clear(Color color) {
		if (proxy != null)
			proxy.clear(color);
		else if (softRend != null)
			clearSoftware(color);
		if (glRend != null)
			clearHardware(color);
	}

	public void update() {
		incCounter();
		if (proxy != null)
			proxy.update();
		else if (softRend != null)
			updateSoftware();
	}

	public void setBufferAccess(int i) {
		fillingMode = i;
	}

	public void optimizeBufferAccess() {
		fillingMode = 0;
		int i = 0;
		int j = 0;
		clearSoftware(Color.black);
		clearSoftware(Color.black);
		clearSoftware(Color.black);
		for (long l = System.currentTimeMillis(); System.currentTimeMillis() - l < 500L;) {
			clearSoftware(Color.black);
			i++;
		}

		fillingMode = 1;
		clearSoftware(Color.black);
		clearSoftware(Color.black);
		clearSoftware(Color.black);
		for (long l1 = System.currentTimeMillis(); System.currentTimeMillis() - l1 < 500L;) {
			clearSoftware(Color.black);
			j++;
		}

		j = (int) ((float) j + (float) j / 40F);
		if (i > j) {
			fillingMode = 0;
			Logger.log(i + "-" + j + " -> using combined buffer access!", 2);
		} else {
			fillingMode = 1;
			Logger.log(i + "-" + j + " -> using splitted buffer access!", 2);
		}
	}

	public Image getOutputBuffer() {
		if (softRend != null)
			return output;
		if (glRend != null) {
			getPixels();
			if (bufferType == 0)
				if (oversampling)
					sourceOut.newPixels(0, 0, owidth, oheight, false);
				else
					source.newPixels(0, 0, width, height, false);
			return output;
		} else {
			return null;
		}
	}

	public int[] getPixels() {
		if (softRend != null)
			if (!oversampling)
				return pixels;
			else
				return pixelsOut;
		Object aobj[] = new Object[2];
		aobj[0] = this;
		if (!oversampling)
			aobj[1] = pixels;
		else
			aobj[1] = pixelsOut;
		glRend.execute(6, aobj);
		return (int[]) aobj[1];
	}

	public void refresh() {
		if (softRend != null && !useBufferedImage)
			if (!oversampling)
				source.newPixels(0, 0, width, height, false);
			else
				sourceOut.newPixels(0, 0, owidth, oheight, false);
	}

	public Object getLock() {
		if (glRend == null || !(glRend instanceof AWTGLRenderer)) {
			return defaultLock;
		} else {
			Object obj = ((AWTGLRenderer) glRend).getLock();
			return obj == null ? defaultLock : obj;
		}
	}

	public Graphics getGraphics() {
		Graphics g = null;
		try {
			g = output.getGraphics();
		} catch (Exception exception) {
		}
		return g;
	}

	public int getType() {
		return bufferType;
	}

	public void blit(Texture texture, int i, int j, int k, int l, int i1, int j1, boolean flag) {
		if (glRend != null) {
			Object aobj[] = new Object[9];
			aobj[0] = texture;
			aobj[1] = this;
			aobj[2] = IntegerC.valueOf(i);
			aobj[3] = IntegerC.valueOf(j);
			aobj[4] = IntegerC.valueOf(k);
			aobj[5] = IntegerC.valueOf(l);
			aobj[6] = IntegerC.valueOf(i1);
			aobj[7] = IntegerC.valueOf(j1);
			aobj[8] = Config.booleanValueOf(flag);
			glRend.execute(4, aobj);
		}
		if (softRend != null)
			blit(texture.texels, texture.width, texture.height, i, j, k, l, i1, j1, flag);
	}

	public void blit(Texture texture, int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, boolean flag,
			Color color) {
		if (glRend != null) {
			Object aobj[] = new Object[18];
			aobj[0] = texture;
			aobj[1] = this;
			aobj[2] = IntegerC.valueOf(i);
			aobj[3] = IntegerC.valueOf(j);
			aobj[4] = IntegerC.valueOf(k);
			aobj[5] = IntegerC.valueOf(l);
			aobj[6] = IntegerC.valueOf(i1);
			aobj[7] = IntegerC.valueOf(j1);
			aobj[8] = Config.booleanValueOf(i2 > -1);
			aobj[11] = IntegerC.valueOf(k1);
			aobj[12] = IntegerC.valueOf(l1);
			aobj[13] = IntegerC.valueOf(i2);
			aobj[14] = Config.booleanValueOf(flag);
			if (color != null) {
				aobj[15] = IntegerC.valueOf(color.getRed());
				aobj[16] = IntegerC.valueOf(color.getGreen());
				aobj[17] = IntegerC.valueOf(color.getBlue());
			} else {
				Integer integer = IntegerC.valueOf(255);
				aobj[15] = integer;
				aobj[16] = integer;
				aobj[17] = integer;
			}
			glRend.execute(4, aobj);
		}
		if (softRend != null) {
			if (blitWrap == null)
				blitWrap = new BlittingWrapper(this);
			blitWrap.blit(texture, i, j, k, l, i1, j1, k1, l1, i2, flag, color);
		}
	}

	public void blit(int ai[], int i, int j, int k, int l, int i1, int j1, int k1, int l1, boolean flag) {
		if (k >= 0 && k < i && l >= 0 && l < j && k + k1 <= i && l + l1 <= j) {
			if (proxy != null)
				proxy.blit(ai, i, j, k, l, i1, j1, k1, l1, flag);
			else if (softRend != null)
				blitSoftware(ai, i, k, l, i1, j1, k1, l1, flag);
			if (glRend != null) {
				Object aobj[] = new Object[11];
				aobj[0] = ai;
				aobj[1] = this;
				aobj[2] = IntegerC.valueOf(k);
				aobj[3] = IntegerC.valueOf(l);
				aobj[4] = IntegerC.valueOf(i1);
				aobj[5] = IntegerC.valueOf(j1);
				aobj[6] = IntegerC.valueOf(k1);
				aobj[7] = IntegerC.valueOf(l1);
				aobj[8] = Config.booleanValueOf(flag);
				aobj[9] = IntegerC.valueOf(i);
				aobj[10] = IntegerC.valueOf(j);
				glRend.execute(7, aobj);
			}
		} else {
			Logger.log("Blitting region out of bounds", 0);
		}
	}

	public void display(Graphics g) {
		display(g, 0);
	}

	public void display(Graphics g, int i) {
		display(g, 0, i);
	}

	public void display(Graphics g, int i, int j) {
		incCounter();
		if (proxy != null)
			proxy.copy();
		else if (softRend != null)
			if (!Config.useMultipleThreads || !Config.useMultiThreadedBlitting || (softRend instanceof LegacyRenderer)) {
				g.drawImage(output, i, j, null);
			} else {
				if (drawers == null) {
					drawers = new Drawer[Config.maxNumberOfCores];
					int k = 0;
					int l = oheight / Config.maxNumberOfCores;
					boolean flag = false;
					for (int k1 = 0; k1 < drawers.length; k1++) {
						int j1 = k + l;
						if (k1 == drawers.length - 1)
							j1 = oheight;
						Drawer drawer2 = new Drawer(output, k, j1, owidth);
						k += l;
						drawers[k1] = drawer2;
					}

				}
				Worker worker = ((SoftGLRenderer) softRend).initWorker();
				for (int i1 = 0; i1 < drawers.length - 1; i1++) {
					Drawer drawer1 = drawers[i1];
					drawer1.set(g, j, i);
					worker.add(drawer1);
				}

				Drawer drawer = drawers[drawers.length - 1];
				drawer.set(g, j, i);
				drawer.doWork();
				worker.waitForAll();
			}
		if (glRend != null)
			glRend.execute(1, null);
	}

	private void incCounter() {
		if (!hasRenderTarget || !(glRend instanceof AWTGLRenderer))
			displayCycle++;
	}

	public void displayGLOnly() {
		if (softRend == null && glRend != null)
			display(null, 0);
		else
			Logger.log("displayGLOnly() shouldn't be called without OpenGL support being (solely) used", 1);
	}

	public void setClippingPlane(int i, Plane plane) {
		if (glRend != null && i < 6) {
			Object aobj[] = new Object[2];
			aobj[0] = new Integer(i);
			aobj[1] = plane.getPlaneEquation();
			glRend.execute(26, aobj);
		}
	}

	public void removeClippingPlane(int i) {
		Object aobj[] = new Object[1];
		aobj[0] = new Integer(i);
		glRend.execute(27, aobj);
	}

	final void register(VisListManager vislistmanager) {
		checkListeners();
		if (!usedBy.contains(vislistmanager))
			usedBy.addElement(vislistmanager);
	}

	final void expandBoundingBox() {
		bbXl = 0;
		bbXr = owidth;
		bbYu = oheight;
		bbYo = 0;
	}

	final void drawLine(float f, float f1, float f2, float f3, int i) {
		float f4 = 0.0F;
		float f7 = f2 - f;
		float f8 = f3 - f1;
		float f9 = 1.0F;
		float f10 = 1.0F;
		if (f7 != 0.0F)
			f10 = f8 / f7;
		if (f8 != 0.0F)
			f9 = f7 / f8;
		if (f2 < 0.0F && f < 0.0F || f >= (float) width && f2 >= (float) width || f3 < 0.0F && f1 < 0.0F
				|| f3 >= (float) height && f1 >= (float) height)
			return;
		if (Math.abs(f9) > Math.abs(f10)) {
			if (f > f2) {
				float f5 = f;
				f = f2;
				f2 = f5;
				f5 = f1;
				f1 = f3;
				f3 = f5;
			}
			if (f < 0.0F) {
				f1 += f10 * -f;
				f = 0.0F;
			}
			if (f2 >= (float) width) {
				f3 += f10 * (f2 - (float) (width + 1));
				f2 = width - 1;
			}
			int j = (int) (f1 * 262144F);
			int l = (int) (f10 * 262144F);
			int j1 = (int) f;
			int l1 = (int) f2;
			int j2 = height << 18;
			for (int l2 = j1; l2 <= l1; l2++) {
				if (j >= 0 && j < j2)
					pixels[(j >> 18) * width + l2] = i;
				j += l;
			}

		} else {
			if (f1 > f3) {
				float f6 = f;
				f = f2;
				f2 = f6;
				f6 = f1;
				f1 = f3;
				f3 = f6;
			}
			if (f1 < 0.0F) {
				f += f9 * -f1;
				f1 = 0.0F;
			}
			if (f3 >= (float) height) {
				f2 += f9 * (f3 - (float) (height + 1));
				f3 = height - 1;
			}
			int k = (int) (f * 262144F);
			int i1 = (int) (f9 * 262144F);
			int k1 = (int) f1;
			int i2 = (int) f3;
			k1 *= width;
			i2 *= width;
			int k2 = width << 18;
			for (int i3 = k1; i3 <= i2; i3 += width) {
				if (k >= 0 && k < k2)
					pixels[i3 + (k >> 18)] = i;
				k += i1;
			}

		}
	}

	private final void updateSoftware() {
		if (useBb && !Config.blur) {
			bbXl--;
			bbXr++;
			bbYu++;
			bbYo--;
			if (bbXl < bbXlL)
				bbXlD = bbXl;
			else
				bbXlD = bbXlL;
			if (bbXr > bbXrL)
				bbXrD = bbXr;
			else
				bbXrD = bbXrL;
			if (bbYo < bbYoL)
				bbYoD = bbYo;
			else
				bbYoD = bbYoL;
			if (bbYu > bbYuL)
				bbYuD = bbYu;
			else
				bbYuD = bbYuL;
			bbXlL = bbXl;
			bbXrL = bbXr;
			bbYuL = bbYu;
			bbYoL = bbYo;
			bbXl = 0x5f5e0ff;
			bbXr = 0;
			bbYo = 0x5f5e0ff;
			bbYu = 0;
		}
		if (Config.blur) {
			bbXlD = 0;
			bbXrD = width - 1;
			bbYoD = 0;
			bbYuD = height - 1;
		}
		if (oversampling) {
			if (currentFoggingState)
				applyPerPixelFog(fogR, fogG, fogB);
			if (!Config.useMultipleThreads) {
				oversample(-1);
			} else {
				Worker worker = ((SoftGLRenderer) softRend).initWorker();
				if (clearLoads == null) {
					clearLoads = new ClearyLoad[Config.maxNumberOfCores];
					for (int i = 0; i < clearLoads.length; i++)
						clearLoads[i] = new ClearyLoad();

				}
				for (int j = 0; j < clearLoads.length - 1; j++) {
					ClearyLoad clearyload1 = clearLoads[j];
					clearyload1.setMode(false);
					clearyload1.setSection(j);
					worker.add(clearyload1);
				}

				ClearyLoad clearyload = clearLoads[Config.maxNumberOfCores - 1];
				clearyload.setSection(Config.maxNumberOfCores - 1);
				clearyload.setMode(false);
				clearyload.doWork();
				worker.waitForAll();
			}
		} else {
			if (currentFoggingState)
				applyPerPixelFog(fogR, fogG, fogB);
			if (!useBufferedImage)
				if (!useBb) {
					source.newPixels(0, 0, width, height, false);
				} else {
					bbXd = (bbXrD - bbXlD) + 1;
					bbYd = (bbYuD - bbYoD) + 1;
					source.newPixels(bbXlD, bbYoD, bbXd, bbYd, false);
				}
		}
	}

	private final void oversample(int i) {
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = length;
		int j1 = owidth * oheight;
		int k1 = i1;
		int l1 = j1;
		if (i >= 0) {
			float f = (float) oheight / (float) clearLoads.length;
			float f1 = 0.0F;
			float f2 = 0.0F;
			int j3 = (int) (f * (float) i);
			int i4 = (int) (f * (float) (i + 1));
			j3 &= 0xffffffe;
			i4 &= 0xffffffe;
			if (samplingmode == 1) {
				f1 = j3 * 2;
				f2 = i4 * 2;
			}
			if (samplingmode == 3) {
				f1 = (float) j3 * 1.5F;
				f2 = (float) i4 * 1.5F;
			}
			if (samplingmode == 2) {
				f1 = j3 / 2;
				f2 = i4 / 2;
			}
			k = j3 * owidth;
			j1 = i4 * owidth;
			j = (int) (f1 * (float) width);
			i1 = (int) (f2 * (float) width);
			if (j1 > l1)
				j1 = l1;
			if (i1 > k1)
				i1 = k1;
			l = j << 8;
		}
		int i2 = 0;
		try {
			if (samplingmode == 1)
				do {
					if (j >= i1)
						break;
					int j4 = pixels[j];
					int i5 = pixels[j + 1];
					int k5 = pixels[j + width];
					int i6 = pixels[j + width + 1];
					int k6 = j4 & 0xff000000 | i5 & 0xff000000 | k5 & 0xff000000 | i6 & 0xff000000;
					int j2 = ((j4 >> 16 & 0xff) + (i5 >> 16 & 0xff) + (k5 >> 16 & 0xff) + (i6 >> 16 & 0xff) >> 2) << 16;
					int l2 = ((j4 >> 8 & 0xff) + (i5 >> 8 & 0xff) + (k5 >> 8 & 0xff) + (i6 >> 8 & 0xff) >> 2) << 8;
					int k3 = (j4 & 0xff) + (i5 & 0xff) + (k5 & 0xff) + (i6 & 0xff) >> 2;
					pixelsOut[k] = j2 | l2 | k3 | k6;
					k++;
					j += 2;
					if ((i2 += 2) >= width) {
						i2 = 0;
						j += width;
					}
				} while (true);
			else if (samplingmode == 2) {
				boolean flag = (owidth & 1) == 1;
				do {
					if (j >= i1)
						break;
					int k4 = pixels[j];
					pixelsOut[k] = k4;
					pixelsOut[k + 1] = k4;
					pixelsOut[k + 1 + owidth] = k4;
					pixelsOut[k + owidth] = k4;
					k += 2;
					j++;
					if (++i2 >= width) {
						i2 = 0;
						k += owidth;
						if (flag)
							k++;
					}
				} while (true);
			} else {
				int l6 = j1;
				int i7 = 0;
				int j7 = width << 8;
				boolean flag1 = (owidth & 1) == 1;
				while (l + j7 + 256 >> 8 < i1 && k < l6) {
					int l4 = pixels[j];
					int j5 = pixels[l + 256 >> 8];
					int l5 = pixels[l + j7 >> 8];
					int j6 = pixels[l + j7 + 256 >> 8];
					int k7 = l4 & 0xff000000 | j5 & 0xff000000 | l5 & 0xff000000 | j6 & 0xff000000;
					int k2 = ((l4 >> 16 & 0xff) + (j5 >> 16 & 0xff) + (l5 >> 16 & 0xff) + (j6 >> 16 & 0xff) >> 2) << 16;
					int i3 = ((l4 >> 8 & 0xff) + (j5 >> 8 & 0xff) + (l5 >> 8 & 0xff) + (j6 >> 8 & 0xff) >> 2) << 8;
					int l3 = (l4 & 0xff) + (j5 & 0xff) + (l5 & 0xff) + (j6 & 0xff) >> 2;
					pixelsOut[k] = k2 | i3 | l3 | k7;
					k++;
					l += 384;
					if (++i2 >= owidth) {
						i2 = 0;
						if ((i7 & 1) == 1)
							l += j7;
						if (flag1)
							l -= 128;
						i7++;
					}
					j = l >> 8;
				}
			}
		} catch (Exception exception) {
		}
		if (!useBufferedImage)
			if (!useBb)
				sourceOut.newPixels(0, 0, owidth, oheight, false);
			else if (samplingmode == 1) {
				bbXd = (bbXrD - bbXlD) + 3;
				bbYd = (bbYuD - bbYoD) + 3;
				sourceOut.newPixels(bbXlD >> 1, bbYoD >> 1, bbXd >> 1, bbYd >> 1, false);
			} else if (samplingmode == 2) {
				bbXd = (bbXrD - bbXlD) + 1;
				bbYd = (bbYuD - bbYoD) + 1;
				sourceOut.newPixels(bbXlD << 1, bbYoD << 1, bbXd << 1, bbYd << 1, false);
			} else {
				bbXd = (bbXrD - bbXlD) + 3;
				bbYd = (bbYuD - bbYoD) + 3;
				sourceOut.newPixels((int) ((float) bbXlD / 1.5F), (int) ((float) bbYoD / 1.5F), (int) ((float) bbXd / 1.5F),
						(int) ((float) bbYd / 1.5F), false);
			}
	}

	private final void clearHardware(Color color) {
		Object aobj[] = null;
		if (color != null) {
			aobj = new Object[1];
			aobj[0] = color;
		}
		glRend.execute(2, aobj);
	}

	private final void clearSoftware(Color color) {
		clearSoftware(color, false);
	}

	private final void clearSoftware(Color color, boolean flag) {
		int i = color.getRed();
		int j = color.getGreen();
		int k = color.getBlue();
		int l = 255;
		l = versionHelper.getAlpha(color);
		frameCount++;
		int i1 = length;
		for (int j1 = 0; j1 < height; j1++) {
			xstart[j1] = 0x5f5e0ff;
			xend[j1] = -1;
			zhigh[j1] = -2.147484E+009F;
			exXstart[j1] = 0x5f5e0ff;
			exXend[j1] = -1;
			exZlow[j1] = 2.147484E+009F;
			exXstart2[j1] = 0x5f5e0ff;
			exXend2[j1] = -1;
			exZlow2[j1] = 2.147484E+009F;
		}

		if (Config.blur) {
			blur();
		} else {
			int k1 = l << 24 | i << 16 | j << 8 | k;
			if (!Config.isIndoor && !flag) {
				int ai[] = zbuffer;
				int ai2[] = pixels;
				if (fillingMode == 1 && !Config.useMultipleThreads) {
					for (int k2 = 0; k2 < i1; k2++)
						ai[k2] = 0x80000001;

					for (int l2 = 0; l2 < i1; l2++)
						ai2[l2] = k1;

				} else if (!Config.useMultipleThreads || singleThreaded || (softRend instanceof LegacyRenderer)) {
					for (int i3 = 0; i3 < i1; i3++) {
						ai[i3] = 0x80000001;
						ai2[i3] = k1;
					}

				} else {
					Worker worker1 = ((SoftGLRenderer) softRend).initWorker();
					if (clearLoads == null) {
						clearLoads = new ClearyLoad[Config.maxNumberOfCores];
						for (int j3 = 0; j3 < clearLoads.length; j3++)
							clearLoads[j3] = new ClearyLoad();

					}
					for (int k3 = 0; k3 < clearLoads.length - 1; k3++) {
						ClearyLoad clearyload3 = clearLoads[k3];
						clearyload3.setMode(true);
						clearyload3.setSection(k3);
						clearyload3.setColor(k1, false);
						worker1.add(clearyload3);
					}

					ClearyLoad clearyload2 = clearLoads[Config.maxNumberOfCores - 1];
					clearyload2.setSection(Config.maxNumberOfCores - 1);
					clearyload2.setMode(true);
					clearyload2.setColor(k1, false);
					clearyload2.doWork();
					worker1.waitForAll();
				}
			} else if (!Config.zTrick)
				if (!Config.useMultipleThreads || singleThreaded || (softRend instanceof LegacyRenderer)) {
					int ai1[] = zbuffer;
					for (int l1 = 0; l1 < i1; l1++)
						ai1[l1] = 0x80000001;

				} else {
					Worker worker = ((SoftGLRenderer) softRend).initWorker();
					if (clearLoads == null) {
						clearLoads = new ClearyLoad[Config.maxNumberOfCores];
						for (int i2 = 0; i2 < clearLoads.length; i2++)
							clearLoads[i2] = new ClearyLoad();

					}
					for (int j2 = 0; j2 < clearLoads.length - 1; j2++) {
						ClearyLoad clearyload1 = clearLoads[j2];
						clearyload1.setMode(true);
						clearyload1.setSection(j2);
						clearyload1.setColor(-1, true);
						worker.add(clearyload1);
					}

					ClearyLoad clearyload = clearLoads[Config.maxNumberOfCores - 1];
					clearyload.setSection(Config.maxNumberOfCores - 1);
					clearyload.setMode(true);
					clearyload.setColor(-1, true);
					clearyload.doWork();
					worker.waitForAll();
				}
		}
	}

	final void useFogging(float f, float f1, float f2, float f3) {
		if (fogLookUp == null)
			fogLookUp = new int[32768];
		if (f != fogDistance) {
			int i = (int) ((1.0F / f) * 2.147484E+009F);
			for (int j = 1; j < 32768; j++)
				fogLookUp[j] = i / j;

		}
		fogR = f1;
		fogG = f2;
		fogB = f3;
		fogDistance = f;
		currentFoggingState = true;
	}

	final void dontUseFogging() {
		fogR = -1F;
		fogG = -1F;
		fogB = -1F;
		fogDistance = -1F;
		currentFoggingState = false;
	}

	private final void applyPerPixelFog(final float rr, final float gg, final float bb) {
		int i = 0;
		int j = length;
		if (!Config.useMultipleThreads || (softRend instanceof LegacyRenderer)) {
			applyPerPixelFog(rr, gg, bb, i, j);
		} else {
			Worker worker = ((SoftGLRenderer) softRend).initWorker();
			final int d = length / Config.maxNumberOfCores;
			for (int k = 0; k < Config.maxNumberOfCores - 1; k++) {
				final int ii = k;
				worker.add(new WorkLoad() {

					public void doWork() {
						applyPerPixelFog(rr, gg, bb, s, e);
					}

					public void done() {
					}

					public void error(Exception exception) {
					}

					final int s;
					final int e;

					{
						s = ii * d;
						e = s + d;
					}
				});
			}

			applyPerPixelFog(rr, gg, bb, (Config.maxNumberOfCores - 1) * d, length);
			worker.waitForAll();
		}
	}

	private final void applyPerPixelFog(float f, float f1, float f2, int i, int j) {
		int ai[] = pixels;
		int ai1[] = zbuffer;
		int k = (int) f;
		int l = (int) f1;
		int i1 = (int) f2;
		if (k < 0)
			k = 0;
		else if (k > 255)
			k = 255;
		if (l < 0)
			l = 0;
		else if (l > 255)
			l = 255;
		if (i1 < 0)
			i1 = 0;
		else if (i1 > 255)
			i1 = 255;
		if (!Config.zTrick) {
			for (int j1 = i; j1 < j; j1++) {
				int k1 = ai1[j1];
				if (k1 != 0x80000001) {
					int i2 = fogLookUp[(k1 >> 17) + 16384];
					if (i2 > 0x10000)
						i2 = 0x10000;
					int k2 = ai[j1] & 0xffffff;
					int i3 = 0x10000 - i2;
					int k3 = k * i2 + i3 * (k2 >> 16) >> 16;
					int i4 = l * i2 + i3 * (k2 >> 8 & 0xff) >> 16;
					int k4 = i1 * i2 + i3 * (k2 & 0xff) >> 16;
					ai[j1] = k3 << 16 | i4 << 8 | k4 | 0xff000000;
				}
			}

		} else {
			byte byte0 = 1;
			if ((frameCount & 1L) == 1L)
				byte0 = -1;
			for (int l1 = i; l1 < j; l1++) {
				int j2 = ai1[l1] * byte0;
				if (j2 < 0)
					j2 = 0;
				int l2 = fogLookUp[j2 >> 16];
				if (l2 > 0x10000)
					l2 = 0x10000;
				int j3 = ai[l1] & 0xffffff;
				int l3 = 0x10000 - l2;
				int j4 = k * l2 + l3 * (j3 >> 16) >> 16;
				int l4 = l * l2 + l3 * (j3 >> 8 & 0xff) >> 16;
				int i5 = i1 * l2 + l3 * (j3 & 0xff) >> 16;
				ai[l1] = j4 << 16 | l4 << 8 | i5 | 0xff000000;
			}

		}
	}

	private void blitSoftware(int ai[], int i, int j, int k, int l, int i1, int j1, int k1, boolean flag) {
		int ai1[] = pixels;
		int l1 = width;
		int i2 = height;
		if (oversampling && blittingTarget == 0) {
			ai1 = pixelsOut;
			l1 = owidth;
			i2 = oheight;
		}
		if (l < 0) {
			j += -l;
			j1 -= -l;
			l = 0;
		}
		if (i1 < 0) {
			k += -i1;
			k1 -= -i1;
			i1 = 0;
		}
		if (l + j1 > l1)
			j1 -= (l + j1) - l1;
		if (i1 + k1 > i2)
			k1 -= (i1 + k1) - i2;
		if (l < l1 && i1 < i2 && j1 > 0 && k1 > 0) {
			int j2 = l + i1 * l1;
			int k2 = j + k * i;
			if (!flag) {
				for (int l2 = 0; l2 < k1; l2++) {
					for (int j3 = 0; j3 < j1; j3++)
						ai1[j2 + j3] = ai[k2 + j3] | 0xff000000;

					j2 += l1;
					k2 += i;
				}

			} else {
				for (int i3 = 0; i3 < k1; i3++) {
					for (int k3 = 0; k3 < j1; k3++) {
						int l3 = ai[k2 + k3];
						if ((l3 & 0xf0f0f0) != 0)
							ai1[k3 + j2] = l3 | 0xff000000;
					}

					j2 += l1;
					k2 += i;
				}

			}
			if (!useBufferedImage)
				if (!oversampling)
					source.newPixels(l, i1, j1, k1, false);
				else if (blittingTarget != 1)
					sourceOut.newPixels(l, i1, j1, k1, false);
		}
	}

	private final void blur() {
		for (int i = 0; i < length; i++) {
			int j = pixels[i];
			int k = j & 0xff0000;
			int l = j & 0xff00;
			int i1 = j & 0xff;
			k = k + (k << 1) >> 2;
			l = l + (l << 1) >> 2;
			i1 = i1 + (i1 << 1) >> 2;
			k &= 0xff0000;
			l &= 0xff00;
			i1 &= 0xff;
			pixels[i] = k | l | i1 | 0xff000000;
			zbuffer[i] = 0x80000001;
		}

	}

	private void checkListeners() {
		if (usedBy == null)
			usedBy = new Vector(2);
		Vector vector = null;
		for (int i = 0; i < usedBy.size(); i++) {
			VisListManager vislistmanager = (VisListManager) usedBy.get(i);
			if (!vislistmanager.isDisposed)
				continue;
			if (vector == null)
				vector = new Vector();
			vector.addElement(vislistmanager);
		}

		if (vector != null) {
			for (int j = 0; j < vector.size(); j++)
				usedBy.removeElement(vector.get(j));

		}
	}

	private void removeListeners() {
		try {
			for (int i = 0; i < usedBy.size(); i++) {
				VisListManager vislistmanager = (VisListManager) usedBy.elementAt(i);
				vislistmanager.remove(this);
			}

		} catch (Exception exception) {
			Logger.log("Couldn't unregister visibility list!", 1);
		}
	}

	protected void finalize() {
		checkListeners();
		removeListeners();
	}

	private static boolean useJOGL = false;
	private static final long serialVersionUID = 4L;
	public static final int MEMORYIMAGESOURCE = 0;
	public static final int BUFFEREDIMAGE = 1;
	public static final int SAMPLINGMODE_GL_AA_4X = 40;
	public static final int SAMPLINGMODE_GL_AA_2X = 20;
	public static final int SAMPLINGMODE_OGSS = 1;
	public static final int SAMPLINGMODE_OGSS_FAST = 3;
	public static final int SAMPLINGMODE_OGUS = 2;
	public static final int SAMPLINGMODE_NORMAL = 0;
	public static final int SAMPLINGMODE_HARDWARE_ONLY = 0;
	public static final boolean BOUNDINGBOX_USED = true;
	public static final boolean BOUNDINGBOX_NOT_USED = false;
	public static final boolean OPAQUE_BLITTING = false;
	public static final boolean TRANSPARENT_BLITTING = true;
	public static final String SUPPORT_FOR_RGB_SCALING = "GL_ARB_texture_env_combine";
	public static final String SUPPORT_FOR_SHADOW_MAPPING = "GL_ARB_shadow";
	public static final int BUFFER_ACCESS_COMBINED = 0;
	public static final int BUFFER_ACCESS_SPLITTED = 1;
	public static final int BLITTING_TARGET_FRONT = 0;
	public static final int BLITTING_TARGET_BACK = 1;
	public static final Object SYNCHRONIZER = new Object();
	private static final int ALPHA = 0xff000000;
	private static final int SUB_ALPHA = 0xffffff;
	long frameCount;
	boolean emulateOpenGL;
	float middleX;
	float middleY;
	boolean useBb;
	int bbXl;
	int bbXr;
	int bbYo;
	int bbYu;
	int bbXlL;
	int bbXrL;
	int bbYoL;
	int bbYuL;
	int bbXlD;
	int bbXrD;
	int bbYoD;
	int bbYuD;
	int bbXd;
	int bbYd;
	int pixels[];
	int width;
	int height;
	int xstart[];
	int xend[];
	int exXstart[];
	int exXend[];
	int exXstart2[];
	int exXend2[];
	float zhigh[];
	float exZlow[];
	float exZlow2[];
	int blittingTarget;
	transient IRenderer softRend;
	transient IRenderer glRend;
	transient ProxyFrameBuffer proxy;
	transient boolean hasRenderTarget;
	transient Texture renderTarget;
	transient long displayCycle;
	boolean canvasMode;
	transient int sliStart[];
	transient long sliTimes[];
	protected int zbuffer[];
	private int owidth;
	private int oheight;
	private transient MemoryImageSource source;
	private transient MemoryImageSource sourceOut;
	private Image output;
	private int length;
	private int pixelsOut[];
	private boolean useBufferedImage;
	private boolean oversampling;
	private int samplingmode;
	private int bufferType;
	private boolean initialized;
	private int fillingMode;
	private int fogLookUp[];
	private float fogR;
	private float fogG;
	private float fogB;
	private float fogDistance;
	private boolean currentFoggingState;
	private transient Vector usedBy;
	private transient Vector postProcessors;
	private Long id;
	private static long sid = 0L;
	private boolean ignoreInit;
	VersionHelper versionHelper;
	private transient BlittingWrapper blitWrap;
	private Object defaultLock;
	private ClearyLoad clearLoads[];
	private boolean singleThreaded;
	private Drawer drawers[];

	static {
		try {
			Toolkit.getDefaultToolkit();
			Class.forName("org.lwjgl.opengl.JOGLPresent").newInstance();
			useJOGL = true;
		} catch (Throwable throwable) {
		}
	}

}
