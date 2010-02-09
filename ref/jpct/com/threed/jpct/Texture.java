// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.threed.jpct;

import java.awt.*;
import java.awt.image.*;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

// Referenced classes of package com.threed.jpct:
//            DummyCanvas, Logger, ITextureEffect, IntegerC,
//            Config, TextureManager, Projector

public class Texture implements Serializable {

	Texture() {
		isUnicolor = false;
		repeat = true;
		bilinear = true;
		mipmap = true;
		projector = null;
		projectorBuffer = null;
		enabled = true;
		isShadowMap = false;
		xDiv = 1.0F;
		yDiv = 1.0F;
		mipMaps = null;
		storeTexels = null;
		myEffect = null;
		usageCnt = 0L;
		isConverted = false;
		openGLID = 0;
		markerGL = 0;
		lastRenderer = -1;
		lastRendererMarker = -1;
		glIDs = new Hashtable();
		marker = new Hashtable();
		width = 256;
		height = 256;
		isBumpmap = true;
		isLoaded = true;
		int i = width * height + width + 1;
		texels = new int[i];
		xend = (float) width - 1E-014F;
		yend = (float) height - 1E-014F;
		shifter = 8;
		isConverted = false;
		resetIDs();
		for (int j = 0; j < i; j++)
			texels[j] = 0xffffff;

	}

	public Texture(String s) {
		isUnicolor = false;
		repeat = true;
		bilinear = true;
		mipmap = true;
		projector = null;
		projectorBuffer = null;
		enabled = true;
		isShadowMap = false;
		xDiv = 1.0F;
		yDiv = 1.0F;
		mipMaps = null;
		storeTexels = null;
		myEffect = null;
		usageCnt = 0L;
		isConverted = false;
		openGLID = 0;
		markerGL = 0;
		lastRenderer = -1;
		lastRendererMarker = -1;
		glIDs = new Hashtable();
		marker = new Hashtable();
		loadTexture(null, s, null, null);
		isBumpmap = false;
		isConverted = false;
	}

	public Texture(String s, boolean flag) {
		isUnicolor = false;
		repeat = true;
		bilinear = true;
		mipmap = true;
		projector = null;
		projectorBuffer = null;
		enabled = true;
		isShadowMap = false;
		xDiv = 1.0F;
		yDiv = 1.0F;
		mipMaps = null;
		storeTexels = null;
		myEffect = null;
		usageCnt = 0L;
		isConverted = false;
		openGLID = 0;
		markerGL = 0;
		lastRenderer = -1;
		lastRendererMarker = -1;
		glIDs = new Hashtable();
		marker = new Hashtable();
		loadTexture(null, s, null, null, flag);
		isBumpmap = false;
		isConverted = false;
	}

	public Texture(URL url, String s) {
		isUnicolor = false;
		repeat = true;
		bilinear = true;
		mipmap = true;
		projector = null;
		projectorBuffer = null;
		enabled = true;
		isShadowMap = false;
		xDiv = 1.0F;
		yDiv = 1.0F;
		mipMaps = null;
		storeTexels = null;
		myEffect = null;
		usageCnt = 0L;
		isConverted = false;
		openGLID = 0;
		markerGL = 0;
		lastRenderer = -1;
		lastRendererMarker = -1;
		glIDs = new Hashtable();
		marker = new Hashtable();
		loadTexture(url, s, null, null);
		isBumpmap = false;
		isConverted = false;
	}

	public Texture(URL url, String s, boolean flag) {
		isUnicolor = false;
		repeat = true;
		bilinear = true;
		mipmap = true;
		projector = null;
		projectorBuffer = null;
		enabled = true;
		isShadowMap = false;
		xDiv = 1.0F;
		yDiv = 1.0F;
		mipMaps = null;
		storeTexels = null;
		myEffect = null;
		usageCnt = 0L;
		isConverted = false;
		openGLID = 0;
		markerGL = 0;
		lastRenderer = -1;
		lastRendererMarker = -1;
		glIDs = new Hashtable();
		marker = new Hashtable();
		loadTexture(url, s, null, null, flag);
		isBumpmap = false;
		isConverted = false;
	}

	public Texture(InputStream inputstream) {
		isUnicolor = false;
		repeat = true;
		bilinear = true;
		mipmap = true;
		projector = null;
		projectorBuffer = null;
		enabled = true;
		isShadowMap = false;
		xDiv = 1.0F;
		yDiv = 1.0F;
		mipMaps = null;
		storeTexels = null;
		myEffect = null;
		usageCnt = 0L;
		isConverted = false;
		openGLID = 0;
		markerGL = 0;
		lastRenderer = -1;
		lastRendererMarker = -1;
		glIDs = new Hashtable();
		marker = new Hashtable();
		loadTexture(null, null, inputstream, null);
		isBumpmap = false;
		isConverted = false;
	}

	public Texture(InputStream inputstream, boolean flag) {
		isUnicolor = false;
		repeat = true;
		bilinear = true;
		mipmap = true;
		projector = null;
		projectorBuffer = null;
		enabled = true;
		isShadowMap = false;
		xDiv = 1.0F;
		yDiv = 1.0F;
		mipMaps = null;
		storeTexels = null;
		myEffect = null;
		usageCnt = 0L;
		isConverted = false;
		openGLID = 0;
		markerGL = 0;
		lastRenderer = -1;
		lastRendererMarker = -1;
		glIDs = new Hashtable();
		marker = new Hashtable();
		loadTexture(null, null, inputstream, null, flag);
		isBumpmap = false;
		isConverted = false;
	}

	public Texture(Image image) {
		isUnicolor = false;
		repeat = true;
		bilinear = true;
		mipmap = true;
		projector = null;
		projectorBuffer = null;
		enabled = true;
		isShadowMap = false;
		xDiv = 1.0F;
		yDiv = 1.0F;
		mipMaps = null;
		storeTexels = null;
		myEffect = null;
		usageCnt = 0L;
		isConverted = false;
		openGLID = 0;
		markerGL = 0;
		lastRenderer = -1;
		lastRendererMarker = -1;
		glIDs = new Hashtable();
		marker = new Hashtable();
		loadTexture(null, "from Image", null, image);
		isBumpmap = false;
		isConverted = false;
	}

	public Texture(Image image, boolean flag) {
		isUnicolor = false;
		repeat = true;
		bilinear = true;
		mipmap = true;
		projector = null;
		projectorBuffer = null;
		enabled = true;
		isShadowMap = false;
		xDiv = 1.0F;
		yDiv = 1.0F;
		mipMaps = null;
		storeTexels = null;
		myEffect = null;
		usageCnt = 0L;
		isConverted = false;
		openGLID = 0;
		markerGL = 0;
		lastRenderer = -1;
		lastRendererMarker = -1;
		glIDs = new Hashtable();
		marker = new Hashtable();
		loadTexture(null, "from Image", null, image, flag);
		isBumpmap = false;
		isConverted = false;
	}

	public Texture(int i, int j) {
		this(i, j, Color.black);
	}

	public Texture(int i, int j, Color color) {
		this(createIntArray(i, j, color), adjustSize(i), adjustSize(j), false);
	}

	private static int[] createIntArray(int i, int j, Color color) {
		if (color == null)
			return null;
		int k = adjustSize(i);
		int l = adjustSize(j);
		int ai[] = new int[k * l];
		int i1 = color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
		if (i1 != 0) {
			for (int j1 = 0; j1 < ai.length; j1++)
				ai[j1] = i1;

		}
		return ai;
	}

	public void add(Texture texture, float f) {
		if (texture.getArraySize() != getArraySize()) {
			Logger.log("Texture sizes don't match", 0);
			return;
		}
		int ai[] = texture.texels;
		for (int i = 0; i < ai.length; i++) {
			int j = ai[i];
			int k = texels[i];
			int l = k >> 24;
			int i1 = (k & 0xff0000) >> 16;
			int j1 = (k & 0xff00) >> 8;
			int k1 = k & 0xff;
			int l1 = j >> 24;
			int i2 = (j & 0xff0000) >> 16;
			int j2 = (j & 0xff00) >> 8;
			int k2 = j & 0xff;
			l = clip(l + (int) ((float) l1 * f));
			i1 = clip(i1 + (int) ((float) i2 * f));
			j1 = clip(j1 + (int) ((float) j2 * f));
			k1 = clip(k1 + (int) ((float) k2 * f));
			texels[i] = l << 24 | i1 << 16 | j1 << 8 | k1;
		}

		setMarker(MARKER_DELETE_AND_UPLOAD);
	}

	public void setProjector(Projector projector1, boolean flag) {
		projector = projector1;
		if (repeat) {
			enableGLClamping();
			setMipmap(false);
		}
		if (flag)
			setMarker(MARKER_DELETE_AND_UPLOAD);
	}

	Texture(int ai[], int i, int j, boolean flag) {
		isUnicolor = false;
		repeat = true;
		bilinear = true;
		mipmap = true;
		projector = null;
		projectorBuffer = null;
		enabled = true;
		isShadowMap = false;
		xDiv = 1.0F;
		yDiv = 1.0F;
		mipMaps = null;
		storeTexels = null;
		myEffect = null;
		usageCnt = 0L;
		isConverted = false;
		openGLID = 0;
		markerGL = 0;
		lastRenderer = -1;
		lastRendererMarker = -1;
		glIDs = new Hashtable();
		marker = new Hashtable();
		if (flag) {
			if (i <= 2048 && j <= 2048) {
				width = adjustSize(i);
				height = adjustSize(j);
				texels = new int[width * height];
				refill(ai, i, j);
			} else {
				Logger.log("Unsupported bitmap size for OpenGL blitting!", 0);
			}
		} else {
			width = i;
			height = j;
			texels = ai;
		}
		isLoaded = true;
		xend = (float) width - 1E-014F;
		yend = (float) height - 1E-014F;
		resetIDs();
	}

	public void setAsShadowMap(boolean flag) {
		isShadowMap = flag;
		setMarker(MARKER_DELETE_AND_UPLOAD);
	}

	public boolean isShadowMap() {
		return isShadowMap;
	}

	public void setEffect(ITextureEffect itextureeffect) {
		if (storeTexels == null) {
			storeTexels = new int[texels.length];
			System.arraycopy(texels, 0, storeTexels, 0, texels.length);
		}
		myEffect = itextureeffect;
		myEffect.init(this);
	}

	public void removeEffect() {
		myEffect = null;
		storeTexels = null;
	}

	public void applyEffect() {
		if (myEffect != null) {
			myEffect.apply(texels, storeTexels);
			if (myEffect.containsAlpha()) {
				int i = texels.length;
				if (alpha == null)
					alpha = new int[i];
				for (int j = 0; j < i; j++) {
					alpha[j] = texels[j] & 0xff000000;
					texels[j] &= 0xffffff;
				}

			}
			setMarker(MARKER_DELETE_AND_UPLOAD);
		} else {
			Logger.log("The texture doesn't have an effect assigned to it!", 0);
		}
	}

	public void setEnabled(boolean flag) {
		enabled = flag;
	}

	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @deprecated Method getByteSize is deprecated
	 */

	public int getByteSize() {
		return getArraySize();
	}

	public int getArraySize() {
		if (texels == null)
			return 0;
		else
			return texels.length;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public long getLastFrame() {
		return usageCnt;
	}

	public void enableGLClamping() {
		repeat = false;
		setMarker(MARKER_DELETE_AND_UPLOAD);
	}

	public void setGLFiltering(boolean flag) {
		bilinear = flag;
		setMarker(MARKER_DELETE_AND_UPLOAD);
	}

	/**
	 * @deprecated Method setGLMipmap is deprecated
	 */

	public void setGLMipmap(boolean flag) {
		setMipmap(flag);
	}

	public void setMipmap(boolean flag) {
		mipmap = flag;
		if (!flag)
			mipMaps = null;
		setMarker(MARKER_DELETE_AND_UPLOAD);
	}

	public void removeAlpha() {
		alpha = new int[texels.length];
		for (int i = 0; i < alpha.length; i++) {
			alpha[i] = 0xff000000;
			texels[i] &= 0xffffff;
		}

		setMarker(MARKER_DELETE_AND_UPLOAD);
	}

	public void setAlpha(int i) {
		alpha = new int[texels.length];
		for (int j = 0; j < alpha.length; j++) {
			alpha[j] = i;
			texels[j] &= 0xffffff;
		}

		setMarker(MARKER_DELETE_AND_UPLOAD);
	}

	final int getOpenGLID(int i) {
		if (isConverted) {
			if (i == lastRenderer)
				return openGLID;
			Integer integer = (Integer) glIDs.get(IntegerC.valueOf(i));
			if (integer != null) {
				openGLID = integer.intValue();
				lastRenderer = i;
				return openGLID;
			}
		}
		return 0;
	}

	final void clearIDs(int i) {
		openGLID = 0;
		markerGL = -999;
		lastRenderer = -1;
		lastRendererMarker = -1;
		glIDs.remove(IntegerC.valueOf(i));
		marker.remove(IntegerC.valueOf(i));
	}

	final void setOpenGLID(int i, int j) {
		openGLID = j;
		if (j != 0) {
			lastRenderer = i;
			glIDs.put(IntegerC.valueOf(i), IntegerC.valueOf(j));
			isConverted = true;
			if (Config.glAvoidTextureCopies && myEffect == null) {
				texels = null;
				alpha = null;
			}
		} else {
			resetIDs();
		}
	}

	final int getMarker(int i) {
		if (markerGL == -999 || lastRendererMarker != i) {
			lastRendererMarker = i;
			if (marker.containsKey(IntegerC.valueOf(i)))
				markerGL = MARKER_NOTHING;
			else
				markerGL = MARKER_DELETE_AND_UPLOAD;
		}
		return markerGL;
	}

	private final void setMarker(int i) {
		setMarker(-1, i);
	}

	final void setMarker(int i, int j) {
		if (j == MARKER_NOTHING) {
			marker.put(IntegerC.valueOf(i), Config.booleanValueOf(true));
			markerGL = MARKER_NOTHING;
			lastRendererMarker = i;
		} else if (j == MARKER_DELETE_AND_UPLOAD) {
			marker.clear();
			mipMaps = null;
			markerGL = -999;
			lastRendererMarker = -1;
		}
	}

	Texture getMipMappedTexture(int paramInt) {
		if ((paramInt <= 0) || (!(this.mipmap)) || (!(Config.mipmap)))
			return this;
		if (this.mipMaps == null)
			synchronized (this) {
				if (this.mipMaps == null) {
					long l = System.currentTimeMillis();
					int i = this.width;
					int j = this.height;
					int k = i;
					if (j > i)
						k = j;
					if (k <= 16)
						return this;
					DirectColorModel localDirectColorModel = null;
					if (this.alpha == null)
						localDirectColorModel = new DirectColorModel(24, 16711680, 65280, 255);
					else
						localDirectColorModel = new DirectColorModel(32, 16711680, 65280, 255, -16777216);
					int[] arrayOfInt = new int[i * j];
					for (int i1 = 0; i1 < arrayOfInt.length; ++i1) {
						int i2 = this.texels[i1] & 0xFFFFFF;
						if (this.alpha != null)
							i2 |= this.alpha[i1] << 24;
						arrayOfInt[i1] = i2;
					}
					MemoryImageSource localMemoryImageSource = new MemoryImageSource(i, j, localDirectColorModel, arrayOfInt, 0,
							i);
					Image localImage1 = Toolkit.getDefaultToolkit().createImage(localMemoryImageSource);
					int i3 = i;
					int i4 = j;
					Vector localVector = new Vector();
					for (int i5 = 0; (i > 8) || (j > 8); ++i5) {
						i /= 2;
						j /= 2;
						if (i < 8)
							i = 8;
						if (j < 8)
							j = 8;
						Image localImage2 = localImage1.getScaledInstance(i, j, 4);
						Texture localTexture = new Texture(localImage2, this.alpha != null);
						localTexture.xDiv = (i3 / i);
						localTexture.yDiv = (i4 / j);
						localVector.addElement(localTexture);
					}
					this.mipMaps = new Texture[localVector.size()];
					for (int i5 = 0; i5 < this.mipMaps.length; ++i5)
						this.mipMaps[i5] = ((Texture) localVector.elementAt(i5));
					Logger.log("Mipmaps created in " + (System.currentTimeMillis() - l) + "ms", 2);
				}
			}
		paramInt = Math.min(this.mipMaps.length, paramInt) - 1;
		return this.mipMaps[paramInt];
	}

	final void updateUsage(long l) {
		usageCnt = l;
	}

	final void createBumpmap() {
		if (this != TextureManager.getInstance().getDummyTexture()) {
			if (isLoaded) {
				int i = intSize + width + 1;
				for (int j = 0; j < i; j++)
					texels[j] = texels[j] & 0xffff;

			} else {
				Logger.log("Tried to process a nonexistent texture!", 0);
			}
			isBumpmap = true;
		}
	}

	final void refill(int ai[], int i, int j) {
		for (int k = 0; k < j; k++) {
			int l = k * width;
			int i1 = i * k;
			for (int j1 = 0; j1 < i; j1++)
				texels[l + j1] = ai[i1 + j1];

		}

		isConverted = false;
	}

	private void loadTexture(URL url, String s, InputStream inputstream, Image image) {
		loadTexture(url, s, inputstream, image, false);
	}

	private void loadTexture(URL url, String s, InputStream inputstream, Image image, boolean flag) {
		isLoaded = false;
		if (s == null)
			s = "from InputStream";
		Logger.log("Loading Texture..." + s, 2);
		try {
			Image image1 = null;
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			if (image == null) {
				if (inputstream == null) {
					if (url != null)
						image1 = toolkit.getImage(new URL(url, s));
					else
						image1 = toolkit.getImage(s);
				} else {
					int i1 = 0x186a0;
					int j1 = 0;
					int l1 = 0;
					int j2 = i1;
					byte abyte0[] = new byte[i1];
					try {
						do {
							if (j1 == -1)
								break;
							j1 = inputstream.read(abyte0, l1, j2 - l1);
							l1 += j1;
							if (j1 != -1 && l1 >= j2) {
								j2 += 0x186a0;
								byte abyte1[] = new byte[j2];
								System.arraycopy(abyte0, 0, abyte1, 0, j2 - 0x186a0);
								abyte0 = abyte1;
								Logger.log("Expanding buffers..." + j2 + " bytes", 2);
							}
						} while (true);
						l1++;
						inputstream.close();
						image1 = toolkit.createImage(abyte0, 0, l1);
					} catch (Exception exception1) {
						Logger.log("Couldn't get data from InputStream: " + exception1.toString(), 0);
					}
				}
			} else {
				image1 = image;
			}
			Thread.yield();
			MediaTracker mediatracker = new MediaTracker(tracky);
			mediatracker.addImage(image1, 0);
			try {
				mediatracker.waitForAll();
			} catch (Exception exception) {
				Logger.log("Error loading texture: " + exception, 0);
			}
			if (image1 != null && image1.getWidth(null) > 0) {
				int k1 = image1.getHeight(null);
				int i2 = image1.getWidth(null);
				width = i2;
				height = k1;
				switch (width) {
				case 8192:
					shifter = 13;
					break;

				case 4096:
					shifter = 12;
					break;

				case 2048:
					shifter = 11;
					break;

				case 1024:
					shifter = 10;
					break;

				case 512:
					shifter = 9;
					break;

				case 256:
					shifter = 8;
					break;

				case 128:
					shifter = 7;
					break;

				case 64: // '@'
					shifter = 6;
					break;

				case 32: // ' '
					shifter = 5;
					break;

				case 16: // '\020'
					shifter = 4;
					break;

				case 8: // '\b'
					shifter = 3;
					break;

				default:
					Logger.log("Unsupported Texture width (" + width + ")...resizing to a width of " + 256 + " pixels!", 1);
					if (k1 != 8 && k1 != 16 && k1 != 32 && k1 != 64 && k1 != 128 && k1 != 256 && k1 != 512 && k1 != 1024
							&& k1 != 2048 && k1 != 4096 && k1 != 8192) {
						k1 = 256;
						height = k1;
					}
					image1 = image1.getScaledInstance(256, k1, 4);
					i2 = 256;
					width = 256;
					shifter = (byte) (int) (Math.log(256D) / Math.log(2D));
					break;
				}
				if (k1 != 8 && k1 != 16 && k1 != 32 && k1 != 64 && k1 != 128 && k1 != 256 && k1 != 512 && k1 != 1024
						&& k1 != 2048 && k1 != 4096 && k1 != 8192) {
					Logger.log("Unsupported Texture height (" + height + ")...resizing to a height of " + 256 + " pixels!", 1);
					image1 = image1.getScaledInstance(i2, 256, 4);
					k1 = 256;
					height = k1;
				}
				xend = (float) i2 - 1E-014F;
				yend = (float) k1 - 1E-014F;
				intSize = width * height;
				texels = new int[intSize + width + 1];
				PixelGrabber pixelgrabber = new PixelGrabber(image1, 0, 0, i2, k1, texels, 0, i2);
				try {
					pixelgrabber.grabPixels();
				} catch (InterruptedException interruptedexception) {
					Logger.log("Could not grab pixels from image!", 0);
				}
				isLoaded = true;
			} else {
				Logger.log("File '" + (s == null ? "unknown" : s) + "' not found - replacement texture used instead!", 0);
				texels = new int[8];
				isLoaded = true;
				intSize = 4;
				width = 2;
				height = 2;
				xend = 2.0F;
				yend = 2.0F;
				shifter = 1;
			}
		} catch (MalformedURLException malformedurlexception) {
			Logger.log("File " + s + " not found!", 0);
		}
		if (isLoaded) {
			int i = intSize + width + 1;
			for (int j = intSize; j < i; j++)
				texels[j] = texels[j - intSize];

			if (!flag) {
				for (int k = 0; k < i; k++)
					texels[k] &= 0xffffff;

			} else {
				alpha = new int[texels.length];
				for (int l = 0; l < i; l++) {
					alpha[l] = texels[l] & 0xff000000;
					texels[l] &= 0xffffff;
				}

			}
		}
	}

	private static int adjustSize(int i) {
		for (int j = 0; j < TEXTURE_SIZES.length; j++)
			if (i <= TEXTURE_SIZES[j])
				return TEXTURE_SIZES[j];

		return i;
	}

	private void resetIDs() {
		openGLID = 0;
		markerGL = -999;
		lastRenderer = -1;
		lastRendererMarker = -1;
		glIDs.clear();
		marker.clear();
	}

	static Texture createSingleColoredTexture(Color color) {
		int ai[] = new int[256];
		int i = color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
		for (int j = 0; j < 256; j++)
			ai[j] = i;

		Texture texture = new Texture(ai, 16, 16, false);
		texture.isUnicolor = true;
		return texture;
	}

	private int clip(int i) {
		if (i < 0)
			i = 0;
		if (i > 255)
			i = 255;
		return i;
	}

	private static final long serialVersionUID = 1L;
	private static final int TEXTURE_SIZES[] = { 8, 16, 32, 64, 128, 256, 512, 1024, 2048 };
	private static final float SIZE_OFFSET = 1E-014F;
	private static final Component tracky = new DummyCanvas();
	public static final int DEFAULT_BPP = 24;
	public static final int DEFAULT_WIDTH = 256;
	public static final int DEFAULT_HEIGHT = 256;
	int width;
	int height;
	int texels[];
	int alpha[];
	int intSize;
	float xend;
	float yend;
	boolean isBumpmap;
	byte shifter;
	boolean isUnicolor;
	boolean repeat;
	boolean bilinear;
	boolean mipmap;
	Projector projector;
	Projector projectorBuffer;
	boolean enabled;
	boolean isShadowMap;
	float xDiv;
	float yDiv;
	Texture mipMaps[];
	private boolean isLoaded;
	private int storeTexels[];
	private ITextureEffect myEffect;
	private long usageCnt;
	private boolean isConverted;
	private int openGLID;
	private int markerGL;
	private int lastRenderer;
	private int lastRendererMarker;
	private Hashtable glIDs;
	private Hashtable marker;
	static int MARKER_NOTHING = 0;
	static int MARKER_DELETE_AND_UPLOAD = 1;

}
