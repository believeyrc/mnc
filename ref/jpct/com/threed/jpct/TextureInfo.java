// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

// Referenced classes of package com.threed.jpct:
//            Config, Logger

public class TextureInfo {

	public TextureInfo(int i, float f, float f1, float f2, float f3, float f4, float f5) {
		textures = new int[Config.maxTextureLayers];
		mode = new int[Config.maxTextureLayers];
		u0 = new float[Config.maxTextureLayers];
		v0 = new float[Config.maxTextureLayers];
		u1 = new float[Config.maxTextureLayers];
		v1 = new float[Config.maxTextureLayers];
		u2 = new float[Config.maxTextureLayers];
		v2 = new float[Config.maxTextureLayers];
		stageCnt = 0;
		add(i, f, f1, f2, f3, f4, f5, 0);
	}

	public TextureInfo(int i) {
		textures = new int[Config.maxTextureLayers];
		mode = new int[Config.maxTextureLayers];
		u0 = new float[Config.maxTextureLayers];
		v0 = new float[Config.maxTextureLayers];
		u1 = new float[Config.maxTextureLayers];
		v1 = new float[Config.maxTextureLayers];
		u2 = new float[Config.maxTextureLayers];
		v2 = new float[Config.maxTextureLayers];
		stageCnt = 0;
		add(i, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0);
	}

	public void add(int i, int j) {
		add(i, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, j);
	}

	public void add(int i, float f, float f1, float f2, float f3, float f4, float f5, int j) {
		if (j == 0 && stageCnt != 0) {
			Logger.log("Wrong mode for texture blending!", 0);
			return;
		}
		if (stageCnt >= Config.maxTextureLayers) {
			Logger.log("Maximum number of texture layer configured is " + Config.maxTextureLayers + "!", 1);
			return;
		} else {
			textures[stageCnt] = i;
			u0[stageCnt] = f;
			v0[stageCnt] = f1;
			u1[stageCnt] = f2;
			v1[stageCnt] = f3;
			u2[stageCnt] = f4;
			v2[stageCnt] = f5;
			mode[stageCnt] = j;
			stageCnt++;
			return;
		}
	}

	private static final int MODE_BASE = 0;
	public static final int MODE_MODULATE = 1;
	public static final int MODE_ADD = 2;
	public static final int MODE_REPLACE = 3;
	public static final int MODE_BLEND = 4;
	public static final int MODE_ADD_SIGNED = 5;
	public static final int MODE_SUBTRACT = 6;
	public static final int MAX_PHYSICAL_TEXTURE_STAGES = 4;
	int textures[];
	int mode[];
	float u0[];
	float v0[];
	float u1[];
	float v1[];
	float u2[];
	float v2[];
	int stageCnt;
}
