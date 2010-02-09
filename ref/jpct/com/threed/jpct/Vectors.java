// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.*;

// Referenced classes of package com.threed.jpct:
//            Config, Mesh

final class Vectors implements Serializable {

	Vectors(int i, Mesh mesh) {
		hasAlpha = false;
		baseMesh = mesh;
		maxVectors = i;
		vertexSector = new int[maxVectors];
		xTr = new float[maxVectors];
		yTr = new float[maxVectors];
		zTr = new float[maxVectors];
		sx = new float[maxVectors];
		sy = new float[maxVectors];
		sz = new float[maxVectors];
		sbOrg = new float[maxVectors];
		sgOrg = new float[maxVectors];
		srOrg = new float[maxVectors];
		uOrg = new float[maxVectors];
		vOrg = new float[maxVectors];
		nuOrg = new float[maxVectors];
		nvOrg = new float[maxVectors];
		su = new float[maxVectors];
		sv = new float[maxVectors];
		if (!Config.saveMemory) {
			sb = new float[maxVectors];
			sg = new float[maxVectors];
			sr = new float[maxVectors];
			eu = new float[maxVectors];
			ev = new float[maxVectors];
			bsu = new float[maxVectors];
			bsv = new float[maxVectors];
		}
		buOrg = new float[maxVectors];
		bvOrg = new float[maxVectors];
	}

	void createMultiCoords() {
		if (uMul == null) {
			uMul = new float[Config.maxTextureLayers - 1][maxVectors];
			vMul = new float[Config.maxTextureLayers - 1][maxVectors];
		}
	}

	void createAlpha() {
		if (alpha == null) {
			alpha = new float[maxVectors];
			for (int i = 0; i < maxVectors; i++)
				alpha[i] = 1.0F;

			hasAlpha = true;
		}
	}

	void killMultiCoords() {
		uMul = (float[][]) null;
		vMul = (float[][]) null;
	}

	void createScreenColors() {
		if (sb == null) {
			sb = new float[maxVectors];
			sg = new float[maxVectors];
			sr = new float[maxVectors];
		}
	}

	void createEnvmapCoords() {
		if (eu == null) {
			eu = new float[maxVectors];
			ev = new float[maxVectors];
		}
	}

	void createBumpmapCoords() {
		if (bsu == null) {
			bsu = new float[maxVectors];
			bsv = new float[maxVectors];
		}
	}

	int checkCoords(float f, float f1, float f2, int i) {
		int j = baseMesh.anzCoords - 1;
		float af[] = baseMesh.xOrg;
		float af1[] = baseMesh.yOrg;
		float af2[] = baseMesh.zOrg;
		for (; j >= 0; j--)
			if (af[j] == f && af1[j] == f1 && af2[j] == f2 && (i == 0 || i == vertexSector[j]))
				return j;

		return -1;
	}

	int addVertex(float f, float f1, float f2) {
		return addVertex(f, f1, f2, 0);
	}

	int addVertex(float f, float f1, float f2, int i) {
		int j = baseMesh.anzCoords;
		baseMesh.xOrg[j] = f;
		baseMesh.yOrg[j] = f1;
		baseMesh.zOrg[j] = f2;
		vertexSector[j] = i;
		baseMesh.anzCoords++;
		return j;
	}

	void setMesh(Mesh mesh) {
		baseMesh = mesh;
	}

	void strip() {
		sx = null;
		sy = null;
		sz = null;
		sr = null;
		sg = null;
		sb = null;
		sbOrg = null;
		sgOrg = null;
		srOrg = null;
		nuOrg = null;
		nvOrg = null;
		uOrg = null;
		vOrg = null;
		su = null;
		sv = null;
		buOrg = null;
		bvOrg = null;
		bsu = null;
		bsv = null;
		alpha = null;
	}

	private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
		objectinputstream.defaultReadObject();
		xTr = new float[maxVectors];
		yTr = new float[maxVectors];
		zTr = new float[maxVectors];
		if (!Config.saveMemory) {
			sb = new float[maxVectors];
			sg = new float[maxVectors];
			sr = new float[maxVectors];
		}
		sx = new float[maxVectors];
		sy = new float[maxVectors];
		sz = new float[maxVectors];
		su = new float[maxVectors];
		sv = new float[maxVectors];
		if (!Config.saveMemory) {
			eu = new float[maxVectors];
			ev = new float[maxVectors];
			bsu = new float[maxVectors];
			bsv = new float[maxVectors];
		}
	}

	static final void calcCross(float af[], float af1[], float af2[]) {
		af[0] = af1[1] * af2[2] - af1[2] * af2[1];
		af[1] = af1[2] * af2[0] - af1[0] * af2[2];
		af[2] = af1[0] * af2[1] - af1[1] * af2[0];
	}

	static final float[] calcCross(float af[], float af1[]) {
		float af2[] = new float[3];
		af2[0] = af[1] * af1[2] - af[2] * af1[1];
		af2[1] = af[2] * af1[0] - af[0] * af1[2];
		af2[2] = af[0] * af1[1] - af[1] * af1[0];
		return af2;
	}

	static final float calcDot(float af[], float af1[]) {
		return af[0] * af1[0] + af[1] * af1[1] + af[2] * af1[2];
	}

	static final double calcDot(double ad[], double ad1[]) {
		return ad[0] * ad1[0] + ad[1] * ad1[1] + ad[2] * ad1[2];
	}

	static final float[] calcSub(float af[], float af1[]) {
		float af2[] = new float[3];
		af2[0] = af[0] - af1[0];
		af2[1] = af[1] - af1[1];
		af2[2] = af[2] - af1[2];
		return af2;
	}

	private static final long serialVersionUID = 1L;
	transient float xTr[];
	transient float yTr[];
	transient float zTr[];
	transient float sx[];
	transient float sy[];
	transient float sz[];
	transient float sb[];
	transient float sr[];
	transient float sg[];
	float sbOrg[];
	float srOrg[];
	float sgOrg[];
	float nuOrg[];
	float nvOrg[];
	float uOrg[];
	float vOrg[];
	float uMul[][];
	float vMul[][];
	transient float su[];
	transient float sv[];
	transient float eu[];
	transient float ev[];
	float buOrg[];
	float bvOrg[];
	float alpha[];
	transient float bsu[];
	transient float bsv[];
	int vertexSector[];
	int maxVectors;
	boolean hasAlpha;
	private Mesh baseMesh;
}
