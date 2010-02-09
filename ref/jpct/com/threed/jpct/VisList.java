// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;
import java.util.Vector;

// Referenced classes of package com.threed.jpct:
//            Object3D, ICompiledInstance, AWTBufferList, Logger, 
//            Config, TextureManager, Texture

public final class VisList implements Serializable {

	VisList(int i) {
		lastCycle = -1L;
		splitted = false;
		fillBuffer = -1;
		isExtracted = false;
		hasBeenSwitched = false;
		buffer = null;
		msgCnt = 0;
		maxStages = 0;
		toFill = new Vector();
		vobj = new Object3D[i + 1];
		vorg = new Object3D[i + 1];
		vnum = new int[i + 1];
		vnumOrg = new int[i + 1];
		portalNum = new int[i + 1];
		zValue = new float[i + 1];
		mode = new int[i + 1];
		stageCnt = new int[i + 1];
		size = i;
		anzpoly = -1;
	}

	public synchronized void addToFill(Object3D object3d) {
		toFill.addElement(object3d);
	}

	public int getSize() {
		return anzpoly + 1;
	}

	public int getMaxSize() {
		return size;
	}

	public void getData(int i, int ai[]) {
		if (ai.length == 2) {
			if (i >= 0 && i < anzpoly + 1) {
				ai[1] = vnumOrg[i];
				ai[0] = vorg[i].number - 2;
			} else {
				ai[0] = -1;
				ai[1] = -1;
			}
		} else {
			Logger.log("Please use an int[2]-array!", 0);
		}
	}

	void clearList() {
		anzpoly = -1;
		maxStages = 0;
		splitted = false;
	}

	void switchBuffers() {
		if (!hasBeenSwitched) {
			fillBuffer ^= 1;
			buffer[fillBuffer].clear();
			hasBeenSwitched = true;
		}
		isExtracted = false;
	}

	synchronized void fillInstances() {
		for (int i = 0; i < toFill.size(); i++) {
			Object3D object3d = (Object3D) toFill.elementAt(i);
			if (!object3d.modified)
				continue;
			for (int j = 0; j < object3d.compiled.size(); j++) {
				ICompiledInstance icompiledinstance = (ICompiledInstance) object3d.compiled.elementAt(j);
				icompiledinstance.fill();
				object3d.modified = false;
			}

		}

		toFill.clear();
	}

	AWTBufferList getFrontBuffer() {
		checkBuffer();
		return buffer[fillBuffer ^ 1];
	}

	AWTBufferList getBackBuffer() {
		checkBuffer();
		return buffer[fillBuffer];
	}

	void extract() {
		hasBeenSwitched = false;
		if (!isExtracted) {
			checkBuffer();
			buffer[fillBuffer].fill(this);
			isExtracted = true;
		}
	}

	private void checkBuffer() {
		if (buffer == null) {
			buffer = new AWTBufferList[2];
			buffer[0] = new AWTBufferList(size + 1);
			buffer[1] = new AWTBufferList(size + 1);
			fillBuffer = 0;
		}
	}

	void addToList(Object3D object3d, Object3D object3d1, int i, int j, float f, int k, boolean flag) {
		addToList(object3d, object3d1, i, j, f, k, 0, flag);
	}

	void addToList(Object3D object3d, Object3D object3d1, int i, int j, float f, int k, int l, boolean flag) {
		if (anzpoly < size) {
			anzpoly++;
			if (l > maxStages)
				maxStages = l;
			stageCnt[anzpoly] = l;
			vorg[anzpoly] = object3d1;
			vnum[anzpoly] = i;
			vnumOrg[anzpoly] = j;
			f += object3d1.sortOffset;
			if (object3d1.isTrans && !object3d1.isBumpmapped)
				zValue[anzpoly] = 3000000F - f;
			else
				zValue[anzpoly] = f;
			portalNum[anzpoly] = k;
			if (flag) {
				vobj[anzpoly] = object3d;
				int i1 = 0;
				if (object3d1.isEnvmapped)
					i1 = 1;
				if (object3d1.isTrans)
					i1 |= 2;
				if (object3d1.isBlended)
					i1 |= 4;
				if (object3d1.isBumpmapped)
					i1 |= 8;
				mode[anzpoly] = i1;
			}
		} else {
			if (msgCnt == 0)
				Logger
						.log(
								"You've exceeded the configured triangle limit for the visibility list. Consider adjusting Config.maxPolysVisible!",
								1);
			msgCnt++;
		}
	}

	void switchLists() {
		if (splitted) {
			Object3D aobject3d[] = vobj2;
			vobj2 = vobj;
			vobj = aobject3d;
			aobject3d = vorg2;
			vorg2 = vorg;
			vorg = aobject3d;
			int ai[] = vnum2;
			vnum2 = vnum;
			vnum = ai;
			ai = vnumOrg2;
			vnumOrg2 = vnumOrg;
			vnumOrg = ai;
			ai = portalNum2;
			portalNum2 = portalNum;
			portalNum = ai;
			ai = mode2;
			mode2 = mode;
			mode = ai;
			ai = stageCnt2;
			stageCnt2 = stageCnt;
			stageCnt = ai;
			float af[] = zValue2;
			zValue2 = zValue;
			zValue = af;
		}
	}

	void splitForMultiPass() {
		if (maxStages + 1 > Config.glStageCount) {
			TextureManager texturemanager = TextureManager.getInstance();
			if (vobj2 == null) {
				vobj2 = new Object3D[size + 1];
				vorg2 = new Object3D[size + 1];
				vnum2 = new int[size + 1];
				vnumOrg2 = new int[size + 1];
				portalNum2 = new int[size + 1];
				zValue2 = new float[size + 1];
				mode2 = new int[size + 1];
				stageCnt2 = new int[size + 1];
			}
			int i = anzpoly + 1;
			int j = 0;
			splitted = false;
			int k = 0;
			do {
				if (k >= i)
					break;
				Object3D object3d = vobj[k];
				Object3D object3d1 = vorg[k];
				int l = vnum[k];
				int i1 = vnumOrg[k];
				int j1 = portalNum[k];
				float f = zValue[k];
				int k1 = mode[k];
				vobj2[j] = object3d;
				vorg2[j] = object3d1;
				vnum2[j] = l;
				vnumOrg2[j] = i1;
				portalNum2[j] = j1;
				zValue2[j] = f;
				mode2[j] = k1;
				if (stageCnt[k] >= Config.glStageCount && object3d1.compiled == null) {
					splitted = true;
					stageCnt2[j] = -1;
					int l1 = stageCnt[k];
					j++;
					boolean flag = (mode[k] & 2) == 2;
					boolean flag1 = Config.glMultiPassSorting && !flag;
					int ai[][] = object3d1.multiTex;
					for (int i2 = 0; i2 < l1; i2++) {
						if (!texturemanager.textures[ai[i2][i1]].enabled)
							continue;
						vorg2[j] = object3d1;
						vnum2[j] = l;
						vnumOrg2[j] = i1;
						portalNum2[j] = j1;
						if (flag1) {
							zValue2[j] = (99999F - f) + (float) (i2 << 1) + 100000F;
						} else {
							zValue2[j] = f;
							if (flag)
								zValue2[j] += i2 + 1;
						}
						stageCnt2[j] = i2 + 1;
						if (++j < size)
							continue;
						if (msgCnt == 0)
							Logger
									.log(
											"You've exceeded the configured triangle limit for the visibility list caused by using multi pass rendering. Consider adjusting Config.maxPolysVisible!",
											1);
						msgCnt++;
						break;
					}

					stageCnt2[j - 1] |= 0x10000;
				} else {
					stageCnt2[j] = 0;
					j++;
				}
				if (j >= size) {
					if (msgCnt == 0)
						Logger
								.log(
										"You've exceeded the configured triangle limit for the visibility list caused by using multi pass rendering. Consider adjusting Config.maxPolysVisible!",
										1);
					msgCnt++;
					break;
				}
				k++;
			} while (true);
			anzpoly = j - 1;
			switchLists();
		}
	}

	void sort(int i, int j, boolean flag) {
		qsort(i, j, flag);
	}

	private void qsort(int i, int j, boolean flag) {
		findpivot(i, j);
		if (pivotindex != -1) {
			int k = partition(i, j, zValue[pivotindex], flag);
			qsort(i, k - 1, flag);
			qsort(k, j, flag);
		}
	}

	private int partition(int i, int j, float f, boolean flag) {
		while (i <= j)
			if (zValue[i] < f)
				i++;
			else if (zValue[j] >= f) {
				j--;
			} else {
				float f1 = zValue[j];
				zValue[j] = zValue[i];
				zValue[i] = f1;
				Object3D object3d = vorg[j];
				vorg[j] = vorg[i];
				vorg[i] = object3d;
				int k = vnum[j];
				vnum[j] = vnum[i];
				vnum[i] = k;
				k = vnumOrg[j];
				vnumOrg[j] = vnumOrg[i];
				vnumOrg[i] = k;
				k = portalNum[j];
				portalNum[j] = portalNum[i];
				portalNum[i] = k;
				if (flag) {
					Object3D object3d1 = vobj[j];
					vobj[j] = vobj[i];
					vobj[i] = object3d1;
					k = mode[j];
					mode[j] = mode[i];
					mode[i] = k;
				}
				k = stageCnt[j];
				stageCnt[j] = stageCnt[i];
				stageCnt[i] = k;
				i++;
				j--;
			}
		return i;
	}

	private void findpivot(int i, int j) {
		pivotindex = -1;
		int k = i;
		float f = zValue[i];
		while (pivotindex == -1 && k <= j)
			if (zValue[k] > f) {
				pivotindex = k;
			} else {
				if (zValue[k] < f)
					pivotindex = i;
				k++;
			}
	}

	private static final long serialVersionUID = 1L;
	int anzpoly;
	int size;
	Object3D vobj[];
	Object3D vorg[];
	int vnum[];
	int vnumOrg[];
	int portalNum[];
	int mode[];
	float zValue[];
	int stageCnt[];
	long lastCycle;
	private Object3D vobj2[];
	private Object3D vorg2[];
	private int vnum2[];
	private int vnumOrg2[];
	private int portalNum2[];
	private int mode2[];
	private float zValue2[];
	private int stageCnt2[];
	boolean splitted;
	private int pivotindex;
	private int fillBuffer;
	private boolean isExtracted;
	private boolean hasBeenSwitched;
	private transient AWTBufferList buffer[];
	private int msgCnt;
	private int maxStages;
	private Vector toFill;
}
