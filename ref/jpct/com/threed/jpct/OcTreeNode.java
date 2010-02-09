// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            SimpleVector, Matrix, Config

final class OcTreeNode implements Serializable {

	OcTreeNode() {
		parent = null;
		id = -1;
		polyCnt = 0;
		pointCnt = 0;
		polyList = null;
		pointList = null;
		childCnt = 0;
		children = null;
		pList = new SimpleVector[8];
		for (int i = 0; i < 8; i++)
			pList[i] = new SimpleVector(0.0F, 0.0F, 0.0F);

		parent = null;
		id = nodeID;
		nodeID++;
		polyCnt = 0;
		pointCnt = 0;
	}

	boolean isLeaf() {
		return getChildCount() == 0;
	}

	int isVisible(Matrix matrix, float f, float f1) {
		float af[][] = matrix.mat;
		float f2 = af[0][0];
		float f3 = af[1][0];
		float f4 = af[1][1];
		float f5 = af[2][1];
		float f6 = af[2][2];
		float f7 = af[1][2];
		float f8 = af[2][0];
		float f9 = af[0][2];
		float f10 = af[0][1];
		float f11 = af[3][0];
		float f12 = af[3][1];
		float f13 = af[3][2];
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		double d = f;
		double d1 = f1;
		boolean flag = false;
		boolean flag1 = false;
		for (int k1 = 0; k1 < 8; k1++) {
			SimpleVector simplevector = pList[k1];
			float f14 = simplevector.x;
			float f15 = simplevector.y;
			float f16 = simplevector.z;
			double d2 = f14 * f2 + f15 * f3 + f16 * f8 + f11;
			double d3 = f14 * f9 + f15 * f7 + f16 * f6 + f13;
			boolean flag3 = false;
			if (d3 < (double) Config.nearPlane) {
				i++;
				flag1 = true;
			} else if (d3 > (double) Config.farPlane) {
				j++;
				flag1 = true;
			} else {
				flag3 = true;
			}
			double d4 = d3 * d;
			if (d2 < -d4) {
				k++;
				flag1 = true;
			} else if (d2 > d4) {
				l++;
				flag1 = true;
			} else {
				double d5 = d3 * d1;
				double d6 = f14 * f10 + f15 * f4 + f16 * f5 + f12;
				if (d6 < -d5) {
					i1++;
					flag1 = true;
				} else if (d6 > d5) {
					j1++;
					flag1 = true;
				} else if (flag3)
					flag = true;
			}
			if (flag1 && flag)
				return 1;
		}

		boolean flag2 = j1 == 8 || l == 8 || i1 == 8 || k == 8 || i == 8 || j == 8;
		if (!flag2)
			return j1 != 0 || l != 0 || i1 != 0 || k != 0 || i != 0 || j != 0 ? 1 : 999;
		else
			return 0;
	}

	void addTriangle(int i, int j, int k, int l, int i1) {
		if (polyList == null) {
			polyList = new int[i];
			pointList = new int[i * 3];
		}
		polyList[polyCnt] = j;
		if (notInList(pointList, pointCnt, k)) {
			pointList[pointCnt] = k;
			pointCnt++;
		}
		if (notInList(pointList, pointCnt, l)) {
			pointList[pointCnt] = l;
			pointCnt++;
		}
		if (notInList(pointList, pointCnt, i1)) {
			pointList[pointCnt] = i1;
			pointCnt++;
		}
		polyCnt++;
	}

	void packPoints() {
		if (pointCnt != polyCnt * 3) {
			int ai[] = new int[pointCnt];
			System.arraycopy(pointList, 0, ai, 0, pointCnt);
			pointList = ai;
		}
	}

	private boolean notInList(int ai[], int i, int j) {
		for (int k = 0; k < i; k++)
			if (j == ai[k])
				return false;

		return true;
	}

	static void resetNodeID() {
		nodeID = 0;
	}

	int getID() {
		return id;
	}

	void addChild(OcTreeNode octreenode) {
		if (children == null)
			children = new OcTreeNode[8];
		if (childCnt < 8) {
			children[childCnt] = octreenode;
			childCnt++;
			octreenode.setParent(this);
		}
	}

	void removeChild(OcTreeNode octreenode) {
		int i = -1;
		int j = 0;
		do {
			if (j >= childCnt)
				break;
			if (children[j].equals(octreenode)) {
				i = j;
				break;
			}
			j++;
		} while (true);
		if (i != -1) {
			for (int k = i; k < childCnt - 1; k++)
				children[k] = children[k + 1];

			childCnt--;
		}
	}

	void setParent(OcTreeNode octreenode) {
		parent = octreenode;
	}

	OcTreeNode getParent() {
		return parent;
	}

	OcTreeNode[] getChildren() {
		return children;
	}

	int getChildCount() {
		return childCnt;
	}

	int getPolyCount() {
		return polyCnt;
	}

	int getPointCount() {
		return pointCnt;
	}

	int[] getPolygons() {
		return polyList;
	}

	int[] getPoints() {
		return pointList;
	}

	boolean completeFit(float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
		return f >= xLow && f <= xHigh && f3 >= xLow && f3 <= xHigh && f6 >= xLow && f6 <= xHigh && f1 >= yLow
				&& f1 <= yHigh && f4 >= yLow && f4 <= yHigh && f7 >= yLow && f7 <= yHigh && f2 >= zLow && f2 <= zHigh
				&& f5 >= zLow && f5 <= zHigh && f8 >= zLow && f8 <= zHigh;
	}

	boolean partialFit(float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
		if (f >= xLow && f <= xHigh && f1 >= yLow && f1 <= yHigh && f2 >= zLow && f2 <= zHigh)
			return true;
		if (f3 >= xLow && f3 <= xHigh && f4 >= yLow && f4 <= yHigh && f5 >= zLow && f5 <= zHigh)
			return true;
		return f6 >= xLow && f6 <= xHigh && f7 >= yLow && f7 <= yHigh && f8 >= zLow && f8 <= zHigh;
	}

	boolean sphereIntersectsNode(float f, float f1, float f2, float f3) {
		return f + f3 >= xLow && f - f3 <= xHigh && f1 + f3 >= yLow && f1 - f3 <= yHigh && f2 + f3 >= zLow
				&& f2 - f3 <= zHigh;
	}

	void extendDimensions(float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
		if (f < xLow)
			xLow = f;
		if (f3 < xLow)
			xLow = f3;
		if (f6 < xLow)
			xLow = f6;
		if (f1 < yLow)
			yLow = f1;
		if (f4 < yLow)
			yLow = f4;
		if (f7 < yLow)
			yLow = f7;
		if (f2 < zLow)
			zLow = f2;
		if (f5 < zLow)
			zLow = f5;
		if (f8 < zLow)
			zLow = f8;
		if (f > xHigh)
			xHigh = f;
		if (f3 > xHigh)
			xHigh = f3;
		if (f6 > xHigh)
			xHigh = f6;
		if (f1 > yHigh)
			yHigh = f1;
		if (f4 > yHigh)
			yHigh = f4;
		if (f7 > yHigh)
			yHigh = f7;
		if (f2 > zHigh)
			zHigh = f2;
		if (f5 > zHigh)
			zHigh = f5;
		if (f8 > zHigh)
			zHigh = f8;
		setDimensions(xLow, yLow, zLow, xHigh, yHigh, zHigh);
		if (parent != null)
			parent.extendDimensions(f, f1, f2, f3, f4, f5, f6, f7, f8);
	}

	void setDimensions(float f, float f1, float f2, float f3, float f4, float f5) {
		xLow = f;
		yLow = f1;
		zLow = f2;
		xHigh = f3;
		yHigh = f4;
		zHigh = f5;
		pList[4].x = f;
		pList[4].y = f1;
		pList[4].z = f5;
		pList[5].x = f;
		pList[5].y = f1;
		pList[5].z = f2;
		pList[6].x = f3;
		pList[6].y = f1;
		pList[6].z = f2;
		pList[7].x = f3;
		pList[7].y = f1;
		pList[7].z = f5;
		pList[0].x = f;
		pList[0].y = f4;
		pList[0].z = f5;
		pList[1].x = f;
		pList[1].y = f4;
		pList[1].z = f2;
		pList[2].x = f3;
		pList[2].y = f4;
		pList[2].z = f2;
		pList[3].x = f3;
		pList[3].y = f4;
		pList[3].z = f5;
	}

	private static final long serialVersionUID = 1L;
	private static int nodeID = 0;
	float xLow;
	float yLow;
	float zLow;
	float xHigh;
	float yHigh;
	float zHigh;
	private OcTreeNode children[];
	private OcTreeNode parent;
	private int childCnt;
	private int id;
	private int polyCnt;
	private int pointCnt;
	private int polyList[];
	private int pointList[];
	private SimpleVector pList[];

}
