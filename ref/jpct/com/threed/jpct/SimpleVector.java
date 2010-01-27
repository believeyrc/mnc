// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            Matrix, Logger

public class SimpleVector implements Serializable {

	public SimpleVector(float f, float f1, float f2) {
		x = f;
		y = f1;
		z = f2;
	}

	public SimpleVector(double d, double d1, double d2) {
		x = (float) d;
		y = (float) d1;
		z = (float) d2;
	}

	public SimpleVector(SimpleVector simplevector) {
		x = simplevector.x;
		y = simplevector.y;
		z = simplevector.z;
	}

	public SimpleVector() {
		x = 0.0F;
		y = 0.0F;
		z = 0.0F;
	}

	public SimpleVector(float af[]) {
		if (af.length == 3) {
			x = af[0];
			y = af[1];
			z = af[2];
		} else {
			x = 0.0F;
			y = 0.0F;
			z = 0.0F;
			Logger.log("Source-array needs to have a length of 3", 0);
		}
	}

	public void set(float f, float f1, float f2) {
		x = f;
		y = f1;
		z = f2;
	}

	public void set(SimpleVector simplevector) {
		x = simplevector.x;
		y = simplevector.y;
		z = simplevector.z;
	}

	public float[] toArray() {
		return (new float[] { x, y, z });
	}

	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
	}

	public boolean equals(Object obj) {
		if (obj instanceof SimpleVector) {
			SimpleVector simplevector = (SimpleVector) obj;
			return simplevector.x == x && simplevector.y == y && simplevector.z == z;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return (int) (x * 100F + y * 10F + z);
	}

	public SimpleVector rotate(SimpleVector simplevector) {
		float f = x;
		float f1 = y;
		float f2 = z;
		float f3 = (float) Math.sin(simplevector.x);
		float f4 = (float) Math.cos(simplevector.x);
		float f5 = (float) Math.sin(simplevector.y);
		float f6 = (float) Math.cos(simplevector.y);
		float f7 = (float) Math.sin(simplevector.z);
		float f8 = (float) Math.cos(simplevector.z);
		float f9 = f * f8 - f1 * f7;
		float f10 = f * f7 + f1 * f8;
		f = f9;
		f1 = f10;
		f9 = f * f6 - f2 * f5;
		float f11 = f * f5 + f2 * f6;
		f2 = f11;
		f10 = f1 * f4 - f2 * f3;
		f11 = f1 * f3 + f2 * f4;
		return new SimpleVector(f9, f10, f11);
	}

	public void rotateX(float f) {
		float f1 = y;
		float f2 = z;
		float f3 = (float) Math.sin(f);
		float f4 = (float) Math.cos(f);
		y = f1 * f4 - f2 * f3;
		z = f1 * f3 + f2 * f4;
	}

	public void rotateY(float f) {
		float f1 = x;
		float f2 = z;
		float f3 = (float) Math.sin(f);
		float f4 = (float) Math.cos(f);
		x = f1 * f4 - f2 * f3;
		z = f1 * f3 + f2 * f4;
	}

	public void rotateZ(float f) {
		float f1 = y;
		float f2 = x;
		float f3 = (float) Math.sin(f);
		float f4 = (float) Math.cos(f);
		x = f2 * f4 - f1 * f3;
		y = f2 * f3 + f1 * f4;
	}

	public SimpleVector normalize() {
		double d = x;
		double d1 = y;
		double d2 = z;
		double d3 = Math.sqrt(d * d + d1 * d1 + d2 * d2);
		if (d3 != 0.0D)
			return new SimpleVector((float) (d / d3), (float) (d1 / d3), (float) (d2 / d3));
		else
			return new SimpleVector(0.0F, 0.0F, 0.0F);
	}

	public void createNormal(SimpleVector simplevector, SimpleVector simplevector1, SimpleVector simplevector2) {
		float f = simplevector1.x - simplevector.x;
		float f1 = simplevector1.y - simplevector.y;
		float f2 = simplevector1.z - simplevector.z;
		float f3 = simplevector2.x - simplevector.x;
		float f4 = simplevector2.y - simplevector.y;
		float f5 = simplevector2.z - simplevector.z;
		x = f1 * f5 - f2 * f4;
		y = f2 * f3 - f * f5;
		z = f * f4 - f1 * f3;
		double d = Math.sqrt(x * x + y * y + z * z);
		if (d != 0.0D) {
			x /= d;
			y /= d;
			z /= d;
		}
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public SimpleVector calcCross(SimpleVector simplevector) {
		double d = x;
		double d1 = y;
		double d2 = z;
		double d3 = simplevector.x;
		double d4 = simplevector.y;
		double d5 = simplevector.z;
		double d6 = d1 * d5 - d2 * d4;
		double d7 = d2 * d3 - d * d5;
		double d8 = d * d4 - d1 * d3;
		return new SimpleVector((float) d6, (float) d7, (float) d8);
	}

	public float calcDot(SimpleVector simplevector) {
		return x * simplevector.x + y * simplevector.y + z * simplevector.z;
	}

	public SimpleVector calcSub(SimpleVector simplevector) {
		return new SimpleVector(x - simplevector.x, y - simplevector.y, z - simplevector.z);
	}

	public float calcAngle(SimpleVector simplevector) {
		SimpleVector simplevector1 = normalize();
		SimpleVector simplevector2 = simplevector.normalize();
		float f = simplevector1.x * simplevector2.x + simplevector1.y * simplevector2.y + simplevector1.z * simplevector2.z;
		if (f < -1F)
			f = -1F;
		if (f > 1.0F)
			f = 1.0F;
		return (float) Math.acos(f);
	}

	public void scalarMul(float f) {
		x *= f;
		y *= f;
		z *= f;
	}

	public void matMul(Matrix matrix) {
		float af[][] = matrix.mat;
		float f = x * af[0][0] + y * af[1][0] + z * af[2][0] + af[3][0];
		float f1 = x * af[0][1] + y * af[1][1] + z * af[2][1] + af[3][1];
		float f2 = x * af[0][2] + y * af[1][2] + z * af[2][2] + af[3][2];
		x = f;
		y = f1;
		z = f2;
	}

	public void rotate(Matrix matrix) {
		float af[][] = matrix.mat;
		float f = x * af[0][0] + y * af[1][0] + z * af[2][0];
		float f1 = x * af[0][1] + y * af[1][1] + z * af[2][1];
		float f2 = x * af[0][2] + y * af[1][2] + z * af[2][2];
		x = f;
		y = f1;
		z = f2;
	}

	public void add(SimpleVector simplevector) {
		x += simplevector.x;
		y += simplevector.y;
		z += simplevector.z;
	}

	public void makeEqualLength(SimpleVector simplevector) {
		float f = simplevector.length();
		float f1 = length();
		if (f1 > f) {
			SimpleVector simplevector1 = normalize();
			simplevector1.scalarMul(f);
			x = simplevector1.x;
			y = simplevector1.y;
			z = simplevector1.z;
		}
	}

	public Matrix getRotationMatrix() {
		return getRotationMatrix(DOWN);
	}

	public Matrix getRotationMatrix(SimpleVector simplevector) {
		double d = x;
		double d1 = y;
		double d2 = z;
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
		af[1][0] = 0.0F;
		af[1][1] = 1.0F;
		af[1][2] = 0.0F;
		af[2][0] = (float) d;
		af[2][1] = (float) d1;
		af[2][2] = (float) d2;
		double d4 = simplevector.x;
		double d5 = simplevector.y;
		double d6 = simplevector.z;
		double d7 = d;
		double d8 = d1;
		double d9 = d2;
		double d10 = d5 * d9 - d6 * d8;
		double d11 = d6 * d7 - d4 * d9;
		double d12 = d4 * d8 - d5 * d7;
		double d13 = 0.0D;
		double d14 = 0.0D;
		double d15 = 0.0D;
		d3 = Math.sqrt(d10 * d10 + d11 * d11 + d12 * d12);
		if (d3 != 0.0D) {
			d10 /= d3;
			d11 /= d3;
			d12 /= d3;
		}
		d13 = d8 * d12 - d9 * d11;
		d14 = d9 * d10 - d7 * d12;
		d15 = d7 * d11 - d8 * d10;
		d3 = Math.sqrt(d13 * d13 + d14 * d14 + d15 * d15);
		if (d3 != 0.0D) {
			d13 /= d3;
			d14 /= d3;
			d15 /= d3;
		}
		af[0][0] = (float) d10;
		af[0][1] = (float) d11;
		af[0][2] = (float) d12;
		af[1][0] = (float) d13;
		af[1][1] = (float) d14;
		af[1][2] = (float) d15;
		matrix.orthonormalizeDouble();
		return matrix;
	}

	private static final long serialVersionUID = 1L;
	public static final SimpleVector ORIGIN = new SimpleVector(0.0F, 0.0F, 0.0F);
	public float x;
	public float y;
	public float z;
	private static final SimpleVector DOWN = new SimpleVector(0.0F, 1.0F, 0.0F);

}
