// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            Object3D, Config, Logger, SimpleVector, 
//            Vectors, Mesh

public final class Portals implements Serializable {

	Portals() {
		portalsObj = new Object3D(Config.maxPortalCoords * Config.maxPortals);
		coords = new int[Config.maxPortals][Config.maxPortalCoords];
		coordCount = new int[Config.maxPortals];
		fromSector = new int[Config.maxPortals];
		toSector = new int[Config.maxPortals];
		portalsUsed = new int[Config.maxPortals >> 1];
		isVisible = new boolean[Config.maxPortals >> 1];
		visSectors = new int[Config.maxPortals >> 1];
		onlyPortal = new int[Config.maxPortals >> 1];
		lowx = new float[Config.maxPortals];
		lowy = new float[Config.maxPortals];
		highx = new float[Config.maxPortals];
		highy = new float[Config.maxPortals];
		curSectors = new int[Config.maxPortals];
		maxPortals = Config.maxPortals;
		anzPortals = 0;
		used = 0;
		anzVisSectors = 0;
		bounding = new float[Config.maxPortals][6];
		boundingCenterX = new float[Config.maxPortals];
		boundingCenterY = new float[Config.maxPortals];
		boundingCenterZ = new float[Config.maxPortals];
		boundingWidthX = new float[Config.maxPortals];
		boundingWidthY = new float[Config.maxPortals];
		boundingWidthZ = new float[Config.maxPortals];
		for (int i = 0; i < anzPortals; i++)
			coordCount[i] = 0;

	}

	public void startNewPortal() {
		if (anzPortals == maxPortals)
			Logger.log("Maximum number of Portals reached: " + anzPortals, 1);
	}

	public void addPortalCoord(SimpleVector simplevector) {
		addPortalCoord(simplevector.x, simplevector.y, simplevector.z);
	}

	public void addPortalCoord(float f, float f1, float f2) {
		int i = portalsObj.objVectors.checkCoords(f, f1, f2, -1);
		if (i == -1) {
			i = portalsObj.objMesh.anzCoords;
			portalsObj.objMesh.xOrg[i] = f;
			portalsObj.objVectors.vertexSector[i] = -1;
			portalsObj.objMesh.yOrg[i] = f1;
			portalsObj.objMesh.zOrg[i] = f2;
			portalsObj.objMesh.anzCoords++;
		}
		addCoord(i);
	}

	public void setPortalAttributes(int i, int j) {
		fromSector[anzPortals] = i;
		toSector[anzPortals] = j;
	}

	public void completePortal() {
		if (anzPortals < maxPortals)
			anzPortals++;
		else
			Logger.log("Warning: Maximum number of Portals reached: " + anzPortals, 1);
	}

	public void setAABoundingBox(int i, float f, float f1, float f2, float f3, float f4, float f5) {
		bounding[i][0] = f;
		bounding[i][1] = f3;
		bounding[i][2] = f1;
		bounding[i][3] = f4;
		bounding[i][4] = f2;
		bounding[i][5] = f5;
		boundingCenterX[i] = f + (f3 - f) / 2.0F;
		boundingCenterY[i] = f1 + (f4 - f2) / 2.0F;
		boundingCenterZ[i] = f2 + (f5 - f2) / 2.0F;
		boundingWidthX[i] = Math.abs(f3 - f);
		boundingWidthY[i] = Math.abs(f4 - f1);
		boundingWidthZ[i] = Math.abs(f5 - f2);
	}

	int[] detectAllCoveredSectors(int ai[], float af[], float af1[], float af2[]) {
		int i = 0;
		x1 = af[0];
		y1 = af1[0];
		z1 = af2[0];
		x2 = af[1];
		y2 = af1[1];
		z2 = af2[1];
		x3 = af[2];
		y3 = af1[2];
		z3 = af2[2];
		x4 = af[3];
		y4 = af1[3];
		z4 = af2[3];
		x5 = af[4];
		y5 = af1[4];
		z5 = af2[4];
		x6 = af[5];
		y6 = af1[5];
		z6 = af2[5];
		x7 = af[6];
		y7 = af1[6];
		z7 = af2[6];
		x8 = af[7];
		y8 = af1[7];
		z8 = af2[7];
		for (int j = 0; j < anzVisSectors; j++) {
			int k = visSectors[j];
			float f = bounding[k][0];
			boolean flag = false;
			if (x1 < f && x2 < f && x3 < f && x4 < f && x5 < f && x6 < f && x7 < f && x8 < f) {
				flag = true;
			} else {
				float f1 = bounding[k][1];
				if (x1 > f1 && x2 > f1 && x3 > f1 && x4 > f1 && x5 > f1 && x6 > f1 && x7 > f1 && x8 > f1) {
					flag = true;
				} else {
					float f2 = bounding[k][2];
					if (y1 < f2 && y2 < f2 && y3 < f2 && y4 < f2 && y5 < f2 && y6 < f2 && y7 < f2 && y8 < f2) {
						flag = true;
					} else {
						float f3 = bounding[k][3];
						if (y1 > f3 && y2 > f3 && y3 > f3 && y4 > f3 && y5 > f3 && y6 > f3 && y7 > f3 && y8 > f3) {
							flag = true;
						} else {
							float f4 = bounding[k][4];
							if (z1 < f4 && z2 < f4 && z3 < f4 && z4 < f4 && z5 < f4 && z6 < f4 && z7 < f4 && z8 < f4) {
								flag = true;
							} else {
								float f5 = bounding[k][5];
								if (z1 > f5 && z2 > f5 && z3 > f5 && z4 > f5 && z5 > f5 && z6 > f5 && z7 > f5 && z8 > f5)
									flag = true;
							}
						}
					}
				}
			}
			if (!flag) {
				i++;
				ai[i] = k;
			}
		}

		ai[0] = i;
		return ai;
	}

	void calcAABoundingBox(Object3D object3d) {
		anzSectors = 0;
		int i = 0;
		int j = object3d.objMesh.anzCoords;
		for (int k = 1; k < maxPortals; k++) {
			float f = 1E+011F;
			float f1 = -1E+011F;
			float f2 = 1E+011F;
			float f3 = -1E+011F;
			float f4 = 1E+011F;
			float f5 = -1E+011F;
			boolean flag = false;
			for (int l = 0; l < j; l++) {
				if (object3d.objVectors.vertexSector[l] != k)
					continue;
				i = k;
				flag = true;
				float f6 = object3d.objMesh.xOrg[l];
				float f7 = object3d.objMesh.yOrg[l];
				float f8 = object3d.objMesh.zOrg[l];
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

			if (flag)
				setAABoundingBox(k, f, f2, f4, f1, f3, f5);
		}

		anzSectors = i;
	}

	boolean testObbAgainstPortals(Object3D object3d, float f, float f1, float f2, float f3) {
		Vectors vectors = object3d.objVectors;
		float f4 = f + f * 2.0F * Config.viewportOffsetX;
		float f5 = f1 + f1 * 2.0F * Config.viewportOffsetY;
		boolean flag = false;
		int i = (object3d.objMesh.obbEnd + 1) - object3d.objMesh.obbStart;
		if (Config.useBB && object3d.hasBoundingBox && Config.useFrustumCulling) {
			int j = object3d.clipAtPortal;
			boolean flag1 = object3d.oneSectorOnly;
			int k = 0;
			int l = used;
			if (j != -1 && flag1) {
				k = j;
				l = j + 1;
			}
			int i1 = k;
			do {
				if (i1 >= l)
					break;
				int j1 = 0;
				int k1 = 0;
				int l1 = 0;
				int i2 = 0;
				int j2 = 0;
				boolean flag2 = true;
				for (int k2 = object3d.objMesh.obbStart; k2 <= object3d.objMesh.obbEnd; k2++) {
					float f6 = 1.0F / vectors.zTr[k2];
					float f7 = f2 * (vectors.xTr[k2] * f6) + f4;
					float f8 = f3 * (vectors.yTr[k2] * f6) + f5;
					if (!object3d.dynSectorDetect) {
						if (!object3d.oneSectorOnly && object3d.sector != null)
							j2 = object3d.sector[k2];
						else
							j2 = object3d.singleSectorNumber;
					} else if (object3d.sectorCnt == 1)
						j2 = object3d.dynSectorList[1];
					else
						flag2 = false;
					if (flag2) {
						int l2 = portalsUsed[i1];
						if (toSector[l2] != j2 || !isVisible[i1])
							continue;
						float f9 = lowx[l2];
						float f10 = highx[l2];
						float f12 = lowy[l2];
						float f14 = highy[l2];
						if (f7 < f9)
							k1++;
						if (f7 > f10)
							j1++;
						if (f8 < f12)
							l1++;
						if (f8 > f14)
							i2++;
						continue;
					}
					j1 = 0;
					k1 = 0;
					l1 = 0;
					i2 = 0;
					for (int i3 = 1; i3 <= object3d.sectorCnt; i3++) {
						j2 = object3d.dynSectorList[i3];
						int j3 = portalsUsed[i1];
						if (toSector[j3] != j2 || !isVisible[i1])
							continue;
						float f11 = lowx[j3];
						float f13 = highx[j3];
						float f15 = lowy[j3];
						float f16 = highy[j3];
						if (f7 < f11)
							k1++;
						if (f7 > f13)
							j1++;
						if (f8 < f15)
							l1++;
						if (f8 > f16)
							i2++;
					}

				}

				if (!flag2)
					i *= object3d.sectorCnt;
				if (k1 != i && j1 != i && l1 != i && i2 != i) {
					i1 = l;
					flag = true;
					break;
				}
				i1++;
			} while (true);
		}
		return flag;
	}

	int testAgainstPortals(Object3D object3d, int i, int j, int k, int l) {
		Vectors vectors = object3d.objVectors;
		float f = vectors.sx[i];
		float f1 = vectors.sy[i];
		float f2 = vectors.sx[j];
		float f3 = vectors.sy[j];
		float f4 = vectors.sx[k];
		float f5 = vectors.sy[k];
		int i1 = 0;
		boolean flag = object3d.oneSectorOnly;
		int k1 = object3d.clipAtPortal;
		if (!object3d.dynSectorDetect) {
			if (!object3d.oneSectorOnly && object3d.sector != null)
				i1 = object3d.sector[l];
			else
				i1 = object3d.singleSectorNumber;
		} else {
			if (object3d.sectorCnt == 1) {
				i1 = object3d.dynSectorList[1];
				flag = true;
			} else {
				flag = false;
			}
			k1 = -1;
		}
		int l1 = -1;
		int i2 = 0;
		int j2 = used;
		if (k1 != -1 && flag) {
			i2 = k1;
			j2 = k1 + 1;
		}
		if (!object3d.dynSectorDetect || object3d.sectorCnt == 1) {
			int k2 = i2;
			do {
				if (k2 >= j2)
					break;
				int i3 = portalsUsed[k2];
				if (toSector[i3] == i1 && isVisible[k2]) {
					float f6 = lowx[i3];
					float f7 = highx[i3];
					float f9 = lowy[i3];
					float f11 = highy[i3];
					boolean flag1 = true;
					if (f < f6 && f2 < f6 && f4 < f6)
						flag1 = false;
					if (f > f7 && f2 > f7 && f4 > f7)
						flag1 = false;
					if (f1 < f9 && f3 < f9 && f5 < f9)
						flag1 = false;
					if (f1 > f11 && f3 > f11 && f5 > f11)
						flag1 = false;
					if (flag1) {
						l1 = i3;
						k2 = used;
						break;
					}
				}
				k2++;
			} while (true);
		} else {
			for (int l2 = i2; l2 < j2; l2++) {
				int j3 = portalsUsed[l2];
				for (int k3 = 1; k3 <= object3d.sectorCnt; k3++) {
					int j1 = object3d.dynSectorList[k3];
					if (toSector[j3] != j1 || !isVisible[l2])
						continue;
					float f8 = lowx[j3];
					float f10 = highx[j3];
					float f12 = lowy[j3];
					float f13 = highy[j3];
					boolean flag2 = true;
					if (f < f8 && f2 < f8 && f4 < f8)
						flag2 = false;
					if (f > f10 && f2 > f10 && f4 > f10)
						flag2 = false;
					if (f1 < f12 && f3 < f12 && f5 < f12)
						flag2 = false;
					if (f1 > f13 && f3 > f13 && f5 > f13)
						flag2 = false;
					if (!flag2)
						continue;
					l1 = 0x5f5e0ff;
					l2 = used;
					k3 = object3d.sectorCnt + 1;
					break;
				}

			}

		}
		return l1;
	}

	boolean isSectorVisible(Object3D object3d, int i) {
		boolean flag = false;
		object3d.clipAtPortal = -1;
		if (i != 0) {
			for (int j = 0; j < anzVisSectors; j++)
				if (visSectors[j] == i) {
					flag = true;
					object3d.clipAtPortal = onlyPortal[j];
					j = anzVisSectors;
				}

		} else {
			flag = true;
		}
		return flag;
	}

	int getCurrentSector(Object3D object3d, float f, float f1, float f2) {
		if (!object3d.isMainWorld)
			Logger.log("Passed an Object3D to getCurrentSector that isn't defined as main!", 0);
		float f3 = 1E+012F;
		viewSector = 0;
		int i = 0;
		for (int j = 1; j <= anzSectors; j++)
			if (f >= bounding[j][0] - 10F && f <= bounding[j][1] + 10F && f1 >= bounding[j][2] - 10F
					&& f1 <= bounding[j][3] + 10F && f2 >= bounding[j][4] - 10F && f2 <= bounding[j][5] + 10F) {
				viewSector = j;
				curSectors[i] = j;
				i++;
			}

		if (i != 1) {
			viewSector = 0;
			for (int k = 0; k < i; k++) {
				int l = object3d.sectorStartPoint[curSectors[k]];
				int i1 = object3d.sectorEndPoint[curSectors[k]] + 1;
				for (int j1 = l; j1 < i1; j1++) {
					float f4 = -object3d.objMesh.xOrg[j1] + f;
					float f5 = -object3d.objMesh.yOrg[j1] + f1;
					float f6 = -object3d.objMesh.zOrg[j1] + f2;
					if (f4 >= Config.sectorRange || f4 <= -Config.sectorRange || f5 >= Config.sectorRange
							|| f5 <= -Config.sectorRange || f6 >= Config.sectorRange || f6 <= -Config.sectorRange)
						continue;
					float f7 = f4 * f4 + f5 * f5 + f6 * f6;
					if (f7 <= f3) {
						f3 = f7;
						viewSector = object3d.objVectors.vertexSector[j1];
					}
				}

			}

		}
		return viewSector;
	}

	void processPortals(float f, float f1, float f2, float f3) {
		used = 0;
		int i = ((int) f << 1) - 1;
		int j = ((int) f1 << 1) - 1;
		float f4 = f + f * 2.0F * Config.viewportOffsetX;
		float f5 = f1 + f1 * 2.0F * Config.viewportOffsetY;
		Object3D object3d = portalsObj;
		for (int k = 0; k < anzPortals; k++) {
			int l = coords[k][2];
			int i1 = coords[k][0];
			int j1 = coords[k][1];
			float f6 = object3d.objVectors.xTr[l];
			float f7 = object3d.objVectors.yTr[l];
			float f8 = object3d.objVectors.zTr[l];
			float f9 = object3d.objVectors.xTr[i1] - f6;
			float f10 = object3d.objVectors.yTr[i1] - f7;
			float f11 = object3d.objVectors.zTr[i1] - f8;
			float f12 = object3d.objVectors.xTr[j1] - f6;
			float f13 = object3d.objVectors.yTr[j1] - f7;
			float f14 = object3d.objVectors.zTr[j1] - f8;
			float f15 = f10 * f14 - f11 * f13;
			float f16 = f11 * f12 - f9 * f14;
			float f17 = f9 * f13 - f10 * f12;
			float f18 = f15 * f6 + f16 * f7 + f17 * f8;
			if (f18 > 0.0F)
				continue;
			boolean flag = true;
			int k1 = coordCount[k];
			int l1 = 0;
			int i2 = 0;
			int j2 = 0;
			int k2 = 0;
			float f19 = 1E+009F;
			float f20 = -1F;
			float f21 = 1E+009F;
			float f22 = -1F;
			float f23 = 0.0F;
			float f25 = 0.0F;
			boolean flag1 = false;
			int l2 = 0;
			int i3 = 0;
			for (int j3 = 0; j3 < k1; j3++) {
				int k3 = coords[k][j3];
				float f27 = object3d.objVectors.zTr[k3];
				if (f27 <= 0.0F) {
					flag1 = true;
					i3++;
					if (f27 <= -20F)
						l2++;
				}
				float f24;
				float f26;
				if (object3d.objVectors.sz[k3] == -1.01F) {
					if (f27 <= 0.0F)
						f27 = 0.0001F;
					float f28 = 1.0F / f27;
					f24 = f2 * (object3d.objVectors.xTr[k3] * f28) + f4;
					f26 = f3 * (object3d.objVectors.yTr[k3] * f28) + f5;
					object3d.objVectors.sx[k3] = f24;
					object3d.objVectors.sy[k3] = f26;
				} else {
					f24 = object3d.objVectors.sx[k3];
					f26 = object3d.objVectors.sy[k3];
				}
				if (f24 < 0.0F) {
					if (!flag1)
						l1++;
					f24 = 0.0F;
				} else if (f24 > (float) i) {
					if (!flag1)
						j2++;
					f24 = i;
				}
				if (f26 < 0.0F) {
					if (!flag1)
						i2++;
					f26 = 0.0F;
				} else if (f26 > (float) j) {
					if (!flag1)
						k2++;
					f26 = j;
				}
				if (f24 < f19)
					f19 = f24;
				if (f24 > f20)
					f20 = f24;
				if (f26 < f21)
					f21 = f26;
				if (f26 > f22)
					f22 = f26;
			}

			if (flag && i3 < k1 && toSector[k] == viewSector)
				viewSector = fromSector[k];
			flag = flag && l1 != k1 && i2 != k1 && j2 != k1 && k2 != k1 && l2 < k1;
			if (!flag)
				continue;
			portalsUsed[used] = k;
			isVisible[used] = false;
			if (flag1) {
				if (f20 > 0.0F && f19 < (float) i && f22 > 0.0F && f21 < (float) j && i3 != k1) {
					lowx[k] = 0.0F;
					lowy[k] = 0.0F;
					highx[k] = i;
					highy[k] = j;
					used++;
				}
			} else {
				lowx[k] = f19;
				lowy[k] = f21;
				highx[k] = f20;
				highy[k] = f22;
				used++;
			}
			if (toSector[k] == viewSector)
				viewSector = fromSector[k];
		}

		cullPortal(viewSector, -10000F, -10000F, 10000F, 10000F, true);
		detectVisibleSectors(viewSector);
	}

	private void detectVisibleSectors(int i) {
		anzVisSectors = 1;
		visSectors[0] = i;
		onlyPortal[0] = -1;
		for (int j = 0; j < used; j++) {
			int k = portalsUsed[j];
			if (!isVisible[j])
				continue;
			int l = toSector[k];
			boolean flag = false;
			int i1 = 0;
			do {
				if (i1 >= anzVisSectors)
					break;
				if (visSectors[i1] == l) {
					onlyPortal[i1] = -1;
					i1 = anzVisSectors;
					flag = true;
					break;
				}
				i1++;
			} while (true);
			if (!flag) {
				visSectors[anzVisSectors] = l;
				onlyPortal[anzVisSectors] = j;
				anzVisSectors++;
			}
		}

	}

	private void addCoord(int i) {
		if (coordCount[anzPortals] < Config.maxPortalCoords) {
			coords[anzPortals][coordCount[anzPortals]] = i;
			coordCount[anzPortals]++;
		} else {
			Logger.log("Portal " + anzPortals + " too complex!", 0);
		}
	}

	private void cullPortal(int i, float f, float f1, float f2, float f3, boolean flag) {
		for (int j = 0; j < used; j++) {
			int k = portalsUsed[j];
			if (fromSector[k] != i || isVisible[j])
				continue;
			float f4 = lowx[k];
			float f5 = highx[k];
			float f6 = lowy[k];
			float f7 = highy[k];
			if (flag) {
				isVisible[j] = true;
				cullPortal(toSector[k], f4, f6, f5, f7, false);
				continue;
			}
			boolean flag1 = true;
			if (f < f4 && f2 < f4)
				flag1 = false;
			if (f > f5 && f2 > f5)
				flag1 = false;
			if (f1 < f6 && f3 < f6)
				flag1 = false;
			if (f1 > f7 && f3 > f7)
				flag1 = false;
			if (flag1) {
				isVisible[j] = true;
				cullPortal(toSector[k], f4, f6, f5, f7, false);
			}
		}

	}

	private static final long serialVersionUID = 1L;
	public static final int PORTAL_NOTDEFINITE = 0x5f5e0ff;
	public static final int SECTOR_UNDEFINED = 0;
	int anzPortals;
	int anzSectors;
	int viewSector;
	int visSectors[];
	int anzVisSectors;
	Object3D portalsObj;
	float lowx[];
	float lowy[];
	float highx[];
	float highy[];
	float bounding[][];
	int fromSector[];
	int toSector[];
	int coords[][];
	int coordCount[];
	private float boundingCenterX[];
	private float boundingCenterY[];
	private float boundingCenterZ[];
	private float boundingWidthX[];
	private float boundingWidthY[];
	private float boundingWidthZ[];
	private int maxPortals;
	private int used;
	private int curSectors[];
	private int onlyPortal[];
	private boolean isVisible[];
	private int portalsUsed[];
	private transient float x1;
	private transient float x2;
	private transient float x3;
	private transient float x4;
	private transient float x5;
	private transient float x6;
	private transient float x7;
	private transient float y1;
	private transient float y2;
	private transient float y3;
	private transient float y4;
	private transient float y5;
	private transient float y6;
	private transient float y7;
	private transient float z1;
	private transient float z2;
	private transient float z3;
	private transient float z4;
	private transient float z5;
	private transient float z6;
	private transient float z7;
	private transient float x8;
	private transient float y8;
	private transient float z8;
}
