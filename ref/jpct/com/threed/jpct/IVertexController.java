// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

// Referenced classes of package com.threed.jpct:
//            Mesh, SimpleVector

public interface IVertexController {

	public abstract boolean init(Mesh mesh, boolean flag);

	public abstract boolean setup();

	public abstract void apply();

	public abstract SimpleVector[] getSourceMesh();

	public abstract SimpleVector[] getSourceNormals();

	public abstract SimpleVector[] getDestinationMesh();

	public abstract SimpleVector[] getDestinationNormals();

	public abstract int getMeshSize();

	public abstract void updateMesh();

	public abstract void refreshMeshData();

	public abstract void destroy();

	public abstract void cleanup();

	public abstract int[] getPolygonIDs(int i, int j);

	public static final boolean ALTER_SOURCE_MESH = true;
	public static final boolean PRESERVE_SOURCE_MESH = false;
}
