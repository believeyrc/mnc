// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            SimpleVector, IVertexController, Mesh, Logger

public abstract class GenericVertexController implements IVertexController, Serializable {

	public GenericVertexController() {
		size = 0;
		initialized = false;
	}

	public final boolean init(Mesh mesh1, boolean flag) {
		boolean flag1 = true;
		if (!initialized) {
			mesh = mesh1;
			if (!mesh1.normalsCalculated)
				Logger.log("No normals have been calculated for this mesh yet!", 1);
			int i = mesh1.obbStart;
			if (i == 0 || mesh1.obbEnd + 1 != mesh1.anzCoords)
				i = mesh1.anzCoords;
			size = i;
			meshData = new SimpleVector[i];
			normalData = new SimpleVector[i];
			if (flag) {
				meshTarget = meshData;
				normalTarget = normalData;
			} else {
				meshTarget = new SimpleVector[i];
				normalTarget = new SimpleVector[i];
			}
			for (int j = 0; j < i; j++) {
				float f = mesh1.xOrg[j];
				float f1 = mesh1.yOrg[j];
				float f2 = mesh1.zOrg[j];
				float f3 = mesh1.nxOrg[j];
				float f4 = mesh1.nyOrg[j];
				float f5 = mesh1.nzOrg[j];
				if (!flag) {
					meshTarget[j] = new SimpleVector(f, f1, f2);
					normalTarget[j] = new SimpleVector(f3, f4, f5);
				}
				meshData[j] = new SimpleVector(f, f1, f2);
				normalData[j] = new SimpleVector(f3, f4, f5);
			}

			flag1 &= setup();
			initialized = flag1;
		} else {
			flag1 = false;
			Logger.log("This instance has already been assigned to another Mesh!", 0);
		}
		return flag1;
	}

	public boolean setup() {
		return true;
	}

	public final SimpleVector[] getSourceMesh() {
		return meshData;
	}

	public final SimpleVector[] getSourceNormals() {
		return normalData;
	}

	public final SimpleVector[] getDestinationMesh() {
		return meshTarget;
	}

	public final SimpleVector[] getDestinationNormals() {
		return normalTarget;
	}

	public final int getMeshSize() {
		return size;
	}

	public void refreshMeshData() {
		for (int i = 0; i < size; i++) {
			meshTarget[i].x = mesh.xOrg[i];
			meshTarget[i].y = mesh.yOrg[i];
			meshTarget[i].z = mesh.zOrg[i];
			normalData[i].x = mesh.nxOrg[i];
			normalData[i].y = mesh.nyOrg[i];
			normalData[i].z = mesh.nzOrg[i];
		}

	}

	public final void updateMesh() {
		float f3 = 1E+011F;
		float f4 = -1E+011F;
		float f5 = 1E+011F;
		float f6 = -1E+011F;
		float f7 = 1E+011F;
		float f8 = -1E+011F;
		for (int i = 0; i < size; i++) {
			float f = meshTarget[i].x;
			float f1 = meshTarget[i].y;
			float f2 = meshTarget[i].z;
			mesh.xOrg[i] = f;
			mesh.yOrg[i] = f1;
			mesh.zOrg[i] = f2;
			mesh.nxOrg[i] = normalTarget[i].x;
			mesh.nyOrg[i] = normalTarget[i].y;
			mesh.nzOrg[i] = normalTarget[i].z;
			if (f < f3)
				f3 = f;
			if (f > f4)
				f4 = f;
			if (f1 < f5)
				f5 = f1;
			if (f1 > f6)
				f6 = f1;
			if (f2 < f7)
				f7 = f2;
			if (f2 > f8)
				f8 = f2;
		}

		if (mesh.obbStart == 0) {
			mesh.obbStart = mesh.anzCoords;
			mesh.obbEnd = size + 7;
			mesh.anzCoords += 8;
		}
		int j = mesh.obbStart;
		mesh.xOrg[j] = f3;
		mesh.yOrg[j] = f5;
		mesh.zOrg[j] = f7;
		j++;
		mesh.xOrg[j] = f3;
		mesh.yOrg[j] = f5;
		mesh.zOrg[j] = f8;
		j++;
		mesh.xOrg[j] = f4;
		mesh.yOrg[j] = f5;
		mesh.zOrg[j] = f7;
		j++;
		mesh.xOrg[j] = f4;
		mesh.yOrg[j] = f5;
		mesh.zOrg[j] = f8;
		j++;
		mesh.xOrg[j] = f4;
		mesh.yOrg[j] = f6;
		mesh.zOrg[j] = f7;
		j++;
		mesh.xOrg[j] = f4;
		mesh.yOrg[j] = f6;
		mesh.zOrg[j] = f8;
		j++;
		mesh.xOrg[j] = f3;
		mesh.yOrg[j] = f6;
		mesh.zOrg[j] = f7;
		j++;
		mesh.xOrg[j] = f3;
		mesh.yOrg[j] = f6;
		mesh.zOrg[j] = f8;
	}

	public final void destroy() {
		cleanup();
		initialized = false;
	}

	public void cleanup() {
	}

	public int[] getPolygonIDs(int i, int j) {
		int ai[] = new int[j];
		int k = 0;
		for (int l = 0; l < mesh.anzTri && k < j; l++)
			if (mesh.coords[mesh.points[l][0]] == i || mesh.coords[mesh.points[l][1]] == i
					|| mesh.coords[mesh.points[l][2]] == i) {
				ai[k] = l;
				k++;
			}

		int ai1[] = new int[k];
		System.arraycopy(ai, 0, ai1, 0, k);
		return ai1;
	}

	public abstract void apply();

	private SimpleVector meshData[];
	private SimpleVector normalData[];
	private SimpleVector meshTarget[];
	private SimpleVector normalTarget[];
	private Mesh mesh;
	private int size;
	private boolean initialized;
}
