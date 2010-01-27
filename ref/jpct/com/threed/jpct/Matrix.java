// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            SimpleVector, Logger

public class Matrix implements Serializable {

	public Matrix() {
		mat = new float[4][4];
		mat0 = mat[0];
		mat1 = mat[1];
		mat2 = mat[2];
		mat3 = mat[3];
		mat0[0] = 1.0F;
		mat1[1] = 1.0F;
		mat2[2] = 1.0F;
		mat3[3] = 1.0F;
	}

	public Matrix(Matrix matrix) {
		mat = new float[4][4];
		mat0 = mat[0];
		mat1 = mat[1];
		mat2 = mat[2];
		mat3 = mat[3];
		setTo(matrix);
	}

	public final boolean isIdentity() {
		return mat[0][0] == 1.0F && mat[1][1] == 1.0F && mat[2][2] == 1.0F && mat[3][3] == 1.0F && mat[0][1] == 0.0F
				&& mat[0][2] == 0.0F && mat[0][3] == 0.0F && mat[1][0] == 0.0F && mat[1][2] == 0.0F && mat[1][3] == 0.0F
				&& mat[2][1] == 0.0F && mat[2][0] == 0.0F && mat[2][3] == 0.0F && mat[3][1] == 0.0F && mat[3][2] == 0.0F
				&& mat[3][0] == 0.0F;
	}

	public final void setIdentity() {
		mat[0][0] = 1.0F;
		mat[1][1] = 1.0F;
		mat[2][2] = 1.0F;
		mat[3][3] = 1.0F;
		mat[0][1] = 0.0F;
		mat[0][2] = 0.0F;
		mat[0][3] = 0.0F;
		mat[1][0] = 0.0F;
		mat[1][2] = 0.0F;
		mat[1][3] = 0.0F;
		mat[2][1] = 0.0F;
		mat[2][0] = 0.0F;
		mat[2][3] = 0.0F;
		mat[3][1] = 0.0F;
		mat[3][2] = 0.0F;
		mat[3][0] = 0.0F;
	}

	public final void scalarMul(float f) {
		mat[0][0] *= f;
		mat[0][1] *= f;
		mat[0][2] *= f;
		mat[1][0] *= f;
		mat[1][1] *= f;
		mat[1][2] *= f;
		mat[2][0] *= f;
		mat[2][1] *= f;
		mat[2][2] *= f;
	}

	public final void matMul(Matrix matrix) {
		float f = mat0[0];
		float f1 = mat0[1];
		float f2 = mat0[2];
		float f3 = mat0[3];
		float f4 = mat1[0];
		float f5 = mat1[1];
		float f6 = mat1[2];
		float f7 = mat1[3];
		float f8 = mat2[0];
		float f9 = mat2[1];
		float f10 = mat2[2];
		float f11 = mat2[3];
		float f12 = mat3[0];
		float f13 = mat3[1];
		float f14 = mat3[2];
		float f15 = mat3[3];
		float f16 = matrix.mat0[0];
		float f17 = matrix.mat0[1];
		float f18 = matrix.mat0[2];
		float f19 = matrix.mat0[3];
		float f20 = matrix.mat1[0];
		float f21 = matrix.mat1[1];
		float f22 = matrix.mat1[2];
		float f23 = matrix.mat1[3];
		float f24 = matrix.mat2[0];
		float f25 = matrix.mat2[1];
		float f26 = matrix.mat2[2];
		float f27 = matrix.mat2[3];
		float f28 = matrix.mat3[0];
		float f29 = matrix.mat3[1];
		float f30 = matrix.mat3[2];
		float f31 = matrix.mat3[3];
		mat0[0] = f * f16 + f1 * f20 + f2 * f24 + f3 * f28;
		mat0[1] = f * f17 + f1 * f21 + f2 * f25 + f3 * f29;
		mat0[2] = f * f18 + f1 * f22 + f2 * f26 + f3 * f30;
		mat0[3] = f * f19 + f1 * f23 + f2 * f27 + f3 * f31;
		mat1[0] = f4 * f16 + f5 * f20 + f6 * f24 + f7 * f28;
		mat1[1] = f4 * f17 + f5 * f21 + f6 * f25 + f7 * f29;
		mat1[2] = f4 * f18 + f5 * f22 + f6 * f26 + f7 * f30;
		mat1[3] = f4 * f19 + f5 * f23 + f6 * f27 + f7 * f31;
		mat2[0] = f8 * f16 + f9 * f20 + f10 * f24 + f11 * f28;
		mat2[1] = f8 * f17 + f9 * f21 + f10 * f25 + f11 * f29;
		mat2[2] = f8 * f18 + f9 * f22 + f10 * f26 + f11 * f30;
		mat2[3] = f8 * f19 + f9 * f23 + f10 * f27 + f11 * f31;
		mat3[0] = f12 * f16 + f13 * f20 + f14 * f24 + f15 * f28;
		mat3[1] = f12 * f17 + f13 * f21 + f14 * f25 + f15 * f29;
		mat3[2] = f12 * f18 + f13 * f22 + f14 * f26 + f15 * f30;
		mat3[3] = f12 * f19 + f13 * f23 + f14 * f27 + f15 * f31;
	}

	public final void rotateX(float f) {
		float f1;
		float f2;
		if (f == 3.141593F) {
			f1 = cpi;
			f2 = spi;
		} else if (f == -3.141593F) {
			f1 = mcpi;
			f2 = mspi;
		} else if (f == 1.570796F) {
			f1 = cpih;
			f2 = spih;
		} else if (f == -1.570796F) {
			f1 = mcpih;
			f2 = mspih;
		} else {
			f1 = (float) Math.cos(f);
			f2 = (float) Math.sin(f);
		}
		float f3 = mat0[1];
		float f4 = mat0[2];
		float f5 = mat1[1];
		float f6 = mat1[2];
		float f7 = mat2[1];
		float f8 = mat2[2];
		mat0[1] = f3 * f1 + f4 * f2;
		mat0[2] = f3 * -f2 + f4 * f1;
		mat1[1] = f5 * f1 + f6 * f2;
		mat1[2] = f5 * -f2 + f6 * f1;
		mat2[1] = f7 * f1 + f8 * f2;
		mat2[2] = f7 * -f2 + f8 * f1;
	}

	public final void rotateY(float f) {
		float f1;
		float f2;
		if (f == 3.141593F) {
			f1 = cpi;
			f2 = spi;
		} else if (f == -3.141593F) {
			f1 = mcpi;
			f2 = mspi;
		} else if (f == 1.570796F) {
			f1 = cpih;
			f2 = spih;
		} else if (f == -1.570796F) {
			f1 = mcpih;
			f2 = mspih;
		} else {
			f1 = (float) Math.cos(f);
			f2 = (float) Math.sin(f);
		}
		float f3 = mat0[0];
		float f4 = mat0[2];
		float f5 = mat1[0];
		float f6 = mat1[2];
		float f7 = mat2[0];
		float f8 = mat2[2];
		mat0[0] = f3 * f1 + f4 * f2;
		mat0[2] = f3 * -f2 + f4 * f1;
		mat1[0] = f5 * f1 + f6 * f2;
		mat1[2] = f5 * -f2 + f6 * f1;
		mat2[0] = f7 * f1 + f8 * f2;
		mat2[2] = f7 * -f2 + f8 * f1;
	}

	public final void rotateZ(float f) {
		float f1;
		float f2;
		if (f == 3.141593F) {
			f1 = cpi;
			f2 = spi;
		} else if (f == -3.141593F) {
			f1 = mcpi;
			f2 = mspi;
		} else if (f == 1.570796F) {
			f1 = cpih;
			f2 = spih;
		} else if (f == -1.570796F) {
			f1 = mcpih;
			f2 = mspih;
		} else {
			f1 = (float) Math.cos(f);
			f2 = (float) Math.sin(f);
		}
		float f3 = mat0[0];
		float f4 = mat0[1];
		float f5 = mat1[0];
		float f6 = mat1[1];
		float f7 = mat2[0];
		float f8 = mat2[1];
		mat0[0] = f3 * f1 + f4 * f2;
		mat0[1] = f3 * -f2 + f4 * f1;
		mat1[0] = f5 * f1 + f6 * f2;
		mat1[1] = f5 * -f2 + f6 * f1;
		mat2[0] = f7 * f1 + f8 * f2;
		mat2[1] = f7 * -f2 + f8 * f1;
	}

	public final void rotateAxis(SimpleVector simplevector, float f) {
		double d = Math.cos(f);
		double d1 = Math.sin(f);
		double d2 = 1.0D - d;
		simplevector = simplevector.normalize();
		double d3 = simplevector.x;
		double d4 = simplevector.y;
		double d5 = simplevector.z;
		Matrix matrix = new Matrix();
		double d6 = d1 * d4;
		double d7 = d1 * d3;
		double d8 = d1 * d5;
		double d9 = d2 * d3 * d4;
		double d10 = d2 * d3 * d5;
		double d11 = d2 * d4 * d5;
		matrix.mat0[0] = (float) (d2 * d3 * d3 + d);
		matrix.mat1[0] = (float) (d9 + d8);
		matrix.mat2[0] = (float) (d10 - d6);
		matrix.mat0[1] = (float) (d9 - d8);
		matrix.mat1[1] = (float) (d2 * d4 * d4 + d);
		matrix.mat2[1] = (float) (d11 + d7);
		matrix.mat0[2] = (float) (d10 + d6);
		matrix.mat1[2] = (float) (d11 - d7);
		matrix.mat2[2] = (float) (d2 * d5 * d5 + d);
		matrix.orthonormalize();
		matMul(matrix);
	}

	public final void interpolate(Matrix matrix, Matrix matrix1, float f) {
		if (f > 1.0F)
			f = 1.0F;
		else if (f < 0.0F)
			f = 0.0F;
		float f1 = 1.0F - f;
		for (int i = 0; i < 4; i++) {
			mat[i][0] = matrix.mat[i][0] * f1 + matrix1.mat[i][0] * f;
			mat[i][1] = matrix.mat[i][1] * f1 + matrix1.mat[i][1] * f;
			mat[i][2] = matrix.mat[i][2] * f1 + matrix1.mat[i][2] * f;
			mat[i][3] = matrix.mat[i][3] * f1 + matrix1.mat[i][3] * f;
		}

		orthonormalize();
	}

	public final SimpleVector getTranslation() {
		return new SimpleVector(mat3[0], mat3[1], mat3[2]);
	}

	public final SimpleVector getXAxis() {
		return new SimpleVector(mat0[0], mat0[1], mat0[2]);
	}

	public final SimpleVector getYAxis() {
		return new SimpleVector(mat1[0], mat1[1], mat1[2]);
	}

	public final SimpleVector getZAxis() {
		return new SimpleVector(mat2[0], mat2[1], mat2[2]);
	}

	public final void setOrientation(SimpleVector simplevector, SimpleVector simplevector1) {
		setOrientation(simplevector, simplevector1, true);
	}

	final void setOrientation(SimpleVector simplevector, SimpleVector simplevector1, boolean flag) {
		simplevector1 = simplevector1.normalize();
		simplevector = simplevector.normalize();
		SimpleVector simplevector2 = simplevector1.calcCross(simplevector).normalize();
		if (!flag) {
			mat[0][0] = simplevector2.x;
			mat[1][0] = simplevector2.y;
			mat[2][0] = simplevector2.z;
			mat[3][0] = 0.0F;
			mat[0][1] = simplevector1.x;
			mat[1][1] = simplevector1.y;
			mat[2][1] = simplevector1.z;
			mat[3][1] = 0.0F;
			mat[0][2] = simplevector.x;
			mat[1][2] = simplevector.y;
			mat[2][2] = simplevector.z;
			mat[3][2] = 0.0F;
			mat[0][3] = 0.0F;
			mat[1][3] = 0.0F;
			mat[2][3] = 0.0F;
			mat[3][3] = 1.0F;
		} else {
			mat[0][0] = simplevector2.x;
			mat[0][1] = simplevector2.y;
			mat[0][2] = simplevector2.z;
			mat[0][3] = 0.0F;
			mat[1][0] = simplevector1.x;
			mat[1][1] = simplevector1.y;
			mat[1][2] = simplevector1.z;
			mat[1][3] = 0.0F;
			mat[2][0] = simplevector.x;
			mat[2][1] = simplevector.y;
			mat[2][2] = simplevector.z;
			mat[2][3] = 0.0F;
			mat[3][0] = 0.0F;
			mat[3][1] = 0.0F;
			mat[3][2] = 0.0F;
			mat[3][3] = 1.0F;
		}
	}

	public final void translate(SimpleVector simplevector) {
		mat3[0] += simplevector.x;
		mat3[1] += simplevector.y;
		mat3[2] += simplevector.z;
	}

	public final void translate(float f, float f1, float f2) {
		mat3[0] += f;
		mat3[1] += f1;
		mat3[2] += f2;
	}

	public final Matrix cloneMatrix() {
		Matrix matrix = new Matrix();
		matrix.setTo(this);
		return matrix;
	}

	public final Matrix invert() {
		Matrix matrix = new Matrix();
		float f = mat[0][0];
		float f1 = mat[0][1];
		float f2 = mat[0][2];
		float f3 = mat[0][3];
		float f4 = mat[1][0];
		float f5 = mat[1][1];
		float f6 = mat[1][2];
		float f7 = mat[1][3];
		float f8 = mat[2][0];
		float f9 = mat[2][1];
		float f10 = mat[2][2];
		float f11 = mat[2][3];
		float f12 = mat[3][0];
		float f13 = mat[3][1];
		float f14 = mat[3][2];
		float f15 = mat[3][3];
		float f16 = f10 * f15;
		float f17 = f14 * f11;
		float f18 = f6 * f15;
		float f19 = f14 * f7;
		float f20 = f6 * f11;
		float f21 = f10 * f7;
		float f22 = f2 * f15;
		float f23 = f14 * f3;
		float f24 = f2 * f11;
		float f25 = f10 * f3;
		float f26 = f2 * f7;
		float f27 = f6 * f3;
		float f28 = (f16 * f5 + f19 * f9 + f20 * f13) - (f17 * f5 + f18 * f9 + f21 * f13);
		float f29 = (f17 * f1 + f22 * f9 + f25 * f13) - (f16 * f1 + f23 * f9 + f24 * f13);
		float f30 = (f18 * f1 + f23 * f5 + f26 * f13) - (f19 * f1 + f22 * f5 + f27 * f13);
		float f31 = (f21 * f1 + f24 * f5 + f27 * f9) - (f20 * f1 + f25 * f5 + f26 * f9);
		float f32 = (f17 * f4 + f18 * f8 + f21 * f12) - (f16 * f4 + f19 * f8 + f20 * f12);
		float f33 = (f16 * f + f23 * f8 + f24 * f12) - (f17 * f + f22 * f8 + f25 * f12);
		float f34 = (f19 * f + f22 * f4 + f27 * f12) - (f18 * f + f23 * f4 + f26 * f12);
		float f35 = (f20 * f + f25 * f4 + f26 * f8) - (f21 * f + f24 * f4 + f27 * f8);
		f16 = f8 * f13;
		f17 = f12 * f9;
		f18 = f4 * f13;
		f19 = f12 * f5;
		f20 = f4 * f9;
		f21 = f8 * f5;
		f22 = f * f13;
		f23 = f12 * f1;
		f24 = f * f9;
		f25 = f8 * f1;
		f26 = f * f5;
		f27 = f4 * f1;
		float f36 = (f16 * f7 + f19 * f11 + f20 * f15) - (f17 * f7 + f18 * f11 + f21 * f15);
		float f37 = (f17 * f3 + f22 * f11 + f25 * f15) - (f16 * f3 + f23 * f11 + f24 * f15);
		float f38 = (f18 * f3 + f23 * f7 + f26 * f15) - (f19 * f3 + f22 * f7 + f27 * f15);
		float f39 = (f21 * f3 + f24 * f7 + f27 * f11) - (f20 * f3 + f25 * f7 + f26 * f11);
		float f40 = (f18 * f10 + f21 * f14 + f17 * f6) - (f20 * f14 + f16 * f6 + f19 * f10);
		float f41 = (f24 * f14 + f16 * f2 + f23 * f10) - (f22 * f10 + f25 * f14 + f17 * f2);
		float f42 = (f22 * f6 + f27 * f14 + f19 * f2) - (f26 * f14 + f18 * f2 + f23 * f6);
		float f43 = (f26 * f10 + f20 * f2 + f25 * f6) - (f24 * f6 + f27 * f10 + f21 * f2);
		float f44 = 1.0F / (f * f28 + f4 * f29 + f8 * f30 + f12 * f31);
		matrix.mat[0][0] = f28 * f44;
		matrix.mat[0][1] = f29 * f44;
		matrix.mat[0][2] = f30 * f44;
		matrix.mat[0][3] = f31 * f44;
		matrix.mat[1][0] = f32 * f44;
		matrix.mat[1][1] = f33 * f44;
		matrix.mat[1][2] = f34 * f44;
		matrix.mat[1][3] = f35 * f44;
		matrix.mat[2][0] = f36 * f44;
		matrix.mat[2][1] = f37 * f44;
		matrix.mat[2][2] = f38 * f44;
		matrix.mat[2][3] = f39 * f44;
		matrix.mat[3][0] = f40 * f44;
		matrix.mat[3][1] = f41 * f44;
		matrix.mat[3][2] = f42 * f44;
		matrix.mat[3][3] = f43 * f44;
		return matrix;
	}

	public final Matrix invert3x3() {
		Matrix matrix = new Matrix();
		matrix.mat[0][1] = mat[1][0];
		matrix.mat[0][2] = mat[2][0];
		matrix.mat[1][0] = mat[0][1];
		matrix.mat[1][2] = mat[2][1];
		matrix.mat[2][0] = mat[0][2];
		matrix.mat[2][1] = mat[1][2];
		matrix.mat[0][0] = mat[0][0];
		matrix.mat[1][1] = mat[1][1];
		matrix.mat[2][2] = mat[2][2];
		return matrix;
	}

	public final Matrix transpose() {
		Matrix matrix = new Matrix();
		matrix.mat[0][1] = mat[1][0];
		matrix.mat[0][2] = mat[2][0];
		matrix.mat[0][3] = mat[3][0];
		matrix.mat[1][0] = mat[0][1];
		matrix.mat[1][2] = mat[2][1];
		matrix.mat[1][3] = mat[3][1];
		matrix.mat[2][0] = mat[0][2];
		matrix.mat[2][1] = mat[1][2];
		matrix.mat[2][3] = mat[3][2];
		matrix.mat[3][0] = mat[0][3];
		matrix.mat[3][1] = mat[1][3];
		matrix.mat[3][2] = mat[2][3];
		matrix.mat[0][0] = mat[0][0];
		matrix.mat[1][1] = mat[1][1];
		matrix.mat[2][2] = mat[2][2];
		matrix.mat[3][3] = mat[3][3];
		return matrix;
	}

	public final void orthonormalizeDouble() {
		double ad[][] = new double[3][3];
		for (int i = 0; i < 3; i++) {
			ad[i][0] = mat[i][0];
			ad[i][1] = mat[i][1];
			ad[i][2] = mat[i][2];
		}

		for (int j = 0; j < 3; j++) {
			for (int l = 0; l < j; l++) {
				double d2 = ad[0][j];
				double d = ad[1][j];
				double d4 = ad[2][j];
				double d7 = ad[0][l];
				double d8 = ad[1][l];
				double d9 = ad[2][l];
				double d10 = d2 * d7 + d * d8 + d4 * d9;
				ad[0][l] -= d2 * d10;
				ad[1][l] -= d * d10;
				ad[2][l] -= d4 * d10;
			}

			double d3 = ad[0][j];
			double d1 = ad[1][j];
			double d5 = ad[2][j];
			double d6 = 1.0D / Math.sqrt(d3 * d3 + d1 * d1 + d5 * d5);
			ad[0][j] *= d6;
			ad[1][j] *= d6;
			ad[2][j] *= d6;
		}

		for (int k = 0; k < 3; k++) {
			mat[k][0] = (float) ad[k][0];
			mat[k][1] = (float) ad[k][1];
			mat[k][2] = (float) ad[k][2];
		}

	}

	public final void orthonormalize() {
		float f = 0.0F;
		float f3 = 0.0F;
		float f6 = 0.0F;
		float f9 = 0.0F;
		float f11 = 0.0F;
		float f13 = 0.0F;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < i; j++) {
				float f1 = mat[0][i];
				float f4 = mat[1][i];
				float f7 = mat[2][i];
				float f10 = mat[0][j];
				float f12 = mat[1][j];
				float f14 = mat[2][j];
				float f15 = f1 * f10 + f4 * f12 + f7 * f14;
				mat[0][j] -= f1 * f15;
				mat[1][j] -= f4 * f15;
				mat[2][j] -= f7 * f15;
			}

			float f2 = mat[0][i];
			float f5 = mat[1][i];
			float f8 = mat[2][i];
			double d = 1.0D / Math.sqrt(f2 * f2 + f5 * f5 + f8 * f8);
			mat[0][i] *= d;
			mat[1][i] *= d;
			mat[2][i] *= d;
		}

	}

	public final float[] getDump() {
		float af[] = new float[16];
		int i = 0;
		for (int j = 0; j < 4; j++) {
			af[i] = mat[j][0];
			af[i + 1] = mat[j][1];
			af[i + 2] = mat[j][2];
			af[i + 3] = mat[j][3];
			i += 4;
		}

		return af;
	}

	public final void setDump(float af[]) {
		if (af.length == 16) {
			int i = 0;
			for (int j = 0; j < 4; j++) {
				mat[j][0] = af[i];
				mat[j][1] = af[i + 1];
				mat[j][2] = af[i + 2];
				mat[j][3] = af[i + 3];
				i += 4;
			}

		} else {
			Logger.log("Not a valid matrix dump!", 0);
		}
	}

	public final void setTo(Matrix matrix) {
		float af[] = mat[0];
		float af1[] = matrix.mat[0];
		af[0] = af1[0];
		af[1] = af1[1];
		af[2] = af1[2];
		af[3] = af1[3];
		af = mat[1];
		af1 = matrix.mat[1];
		af[0] = af1[0];
		af[1] = af1[1];
		af[2] = af1[2];
		af[3] = af1[3];
		af = mat[2];
		af1 = matrix.mat[2];
		af[0] = af1[0];
		af[1] = af1[1];
		af[2] = af1[2];
		af[3] = af1[3];
		af = mat[3];
		af1 = matrix.mat[3];
		af[0] = af1[0];
		af[1] = af1[1];
		af[2] = af1[2];
		af[3] = af1[3];
	}

	public final void set(int i, int j, float f) {
		if (i >= 0 && i <= 4 && j >= 0 && j <= 4)
			mat[i][j] = f;
	}

	public final float get(int i, int j) {
		if (i >= 0 && i <= 4 && j >= 0 && j <= 4)
			return mat[i][j];
		else
			return 0.0F;
	}

	public String toString() {
		String s = "(\n";
		for (int i = 0; i < 4; i++) {
			s = s + "\t" + mat[i][0];
			s = s + "\t" + mat[i][1];
			s = s + "\t" + mat[i][2];
			s = s + "\t" + mat[i][3] + "\n";
		}

		s = s + ")\n";
		return s;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Matrix) {
			Matrix matrix = (Matrix) obj;
			for (int i = 0; i < 4; i++) {
				if (matrix.mat[i][0] != mat[i][0])
					return false;
				if (matrix.mat[i][1] != mat[i][1])
					return false;
				if (matrix.mat[i][2] != mat[i][2])
					return false;
				if (matrix.mat[i][3] != mat[i][3])
					return false;
			}

			return true;
		} else {
			return false;
		}
	}

	private static final long serialVersionUID = 4L;
	private static final float pi = 3.141593F;
	private static final float mpi = -3.141593F;
	private static final float pih = 1.570796F;
	private static final float mpih = -1.570796F;
	private static final float spi = (float) Math.sin(3.1415926535897931D);
	private static final float mspi = (float) Math.sin(-3.1415926535897931D);
	private static final float cpi = (float) Math.cos(3.1415926535897931D);
	private static final float mcpi = (float) Math.cos(-3.1415926535897931D);
	private static final float spih = (float) Math.sin(1.5707963267948966D);
	private static final float mspih = (float) Math.sin(-1.5707963267948966D);
	private static final float cpih = (float) Math.cos(1.5707963267948966D);
	private static final float mcpih = (float) Math.cos(-1.5707963267948966D);
	float mat[][];
	float mat0[];
	float mat1[];
	float mat2[];
	float mat3[];

}
