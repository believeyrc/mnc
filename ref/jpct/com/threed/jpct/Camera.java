// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            BufferedMatrix, SimpleVector, Matrix, Config, 
//            Object3D

public class Camera extends BufferedMatrix implements Serializable {

	public Camera() {
		ellipsoidMode = 0;
		cameraFOV = Config.defaultCameraFOV;
		yFOV = -1F;
		scaleX = 1.0F;
		scaleY = 1.0F;
		divx = 1.0F;
		divy = 1.0F;
		lowerLimit = 0.5F;
		higherLimit = 1.5F;
	}

	void calcFOV(int i, int j) {
		float f = cameraFOV / 2.0F;
		if (!Config.autoMaintainAspectRatio || yFOV != -1F) {
			divx = f;
			divy = f;
			if (yFOV != -1F)
				divy = yFOV / 2.0F;
		} else {
			divx = f;
			divy = f * ((float) j / (float) i);
		}
		scaleX = (float) i / (2.0F * divx);
		scaleY = (float) j / (2.0F * divy);
	}

	public SimpleVector getPosition() {
		return new SimpleVector(super.backBx, super.backBy, super.backBz);
	}

	public SimpleVector getXAxis() {
		return super.backMatrix.getXAxis();
	}

	public SimpleVector getYAxis() {
		return super.backMatrix.getYAxis();
	}

	public SimpleVector getZAxis() {
		return super.backMatrix.getZAxis();
	}

	public SimpleVector getDirection() {
		return getVector(2);
	}

	public SimpleVector getUpVector() {
		SimpleVector simplevector = getVector(1);
		simplevector.scalarMul(-1F);
		return simplevector;
	}

	public SimpleVector getSideVector() {
		return getVector(0);
	}

	private SimpleVector getVector(int i) {
		float f = super.backMatrix.mat[0][i];
		float f1 = super.backMatrix.mat[1][i];
		float f2 = super.backMatrix.mat[2][i];
		float f3 = (float) (1.0D / Math.sqrt(f * f + f1 * f1 + f2 * f2));
		return new SimpleVector(f * f3, f1 * f3, f2 * f3);
	}

	public float convertRADAngleIntoFOV(float f) {
		f = (float) (Math.tan((double) f / 2D) * 2D);
		return f;
	}

	public float convertDEGAngleIntoFOV(float f) {
		double d = ((double) f / 180D) * 3.1415926535897931D;
		f = (float) (Math.tan(d / 2D) * 2D);
		return f;
	}

	public void setFOVLimits(float f, float f1) {
		lowerLimit = f;
		higherLimit = f1;
	}

	public float getMaxFOV() {
		return higherLimit;
	}

	public float getMinFOV() {
		return lowerLimit;
	}

	public void setFOV(float f) {
		if (f > higherLimit)
			f = higherLimit;
		else if (f < lowerLimit)
			f = lowerLimit;
		cameraFOV = f;
	}

	public void setYFOV(float f) {
		if (f != -1F)
			if (f > higherLimit)
				f = higherLimit;
			else if (f < lowerLimit)
				f = lowerLimit;
		yFOV = f;
	}

	public float getFOV() {
		return cameraFOV;
	}

	public float getYFOV() {
		return yFOV;
	}

	public void increaseFOV(float f) {
		cameraFOV += f;
		if (cameraFOV > higherLimit)
			cameraFOV = higherLimit;
		else if (cameraFOV < lowerLimit)
			cameraFOV = lowerLimit;
	}

	public void decreaseFOV(float f) {
		cameraFOV -= f;
		if (cameraFOV > higherLimit)
			cameraFOV = higherLimit;
		else if (cameraFOV < lowerLimit)
			cameraFOV = lowerLimit;
	}

	public void setFOVtoDefault() {
		cameraFOV = Config.defaultCameraFOV;
	}

	public void lookAt(SimpleVector simplevector) {
		double d = simplevector.x - super.backBx;
		double d1 = simplevector.y - super.backBy;
		double d2 = simplevector.z - super.backBz;
		if (d == 0.0D && d2 == 0.0D)
			d += 1.0000000000000001E-128D;
		double d3 = Math.sqrt(d * d + d1 * d1 + d2 * d2);
		if (d3 != 0.0D) {
			d /= d3;
			d1 /= d3;
			d2 /= d3;
		}
		Matrix matrix = new Matrix();
		float af[][] = matrix.mat;
		af[0][1] = 0.0F;
		af[1][1] = 1.0F;
		af[2][1] = 0.0F;
		af[0][2] = (float) d;
		af[1][2] = (float) d1;
		af[2][2] = (float) d2;
		double d4 = 0.0D;
		double d5 = 1.0D;
		double d6 = 0.0D;
		double d7 = d;
		double d8 = d1;
		double d9 = d2;
		double d10 = d5 * d9 - d6 * d8;
		double d11 = d6 * d7 - d4 * d9;
		double d12 = d4 * d8 - d5 * d7;
		d3 = Math.sqrt(d10 * d10 + d11 * d11 + d12 * d12);
		if (d3 != 0.0D) {
			d10 /= d3;
			d11 /= d3;
			d12 /= d3;
		}
		double d13 = d8 * d12 - d9 * d11;
		double d14 = d9 * d10 - d7 * d12;
		double d15 = d7 * d11 - d8 * d10;
		d3 = Math.sqrt(d13 * d13 + d14 * d14 + d15 * d15);
		if (d3 != 0.0D) {
			d13 /= d3;
			d14 /= d3;
			d15 /= d3;
		}
		af[0][0] = (float) d10;
		af[1][0] = (float) d11;
		af[2][0] = (float) d12;
		af[0][1] = (float) d13;
		af[1][1] = (float) d14;
		af[2][1] = (float) d15;
		matrix.orthonormalizeDouble();
		super.backMatrix = matrix;
	}

	public void align(Object3D object3d) {
		Matrix matrix = object3d.getRotationMatrix().cloneMatrix();
		matrix.scalarMul(1.0F / object3d.getScale());
		super.backMatrix = matrix.invert3x3();
	}

	public void setPositionToCenter(Object3D object3d) {
		SimpleVector simplevector = object3d.getTransformedCenter();
		super.backBx = simplevector.x;
		super.backBy = simplevector.y;
		super.backBz = simplevector.z;
	}

	public void setPosition(SimpleVector simplevector) {
		super.backBx = simplevector.x;
		super.backBy = simplevector.y;
		super.backBz = simplevector.z;
	}

	public void setPosition(float f, float f1, float f2) {
		super.backBx = f;
		super.backBy = f1;
		super.backBz = f2;
	}

	public void setOrientation(SimpleVector simplevector, SimpleVector simplevector1) {
		super.backMatrix.setOrientation(simplevector, simplevector1, false);
	}

	public SimpleVector transform(SimpleVector simplevector) {
		Matrix matrix = super.backMatrix;
		Matrix matrix1 = new Matrix();
		matrix1.mat[3][0] = -super.backBx;
		matrix1.mat[3][1] = -super.backBy;
		matrix1.mat[3][2] = -super.backBz;
		matrix1.matMul(matrix);
		float f = matrix1.mat[2][2];
		float f1 = matrix1.mat[1][2];
		float f2 = matrix1.mat[0][2];
		float f3 = matrix1.mat[3][2];
		float f4 = simplevector.x;
		float f5 = simplevector.y;
		float f6 = simplevector.z;
		float f7 = f4 * f2 + f5 * f1 + f6 * f + f3;
		float f8 = matrix1.mat[0][0];
		float f9 = matrix1.mat[1][0];
		float f10 = matrix1.mat[1][1];
		float f11 = matrix1.mat[2][1];
		float f12 = matrix1.mat[2][0];
		float f13 = matrix1.mat[0][1];
		float f14 = matrix1.mat[3][0];
		float f15 = matrix1.mat[3][1];
		float f16 = f4 * f8 + f5 * f9 + f6 * f12 + f14;
		float f17 = f4 * f13 + f5 * f10 + f6 * f11 + f15;
		return new SimpleVector(f16, f17, f7);
	}

	public void moveCamera(int i, float f) {
		float f1 = -1F;
		if ((i & 1) == 1)
			f1 = 1.0F;
		f1 *= f;
		int j = 2 - ((i + 1) / 2 - 1);
		super.backBx += super.backMatrix.mat[0][j] * f1;
		super.backBy += super.backMatrix.mat[1][j] * f1;
		super.backBz += super.backMatrix.mat[2][j] * f1;
	}

	public void moveCamera(SimpleVector simplevector, float f) {
		super.backBx += simplevector.x * f;
		super.backBy += simplevector.y * f;
		super.backBz += simplevector.z * f;
	}

	public void rotateCameraAxis(SimpleVector simplevector, float f) {
		super.backMatrix.rotateAxis(simplevector, -f);
	}

	public void rotateCameraX(float f) {
		super.backMatrix.rotateX(-f);
	}

	public void rotateCameraY(float f) {
		super.backMatrix.rotateY(-f);
	}

	public void rotateCameraZ(float f) {
		super.backMatrix.rotateZ(-f);
	}

	public void setEllipsoidMode(int i) {
		ellipsoidMode = i;
	}

	public int getEllipsoidMode() {
		return ellipsoidMode;
	}

	private static final long serialVersionUID = 1L;
	public static final int CAMERA_MOVEIN = 1;
	public static final int CAMERA_MOVEOUT = 2;
	public static final int CAMERA_MOVEDOWN = 3;
	public static final int CAMERA_MOVEUP = 4;
	public static final int CAMERA_MOVELEFT = 6;
	public static final int CAMERA_MOVERIGHT = 5;
	public static final int CAMERA_DONT_MOVE = 7;
	public static final boolean SLIDE = true;
	public static final boolean DONT_SLIDE = false;
	public static final int ELLIPSOID_ALIGNED = 0;
	public static final int ELLIPSOID_TRANSFORMED = 1;
	float scaleX;
	float scaleY;
	float divx;
	float divy;
	private float cameraFOV;
	private float yFOV;
	private float lowerLimit;
	private float higherLimit;
	private int ellipsoidMode;
}
