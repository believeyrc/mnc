// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

// Referenced classes of package com.threed.jpct:
//            SimpleVector, Matrix, FrameBuffer, Camera, 
//            Config, Object3D, Logger, World, 
//            VisList, Mesh, Vectors, BufferedMatrix

public final class Interact2D {

	private Interact2D() {
	}

	public static SimpleVector reproject2D3D(Camera camera, FrameBuffer framebuffer, int i, int j) {
		return reproject2D3D(camera, framebuffer, i, j, 1.0F);
	}

	public static SimpleVector reproject2D3D(Camera camera, FrameBuffer framebuffer, int i, int j, float f) {
		camera.calcFOV(framebuffer.width, framebuffer.height);
		float f1 = framebuffer.middleX + framebuffer.middleX * 2.0F * Config.viewportOffsetX;
		float f2 = framebuffer.middleY + framebuffer.middleY * 2.0F * Config.viewportOffsetY;
		float f3 = (((float) i - f1) * f) / camera.scaleX;
		float f4 = (((float) j - f2) * f) / camera.scaleY;
		return new SimpleVector(f3, f4, f);
	}

	public static SimpleVector project3D2D(Camera camera, FrameBuffer framebuffer, SimpleVector simplevector) {
		return project3D2DInternal(camera, framebuffer, simplevector, null);
	}

	public static SimpleVector projectCenter3D2D(FrameBuffer framebuffer, Object3D object3d) {
		return projectCenter3D2D(null, framebuffer, object3d);
	}

	public static SimpleVector projectCenter3D2D(Camera camera, FrameBuffer framebuffer, Object3D object3d) {
		if (camera == null && object3d.myWorld == null) {
			Logger.log("Object doesn't belong to a world!", 0);
			return new SimpleVector();
		}
		if (camera == null)
			camera = object3d.myWorld.camera;
		return project3D2DInternal(camera, framebuffer, object3d.getCenter(), object3d.getWorldTransformation());
	}

	public static int getObjectID(int ai[]) {
		if (ai == null || ai.length < 2)
			return -1;
		else
			return ai[0];
	}

	public static int getPolygonID(int ai[]) {
		if (ai == null || ai.length < 2)
			return -1;
		else
			return ai[1];
	}

	public static int[] pickPolygon(VisList vislist, SimpleVector simplevector) {
		return pickPolygon(vislist, ORIGIN, simplevector, 0);
	}

	public static int[] pickPolygon(VisList vislist, SimpleVector simplevector, int i) {
		return pickPolygon(vislist, ORIGIN, simplevector, i);
	}

	public static int[] pickPolygon(VisList vislist, SimpleVector simplevector, SimpleVector simplevector1) {
		return pickPolygon(vislist, simplevector, simplevector1, 0);
	}

	public static int[] pickPolygon(VisList vislist, SimpleVector simplevector, SimpleVector simplevector1, int i) {
		float af[] = simplevector1.toArray();
		float af1[] = simplevector.toArray();
		boolean flag = (i & 1) == 1;
		boolean flag1 = (i & 2) == 2;
		float f = (float) Math.sqrt(af[0] * af[0] + af[1] * af[1] + af[2] * af[2]);
		af[0] /= f;
		af[1] /= f;
		af[2] /= f;
		float af2[] = new float[3];
		float af3[] = new float[3];
		float af4[] = new float[3];
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = -1;
		float f1 = (1.0F / 0.0F);
		float f2 = (1.0F / 0.0F);
		for (int j1 = 0; j1 <= vislist.anzpoly; j1++) {
			Object3D object3d = vislist.vorg[j1];
			if (flag && object3d.isTrans || flag1 && !object3d.isSelectable || object3d.compiled != null)
				continue;
			int k1 = vislist.vnumOrg[j1];
			int l1 = object3d.objMesh.coords[object3d.objMesh.points[k1][0]];
			float f3 = object3d.objVectors.xTr[l1];
			float f4 = object3d.objVectors.yTr[l1];
			float f5 = object3d.objVectors.zTr[l1];
			int i2 = object3d.objMesh.coords[object3d.objMesh.points[k1][2]];
			int j2 = object3d.objMesh.coords[object3d.objMesh.points[k1][1]];
			af2[0] = f3;
			af2[1] = f4;
			af2[2] = f5;
			af3[0] = object3d.objVectors.xTr[j2];
			af3[1] = object3d.objVectors.yTr[j2];
			af3[2] = object3d.objVectors.zTr[j2];
			af4[0] = object3d.objVectors.xTr[i2];
			af4[1] = object3d.objVectors.yTr[i2];
			af4[2] = object3d.objVectors.zTr[i2];
			if ((f2 > f5 || f2 > af3[2] || f2 > af4[2]) && (f5 > 0.0F || af3[2] > 0.0F || af4[2] > 0.0F)) {
				float af5[] = Vectors.calcSub(af3, af2);
				float af6[] = Vectors.calcSub(af4, af2);
				float af7[] = Vectors.calcCross(af, af6);
				float f6 = Vectors.calcDot(af5, af7);
				if (f6 >= 1E-016F) {
					float f7 = 1.0F / f6;
					float af8[] = Vectors.calcSub(af1, af2);
					float f8 = Vectors.calcDot(af8, af7) * f7;
					if ((double) f8 >= 0.0D && f8 <= 1.0F) {
						float af9[] = Vectors.calcCross(af8, af5);
						float f9 = Vectors.calcDot(af, af9) * f7;
						if ((double) f9 >= 0.0D && (double) (f8 + f9) <= 1.0D) {
							float f10 = Vectors.calcDot(af6, af9) * f7;
							if (f10 >= 0.0F && f10 < f1) {
								f1 = f10;
								i1 = j1;
								float f11 = af2[2];
								if (af3[2] > f11)
									f11 = af3[2];
								if (af4[2] > f11)
									f11 = af4[2];
								if (f2 > f11)
									f2 = f11;
							}
							j++;
						}
					}
				}
			} else {
				k++;
			}
			l++;
		}

		if (i1 != -1 && vislist.vorg[i1].isSelectable)
			return (new int[] { vislist.vorg[i1].number - 2, vislist.vnumOrg[i1] });
		else
			return null;
	}

	static SimpleVector reproject2D3DBlit(float f, float f1, FrameBuffer framebuffer, int i, int j, float f2,
			SimpleVector simplevector) {
		if (simplevector == null)
			simplevector = new SimpleVector();
		float f3 = (((float) i - framebuffer.middleX) * f2) / f;
		float f4 = (((float) j - framebuffer.middleY) * f2) / f1;
		simplevector.x = f3;
		simplevector.y = f4;
		simplevector.z = f2;
		return simplevector;
	}

	static SimpleVector reproject2D3DBlit(float f, float f1, FrameBuffer framebuffer, int i, int j, float f2, float f3,
			SimpleVector simplevector) {
		if (simplevector == null)
			simplevector = new SimpleVector();
		float f4 = 1.0F / f3;
		float f5 = (((float) i - framebuffer.middleX * f4) * f2) / (f * f4);
		float f6 = (((float) j - framebuffer.middleY * f4) * f2) / (f1 * f4);
		simplevector.x = f5;
		simplevector.y = f6;
		simplevector.z = f2;
		return simplevector;
	}

	private static SimpleVector project3D2DInternal(Camera camera, FrameBuffer framebuffer, SimpleVector simplevector,
			Matrix matrix) {
		Matrix matrix1 = ((BufferedMatrix) (camera)).backMatrix;
		Matrix matrix2 = new Matrix();
		matrix2.mat[3][0] = -((BufferedMatrix) (camera)).backBx;
		matrix2.mat[3][1] = -((BufferedMatrix) (camera)).backBy;
		matrix2.mat[3][2] = -((BufferedMatrix) (camera)).backBz;
		if (matrix != null)
			matrix.matMul(matrix2);
		else
			matrix = matrix2;
		matrix.matMul(matrix1);
		float f = matrix.mat[2][2];
		float f1 = matrix.mat[1][2];
		float f2 = matrix.mat[0][2];
		float f3 = matrix.mat[3][2];
		float f4 = simplevector.x;
		float f5 = simplevector.y;
		float f6 = simplevector.z;
		float f7 = f4 * f2 + f5 * f1 + f6 * f + f3;
		if (f7 > 0.0F) {
			float f8 = matrix.mat[0][0];
			float f9 = matrix.mat[1][0];
			float f10 = matrix.mat[1][1];
			float f11 = matrix.mat[2][1];
			float f12 = matrix.mat[2][0];
			float f13 = matrix.mat[0][1];
			float f14 = matrix.mat[3][0];
			float f15 = matrix.mat[3][1];
			float f16 = f4 * f8 + f5 * f9 + f6 * f12 + f14;
			float f17 = f4 * f13 + f5 * f10 + f6 * f11 + f15;
			float f18 = framebuffer.middleX + framebuffer.middleX * 2.0F * Config.viewportOffsetX;
			float f19 = framebuffer.middleY + framebuffer.middleY * 2.0F * Config.viewportOffsetY;
			camera.calcFOV(framebuffer.width, framebuffer.height);
			float f20 = 1.0F / f7;
			float f21 = camera.scaleX * (f16 * f20) + f18;
			float f22 = camera.scaleY * (f17 * f20) + f19;
			return new SimpleVector(f21, f22, f20);
		} else {
			return null;
		}
	}

	public static final int EXCLUDE_TRANSPARENT = 1;
	public static final int EXCLUDE_NOT_SELECTABLE = 2;
	private static final float VIEWPLANE_Z_VALUE = 1F;
	private static final float EPSILON = 1E-016F;
	private static final SimpleVector ORIGIN = new SimpleVector(0.0F, 0.0F, 0.0F);

}
