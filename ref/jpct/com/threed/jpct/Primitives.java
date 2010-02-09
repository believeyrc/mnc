// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

// Referenced classes of package com.threed.jpct:
//            Object3D, SimpleVector

public class Primitives {

	private Primitives() {
	}

	public static Object3D getPlane(int i, float f) {
		float f1 = (-f * (float) i) / 2.0F;
		float f2 = f1;
		float f3 = 0.0F;
		float f4 = 0.0F;
		float f5 = 1.0F / (float) i;
		Object3D object3d = new Object3D(i * i * 2 + 8);
		for (int j = 0; j < i; j++) {
			for (int k = 0; k < i; k++) {
				float f6 = f3 + f5;
				float f7 = f4 + f5;
				if (f6 > 1.0F)
					f6 = 1.0F;
				if (f7 > 1.0F)
					f7 = 1.0F;
				object3d.addTriangle(f1, f2, 0.0F, f3, f4, f1, f2 + f, 0.0F, f3, f7, f1 + f, f2, 0.0F, f6, f4);
				object3d.addTriangle(f1, f2 + f, 0.0F, f3, f7, f1 + f, f2 + f, 0.0F, f6, f7, f1 + f, f2, 0.0F, f6, f4);
				f1 += f;
				f3 += f5;
			}

			f2 += f;
			f1 = (-f * (float) i) / 2.0F;
			f3 = 0.0F;
			f4 += f5;
		}

		return object3d;
	}

	public static Object3D getCone(float f) {
		return getCone(90, f);
	}

	public static Object3D getCone(int i, float f) {
		f *= 2.0F;
		return createLatheObject(i, new SimpleVector[] { new SimpleVector(0.5F, 0.5F, 0.0F) }, f);
	}

	public static Object3D getCone(int i, float f, float f1) {
		f *= 2.0F;
		return createLatheObject(i, new SimpleVector[] { new SimpleVector(0.5F, 0.5F * f1, 0.0F) }, f, f1);
	}

	public static Object3D getCylinder(float f) {
		return getCylinder(90, f);
	}

	public static Object3D getCylinder(int i, float f) {
		f *= 2.0F;
		return createLatheObject(i, new SimpleVector[] { new SimpleVector(0.5F, 0.5F, 0.0F),
				new SimpleVector(0.5F, -0.5F, 0.0F) }, f);
	}

	public static Object3D getCylinder(int i, float f, float f1) {
		f *= 2.0F;
		return createLatheObject(i, new SimpleVector[] { new SimpleVector(0.5F, 0.5F * f1, 0.0F),
				new SimpleVector(0.5F, -0.5F * f1, 0.0F) }, f, f1);
	}

	public static Object3D getPyramide(float f) {
		f *= 2.0F;
		return createLatheObject(4, new SimpleVector[] { new SimpleVector(HRT, 0.5F, 0.0F) }, f);
	}

	public static Object3D getPyramide(float f, float f1) {
		f *= 2.0F;
		return createLatheObject(4, new SimpleVector[] { new SimpleVector(HRT, 0.5F * f1, 0.0F) }, f, f1);
	}

	public static Object3D getDoubleCone(float f) {
		return getDoubleCone(90, f);
	}

	public static Object3D getDoubleCone(int i, float f) {
		f *= 2.0F;
		return createLatheObject(i, new SimpleVector[] { new SimpleVector(0.5F, 0.0F, 0.0F) }, f);
	}

	public static Object3D getCube(float f) {
		f *= 2.0F;
		return createLatheObject(4, new SimpleVector[] { new SimpleVector(HRT, 0.5F, 0.0F),
				new SimpleVector(HRT, -0.5F, 0.0F) }, f);
	}

	public static Object3D getBox(float f, float f1) {
		f *= 2.0F;
		return createLatheObject(4, new SimpleVector[] { new SimpleVector(HRT, 0.5F * f1, 0.0F),
				new SimpleVector(HRT, -0.5F * f1, 0.0F) }, f, f1);
	}

	public static Object3D getSphere(float f) {
		return getSphere(20, f);
	}

	public static Object3D getSphere(int i, float f) {
		return getEllipsoid(i, f, 1.0F);
	}

	public static Object3D getEllipsoid(float f, float f1) {
		return getEllipsoid(20, f, f1);
	}

	public static Object3D getEllipsoid(int i, float f, float f1) {
		f *= 2.0F;
		SimpleVector asimplevector[] = new SimpleVector[i];
		for (int j = 0; j < asimplevector.length; j++) {
			float f2 = ((float) j + 1.0F) / ((float) asimplevector.length + 1.0F);
			asimplevector[j] = new SimpleVector((float) Math.sin((double) f2 * 3.1415926535897931D) * 0.5F, (float) Math
					.cos((double) f2 * 3.1415926535897931D)
					* 0.5F * f1, 0.0F);
		}

		return createLatheObject(i, asimplevector, f, f1);
	}

	private static final Object3D createLatheObject(int i, SimpleVector asimplevector[], float f) {
		return createLatheObject(i, asimplevector, f, 1.0F);
	}

	private static final Object3D createLatheObject(int i, SimpleVector asimplevector[], float f, float f1) {
		Object3D object3d = new Object3D(i * 2 + i * 2 * (asimplevector.length - 1) + 1);
		SimpleVector asimplevector1[] = new SimpleVector[i * asimplevector.length + 2];
		asimplevector1[0] = new SimpleVector(0.0F, 0.5F * f1, 0.0F);
		asimplevector1[1] = new SimpleVector(0.0F, -0.5F * f1, 0.0F);
		int j = 2;
		for (int k = 0; k < i; k++) {
			for (int j1 = 0; j1 < asimplevector.length; j1++) {
				float f2 = (float) k / (float) i;
				asimplevector1[j++] = asimplevector[j1].rotate(new SimpleVector(0.0F, f2 * 2.0F * 3.141593F, 0.0F));
			}

		}

		for (int l = 0; l < i; l++) {
			int k1 = ((l + 0) % i) * asimplevector.length + 2;
			int i2 = ((l + 1) % i) * asimplevector.length + 2;
			object3d.addTriangle(f * asimplevector1[0].x, f * asimplevector1[0].y, f * asimplevector1[0].z, f
					* asimplevector1[i2].x, f * asimplevector1[i2].y, f * asimplevector1[i2].z, f * asimplevector1[k1].x, f
					* asimplevector1[k1].y, f * asimplevector1[k1].z);
			int k2 = (k1 + asimplevector.length) - 1;
			int i3 = (i2 + asimplevector.length) - 1;
			object3d.addTriangle(f * asimplevector1[1].x, f * asimplevector1[1].y, f * asimplevector1[1].z, f
					* asimplevector1[k2].x, f * asimplevector1[k2].y, f * asimplevector1[k2].z, f * asimplevector1[i3].x, f
					* asimplevector1[i3].y, f * asimplevector1[i3].z);
		}

		for (int i1 = 0; i1 < asimplevector.length - 1; i1++) {
			for (int l1 = 0; l1 < i; l1++) {
				int j2 = ((l1 + 0) % i) * asimplevector.length + i1 + 2;
				int l2 = j2 + 1;
				int j3 = ((l1 + 1) % i) * asimplevector.length + i1 + 2;
				int k3 = j3 + 1;
				object3d.addTriangle(f * asimplevector1[j2].x, f * asimplevector1[j2].y, f * asimplevector1[j2].z, f
						* asimplevector1[k3].x, f * asimplevector1[k3].y, f * asimplevector1[k3].z, f * asimplevector1[l2].x, f
						* asimplevector1[l2].y, f * asimplevector1[l2].z);
				object3d.addTriangle(f * asimplevector1[j2].x, f * asimplevector1[j2].y, f * asimplevector1[j2].z, f
						* asimplevector1[j3].x, f * asimplevector1[j3].y, f * asimplevector1[j3].z, f * asimplevector1[k3].x, f
						* asimplevector1[k3].y, f * asimplevector1[k3].z);
			}

		}

		return object3d;
	}

	private static final float HRT = (float) (Math.sqrt(2D) / 2D);

}
