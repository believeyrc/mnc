// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            Matrix, BufferedMatrix, SimpleVector, Logger

public final class Lights implements Serializable {

	Lights(int i) {
		mat3 = new Matrix();
		mat5 = new Matrix();
		mat6 = new Matrix();
		maxLightValue = 8192;
		rgbScale = 1;
		maxLights = i;
		lightCnt = 0;
		xOrg = new float[maxLights];
		yOrg = new float[maxLights];
		zOrg = new float[maxLights];
		xTr = new float[maxLights];
		yTr = new float[maxLights];
		zTr = new float[maxLights];
		xRot = new float[maxLights];
		yRot = new float[maxLights];
		zRot = new float[maxLights];
		bOrg = new float[maxLights];
		gOrg = new float[maxLights];
		rOrg = new float[maxLights];
		transform = new BufferedMatrix[maxLights];
		isVisible = new boolean[maxLights];
		attenuation = new float[maxLights];
		discardDistance = new float[maxLights];
		for (int j = 0; j < maxLights; j++) {
			attenuation[j] = -2F;
			discardDistance[j] = -2F;
		}

	}

	public void setOverbrightLighting(int i) {
		maxLightValue = i;
	}

	public void setRGBScale(int i) {
		if (i > 0 && i < 5 && i != 3)
			rgbScale = i;
		else
			Logger.log("This RGB scaling (" + i + ") is not supported!", 1);
	}

	float getAttenuation(int i) {
		if (!isFine(i))
			return 0.0F;
		else
			return attenuation[i];
	}

	float getDiscardDistance(int i) {
		if (!isFine(i))
			return 0.0F;
		else
			return discardDistance[i];
	}

	SimpleVector getPosition(int i) {
		SimpleVector simplevector = new SimpleVector();
		if (isFine(i)) {
			simplevector.z = zOrg[i];
			simplevector.x = xOrg[i];
			simplevector.y = yOrg[i];
		}
		return simplevector;
	}

	SimpleVector getIntensity(int i) {
		if (!isFine(i))
			return new SimpleVector(0.0F, 0.0F, 0.0F);
		else
			return new SimpleVector(rOrg[i], gOrg[i], bOrg[i]);
	}

	void setAttenuation(int i, float f) {
		if (isFine(i)) {
			if (f < -1F)
				f = -1F;
			if (f == 0.0F)
				f = 1.0F;
			attenuation[i] = f;
		}
	}

	void setDiscardDistance(int i, float f) {
		if (isFine(i)) {
			if (f < -1F)
				f = -2F;
			discardDistance[i] = f;
		}
	}

	int addLight(float f, float f1, float f2, float f3, float f4, float f5) {
		if (lightCnt < maxLights) {
			xOrg[lightCnt] = f;
			yOrg[lightCnt] = f1;
			zOrg[lightCnt] = f2;
			rOrg[lightCnt] = f3;
			gOrg[lightCnt] = f4;
			bOrg[lightCnt] = f5;
			xRot[lightCnt] = 0.0F;
			yRot[lightCnt] = 0.0F;
			zRot[lightCnt] = 0.0F;
			isVisible[lightCnt] = true;
			transform[lightCnt] = new BufferedMatrix();
			Logger.log("Adding Lightsource: " + lightCnt, 2);
			lightCnt++;
		} else {
			Logger.log("Maximum number of Lightsources reached...ignoring additional ones!", 1);
		}
		return lightCnt - 1;
	}

	void transformLights(BufferedMatrix bufferedmatrix) {
		Matrix matrix = bufferedmatrix.frontMatrix;
		mat3.setIdentity();
		mat3.mat[3][0] = -bufferedmatrix.frontBx;
		mat3.mat[3][1] = -bufferedmatrix.frontBy;
		mat3.mat[3][2] = -bufferedmatrix.frontBz;
		for (int i = 0; i < lightCnt; i++) {
			if (!isVisible[i])
				continue;
			if (xRot[i] != 0.0F)
				transform[i].rotateX(xRot[i]);
			if (yRot[i] != 0.0F)
				transform[i].rotateY(yRot[i]);
			if (zRot[i] != 0.0F)
				transform[i].rotateZ(zRot[i]);
			transform[i].prepareTransform();
			mat5.setIdentity();
			if (!transform[i].frontMatrix.isIdentity()) {
				mat6.setIdentity();
				mat5.mat[3][0] = -transform[i].frontBx;
				mat5.mat[3][1] = -transform[i].frontBy;
				mat5.mat[3][2] = -transform[i].frontBz;
				mat6.mat[3][0] = transform[i].frontBx;
				mat6.mat[3][1] = transform[i].frontBy;
				mat6.mat[3][2] = transform[i].frontBz;
				mat5.matMul(transform[i].frontMatrix);
				mat5.matMul(mat6);
			}
			mat5.matMul(mat3);
			mat5.matMul(matrix);
			float f = mat5.mat[0][0];
			float f1 = mat5.mat[1][0];
			float f2 = mat5.mat[1][1];
			float f3 = mat5.mat[2][1];
			float f4 = mat5.mat[2][2];
			float f5 = mat5.mat[1][2];
			float f6 = mat5.mat[2][0];
			float f7 = mat5.mat[0][2];
			float f8 = mat5.mat[0][1];
			float f9 = mat5.mat[3][0];
			float f10 = mat5.mat[3][1];
			float f11 = mat5.mat[3][2];
			float f12 = xOrg[i];
			float f13 = yOrg[i];
			float f14 = zOrg[i];
			xTr[i] = f12 * f + f13 * f1 + f14 * f6 + f9;
			yTr[i] = f12 * f8 + f13 * f2 + f14 * f3 + f10;
			zTr[i] = f12 * f7 + f13 * f5 + f14 * f4 + f11;
		}

	}

	void setVisibility(int i, boolean flag) {
		if (isFine(i))
			isVisible[i] = flag;
	}

	void setAutoRotation(int i, float f, float f1, float f2, float f3, float f4, float f5) {
		if (isFine(i)) {
			transform[i].backBx = f;
			transform[i].backBy = f1;
			transform[i].backBz = f2;
			xRot[i] = f3;
			yRot[i] = f4;
			zRot[i] = f5;
		}
	}

	void setAutoRotation(int i, float f, float f1, float f2) {
		if (isFine(i)) {
			xRot[i] = f;
			yRot[i] = f1;
			zRot[i] = f2;
		}
	}

	void setLightIntensity(int i, float f, float f1, float f2) {
		if (isFine(i)) {
			rOrg[i] = f;
			gOrg[i] = f1;
			bOrg[i] = f2;
		}
	}

	void setPosition(int i, float f, float f1, float f2) {
		if (isFine(i)) {
			zOrg[i] = f2;
			xOrg[i] = f;
			yOrg[i] = f1;
		}
	}

	void displaceLights(float f, float f1, float f2) {
		for (int i = 0; i < lightCnt; i++) {
			zOrg[i] += f2;
			xOrg[i] += f;
			yOrg[i] += f1;
		}

	}

	private boolean isFine(int i) {
		if (i < lightCnt) {
			return true;
		} else {
			logError();
			return false;
		}
	}

	private void logError() {
		Logger.log("Tried to modify a nonexistent light-source!", 0);
	}

	private static final long serialVersionUID = 1L;
	public static final boolean LIGHT_VISIBLE = true;
	public static final boolean LIGHT_INVISIBLE = false;
	public static final int OVERBRIGHT_LIGHTING_DISABLED = 255;
	public static final int OVERBRIGHT_LIGHTING_ENABLED = 8191;
	public static final int RGB_SCALE_DEFAULT = 1;
	public static final int RGB_SCALE_2X = 2;
	public static final int RGB_SCALE_4X = 4;
	int rgbScale;
	int maxLightValue;
	int lightCnt;
	int maxLights;
	float xOrg[];
	float yOrg[];
	float zOrg[];
	float xTr[];
	float yTr[];
	float zTr[];
	float xRot[];
	float yRot[];
	float zRot[];
	float bOrg[];
	float rOrg[];
	float gOrg[];
	BufferedMatrix transform[];
	boolean isVisible[];
	float attenuation[];
	float discardDistance[];
	private static final String ERROR_MESSAGE = "Tried to modify a nonexistent light-source!";
	private Matrix mat3;
	private Matrix mat5;
	private Matrix mat6;
}
