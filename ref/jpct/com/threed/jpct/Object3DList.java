// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.NoSuchElementException;

// Referenced classes of package com.threed.jpct:
//            Object3D

final class Object3DList implements Serializable {

	Object3DList() {
		objList = null;
		SIZE = 100;
		count = 0;
		objList = new Object3D[SIZE];
	}

	Object3DList(int i) {
		objList = null;
		SIZE = 100;
		count = 0;
		SIZE = i;
		objList = new Object3D[i];
	}

	int size() {
		return count;
	}

	void clear() {
		for (int i = 0; i < count; i++)
			objList[i] = null;

		if (objList.length > 1000)
			objList = new Object3D[SIZE];
		count = 0;
	}

	Object3D[] toArray() {
		Object3D aobject3d[] = new Object3D[count];
		System.arraycopy(objList, 0, aobject3d, 0, count);
		return aobject3d;
	}

	void addElement(Object3D object3d) {
		if (count >= objList.length) {
			Object3D aobject3d[] = new Object3D[SIZE + objList.length];
			System.arraycopy(objList, 0, aobject3d, 0, objList.length);
			objList = aobject3d;
		}
		objList[count] = object3d;
		count++;
	}

	Object3D elementAt(int i) {
		return objList[i];
	}

	void removeElementAt(int i) {
		if (i + 1 < count)
			System.arraycopy(objList, i + 1, objList, i, count - i - 1);
		count--;
		objList[count] = null;
	}

	boolean removeElement(Object3D object3d) {
		for (int i = 0; i < size(); i++)
			if (objList[i].equals(object3d)) {
				removeElementAt(i);
				return true;
			}

		return false;
	}

	Enumeration elements() {
		return new Enumeration() {

			public boolean hasMoreElements() {
				return cnt < count;
			}

			public Object nextElement() {
				if (cnt < count)
					return objList[cnt++];
				else
					throw new NoSuchElementException("ObjList Enumeration");
			}

			int cnt;

			{
				cnt = 0;
			}
		};
	}

	private static final long serialVersionUID = 1L;
	private Object3D objList[];
	private int SIZE;
	private int count;

}
