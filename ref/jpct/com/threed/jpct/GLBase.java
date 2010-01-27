// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.threed.jpct;

import java.awt.Color;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.Util;
import org.lwjgl.util.glu.GLU;

// Referenced classes of package com.threed.jpct:
//            Matrix, SimpleVector, VideoMode, ICompiledInstance,
//            IPostProcessor, FrameBuffer, Texture, Config,
//            TextureManager, IntegerC, Logger, Camera,
//            BufferedMatrix, GLHelper, CompiledInstance, World,
//            Lights, Interact2D, IPaintListener

abstract class GLBase {

	protected GLBase() {
		lastFOVMode = Config.autoMaintainAspectRatio;
		init = false;
		stateChanges = 0;
		myWorld = null;
		pixelBufferSize = 0;
		blitBufferWidth = 0;
		blitBufferHeight = 0;
		currentRGBScaling = 1;
		texMan = null;
		curPos = 0;
		colPos = 0;
		vertPos = 0;
		texPos = 0;
		mtTexPos = null;
		wasTransparent = false;
		lastTransMode = 0;
		vertexArraysInitialized = false;
		disposed = false;
		listener = null;
		listenerActive = true;
		stageInitialized = new boolean[4];
		lastTextures = new int[4];
		maxStages = 0;
		lastMultiTextures = new int[4];
		lastIDs = new int[4];
		lastMultiModes = new int[4];
		lastMode = new int[4];
		minDriverAndConfig = 0;
		lastCoords = 1;
		veryLastCoords = 1;
		lastFarPlane = -999F;
		lastNearPlane = -999F;
		colors = null;
		vertices = null;
		textures = null;
		clippingBuffer = null;
		multiTextures = new FloatBuffer[4];
		renderTarget = null;
		yTargetStart = 0;
		xViewStart = 0;
		yViewStart = 0;
		xViewEnd = 0;
		yViewEnd = 0;
		textureScale = new Matrix();
		projective = new boolean[4];
		alphaTest = false;
		useFBO = false;
		fbo = -1;
		fboTexture = -1;
		fboDepthMode = false;
		fboColorStorage = null;
		fboDepthStorage = null;
		blending = false;
		hasOpenGL12 = false;
		buffersEnabled = new boolean[4];
		enabledStages = new boolean[4];
		singleTexturing = true;
		currentFogColor = -1;
		currentFoggingState = false;
		currentFogDistance = -1F;
		lastState = 0xfff0bdcb;
		changeCnt = 0;
		supportsRGBScaling = 0;
		supportsShadowMapping = 0;
		textureBufferSize = 0;
		smallBuffer = null;
		blitCoords1 = new SimpleVector();
		blitCoords2 = new SimpleVector();
		depthBuffer = false;
		blitMode = false;
		blitTrans = false;
		blitAdditive = false;
		scissorEnabled = false;
		scissorClearAll = true;
		blitScaling = 0;
		floatBuffer = new float[16];
		fovMatrixCache = new HashMap();
		floatBuffer64 = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asFloatBuffer();
		floatBuffer16 = ByteBuffer.allocateDirect(16).order(ByteOrder.nativeOrder()).asFloatBuffer();
		ambientBuffer = ByteBuffer.allocateDirect(16).order(ByteOrder.nativeOrder()).asFloatBuffer();
		toUnload = new HashSet();
		matrixCache = new HashMap();
		toDispose = new ArrayList();
		toCompileToDL = new HashSet();
		foggingOn = false;
		float af[] = { 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F, 0.0F, 0.5F, 0.5F, 0.5F, 1.0F };
		textureScale.setDump(af);
		resetStates();
		myID = rendererID;
		rendererID++;
		lastFOV = -999F;
		lastFarPlane = -999F;
		lastNearPlane = -999F;
		init = false;
		lastTexture = 0;
		stateChanges = 0;
		pixelBuffer = null;
		pixelBufferSize = 0;
		textureBuffer = null;
		textureBufferSize = 0;
		blitBuffer = null;
		blitBufferWidth = 0;
		blitBufferHeight = 0;
		supportsRGBScaling = 0;
		texMan = TextureManager.getInstance();
	}

	protected void remove(int i) {
		synchronized (this) {
			toDispose.add(IntegerC.valueOf(i));
		}
	}

	protected void resetStates() {
		for (int i = 0; i < 4; i++) {
			stageInitialized[i] = false;
			enabledStages[i] = false;
			buffersEnabled[i] = false;
			lastTextures[i] = 0;
			lastMultiTextures[i] = 0;
			lastIDs[i] = 0;
			lastMultiModes[i] = 0;
			lastMode[i] = -1;
		}

	}

	public final void setPaintListener(IPaintListener ipaintlistener) {
		listener = ipaintlistener;
	}

	public final boolean isInitialized() {
		return init;
	}

	public VideoMode[] getAvailableVideoModes() {
		try {
			DisplayMode[] arrayOfDisplayMode = Display.getAvailableDisplayModes();
			VideoMode[] arrayOfVideoMode = new VideoMode[arrayOfDisplayMode.length];
			for (int i = 0; i < arrayOfDisplayMode.length; ++i) {
				if (arrayOfDisplayMode[i] == null)
					continue;
				arrayOfVideoMode[i] = new VideoMode(arrayOfDisplayMode[i].getWidth(), arrayOfDisplayMode[i].getHeight(),
						arrayOfDisplayMode[i].getBitsPerPixel(), Config.glZBufferDepth, arrayOfDisplayMode[i].getFrequency());
			}
			return arrayOfVideoMode;
		} catch (Exception localException) {
			Logger.log("Couldn't get available video modes: " + localException.getMessage(), 0);
		}
		return null;
	}

	protected synchronized void addToCompile(ICompiledInstance icompiledinstance) {
		toCompileToDL.add(icompiledinstance);
	}

	protected synchronized void compileDLs() {
		if (toCompileToDL.size() != 0) {
			ICompiledInstance icompiledinstance;
			for (Iterator iterator = toCompileToDL.iterator(); iterator.hasNext(); icompiledinstance.compileToDL()) {
				icompiledinstance = (ICompiledInstance) iterator.next();
				if (!icompiledinstance.isFilled())
					Logger.log("Internal error: Unfilled object!", 0);
			}

			Logger.log("Compiled " + toCompileToDL.size() + " display lists!", 2);
			toCompileToDL.clear();
			renableVertexArrays();
		}
	}

	protected void unsetBlendingMode() {
		if (renderTarget == null || !renderTarget.isShadowMap) {
			GL11.glDisable(3042);
		} else {
			GL11.glDisable(3008);
			alphaTest = false;
		}
	}

	protected void setDepthBuffer() {
		if (renderTarget == null || !renderTarget.isShadowMap)
			GL11.glDepthMask(false);
	}

	protected void clear(Object aobj[]) {
		if (scissorEnabled && scissorClearAll)
			GL11.glDisable(3089);
		int i = 256;
		if (!Config.isIndoor) {
			i |= 0x4000;
			if (aobj != null) {
				Color color = (Color) aobj[0];
				GL11.glClearColor((float) color.getRed() / 255F, (float) color.getGreen() / 255F,
						(float) color.getBlue() / 255F, 0.0F);
			} else {
				GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
			}
		}
		GL11.glClear(i);
		if (scissorEnabled && scissorClearAll)
			GL11.glEnable(3089);
	}

	protected void clearZBufferOnly() {
		if (scissorEnabled && scissorClearAll)
			GL11.glDisable(3089);
		GL11.glClear(256);
		if (scissorEnabled && scissorClearAll)
			GL11.glEnable(3089);
	}

	protected void setBlendingMode(int i) {
		if (renderTarget == null || !renderTarget.isShadowMap) {
			switch (i) {
			case 0: // '\0'
				GL11.glEnable(3042);
				GL11.glBlendFunc(770, 771);
				break;

			case 1: // '\001'
				GL11.glEnable(3042);
				GL11.glBlendFunc(770, 1);
				break;
			}
		} else {
			GL11.glAlphaFunc(518, 0.01F);
			GL11.glEnable(3008);
		}
	}

	protected void doPostProcessing(Object aobj[]) {
		endState();
		IPostProcessor ipostprocessor = (IPostProcessor) aobj[1];
		if (!ipostprocessor.isInitialized())
			ipostprocessor.init((FrameBuffer) aobj[0]);
		int i = currentRGBScaling;
		disableAllHigherStages();
		enableStage(0);
		if (projective[0]) {
			projective[0] = false;
			disableProjection();
		}
		setRGBScaling(1);
		if (renderTarget != null)
			resetViewport((FrameBuffer) aobj[0]);
		lastTextures[0] = -1;
		switchTextureMode(0, modeMap[0]);
		ipostprocessor.process();
		if (renderTarget != null)
			setViewport((FrameBuffer) aobj[0]);
		setRGBScaling(i);
	}

	protected void setRenderTarget(Object aobj[]) {
		renderTarget = (Texture) aobj[0];
		FrameBuffer framebuffer = (FrameBuffer) aobj[1];
		int i = ((Integer) aobj[2]).intValue();
		int j = ((Integer) aobj[3]).intValue();
		int k = ((Integer) aobj[4]).intValue();
		int l = ((Integer) aobj[5]).intValue();
		scissorClearAll = ((Boolean) aobj[6]).booleanValue();
		if (renderTarget != null)
			enableScissor(framebuffer, renderTarget, i, j, k, l);
		else
			disableScissor();
		if (!useFBO) {
			if (renderTarget == null) {
				resetViewport(framebuffer);
				GL11.glColorMask(true, true, true, true);
			} else {
				setViewport(framebuffer);
				if (renderTarget.isShadowMap)
					GL11.glColorMask(false, false, false, false);
			}
		} else if (renderTarget == null) {
			GL11.glColorMask(true, true, true, true);
			if (fbo != -1) {
				EXTFramebufferObject.glBindFramebufferEXT(36160, 0);
				resetViewport(framebuffer);
			}
		} else {
			if (fbo == -1 || fboTexture != renderTarget.getOpenGLID(myID) || fboDepthMode != renderTarget.isShadowMap) {
				IntBuffer intbuffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
				if (fbo != -1) {
					intbuffer.put(fbo);
					intbuffer.rewind();
					EXTFramebufferObject.glDeleteFramebuffersEXT(intbuffer);
					intbuffer.rewind();
					fbo = -1;
				}
				EXTFramebufferObject.glGenFramebuffersEXT(intbuffer);
				fbo = intbuffer.get();
				if (renderTarget.getOpenGLID(myID) == 0) {
					renderTarget.setMarker(myID, Texture.MARKER_NOTHING);
					endState();
					convertTexture(renderTarget);
					lastTextures[0] = -1;
				}
				int i1 = renderTarget.getOpenGLID(myID);
				bindTexture(0, i1);
				fboTexture = renderTarget.getOpenGLID(myID);
				fboDepthMode = renderTarget.isShadowMap;
				int j1 = 36064;
				if (fboDepthMode)
					j1 = 36096;
				EXTFramebufferObject.glBindFramebufferEXT(36160, fbo);
				EXTFramebufferObject.glFramebufferTexture2DEXT(36160, j1, 3553, i1, 0);
				if (fboDepthMode) {
					if (fboColorStorage != null
							&& (fboColorStorage.getHeight() != renderTarget.getHeight() || fboColorStorage.getWidth() != renderTarget
									.getWidth())) {
						removeTexture(fboColorStorage);
						fboColorStorage = null;
					}
					if (fboColorStorage == null) {
						fboColorStorage = new Texture(renderTarget.getWidth(), renderTarget.getHeight());
						fboColorStorage.setGLFiltering(false);
						fboColorStorage.setGLMipmap(false);
						convertTexture(fboColorStorage);
						fboColorStorage.texels = null;
					}
					int k1 = fboColorStorage.getOpenGLID(myID);
					if (!Config.glIgnoreAlphaBlendingFBO)
						EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, k1, 0);
					GL11.glReadBuffer(0);
					if (Config.glIgnoreAlphaBlendingFBO)
						GL11.glDrawBuffer(0);
					GL11.glColorMask(false, false, false, false);
				} else {
					if (fboDepthStorage != null
							&& (fboDepthStorage.getHeight() != renderTarget.getHeight() || fboDepthStorage.getWidth() != renderTarget
									.getWidth())) {
						removeTexture(fboDepthStorage);
						fboDepthStorage = null;
					}
					if (fboDepthStorage == null) {
						fboDepthStorage = new Texture(renderTarget.getWidth(), renderTarget.getHeight());
						fboDepthStorage.setGLFiltering(false);
						fboDepthStorage.setGLMipmap(false);
						fboDepthStorage.setAsShadowMap(true);
						convertTexture(fboDepthStorage);
						fboDepthStorage.texels = null;
					}
					int l1 = fboDepthStorage.getOpenGLID(myID);
					EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, l1, 0);
					GL11.glColorMask(true, true, true, true);
				}
				checkFrameBufferObject();
			} else {
				EXTFramebufferObject.glBindFramebufferEXT(36160, fbo);
			}
			GL11.glViewport(0, 0, renderTarget.getWidth(), renderTarget.getHeight());
		}
	}

	private void checkFrameBufferObject() {
		int i = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
		if (i != 36053) {
			useFBO = false;
			fbo = -1;
		}
		switch (i) {
		case 36054:
			Logger.log("FrameBuffer: " + fbo + " has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception", 0);
			break;

		case 36055:
			Logger
					.log("FrameBuffer: " + fbo + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception", 0);
			break;

		case 36057:
			Logger.log("FrameBuffer: " + fbo + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception", 0);
			break;

		case 36059:
			Logger.log("FrameBuffer: " + fbo + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception", 0);
			break;

		case 36058:
			Logger.log("FrameBuffer: " + fbo + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception", 0);
			break;

		case 36060:
			Logger.log("FrameBuffer: " + fbo + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception", 0);
			break;

		case 36056:
		default:
			Logger.log("Unexpected reply from glCheckFramebufferStatusEXT: " + i, 0);
			break;

		case 36053:
			break;
		}
	}

	protected Matrix createTextureProjectionMatrix(Camera camera, Texture texture) {
		float f = camera.getFOV();
		String s = new Float(f + 100F * camera.getYFOV()) + texture.toString();
		Matrix matrix = (Matrix) fovMatrixCache.get(s);
		if (matrix == null) {
			GL11.glMatrixMode(5889);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			float f1 = f;
			if (Config.autoMaintainAspectRatio)
				f1 = f * ((float) texture.width / (float) texture.height);
			if (camera.getYFOV() != -1F) {
				f1 = camera.getYFOV();
				if (f1 == 0.0F)
					f1 = 1E-005F;
			}
			float f2 = Config.farPlane;
			float f3 = 1.0F;
			if (!Config.glIgnoreNearPlane)
				f3 = Config.nearPlane;
			float f4 = f1 * 0.5F;
			float f5 = -f1 * 0.5F;
			float f6 = -f * 0.5F;
			float f7 = f * 0.5F;
			GL11.glFrustum(f6, f7, f5, f4, f3, f2);
			GL11.glGetFloat(2983, floatBuffer64);
			GL11.glPopMatrix();
			floatBuffer64.rewind();
			matrix = new Matrix();
			float af[] = floatBuffer;
			for (int i = 0; i < 16; i++)
				af[i] = floatBuffer64.get(i);

			matrix.setDump(af);
			if (fovMatrixCache.size() > 1000)
				fovMatrixCache.clear();
			fovMatrixCache.put(s, matrix);
		}
		return matrix;
	}

	protected void resetTextureStates() {
		for (int i = 0; i < 4; i++)
			lastTextures[i] = -1;

	}

	protected void processProjection(int i, Texture texture, Camera camera) {
		if (texture.projector != null || texture.projectorBuffer != null) {
			Matrix matrix = new Matrix();
			Matrix matrix1 = null;
			SimpleVector simplevector = camera.getPosition();
			matrix1 = camera.getBack().cloneMatrix();
			matrix1.rotateX(3.141593F);
			matrix1 = matrix1.invert3x3();
			matrix1.mat[3][0] = simplevector.x;
			matrix1.mat[3][1] = simplevector.y;
			matrix1.mat[3][2] = simplevector.z;
			Projector projector = texture.projectorBuffer;
			if (projector == null)
				projector = texture.projector;
			simplevector = projector.getPosition();
			matrix.mat[3][0] = -simplevector.x;
			matrix.mat[3][1] = -simplevector.y;
			matrix.mat[3][2] = -simplevector.z;
			matrix1.matMul(matrix);
			Matrix matrix2 = projector.getBack().cloneMatrix();
			matrix2.rotateX(3.141593F);
			matrix1.matMul(matrix2);
			matrix1.matMul(createTextureProjectionMatrix(projector, texture));
			matrix1.matMul(textureScale);
			float af[] = matrix1.getDump();
			floatBuffer16.rewind();
			for (int j = 0; j < 4; j++)
				floatBuffer16.put(af[j << 2]);

			floatBuffer16.flip();
			GL11.glTexGeni(8192, 9472, 9216);
			GL11.glTexGen(8192, 9474, floatBuffer16);
			GL11.glEnable(3168);
			floatBuffer16.rewind();
			for (int k = 0; k < 4; k++)
				floatBuffer16.put(af[1 + (k << 2)]);

			floatBuffer16.flip();
			GL11.glTexGeni(8193, 9472, 9216);
			GL11.glTexGen(8193, 9474, floatBuffer16);
			GL11.glEnable(3169);
			floatBuffer16.rewind();
			for (int l = 0; l < 4; l++)
				floatBuffer16.put(af[2 + (l << 2)]);

			floatBuffer16.flip();
			GL11.glTexGeni(8194, 9472, 9216);
			GL11.glTexGen(8194, 9474, floatBuffer16);
			GL11.glEnable(3170);
			floatBuffer16.rewind();
			for (int i1 = 0; i1 < 4; i1++)
				floatBuffer16.put(af[3 + (i1 << 2)]);

			floatBuffer16.flip();
			GL11.glTexGeni(8195, 9472, 9216);
			GL11.glTexGen(8195, 9474, floatBuffer16);
			GL11.glEnable(3171);
			if (texture.getOpenGLID(myID) == 0) {
				texture.setMarker(myID, Texture.MARKER_NOTHING);
				endState();
				convertTexture(texture);
				lastTextures[0] = -1;
			}
			projective[i] = true;
			if (texture.isShadowMap)
				enableShadowMapping(texture);
		}
	}

	protected void enableShadowMapping(Texture texture) {
		if (renderTarget != texture) {
			GL11.glAlphaFunc(518, 0.01F);
			GL11.glEnable(3008);
			alphaTest = true;
		}
	}

	protected void disableProjection() {
		GL11.glDisable(3168);
		GL11.glDisable(3169);
		GL11.glDisable(3170);
		GL11.glDisable(3171);
	}

	protected void disableGlobalAlphaTest() {
		if (alphaTest) {
			GL11.glDisable(3008);
			alphaTest = false;
		}
	}

	protected void disableShadowMap() {
		disableGlobalAlphaTest();
	}

	protected boolean renderToTarget() {
		if (renderTarget == null)
			return false;
		if (useFBO)
			return true;
		disableUnusedStages();
		switchTextureMode(0, modeMap[0]);
		endState();
		int i = currentRGBScaling;
		setRGBScaling(1);
		if (renderTarget.getOpenGLID(myID) == 0) {
			renderTarget.setMarker(myID, Texture.MARKER_NOTHING);
			endState();
			convertTexture(renderTarget);
			lastTextures[0] = -1;
		}
		int j = renderTarget.getOpenGLID(myID);
		bindTexture(0, j);
		if (renderTarget.isShadowMap) {
			if (supportsShadowMapping == 1)
				GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, yTargetStart, renderTarget.getWidth(), renderTarget.getHeight());
		} else if (Config.glFlipRenderTargets) {
			toOrtho();
			GL11.glCopyTexImage2D(3553, 0, 6407, 0, yTargetStart, renderTarget.getWidth(), renderTarget.getHeight(), 0);
			disableDepthBuffer();
			drawQuad();
			enableDepthBuffer();
			GL11.glCopyTexImage2D(3553, 0, 6407, 0, yTargetStart, renderTarget.getWidth(), renderTarget.getHeight(), 0);
			restorePerspectiveProjection();
		} else {
			GL11.glCopyTexImage2D(3553, 0, 6407, 0, yTargetStart, renderTarget.getWidth(), renderTarget.getHeight(), 0);
		}
		setRGBScaling(i);
		return true;
	}

	protected void disableAllHigherStages() {
		for (int i = 1; i < maxStages; i++)
			disableStage(i);

	}

	private void drawQuad() {
		GL11.glBegin(7);
		GL11.glTexCoord2f(0.0F, 1.0F);
		GL11.glVertex3f(0.0F, 1.0F, -1F);
		GL11.glTexCoord2f(0.0F, 0.0F);
		GL11.glVertex3f(0.0F, 0.0F, -1F);
		GL11.glTexCoord2f(1.0F, 0.0F);
		GL11.glVertex3f(1.0F, 0.0F, -1F);
		GL11.glTexCoord2f(1.0F, 1.0F);
		GL11.glVertex3f(1.0F, 1.0F, -1F);
		GL11.glEnd();
	}

	private static void toOrtho() {
		GL11.glMatrixMode(5888);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glMatrixMode(5889);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, 1.0D, 1.0D, 0.0D, 0.10000000000000001D, 100D);
	}

	private static void restorePerspectiveProjection() {
		GL11.glMatrixMode(5889);
		GL11.glPopMatrix();
		GL11.glMatrixMode(5888);
		GL11.glPopMatrix();
	}

	protected final void init(boolean flag, int i, int j) {
		if (flag) {
			GL11.glViewport(0, 0, i, j);
			GL11.glMatrixMode(5889);
			GL11.glLoadIdentity();
			GL11.glMatrixMode(5888);
			GL11.glLoadIdentity();
			GL11.glShadeModel(7425);
			GL11.glClearDepth(1.0D);
			enableDepthBuffer();
			GL11.glDepthFunc(515);
			GL11.glHint(3152, 4354);
			GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
			GL11.glClear(16640);
			initTextureStage(0);
			lastFOV = -999F;
			lastFarPlane = -999F;
			lastNearPlane = -999F;
			minDriverAndConfig = getTextureStages();
			Config.glStageCount = minDriverAndConfig;
			GLHelper.printDriver();
			hasOpenGL12 = GLContext.getCapabilities().OpenGL12;
			Config.glBlendingAffectsAlpha = GLContext.getCapabilities().GL_ARB_texture_env_combine
					&& Config.glBlendingAffectsAlpha;
			if (Config.glBlendingAffectsAlpha)
				Logger.log("GL_ARB_texture_env_combine supported and used!", 2);
			else
				Logger.log("GL_ARB_texture_env_combine not supported or disabled!", 2);
			useFBO = GLContext.getCapabilities().GL_EXT_framebuffer_object && Config.glUseFBO;
			Config.glUseFBO = useFBO;
			if (useFBO)
				Logger.log("FBO supported and used!", 2);
			else
				Logger.log("FBO not supported or disabled!", 2);
			Logger.log("OpenGL renderer initialized (using " + minDriverAndConfig + " texture stages)", 2);
			GL11.glFinish();
			init = true;
		}
	}

	protected void reInit() {
		endState();
		if (depthBuffer && !GL11.glIsEnabled(2929))
			GL11.glEnable(2929);
		if (enabledStages[0]) {
			ARBMultitexture.glActiveTextureARB(stageMap[0]);
			if (!GL11.glIsEnabled(3553)) {
				GL11.glEnable(3553);
				disableAllHigherStages();
			}
		}
	}

	protected void dispose() {
		if (!disposed) {
			disposed = true;
			init = false;
			lastFOV = -999F;
			lastFarPlane = -999F;
			lastNearPlane = -999F;
			lastTexture = 0;
			pixelBuffer = null;
			textureBuffer = null;
			blitBuffer = null;
			listener = null;
			try {
				if (useFBO && fbo != -1) {
					IntBuffer intbuffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
					if (fbo != -1) {
						intbuffer.put(fbo);
						intbuffer.rewind();
						EXTFramebufferObject.glDeleteFramebuffersEXT(intbuffer);
					}
					if (fboColorStorage != null) {
						removeTexture(fboColorStorage);
						fboColorStorage = null;
					}
					if (fboDepthStorage != null) {
						removeTexture(fboDepthStorage);
						fboDepthStorage = null;
					}
				}
			} catch (Exception exception) {
			}
			Texture atexture[] = TextureManager.getInstance().textures;
			for (int i = 0; i < atexture.length; i++)
				try {
					removeTexture(atexture[i]);
				} catch (Exception exception1) {
				}

			texMan.flushOpenGLIDs(myID);
		}
	}

	protected final void initTextureStage(int i, int j) {
		switchTextureMode(i, j);
		stageInitialized[i] = true;
	}

	protected final void disableUnusedStages() {
		if (!singleTexturing) {
			endState();
			for (int i = 1; i < maxStages; i++)
				if (enabledStages[i]) {
					ARBMultitexture.glActiveTextureARB(stageMap[i]);
					GL11.glDisable(3553);
					enabledStages[i] = false;
				}

			enableStage(0);
			singleTexturing = true;
		}
	}

	protected final void enableStage(int i) {
		endState();
		ARBMultitexture.glActiveTextureARB(stageMap[i]);
		if (!enabledStages[i]) {
			GL11.glEnable(3553);
			enabledStages[i] = true;
			if (i > 0)
				singleTexturing = false;
		}
	}

	protected final void disableStage(int i) {
		if (enabledStages[i] && !singleTexturing) {
			endState();
			ARBMultitexture.glActiveTextureARB(stageMap[i]);
			GL11.glDisable(3553);
			enabledStages[i] = false;
			singleTexturing = true;
			int j = 1;
			do {
				if (j >= maxStages)
					break;
				if (enabledStages[j]) {
					singleTexturing = false;
					break;
				}
				j++;
			} while (true);
		}
	}

	protected void bindAndProject(int i, Texture texture, Camera camera) {
		int j = texture.getOpenGLID(myID);
		boolean flag = texture.projector != null;
		if (j != lastTextures[i] || flag != projective[i] || texture.isShadowMap && !alphaTest) {
			bindTexture(i, j, flag);
			processProjection(i, texture, camera);
		} else if (i != 0)
			enableStage(i);
	}

	protected final void enableCompiledPipeline() {
		CompiledInstance.lastVertexBuffer = null;
		GL11.glEnable(2977);
		GL11.glEnable(2896);
		GL11.glEnable(2884);
	}

	protected final void disableCompiledPipeline() {
		CompiledInstance.lastVertexBuffer = null;
		GL11.glDisable(2884);
		GL11.glDisable(2896);
		GL11.glDisable(2977);
	}

	protected final void bindTexture(int i, int j, boolean flag) {
		enableStage(i);
		GL11.glBindTexture(3553, j);
		stateChanges++;
		lastTextures[i] = j;
		if (!flag && projective[i]) {
			disableProjection();
			projective[i] = false;
		}
	}

	protected final void bindTexture(int i, int j) {
		bindTexture(i, j, false);
	}

	protected final void switchTextureMode(int i, int j) {
		if (lastMode[i] != j) {
			endState();
			enableStage(i);
			if (Config.glBlendingAffectsAlpha && j != 3042) {
				GL11.glTexEnvi(8960, 8704, 34160);
				GL11.glTexEnvi(8960, 34161, j);
				GL11.glTexEnvi(8960, 34162, j);
			} else {
				GL11.glTexEnvi(8960, 8704, j);
			}
			lastMode[i] = j;
		}
	}

	protected final void beginState(int i) {
		if (lastState != i) {
			if (lastState != 0xfff0bdcb)
				GL11.glEnd();
			GL11.glBegin(i);
			lastState = i;
		} else {
			changeCnt++;
		}
	}

	protected final void endState() {
		if (lastState != 0xfff0bdcb)
			GL11.glEnd();
		changeCnt = 0;
		lastState = 0xfff0bdcb;
	}

	protected final boolean isDisposed() {
		return disposed;
	}

	protected final void removeTexture(Texture texture) {
		if (texture != null && texture.getOpenGLID(myID) != 0) {
			IntBuffer intbuffer = getSmallBuffer();
			intbuffer.put(texture.getOpenGLID(myID));
			intbuffer.flip();
			GL11.glDeleteTextures(intbuffer);
		}
	}

	protected final void addClippingPlane(int i, float af[]) {
		endState();
		if (clippingBuffer == null)
			clippingBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asDoubleBuffer();
		clippingBuffer.rewind();
		clippingBuffer.put(0, af[0]);
		clippingBuffer.put(1, af[1]);
		clippingBuffer.put(2, af[2]);
		clippingBuffer.put(3, af[3]);
		GL11.glClipPlane(12288 + i, clippingBuffer);
		GL11.glEnable(12288 + i);
	}

	protected final void removeClippingPlane(int i) {
		GL11.glDisable(12288 + i);
	}

	protected final void initializeVertexArrays() {
		if (init) {
			colors = ByteBuffer.allocateDirect(16000).order(ByteOrder.nativeOrder()).asFloatBuffer();
			vertices = ByteBuffer.allocateDirect(12000).order(ByteOrder.nativeOrder()).asFloatBuffer();
			GL11.glColorPointer(4, 16, colors);
			GL11.glVertexPointer(3, 12, vertices);
			GL11.glEnableClientState(32886);
			GL11.glEnableClientState(32884);
			createVertexArrays(minDriverAndConfig);
			vertexArraysInitialized = true;
		}
	}

	protected final void renableVertexArrays() {
		if (colors != null) {
			GL11.glColorPointer(4, 16, colors);
			GL11.glVertexPointer(3, 12, vertices);
			GL11.glEnableClientState(32886);
			GL11.glEnableClientState(32884);
			GL11.glDisableClientState(32885);
			for (int i = 0; i < minDriverAndConfig; i++) {
				ARBMultitexture.glClientActiveTextureARB(stageMap[i]);
				GL11.glEnableClientState(32888);
				GL11.glTexCoordPointer(2, 8, multiTextures[i]);
			}

		}
	}

	protected final void enableVertexArrays(int i, int j) {
		if (i > minDriverAndConfig)
			return;
		if (i == j)
			return;
		if (i < j) {
			for (int k = i; k < j; k++)
				if (buffersEnabled[k]) {
					ARBMultitexture.glClientActiveTextureARB(stageMap[k]);
					GL11.glDisableClientState(32888);
					buffersEnabled[k] = false;
				}

			return;
		}
		if (j < i) {
			for (int l = j; l < i; l++)
				if (!buffersEnabled[l]) {
					ARBMultitexture.glClientActiveTextureARB(stageMap[l]);
					GL11.glEnableClientState(32888);
					buffersEnabled[l] = true;
				}

			return;
		} else {
			return;
		}
	}

	protected final void renderVertexArray(int i) {
		if (i != 0) {
			boolean flag = false;
			for (int j = 0; j < projective.length; j++)
				if (projective[j] && buffersEnabled[j]) {
					ARBMultitexture.glClientActiveTextureARB(stageMap[j]);
					GL11.glDisableClientState(32888);
					flag = true;
				}

			GL11.glDrawArrays(4, 0, i);
			if (flag) {
				for (int k = 0; k < projective.length; k++)
					if (projective[k] && buffersEnabled[k]) {
						ARBMultitexture.glClientActiveTextureARB(stageMap[k]);
						GL11.glEnableClientState(32888);
					}

			}
		}
	}

	protected final IntBuffer getSmallBuffer() {
		if (smallBuffer == null || !Config.glUseCaches)
			smallBuffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		else
			smallBuffer.clear();
		return smallBuffer;
	}

	protected final void disableFogging() {
		GL11.glDisable(2912);
		currentFoggingState = false;
		currentFogColor = -1;
		currentFogDistance = -1F;
	}

	private void resetShadows() {
		disableShadowMap();
	}

	protected final void setRGBScaling(int i) {
		resetShadows();
		try {
			if (i != currentRGBScaling) {
				if (supportsRGBScaling == 0)
					if (supportsExtension("GL_ARB_texture_env_combine")) {
						supportsRGBScaling = 1;
					} else {
						supportsRGBScaling = -1;
						Logger.log("This hardware or driver doesn't support the GL_ARB_texture_env_combine extension!", 1);
					}
				if (supportsRGBScaling == 1) {
					enableStage(0);
					GL11.glTexEnvi(8960, 8704, 34160);
					GL11.glTexEnvi(8960, 34161, 8448);
					GL11.glTexEnvi(8960, 34163, i);
					currentRGBScaling = i;
				}
			}
		} catch (Exception exception) {
			Logger.log("Couldn't initialize the GL_ARB_texture_env_combine extension!", 0);
		}
	}

	protected final void enableFogging(float f, float f1, float f2, float f3, float f4) {
		if (f2 < 0.0F)
			f2 = 0.0F;
		else if (f2 > 255F)
			f2 = 255F;
		if (f3 < 0.0F)
			f3 = 0.0F;
		else if (f3 > 255F)
			f3 = 255F;
		if (f4 < 0.0F)
			f4 = 0.0F;
		else if (f4 > 255F)
			f4 = 255F;
		int i = (int) f2 << 16 | (int) f3 << 8 | (int) f4;
		if (f1 != currentFogDistance || i != currentFogColor) {
			if (currentFoggingState)
				disableFogging();
			currentFoggingState = true;
			FloatBuffer floatbuffer = ByteBuffer.allocateDirect(16).order(ByteOrder.nativeOrder()).asFloatBuffer();
			floatbuffer.put(f2 / 255F);
			floatbuffer.put(f3 / 255F);
			floatbuffer.put(f4 / 255F);
			floatbuffer.put(1.0F);
			floatbuffer.flip();
			GL11.glEnable(2912);
			GL11.glFogf(2915, f);
			GL11.glFogf(2916, f1);
			GL11.glFogi(2917, 9729);
			GL11.glFog(2918, floatbuffer);
			GL11.glFogf(2914, 1.0F);
			currentFogColor = i;
			currentFogDistance = f1;
		}
	}

	protected final void convertTexture(Texture texture) {
		int i = texture.getHeight();
		int j = texture.getWidth();
		int k = i * j;
		int ai[] = texture.texels;
		ByteBuffer bytebuffer = null;
		int l = k << 2;
		if (Config.glUseCaches && textureBuffer != null && l == textureBufferSize)
			bytebuffer = textureBuffer;
		if (bytebuffer == null) {
			if (Config.glVerbose)
				Logger.log("Allocating " + l + " bytes of direct memory for texture: " + texture, 2);
			try {
				bytebuffer = ByteBuffer.allocateDirect(l);
				bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
			} catch (OutOfMemoryError outofmemoryerror) {
				Logger.log("Unable to allocate " + l + " bytes of direct memory", 0);
				return;
			}
			if (k <= 0x100000) {
				textureBuffer = bytebuffer;
				textureBufferSize = l;
				if (Config.glVerbose)
					Logger.log("Caching " + l + " bytes of direct memory!", 2);
			}
		}
		int ai1[] = texture.alpha;
		if (ai != null) {
			for (int i1 = 0; i1 < k; i1++) {
				int j1 = i1 << 2;
				int k1 = ai[i1];
				int i2 = k1 & 0xff00 | (k1 & 0xff) << 16 | k1 >> 16;
				if (ai1 == null) {
					if ((k1 & 0xf0f0f0) != 0)
						i2 |= 0xff000000;
				} else {
					i2 |= ai1[i1];
				}
				bytebuffer.putInt(j1, i2);
			}

		}
		ByteBuffer bytebuffer1 = bytebuffer;
		IntBuffer intbuffer = getSmallBuffer();
		GL11.glGenTextures(intbuffer);
		int l1 = intbuffer.get(0);
		if (l1 == 0)
			Logger.log("Failed to upload texture!", 0);
		int j2 = lastTextures[0];
		bindTexture(0, l1);
		char c = '\u2601';
		if (Config.glMipmap)
			c = '\u2701';
		if (Config.glTrilinear)
			c = '\u2703';
		if (!texture.mipmap)
			c = '\u2601';
		int k2 = 6408;
		if (Config.glTextureDepth == 32)
			k2 = 32856;
		if (Config.glTextureDepth == 16)
			k2 = 32854;
		if (c == '\u2601' || texture.isShadowMap) {
			if (texture.isShadowMap) {
				if (supportsShadowMapping == 0)
					if (supportsExtension("GL_ARB_shadow") && supportsExtension("GL_ARB_depth_texture")) {
						supportsShadowMapping = 1;
						checkLimitations();
					} else {
						supportsShadowMapping = -1;
						Logger.log("This hardware or driver doesn't support ARB shadow mapping extensions!", 1);
					}
				if (supportsShadowMapping == 1) {
					GL11.glTexImage2D(3553, 0, 6402, j, i, 0, 6402, 5121, (ByteBuffer) null);
					GL11.glTexParameteri(3553, 34892, 34894);
					GL11.glTexParameteri(3553, 34893, 515);
					GL11.glTexParameteri(3553, 34891, 32841);
					floatBuffer16.rewind();
					floatBuffer16.put(1.0F);
					floatBuffer16.put(1.0F);
					floatBuffer16.put(1.0F);
					floatBuffer16.put(1.0F);
					floatBuffer16.flip();
					GL11.glTexParameter(3553, 4100, floatBuffer16);
				}
			} else {
				GL11.glTexImage2D(3553, 0, k2, j, i, 0, 6408, 5121, bytebuffer1);
			}
		} else {
			GLU.gluBuild2DMipmaps(3553, k2, j, i, 6408, 5121, bytebuffer1);
		}
		int l2 = 10497;
		if (!texture.repeat)
			if (hasOpenGL12)
				l2 = 33071;
			else
				l2 = 10496;
		if (texture.isShadowMap) {
			c = '\u2600';
			l2 = 10496;
		}
		GL11.glTexParameteri(3553, 10241, c);
		if (texture.bilinear)
			GL11.glTexParameteri(3553, 10240, 9729);
		else
			GL11.glTexParameteri(3553, 10240, 9728);
		GL11.glTexParameteri(3553, 10242, l2);
		GL11.glTexParameteri(3553, 10243, l2);
		texture.setOpenGLID(myID, l1);
		if (j2 != 0)
			bindTexture(0, j2);
	}

	protected void checkLimitations() {
		if (!Config.glUseFBO) {
			String s = GLHelper.getHardware().toLowerCase();
			String as[] = { "x800", "x700", "x600", "x500", "x300", "x850", "x1800", "9700", "9800", "9650", "9600", "9500",
					"9200", "9100", "9000", "8500", "8000", "7500" };
			String as1[] = { "x1900", "x1600", "x1950", "x1650", "hd 2900", "hd 2600", "hd 2400", "x1300" };
			if (s.indexOf("ati") != -1) {
				int i = 0;
				do {
					if (i >= as.length)
						break;
					if (s.indexOf(as[i]) != -1) {
						Logger
								.log(
										"This graphics hardware may have performance problems with shadow mapping/depth textures without using FBOs!",
										1);
						break;
					}
					i++;
				} while (true);
				i = 0;
				do {
					if (i >= as1.length)
						break;
					if (s.indexOf(as1[i]) != -1) {
						Logger
								.log(
										"This graphics hardware may have problems with shadow mapping in combination with anti-aliasing without using FBOs!",
										1);
						break;
					}
					i++;
				} while (true);
			}
		}
	}

	protected final boolean supportsExtension(String s) {
		String s1 = GL11.glGetString(7939);
		return s1.toLowerCase().indexOf(s.toLowerCase()) != -1;
	}

	protected final int getTextureSize() {
		IntBuffer intbuffer = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
		GL11.glGetInteger(3379, intbuffer);
		return intbuffer.get(0);
	}

	protected final void enableDepthBuffer() {
		if (!depthBuffer) {
			GL11.glEnable(2929);
			depthBuffer = true;
		}
	}

	protected final void disableDepthBuffer() {
		if (depthBuffer) {
			GL11.glDisable(2929);
			depthBuffer = false;
		}
	}

	protected void enableBlitting(Object aobj[]) {
		if (!blitMode) {
			GL11.glFlush();
			curPos = 0;
			colPos = 0;
			texPos = 0;
			vertPos = 0;
		}
		boolean flag = ((Boolean) aobj[8]).booleanValue();
		boolean flag1 = aobj.length > 11 && ((Boolean) aobj[14]).booleanValue();
		endState();
		resetShadows();
		if (flag && (!blitMode || !blitTrans)) {
			int i = 0;
			if (flag1)
				i = 1;
			blitAdditive = flag1;
			setBlendingMode(i);
			blitTrans = true;
		}
		if (!blitMode) {
			blitScaling = currentRGBScaling;
			if (myWorld != null)
				setRGBScaling(1);
			disableDepthBuffer();
			blitMode = true;
		} else {
			if (blitTrans && !flag) {
				executeBufferedBlits();
				GL11.glDisable(3042);
				blitTrans = false;
			}
			if (blitTrans && flag && flag1 != blitAdditive) {
				executeBufferedBlits();
				blitAdditive = flag1;
				int j = 0;
				if (flag1)
					j = 1;
				setBlendingMode(j);
			}
		}
	}

	protected void executeBufferedBlits() {
		if (Config.glVertexArrays && Config.glBufferedBlits) {
			renderVertexArray(curPos);
			curPos = 0;
			colPos = 0;
			texPos = 0;
			vertPos = 0;
		}
	}

	protected void disableBlitting() {
		if (blitMode) {
			executeBufferedBlits();
			if (myWorld != null && blitScaling != currentRGBScaling)
				setRGBScaling(blitScaling);
			if (blitTrans) {
				GL11.glDisable(3042);
				blitTrans = false;
			}
			enableDepthBuffer();
			blitMode = false;
		}
	}

	protected void setLightsAndFog(World world) {
		setRGBScaling(world.lights.rgbScale);
		boolean flag = false;
		if (world.fogModeChanged == 1) {
			enableFogging(world.fogStart, world.fogDistance, world.fogColorR, world.fogColorG, world.fogColorB);
			world.fogModeChanged = 0;
			foggingOn = true;
			flag = true;
		} else if (world.fogModeChanged == 2) {
			disableFogging();
			world.fogModeChanged = 0;
			foggingOn = false;
			flag = true;
		}
		if (!flag)
			if (!world.useFogging && foggingOn) {
				disableFogging();
				foggingOn = false;
			} else if (world.useFogging && world.perPixelFogging && !foggingOn) {
				enableFogging(world.fogStart, world.fogDistance, world.fogColorR, world.fogColorG, world.fogColorB);
				foggingOn = true;
			}
	}

	protected void grabScreen(Object aobj[]) {
		FrameBuffer framebuffer = (FrameBuffer) aobj[0];
		int ai[] = (int[]) aobj[1];
		int i = framebuffer.getOutputWidth();
		int j = framebuffer.getOutputHeight();
		int k = i * j;
		int l = k << 2;
		IntBuffer intbuffer = null;
		if (pixelBuffer != null && l == pixelBufferSize)
			intbuffer = pixelBuffer;
		if (!Config.glUseCaches)
			intbuffer = null;
		if (intbuffer == null) {
			intbuffer = ByteBuffer.allocateDirect(l).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
			pixelBuffer = intbuffer;
			pixelBufferSize = l;
		}
		GL11.glReadPixels(0, 0, i, j, 32993, 5121, intbuffer);
		for (int i1 = 0; i1 < k; i1++)
			ai[i1] = intbuffer.get(i1);

		for (int j1 = 0; j1 < j >> 1; j1++) {
			int k1 = j1 * i;
			int l1 = (j - 1 - j1) * i;
			for (int i2 = 0; i2 < i; i2++) {
				int j2 = i2 + k1;
				int k2 = l1 + i2;
				int l2 = ai[j2];
				ai[j2] = ai[k2] & 0xffffff;
				ai[k2] = l2 & 0xffffff;
			}

		}

	}

	protected void prepareForBlitting(Object aobj[]) {
		boolean flag = false;
		int j = ((Integer) aobj[9]).intValue();
		int k = ((Integer) aobj[10]).intValue();
		if (blitBuffer != null) {
			int i = blitBuffer.getOpenGLID(myID);
			if (blitBufferWidth == j && blitBufferHeight == k) {
				if (!Config.glUseIgnorantBlits) {
					blitBuffer.refill((int[]) aobj[0], j, k);
					blitBuffer.setMarker(myID, Texture.MARKER_DELETE_AND_UPLOAD);
					blitBuffer.setOpenGLID(myID, i);
				}
			} else {
				removeTexture(blitBuffer);
				blitBuffer = new Texture((int[]) aobj[0], j, k, true);
				blitBuffer.setMarker(myID, Texture.MARKER_DELETE_AND_UPLOAD);
				blitBuffer.setOpenGLID(myID, i);
			}
		} else {
			blitBuffer = new Texture((int[]) aobj[0], j, k, true);
		}
		blitBufferWidth = j;
		blitBufferHeight = k;
	}

	protected void setBufferViewport(Object aobj[]) {
		xViewStart = ((Integer) aobj[0]).intValue();
		yViewStart = ((Integer) aobj[1]).intValue();
		xViewEnd = ((Integer) aobj[2]).intValue();
		yViewEnd = ((Integer) aobj[3]).intValue();
		if (renderTarget == null)
			GL11.glViewport(xViewStart, yViewStart, xViewEnd, yViewEnd);
	}

	protected void blitVA(Object aobj[], float f, float f1) {
		if (init) {
			if (!vertexArraysInitialized)
				initializeVertexArrays();
			FrameBuffer framebuffer = (FrameBuffer) aobj[1];
			if (renderTarget != null)
				resetViewport(framebuffer);
			if (veryLastCoords != 1) {
				enableVertexArrays(1, veryLastCoords);
				lastCoords = 1;
				veryLastCoords = 1;
			}
			disableUnusedStages();
			switchTextureMode(0, modeMap[0]);
			float f2 = -1F;
			if (Config.glFixedBlitting)
				f2 = -1F;
			endState();
			Texture texture = (Texture) aobj[0];
			float f3 = texture.getHeight();
			float f4 = texture.getWidth();
			int i = texture.getOpenGLID(myID);
			if (i == 0 || texture.getMarker(myID) == Texture.MARKER_DELETE_AND_UPLOAD) {
				texture.setMarker(myID, Texture.MARKER_NOTHING);
				if (i != 0) {
					IntBuffer intbuffer = getSmallBuffer();
					intbuffer.put(i);
					intbuffer.flip();
					GL11.glDeleteTextures(intbuffer);
				}
				convertTexture(texture);
				i = texture.getOpenGLID(myID);
			}
			if (i != lastTextures[0]) {
				executeBufferedBlits();
				bindTexture(0, i);
			}
			int j = framebuffer.getSamplingMode();
			float f5 = 1.0F;
			if (j != 0)
				switch (j) {
				case 1: // '\001'
					f5 = 2.0F;
					break;

				case 3: // '\003'
					f5 = 1.5F;
					break;

				case 2: // '\002'
					f5 = 0.5F;
					break;
				}
			float f6 = ((Integer) aobj[2]).intValue();
			float f7 = ((Integer) aobj[3]).intValue();
			int k = ((Integer) aobj[6]).intValue();
			int l = ((Integer) aobj[7]).intValue();
			int i1 = k;
			int j1 = l;
			float f8 = 1.0F;
			float f9 = 1.0F;
			float f10 = 1.0F;
			float f11 = 1.0F;
			if (aobj.length > 11) {
				i1 = ((Integer) aobj[11]).intValue();
				j1 = ((Integer) aobj[12]).intValue();
				f8 = Config.glTransparencyOffset + (float) ((Integer) aobj[13]).intValue() * Config.glTransparencyMul;
				if (f8 > 1.0F)
					f8 = 1.0F;
				f9 = ((Integer) aobj[15]).floatValue() / 255F;
				f10 = ((Integer) aobj[16]).floatValue() / 255F;
				f11 = ((Integer) aobj[17]).floatValue() / 255F;
			}
			int k1 = ((Integer) aobj[4]).intValue();
			int l1 = ((Integer) aobj[5]).intValue();
			float f12 = 1.0F / f4;
			float f13 = 1.0F / f3;
			float f14 = f12 * f6;
			float f15 = f13 * f7;
			float f16 = f12 * ((float) k + f6);
			float f17 = f13 * ((float) l + f7);
			if (myWorld != null) {
				if (f5 != 1.0F) {
					Interact2D.reproject2D3DBlit(f, f1, framebuffer, k1, l1, 1.0F, f5, blitCoords1);
					Interact2D.reproject2D3DBlit(f, f1, framebuffer, k1 + i1, l1 + j1, 1.0F, f5, blitCoords2);
				} else {
					Interact2D.reproject2D3DBlit(f, f1, framebuffer, k1, l1, 1.0F, blitCoords1);
					Interact2D.reproject2D3DBlit(f, f1, framebuffer, k1 + i1, l1 + j1, 1.0F, blitCoords2);
				}
				if (curPos >= 994)
					executeBufferedBlits();
				for (int i2 = 0; i2 < 6; i2++) {
					colors.put(colPos, f9);
					colors.put(colPos + 1, f10);
					colors.put(colPos + 2, f11);
					colors.put(colPos + 3, f8);
					colPos += 4;
				}

				vertices.put(vertPos, blitCoords1.x);
				vertices.put(vertPos + 1, -blitCoords2.y);
				vertices.put(vertPos + 2, f2);
				vertPos += 3;
				vertices.put(vertPos, blitCoords2.x);
				vertices.put(vertPos + 1, -blitCoords2.y);
				vertices.put(vertPos + 2, f2);
				vertPos += 3;
				vertices.put(vertPos, blitCoords1.x);
				vertices.put(vertPos + 1, -blitCoords1.y);
				vertices.put(vertPos + 2, f2);
				vertPos += 3;
				vertices.put(vertPos, blitCoords1.x);
				vertices.put(vertPos + 1, -blitCoords1.y);
				vertices.put(vertPos + 2, f2);
				vertPos += 3;
				vertices.put(vertPos, blitCoords2.x);
				vertices.put(vertPos + 1, -blitCoords1.y);
				vertices.put(vertPos + 2, f2);
				vertPos += 3;
				vertices.put(vertPos, blitCoords2.x);
				vertices.put(vertPos + 1, -blitCoords2.y);
				vertices.put(vertPos + 2, f2);
				vertPos += 3;
				textures.put(texPos, f14);
				textures.put(texPos + 1, f17);
				texPos += 2;
				textures.put(texPos, f16);
				textures.put(texPos + 1, f17);
				texPos += 2;
				textures.put(texPos, f14);
				textures.put(texPos + 1, f15);
				texPos += 2;
				textures.put(texPos, f14);
				textures.put(texPos + 1, f15);
				texPos += 2;
				textures.put(texPos, f16);
				textures.put(texPos + 1, f15);
				texPos += 2;
				textures.put(texPos, f16);
				textures.put(texPos + 1, f17);
				texPos += 2;
				curPos += 6;
			}
			if (renderTarget != null)
				setViewport(framebuffer);
		}
	}

	protected void blit(Object aobj[], float f, float f1) {
		if (init) {
			if (Config.glVertexArrays && Config.glBufferedBlits) {
				blitVA(aobj, f, f1);
				return;
			}
			FrameBuffer framebuffer = (FrameBuffer) aobj[1];
			if (renderTarget != null)
				resetViewport(framebuffer);
			disableUnusedStages();
			switchTextureMode(0, modeMap[0]);
			float f2 = -1F;
			if (Config.glFixedBlitting)
				f2 = -1F;
			endState();
			Texture texture = (Texture) aobj[0];
			float f3 = texture.getHeight();
			float f4 = texture.getWidth();
			int i = texture.getOpenGLID(myID);
			if (i == 0 || texture.getMarker(myID) == Texture.MARKER_DELETE_AND_UPLOAD) {
				texture.setMarker(myID, Texture.MARKER_NOTHING);
				if (i != 0) {
					IntBuffer intbuffer = getSmallBuffer();
					intbuffer.put(i);
					intbuffer.flip();
					GL11.glDeleteTextures(intbuffer);
				}
				convertTexture(texture);
				i = texture.getOpenGLID(myID);
			}
			if (i != lastTextures[0])
				bindTexture(0, i);
			int j = framebuffer.getSamplingMode();
			float f5 = 1.0F;
			if (j != 0)
				switch (j) {
				case 1: // '\001'
					f5 = 2.0F;
					break;

				case 3: // '\003'
					f5 = 1.5F;
					break;

				case 2: // '\002'
					f5 = 0.5F;
					break;
				}
			float f6 = ((Integer) aobj[2]).intValue();
			float f7 = ((Integer) aobj[3]).intValue();
			int k = ((Integer) aobj[6]).intValue();
			int l = ((Integer) aobj[7]).intValue();
			int i1 = k;
			int j1 = l;
			float f8 = 1.0F;
			float f9 = 1.0F;
			float f10 = 1.0F;
			float f11 = 1.0F;
			if (aobj.length > 11) {
				i1 = ((Integer) aobj[11]).intValue();
				j1 = ((Integer) aobj[12]).intValue();
				f8 = Config.glTransparencyOffset + (float) ((Integer) aobj[13]).intValue() * Config.glTransparencyMul;
				if (f8 > 1.0F)
					f8 = 1.0F;
				f9 = ((Integer) aobj[15]).floatValue() / 255F;
				f10 = ((Integer) aobj[16]).floatValue() / 255F;
				f11 = ((Integer) aobj[17]).floatValue() / 255F;
			}
			int k1 = ((Integer) aobj[4]).intValue();
			int l1 = ((Integer) aobj[5]).intValue();
			float f12 = 1.0F / f4;
			float f13 = 1.0F / f3;
			float f14 = f12 * f6;
			float f15 = f13 * f7;
			float f16 = f12 * ((float) k + f6);
			float f17 = f13 * ((float) l + f7);
			if (myWorld != null) {
				if (f5 != 1.0F) {
					Interact2D.reproject2D3DBlit(f, f1, framebuffer, k1, l1, 1.0F, f5, blitCoords1);
					Interact2D.reproject2D3DBlit(f, f1, framebuffer, k1 + i1, l1 + j1, 1.0F, f5, blitCoords2);
				} else {
					Interact2D.reproject2D3DBlit(f, f1, framebuffer, k1, l1, 1.0F, blitCoords1);
					Interact2D.reproject2D3DBlit(f, f1, framebuffer, k1 + i1, l1 + j1, 1.0F, blitCoords2);
				}
				GL11.glBegin(5);
				GL11.glColor4f(f9, f10, f11, f8);
				GL11.glTexCoord2f(f14, f17);
				GL11.glVertex3f(blitCoords1.x, -blitCoords2.y, f2);
				GL11.glTexCoord2f(f16, f17);
				GL11.glVertex3f(blitCoords2.x, -blitCoords2.y, f2);
				GL11.glTexCoord2f(f14, f15);
				GL11.glVertex3f(blitCoords1.x, -blitCoords1.y, f2);
				GL11.glTexCoord2f(f16, f15);
				GL11.glVertex3f(blitCoords2.x, -blitCoords1.y, f2);
				GL11.glEnd();
			}
			if (renderTarget != null)
				setViewport(framebuffer);
		}
	}

	protected void addForUnload(Texture texture) {
		synchronized (this) {
			toUnload.add(texture);
		}
	}

	protected void unloadTextures() {
		if (toUnload.size() > 0)
			synchronized (this) {
				endState();
				Iterator iterator = toUnload.iterator();
				do {
					if (!iterator.hasNext())
						break;
					Texture texture = (Texture) iterator.next();
					if (texture.getOpenGLID(myID) != 0) {
						removeTexture(texture);
						texture.clearIDs(myID);
					}
				} while (true);
				toUnload.clear();
			}
	}

	protected void removeLists() {
		if (toDispose != null && toDispose.size() > 0)
			synchronized (this) {
				GL11.glFlush();
				GL11.glFinish();
				for (int i = 0; i < toDispose.size(); i++)
					try {
						int j = ((Integer) toDispose.get(i)).intValue();
						GL11.glDeleteLists(j, 1);
						Util.checkGLError();
						Logger.log("Display list " + j + " deleted!", 2);
					} catch (Exception exception) {
						Logger.log("Failed to delete display list: " + toDispose.get(i), 1);
					}

				GL11.glFlush();
				GL11.glFinish();
				toDispose.clear();
			}
	}

	private final void createVertexArrays(int i) {
		GL11.glEnableClientState(32888);
		for (int j = 0; j < i; j++) {
			multiTextures[j] = ByteBuffer.allocateDirect(8000).order(ByteOrder.nativeOrder()).asFloatBuffer();
			ARBMultitexture.glClientActiveTextureARB(stageMap[j]);
			GL11.glTexCoordPointer(2, 8, multiTextures[j]);
			if (j == 0) {
				textures = multiTextures[0];
				buffersEnabled[0] = true;
			}
		}

	}

	private final int getTextureStages() {
		IntBuffer intbuffer = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
		GL11.glGetInteger(34018, intbuffer);
		int i = intbuffer.get(0);
		if (Config.glOverrideStageCount > 0)
			i = Config.glOverrideStageCount;
		if (i > 4)
			i = 4;
		return i;
	}

	private final void initTextureStage(int i) {
		initTextureStage(i, modeMap[0]);
	}

	private void resetViewport(FrameBuffer framebuffer) {
		if (xViewStart == 0 && xViewEnd == 0) {
			xViewStart = 0;
			xViewEnd = framebuffer.getOutputWidth();
			yViewStart = 0;
			yViewEnd = framebuffer.getOutputHeight();
		}
		GL11.glViewport(xViewStart, yViewStart, xViewEnd, yViewEnd);
	}

	private void setViewport(FrameBuffer framebuffer) {
		yTargetStart = framebuffer.getOutputHeight() - renderTarget.getHeight();
		GL11.glViewport(0, yTargetStart, renderTarget.getWidth(), renderTarget.getHeight());
	}

	private void enableScissor(FrameBuffer framebuffer, Texture texture, int i, int j, int k, int l) {
		int i1 = 0;
		int j1 = 0;
		if (!useFBO)
			j1 = framebuffer.getOutputHeight() - texture.getHeight();
		int k1 = texture.getWidth();
		int l1 = texture.getHeight();
		if (i != -1) {
			scissorEnabled = true;
			i1 += i;
		}
		if (j != -1) {
			scissorEnabled = true;
			j1 += j;
		}
		if (k != -1) {
			scissorEnabled = true;
			k1 -= i + k;
		}
		if (l != -1) {
			scissorEnabled = true;
			l1 -= j + l;
		}
		if (scissorEnabled) {
			GL11.glEnable(3089);
			if (i1 < 0)
				i1 = 0;
			if (k1 < 0)
				k1 = 0;
			if (j1 < 0)
				j1 = 0;
			if (l1 < 0)
				l1 = 0;
			GL11.glScissor(i1, j1, k1, l1);
		}
	}

	private void disableScissor() {
		if (scissorEnabled) {
			GL11.glDisable(3089);
			scissorEnabled = false;
		}
	}

	static final int MODE_SET_FRUSTUM = 0;
	static final int MODE_SWAP_BUFFERS = 1;
	static final int MODE_CLEAR = 2;
	static final int MODE_PRINT_LOGS = 3;
	static final int MODE_BLIT_TEXTURE = 4;
	static final int MODE_FLUSH = 5;
	static final int MODE_GRAB_SCREEN = 6;
	static final int MODE_BLIT_INT = 7;
	static final int MODE_END_STATE = 8;
	static final int MODE_SET_VIEWPORT = 9;
	static final int MODE_CHECK_EXT = 10;
	static final int MODE_RETURN_CANVAS = 11;
	static final int MODE_START_PAINTING = 12;
	static final int MODE_END_PAINTING = 13;
	static final int MODE_SET_CANVAS_MODE = 14;
	static final int MODE_CLEAR_ZBUFFER = 15;
	static final int MODE_POST_PROCESS = 16;
	static final int MODE_DISPOSE_PROCESSOR = 17;
	static final int MODE_SET_RENDER_TARGET = 18;
	static final int MODE_SET_CAMERA = 21;
	static final int MODE_SET_TEXTURE_PROJECTOR = 22;
	static final int MODE_REINIT = 23;
	static final int MODE_SET_LISTENER_STATE = 24;
	static final int MODE_GET_MAX_TEXTURE_SIZE = 25;
	static final int MODE_ADD_CLIPPING_PLANE = 26;
	static final int MODE_REMOVE_CLIPPING_PLANE = 27;
	static final int MODE_RENDER_TO_TARGET = 1000;
	protected static float COLOR_INV = 0.003921569F;
	protected static final int VERTEX_ARRAY_SIZE = 1000;
	protected static int stageMap[] = { 33984, 33985, 33986, 33987 };
	protected static int modeMap[] = { 8448, 8448, 260, 7681, 3042, 34164, 34023 };
	protected static int blendSrcMap[] = { 0, 0, 1, 1, 0 };
	protected static int blendDstMap[] = { 768, 768, 1, 0, 769 };
	private static final int NO_STATE = 0xfff0bdcb;
	protected static int rendererID = 0;
	protected int myID;
	protected float lastFOV;
	protected boolean lastFOVMode;
	protected boolean init;
	protected int stateChanges;
	protected World myWorld;
	protected IntBuffer pixelBuffer;
	protected int pixelBufferSize;
	protected Texture blitBuffer;
	protected int blitBufferWidth;
	protected int blitBufferHeight;
	protected int currentRGBScaling;
	protected TextureManager texMan;
	protected int curPos;
	protected int colPos;
	protected int vertPos;
	protected int texPos;
	protected int mtTexPos[];
	protected boolean wasTransparent;
	protected int lastTransMode;
	protected boolean vertexArraysInitialized;
	protected int xp;
	protected int yp;
	protected boolean disposed;
	protected IPaintListener listener;
	protected boolean listenerActive;
	protected float scaleX;
	protected float scaleY;
	protected boolean stageInitialized[];
	protected int lastTextures[];
	protected int maxStages;
	protected int lastMultiTextures[];
	protected int lastIDs[];
	protected int lastMultiModes[];
	protected int lastMode[];
	protected int minDriverAndConfig;
	protected int lastCoords;
	protected int veryLastCoords;
	protected int lastTexture;
	protected float lastFarPlane;
	protected float lastNearPlane;
	protected FloatBuffer colors;
	protected FloatBuffer vertices;
	protected FloatBuffer textures;
	protected DoubleBuffer clippingBuffer;
	protected FloatBuffer multiTextures[];
	protected Texture renderTarget;
	protected int yTargetStart;
	protected int xViewStart;
	protected int yViewStart;
	protected int xViewEnd;
	protected int yViewEnd;
	protected Matrix textureScale;
	protected boolean projective[];
	protected boolean alphaTest;
	protected boolean useFBO;
	protected int fbo;
	protected int fboTexture;
	protected boolean fboDepthMode;
	protected Texture fboColorStorage;
	protected Texture fboDepthStorage;
	protected boolean blending;
	private boolean hasOpenGL12;
	private boolean buffersEnabled[];
	private boolean enabledStages[];
	private boolean singleTexturing;
	private int currentFogColor;
	private boolean currentFoggingState;
	private float currentFogDistance;
	private int lastState;
	private int changeCnt;
	private int supportsRGBScaling;
	private int supportsShadowMapping;
	private int textureBufferSize;
	private IntBuffer smallBuffer;
	private ByteBuffer textureBuffer;
	private SimpleVector blitCoords1;
	private SimpleVector blitCoords2;
	private boolean depthBuffer;
	protected boolean blitMode;
	private boolean blitTrans;
	private boolean blitAdditive;
	private boolean scissorEnabled;
	private boolean scissorClearAll;
	private int blitScaling;
	private float floatBuffer[];
	private Map fovMatrixCache;
	private FloatBuffer floatBuffer64;
	private FloatBuffer floatBuffer16;
	protected FloatBuffer ambientBuffer;
	private Set toUnload;
	protected Map matrixCache;
	private java.util.List toDispose;
	private Set toCompileToDL;
	private boolean foggingOn;

}
