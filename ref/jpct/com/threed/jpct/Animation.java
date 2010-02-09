// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            Mesh, Config, Logger, Object3D, 
//            Matrix

public final class Animation implements Serializable {

	public Animation(int i) {
		maxFrames = i;
		keyFrames = new Mesh[i];
		endFrame = 0;
		aktFrames = 0;
		hermiteBias = 0.0F;
		hermiteTension = 0.0F;
		mode = 0;
		anzAnim = 0;
		wrapMode = 0;
		startFrames = new int[Config.maxAnimationSubSequences];
		endFrames = new int[Config.maxAnimationSubSequences];
		seqNames = new String[Config.maxAnimationSubSequences];
		for (int j = 0; j < Config.maxAnimationSubSequences; j++) {
			startFrames[j] = -1;
			endFrames[j] = -1;
			seqNames[j] = null;
		}

	}

	public void strip() {
		for (int i = 0; i < aktFrames; i++)
			keyFrames[i].strip();

	}

	public int[] getSequenceBorders(int i) {
		return (new int[] { startFrames[i], endFrames[i] });
	}

	public Mesh[] getKeyFrames() {
		Mesh amesh[] = new Mesh[aktFrames];
		System.arraycopy(keyFrames, 0, amesh, 0, aktFrames);
		return amesh;
	}

	public int createSubSequence(String s) {
		if (anzAnim + 1 < Config.maxAnimationSubSequences) {
			anzAnim++;
			startFrames[anzAnim] = aktFrames;
			seqNames[anzAnim] = s;
			startFrames[0] = 0;
			endFrames[0] = 0;
			seqNames[0] = "complete";
		} else {
			Logger.log("Too many sub-sequences defined. Modify configuration to allow a higher number.", 0);
		}
		return anzAnim;
	}

	public int getSequenceCount() {
		return anzAnim;
	}

	public void setClampingMode(int i) {
		if (i != 0 && i != 1)
			Logger.log("Clamping-mode not supported!", 0);
		else
			wrapMode = i;
	}

	public void addKeyFrame(Mesh mesh) {
		if (anzAnim == 0)
			Logger.log("Can't add a keyframe without a sub-sequence being created!", 0);
		else if (mesh.obbStart != 0) {
			if (aktFrames < maxFrames) {
				keyFrames[aktFrames] = mesh;
				aktFrames++;
				endFrames[anzAnim] = aktFrames;
				endFrame = aktFrames;
			} else {
				Logger.log("Too many keyframes defined!", 0);
			}
		} else {
			Logger.log("Bounding box missing in this mesh!", 0);
		}
		endFrames[0] = endFrame;
	}

	public void setInterpolationMethod(int i) {
		mode = i;
	}

	public void setHermiteParameter(float f, float f1) {
		hermiteBias = f;
		hermiteTension = f1;
	}

	void rotateMesh(Matrix matrix, float f, float f1, float f2, float f3) {
		for (int i = 0; i < aktFrames; i++)
			keyFrames[i].rotateMesh(matrix, f, f1, f2, f3);

	}

	void translateMesh(Matrix matrix, Matrix matrix1) {
		for (int i = 0; i < aktFrames; i++)
			keyFrames[i].translateMesh(matrix, matrix1);

	}

	void doAnimation(Object3D object3d, int i, float f) {
		if (i <= anzAnim) {
			if (f > 1.0F)
				f = 1.0F;
			else if (f < 0.0F)
				f = 0.0F;
			int j = endFrames[i];
			int k = startFrames[i];
			float f1 = f * (float) (j - k - wrapMode) + (float) k;
			if (wrapMode == 1)
				if (f1 >= (float) j)
					f1 = j - 1;
				else if (f1 < (float) k)
					f1 = k;
			int l = (int) f1;
			float f2 = f1 - (float) l;
			switch (mode) {
			case 0: // '\0'
				interpolateLinear(object3d, l, f2, k, j);
				break;

			case 1: // '\001'
				interpolateCosine(object3d, l, f2, k, j);
				break;

			case 2: // '\002'
				interpolateBiCubic(object3d, l, f2, k, j);
				break;

			case 3: // '\003'
				interpolateHermite(object3d, l, f2, k, j);
				break;

			case 4: // '\004'
				interpolateNone(object3d, l, k, j);
				break;

			default:
				Logger.log("Unsupported interpolation mode used!", 0);
				break;
			}
		} else {
			Logger.log("Sub-sequence number " + i + " doesn't exist!", 0);
		}
	}

	void interpolateHermite(Object3D object3d, int i, float f, int j, int k) {
		int l = i - 1;
		int i1 = i;
		int j1 = i + 1;
		int k1 = i + 2;
		if (wrapMode == 1) {
			if (k1 >= k)
				k1 = k - 1;
			else if (k1 < j)
				k1 = j;
			if (l >= k)
				l = k - 1;
			else if (l < j)
				l = j;
			if (i1 >= k)
				i1 = k - 1;
			else if (i1 < j)
				i1 = j;
			if (j1 >= k)
				j1 = k - 1;
			else if (j1 < j)
				j1 = j;
		} else {
			if (k1 >= k)
				k1 = j + 1;
			else if (k1 < j)
				k1 = k - 1;
			if (l >= k)
				l = j;
			else if (l < j)
				l = k - 2;
			if (i1 >= k)
				i1 = j;
			else if (i1 < j)
				i1 = k - 1;
			if (j1 >= k)
				j1 = j;
			else if (j1 < j)
				j1 = k - 1;
		}
		float f3 = f * f;
		float f4 = f3 * f;
		int l1 = object3d.objMesh.anzCoords;
		for (int i2 = 0; i2 < l1; i2++) {
			float f1 = ((keyFrames[i1].xOrg[i2] - keyFrames[l].xOrg[i2]) * (1.0F + hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f1 += ((keyFrames[j1].xOrg[i2] - keyFrames[i1].xOrg[i2]) * (1.0F - hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			float f2 = ((keyFrames[j1].xOrg[i2] - keyFrames[i1].xOrg[i2]) * (1.0F + hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f2 += ((keyFrames[k1].xOrg[i2] - keyFrames[j1].xOrg[i2]) * (1.0F - hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			float f5 = (2.0F * f4 - 3F * f3) + 1.0F;
			float f6 = (f4 - 2.0F * f3) + f;
			float f7 = f4 - f3;
			float f8 = -2F * f4 + 3F * f3;
			object3d.objMesh.xOrg[i2] = f5 * keyFrames[i1].xOrg[i2] + f6 * f1 + f7 * f2 + f8 * keyFrames[j1].xOrg[i2];
			f1 = ((keyFrames[i1].yOrg[i2] - keyFrames[l].yOrg[i2]) * (1.0F + hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f1 += ((keyFrames[j1].yOrg[i2] - keyFrames[i1].yOrg[i2]) * (1.0F - hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f2 = ((keyFrames[j1].yOrg[i2] - keyFrames[i1].yOrg[i2]) * (1.0F + hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f2 += ((keyFrames[k1].yOrg[i2] - keyFrames[j1].yOrg[i2]) * (1.0F - hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f5 = (2.0F * f4 - 3F * f3) + 1.0F;
			f6 = (f4 - 2.0F * f3) + f;
			f7 = f4 - f3;
			f8 = -2F * f4 + 3F * f3;
			object3d.objMesh.yOrg[i2] = f5 * keyFrames[i1].yOrg[i2] + f6 * f1 + f7 * f2 + f8 * keyFrames[j1].yOrg[i2];
			f1 = ((keyFrames[i1].zOrg[i2] - keyFrames[l].zOrg[i2]) * (1.0F + hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f1 += ((keyFrames[j1].zOrg[i2] - keyFrames[i1].zOrg[i2]) * (1.0F - hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f2 = ((keyFrames[j1].zOrg[i2] - keyFrames[i1].zOrg[i2]) * (1.0F + hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f2 += ((keyFrames[k1].zOrg[i2] - keyFrames[j1].zOrg[i2]) * (1.0F - hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f5 = (2.0F * f4 - 3F * f3) + 1.0F;
			f6 = (f4 - 2.0F * f3) + f;
			f7 = f4 - f3;
			f8 = -2F * f4 + 3F * f3;
			object3d.objMesh.zOrg[i2] = f5 * keyFrames[i1].zOrg[i2] + f6 * f1 + f7 * f2 + f8 * keyFrames[j1].zOrg[i2];
			f1 = ((keyFrames[i1].nxOrg[i2] - keyFrames[l].nxOrg[i2]) * (1.0F + hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f1 += ((keyFrames[j1].nxOrg[i2] - keyFrames[i1].nxOrg[i2]) * (1.0F - hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f2 = ((keyFrames[j1].nxOrg[i2] - keyFrames[i1].nxOrg[i2]) * (1.0F + hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f2 += ((keyFrames[k1].nxOrg[i2] - keyFrames[j1].nxOrg[i2]) * (1.0F - hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f5 = (2.0F * f4 - 3F * f3) + 1.0F;
			f6 = (f4 - 2.0F * f3) + f;
			f7 = f4 - f3;
			f8 = -2F * f4 + 3F * f3;
			object3d.objMesh.nxOrg[i2] = f5 * keyFrames[i1].nxOrg[i2] + f6 * f1 + f7 * f2 + f8 * keyFrames[j1].nxOrg[i2];
			f1 = ((keyFrames[i1].nyOrg[i2] - keyFrames[l].nyOrg[i2]) * (1.0F + hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f1 += ((keyFrames[j1].nyOrg[i2] - keyFrames[i1].nyOrg[i2]) * (1.0F - hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f2 = ((keyFrames[j1].nyOrg[i2] - keyFrames[i1].nyOrg[i2]) * (1.0F + hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f2 += ((keyFrames[k1].nyOrg[i2] - keyFrames[j1].nyOrg[i2]) * (1.0F - hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f5 = (2.0F * f4 - 3F * f3) + 1.0F;
			f6 = (f4 - 2.0F * f3) + f;
			f7 = f4 - f3;
			f8 = -2F * f4 + 3F * f3;
			object3d.objMesh.nyOrg[i2] = f5 * keyFrames[i1].nyOrg[i2] + f6 * f1 + f7 * f2 + f8 * keyFrames[j1].nyOrg[i2];
			f1 = ((keyFrames[i1].nzOrg[i2] - keyFrames[l].nzOrg[i2]) * (1.0F + hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f1 += ((keyFrames[j1].nzOrg[i2] - keyFrames[i1].nzOrg[i2]) * (1.0F - hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f2 = ((keyFrames[j1].nzOrg[i2] - keyFrames[i1].nzOrg[i2]) * (1.0F + hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f2 += ((keyFrames[k1].nzOrg[i2] - keyFrames[j1].nzOrg[i2]) * (1.0F - hermiteBias) * (1.0F - hermiteTension)) / 2.0F;
			f5 = (2.0F * f4 - 3F * f3) + 1.0F;
			f6 = (f4 - 2.0F * f3) + f;
			f7 = f4 - f3;
			f8 = -2F * f4 + 3F * f3;
			object3d.objMesh.nzOrg[i2] = f5 * keyFrames[i1].nzOrg[i2] + f6 * f1 + f7 * f2 + f8 * keyFrames[j1].nzOrg[i2];
		}

	}

	void interpolateBiCubic(Object3D object3d, int i, float f, int j, int k) {
		int l = i - 1;
		int i1 = i;
		int j1 = i + 1;
		int k1 = i + 2;
		if (wrapMode == 1) {
			if (k1 >= k)
				k1 = k - 1;
			else if (k1 < j)
				k1 = j;
			if (l >= k)
				l = k - 1;
			else if (l < j)
				l = j;
			if (i1 >= k)
				i1 = k - 1;
			else if (i1 < j)
				i1 = j;
			if (j1 >= k)
				j1 = k - 1;
			else if (j1 < j)
				j1 = j;
		} else {
			if (k1 >= k)
				k1 = j + 1;
			else if (k1 < j)
				k1 = k - 1;
			if (l >= k)
				l = j;
			else if (l < j)
				l = k - 2;
			if (i1 >= k)
				i1 = j;
			else if (i1 < j)
				i1 = k - 1;
			if (j1 >= k)
				j1 = j;
			else if (j1 < j)
				j1 = k - 1;
		}
		float f1 = f * f;
		int l1 = object3d.objMesh.anzCoords;
		for (int i2 = 0; i2 < l1; i2++) {
			float f2 = (keyFrames[k1].xOrg[i2] - keyFrames[j1].xOrg[i2] - keyFrames[l].xOrg[i2]) + keyFrames[i1].xOrg[i2];
			float f3 = keyFrames[l].xOrg[i2] - keyFrames[i1].xOrg[i2] - f2;
			float f4 = keyFrames[j1].xOrg[i2] - keyFrames[l].xOrg[i2];
			float f5 = keyFrames[i1].xOrg[i2];
			object3d.objMesh.xOrg[i2] = f2 * f * f1 + f3 * f1 + f4 * f + f5;
			f2 = (keyFrames[k1].yOrg[i2] - keyFrames[j1].yOrg[i2] - keyFrames[l].yOrg[i2]) + keyFrames[i1].yOrg[i2];
			f3 = keyFrames[l].yOrg[i2] - keyFrames[i1].yOrg[i2] - f2;
			f4 = keyFrames[j1].yOrg[i2] - keyFrames[l].yOrg[i2];
			f5 = keyFrames[i1].yOrg[i2];
			object3d.objMesh.yOrg[i2] = f2 * f * f1 + f3 * f1 + f4 * f + f5;
			f2 = (keyFrames[k1].zOrg[i2] - keyFrames[j1].zOrg[i2] - keyFrames[l].zOrg[i2]) + keyFrames[i1].zOrg[i2];
			f3 = keyFrames[l].zOrg[i2] - keyFrames[i1].zOrg[i2] - f2;
			f4 = keyFrames[j1].zOrg[i2] - keyFrames[l].zOrg[i2];
			f5 = keyFrames[i1].zOrg[i2];
			object3d.objMesh.zOrg[i2] = f2 * f * f1 + f3 * f1 + f4 * f + f5;
			f2 = (keyFrames[k1].nxOrg[i2] - keyFrames[j1].nxOrg[i2] - keyFrames[l].nxOrg[i2]) + keyFrames[i1].nxOrg[i2];
			f3 = keyFrames[l].nxOrg[i2] - keyFrames[i1].nxOrg[i2] - f2;
			f4 = keyFrames[j1].nxOrg[i2] - keyFrames[l].nxOrg[i2];
			f5 = keyFrames[i1].nxOrg[i2];
			object3d.objMesh.nxOrg[i2] = f2 * f * f1 + f3 * f1 + f4 * f + f5;
			f2 = (keyFrames[k1].nyOrg[i2] - keyFrames[j1].nyOrg[i2] - keyFrames[l].nyOrg[i2]) + keyFrames[i1].nyOrg[i2];
			f3 = keyFrames[l].nyOrg[i2] - keyFrames[i1].nyOrg[i2] - f2;
			f4 = keyFrames[j1].nyOrg[i2] - keyFrames[l].nyOrg[i2];
			f5 = keyFrames[i1].nyOrg[i2];
			object3d.objMesh.nyOrg[i2] = f2 * f * f1 + f3 * f1 + f4 * f + f5;
			f2 = (keyFrames[k1].nzOrg[i2] - keyFrames[j1].nzOrg[i2] - keyFrames[l].nzOrg[i2]) + keyFrames[i1].nzOrg[i2];
			f3 = keyFrames[l].nzOrg[i2] - keyFrames[i1].nzOrg[i2] - f2;
			f4 = keyFrames[j1].nzOrg[i2] - keyFrames[l].nzOrg[i2];
			f5 = keyFrames[i1].nzOrg[i2];
			object3d.objMesh.nzOrg[i2] = f2 * f * f1 + f3 * f1 + f4 * f + f5;
		}

	}

	void interpolateLinear(Object3D object3d, int i, float f, int j, int k) {
		float f1 = 1.0F - f;
		float f2 = f;
		int l = i;
		int i1 = i + 1;
		if (wrapMode == 1) {
			if (i1 >= k)
				i1 = k - 1;
			else if (i1 < j)
				i1 = j;
			if (l >= k)
				l = k - 1;
			else if (l < j)
				l = j;
		} else {
			if (i1 >= k)
				i1 = j;
			else if (i1 < j)
				i1 = k - 1;
			if (l >= k)
				l = j;
			else if (l < j)
				l = k - 1;
		}
		int j1 = object3d.objMesh.anzCoords;
		for (int k1 = 0; k1 < j1; k1++) {
			object3d.objMesh.xOrg[k1] = keyFrames[l].xOrg[k1] * f1 + keyFrames[i1].xOrg[k1] * f2;
			object3d.objMesh.yOrg[k1] = keyFrames[l].yOrg[k1] * f1 + keyFrames[i1].yOrg[k1] * f2;
			object3d.objMesh.zOrg[k1] = keyFrames[l].zOrg[k1] * f1 + keyFrames[i1].zOrg[k1] * f2;
			object3d.objMesh.nxOrg[k1] = keyFrames[l].nxOrg[k1] * f1 + keyFrames[i1].nxOrg[k1] * f2;
			object3d.objMesh.nyOrg[k1] = keyFrames[l].nyOrg[k1] * f1 + keyFrames[i1].nyOrg[k1] * f2;
			object3d.objMesh.nzOrg[k1] = keyFrames[l].nzOrg[k1] * f1 + keyFrames[i1].nzOrg[k1] * f2;
		}

	}

	void interpolateNone(Object3D object3d, int i, int j, int k) {
		int l = i;
		if (wrapMode == 1) {
			if (l >= k)
				l = k - 1;
			else if (l < j)
				l = j;
		} else if (l >= k)
			l = j;
		else if (l < j)
			l = k - 1;
		int i1 = object3d.objMesh.anzCoords;
		for (int j1 = 0; j1 < i1; j1++) {
			object3d.objMesh.xOrg[j1] = keyFrames[l].xOrg[j1];
			object3d.objMesh.yOrg[j1] = keyFrames[l].yOrg[j1];
			object3d.objMesh.zOrg[j1] = keyFrames[l].zOrg[j1];
		}

	}

	void interpolateCosine(Object3D object3d, int i, float f, int j, int k) {
		float f1 = (1.0F - (float) Math.cos((double) f * 3.1415926535897931D)) / 2.0F;
		float f2 = 1.0F - f1;
		int l = i;
		int i1 = i + 1;
		if (wrapMode == 1) {
			if (i1 >= k)
				i1 = k - 1;
			else if (i1 < j)
				i1 = j;
			if (l >= k)
				l = k - 1;
			else if (l < j)
				l = j;
		} else {
			if (i1 >= k)
				i1 = j;
			else if (i1 < j)
				i1 = k - 1;
			if (l >= k)
				l = j;
			else if (l < j)
				l = k - 1;
		}
		int j1 = object3d.objMesh.anzCoords;
		for (int k1 = 0; k1 < j1; k1++) {
			object3d.objMesh.xOrg[k1] = keyFrames[l].xOrg[k1] * f2 + keyFrames[i1].xOrg[k1] * f1;
			object3d.objMesh.yOrg[k1] = keyFrames[l].yOrg[k1] * f2 + keyFrames[i1].yOrg[k1] * f1;
			object3d.objMesh.zOrg[k1] = keyFrames[l].zOrg[k1] * f2 + keyFrames[i1].zOrg[k1] * f1;
			object3d.objMesh.nxOrg[k1] = keyFrames[l].nxOrg[k1] * f2 + keyFrames[i1].nxOrg[k1] * f1;
			object3d.objMesh.nyOrg[k1] = keyFrames[l].nyOrg[k1] * f2 + keyFrames[i1].nyOrg[k1] * f1;
			object3d.objMesh.nzOrg[k1] = keyFrames[l].nzOrg[k1] * f2 + keyFrames[i1].nzOrg[k1] * f1;
		}

	}

	private static final long serialVersionUID = 1L;
	public static final int LINEAR = 0;
	public static final int COSINE = 1;
	public static final int BICUBIC = 2;
	public static final int HERMITE = 3;
	public static final int KEYFRAMESONLY = 4;
	public static final int USE_WRAPPING = 0;
	public static final int USE_CLAMPING = 1;
	int aktFrames;
	Mesh keyFrames[];
	private int endFrame;
	private int maxFrames;
	private int mode;
	private float hermiteBias;
	private float hermiteTension;
	private int startFrames[];
	private int endFrames[];
	private String seqNames[];
	private int anzAnim;
	private int wrapMode;
}
