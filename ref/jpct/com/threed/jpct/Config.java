// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.threed.jpct;

import java.lang.reflect.Field;
import java.util.Vector;

// Referenced classes of package com.threed.jpct:
//            Logger, VideoMode

public final class Config {

	private Config() {
	}

	public static String getVersion() {
		return "1.20";
	}

	public static void setParameterValue(String s, Object obj) {
		Field field = null;
		try {
			field = (com.threed.jpct.Config.class).getField(s);
			field.set(com.threed.jpct.Config.class, obj);
		} catch (IllegalArgumentException illegalargumentexception) {
			Logger.log("Wrong parameter value type: " + obj.getClass() + " found, " + field.getType() + " expected!", 0);
		} catch (Exception exception) {
			Logger.log("Unknown parameter: " + s, 0);
		}
	}

	public static Object getParameterValue(String paramString) {
		try {
			Field localField = Config.class.getField(paramString);
			return localField.get(Config.class);
		} catch (Exception localException) {
		}
		return null;
	}

	public static String[] getParameterNames() {
		Field afield[] = (com.threed.jpct.Config.class).getFields();
		Vector vector = new Vector();
		int i = 0;
		for (int j = 0; j < afield.length; j++) {
			String s = afield[j].getName();
			if (!s.equals("VERSION")) {
				vector.addElement(s);
				i++;
			}
		}

		return (String[]) vector.toArray(new String[vector.size()]);
	}

	public static void tuneForIndoor() {
		isIndoor = true;
		zTrick = true;
		doPortalHsr = false;
		useBB = true;
		useFastCollisionDetection = true;
		useFastSpecular = true;
		useFrustumCulling = true;
		spanBasedHsr = true;
		doSorting = true;
		texelFilter = true;
	}

	public static void tuneForOutdoor() {
		isIndoor = false;
		zTrick = false;
		doPortalHsr = false;
		useBB = true;
		useFastCollisionDetection = true;
		useFastSpecular = true;
		useFrustumCulling = true;
		spanBasedHsr = true;
		doSorting = true;
		texelFilter = true;
	}

	public static void glSetDesiredVideoMode(int i, int j, int k, boolean flag) {
		glColorDepth = i;
		glFullscreen = flag;
		glRefresh = k;
		glZBufferDepth = j;
	}

	public static void glSetDesiredVideoMode(VideoMode videomode, boolean flag) {
		glSetDesiredVideoMode(videomode.bpp, videomode.depth, videomode.refresh, flag);
	}

	static Boolean booleanValueOf(boolean flag) {
		return flag ? Boolean.TRUE : Boolean.FALSE;
	}

	static final int BLACK_MASK = 0xf0f0f0;
	static final float ADD_Z_SORT = 3000000F;
	/**
	 * @deprecated Field VERSION is deprecated
	 */
	public static final String VERSION = "1.20";
	private static final String INT_VERSION = "1.20";
	public static boolean saveMemory = true;
	public static boolean shareVisibilityList = false;
	public static int polygonBufferSize = -1;
	public static boolean useLocking = false;
	public static int lockingTimer = 0;
	public static float defaultCameraFOV = 1.25F;
	public static boolean autoMaintainAspectRatio = true;
	public static boolean neverUseBufferedImage = false;
	public static boolean useFramebufferWithAlpha = false;
	public static int maxTextures = 256;
	public static int maxPolysVisible = 4096;
	public static int maxLights = 32;
	public static int maxTextureLayers = 16;
	public static int maxAnimationSubSequences = 20;
	public static float collideOffset = 40F;
	public static float collideEdgeMul = 1.0F;
	static final float COLLIDE_CAMERA_RANGE = 3F;
	public static float collideSectorOffset = 3F;
	public static float collideEllipsoidThreshold = 0.1F;
	public static boolean collideEllipsoidSmoothing = true;
	public static boolean useFastCollisionDetection = true;
	public static float nearPlane = 1.0F;
	public static float farPlane = 1000F;
	public static float viewportOffsetX = 0.0F;
	public static float viewportOffsetY = 0.0F;
	public static int maxPortalCoords = 8;
	public static int maxPortals = 0;
	public static int maxParentObjects = 2;
	public static float sectorRange = 30F;
	public static boolean useFastSpecular = true;
	public static boolean gouraud = true;
	public static boolean fadeoutLight = true;
	public static int lightMul = 10;
	public static float linearDiv = 50F;
	public static float specTerm = 10F;
	public static float specPow = 6F;
	public static float lightDiscardDistance = -1F;
	/**
	 * @deprecated Field blur is deprecated
	 */
	public static boolean blur = false;
	public static boolean optiZ = true;
	public static boolean zTrick = false;
	public static boolean spanBasedHsr = true;
	public static boolean texelFilter = true;
	public static boolean isIndoor = false;
	public static boolean doPortalHsr = false;
	public static boolean doSorting = true;
	public static boolean alwaysSort = true;
	public static boolean useFrustumCulling = true;
	public static boolean useBB = true;
	public static int optimizeNormalCalcTH = 1500;
	public static boolean useMultipleThreads = false;
	public static boolean useMultiThreadedBlitting = false;
	public static int loadBalancingStrategy = 0;
	public static int maxNumberOfCores = 4;
	public static boolean mtDebug = false;
	public static float glTransparencyOffset = 0.7F;
	public static float glTransparencyMul = 0.06F;
	public static float glShadowZBias = 0.5F;
	public static boolean glFullscreen = false;
	public static int glColorDepth = 32;
	public static int glZBufferDepth = 24;
	public static int glRefresh = 60;
	public static int glOverrideStageCount = -1;
	public static boolean glMultiPassSorting = true;
	public static Object glAdditionalConfiguration[] = null;
	static int glStageCount = 1;
	public static boolean glMipmap = true;
	public static boolean mipmap = false;
	public static boolean glTrilinear = false;
	public static int glTextureDepth = 32;
	public static boolean glTriangleStrips = true;
	public static boolean glVertexArrays = true;
	public static String glWindowName = "jPCT - http://www.jpct.net";
	public static boolean glForceFinish = false;
	public static boolean glForceEnvMapToSecondStage = false;
	public static boolean glFixedBlitting = true;
	public static boolean glBufferedBlits = false;
	public static boolean glVerbose = false;
	public static boolean glAvoidTextureCopies = false;
	public static int glAWTCommandQueueSize = 1000;
	public static int glAWTCommandQueueCleanup = 10000;
	public static boolean glSkipInitialization = false;
	public static boolean glUseIgnorantBlits = false;
	public static boolean glUseCaches = true;
	public static boolean glUseFBO = true;
	public static boolean glIgnoreAlphaBlendingFBO = false;
	public static boolean glRevertADDtoMODULATE = false;
	public static boolean glFlipRenderTargets = false;
	public static boolean glBlendingAffectsAlpha = true;
	public static boolean glVSync = false;
	public static boolean glUseUnappropriateModes = false;
	public static int glBatchSize = 8000;
	public static int glDynamicBatchSize = 333;
	public static boolean glIgnoreNearPlane = true;
	public static int polygonIDLimit = 50;
	public static boolean useMultipassStriping = true;
	public static boolean oldStyle3DSLoader = false;
	public static boolean oldStyleBillBoarding = false;
	public static boolean autoBuild = false;
	static int loadMaxVerticesASC = 0x101d0;
	static int loadMaxTrianglesASC = 0x101d0;
	static int loadMaxVertices3DS = 0x101d0;
	static int loadMaxTriangles3DS = 0x101d0;
	static final boolean FRAME_NOTIFICATION = false;

}
