// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

// Referenced classes of package com.threed.jpct:
//            Object3D

public class CollisionEvent {

	CollisionEvent(Object3D object3d, Object3D object3d1, int i, int j, Object3D aobject3d[]) {
		obj = null;
		source = null;
		type = 0;
		algorithm = 0;
		ids = null;
		targets = null;
		obj = object3d;
		type = i;
		algorithm = j;
		source = object3d1;
		targets = aobject3d;
	}

	public Object3D getObject() {
		return obj;
	}

	public Object3D[] getTargets() {
		return targets;
	}

	public Object3D getSource() {
		if (type == 1)
			return obj;
		else
			return source;
	}

	public int getType() {
		return type;
	}

	public int getAlgorithm() {
		return algorithm;
	}

	public int[] getPolygonIDs() {
		if (type == 1)
			return null;
		else
			return ids;
	}

	public String toString() {
		return "Object: " + obj.getName() + "/" + TYPES[type] + "/" + ALGOS[algorithm];
	}

	void setPolygonIDs(int ai[], int i) {
		if (ai != null) {
			ids = new int[i];
			System.arraycopy(ai, 0, ids, 0, i);
		}
	}

	private static final long serialVersionUID = 1L;
	public static final int TYPE_TARGET = 0;
	public static final int TYPE_SOURCE = 1;
	public static final int ALGORITHM_RAY = 0;
	public static final int ALGORITHM_SPHERE = 1;
	public static final int ALGORITHM_ELLIPSOID = 2;
	private Object3D obj;
	private Object3D source;
	private int type;
	private int algorithm;
	private int ids[];
	private Object3D targets[];
	private static final String TYPES[] = { "target", "source" };
	private static final String ALGOS[] = { "ray-polygon", "sphere-polygon", "ellipsoid-polygon" };

}
