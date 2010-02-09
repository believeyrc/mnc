// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Referenced classes of package com.threed.jpct:
//            Texture, TextureManager, VisList, Object3D, 
//            IntegerC, Mesh, Vectors, Config

class AWTBufferList implements Serializable {

	AWTBufferList(int i) {
		u1Mul0 = null;
		u2Mul0 = null;
		u3Mul0 = null;
		v1Mul0 = null;
		v2Mul0 = null;
		v3Mul0 = null;
		u1Mul1 = null;
		u2Mul1 = null;
		u3Mul1 = null;
		v1Mul1 = null;
		v2Mul1 = null;
		v3Mul1 = null;
		u1Mul2 = null;
		u2Mul2 = null;
		u3Mul2 = null;
		v1Mul2 = null;
		v2Mul2 = null;
		v3Mul2 = null;
		lights = null;
		anzPoly = 0;
		size = 0;
		texMan = TextureManager.getInstance();
		cis = new ArrayList();
		alreadyCopied = new HashMap();
		obj2Matrix = new HashMap();
		resize(i);
	}

	private void resize(int i) {
		size = i;
		texture = new Texture[size];
		trans = new boolean[size];
		transValue = new int[size];
		stageCnt = new int[size];
		x1 = new float[size];
		y1 = new float[size];
		z1 = new float[size];
		u1 = new float[size];
		v1 = new float[size];
		x2 = new float[size];
		y2 = new float[size];
		z2 = new float[size];
		u2 = new float[size];
		v2 = new float[size];
		x3 = new float[size];
		y3 = new float[size];
		z3 = new float[size];
		u3 = new float[size];
		v3 = new float[size];
		r1 = new float[size];
		g1 = new float[size];
		b1 = new float[size];
		r2 = new float[size];
		g2 = new float[size];
		b2 = new float[size];
		r3 = new float[size];
		g3 = new float[size];
		b3 = new float[size];
		if (a1 != null) {
			a1 = new float[size];
			a2 = new float[size];
			a3 = new float[size];
		}
		multi = new boolean[size];
		maxStages = new int[size];
		multiTextures = (Texture[][]) null;
	}

	final void clear() {
		alreadyCopied.clear();
		cis.clear();
		obj2Matrix.clear();
	}

	final void fill(VisList vislist) {
		anzPoly = vislist.anzpoly + 1;
		Vectors vectors = null;
		Object obj = null;
		Texture atexture[] = texMan.textures;
		Object3D object3d1 = null;
		Object3D aobject3d[] = vislist.vorg;
		int ai[] = vislist.vnum;
		int ai1[] = vislist.vnumOrg;
		int ai2[] = null;
		int ai3[] = null;
		boolean flag = false;
		int j = 0;
		int k = 0;
		int ai4[][] = (int[][]) null;
		float af[] = null;
		float af1[] = null;
		float af2[] = null;
		float af3[] = null;
		float af4[] = null;
		float af5[] = null;
		float af6[] = null;
		int ai5[][] = (int[][]) null;
		int ai6[] = null;
		boolean flag1 = false;
		float af7[] = null;
		float af8[] = null;
		int ai7[][] = (int[][]) null;
		int ai8[][] = (int[][]) null;
		float af9[] = null;
		float af10[] = null;
		float af11[] = null;
		float af12[] = null;
		float af13[] = null;
		float af14[] = null;
		int l = 0;
		boolean flag3 = false;
		boolean flag4 = false;
		java.awt.Color color = java.awt.Color.WHITE;
		int i1 = 0;
		float af15[][] = (float[][]) null;
		for (int j1 = 0; j1 < anzPoly; j1++) {
			Object3D object3d = aobject3d[j1];
			if (object3d != object3d1) {
				object3d1 = object3d;
				flag4 = object3d.isCompiled();
				if (flag4) {
					if (!alreadyCopied.containsKey(object3d))
						synchronized (object3d.compiled) {
							i1 = cis.size();
							alreadyCopied.put(object3d, IntegerC.valueOf(i1));
							cis.addAll(object3d.compiled);
						}
					else
						i1 = ((Integer) alreadyCopied.get(object3d)).intValue();
					obj2Matrix.put(object3d, object3d.transBuffer);
					float af16[][] = object3d.nearestLights;
					af15 = new float[af16.length][af16[0].length];
					for (int l1 = 0; l1 < af16.length; l1++)
						System.arraycopy(af16[l1], 0, af15[l1], 0, af16[l1].length);

				}
				ai2 = object3d.basemap;
				ai3 = object3d.texture;
				flag = object3d.isTrans & (!object3d.isBumpmapped || !object3d.isEnvmapped);
				j = object3d.transValue;
				k = object3d.transMode;
				vectors = object3d.objVectors;
				Mesh mesh = object3d.objMesh;
				ai4 = mesh.points;
				ai6 = mesh.coords;
				af = vectors.xTr;
				af1 = vectors.yTr;
				af2 = vectors.zTr;
				color = object3d.getAdditionalColor();
				af3 = vectors.srOrg;
				af4 = vectors.sgOrg;
				af5 = vectors.sbOrg;
				af6 = vectors.alpha;
				ai5 = object3d.multiTex;
				af7 = vectors.nuOrg;
				af8 = vectors.nvOrg;
				ai7 = object3d.multiTex;
				ai8 = object3d.multiMode;
				flag3 = object3d.usesMultiTexturing;
				if (flag3) {
					l = vectors.uMul.length;
					af9 = vectors.uMul[0];
					af10 = vectors.vMul[0];
					if (l > 1) {
						af11 = vectors.uMul[1];
						af12 = vectors.vMul[1];
						if (l > 2) {
							af13 = vectors.uMul[2];
							af14 = vectors.vMul[2];
						}
					}
				}
			}
			int k1 = 0;
			int i2 = 0;
			if (vislist.splitted) {
				k1 = vislist.stageCnt[j1];
				i2 = (k1 & 0xffff) - 1;
			}
			stageCnt[j1] = k1;
			int i = ai[j1];
			boolean flag2 = object3d.isEnvmapped && !object3d.isBlended && k1 <= 0;
			boolean flag5 = flag2 && Config.glForceEnvMapToSecondStage;
			flag2 = flag2 && (!Config.glForceEnvMapToSecondStage || !flag3);
			if (object3d.isBlended)
				texture[j1] = atexture[ai2[i]];
			else if (k1 < 1)
				texture[j1] = atexture[ai3[i]];
			else
				texture[j1] = atexture[ai7[i2][i]];
			int ai9[] = ai4[ai1[j1]];
			int j2 = ai9[0];
			int k2 = ai9[1];
			int l2 = ai9[2];
			int i3 = ai6[j2];
			int j3 = ai6[k2];
			int k3 = ai6[l2];
			x1[j1] = af[i3];
			y1[j1] = af1[i3];
			z1[j1] = af2[i3];
			x2[j1] = af[j3];
			y2[j1] = af1[j3];
			z2[j1] = af2[j3];
			x3[j1] = af[k3];
			y3[j1] = af1[k3];
			z3[j1] = af2[k3];
			if (!flag4) {
				if (k1 < 1) {
					r1[j1] = af3[j2];
					r2[j1] = af3[k2];
					r3[j1] = af3[l2];
					g1[j1] = af4[j2];
					g2[j1] = af4[k2];
					g3[j1] = af4[l2];
					b1[j1] = af5[j2];
					b2[j1] = af5[k2];
					b3[j1] = af5[l2];
				} else {
					r1[j1] = 255F;
					r2[j1] = 255F;
					r3[j1] = 255F;
					g1[j1] = 255F;
					g2[j1] = 255F;
					g3[j1] = 255F;
					b1[j1] = 255F;
					b2[j1] = 255F;
					b3[j1] = 255F;
				}
				if (af6 != null) {
					if (a1 == null) {
						a1 = new float[size];
						a2 = new float[size];
						a3 = new float[size];
						for (int l3 = 0; l3 < size; l3++) {
							a1[l3] = -1F;
							a2[l3] = -1F;
							a3[l3] = -1F;
						}

					}
					a1[j1] = af6[j2];
					a2[j1] = af6[k2];
					a3[j1] = af6[l2];
				} else if (a1 != null) {
					a1[j1] = -1F;
					a2[j1] = -1F;
					a3[j1] = -1F;
				}
			} else {
				int i4 = color.getRed();
				int k4 = color.getGreen();
				int i5 = color.getBlue();
				r1[j1] = i4;
				r2[j1] = vislist.portalNum[j1] + i1;
				g1[j1] = k4;
				b1[j1] = i5;
				if (lights == null)
					lights = new Object[size];
				lights[j1] = af15;
				r3[j1] = -1000000F;
			}
			if (!flag4)
				if (flag2) {
					float f = 1.0F / (float) texture[j1].width;
					float f1 = 1.0F / (float) texture[j1].height;
					u1[j1] = vectors.eu[j2] * f;
					v1[j1] = vectors.ev[j2] * f1;
					u2[j1] = vectors.eu[k2] * f;
					v2[j1] = vectors.ev[k2] * f1;
					u3[j1] = vectors.eu[l2] * f;
					v3[j1] = vectors.ev[l2] * f1;
				} else if (k1 < 1) {
					u1[j1] = af7[j2];
					v1[j1] = af8[j2];
					u2[j1] = af7[k2];
					v2[j1] = af8[k2];
					u3[j1] = af7[l2];
					v3[j1] = af8[l2];
				} else {
					u1[j1] = vectors.uMul[i2][j2];
					v1[j1] = vectors.vMul[i2][j2];
					u2[j1] = vectors.uMul[i2][k2];
					v2[j1] = vectors.vMul[i2][k2];
					u3[j1] = vectors.uMul[i2][l2];
					v3[j1] = vectors.vMul[i2][l2];
				}
			if (k1 < 1) {
				trans[j1] = flag;
				transValue[j1] = j | k << 16;
			} else {
				trans[j1] = false;
				transValue[j1] = -1;
			}
			if (k1 == 0) {
				multi[j1] = flag3;
				maxStages[j1] = object3d.maxStagesUsed;
				if (!flag3)
					continue;
				if (multiTextures == null) {
					multiTextures = new Texture[3][size];
					u1Mul = new float[3][size];
					v1Mul = new float[3][size];
					u2Mul = new float[3][size];
					v2Mul = new float[3][size];
					u3Mul = new float[3][size];
					v3Mul = new float[3][size];
					multiMode = new int[3][size];
					u1Mul0 = u1Mul[0];
					u2Mul0 = u2Mul[0];
					u3Mul0 = u3Mul[0];
					v1Mul0 = v1Mul[0];
					v2Mul0 = v2Mul[0];
					v3Mul0 = v3Mul[0];
					u1Mul1 = u1Mul[1];
					u2Mul1 = u2Mul[1];
					u3Mul1 = u3Mul[1];
					v1Mul1 = v1Mul[1];
					v2Mul1 = v2Mul[1];
					v3Mul1 = v3Mul[1];
					u1Mul2 = u1Mul[2];
					u2Mul2 = u2Mul[2];
					u3Mul2 = u3Mul[2];
					v1Mul2 = v1Mul[2];
					v2Mul2 = v2Mul[2];
					v3Mul2 = v3Mul[2];
				}
				if (ai7[0][i] == -1) {
					multi[j1] = false;
					maxStages[j1] = 1;
					continue;
				}
				int j4 = ai5[0][i];
				int l4 = ai8[0][i];
				if (Config.glRevertADDtoMODULATE && (l4 == 2 || l4 == 5 || l4 == 6))
					multiMode[0][j1] = 1;
				else
					multiMode[0][j1] = ai8[0][i];
				if (j4 != -1) {
					Texture texture1 = atexture[j4];
					if (!texture1.enabled)
						texture1 = null;
					multiTextures[0][j1] = texture1;
					if (!flag4)
						if (flag5 && texture1 != null) {
							float f2 = 1.0F / (float) texture[j1].width;
							float f3 = 1.0F / (float) texture[j1].height;
							u1Mul0[j1] = vectors.eu[j2] * f2;
							v1Mul0[j1] = vectors.ev[j2] * f3;
							u2Mul0[j1] = vectors.eu[k2] * f2;
							v2Mul0[j1] = vectors.ev[k2] * f3;
							u3Mul0[j1] = vectors.eu[l2] * f2;
							v3Mul0[j1] = vectors.ev[l2] * f3;
						} else {
							u1Mul0[j1] = af9[j2];
							v1Mul0[j1] = af10[j2];
							u2Mul0[j1] = af9[k2];
							v2Mul0[j1] = af10[k2];
							u3Mul0[j1] = af9[l2];
							v3Mul0[j1] = af10[l2];
						}
				} else {
					multiTextures[0][j1] = null;
				}
				if (l > 1) {
					j4 = ai5[1][i];
					l4 = ai8[1][i];
					if (Config.glRevertADDtoMODULATE && (l4 == 2 || l4 == 5 || l4 == 6))
						multiMode[1][j1] = 1;
					else
						multiMode[1][j1] = ai8[1][i];
					if (j4 != -1) {
						Texture texture2 = atexture[j4];
						if (!texture2.enabled)
							texture2 = null;
						multiTextures[1][j1] = texture2;
						if (!flag4) {
							u1Mul1[j1] = af11[j2];
							v1Mul1[j1] = af12[j2];
							u2Mul1[j1] = af11[k2];
							v2Mul1[j1] = af12[k2];
							u3Mul1[j1] = af11[l2];
							v3Mul1[j1] = af12[l2];
						}
					} else {
						multiTextures[1][j1] = null;
					}
				}
				if (l <= 2)
					continue;
				j4 = ai5[2][i];
				l4 = ai8[2][i];
				if (Config.glRevertADDtoMODULATE && (l4 == 2 || l4 == 5 || l4 == 6))
					multiMode[2][j1] = 1;
				else
					multiMode[2][j1] = ai8[2][i];
				if (j4 != -1) {
					Texture texture3 = atexture[j4];
					if (!texture3.enabled)
						texture3 = null;
					multiTextures[2][j1] = texture3;
					if (!flag4) {
						u1Mul2[j1] = af13[j2];
						v1Mul2[j1] = af14[j2];
						u2Mul2[j1] = af13[k2];
						v2Mul2[j1] = af14[k2];
						u3Mul2[j1] = af13[l2];
						v3Mul2[j1] = af14[l2];
					}
				} else {
					multiTextures[2][j1] = null;
				}
				continue;
			}
			multi[j1] = false;
			maxStages[j1] = 1;
			if (multiMode == null)
				multiMode = new int[3][size];
			if (k1 > 0)
				multiMode[0][j1] = ai8[i2][i];
			else
				multiMode[0][j1] = -1;
		}

	}

	private static final long serialVersionUID = 1L;
	Texture texture[];
	Texture multiTextures[][];
	int multiMode[][];
	int stageCnt[];
	boolean trans[];
	int transValue[];
	boolean multi[];
	int maxStages[];
	float x1[];
	float y1[];
	float z1[];
	float u1[];
	float v1[];
	float x2[];
	float y2[];
	float z2[];
	float u2[];
	float v2[];
	float x3[];
	float y3[];
	float z3[];
	float u3[];
	float v3[];
	float r1[];
	float g1[];
	float b1[];
	float r2[];
	float g2[];
	float b2[];
	float r3[];
	float g3[];
	float b3[];
	float a1[];
	float a2[];
	float a3[];
	float u1Mul[][];
	float u2Mul[][];
	float u3Mul[][];
	float v1Mul[][];
	float v2Mul[][];
	float v3Mul[][];
	float u1Mul0[];
	float u2Mul0[];
	float u3Mul0[];
	float v1Mul0[];
	float v2Mul0[];
	float v3Mul0[];
	float u1Mul1[];
	float u2Mul1[];
	float u3Mul1[];
	float v1Mul1[];
	float v2Mul1[];
	float v3Mul1[];
	float u1Mul2[];
	float u2Mul2[];
	float u3Mul2[];
	float v1Mul2[];
	float v2Mul2[];
	float v3Mul2[];
	Object lights[];
	int anzPoly;
	int size;
	private TextureManager texMan;
	List cis;
	Map alreadyCopied;
	Map obj2Matrix;
}
