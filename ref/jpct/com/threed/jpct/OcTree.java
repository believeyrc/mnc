// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;
import java.util.Vector;

// Referenced classes of package com.threed.jpct:
//            OcTreeNode, Object3D, Mesh, World, 
//            IntegerC, Logger, Matrix

public final class OcTree implements Serializable {

	public OcTree(Mesh mesh, int i, int j) {
		root = null;
		maxPoly = 0;
		maxDepth = -1;
		leafs = 0;
		curLeafs = 0;
		totalPolys = 0;
		nodes = 0;
		leafList = null;
		visibleLeafs = null;
		useForCollision = true;
		useForRendering = true;
		objMesh = null;
		tris = null;
		threadsBuffer = null;
		allLeafs = null;
		initOcTree(mesh, i, -1, j);
	}

	public OcTree(Mesh mesh, int i, int j, int k) {
		root = null;
		maxPoly = 0;
		maxDepth = -1;
		leafs = 0;
		curLeafs = 0;
		totalPolys = 0;
		nodes = 0;
		leafList = null;
		visibleLeafs = null;
		useForCollision = true;
		useForRendering = true;
		objMesh = null;
		tris = null;
		threadsBuffer = null;
		allLeafs = null;
		initOcTree(mesh, i, j, k);
	}

	public OcTree(Object3D object3d, int i, int j) {
		root = null;
		maxPoly = 0;
		maxDepth = -1;
		leafs = 0;
		curLeafs = 0;
		totalPolys = 0;
		nodes = 0;
		leafList = null;
		visibleLeafs = null;
		useForCollision = true;
		useForRendering = true;
		objMesh = null;
		tris = null;
		threadsBuffer = null;
		allLeafs = null;
		initOcTree(object3d.getMesh(), i, -1, j);
	}

	public OcTree(Object3D object3d, int i, int j, int k) {
		root = null;
		maxPoly = 0;
		maxDepth = -1;
		leafs = 0;
		curLeafs = 0;
		totalPolys = 0;
		nodes = 0;
		leafList = null;
		visibleLeafs = null;
		useForCollision = true;
		useForRendering = true;
		objMesh = null;
		tris = null;
		threadsBuffer = null;
		allLeafs = null;
		initOcTree(object3d.getMesh(), i, j, k);
	}

	private void initOcTree(Mesh mesh, int i, int j, int k) {
		leafs = 0;
		nodes = 0;
		maxDepth = j;
		objMesh = mesh.cloneMesh(false);
		maxPoly = i;
		tris = new int[i + 1];
		buildTree();
		leafList = new OcTreeNode[leafs];
		visibleLeafs = new boolean[leafs];
		objMesh = null;
		tris = null;
		mode = k;
		useForCollision = false;
		radiusMul = 1.5F;
	}

	public Vector getFilledLeafs() {
		if (allLeafs == null) {
			allLeafs = new Vector();
			fillLeafs(root);
		}
		return allLeafs;
	}

	private void fillLeafs(OcTreeNode octreenode) {
		if (octreenode.getPolyCount() > 0)
			allLeafs.add(octreenode);
		for (int i = 0; i < octreenode.getChildCount(); i++)
			fillLeafs(octreenode.getChildren()[i]);

	}

	public void setCollisionUse(boolean flag) {
		useForCollision = flag;
	}

	public void setRenderingUse(boolean flag) {
		useForRendering = flag;
	}

	public boolean getCollisionUse() {
		return useForCollision;
	}

	public boolean getRenderingUse() {
		return useForRendering;
	}

	public void setRadiusMultiplier(float f) {
		if (f > 0.0F)
			radiusMul = f;
	}

	public float getRadiusMultiplier() {
		return radiusMul;
	}

	public boolean isOfOrderZero() {
		return root.isLeaf();
	}

	int getVisibleLeafs(Matrix matrix, float f, float f1) {
		int ai[] = { 0 };
		curLeafs = 0;
		getVisibleLeafs(root, matrix, f, f1, ai);
		curLeafs = ai[0];
		return curLeafs;
	}

	Object[] getColliderLeafs(float f, float f1, float f2, float f3) {
		int ai[] = { 0 };
		return getColliderLeafs(root, f, f1, f2, f3, ai, null);
	}

	int getLeafCount() {
		return curLeafs;
	}

	int getTotalLeafs() {
		return leafs;
	}

	int getTotalPolyCount() {
		return totalPolys;
	}

	boolean isCompletelyVisible(int i) {
		return visibleLeafs[i];
	}

	OcTreeNode[] getLeafList() {
		return leafList;
	}

	private Object[] getColliderLeafs(OcTreeNode octreenode, float f, float f1, float f2, float f3, int ai[],
			OcTreeNode aoctreenode[]) {
		boolean flag = false;
		if (octreenode.getChildCount() != 0 || octreenode.getPolyCount() != 0)
			flag = octreenode.sphereIntersectsNode(f, f1, f2, f3);
		Thread thread = World.defaultThread;
		if (flag && aoctreenode == null)
			if (thread == null || Thread.currentThread() != thread || thread == Thread.currentThread()
					&& threadsBuffer == null) {
				aoctreenode = new OcTreeNode[leafs];
				if (thread != null && thread == Thread.currentThread())
					threadsBuffer = aoctreenode;
			} else if (threadsBuffer != null)
				aoctreenode = threadsBuffer;
		if (octreenode.getPolyCount() != 0 && octreenode.getChildCount() == 0 && flag) {
			aoctreenode[ai[0]] = octreenode;
			ai[0]++;
		} else if (flag && octreenode.getChildCount() != 0) {
			int i = octreenode.getChildCount();
			OcTreeNode aoctreenode1[] = octreenode.getChildren();
			for (int j = 0; j < i; j++)
				getColliderLeafs(aoctreenode1[j], f, f1, f2, f3, ai, aoctreenode);

		}
		return (new Object[] { IntegerC.valueOf(ai[0]), aoctreenode });
	}

	private void markAllLeafsAsVisible(OcTreeNode octreenode, int ai[]) {
		if (octreenode.getPolyCount() != 0 && octreenode.getChildCount() == 0) {
			leafList[ai[0]] = octreenode;
			visibleLeafs[ai[0]] = true;
			ai[0]++;
		} else {
			OcTreeNode aoctreenode[] = octreenode.getChildren();
			int i = octreenode.getChildCount();
			for (int j = 0; j < i; j++)
				markAllLeafsAsVisible(aoctreenode[j], ai);

		}
	}

	private void getVisibleLeafs(OcTreeNode octreenode, Matrix matrix, float f, float f1, int ai[]) {
		boolean flag = false;
		boolean flag1 = false;
		int i = octreenode.getChildCount();
		if (i != 0 || octreenode.getPolyCount() != 0) {
			int j = octreenode.isVisible(matrix, f, f1);
			if (j == 999) {
				flag1 = true;
				if (i != 0) {
					markAllLeafsAsVisible(octreenode, ai);
					j = 0;
				} else {
					j = 1;
				}
			}
			flag = j == 1;
		}
		if (flag && octreenode.getPolyCount() != 0 && i == 0) {
			leafList[ai[0]] = octreenode;
			visibleLeafs[ai[0]] = flag1;
			ai[0]++;
		} else if (flag && i != 0) {
			OcTreeNode aoctreenode[] = octreenode.getChildren();
			int k = i;
			for (int l = 0; l < k; l++)
				getVisibleLeafs(aoctreenode[l], matrix, f, f1, ai);

		}
	}

	private boolean createChildren(OcTreeNode octreenode, int i) {
		nodes++;
		int j = 0;
		i++;
		if (octreenode != null) {
			for (int k = 0; k < objMesh.anzTri; k++) {
				int i1 = objMesh.coords[objMesh.points[k][0]];
				if (i1 == -9999)
					continue;
				int k1 = objMesh.coords[objMesh.points[k][1]];
				int i2 = objMesh.coords[objMesh.points[k][2]];
				float f4 = objMesh.xOrg[i1];
				float f6 = objMesh.yOrg[i1];
				float f9 = objMesh.zOrg[i1];
				float f12 = objMesh.xOrg[k1];
				float f15 = objMesh.yOrg[k1];
				float f18 = objMesh.zOrg[k1];
				float f20 = objMesh.xOrg[i2];
				float f22 = objMesh.yOrg[i2];
				float f24 = objMesh.zOrg[i2];
				if (tris.length < j + 1) {
					int ai[] = new int[tris.length * 2];
					System.arraycopy(tris, 0, ai, 0, tris.length);
					tris = ai;
				}
				if (octreenode.completeFit(f4, f6, f9, f12, f15, f18, f20, f22, f24)) {
					tris[j] = k;
					j++;
				} else if (octreenode.partialFit(f4, f6, f9, f12, f15, f18, f20, f22, f24)) {
					tris[j] = k;
					j++;
				}
				if (j > maxPoly && i != maxDepth + 1)
					break;
			}

			if (j <= maxPoly || i == maxDepth + 1) {
				if (j != 0) {
					for (int l = 0; l < j; l++) {
						int j1 = tris[l];
						int l1 = objMesh.coords[objMesh.points[j1][0]];
						int j2 = objMesh.coords[objMesh.points[j1][1]];
						int k2 = objMesh.coords[objMesh.points[j1][2]];
						float f7 = objMesh.xOrg[l1];
						float f10 = objMesh.yOrg[l1];
						float f13 = objMesh.zOrg[l1];
						float f16 = objMesh.xOrg[j2];
						float f19 = objMesh.yOrg[j2];
						float f21 = objMesh.zOrg[j2];
						float f23 = objMesh.xOrg[k2];
						float f25 = objMesh.yOrg[k2];
						float f26 = objMesh.zOrg[k2];
						if (octreenode.partialFit(f7, f10, f13, f16, f19, f21, f23, f25, f26))
							octreenode.extendDimensions(f7, f10, f13, f16, f19, f21, f23, f25, f26);
						octreenode.addTriangle(j, j1, l1, j2, k2);
						objMesh.coords[objMesh.points[j1][0]] = -9999;
						objMesh.coords[objMesh.points[j1][1]] = -9999;
						objMesh.coords[objMesh.points[j1][2]] = -9999;
					}

					if (mode == 1)
						octreenode.packPoints();
					totalPolys += octreenode.getPolyCount();
					leafs++;
				}
			} else {
				float f = octreenode.xLow;
				float f1 = octreenode.yLow;
				float f2 = octreenode.zLow;
				float f3 = octreenode.xHigh;
				float f5 = octreenode.yHigh;
				float f8 = octreenode.zHigh;
				float f11 = (f3 - f) / 2.0F + f;
				float f14 = (f5 - f1) / 2.0F + f1;
				float f17 = (f8 - f2) / 2.0F + f2;
				OcTreeNode octreenode1 = new OcTreeNode();
				OcTreeNode octreenode2 = new OcTreeNode();
				OcTreeNode octreenode3 = new OcTreeNode();
				OcTreeNode octreenode4 = new OcTreeNode();
				OcTreeNode octreenode5 = new OcTreeNode();
				OcTreeNode octreenode6 = new OcTreeNode();
				OcTreeNode octreenode7 = new OcTreeNode();
				OcTreeNode octreenode8 = new OcTreeNode();
				octreenode1.setDimensions(f, f14, f2, f11, f5, f17);
				octreenode2.setDimensions(f, f14, f17, f11, f5, f8);
				octreenode3.setDimensions(f11, f14, f2, f3, f5, f17);
				octreenode4.setDimensions(f11, f14, f17, f3, f5, f8);
				octreenode5.setDimensions(f, f1, f2, f11, f14, f17);
				octreenode6.setDimensions(f, f1, f17, f11, f14, f8);
				octreenode7.setDimensions(f11, f1, f2, f3, f14, f17);
				octreenode8.setDimensions(f11, f1, f17, f3, f14, f8);
				octreenode.addChild(octreenode1);
				boolean flag = createChildren(octreenode1, i);
				if (!flag && octreenode1.getChildCount() == 0) {
					octreenode.removeChild(octreenode1);
					nodes--;
				}
				octreenode.addChild(octreenode3);
				flag = createChildren(octreenode3, i);
				if (!flag && octreenode3.getChildCount() == 0) {
					octreenode.removeChild(octreenode3);
					nodes--;
				}
				octreenode.addChild(octreenode2);
				flag = createChildren(octreenode2, i);
				if (!flag && octreenode2.getChildCount() == 0) {
					octreenode.removeChild(octreenode2);
					nodes--;
				}
				octreenode.addChild(octreenode4);
				flag = createChildren(octreenode4, i);
				if (!flag && octreenode4.getChildCount() == 0) {
					octreenode.removeChild(octreenode4);
					nodes--;
				}
				octreenode.addChild(octreenode5);
				flag = createChildren(octreenode5, i);
				if (!flag && octreenode5.getChildCount() == 0) {
					octreenode.removeChild(octreenode5);
					nodes--;
				}
				octreenode.addChild(octreenode7);
				flag = createChildren(octreenode7, i);
				if (!flag && octreenode7.getChildCount() == 0) {
					octreenode.removeChild(octreenode7);
					nodes--;
				}
				octreenode.addChild(octreenode6);
				flag = createChildren(octreenode6, i);
				if (!flag && octreenode6.getChildCount() == 0) {
					octreenode.removeChild(octreenode6);
					nodes--;
				}
				octreenode.addChild(octreenode8);
				flag = createChildren(octreenode8, i);
				if (!flag && octreenode8.getChildCount() == 0) {
					octreenode.removeChild(octreenode8);
					nodes--;
				}
			}
			return octreenode.getPolyCount() != 0;
		} else {
			return false;
		}
	}

	private void buildTree() {
		OcTreeNode.resetNodeID();
		root = new OcTreeNode();
		Logger.log("Building octree for " + objMesh.anzTri + " triangles!", 2);
		float af[] = objMesh.calcBoundingBox();
		root.setDimensions(af[0], af[2], af[4], af[1], af[3], af[5]);
		createChildren(root, 0);
		Logger.log("Octree constructed with " + nodes + " nodes / " + leafs + " leafs.", 2);
	}

	private static final long serialVersionUID = 1L;
	public static final int MODE_NORMAL = 0;
	public static final int MODE_OPTIMIZED = 1;
	public static final boolean COLLISION_USE = true;
	public static final boolean COLLISION_DONT_USE = false;
	public static final boolean RENDERING_USE = true;
	public static final boolean RENDERING_DONT_USE = false;
	private OcTreeNode root;
	private int maxPoly;
	private int maxDepth;
	private int leafs;
	private int curLeafs;
	private int totalPolys;
	private int nodes;
	private OcTreeNode leafList[];
	private boolean visibleLeafs[];
	private boolean useForCollision;
	private boolean useForRendering;
	private float radiusMul;
	private int mode;
	private Mesh objMesh;
	private int tris[];
	private OcTreeNode threadsBuffer[];
	private Vector allLeafs;
}
