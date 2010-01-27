// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct.util;

import com.threed.jpct.*;
import java.awt.Color;

public class LensFlare {

	public LensFlare(SimpleVector simplevector, String s, String s1, String s2, String s3) {
		burst = null;
		halo1 = null;
		halo2 = null;
		halo3 = null;
		sunPos = null;
		types = new Texture[7];
		scales = new float[7][2];
		globalScale = 1.0F;
		trans = 1;
		cover = true;
		maxDistance = -1F;
		revertDirection = false;
		visible = false;
		light2D = null;
		TextureManager texturemanager = TextureManager.getInstance();
		burst = texturemanager.getTexture(s);
		halo1 = texturemanager.getTexture(s1);
		halo2 = texturemanager.getTexture(s2);
		halo3 = texturemanager.getTexture(s3);
		sunPos = new SimpleVector(simplevector);
		types[0] = burst;
		types[1] = halo1;
		types[2] = burst;
		types[3] = halo2;
		types[4] = burst;
		types[5] = halo3;
		types[6] = burst;
		scales[0][0] = 1.0F;
		scales[0][1] = 1.0F;
		scales[1][0] = 2.0F;
		scales[1][1] = 0.5F;
		scales[2][0] = 3F;
		scales[2][1] = 0.25F;
		scales[3][0] = 8F;
		scales[3][1] = 1.0F;
		scales[4][0] = -2F;
		scales[4][1] = 0.5F;
		scales[5][0] = -4F;
		scales[5][1] = 0.25F;
		scales[6][0] = -5.5F;
		scales[6][1] = 0.25F;
	}

	public void setTransparency(int i) {
		trans = i;
	}

	public void setLightPosition(SimpleVector simplevector) {
		sunPos.set(simplevector);
	}

	public void setGlobalScale(float f) {
		globalScale = f;
	}

	public void setHiding(boolean flag) {
		cover = flag;
	}

	public void setMaximumDistance(float f) {
		maxDistance = f;
	}

	public void setDirection(boolean flag) {
		revertDirection = flag;
	}

	public void update(FrameBuffer framebuffer, World world) {
		Camera camera = world.getCamera();
		light2D = Interact2D.project3D2D(camera, framebuffer, sunPos);
		visible = true;
		if (cover) {
			SimpleVector simplevector = camera.getPosition();
			if (!revertDirection) {
				SimpleVector simplevector1 = sunPos.calcSub(simplevector);
				float f = simplevector1.length();
				float f2 = world.calcMinDistance(simplevector, simplevector1.normalize(), maxDistance == -1F ? f * 1.05F : Math
						.min(maxDistance, f * 1.05F));
				visible = f2 == 1E+012F || f2 > f - 5F;
			} else {
				SimpleVector simplevector2 = simplevector.calcSub(sunPos);
				float f1 = simplevector2.length();
				float f3 = world.calcMinDistance(sunPos, simplevector2.normalize(), maxDistance == -1F ? f1 * 1.05F : Math.min(
						maxDistance, f1 * 1.05F));
				visible = f3 == 1E+012F || f3 > f1 - 5F;
			}
		}
	}

	public void render(FrameBuffer framebuffer) {
		if (light2D != null && visible) {
			SimpleVector simplevector = new SimpleVector(light2D);
			float f = framebuffer.getMiddleX();
			float f1 = framebuffer.getMiddleY();
			simplevector.z = 0.0F;
			SimpleVector simplevector1 = new SimpleVector(f, f1, 0.0F);
			SimpleVector simplevector2 = simplevector.calcSub(simplevector1);
			float f2 = simplevector2.length();
			simplevector2 = simplevector2.normalize();
			SimpleVector simplevector3 = new SimpleVector();
			for (int i = 0; i < types.length; i++) {
				simplevector3.set(simplevector2);
				Texture texture = types[i];
				float f3 = scales[i][0];
				float f4 = scales[i][1] * globalScale;
				simplevector3.scalarMul((1.0F / f3) * f2);
				int j = texture.getWidth();
				int k = texture.getHeight();
				int l = (int) (simplevector3.x - (float) (j >> 1) * f4);
				int i1 = (int) (simplevector3.y - (float) (k >> 1) * f4);
				framebuffer.blit(texture, 0, 0, l + (int) f, i1 + (int) f1, j, k, (int) ((float) j * f4),
						(int) ((float) k * f4), trans, true, Color.WHITE);
			}

		}
	}

	private Texture burst;
	private Texture halo1;
	private Texture halo2;
	private Texture halo3;
	private SimpleVector sunPos;
	private Texture types[];
	private float scales[][];
	private float globalScale;
	private int trans;
	private boolean cover;
	private float maxDistance;
	private boolean revertDirection;
	private boolean visible;
	private SimpleVector light2D;
}
