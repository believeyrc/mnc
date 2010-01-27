// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

// Referenced classes of package com.threed.jpct:
//            SimpleVector, TextureManager, Object3D, Config, 
//            Vectors, TextureInfo, Texture, Mesh, 
//            Logger

public class PolygonManager {

	PolygonManager(Object3D object3d) {
		myObj = null;
		texMan = null;
		myObj = object3d;
		texMan = TextureManager.getInstance();
	}

	public int getPolygonTexture(int i) {
		if (outOfBounds(i))
			return -1;
		else
			return myObj.texture[i];
	}

	public int[] getPolygonTextures(int i) {
		if (outOfBounds(i))
			return null;
		int j = 0;
		if (myObj.multiTex != null)
			j = myObj.multiTex.length;
		int ai[] = new int[1 + j];
		ai[0] = myObj.texture[i];
		if (myObj.multiTex != null) {
			for (int k = 0; k < myObj.multiTex.length; k++)
				ai[1 + k] = myObj.multiTex[k][i];

		}
		return ai;
	}

	public void setPolygonTexture(int i, int j) {
		if (outOfBounds(i)) {
			return;
		} else {
			myObj.texture[i] = j;
			return;
		}
	}

	public void addTexture(int i, int j, int k) {
		if (myObj.multiTex == null) {
			myObj.multiTex = new int[Config.maxTextureLayers - 1][myObj.texture.length];
			myObj.multiMode = new int[Config.maxTextureLayers - 1][myObj.texture.length];
			for (int l = 0; l < myObj.texture.length; l++) {
				for (int j1 = 0; j1 < Config.maxTextureLayers - 1; j1++)
					myObj.multiTex[j1][l] = -1;

			}

			myObj.objVectors.createMultiCoords();
		}
		int i1 = 0;
		do {
			if (i1 >= 3)
				break;
			if (myObj.multiTex[i1][i] == -1) {
				myObj.multiTex[i1][i] = j;
				myObj.multiMode[i1][i] = k;
				break;
			}
			i1++;
		} while (true);
		myObj.usesMultiTexturing = true;
		myObj.maxStagesUsed++;
		if (myObj.maxStagesUsed > Config.maxTextureLayers)
			myObj.maxStagesUsed = Config.maxTextureLayers;
	}

	public void setPolygonTexture(int i, TextureInfo textureinfo) {
		if (textureinfo != null) {
			if (myObj.multiTex == null && textureinfo.stageCnt > 1) {
				myObj.multiTex = new int[Config.maxTextureLayers - 1][myObj.texture.length];
				myObj.multiMode = new int[Config.maxTextureLayers - 1][myObj.texture.length];
				for (int j = 0; j < myObj.texture.length; j++) {
					for (int k = 0; k < Config.maxTextureLayers - 1; k++)
						myObj.multiTex[k][j] = -1;

				}

				myObj.objVectors.createMultiCoords();
				myObj.usesMultiTexturing = true;
			}
			Vectors vectors = myObj.objVectors;
			float af[] = { textureinfo.u0[0], textureinfo.u1[0], textureinfo.u2[0] };
			float af1[] = { textureinfo.v0[0], textureinfo.v1[0], textureinfo.v2[0] };
			int l = textureinfo.textures[0];
			if (myObj.maxStagesUsed < textureinfo.stageCnt)
				myObj.maxStagesUsed = textureinfo.stageCnt;
			boolean flag = myObj.usesMultiTexturing && textureinfo != null;
			myObj.texture[i] = l;
			myObj.basemap[i] = l;
			if (flag) {
				for (int i1 = 0; i1 < textureinfo.stageCnt - 1; i1++) {
					myObj.multiTex[i1][i] = textureinfo.textures[i1 + 1];
					myObj.multiMode[i1][i] = textureinfo.mode[i1 + 1];
				}

				for (int j1 = textureinfo.stageCnt - 1; j1 < Config.maxTextureLayers - 1; j1++)
					myObj.multiTex[j1][i] = -1;

			}
			float f = 2.0F;
			float f1 = 2.0F;
			if (l != -1) {
				f = texMan.textures[l].width;
				f1 = texMan.textures[l].height;
			}
			float af2[] = null;
			float af3[] = null;
			for (int k1 = 0; k1 < 3; k1++) {
				int l1 = myObj.objMesh.points[i][k1];
				vectors.nuOrg[l1] = af[k1];
				vectors.nvOrg[l1] = af1[k1];
				vectors.uOrg[l1] = af[k1] * f;
				vectors.vOrg[l1] = af1[k1] * f1;
				vectors.buOrg[l1] = af[k1];
				vectors.bvOrg[l1] = af1[k1];
				switch (k1) {
				case 0: // '\0'
					af2 = textureinfo.u0;
					af3 = textureinfo.v0;
					break;

				case 1: // '\001'
					af2 = textureinfo.u1;
					af3 = textureinfo.v1;
					break;

				case 2: // '\002'
					af2 = textureinfo.u2;
					af3 = textureinfo.v2;
					break;
				}
				for (int i2 = 0; i2 < textureinfo.stageCnt - 1; i2++) {
					vectors.uMul[i2][l1] = af2[i2 + 1];
					vectors.vMul[i2][l1] = af3[i2 + 1];
				}

			}

		}
	}

	public void setVertexAlpha(int i, int j, float f) {
		if (outOfBounds(i) || j < 0 || j > 2)
			return;
		int k = myObj.objMesh.points[i][j];
		Vectors vectors = myObj.objVectors;
		vectors.createAlpha();
		if (f < 0.0F)
			f = 0.0F;
		else if (f > 1.0F)
			f = 1.0F;
		vectors.alpha[k] = f;
	}

	public SimpleVector getTransformedVertex(int i, int j) {
		if (outOfBounds(i) || j < 0 || j > 2) {
			return null;
		} else {
			Matrix matrix = myObj.getWorldTransformation();
			SimpleVector simplevector = new SimpleVector();
			Mesh mesh = myObj.objMesh;
			int k = mesh.coords[mesh.points[i][j]];
			simplevector.x = mesh.xOrg[k];
			simplevector.y = mesh.yOrg[k];
			simplevector.z = mesh.zOrg[k];
			simplevector.matMul(matrix);
			return simplevector;
		}
	}

	public SimpleVector getTextureUV(int i, int j) {
		if (outOfBounds(i) || j < 0 || j > 2) {
			return null;
		} else {
			int k = myObj.objMesh.points[i][j];
			Vectors vectors = myObj.objVectors;
			SimpleVector simplevector = new SimpleVector();
			simplevector.x = vectors.nuOrg[k];
			simplevector.y = vectors.nvOrg[k];
			return simplevector;
		}
	}

	public SimpleVector getTransformedNormal(int i) {
		if (outOfBounds(i)) {
			return null;
		} else {
			Matrix matrix = myObj.getWorldTransformation();
			SimpleVector simplevector = new SimpleVector();
			Mesh mesh = myObj.objMesh;
			int j = mesh.coords[mesh.points[i][0]];
			int k = mesh.coords[mesh.points[i][1]];
			int l = mesh.coords[mesh.points[i][2]];
			simplevector.x = mesh.xOrg[l];
			simplevector.y = mesh.yOrg[l];
			simplevector.z = mesh.zOrg[l];
			simplevector.matMul(matrix);
			float f = simplevector.x;
			float f1 = simplevector.y;
			float f2 = simplevector.z;
			simplevector.x = mesh.xOrg[k];
			simplevector.y = mesh.yOrg[k];
			simplevector.z = mesh.zOrg[k];
			simplevector.matMul(matrix);
			float f3 = simplevector.x;
			float f4 = simplevector.y;
			float f5 = simplevector.z;
			simplevector.x = mesh.xOrg[j];
			simplevector.y = mesh.yOrg[j];
			simplevector.z = mesh.zOrg[j];
			simplevector.matMul(matrix);
			float f6 = simplevector.x - f;
			float f7 = simplevector.y - f1;
			float f8 = simplevector.z - f2;
			float f9 = f3 - f;
			float f10 = f4 - f1;
			float f11 = f5 - f2;
			simplevector.x = f7 * f11 - f8 * f10;
			simplevector.y = f8 * f9 - f6 * f11;
			simplevector.z = f6 * f10 - f7 * f9;
			return simplevector.normalize();
		}
	}

	public int getMaxPolygonID() {
		return myObj.objMesh.anzTri;
	}

	private boolean outOfBounds(int i) {
		if (i < 0 || i >= myObj.objMesh.anzTri) {
			Logger.log("No such polygon!", 0);
			return true;
		} else {
			return false;
		}
	}

	Object3D myObj;
	TextureManager texMan;
}
