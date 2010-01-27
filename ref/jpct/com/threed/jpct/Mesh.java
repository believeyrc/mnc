// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.*;
import java.util.Hashtable;
import java.util.Vector;

// Referenced classes of package com.threed.jpct:
//            SimpleVector, IVertexController, Logger, World, 
//            Object3DList, Object3D, Matrix, Config, 
//            IntegerC

public final class Mesh implements Serializable {

	Mesh(int i) {
		serializeMethod = 0;
		myController = null;
		sxOrg = null;
		syOrg = null;
		szOrg = null;
		snxOrg = null;
		snyOrg = null;
		snzOrg = null;
		maxVectors = i;
		anzVectors = 0;
		anzCoords = 0;
		anzTri = 0;
		obbStart = 0;
		obbEnd = 0;
		normalsCalculated = false;
		points = new int[maxVectors / 3 + 1][3];
		coords = new int[maxVectors];
		xOrg = new float[maxVectors];
		yOrg = new float[maxVectors];
		zOrg = new float[maxVectors];
		nxOrg = new float[maxVectors];
		nyOrg = new float[maxVectors];
		nzOrg = new float[maxVectors];
	}

	public boolean setVertexController(IVertexController ivertexcontroller, boolean flag) {
		if (myController != null)
			myController.destroy();
		if (ivertexcontroller.init(this, flag)) {
			myController = ivertexcontroller;
			return true;
		} else {
			return false;
		}
	}

	public void applyVertexController() {
		if (myController != null) {
			myController.apply();
			myController.updateMesh();
		} else {
			Logger.log("No controller has been assigned to this mesh", 1);
		}
	}

	public void removeVertexController() {
		if (myController != null) {
			myController.destroy();
			myController = null;
		}
	}

	public void strip() {
		points = (int[][]) null;
	}

	void strongStrip(World world, Object3D object3d) {
		if (world == null)
			return;
		int i = world.objectList.size();
		for (int j = 0; j < i; j++) {
			Object3D object3d1 = world.objectList.elementAt(j);
			if (object3d1 != object3d && object3d1.objMesh == this && (!object3d1.isCompiled() || object3d1.dynamic))
				return;
		}

		nxOrg = null;
		nyOrg = null;
		nzOrg = null;
		sxOrg = null;
		syOrg = null;
		szOrg = null;
		snxOrg = null;
		snyOrg = null;
		snzOrg = null;
	}

	public void compress() {
		int i = anzCoords;
		int j = anzVectors + 8;
		if (obbEnd == 0)
			i += 8;
		float af[] = new float[i];
		float af1[] = new float[i];
		float af2[] = new float[i];
		float af3[] = new float[i];
		float af4[] = new float[i];
		float af5[] = new float[i];
		for (int k = 0; k < anzCoords; k++) {
			af[k] = xOrg[k];
			af1[k] = yOrg[k];
			af2[k] = zOrg[k];
			af3[k] = nxOrg[k];
			af4[k] = nyOrg[k];
			af5[k] = nzOrg[k];
		}

		xOrg = af;
		yOrg = af1;
		zOrg = af2;
		nxOrg = af3;
		nyOrg = af4;
		nzOrg = af5;
		if (j < maxVectors) {
			int l = j / 3 + 1;
			int ai[][] = new int[l][3];
			int ai1[] = new int[j];
			for (int i1 = 0; i1 < j; i1++)
				ai1[i1] = coords[i1];

			for (int j1 = 0; j1 < l; j1++)
				ai[j1] = points[j1];

			coords = ai1;
			points = ai;
			maxVectors = j;
		}
	}

	public Mesh cloneMesh(boolean flag) {
		Mesh mesh = new Mesh(maxVectors);
		mesh.anzCoords = anzCoords;
		mesh.anzVectors = anzVectors;
		mesh.anzTri = anzTri;
		mesh.obbStart = obbStart;
		mesh.obbEnd = obbEnd;
		for (int i = 0; i < maxVectors; i++) {
			mesh.coords[i] = coords[i];
			if (i < anzCoords) {
				mesh.xOrg[i] = xOrg[i];
				mesh.yOrg[i] = yOrg[i];
				mesh.zOrg[i] = zOrg[i];
				mesh.nxOrg[i] = nxOrg[i];
				mesh.nyOrg[i] = nyOrg[i];
				mesh.nzOrg[i] = nzOrg[i];
			}
		}

		int j = maxVectors / 3 + 1;
		for (int k = 0; k < j; k++) {
			for (int l = 0; l < 3; l++)
				mesh.points[k][l] = points[k][l];

		}

		if (flag)
			mesh.compress();
		return mesh;
	}

	public int getVertexCount() {
		return anzVectors;
	}

	public int getUniqueVertexCount() {
		return anzCoords;
	}

	public int getTriangleCount() {
		return anzTri;
	}

	public float[] getBoundingBox() {
		return calcBoundingBox();
	}

	void translateMesh(Matrix matrix, Matrix matrix1) {
		for (int i = 0; i < anzCoords; i++) {
			zOrg[i] += matrix.mat[3][2] + matrix1.mat[3][2];
			xOrg[i] += matrix.mat[3][0] + matrix1.mat[3][0];
			yOrg[i] += matrix.mat[3][1] + matrix1.mat[3][1];
		}

	}

	final float getLargestCoveredDistance() {
		float f = -1F;
		for (int i = 0; i < anzTri; i++) {
			int j = coords[points[i][0]];
			int k = coords[points[i][1]];
			int l = coords[points[i][2]];
			float f1 = xOrg[j];
			float f2 = yOrg[j];
			float f3 = zOrg[j];
			float f4 = xOrg[k];
			float f5 = yOrg[k];
			float f6 = zOrg[k];
			float f7 = xOrg[l];
			float f8 = yOrg[l];
			float f9 = zOrg[l];
			float f10 = Math.abs(f1 - f4);
			float f11 = Math.abs(f4 - f7);
			float f12 = Math.abs(f1 - f7);
			float f13 = Math.abs(f2 - f5);
			float f14 = Math.abs(f5 - f8);
			float f15 = Math.abs(f2 - f8);
			float f16 = Math.abs(f3 - f6);
			float f17 = Math.abs(f6 - f9);
			float f18 = Math.abs(f3 - f9);
			if (f10 > f)
				f = f10;
			if (f11 > f)
				f = f11;
			if (f12 > f)
				f = f12;
			if (f13 > f)
				f = f13;
			if (f14 > f)
				f = f14;
			if (f15 > f)
				f = f15;
			if (f16 > f)
				f = f16;
			if (f17 > f)
				f = f17;
			if (f18 > f)
				f = f18;
		}

		return f;
	}

	synchronized void calcNormals() {
		if (anzTri >= Config.optimizeNormalCalcTH)
			calcNormalsFast();
		else
			calcNormalsSlow();
		normalsCalculated = true;
	}

	final Vector[] calcNormalsMD2(Vector avector[]) {
		Vector avector1[] = null;
		if (avector == null)
			avector1 = new Vector[anzCoords];
		for (int i = 0; i < anzCoords; i++) {
			if (avector == null)
				avector1[i] = new Vector();
			int j = 0;
			double d = 0.0D;
			double d1 = 0.0D;
			double d2 = 0.0D;
			float f = xOrg[i];
			float f1 = yOrg[i];
			float f2 = zOrg[i];
			if (avector == null) {
				for (int k = 0; k < anzTri; k++) {
					int i1 = coords[points[k][0]];
					int k1 = coords[points[k][1]];
					int i2 = coords[points[k][2]];
					float f3 = xOrg[i2];
					float f4 = yOrg[i2];
					float f6 = zOrg[i2];
					float f8 = xOrg[i1];
					float f10 = yOrg[i1];
					float f12 = zOrg[i1];
					float f14 = xOrg[k1];
					float f16 = yOrg[k1];
					float f18 = zOrg[k1];
					if (f == f3 && f1 == f4 && f2 == f6 || f == f14 && f1 == f16 && f2 == f18 || f == f8 && f1 == f10
							&& f2 == f12) {
						avector1[i].addElement(IntegerC.valueOf(k));
						j++;
						double d4 = f3;
						double d6 = f4;
						double d8 = f6;
						double d10 = (double) f8 - d4;
						double d12 = (double) f10 - d6;
						double d14 = (double) f12 - d8;
						double d16 = (double) f14 - d4;
						double d18 = (double) f16 - d6;
						double d20 = (double) f18 - d8;
						d += d12 * d20 - d14 * d18;
						d1 += d14 * d16 - d10 * d20;
						d2 += d10 * d18 - d12 * d16;
					}
				}

			} else {
				for (int l = 0; l < avector[i].size(); l++) {
					int j1 = ((Integer) avector[i].elementAt(l)).intValue();
					int l1 = coords[points[j1][0]];
					int j2 = coords[points[j1][1]];
					int k2 = coords[points[j1][2]];
					float f5 = xOrg[k2];
					float f7 = yOrg[k2];
					float f9 = zOrg[k2];
					float f11 = xOrg[l1];
					float f13 = yOrg[l1];
					float f15 = zOrg[l1];
					float f17 = xOrg[j2];
					float f19 = yOrg[j2];
					float f20 = zOrg[j2];
					if (f == f5 && f1 == f7 && f2 == f9 || f == f17 && f1 == f19 && f2 == f20 || f == f11 && f1 == f13
							&& f2 == f15) {
						j++;
						double d5 = f5;
						double d7 = f7;
						double d9 = f9;
						double d11 = (double) f11 - d5;
						double d13 = (double) f13 - d7;
						double d15 = (double) f15 - d9;
						double d17 = (double) f17 - d5;
						double d19 = (double) f19 - d7;
						double d21 = (double) f20 - d9;
						d += d13 * d21 - d15 * d19;
						d1 += d15 * d17 - d11 * d21;
						d2 += d11 * d19 - d13 * d17;
					}
				}

			}
			if (j == 0)
				continue;
			double d3 = Math.sqrt(d * d + d1 * d1 + d2 * d2);
			if (d3 == 0.0D)
				d3 = 9.999999960041972E-013D;
			nxOrg[i] = (float) (d / d3);
			nyOrg[i] = (float) (d1 / d3);
			nzOrg[i] = (float) (d2 / d3);
		}

		if (avector == null)
			return avector1;
		else
			return avector;
	}

	SimpleVector calcCenter() {
		float f = 0.0F;
		float f1 = 0.0F;
		float f2 = 0.0F;
		int i = 0;
		for (int j = 0; j < anzTri; j++) {
			int ai[] = points[j];
			for (int k = 0; k < 3; k++) {
				int l = coords[ai[k]];
				f += xOrg[l];
				f2 += yOrg[l];
				f1 += zOrg[l];
				i++;
			}

		}

		if (i != 0) {
			float f3 = i;
			return new SimpleVector(f / f3, f2 / f3, f1 / f3);
		} else {
			return new SimpleVector(0.0F, 0.0F, 0.0F);
		}
	}

	void rotateMesh(Matrix matrix, float f, float f1, float f2, float f3) {
		float f4 = matrix.mat[0][0];
		float f5 = matrix.mat[1][0];
		float f6 = matrix.mat[2][0];
		float f7 = matrix.mat[0][1];
		float f8 = matrix.mat[1][1];
		float f9 = matrix.mat[2][1];
		float f10 = matrix.mat[0][2];
		float f11 = matrix.mat[1][2];
		float f12 = matrix.mat[2][2];
		float f13 = f;
		float f14 = f1;
		float f15 = f2;
		for (int i = 0; i < anzCoords; i++) {
			float f16 = zOrg[i] - f15;
			float f17 = xOrg[i] - f13;
			float f18 = yOrg[i] - f14;
			float f19 = f17 * f4 + f18 * f5 + f16 * f6 + f13;
			float f20 = f17 * f7 + f18 * f8 + f16 * f9 + f14;
			float f21 = f17 * f10 + f18 * f11 + f16 * f12 + f15;
			xOrg[i] = f19;
			yOrg[i] = f20;
			zOrg[i] = f21;
			f17 = nxOrg[i];
			f18 = nyOrg[i];
			f16 = nzOrg[i];
			f19 = f17 * f4 + f18 * f5 + f16 * f6;
			f20 = f17 * f7 + f18 * f8 + f16 * f9;
			f21 = f17 * f10 + f18 * f11 + f16 * f12;
			nxOrg[i] = f19 / f3;
			nyOrg[i] = f20 / f3;
			nzOrg[i] = f21 / f3;
		}

	}

	synchronized float[] calcBoundingBox() {
		float af[] = new float[6];
		float f = 1E+011F;
		float f1 = -1E+011F;
		float f2 = 1E+011F;
		float f3 = -1E+011F;
		float f4 = 1E+011F;
		float f5 = -1E+011F;
		int i = anzCoords;
		if (obbStart != 0)
			i = obbStart;
		for (int j = 0; j < i; j++) {
			float f6 = xOrg[j];
			float f7 = yOrg[j];
			float f8 = zOrg[j];
			if (f6 < f)
				f = f6;
			if (f6 > f1)
				f1 = f6;
			if (f7 < f2)
				f2 = f7;
			if (f7 > f3)
				f3 = f7;
			if (f8 < f4)
				f4 = f8;
			if (f8 > f5)
				f5 = f8;
		}

		af[0] = f;
		af[1] = f1;
		af[2] = f2;
		af[3] = f3;
		af[4] = f4;
		af[5] = f5;
		return af;
	}

	private final void calcNormalsSlow() {
		for (int i = 0; i < anzCoords; i++) {
			int j = 0;
			double d = 0.0D;
			double d1 = 0.0D;
			double d2 = 0.0D;
			float f = xOrg[i];
			float f1 = yOrg[i];
			float f2 = zOrg[i];
			for (int k = 0; k < anzTri; k++) {
				int l = coords[points[k][0]];
				int i1 = coords[points[k][1]];
				int j1 = coords[points[k][2]];
				float f3 = xOrg[j1];
				float f4 = yOrg[j1];
				float f5 = zOrg[j1];
				float f6 = xOrg[l];
				float f7 = yOrg[l];
				float f8 = zOrg[l];
				float f9 = xOrg[i1];
				float f10 = yOrg[i1];
				float f11 = zOrg[i1];
				if (f == f3 && f1 == f4 && f2 == f5 || f == f9 && f1 == f10 && f2 == f11 || f == f6 && f1 == f7 && f2 == f8) {
					j++;
					double d4 = f3;
					double d5 = f4;
					double d6 = f5;
					double d7 = (double) f6 - d4;
					double d8 = (double) f7 - d5;
					double d9 = (double) f8 - d6;
					double d10 = (double) f9 - d4;
					double d11 = (double) f10 - d5;
					double d12 = (double) f11 - d6;
					d += d8 * d12 - d9 * d11;
					d1 += d9 * d10 - d7 * d12;
					d2 += d7 * d11 - d8 * d10;
				}
			}

			if (j == 0)
				continue;
			double d3 = Math.sqrt(d * d + d1 * d1 + d2 * d2);
			if (d3 == 0.0D)
				d3 = 9.999999960041972E-013D;
			nxOrg[i] = (float) (d / d3);
			nyOrg[i] = (float) (d1 / d3);
			nzOrg[i] = (float) (d2 / d3);
		}

	}

	private final void calcNormalsFast() {
		Hashtable hashtable = new Hashtable();
		StringBuffer stringbuffer = new StringBuffer();
		Object obj = null;
		for (int i = 0; i < anzTri; i++) {
			int ai[] = points[i];
			for (int l = 0; l < 3; l++) {
				int i1 = coords[ai[l]];
				float f = xOrg[i1];
				float f1 = yOrg[i1];
				float f2 = zOrg[i1];
				stringbuffer.setLength(0);
				stringbuffer.append(f).append('/').append(f1).append('/').append(f2);
				String s = stringbuffer.toString();
				Vector vector = (Vector) hashtable.get(s);
				if (vector == null) {
					vector = new Vector(4);
					hashtable.put(s, vector);
				}
				vector.addElement(IntegerC.valueOf(i));
			}

		}

		for (int j = 0; j < anzCoords; j++) {
			int k = 0;
			double d = 0.0D;
			double d1 = 0.0D;
			double d2 = 0.0D;
			float f3 = xOrg[j];
			float f4 = yOrg[j];
			float f5 = zOrg[j];
			stringbuffer.setLength(0);
			stringbuffer.append(f3).append('/').append(f4).append('/').append(f5);
			String s1 = stringbuffer.toString();
			Object obj1 = hashtable.get(s1);
			if (obj1 != null) {
				Vector vector1 = (Vector) obj1;
				for (int j1 = 0; j1 < vector1.size(); j1++) {
					int k1 = ((Integer) vector1.elementAt(j1)).intValue();
					int l1 = coords[points[k1][0]];
					int i2 = coords[points[k1][1]];
					int j2 = coords[points[k1][2]];
					float f6 = xOrg[j2];
					float f7 = yOrg[j2];
					float f8 = zOrg[j2];
					float f9 = xOrg[l1];
					float f10 = yOrg[l1];
					float f11 = zOrg[l1];
					float f12 = xOrg[i2];
					float f13 = yOrg[i2];
					float f14 = zOrg[i2];
					k++;
					double d4 = f6;
					double d5 = f7;
					double d6 = f8;
					double d7 = (double) f9 - d4;
					double d8 = (double) f10 - d5;
					double d9 = (double) f11 - d6;
					double d10 = (double) f12 - d4;
					double d11 = (double) f13 - d5;
					double d12 = (double) f14 - d6;
					d += d8 * d12 - d9 * d11;
					d1 += d9 * d10 - d7 * d12;
					d2 += d7 * d11 - d8 * d10;
				}

			}
			if (k == 0)
				continue;
			double d3 = Math.sqrt(d * d + d1 * d1 + d2 * d2);
			if (d3 == 0.0D)
				d3 = 9.999999960041972E-013D;
			nxOrg[j] = (float) (d / d3);
			nyOrg[j] = (float) (d1 / d3);
			nzOrg[j] = (float) (d2 / d3);
		}

	}

	public void setSerializeMethod(int i) {
		serializeMethod = i;
	}

	private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
		objectinputstream.defaultReadObject();
		if ((serializeMethod & 2) != 0) {
			xOrg = shortToFloat(sxOrg);
			sxOrg = null;
			yOrg = shortToFloat(syOrg);
			syOrg = null;
			zOrg = shortToFloat(szOrg);
			szOrg = null;
			nxOrg = shortToFloat(snxOrg);
			snxOrg = null;
			nyOrg = shortToFloat(snyOrg);
			snyOrg = null;
			nzOrg = shortToFloat(snzOrg);
			snzOrg = null;
		}
		if ((serializeMethod & 1) != 0) {
			nxOrg = new float[xOrg.length];
			nyOrg = new float[yOrg.length];
			nzOrg = new float[zOrg.length];
			if (points != null)
				calcNormals();
		}
	}

	private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
		float af[] = xOrg;
		float af1[] = yOrg;
		float af2[] = zOrg;
		float af3[] = nxOrg;
		float af4[] = nyOrg;
		float af5[] = nzOrg;
		if ((serializeMethod & 1) != 0) {
			nxOrg = null;
			nyOrg = null;
			nzOrg = null;
		}
		if ((serializeMethod & 2) != 0) {
			sxOrg = floatToShort(xOrg);
			xOrg = null;
			syOrg = floatToShort(yOrg);
			yOrg = null;
			szOrg = floatToShort(zOrg);
			zOrg = null;
			snxOrg = floatToShort(nxOrg);
			nxOrg = null;
			snyOrg = floatToShort(nyOrg);
			nyOrg = null;
			snzOrg = floatToShort(nzOrg);
			nzOrg = null;
		}
		objectoutputstream.defaultWriteObject();
		xOrg = af;
		yOrg = af1;
		zOrg = af2;
		nxOrg = af3;
		nyOrg = af4;
		nzOrg = af5;
	}

	private short[] floatToShort(float af[]) {
		if (af == null)
			return null;
		short aword0[] = new short[af.length];
		for (int i = 0; i < af.length; i++)
			aword0[i] = (short) (Float.floatToRawIntBits(af[i]) >> 16);

		return aword0;
	}

	private float[] shortToFloat(short aword0[]) {
		if (aword0 == null)
			return null;
		float af[] = new float[aword0.length];
		for (int i = 0; i < aword0.length; i++)
			af[i] = Float.intBitsToFloat(aword0[i] << 16);

		return af;
	}

	private static final long serialVersionUID = 1L;
	public static final boolean COMPRESS = true;
	public static final boolean DONT_COMPRESS = false;
	public static final int SERIALIZE_ALL = 0;
	public static final int SERIALIZE_VERTICES_ONLY = 1;
	public static final int SERIALIZE_LOW_PRECISION = 2;
	private int serializeMethod;
	private IVertexController myController;
	boolean normalsCalculated;
	int anzVectors;
	int maxVectors;
	int anzCoords;
	int anzTri;
	int points[][];
	float xOrg[];
	float yOrg[];
	float zOrg[];
	float nxOrg[];
	float nyOrg[];
	float nzOrg[];
	int coords[];
	int obbStart;
	int obbEnd;
	short sxOrg[];
	short syOrg[];
	short szOrg[];
	short snxOrg[];
	short snyOrg[];
	short snzOrg[];
}
