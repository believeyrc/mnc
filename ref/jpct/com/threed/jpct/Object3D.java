// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 2010-1-27 12:07:09
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)

package com.threed.jpct;

import java.awt.Color;
import java.io.*;
import java.util.*;

// Referenced classes of package com.threed.jpct:
//            Matrix, Mesh, Vectors, PolygonManager,
//            SimpleVector, Plane, ICompiledInstance, CollisionEvent,
//            CollisionListener, Lights, Logger, TextureManager,
//            Texture, Config, FrameBuffer, Animation,
//            World, Portals, BufferedMatrix, TextureInfo,
//            OcTree, OcTreeNode, CollisionInfo, Camera,
//            IntegerC, VisList, Object3DList, IRenderHook

public class Object3D implements Serializable {

	public static Object3D createDummyObj() {
		return new Object3D(0);
	}

	public void setUserObject(Object obj) {
		userObj = obj;
	}

	public Object getUserObject() {
		return userObj;
	}

	public void setSortOffset(float f) {
		sortOffset = f;
	}

	public static Object3D mergeObjects(Object3D object3d, Object3D object3d1) {
		if (object3d != null && object3d1 != null) {
			int i = Math.max(object3d.objMesh.anzTri + object3d1.objMesh.anzTri,
					(object3d.objMesh.anzVectors + object3d1.objMesh.anzVectors) / 3);
			Object3D object3d2 = new Object3D(i);
			object3d.appendToObject(object3d2);
			object3d1.appendToObject(object3d2);
			return object3d2;
		} else {
			Logger.log("Can't merge null!", 0);
			return null;
		}
	}

	public static Object3D mergeAll(Object3D aobject3d[]) {
		Object3D object3d = createDummyObj();
		for (int i = 0; i < aobject3d.length; i++) {
			object3d = mergeObjects(object3d, aobject3d[i]);
			aobject3d[i] = null;
		}

		return object3d;
	}

	public static void resetNextID() {
		nextID = 0;
	}

	public static int getNextID() {
		return nextID;
	}

	public static void setNextID(int i) {
		if (i >= nextID)
			nextID = i;
		else
			Logger.log("The next ID can't be lower than the current one", 0);
	}

	public Object3D(int i) {
		transMode = 0;
		sortOffset = 0.0F;
		number = 0;
		name = "";
		maxStagesUsed = 0;
		usesMultiTexturing = false;
		parentCnt = 0;
		compiled = null;
		dynamic = false;
		modified = false;
		indexed = true;
		batchSize = -1;
		preferDL = true;
		staticUV = true;
		nearestLights = (float[][]) null;
		toStrip = false;
		renderHook = null;
		isSharingSource = false;
		sharing = false;
		shareWith = null;
		transBuffer = new Matrix();
		stripped = false;
		collisionListener = null;
		disableListeners = false;
		polygonIDs = null;
		pIDCount = 0;
		lastAddedID = -1;
		rotationMatrix = new Matrix();
		translationMatrix = new Matrix();
		originMatrix = new Matrix();
		mat2 = new Matrix();
		mat3 = new Matrix();
		mat5 = new Matrix();
		mat6 = new Matrix();
		writeToZbuffer = false;
		neverOptimize = false;
		transCache = null;
		invCache = null;
		visComplete = false;
		optimizeColDet = false;
		largestPolygonSize = -1F;
		polyManager = null;
		ellipsoidMode = 0;
		userObj = null;
		reverseCulling = false;
		hasBeenBuild = false;
		useMatrixCache = true;
		matrixCache = new Hashtable(3);
		addColorInstance = Color.black;
		sectors = null;
		cachedTC = null;
		reNormalize = false;
		init(i);
	}

	public Object3D(Object3D object3d) {
		this(object3d, false);
	}

	public Object3D(Object3D object3d, boolean flag) {
		transMode = 0;
		sortOffset = 0.0F;
		number = 0;
		name = "";
		maxStagesUsed = 0;
		usesMultiTexturing = false;
		parentCnt = 0;
		compiled = null;
		dynamic = false;
		modified = false;
		indexed = true;
		batchSize = -1;
		preferDL = true;
		staticUV = true;
		nearestLights = (float[][]) null;
		toStrip = false;
		renderHook = null;
		isSharingSource = false;
		sharing = false;
		shareWith = null;
		transBuffer = new Matrix();
		stripped = false;
		collisionListener = null;
		disableListeners = false;
		polygonIDs = null;
		pIDCount = 0;
		lastAddedID = -1;
		rotationMatrix = new Matrix();
		translationMatrix = new Matrix();
		originMatrix = new Matrix();
		mat2 = new Matrix();
		mat3 = new Matrix();
		mat5 = new Matrix();
		mat6 = new Matrix();
		writeToZbuffer = false;
		neverOptimize = false;
		transCache = null;
		invCache = null;
		visComplete = false;
		optimizeColDet = false;
		largestPolygonSize = -1F;
		polyManager = null;
		ellipsoidMode = 0;
		userObj = null;
		reverseCulling = false;
		hasBeenBuild = false;
		useMatrixCache = true;
		matrixCache = new Hashtable(3);
		addColorInstance = Color.black;
		sectors = null;
		cachedTC = null;
		reNormalize = false;
		int i = 0;
		if (object3d != null && object3d.texture != null)
			i = object3d.texture.length;
		init(i + 8);
		if (i != 0)
			copy(object3d, flag);
	}

	public Object3D(float af[], float af1[], int ai[], int i) {
		transMode = 0;
		sortOffset = 0.0F;
		number = 0;
		name = "";
		maxStagesUsed = 0;
		usesMultiTexturing = false;
		parentCnt = 0;
		compiled = null;
		dynamic = false;
		modified = false;
		indexed = true;
		batchSize = -1;
		preferDL = true;
		staticUV = true;
		nearestLights = (float[][]) null;
		toStrip = false;
		renderHook = null;
		isSharingSource = false;
		sharing = false;
		shareWith = null;
		transBuffer = new Matrix();
		stripped = false;
		collisionListener = null;
		disableListeners = false;
		polygonIDs = null;
		pIDCount = 0;
		lastAddedID = -1;
		rotationMatrix = new Matrix();
		translationMatrix = new Matrix();
		originMatrix = new Matrix();
		mat2 = new Matrix();
		mat3 = new Matrix();
		mat5 = new Matrix();
		mat6 = new Matrix();
		writeToZbuffer = false;
		neverOptimize = false;
		transCache = null;
		invCache = null;
		visComplete = false;
		optimizeColDet = false;
		largestPolygonSize = -1F;
		polyManager = null;
		ellipsoidMode = 0;
		userObj = null;
		reverseCulling = false;
		hasBeenBuild = false;
		useMatrixCache = true;
		matrixCache = new Hashtable(3);
		addColorInstance = Color.black;
		sectors = null;
		cachedTC = null;
		reNormalize = false;
		if (ai == null) {
			ai = new int[af.length / 3];
			for (int j = 0; j < ai.length; j++)
				ai[j] = j;

		}
		init((ai.length + 3) / 2);
		objMesh.anzCoords = 0;
		int k = 0;
		for (int l = 0; l < af.length; l += 3) {
			int i1 = l / 3;
			objMesh.xOrg[i1] = af[l];
			objMesh.yOrg[i1] = af[l + 1];
			objMesh.zOrg[i1] = af[l + 2];
		}

		objMesh.anzCoords = af.length / 3;
		TextureManager texturemanager = TextureManager.getInstance();
		float f = 2.0F;
		float f1 = 2.0F;
		if (i != -1) {
			f = texturemanager.textures[i].width;
			f1 = texturemanager.textures[i].height;
		} else {
			i = 0;
		}
		int j1 = 0;
		int k1 = 0;
		for (int l1 = 0; l1 < ai.length; l1 += 3) {
			for (int i2 = 0; i2 < 3; i2++) {
				float f2 = af1[2 * ai[l1 + i2]];
				float f3 = af1[2 * ai[l1 + i2] + 1];
				objVectors.uOrg[j1] = f2 * f;
				objVectors.vOrg[j1] = f3 * f1;
				objVectors.buOrg[j1] = f2 * f;
				objVectors.bvOrg[j1] = f3 * f1;
				objVectors.nuOrg[j1] = f2;
				objVectors.nvOrg[j1] = f3;
				objMesh.coords[j1] = ai[l1 + i2];
				objMesh.points[k1][i2] = j1;
				j1++;
				objMesh.anzVectors++;
			}

			if (sector != null)
				sector[k1] = k;
			texture[k1] = i;
			basemap[k1] = i;
			if (bumpmap != null)
				bumpmap[k1] = i;
			k1++;
			objMesh.anzTri++;
		}

		objMesh.compress();
	}

	private void copy(Object3D object3d, boolean flag) {
		if (flag)
			objMesh = object3d.objMesh;
		else
			objMesh = object3d.objMesh.cloneMesh(true);
		if (objMesh.anzVectors > 0 && object3d.objVectors.eu != null)
			objVectors.createEnvmapCoords();
		if (object3d.hasVertexAlpha() && object3d.objVectors.alpha != null) {
			objVectors.createAlpha();
			System.arraycopy(object3d.objVectors.alpha, 0, objVectors.alpha, 0, object3d.objVectors.alpha.length);
		}
		for (int i = 0; i < object3d.objVectors.maxVectors; i++) {
			objVectors.sbOrg[i] = object3d.objVectors.sbOrg[i];
			objVectors.srOrg[i] = object3d.objVectors.srOrg[i];
			objVectors.sgOrg[i] = object3d.objVectors.sgOrg[i];
			objVectors.nuOrg[i] = object3d.objVectors.nuOrg[i];
			objVectors.nvOrg[i] = object3d.objVectors.nvOrg[i];
			objVectors.uOrg[i] = object3d.objVectors.uOrg[i];
			objVectors.vOrg[i] = object3d.objVectors.vOrg[i];
			if (objVectors.eu != null) {
				objVectors.eu[i] = object3d.objVectors.eu[i];
				objVectors.ev[i] = object3d.objVectors.ev[i];
			}
			objVectors.buOrg[i] = object3d.objVectors.buOrg[i];
			objVectors.bvOrg[i] = object3d.objVectors.bvOrg[i];
			objVectors.vertexSector[i] = object3d.objVectors.vertexSector[i];
		}

		if (object3d.objVectors.uMul != null) {
			objVectors.createMultiCoords();
			for (int j = 0; j < Config.maxTextureLayers - 1; j++) {
				for (int l = 0; l < object3d.objVectors.maxVectors; l++) {
					objVectors.uMul[j][l] = object3d.objVectors.uMul[j][l];
					objVectors.vMul[j][l] = object3d.objVectors.vMul[j][l];
				}

			}

		}
		maxStagesUsed = object3d.maxStagesUsed;
		usesMultiTexturing = object3d.usesMultiTexturing;
		objVectors.setMesh(objMesh);
		isTrans = object3d.isTrans;
		transMode = object3d.transMode;
		isEnvmapped = object3d.isEnvmapped;
		useCSEnvmapping = object3d.useCSEnvmapping;
		envMapDir = object3d.envMapDir;
		isBlended = object3d.isBlended;
		isBumpmapped = object3d.isBumpmapped;
		isVisible = object3d.isVisible;
		isSelectable = object3d.isSelectable;
		myWorld = object3d.myWorld;
		lowestPos = object3d.lowestPos;
		highestPos = object3d.highestPos;
		lazyTransforms = object3d.lazyTransforms;
		optimizeColDet = object3d.optimizeColDet;
		largestPolygonSize = object3d.largestPolygonSize;
		isBillBoard = object3d.isBillBoard;
		writeToZbuffer = object3d.writeToZbuffer;
		if (object3d.multiTex != null) {
			for (int k = 0; k < Config.maxTextureLayers - 1; k++) {
				if (multiTex == null) {
					multiTex = new int[Config.maxTextureLayers - 1][texture.length];
					multiMode = new int[Config.maxTextureLayers - 1][texture.length];
				}
				System.arraycopy(object3d.multiTex[k], 0, multiTex[k], 0, object3d.multiTex[k].length);
				System.arraycopy(object3d.multiMode[k], 0, multiMode[k], 0, object3d.multiMode[k].length);
			}

		}
		System.arraycopy(object3d.texture, 0, texture, 0, object3d.texture.length);
		System.arraycopy(object3d.basemap, 0, basemap, 0, object3d.basemap.length);
		if (object3d.sector != null) {
			if (sector == null)
				sector = new int[object3d.texture.length];
			System.arraycopy(object3d.sector, 0, sector, 0, object3d.sector.length);
		}
		if (object3d.bumpmap != null) {
			if (bumpmap == null)
				bumpmap = new int[object3d.texture.length];
			System.arraycopy(object3d.bumpmap, 0, bumpmap, 0, object3d.bumpmap.length);
		}
		System.arraycopy(object3d.parent, 0, parent, 0, object3d.parent.length);
		System.arraycopy(object3d.sectorStartPoint, 0, sectorStartPoint, 0, object3d.sectorStartPoint.length);
		System.arraycopy(object3d.sectorEndPoint, 0, sectorEndPoint, 0, object3d.sectorEndPoint.length);
		System.arraycopy(object3d.sectorStartPoly, 0, sectorStartPoly, 0, object3d.sectorStartPoly.length);
		System.arraycopy(object3d.sectorEndPoly, 0, sectorEndPoly, 0, object3d.sectorEndPoly.length);
		transValue = object3d.transValue;
		oneSectorOnly = object3d.oneSectorOnly;
		alwaysFilter = object3d.alwaysFilter;
		xRotationCenter = object3d.xRotationCenter;
		yRotationCenter = object3d.yRotationCenter;
		zRotationCenter = object3d.zRotationCenter;
		centerX = object3d.centerX;
		centerY = object3d.centerY;
		centerZ = object3d.centerZ;
		hasBoundingBox = object3d.hasBoundingBox;
		addColorR = object3d.addColorR;
		addColorG = object3d.addColorG;
		addColorB = object3d.addColorB;
		addColorInstance = object3d.addColorInstance;
		rotationMatrix = object3d.rotationMatrix.cloneMatrix();
		translationMatrix = object3d.translationMatrix.cloneMatrix();
		originMatrix = object3d.originMatrix.cloneMatrix();
		doCulling = object3d.doCulling;
		anim = object3d.anim;
		userObj = object3d.userObj;
	}

	private void init(int i) {
		if (i != -1 && i != 0) {
			int j = 3 * i + 8;
			objMesh = new Mesh(j);
			objVectors = new Vectors(j, objMesh);
			texture = new int[i];
			basemap = new int[i];
			if (!Config.saveMemory) {
				bumpmap = new int[i];
				sector = new int[i];
			}
		} else {
			objMesh = new Mesh(1);
		}
		parent = new Object3D[Config.maxParentObjects];
		parentCnt = 0;
		object3DRendered = false;
		rotationMatrix.setIdentity();
		translationMatrix.setIdentity();
		originMatrix.setIdentity();
		xRotationCenter = 0.0F;
		yRotationCenter = 0.0F;
		zRotationCenter = 0.0F;
		xWs = new float[8];
		yWs = new float[8];
		zWs = new float[8];
		centerX = 0.0F;
		centerY = 0.0F;
		centerZ = 0.0F;
		envMapDir = 1;
		dynSectorDetect = false;
		dynSectorList = new int[Config.maxPortals];
		sectorCnt = 0;
		number = nextID;
		nextID++;
		name = "object" + number;
		objMesh.anzTri = 0;
		isPotentialCollider = false;
		mayCollide = false;
		isBillBoard = false;
		isFlatShaded = false;
		isLit = true;
		lazyTransforms = false;
		wasCollider = false;
		anim = null;
		oneSectorOnly = true;
		ocTree = null;
		if (i != 0) {
			sectorStartPoint = new int[Config.maxPortals];
			sectorEndPoint = new int[Config.maxPortals];
			sectorStartPoly = new int[Config.maxPortals];
			sectorEndPoly = new int[Config.maxPortals];
			isEnvmapped = false;
			useCSEnvmapping = false;
			isBlended = false;
			isBumpmapped = false;
			isMainWorld = false;
			isSelectable = true;
			alwaysFilter = false;
			transValue = 0;
			isTrans = false;
			isVisible = true;
			doCulling = true;
			doSpecularLighting = false;
			singleSectorNumber = 0;
			hasPortals = false;
			addColorR = 0;
			addColorG = 0;
			addColorB = 0;
			addColorInstance = Color.black;
			hasBoundingBox = false;
		}
		scaleFactor = 1.0F;
		invScaleFactor = 1.0F;
	}

	public void compile() {
		dynamic = false;
		preferDL = true;
		indexed = true;
		batchSize = -1;
		staticUV = true;
		compileInternal();
	}

	public void compileAndStrip() {
		compile();
		strip();
	}

	public void compile(boolean flag) {
		dynamic = flag;
		preferDL = true;
		indexed = !flag;
		staticUV = true;
		batchSize = -1;
		compileInternal();
	}

	public void compile(boolean flag, boolean flag1, boolean flag2, boolean flag3, int i) {
		dynamic = flag;
		preferDL = flag2;
		indexed = flag3;
		batchSize = i;
		staticUV = flag1;
		compileInternal();
	}

	public boolean isCompiled() {
		if (this.compiled == null)
			return false;
		synchronized (this.compiled) {
			return ((this.compiled != null) && (this.compiled.size() > 0));
		}
	}

	public void shareCompiledData(Object3D object3d) {
		if (object3d.shareWith != null) {
			Logger.log("Can't enable share data with an object that shares data itself! Use the source object instead!", 0);
			return;
		}
		if (compiled != null) {
			Logger.log("Can't enable data sharing after calling compile on object " + getName() + "!", 0);
			return;
		}
		if (object3d.compiled == null) {
			Logger.log("Can't set an uncompiled object was a source for compiled data!", 0);
			return;
		}
		if (sharing) {
			Logger.log("This object already shares data with '" + object3d.getName() + "'", 0);
			return;
		}
		if (object3d.ocTree != null || ocTree != null) {
			Logger.log("No data sharing with octrees supported!", 0);
			return;
		}
		if (object3d.objMesh != objMesh) {
			Logger.log("Can't share data from different meshes!", 0);
			return;
		} else {
			object3d.isSharingSource = true;
			shareWith = object3d;
			return;
		}
	}

	public void decompile(FrameBuffer framebuffer) {
		if (isSharingSource) {
			Logger.log("You can't decompile an object that is the source of data for another one!", 1);
			return;
		}
		Object obj = null;
		if (framebuffer != null)
			obj = framebuffer.getLock();
		else
			obj = this;
		synchronized (obj) {
			if (isCompiled()) {
				if (stripped)
					Logger.log("A stripped Object3D can't be decompiled!", 0);
				else
					synchronized (compiled) {
						compiled.clear();
						compiled = null;
						toStrip = false;
					}
			} else if (compiled != null)
				synchronized (compiled) {
					compiled = null;
					toStrip = false;
				}
		}
	}

	public void touch() {
		modified = true;
	}

	public void strip() {
		if (compiled != null) {
			if (dynamic)
				Logger.log("An Object3D compiled as dynamic can't be stripped!", 0);
			else
				toStrip = true;
		} else {
			Logger.log("An Object3D can't be stripped if it hasn't been compiled before!", 0);
		}
	}

	public void setAnimationSequence(Animation animation) {
		if (animation == null) {
			anim = null;
			return;
		}
		if (animation.aktFrames != 0) {
			if (animation.keyFrames[0].anzCoords == objMesh.anzCoords)
				anim = animation;
			else
				Logger.log("The sizes of the Animation's Meshes (" + animation.keyFrames[0].anzCoords
						+ ") and the object's Mesh (" + objMesh.anzCoords + ") don't match!", 0);
		} else {
			Logger.log("This Animation is empty!", 0);
		}
	}

	public void clearAnimation() {
		anim = null;
	}

	public Animation getAnimationSequence() {
		return anim;
	}

	public void animate(float f, int i) {
		if ((compiled == null || dynamic) && anim != null) {
			modified = true;
			anim.doAnimation(this, i, f);
		}
	}

	public void animate(float f) {
		animate(f, 0);
	}

	public void animateSync(float f, FrameBuffer framebuffer) {
		synchronized (framebuffer.getLock()) {
			animate(f);
		}
	}

	public void animateSync(float f, int i, FrameBuffer framebuffer) {
		synchronized (framebuffer.getLock()) {
			animate(f, i);
		}
	}

	public void setCollisionMode(int i) {
		if (i == 0) {
			isPotentialCollider = false;
			mayCollide = false;
		} else {
			if ((i & 1) == 1)
				isPotentialCollider = true;
			else
				isPotentialCollider = false;
			if ((i & 2) == 2)
				mayCollide = true;
			else
				mayCollide = false;
		}
	}

	public void setCollisionOptimization(boolean flag) {
		if (largestPolygonSize == -1F)
			largestPolygonSize = objMesh.getLargestCoveredDistance();
		optimizeColDet = flag;
	}

	public void setVisibility(boolean flag) {
		isVisible = flag;
	}

	public boolean getVisibility() {
		return isVisible;
	}

	public void calcBoundingBox() {
		float af[] = objMesh.calcBoundingBox();
		setBoundingBox(af[0], af[1], af[2], af[3], af[4], af[5]);
	}

	public void createTriangleStrips() {
		createTriangleStrips(50);
	}

	public void createTriangleStrips(int i) {
		Object3D object3d = this;
		Mesh mesh = object3d.objMesh;
		int j = object3d.objMesh.anzTri;
		int k = 0;
		int l = -1;
		int i1 = 0;
		if (!Config.useMultipassStriping)
			i1 = i - 1;
		if (ocTree != null) {
			Logger.log("Creating strips has destroyed the octree of this object!", 1);
			ocTree = null;
		}
		while (k != l && i1 < i) {
			i1++;
			k = l;
			l = 0;
			int j1 = 0;
			while (j1 < j) {
				int k1 = 0;
				for (int l1 = j1 + 1; l1 < j; l1++) {
					int i2 = j1 + k1;
					if (object3d.texture[i2] != object3d.texture[l1] || object3d.sector != null
							&& object3d.sector[i2] != object3d.sector[l1])
						continue;
					int j2 = l1;
					int k2 = mesh.points[i2][0];
					int l2 = mesh.points[i2][1];
					int i3 = mesh.points[i2][2];
					int j3 = mesh.points[j2][0];
					int k3 = mesh.points[j2][1];
					int l3 = mesh.coords[k2];
					int i4 = mesh.coords[l2];
					int j4 = mesh.coords[i3];
					int k4 = mesh.coords[j3];
					int l4 = mesh.coords[k3];
					float f = object3d.objVectors.nuOrg[k2];
					float f1 = object3d.objVectors.nvOrg[k2];
					float f2 = object3d.objVectors.nuOrg[l2];
					float f3 = object3d.objVectors.nvOrg[l2];
					float f4 = object3d.objVectors.nuOrg[i3];
					float f5 = object3d.objVectors.nvOrg[i3];
					float f6 = object3d.objVectors.nuOrg[j3];
					float f7 = object3d.objVectors.nvOrg[j3];
					float f8 = object3d.objVectors.nuOrg[k3];
					float f9 = object3d.objVectors.nvOrg[k3];
					int i5 = k1;
					if ((k1 & 1) == 0 && i4 == l4 && j4 == k4 && f2 == f8 && f3 == f9 && f4 == f6 && f5 == f7) {
						k1++;
						if (i2 + 1 != j2 && i2 + 1 < j)
							switchTriangles(j2, i2 + 1);
					} else if ((k1 & 1) == 1 && l3 == k4 && j4 == l4 && f == f6 && f1 == f7 && f4 == f8 && f5 == f9) {
						k1++;
						if (i2 + 1 != j2 && i2 + 1 < j)
							switchTriangles(j2, i2 + 1);
					}
					if (i5 != k1)
						continue;
					j3 = mesh.points[j2][1];
					k3 = mesh.points[j2][2];
					k4 = mesh.coords[j3];
					l4 = mesh.coords[k3];
					f6 = object3d.objVectors.nuOrg[j3];
					f7 = object3d.objVectors.nvOrg[j3];
					f8 = object3d.objVectors.nuOrg[k3];
					f9 = object3d.objVectors.nvOrg[k3];
					i5 = k1;
					if ((k1 & 1) == 0 && i4 == l4 && j4 == k4 && f2 == f8 && f3 == f9 && f4 == f6 && f5 == f7) {
						int j5 = mesh.points[j2][0];
						mesh.points[j2][0] = mesh.points[j2][1];
						mesh.points[j2][1] = mesh.points[j2][2];
						mesh.points[j2][2] = j5;
						k1++;
						if (i2 + 1 != j2 && i2 + 1 < j)
							switchTriangles(j2, i2 + 1);
					} else if ((k1 & 1) == 1 && l3 == k4 && j4 == l4 && f == f6 && f1 == f7 && f4 == f8 && f5 == f9) {
						int k5 = mesh.points[j2][0];
						mesh.points[j2][0] = mesh.points[j2][1];
						mesh.points[j2][1] = mesh.points[j2][2];
						mesh.points[j2][2] = k5;
						k1++;
						if (i2 + 1 != j2 && i2 + 1 < j)
							switchTriangles(j2, i2 + 1);
					}
					if (i5 != k1)
						continue;
					j3 = mesh.points[j2][2];
					k3 = mesh.points[j2][0];
					k4 = mesh.coords[j3];
					l4 = mesh.coords[k3];
					f6 = object3d.objVectors.nuOrg[j3];
					f7 = object3d.objVectors.nvOrg[j3];
					f8 = object3d.objVectors.nuOrg[k3];
					f9 = object3d.objVectors.nvOrg[k3];
					i5 = k1;
					if ((k1 & 1) == 0 && i4 == l4 && j4 == k4 && f2 == f8 && f3 == f9 && f4 == f6 && f5 == f7) {
						int l5 = mesh.points[j2][0];
						mesh.points[j2][0] = mesh.points[j2][1];
						mesh.points[j2][1] = mesh.points[j2][2];
						mesh.points[j2][2] = l5;
						k1++;
						if (i2 + 1 != j2 && i2 + 1 < j)
							switchTriangles(j2, i2 + 1);
						continue;
					}
					if ((k1 & 1) != 1 || l3 != k4 || j4 != l4 || f != f6 || f1 != f7 || f4 != f8 || f5 != f9)
						continue;
					int i6 = mesh.points[j2][0];
					mesh.points[j2][0] = mesh.points[j2][1];
					mesh.points[j2][1] = mesh.points[j2][2];
					mesh.points[j2][2] = i6;
					k1++;
					if (i2 + 1 != j2 && i2 + 1 < j)
						switchTriangles(j2, i2 + 1);
				}

				if (k1 != 0)
					l++;
				j1 += k1;
				j1++;
			}
		}
		if (!Config.useMultipassStriping)
			i1 = 2;
		Logger.log("Created " + l + " triangle-strips for " + getName() + " in " + (i1 - 1) + " pass(es)", 2);
	}

	public void rebuild() {
		build();
	}

	public void build() {
		try {
			hasBeenBuild = true;
			if (!isMainWorld) {
				calcCenter();
				calcBoundingBox();
			} else {
				reorderSectors(1);
				myWorld.portals.calcAABoundingBox(this);
			}
			calcNormals();
			recreateTextureCoords();
		} catch (Exception exception) {
			exception.printStackTrace();
			Logger.log("Couldn't build() object (" + name + "): " + exception.getClass().getName() + "/"
					+ exception.getMessage(), 0);
		}
	}

	public void unbuild() {
		if (objMesh.obbStart != 0 || objMesh.obbEnd != 0) {
			objMesh.anzCoords = objMesh.obbStart;
			objMesh.obbStart = 0;
			objMesh.obbEnd = 0;
		}
	}

	public void disableVertexSharing() {
		neverOptimize = true;
	}

	public void reorderSectors(int i) {
		if (!oneSectorOnly) {
			boolean flag = false;
			boolean flag1 = false;
			int j = objMesh.anzTri - 1;
			while (!flag) {
				flag = true;
				int k = 0;
				while (k < j) {
					int j1 = sector[k];
					int i2 = sector[k + 1];
					boolean flag2 = j1 > i2;
					if (i != 1) {
						if (i == 2) {
							if (j1 == i2
									&& objMesh.zOrg[objMesh.coords[objMesh.points[k][0]]] > objMesh.zOrg[objMesh.coords[objMesh.points[k + 1][0]]])
								flag2 = true;
						} else if (i == 3) {
							if (j1 == i2
									&& objMesh.yOrg[objMesh.coords[objMesh.points[k][0]]] > objMesh.yOrg[objMesh.coords[objMesh.points[k + 1][0]]])
								flag2 = true;
						} else if (i == 4
								&& j1 == i2
								&& objMesh.xOrg[objMesh.coords[objMesh.points[k][0]]] > objMesh.xOrg[objMesh.coords[objMesh.points[k + 1][0]]])
							flag2 = true;
					} else if (j1 == i2 && texture[k] > texture[k + 1])
						flag2 = true;
					if (flag2) {
						switchTriangles(k, k + 1);
						flag = false;
					}
					k++;
				}
			}
			flag = false;
			j = objMesh.anzCoords - 1;
			while (!flag) {
				flag = true;
				int l = 0;
				while (l < j) {
					int k1 = l;
					int j2 = l + 1;
					int k2 = objVectors.vertexSector[k1];
					int l2 = objVectors.vertexSector[j2];
					if (k2 > l2) {
						float f = objMesh.xOrg[k1];
						objMesh.xOrg[k1] = objMesh.xOrg[j2];
						objMesh.xOrg[j2] = f;
						f = objMesh.yOrg[k1];
						objMesh.yOrg[k1] = objMesh.yOrg[j2];
						objMesh.yOrg[j2] = f;
						f = objMesh.zOrg[k1];
						objMesh.zOrg[k1] = objMesh.zOrg[j2];
						objMesh.zOrg[j2] = f;
						f = objMesh.nxOrg[k1];
						objMesh.nxOrg[k1] = objMesh.nxOrg[j2];
						objMesh.nxOrg[j2] = f;
						f = objMesh.nyOrg[k1];
						objMesh.nyOrg[k1] = objMesh.nyOrg[j2];
						objMesh.nyOrg[j2] = f;
						f = objMesh.nzOrg[k1];
						objMesh.nzOrg[k1] = objMesh.nzOrg[j2];
						objMesh.nzOrg[j2] = f;
						int i3 = objVectors.vertexSector[k1];
						objVectors.vertexSector[k1] = objVectors.vertexSector[j2];
						objVectors.vertexSector[j2] = i3;
						for (int j3 = 0; j3 < objMesh.anzVectors; j3++) {
							if (objMesh.coords[j3] == l) {
								objMesh.coords[j3] = l + 1;
								continue;
							}
							if (objMesh.coords[j3] == l + 1)
								objMesh.coords[j3] = l;
						}

						flag = false;
					}
					l++;
				}
			}
			int i1 = -99;
			int l1 = 0;
			for (l1 = 0; l1 < objMesh.anzTri; l1++) {
				if (sector[l1] != i1) {
					sectorStartPoly[sector[l1]] = l1;
					if (i1 != -99)
						sectorEndPoly[i1] = l1 - 1;
				}
				i1 = sector[l1];
			}

			if (i1 != -99) {
				sectorEndPoly[i1] = l1 - 1;
				i1 = -99;
			}
			for (l1 = 0; l1 < objMesh.anzCoords; l1++) {
				if (objVectors.vertexSector[l1] != i1) {
					sectorStartPoint[objVectors.vertexSector[l1]] = l1;
					if (i1 != -99)
						sectorEndPoint[i1] = l1 - 1;
				}
				i1 = objVectors.vertexSector[l1];
			}

			if (i1 != -99)
				sectorEndPoint[i1] = l1 - 1;
			sectorStartPoint[i1 + 1] = objMesh.anzCoords + 1;
			sectorEndPoint[i1 + 1] = objMesh.anzCoords + 1;
			sectorStartPoly[i1 + 1] = objMesh.anzTri + 1;
		}
	}

	public void setSectorDetectionMode(boolean flag) {
		if (flag && !oneSectorOnly)
			Logger.log(
					"Autodection of sectors can't be used for a multi-sectored object. It has to use static sector definitions!",
					0);
		else
			dynSectorDetect = flag;
	}

	public boolean hasChild(Object3D object3d) {
		if (object3d != null) {
			return object3d.hasParent(this);
		} else {
			Logger.log("Testing a null-Object3D for being a child is rather senseless!", 1);
			return false;
		}
	}

	public boolean hasParent(Object3D object3d) {
		boolean flag = false;
		if (object3d != null) {
			int i = 0;
			do {
				if (i >= parentCnt)
					break;
				if (parent[i].number == object3d.number) {
					flag = true;
					break;
				}
				i++;
			} while (true);
			return flag;
		} else {
			Logger.log("Testing a null-Object3D for being a parent is rather senseless!", 1);
			return false;
		}
	}

	public void addChild(Object3D object3d) {
		if (object3d != null)
			object3d.addParent(this);
		else
			Logger.log("Tried to assign a non-existent Object3D as child!", 0);
	}

	public void removeChild(Object3D object3d) {
		if (object3d != null)
			object3d.removeParent(this);
		else
			Logger.log("Tried to remove a non-existent Object3D from the child collection!", 0);
	}

	public void removeParent(Object3D object3d) {
		if (object3d != null) {
			boolean flag = false;
			for (int i = 0; i < parentCnt; i++) {
				if (parent[i].number != object3d.number)
					continue;
				flag = true;
				if (i != parentCnt - 1) {
					for (int j = i; j < parentCnt - 1; j++)
						parent[j] = parent[j + 1];

				}
				parentCnt--;
			}

			if (!flag)
				Logger.log("Tried to remove an object from the parent collection that isn't part of it!", 0);
		} else {
			Logger.log("Tried to remove a non-existent object from the parent collection!", 0);
		}
	}

	public void addParent(Object3D object3d) {
		if (object3d == this) {
			Logger.log("An object can't be its own parent!", 1);
			return;
		}
		if (object3d != null) {
			if (parentCnt < Config.maxParentObjects) {
				parent[parentCnt] = object3d;
				parentCnt++;
			} else {
				Logger.log("Can't assign more than " + Config.maxParentObjects
						+ " objects as parent objects in the current Configuration!", 0);
			}
		} else {
			Logger.log("Tried to assign a nonexistent object as parent!", 0);
		}
	}

	public Object3D[] getParents() {
		Object3D aobject3d[] = new Object3D[parentCnt];
		if (parentCnt != 0)
			System.arraycopy(parent, 0, aobject3d, 0, parentCnt);
		return aobject3d;
	}

	public int getID() {
		return number - 2;
	}

	public String getName() {
		return name;
	}

	public void setName(String s) {
		if (myWorld != null && myWorld.getInternalObjectByName(s) != null)
			Logger.log("Object with name '" + s + "' already exists!", 0);
		else
			name = s;
	}

	public void setSelectable(boolean flag) {
		isSelectable = flag;
	}

	public boolean isSelectable() {
		if (compiled != null)
			return false;
		else
			return isSelectable;
	}

	public boolean wasVisible() {
		return object3DRendered;
	}

	public void setCulling(boolean flag) {
		doCulling = flag;
	}

	public boolean getCulling() {
		return doCulling;
	}

	public void setShadingMode(int i) {
		isFlatShaded = false;
		if (i == 1)
			isFlatShaded = true;
	}

	public void setLighting(int i) {
		if (i == 0)
			isLit = true;
		if (i == 1)
			isLit = false;
	}

	public int getLighting() {
		return !isLit ? 1 : 0;
	}

	public void setSpecularLighting(boolean flag) {
		doSpecularLighting = flag;
	}

	public void setFiltering(boolean flag) {
		alwaysFilter = flag;
	}

	public void setTransparency(int i) {
		transValue = i;
		if (i >= 0)
			isTrans = true;
		else
			isTrans = false;
	}

	public int getTransparency() {
		if (!isTrans)
			return -1;
		else
			return transValue;
	}

	public boolean isTransparent() {
		return isTrans;
	}

	public void setTransparencyMode(int i) {
		transMode = i;
	}

	public int getTransparencyMode() {
		return transMode;
	}

	public boolean hasVertexAlpha() {
		return objVectors.hasAlpha;
	}

	public void setAdditionalColor(Color color) {
		int i = color.getRed();
		int j = color.getGreen();
		int k = color.getBlue();
		addColorInstance = color;
		if (i >= 0 && i < 256 && j >= 0 && j < 256 && k >= 0 && k < 256) {
			addColorR = i;
			addColorG = j;
			addColorB = k;
		} else {
			Logger.log("Color values need to be in the range of [0..255]!", 0);
		}
	}

	public Color getAdditionalColor() {
		return addColorInstance;
	}

	public void clearAdditionalColor() {
		addColorR = 0;
		addColorG = 0;
		addColorB = 0;
		addColorInstance = Color.black;
	}

	public void clearObject() {
		objMesh.obbStart = 0;
		objMesh.obbEnd = 0;
		objMesh.anzTri = 0;
		objMesh.anzCoords = 0;
		objMesh.anzVectors = 0;
	}

	public void setMatrixCacheUsage(boolean flag) {
		useMatrixCache = flag;
		if (!useMatrixCache)
			matrixCache = null;
		else if (matrixCache == null)
			matrixCache = new Hashtable(3);
	}

	public void setReNormalization(boolean flag) {
		reNormalize = flag;
	}

	public void setDepthBufferWrites(boolean flag) {
		writeToZbuffer = flag;
	}

	public void decoupleMesh() {
		objMesh = new Mesh(0);
		objVectors.setMesh(objMesh);
	}

	public void setBlending(boolean flag) {
		isEnvmapped = flag;
		isBlended = flag;
		isBumpmapped = flag;
		TextureManager texturemanager = TextureManager.getInstance();
		if (flag) {
			checkBumpmap();
			if (!texturemanager.textures[bumpmap[0]].isBumpmap)
				texturemanager.textures[bumpmap[0]].createBumpmap();
			if (texturemanager.textures[basemap[0]].height != texturemanager.textures[bumpmap[0]].height
					|| texturemanager.textures[basemap[0]].width != texturemanager.textures[bumpmap[0]].width)
				Logger.log("Correct blending can only be applied, if texture- and bumpmap are equal in size!", 1);
		}
	}

	public boolean getBlending() {
		return isBlended;
	}

	public void setBumpmapped(boolean flag) {
		isBumpmapped = flag;
		isEnvmapped = flag;
		TextureManager texturemanager = TextureManager.getInstance();
		if (flag) {
			checkBumpmap();
			if (!texturemanager.textures[bumpmap[0]].isBumpmap)
				texturemanager.textures[bumpmap[0]].createBumpmap();
			if (texturemanager.textures[texture[0]].height != 256 || texturemanager.textures[texture[0]].height != 256)
				Logger.log("The environment-map used for bumpmapping should be 256x256 pixels in size.", 1);
		}
	}

	public void setBillboarding(boolean flag) {
		isBillBoard = flag;
	}

	public boolean isBumpmapped() {
		return isBumpmapped;
	}

	public boolean isEnvmapped() {
		return isEnvmapped;
	}

	public void setEnvmapped(boolean flag) {
		isEnvmapped = flag;
	}

	public void setEnvmapMode(boolean flag) {
		useCSEnvmapping = flag;
	}

	public void setEnvmapDirection(int i) {
		envMapDir = i;
	}

	public boolean getEnvmapMode() {
		return useCSEnvmapping;
	}

	public void rotateX(float f) {
		rotationMatrix.rotateX(f);
	}

	public void rotateY(float f) {
		rotationMatrix.rotateY(f);
	}

	public void rotateZ(float f) {
		rotationMatrix.rotateZ(f);
	}

	public void rotateAxis(SimpleVector simplevector, float f) {
		rotationMatrix.rotateAxis(simplevector, f);
	}

	public void translateMesh() {
		objMesh.translateMesh(translationMatrix, originMatrix);
		if (anim != null)
			anim.translateMesh(translationMatrix, originMatrix);
		calcBoundingBox();
	}

	public void translate(SimpleVector simplevector) {
		translationMatrix.translate(simplevector);
	}

	public void translate(float f, float f1, float f2) {
		translationMatrix.translate(f, f1, f2);
	}

	public void align(Camera camera) {
		float f = getScale();
		setScale(1.0F);
		rotationMatrix = ((BufferedMatrix) (camera)).backMatrix.invert3x3();
		setScale(f);
	}

	public void align(Object3D object3d) {
		float f = getScale();
		setScale(1.0F);
		rotationMatrix = object3d.rotationMatrix.cloneMatrix();
		setScale(f);
	}

	public void setOrientation(SimpleVector simplevector, SimpleVector simplevector1) {
		rotationMatrix.setOrientation(simplevector, simplevector1);
	}

	public void enableLazyTransformations() {
		lazyTransforms = true;
		transCache = null;
		invCache = null;
	}

	public void disableLazyTransformations() {
		lazyTransforms = false;
		transCache = null;
		invCache = null;
	}

	public void scale(float f) {
		if (f > 0.0F) {
			scaleFactor *= f;
			invScaleFactor = 1.0F / scaleFactor;
			rotationMatrix.scalarMul(f);
		} else {
			Logger.log("Scale has to be greater than zero!", 0);
		}
	}

	public void setScale(float f) {
		if (scaleFactor != 0.0F && f > 0.0F)
			scale(f / scaleFactor);
		else
			Logger.log("Invalid scale!", 0);
	}

	public float getScale() {
		return scaleFactor;
	}

	public SimpleVector getTranslation() {
		return translationMatrix.getTranslation();
	}

	public SimpleVector getOrigin() {
		return originMatrix.getTranslation();
	}

	public SimpleVector getXAxis() {
		return rotationMatrix.getXAxis();
	}

	public SimpleVector getYAxis() {
		return rotationMatrix.getYAxis();
	}

	public SimpleVector getZAxis() {
		return rotationMatrix.getZAxis();
	}

	public Matrix getRotationMatrix() {
		return rotationMatrix;
	}

	public Matrix getTranslationMatrix() {
		return translationMatrix;
	}

	public Matrix getOriginMatrix() {
		return originMatrix;
	}

	public void setRotationMatrix(Matrix matrix) {
		rotationMatrix = matrix;
	}

	public void rotateMesh() {
		objMesh.rotateMesh(rotationMatrix, xRotationCenter, yRotationCenter, zRotationCenter, scaleFactor);
		if (anim != null)
			anim.rotateMesh(rotationMatrix, xRotationCenter, yRotationCenter, zRotationCenter, scaleFactor);
		calcBoundingBox();
	}

	public void setTranslationMatrix(Matrix matrix) {
		translationMatrix = matrix;
	}

	public void setMesh(Mesh mesh) {
		objMesh = mesh;
		if (objVectors != null)
			objVectors.setMesh(objMesh);
	}

	public Mesh getMesh() {
		return objMesh;
	}

	public PolygonManager getPolygonManager() {
		if (stripped) {
			Logger.log("A stripped object can't be modified by a PolygonManager!", 0);
			return null;
		}
		if (polyManager == null)
			polyManager = new PolygonManager(this);
		return polyManager;
	}

	public void setBoundingBox(float f, float f1, float f2, float f3, float f4, float f5) {
		synchronized (objMesh) {
			if (!isMainWorld && oneSectorOnly) {
				if (objMesh.obbStart != 0) {
					objMesh.anzCoords = objMesh.obbStart;
					objMesh.obbStart = 0;
					objMesh.obbEnd = 0;
				}
				if (objVectors != null) {
					objMesh.obbStart = objVectors.addVertex(f, f2, f4);
					objVectors.addVertex(f, f2, f5);
					objVectors.addVertex(f1, f2, f4);
					objVectors.addVertex(f1, f2, f5);
					objVectors.addVertex(f1, f3, f4);
					objVectors.addVertex(f1, f3, f5);
					objVectors.addVertex(f, f3, f4);
					objMesh.obbEnd = objVectors.addVertex(f, f3, f5);
					hasBoundingBox = true;
				}
			} else {
				Logger.log("Can't create a bounding box for a multi-sectored object!", 1);
			}
		}
	}

	public Object3D cloneObject() {
		return new Object3D(this, true);
	}

	void enlarge(int i) {
		int j = texture.length + i;
		if (j <= texture.length)
			return;
		Object3D object3d = new Object3D(j);
		Vectors vectors = object3d.objVectors;
		Mesh mesh = object3d.objMesh;
		if (objVectors.sb != null)
			vectors.createScreenColors();
		if (objVectors.eu != null)
			object3d.objVectors.createEnvmapCoords();
		if (objVectors.alpha != null)
			object3d.objVectors.createAlpha();
		if (objVectors.bsu != null)
			object3d.objVectors.createBumpmapCoords();
		for (int k = 0; k < objVectors.maxVectors; k++) {
			vectors.sbOrg[k] = objVectors.sbOrg[k];
			vectors.srOrg[k] = objVectors.srOrg[k];
			vectors.sgOrg[k] = objVectors.sgOrg[k];
			vectors.nuOrg[k] = objVectors.nuOrg[k];
			vectors.nvOrg[k] = objVectors.nvOrg[k];
			vectors.uOrg[k] = objVectors.uOrg[k];
			vectors.vOrg[k] = objVectors.vOrg[k];
			if (objVectors.eu != null) {
				vectors.eu[k] = objVectors.eu[k];
				vectors.ev[k] = objVectors.ev[k];
			}
			vectors.buOrg[k] = objVectors.buOrg[k];
			vectors.bvOrg[k] = objVectors.bvOrg[k];
			vectors.vertexSector[k] = objVectors.vertexSector[k];
			vectors.sx[k] = objVectors.sx[k];
			vectors.sy[k] = objVectors.sy[k];
			vectors.sz[k] = objVectors.sz[k];
			vectors.su[k] = objVectors.su[k];
			vectors.sv[k] = objVectors.sv[k];
			if (vectors.bsu != null) {
				vectors.bsu[k] = objVectors.bsu[k];
				vectors.bsv[k] = objVectors.bsv[k];
			}
			if (objVectors.sb != null) {
				vectors.sb[k] = objVectors.sb[k];
				vectors.sr[k] = objVectors.sr[k];
				vectors.sg[k] = objVectors.sg[k];
			}
			if (objVectors.alpha != null)
				vectors.alpha[k] = objVectors.alpha[k];
		}

		objVectors.sbOrg = vectors.sbOrg;
		objVectors.srOrg = vectors.srOrg;
		objVectors.sgOrg = vectors.sgOrg;
		objVectors.nuOrg = vectors.nuOrg;
		objVectors.nvOrg = vectors.nvOrg;
		objVectors.uOrg = vectors.uOrg;
		objVectors.vOrg = vectors.vOrg;
		objVectors.eu = vectors.eu;
		objVectors.ev = vectors.ev;
		objVectors.buOrg = vectors.buOrg;
		objVectors.bvOrg = vectors.bvOrg;
		objVectors.vertexSector = vectors.vertexSector;
		objVectors.maxVectors = vectors.maxVectors;
		objVectors.sx = vectors.sx;
		objVectors.sy = vectors.sy;
		objVectors.sz = vectors.sz;
		objVectors.su = vectors.su;
		objVectors.sv = vectors.sv;
		objVectors.sb = vectors.sb;
		objVectors.sr = vectors.sr;
		objVectors.sg = vectors.sg;
		objVectors.bsu = vectors.bsu;
		objVectors.bsv = vectors.bsv;
		objVectors.setMesh(objMesh);
		objMesh.maxVectors = mesh.maxVectors;
		for (int l = 0; l < objMesh.points.length; l++) {
			mesh.points[l][0] = objMesh.points[l][0];
			mesh.points[l][1] = objMesh.points[l][1];
			mesh.points[l][2] = objMesh.points[l][2];
		}

		objMesh.points = mesh.points;
		for (int i1 = 0; i1 < objMesh.xOrg.length; i1++) {
			mesh.xOrg[i1] = objMesh.xOrg[i1];
			mesh.yOrg[i1] = objMesh.yOrg[i1];
			mesh.zOrg[i1] = objMesh.zOrg[i1];
			mesh.nxOrg[i1] = objMesh.nxOrg[i1];
			mesh.nyOrg[i1] = objMesh.nyOrg[i1];
			mesh.nzOrg[i1] = objMesh.nzOrg[i1];
			mesh.coords[i1] = objMesh.coords[i1];
		}

		objMesh.xOrg = mesh.xOrg;
		objMesh.yOrg = mesh.yOrg;
		objMesh.zOrg = mesh.zOrg;
		objMesh.nxOrg = mesh.nxOrg;
		objMesh.nyOrg = mesh.nyOrg;
		objMesh.nzOrg = mesh.nzOrg;
		objMesh.coords = mesh.coords;
		System.arraycopy(texture, 0, object3d.texture, 0, texture.length);
		System.arraycopy(basemap, 0, object3d.basemap, 0, basemap.length);
		texture = object3d.texture;
		basemap = object3d.basemap;
		if (sector != null) {
			if (object3d.sector == null)
				object3d.sector = new int[texture.length];
			System.arraycopy(sector, 0, object3d.sector, 0, sector.length);
			sector = object3d.sector;
		}
		if (bumpmap != null) {
			if (object3d.bumpmap == null)
				object3d.bumpmap = new int[texture.length];
			System.arraycopy(bumpmap, 0, object3d.bumpmap, 0, bumpmap.length);
			bumpmap = object3d.bumpmap;
		}
	}

	public Matrix getWorldTransformation() {
		Matrix matrix;
		if (!lazyTransforms || transCache == null) {
			matrix = new Matrix();
			Matrix matrix1 = getCachedMatrix(0);
			matrix.mat[3][0] = -xRotationCenter;
			matrix.mat[3][1] = -yRotationCenter;
			matrix.mat[3][2] = -zRotationCenter;
			matrix1.mat[3][0] = xRotationCenter + translationMatrix.mat[3][0] + originMatrix.mat[3][0];
			matrix1.mat[3][1] = yRotationCenter + translationMatrix.mat[3][1] + originMatrix.mat[3][1];
			matrix1.mat[3][2] = zRotationCenter + translationMatrix.mat[3][2] + originMatrix.mat[3][2];
			if (!isBillBoard) {
				matrix.matMul(rotationMatrix);
			} else {
				Matrix matrix2 = mat2.invert();
				matrix2.scalarMul(scaleFactor);
				matrix.matMul(matrix2);
			}
			matrix.matMul(matrix1);
			if (parentCnt != 0)
				if (isBillBoard && !Config.oldStyleBillBoarding)
					matrix = recurseObjectsBillboarded(matrix);
				else
					matrix = recurseObjects(matrix);
			if (lazyTransforms)
				transCache = matrix.cloneMatrix();
		} else {
			matrix = transCache.cloneMatrix();
		}
		return matrix;
	}

	void getWorldTransformationInternal(Matrix matrix) {
		if (!lazyTransforms || transCache == null) {
			Matrix matrix1 = getWorldTransformation();
			matrix.setTo(matrix1);
		} else {
			matrix.setTo(transCache);
		}
	}

	public synchronized void addCollisionListener(CollisionListener collisionlistener) {
		if (collisionListener == null)
			collisionListener = new Vector(2);
		collisionListener.addElement(collisionlistener);
		globalListenerCount++;
	}

	public synchronized void removeCollisionListener(CollisionListener collisionlistener) {
		if (collisionListener != null) {
			collisionListener.removeElement(collisionlistener);
			globalListenerCount--;
			if (collisionListener.size() == 0)
				collisionListener = null;
		}
	}

	public void disableCollisionListeners() {
		disableListeners = true;
	}

	public void enableCollisionListeners() {
		disableListeners = false;
	}

	public Enumeration getCollisionListeners() {
		if (collisionListener != null)
			return collisionListener.elements();
		else
			return (new Vector(1)).elements();
	}

	public void setRenderHook(IRenderHook irenderhook) {
		renderHook = irenderhook;
	}

	public int checkForCollision(SimpleVector simplevector, float f) {
		return myWorld.checkObjCollision(this, simplevector, f);
	}

	public SimpleVector checkForCollisionSpherical(SimpleVector simplevector, float f) {
		return myWorld.checkObjCollisionSpherical(this, simplevector, f);
	}

	public SimpleVector checkForCollisionEllipsoid(SimpleVector simplevector, SimpleVector simplevector1, int i) {
		if (i < 1)
			i = 1;
		return myWorld.checkObjCollisionEllipsoid(this, simplevector, simplevector1, i);
	}

	public void setEllipsoidMode(int i) {
		ellipsoidMode = i;
	}

	public int getEllipsoidMode() {
		return ellipsoidMode;
	}

	public boolean wasTargetOfLastCollision() {
		return wasCollider;
	}

	public void resetCollisionStatus() {
		wasCollider = false;
	}

	public float calcMinDistance(SimpleVector simplevector, SimpleVector simplevector1) {
		wasCollider = false;
		resetPolygonIDCount();
		float f = collide(new float[] { simplevector.x, simplevector.y, simplevector.z }, new float[] { simplevector1.x,
				simplevector1.y, simplevector1.z }, 0.0F, 1E+012F, false);
		if (f != 1E+012F) {
			notifyCollisionListeners(0, 0, new Object3D[] { this });
			wasCollider = true;
		}
		return f;
	}

	public float calcMinDistance(SimpleVector simplevector, SimpleVector simplevector1, float f) {
		return calcMinDistance(simplevector, simplevector1, f, true);
	}

	float calcMinDistance(SimpleVector simplevector, SimpleVector simplevector1, float f, boolean flag) {
		wasCollider = false;
		resetPolygonIDCount();
		float f1 = Config.collideOffset;
		Config.collideOffset = f;
		float f2 = collide(new float[] { simplevector.x, simplevector.y, simplevector.z }, new float[] { simplevector1.x,
				simplevector1.y, simplevector1.z }, 0.0F, f, false);
		Config.collideOffset = f1;
		if (flag && f2 != 1E+012F) {
			notifyCollisionListeners(0, 0, new Object3D[] { this });
			wasCollider = true;
		}
		return f2;
	}

	public void setCenter(SimpleVector simplevector) {
		centerX = simplevector.x;
		centerY = simplevector.y;
		centerZ = simplevector.z;
	}

	public SimpleVector getCenter() {
		return new SimpleVector(centerX, centerY, centerZ);
	}

	public SimpleVector getTransformedCenter() {
		SimpleVector simplevector = new SimpleVector();
		getProjectedPoint(centerX, centerY, centerZ, simplevector, null);
		return simplevector;
	}

	public void setRotationPivot(SimpleVector simplevector) {
		xRotationCenter = simplevector.x;
		yRotationCenter = simplevector.y;
		zRotationCenter = simplevector.z;
	}

	public SimpleVector getRotationPivot() {
		return new SimpleVector(xRotationCenter, yRotationCenter, zRotationCenter);
	}

	public void calcCenter() {
		SimpleVector simplevector = objMesh.calcCenter();
		xRotationCenter = simplevector.x;
		yRotationCenter = simplevector.y;
		zRotationCenter = simplevector.z;
		centerX = simplevector.x;
		centerY = simplevector.y;
		centerZ = simplevector.z;
	}

	public void setOcTree(OcTree octree) {
		if (!Config.doPortalHsr || octree == null)
			ocTree = octree;
		else
			Logger.log("Octree is null or portal rendering is being used!", 1);
	}

	public OcTree getOcTree() {
		return ocTree;
	}

	public void setSector(int i) {
		if (oneSectorOnly) {
			int j = Config.maxPortals - 1;
			if (myWorld != null)
				j = myWorld.portals.anzSectors;
			for (int k = 1; k <= j; k++) {
				if (k == i) {
					sectorEndPoint[k] = objMesh.anzCoords;
					sectorEndPoly[k] = objMesh.anzTri;
				} else {
					sectorEndPoint[k] = 0;
					sectorEndPoly[k] = 0;
				}
				sectorStartPoint[k] = 0;
				sectorStartPoly[k] = 0;
			}

			singleSectorNumber = i;
		} else {
			Logger.log("Multi-sectored objects can't be assigned to a single sector!", 0);
		}
	}

	public void setAsMultiSectored() {
		oneSectorOnly = false;
	}

	public void setOrigin(SimpleVector simplevector) {
		originMatrix.setIdentity();
		originMatrix.translate(simplevector.x, simplevector.y, simplevector.z);
	}

	public void invert() {
		for (int i = 0; i < objMesh.anzTri; i++) {
			int j = objMesh.points[i][0];
			int k = objMesh.points[i][2];
			int l = objMesh.coords[j];
			int i1 = objMesh.coords[k];
			objMesh.coords[j] = i1;
			objMesh.coords[k] = l;
			float f = objVectors.nuOrg[j];
			float f1 = objVectors.nuOrg[k];
			objVectors.nuOrg[j] = f1;
			objVectors.nuOrg[k] = f;
			f = objVectors.nvOrg[j];
			f1 = objVectors.nvOrg[k];
			objVectors.nvOrg[j] = f1;
			objVectors.nvOrg[k] = f;
			f = objVectors.uOrg[j];
			f1 = objVectors.uOrg[k];
			objVectors.uOrg[j] = f1;
			objVectors.uOrg[k] = f;
			f = objVectors.vOrg[j];
			f1 = objVectors.vOrg[k];
			objVectors.vOrg[j] = f1;
			objVectors.vOrg[k] = f;
			f = objVectors.buOrg[j];
			f1 = objVectors.buOrg[k];
			objVectors.buOrg[j] = f1;
			objVectors.buOrg[k] = f;
			f = objVectors.bvOrg[j];
			f1 = objVectors.bvOrg[k];
			objVectors.bvOrg[j] = f1;
			objVectors.bvOrg[k] = f;
		}

	}

	public void invertCulling(boolean flag) {
		reverseCulling = flag;
	}

	public boolean cullingIsInverted() {
		return reverseCulling;
	}

	public void calcNormals() {
		objMesh.calcNormals();
	}

	public void calcTextureWrap() {
		int i = 256;
		int j = 256;
		int k = 256;
		int l = 256;
		int i1 = i >> 1;
		int j1 = j >> 1;
		i--;
		j--;
		int k1 = l >> 1;
		int l1 = k >> 1;
		l--;
		k--;
		for (int i2 = 0; i2 < objMesh.anzCoords; i2++) {
			float f = objMesh.nxOrg[i2];
			float f1 = objMesh.nyOrg[i2];
			float f2 = (float) i1 + f * (float) i1;
			float f3 = (float) j1 + f1 * (float) j1;
			float f4 = (float) k1 + f * (float) k1;
			float f5 = (float) l1 + f1 * (float) l1;
			do {
				if (f2 <= (float) i && f2 >= 0.0F && f3 <= (float) j && f3 >= 0.0F)
					break;
				if (f2 > (float) i)
					f2 = -i;
				else if (f2 < 0.0F)
					f2 = i;
				if (f3 > (float) j)
					f3 = -j;
				else if (f3 < 0.0F)
					f3 = j;
			} while (true);
			do {
				if (f4 <= (float) l && f4 >= 0.0F && f5 <= (float) k && f5 >= 0.0F)
					break;
				if (f4 > (float) l)
					f4 = -l;
				else if (f4 < 0.0F)
					f4 = l;
				if (f5 > (float) k)
					f5 = -k;
				else if (f5 < 0.0F)
					f5 = k;
			} while (true);
			for (int j2 = 0; j2 < objMesh.anzVectors; j2++)
				if (objMesh.coords[j2] == i2) {
					objVectors.uOrg[j2] = f2;
					objVectors.vOrg[j2] = f3;
					objVectors.buOrg[j2] = f4;
					objVectors.bvOrg[j2] = f5;
					objVectors.nuOrg[j2] = f2 / (float) i;
					objVectors.nvOrg[j2] = f3 / (float) j;
				}

		}

	}

	public void calcTextureWrapSpherical() {
		int i = 256;
		int j = 256;
		int k = 256;
		int l = 256;
		calcCenter();
		double d = 1.0D;
		double d1 = 1.0D;
		for (int i1 = 0; i1 < objMesh.anzCoords; i1++) {
			float f = objMesh.xOrg[i1] - centerX;
			float f1 = objMesh.yOrg[i1] - centerY;
			float f2 = objMesh.zOrg[i1] - centerZ;
			float f3 = (float) Math.sqrt(f * f + f1 * f1 + f2 * f2);
			f /= f3;
			f1 /= f3;
			float f4 = (float) ((Math.asin(f) / 3.1415926535897931D + 0.5D) * d);
			float f5 = (float) ((Math.asin(f1) / 3.1415926535897931D + 0.5D) * d1);
			float f6 = f4 * (float) l;
			float f7 = f5 * (float) k;
			f4 *= i;
			f5 *= j;
			do {
				if (f4 <= (float) i && f4 >= 0.0F && f5 <= (float) j && f5 >= 0.0F)
					break;
				if (f4 > (float) i)
					f4 = -i;
				else if (f4 < 0.0F)
					f4 = i;
				if (f5 > (float) j)
					f5 = -j;
				else if (f5 < 0.0F)
					f5 = j;
			} while (true);
			do {
				if (f6 <= (float) l && f6 >= 0.0F && f7 <= (float) k && f7 >= 0.0F)
					break;
				if (f6 > (float) l)
					f6 = -l;
				else if (f6 < 0.0F)
					f6 = l;
				if (f7 > (float) k)
					f7 = -k;
				else if (f7 < 0.0F)
					f7 = k;
			} while (true);
			for (int j1 = 0; j1 < objMesh.anzVectors; j1++)
				if (objMesh.coords[j1] == i1) {
					objVectors.uOrg[j1] = f4;
					objVectors.vOrg[j1] = f5;
					objVectors.buOrg[j1] = f6;
					objVectors.bvOrg[j1] = f7;
					objVectors.nuOrg[j1] = f4 / (float) i;
					objVectors.nvOrg[j1] = f5 / (float) j;
				}

		}

	}

	public void recreateTextureCoords() {
		TextureManager texturemanager = TextureManager.getInstance();
		for (int i = 0; i < objMesh.anzTri; i++) {
			int j = texturemanager.textures[texture[i]].width;
			int k = texturemanager.textures[texture[i]].height;
			int l = 1;
			int i1 = 1;
			if (bumpmap != null) {
				l = texturemanager.textures[bumpmap[i]].width;
				i1 = texturemanager.textures[bumpmap[i]].height;
			}
			for (int j1 = 0; j1 < 3; j1++) {
				int k1 = objMesh.points[i][j1];
				objVectors.buOrg[k1] = objVectors.nuOrg[k1] * (float) l;
				objVectors.bvOrg[k1] = objVectors.nvOrg[k1] * (float) i1;
				objVectors.uOrg[k1] = objVectors.nuOrg[k1] * (float) j;
				objVectors.vOrg[k1] = objVectors.nvOrg[k1] * (float) k;
			}

		}

	}

	public void setAllTextures(String s, String s1) {
		setAllTextures(s, s, s1);
	}

	public void setAllTextures(String s, String s1, String s2) {
		checkBumpmap();
		TextureManager texturemanager = TextureManager.getInstance();
		int i = texturemanager.getTextureID(s1);
		int j = texturemanager.getTextureID(s);
		int k = texturemanager.getTextureID(s2);
		if (i != -1 && j != -1 && k != -1) {
			for (int l = 0; l < objMesh.anzTri; l++) {
				texture[l] = i;
				basemap[l] = j;
				bumpmap[l] = k;
			}

		} else {
			Logger.log("Tried to set an undefined texture!", 0);
		}
		if (!texturemanager.textures[k].isBumpmap)
			texturemanager.textures[k].createBumpmap();
	}

	public void setBaseTexture(String s) {
		TextureManager texturemanager = TextureManager.getInstance();
		int i = texturemanager.getTextureID(s);
		if (i != -1) {
			for (int j = 0; j < objMesh.anzTri; j++)
				basemap[j] = i;

		} else {
			Logger.log("Tried to set an undefined texture as base!", 0);
		}
	}

	public void setTexture(String s) {
		TextureManager texturemanager = TextureManager.getInstance();
		int i = texturemanager.getTextureID(s);
		if (i != -1) {
			for (int j = 0; j < objMesh.anzTri; j++)
				texture[j] = i;

		} else {
			Logger.log("Tried to set an undefined texture as default!", 0);
		}
	}

	public void setTexture(TextureInfo textureinfo) {
		if (texture != null) {
			if (textureinfo.stageCnt > 1) {
				if (multiTex == null) {
					multiTex = new int[Config.maxTextureLayers - 1][texture.length];
					multiMode = new int[Config.maxTextureLayers - 1][texture.length];
					for (int i = 0; i < texture.length; i++) {
						for (int k = 0; k < Config.maxTextureLayers - 1; k++)
							multiTex[k][i] = -1;

					}

				}
				objVectors.createMultiCoords();
				usesMultiTexturing = true;
			} else {
				usesMultiTexturing = false;
			}
			int j = textureinfo.textures[0];
			if (j != -1) {
				for (int l = 0; l < objMesh.anzTri; l++)
					texture[l] = j;

			} else {
				Logger.log("Tried to set an undefined texture as default!", 0);
			}
			for (int i1 = 1; i1 < textureinfo.stageCnt; i1++) {
				int j1 = textureinfo.textures[i1];
				int k1 = textureinfo.mode[i1];
				int l1 = i1 - 1;
				for (int i2 = 0; i2 < objMesh.anzTri; i2++) {
					multiTex[l1][i2] = j1;
					multiMode[l1][i2] = k1;
				}

				for (int j2 = 0; j2 < objVectors.nuOrg.length; j2++) {
					objVectors.uMul[l1][j2] = objVectors.nuOrg[j2];
					objVectors.vMul[l1][j2] = objVectors.nvOrg[j2];
				}

			}

			maxStagesUsed = textureinfo.stageCnt;
		}
	}

	public void setBumpmapTexture(String s) {
		checkBumpmap();
		TextureManager texturemanager = TextureManager.getInstance();
		int i = texturemanager.getTextureID(s);
		if (i != -1) {
			for (int j = 0; j < objMesh.anzTri; j++)
				bumpmap[j] = i;

			if (!texturemanager.textures[i].isBumpmap)
				texturemanager.textures[i].createBumpmap();
		} else {
			Logger.log("Tried to set an undefined texture as bumpmap!", 0);
		}
	}

	public void removeMultiTexturing() {
		maxStagesUsed = 1;
		usesMultiTexturing = false;
		multiMode = (int[][]) null;
		multiTex = (int[][]) null;
	}

	public float rayIntersectsAABB(SimpleVector simplevector, SimpleVector simplevector1, boolean flag) {
		return rayIntersectsAABB(new float[] { simplevector.x, simplevector.y, simplevector.z }, new float[] {
				simplevector1.x, simplevector1.y, simplevector1.z }, flag);
	}

	public float rayIntersectsAABB(SimpleVector simplevector, SimpleVector simplevector1) {
		return rayIntersectsAABB(new float[] { simplevector.x, simplevector.y, simplevector.z }, new float[] {
				simplevector1.x, simplevector1.y, simplevector1.z }, false);
	}

	final float rayIntersectsAABB(float af[], float af1[], boolean flag) {
		if (!hasBoundingBox)
			return 1E+012F;
		Matrix amatrix[] = getInverseWorldTransformation();
		Matrix matrix = amatrix[1];
		float f6 = matrix.mat[0][0];
		float f7 = matrix.mat[1][0];
		float f8 = matrix.mat[1][1];
		float f9 = matrix.mat[2][1];
		float f10 = matrix.mat[2][0];
		float f11 = matrix.mat[0][1];
		float f12 = matrix.mat[2][2];
		float f13 = matrix.mat[1][2];
		float f14 = matrix.mat[0][2];
		float f15 = matrix.mat[3][0];
		float f16 = matrix.mat[3][1];
		float f17 = matrix.mat[3][2];
		float f = af1[0] * f6 + af1[1] * f7 + af1[2] * f10;
		float f1 = af1[0] * f11 + af1[1] * f8 + af1[2] * f9;
		float f2 = af1[0] * f14 + af1[1] * f13 + af1[2] * f12;
		float f3 = af[0] * f6 + af[1] * f7 + af[2] * f10 + f15;
		float f4 = af[0] * f11 + af[1] * f8 + af[2] * f9 + f16;
		float f5 = af[0] * f14 + af[1] * f13 + af[2] * f12 + f17;
		float f18 = -1E+011F;
		float f19 = -1E+011F;
		float f20 = -1E+011F;
		float f21 = 1E+011F;
		float f22 = 1E+011F;
		float f23 = 1E+011F;
		if (!flag) {
			float f24 = (float) Math.sqrt(f * f + f1 * f1 + f2 * f2);
			f /= f24;
			f1 /= f24;
			f2 /= f24;
		}
		int i = objMesh.obbStart;
		float f25 = objMesh.xOrg[i];
		float f26 = objMesh.yOrg[i];
		float f27 = objMesh.zOrg[i];
		float f28 = f25;
		float f29 = f26;
		float f30 = f27;
		for (int j = 1; j < 8; j++) {
			float f35 = objMesh.xOrg[j + i];
			float f37 = objMesh.zOrg[j + i];
			float f38 = objMesh.yOrg[j + i];
			if (f35 < f25)
				f25 = f35;
			else if (f35 > f28)
				f28 = f35;
			if (f38 < f26)
				f26 = f38;
			else if (f38 > f29)
				f29 = f38;
			if (f37 < f27) {
				f27 = f37;
				continue;
			}
			if (f37 > f30)
				f30 = f37;
		}

		if (Math.abs(f) > 1E-009F) {
			f18 = (f25 - f3) / f;
			f21 = (f28 - f3) / f;
			if (f18 > f21) {
				float f31 = f18;
				f18 = f21;
				f21 = f31;
			}
		}
		if (Math.abs(f1) > 1E-009F) {
			f19 = (f26 - f4) / f1;
			f22 = (f29 - f4) / f1;
			if (f19 > f22) {
				float f32 = f19;
				f19 = f22;
				f22 = f32;
			}
		}
		if (Math.abs(f2) > 1E-009F) {
			f20 = (f27 - f5) / f2;
			f23 = (f30 - f5) / f2;
			if (f20 > f23) {
				float f33 = f20;
				f20 = f23;
				f23 = f33;
			}
		}
		float f34 = f18;
		if (f34 < f19)
			f34 = f19;
		if (f34 < f20)
			f34 = f20;
		float f36 = f21;
		if (f36 > f22)
			f36 = f22;
		if (f36 > f23)
			f36 = f23;
		if (f34 <= f36 && f36 > 0.0F)
			return f34;
		else
			return 1E+012F;
	}

	public boolean ellipsoidIntersectsAABB(SimpleVector simplevector, SimpleVector simplevector1) {
		if (!Config.useFastCollisionDetection)
			return true;
		if (!hasBoundingBox)
			return false;
		boolean flag = true;
		Matrix amatrix[] = getInverseWorldTransformation();
		Matrix matrix = amatrix[1];
		float f3 = matrix.mat[0][0];
		float f4 = matrix.mat[1][0];
		float f5 = matrix.mat[1][1];
		float f6 = matrix.mat[2][1];
		float f7 = matrix.mat[2][0];
		float f8 = matrix.mat[0][1];
		float f9 = matrix.mat[2][2];
		float f10 = matrix.mat[1][2];
		float f11 = matrix.mat[0][2];
		float f12 = matrix.mat[3][0];
		float f13 = matrix.mat[3][1];
		float f14 = matrix.mat[3][2];
		float f = simplevector.x * f3 + simplevector.y * f4 + simplevector.z * f7 + f12;
		float f1 = simplevector.x * f8 + simplevector.y * f5 + simplevector.z * f6 + f13;
		float f2 = simplevector.x * f11 + simplevector.y * f10 + simplevector.z * f9 + f14;
		float f15 = Math.abs(simplevector1.x * f3 + simplevector1.y * f4 + simplevector1.z * f7);
		float f16 = Math.abs(simplevector1.x * f8 + simplevector1.y * f5 + simplevector1.z * f6);
		float f17 = Math.abs(simplevector1.x * f11 + simplevector1.y * f10 + simplevector1.z * f9);
		f /= f15;
		f1 /= f16;
		f2 /= f17;
		int i = objMesh.obbStart;
		float f18 = 1.0F / f15;
		float f19 = 1.0F / f16;
		float f20 = 1.0F / f17;
		float f21 = objMesh.xOrg[i] * f18;
		float f22 = objMesh.yOrg[i] * f19;
		float f23 = objMesh.zOrg[i] * f20;
		float f24 = f21;
		float f25 = f22;
		float f26 = f23;
		int j = 1 + i;
		int k = 8 + i;
		for (int l = j; l < k; l++) {
			float f27 = objMesh.xOrg[l] * f18;
			float f28 = objMesh.yOrg[l] * f19;
			float f29 = objMesh.zOrg[l] * f20;
			if (f27 < f21)
				f21 = f27;
			else if (f27 > f24)
				f24 = f27;
			if (f28 < f22)
				f22 = f28;
			else if (f28 > f25)
				f25 = f28;
			if (f29 < f23) {
				f23 = f29;
				continue;
			}
			if (f29 > f26)
				f26 = f29;
		}

		if (f + 1.0F < f21 || f - 1.0F > f24 || f1 + 1.0F < f22 || f1 - 1.0F > f25 || f2 + 1.0F < f23 || f2 - 1.0F > f26)
			flag = false;
		return flag;
	}

	public boolean sphereIntersectsAABB(SimpleVector simplevector, float f) {
		return sphereIntersectsAABB(simplevector.toArray(), f);
	}

	final boolean sphereIntersectsAABB(float af[], float f) {
		if (!Config.useFastCollisionDetection)
			return true;
		if (!hasBoundingBox)
			return false;
		boolean flag = true;
		Matrix amatrix[] = getInverseWorldTransformation();
		Matrix matrix = amatrix[1];
		float f4 = matrix.mat[0][0];
		float f5 = matrix.mat[1][0];
		float f6 = matrix.mat[1][1];
		float f7 = matrix.mat[2][1];
		float f8 = matrix.mat[2][0];
		float f9 = matrix.mat[0][1];
		float f10 = matrix.mat[2][2];
		float f11 = matrix.mat[1][2];
		float f12 = matrix.mat[0][2];
		float f13 = matrix.mat[3][0];
		float f14 = matrix.mat[3][1];
		float f15 = matrix.mat[3][2];
		float f1 = af[0] * f4 + af[1] * f5 + af[2] * f8 + f13;
		float f2 = af[0] * f9 + af[1] * f6 + af[2] * f7 + f14;
		float f3 = af[0] * f12 + af[1] * f11 + af[2] * f10 + f15;
		int i = objMesh.obbStart;
		float f16 = objMesh.xOrg[i];
		float f17 = objMesh.yOrg[i];
		float f18 = objMesh.zOrg[i];
		float f19 = f16;
		float f20 = f17;
		float f21 = f18;
		for (int j = 1; j < 8; j++) {
			float f22 = objMesh.xOrg[j + i];
			float f23 = objMesh.zOrg[j + i];
			float f24 = objMesh.yOrg[j + i];
			if (f22 < f16)
				f16 = f22;
			else if (f22 > f19)
				f19 = f22;
			if (f24 < f17)
				f17 = f24;
			else if (f24 > f20)
				f20 = f24;
			if (f23 < f18) {
				f18 = f23;
				continue;
			}
			if (f23 > f21)
				f21 = f23;
		}

		if (f1 + f < f16 || f1 - f > f19 || f2 + f < f17 || f2 - f > f20 || f3 + f < f18 || f3 - f > f21)
			flag = false;
		return flag;
	}

	final float collide(float af[], float af1[], float f, float f1) {
		return collide(af, af1, f, f1, true);
	}

	private final float collide(float af[], float af1[], float f, float f1, boolean flag) {
		float af2[] = new float[3];
		float af3[] = new float[3];
		float af4[] = new float[3];
		float af5[] = new float[3];
		float af6[] = new float[3];
		float af7[] = new float[3];
		float af8[] = new float[3];
		float f3 = Config.collideOffset;
		if (optimizeColDet && largestPolygonSize != -1F && flag) {
			float f4 = largestPolygonSize + 2.0F + f;
			if (f4 < f3)
				f3 = f4;
		}
		int ai[] = null;
		int i = 0;
		float f5;
		float f6;
		float f7;
		if (!isMainWorld) {
			Matrix amatrix[] = getInverseWorldTransformation();
			Matrix matrix = amatrix[0];
			Matrix matrix1 = amatrix[1];
			float f9 = matrix1.mat[0][0];
			float f11 = matrix1.mat[1][0];
			float f13 = matrix1.mat[1][1];
			float f15 = matrix1.mat[2][1];
			float f17 = matrix1.mat[2][0];
			float f19 = matrix1.mat[0][1];
			float f21 = matrix1.mat[2][2];
			float f23 = matrix1.mat[1][2];
			float f25 = matrix1.mat[0][2];
			float f27 = matrix1.mat[3][0];
			float f29 = matrix1.mat[3][1];
			float f31 = matrix1.mat[3][2];
			af2[0] = af1[0] * f9 + af1[1] * f11 + af1[2] * f17;
			af2[1] = af1[0] * f19 + af1[1] * f13 + af1[2] * f15;
			af2[2] = af1[0] * f25 + af1[1] * f23 + af1[2] * f21;
			f5 = af[0] * f9 + af[1] * f11 + af[2] * f17 + f27;
			f6 = af[0] * f19 + af[1] * f13 + af[2] * f15 + f29;
			f7 = af[0] * f25 + af[1] * f23 + af[2] * f21 + f31;
			af3[0] = f5;
			af3[1] = f6;
			af3[2] = f7;
			if (Config.doPortalHsr && dynSectorDetect && !hasPortals && objMesh.obbEnd - objMesh.obbStart == 7) {
				float af9[] = new float[8];
				float af10[] = new float[8];
				float af11[] = new float[8];
				float f10 = matrix.mat[0][0];
				float f12 = matrix.mat[1][0];
				float f14 = matrix.mat[1][1];
				float f16 = matrix.mat[2][1];
				float f18 = matrix.mat[2][0];
				float f20 = matrix.mat[0][1];
				float f22 = matrix.mat[2][2];
				float f24 = matrix.mat[1][2];
				float f26 = matrix.mat[0][2];
				float f28 = matrix.mat[3][0];
				float f30 = matrix.mat[3][1];
				float f32 = matrix.mat[3][2];
				for (int j3 = 0; j3 < 8; j3++) {
					float f33 = objMesh.zOrg[j3 + objMesh.obbStart];
					float f34 = objMesh.xOrg[j3 + objMesh.obbStart];
					float f35 = objMesh.yOrg[j3 + objMesh.obbStart];
					af9[j3] = f34 * f10 + f35 * f12 + f33 * f18 + f28;
					af10[j3] = f34 * f20 + f35 * f14 + f33 * f16 + f30;
					af11[j3] = f34 * f26 + f35 * f24 + f33 * f22 + f32;
				}

				ai = new int[Config.maxPortals];
				ai = myWorld.portals.detectAllCoveredSectors(ai, af9, af10, af11);
				i = ai[0];
			}
		} else {
			af2[0] = af1[0];
			af2[1] = af1[1];
			af2[2] = af1[2];
			af3[0] = af[0];
			af3[1] = af[1];
			af3[2] = af[2];
			f5 = af3[0];
			f6 = af3[1];
			f7 = af3[2];
		}
		int j = 0;
		Object aobj[] = null;
		if (ocTree != null && !Config.doPortalHsr && ocTree.getCollisionUse()) {
			float f8 = f1 + f;
			aobj = ocTree.getColliderLeafs(af3[0], af3[1], af3[2], f8 * ocTree.getRadiusMultiplier());
			j = ((Integer) aobj[0]).intValue();
			if (j == 0)
				return 1E+012F;
		}
		int k = 0;
		int l = 0;
		int i1 = 0;
		boolean flag1 = false;
		float f2 = 1E+012F;
		int j1 = -1;
		boolean flag2 = false;
		boolean flag3 = false;
		int ai1[] = new int[Math.max(Config.maxPortals, 1)];
		int i2 = 0;
		if (myWorld != null) {
			for (int j2 = 1; j2 <= myWorld.portals.anzSectors; j2++)
				if (f1 == 1E+012F || af[0] >= myWorld.portals.bounding[j2][0] - f1
						&& af[0] <= myWorld.portals.bounding[j2][1] + f1 && af[1] >= myWorld.portals.bounding[j2][2] - f1
						&& af[1] <= myWorld.portals.bounding[j2][3] + f1 && af[2] >= myWorld.portals.bounding[j2][4] - f1
						&& af[2] <= myWorld.portals.bounding[j2][5] + f1) {
					ai1[i2] = j2;
					i2++;
				}

		}
		if (i2 == 0) {
			i2 = 1;
			ai1[0] = 0;
		}
		for (int k2 = 0; k2 < i2; k2++) {
			int k1 = 0;
			int l1 = 0;
			if (isMainWorld || !oneSectorOnly) {
				k1 = sectorStartPoly[ai1[k2]];
				l1 = sectorEndPoly[ai1[k2]] + 1;
				if (k1 + 1 == l1) {
					l1 = 0;
					k1 = 0;
				}
			} else if (!dynSectorDetect && ai1[k2] == singleSectorNumber || singleSectorNumber == 0) {
				k1 = 0;
				l1 = objMesh.anzTri;
				k2 = i2;
			} else if (dynSectorDetect)
				if (i == 1) {
					if (ai[1] == ai1[k2]) {
						k1 = 0;
						l1 = objMesh.anzTri;
						k2 = i2;
					}
				} else {
					int l2 = 1;
					do {
						if (l2 > i)
							break;
						if (ai[l2] == ai1[k2]) {
							k1 = 0;
							l1 = objMesh.anzTri;
							k2 = i2;
							break;
						}
						l2++;
					} while (true);
				}
			boolean flag4 = false;
			OcTreeNode aoctreenode[] = null;
			int ai2[] = null;
			int i3 = 0;
			if (ocTree != null && !Config.doPortalHsr && ocTree.getCollisionUse()) {
				aoctreenode = (OcTreeNode[]) aobj[1];
				flag4 = true;
			}
			int ai3[] = objMesh.coords;
			float af12[] = objMesh.xOrg;
			float af13[] = objMesh.yOrg;
			float af14[] = objMesh.zOrg;
			do {
				if (flag4) {
					ai2 = aoctreenode[i3].getPolygons();
					k1 = 0;
					l1 = aoctreenode[i3].getPolyCount();
					i3++;
				}
				int k3 = k1;
				do {
					if (k3 >= l1)
						break;
					int l3 = k3;
					if (flag4)
						l3 = ai2[k3];
					int j4 = ai3[objMesh.points[l3][0]];
					float f36 = af12[j4];
					float f37 = af13[j4];
					float f38 = af14[j4];
					if (f1 == 1E+012F || Math.abs(f36 - f5) <= f3 && Math.abs(f37 - f6) <= f3 && Math.abs(f38 - f7) <= f3) {
						int k4 = ai3[objMesh.points[l3][2]];
						int l4 = ai3[objMesh.points[l3][1]];
						af7[0] = af12[l4] - f36;
						af7[1] = af13[l4] - f37;
						af7[2] = af14[l4] - f38;
						af8[0] = af12[k4] - f36;
						af8[1] = af13[k4] - f37;
						af8[2] = af14[k4] - f38;
						Vectors.calcCross(af4, af2, af8);
						float f39 = Vectors.calcDot(af7, af4);
						if (f39 >= 1E-009F) {
							float f40 = 1.0F / f39;
							af5[0] = f5 - f36;
							af5[1] = f6 - f37;
							af5[2] = f7 - f38;
							float f41 = Vectors.calcDot(af5, af4) * f40;
							if ((double) f41 >= 0.0D && f41 <= 1.0F) {
								Vectors.calcCross(af6, af5, af7);
								float f42 = Vectors.calcDot(af2, af6) * f40;
								if ((double) f42 >= 0.0D && (double) (f41 + f42) <= 1.0D) {
									float f43 = Vectors.calcDot(af8, af6) * f40;
									if (f43 < f && f43 >= 0.0F) {
										flag1 = true;
										f2 = f43;
										j1 = l3;
									} else {
										flag1 = false;
										if (f43 < f2 && f43 >= 0.0F) {
											f2 = f43;
											j1 = l3;
										}
									}
									k++;
								}
							}
						}
					} else {
						l++;
					}
					i1++;
					if (flag1) {
						int i4 = l1;
						k2 = i2;
						break;
					}
					k3++;
				} while (true);
			} while (flag4 && i3 < j);
		}

		if (j1 != -1)
			addPolygonID(j1);
		return f2;
	}

	final float[] collideSpherical(float af[], float f, float f1, boolean aflag[], boolean flag) {
		float af1[] = new float[3];
		double ad[] = new double[3];
		double ad1[] = new double[3];
		int ai[] = null;
		int i = 0;
		Matrix matrix = null;
		float f8 = Config.collideOffset;
		if (optimizeColDet && largestPolygonSize != -1F) {
			float f9 = largestPolygonSize + f + 1.0F;
			if (f9 < f8)
				f8 = f9;
		}
		float f2;
		float f4;
		float f6;
		if (!isMainWorld) {
			Matrix amatrix[] = getInverseWorldTransformation();
			matrix = amatrix[0];
			Matrix matrix1 = amatrix[1];
			float f10 = matrix1.mat[0][0];
			float f12 = matrix1.mat[1][0];
			float f14 = matrix1.mat[1][1];
			float f16 = matrix1.mat[2][1];
			float f18 = matrix1.mat[2][0];
			float f20 = matrix1.mat[0][1];
			float f23 = matrix1.mat[2][2];
			float f26 = matrix1.mat[1][2];
			float f29 = matrix1.mat[0][2];
			float f32 = matrix1.mat[3][0];
			float f35 = matrix1.mat[3][1];
			float f38 = matrix1.mat[3][2];
			f2 = af[0] * f10 + af[1] * f12 + af[2] * f18 + f32;
			f4 = af[0] * f20 + af[1] * f14 + af[2] * f16 + f35;
			f6 = af[0] * f29 + af[1] * f26 + af[2] * f23 + f38;
			af1[0] = f2;
			af1[1] = f4;
			af1[2] = f6;
			if (Config.doPortalHsr && dynSectorDetect && !hasPortals && objMesh.obbEnd - objMesh.obbStart == 7) {
				float af2[] = new float[8];
				float af3[] = new float[8];
				float af4[] = new float[8];
				float f11 = matrix.mat[0][0];
				float f13 = matrix.mat[1][0];
				float f15 = matrix.mat[1][1];
				float f17 = matrix.mat[2][1];
				float f19 = matrix.mat[2][0];
				float f21 = matrix.mat[0][1];
				float f24 = matrix.mat[2][2];
				float f27 = matrix.mat[1][2];
				float f30 = matrix.mat[0][2];
				float f33 = matrix.mat[3][0];
				float f36 = matrix.mat[3][1];
				float f39 = matrix.mat[3][2];
				for (int i3 = 0; i3 < 8; i3++) {
					int j3 = i3 + objMesh.obbStart;
					float f48 = objMesh.zOrg[j3];
					float f50 = objMesh.xOrg[j3];
					float f51 = objMesh.yOrg[j3];
					af2[i3] = f50 * f11 + f51 * f13 + f48 * f19 + f33;
					af3[i3] = f50 * f21 + f51 * f15 + f48 * f17 + f36;
					af4[i3] = f50 * f30 + f51 * f27 + f48 * f24 + f39;
				}

				ai = new int[Config.maxPortals];
				ai = myWorld.portals.detectAllCoveredSectors(ai, af2, af3, af4);
				i = ai[0];
			}
		} else {
			af1[0] = af[0];
			af1[1] = af[1];
			af1[2] = af[2];
			f2 = af1[0];
			f4 = af1[1];
			f6 = af1[2];
		}
		int j = 0;
		Object aobj[] = null;
		if (ocTree != null && !Config.doPortalHsr && ocTree.getCollisionUse()) {
			aobj = ocTree.getColliderLeafs(af1[0], af1[1], af1[2], f * ocTree.getRadiusMultiplier());
			j = ((Integer) aobj[0]).intValue();
			if (j == 0) {
				af1[0] = af[0];
				af1[1] = af[1];
				af1[2] = af[2];
				return af1;
			}
		}
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag3 = false;
		int ai1[] = new int[Math.max(Config.maxPortals, 1)];
		int i1 = 0;
		if (myWorld != null && Config.doPortalHsr) {
			for (int j1 = 1; j1 <= myWorld.portals.anzSectors; j1++)
				if (f1 == 1E+012F || af[0] >= myWorld.portals.bounding[j1][0] - f1
						&& af[0] <= myWorld.portals.bounding[j1][1] + f1 && af[1] >= myWorld.portals.bounding[j1][2] - f1
						&& af[1] <= myWorld.portals.bounding[j1][3] + f1 && af[2] >= myWorld.portals.bounding[j1][4] - f1
						&& af[2] <= myWorld.portals.bounding[j1][5] + f1) {
					ai1[i1] = j1;
					i1++;
				}

		}
		if (i1 == 0) {
			i1 = 1;
			ai1[0] = 0;
		}
		for (int k1 = 0; k1 < i1; k1++) {
			int k = 0;
			int l = 0;
			if (isMainWorld || !oneSectorOnly) {
				k = sectorStartPoly[ai1[k1]];
				l = sectorEndPoly[ai1[k1]] + 1;
				if (k + 1 == l) {
					l = 0;
					k = 0;
				}
			} else if (!dynSectorDetect && ai1[k1] == singleSectorNumber || singleSectorNumber == 0) {
				k = 0;
				l = objMesh.anzTri;
				k1 = i1;
			} else if (dynSectorDetect)
				if (i == 1) {
					if (ai[1] == ai1[k1]) {
						k = 0;
						l = objMesh.anzTri;
						k1 = i1;
					}
				} else {
					int l1 = 1;
					do {
						if (l1 > i)
							break;
						if (ai[l1] == ai1[k1]) {
							k = 0;
							l = objMesh.anzTri;
							k1 = i1;
							break;
						}
						l1++;
					} while (true);
				}
			double d9 = 0.0D;
			boolean flag4 = false;
			OcTreeNode aoctreenode[] = null;
			int ai2[] = null;
			int i2 = 0;
			if (ocTree != null && !Config.doPortalHsr && ocTree.getCollisionUse()) {
				aoctreenode = (OcTreeNode[]) aobj[1];
				flag4 = true;
			}
			do {
				if (flag4) {
					ai2 = aoctreenode[i2].getPolygons();
					k = 0;
					l = aoctreenode[i2].getPolyCount();
					i2++;
				}
				for (int j2 = k; j2 < l; j2++) {
					int k2 = j2;
					if (flag4)
						k2 = ai2[j2];
					int l2 = objMesh.coords[objMesh.points[k2][0]];
					float f44 = objMesh.xOrg[l2];
					float f46 = objMesh.yOrg[l2];
					float f49 = objMesh.zOrg[l2];
					boolean flag5 = false;
					if (f1 != 1E+012F && (Math.abs(f44 - f2) > f8 || Math.abs(f46 - f4) > f8 || Math.abs(f49 - f6) > f8))
						continue;
					int k3 = objMesh.coords[objMesh.points[k2][2]];
					int l3 = objMesh.coords[objMesh.points[k2][1]];
					double d12 = objMesh.xOrg[l3] - f44;
					double d13 = objMesh.yOrg[l3] - f46;
					double d14 = objMesh.zOrg[l3] - f49;
					double d15 = objMesh.xOrg[k3] - f44;
					double d16 = objMesh.yOrg[k3] - f46;
					double d17 = objMesh.zOrg[k3] - f49;
					double d = d13 * d17 - d14 * d16;
					double d1 = d14 * d15 - d12 * d17;
					double d2 = d12 * d16 - d13 * d15;
					double d18 = Math.sqrt(d * d + d1 * d1 + d2 * d2);
					d /= d18;
					d1 /= d18;
					d2 /= d18;
					double d20 = (d * (double) af1[0] + d1 * (double) af1[1] + d2 * (double) af1[2])
							- (d * (double) f44 + d1 * (double) f46 + d2 * (double) f49);
					if (Math.abs(d20) >= (double) f)
						continue;
					double d3 = (double) af1[0] - d * d20;
					double d5 = (double) af1[1] - d1 * d20;
					double d7 = (double) af1[2] - d2 * d20;
					double d21 = 0.0D;
					int i4 = 0;
					do {
						if (i4 >= 3)
							break;
						int k4 = objMesh.coords[objMesh.points[k2][i4]];
						float f52 = objMesh.xOrg[k4];
						float f54 = objMesh.yOrg[k4];
						float f56 = objMesh.zOrg[k4];
						ad[0] = (double) f52 - d3;
						ad[1] = (double) f54 - d5;
						ad[2] = (double) f56 - d7;
						int i5 = (i4 + 1) % 3;
						k4 = objMesh.coords[objMesh.points[k2][i5]];
						f52 = objMesh.xOrg[k4];
						f54 = objMesh.yOrg[k4];
						f56 = objMesh.zOrg[k4];
						ad1[0] = (double) f52 - d3;
						ad1[1] = (double) f54 - d5;
						ad1[2] = (double) f56 - d7;
						double d23 = Vectors.calcDot(ad, ad1);
						double d24 = Math.sqrt(ad[0] * ad[0] + ad[1] * ad[1] + ad[2] * ad[2]);
						double d26 = Math.sqrt(ad1[0] * ad1[0] + ad1[1] * ad1[1] + ad1[2] * ad1[2]);
						double d27 = d24 * d26;
						double d28 = Math.acos(d23 / d27);
						if (Double.isNaN(d28))
							d28 = 0.0D;
						d21 += d28;
						if (d21 >= 6.2203534541077907D)
							break;
						i4++;
					} while (true);
					if (d21 >= 6.2203534541077907D) {
						flag5 = true;
					} else {
						int j4 = 0;
						do {
							if (j4 >= 3)
								break;
							int l4 = objMesh.coords[objMesh.points[k2][j4]];
							float f53 = objMesh.xOrg[l4];
							float f55 = objMesh.yOrg[l4];
							float f57 = objMesh.zOrg[l4];
							ad[0] = af1[0] - f53;
							ad[1] = af1[1] - f55;
							ad[2] = af1[2] - f57;
							int j5 = (j4 + 1) % 3;
							l4 = objMesh.coords[objMesh.points[k2][j5]];
							float f58 = objMesh.xOrg[l4];
							float f59 = objMesh.yOrg[l4];
							float f60 = objMesh.zOrg[l4];
							ad1[0] = f58 - f53;
							ad1[1] = f59 - f55;
							ad1[2] = f60 - f57;
							double d19 = Math.sqrt(ad1[0] * ad1[0] + ad1[1] * ad1[1] + ad1[2] * ad1[2]);
							ad1[0] /= d19;
							ad1[1] /= d19;
							ad1[2] /= d19;
							double d25 = Vectors.calcDot(ad, ad1);
							double d4;
							double d6;
							double d8;
							if (d25 <= 0.0D) {
								d4 = f53;
								d6 = f55;
								d8 = f57;
							} else {
								double d10 = Math.sqrt((f53 - f58) * (f53 - f58) + (f55 - f59) * (f55 - f59) + (f57 - f60)
										* (f57 - f60));
								if (d25 >= d10) {
									d4 = f58;
									d6 = f59;
									d8 = f60;
								} else {
									ad[0] = ad1[0] * d25;
									ad[1] = ad1[1] * d25;
									ad[2] = ad1[2] * d25;
									d4 = (double) f53 + ad[0];
									d6 = (double) f55 + ad[1];
									d8 = (double) f57 + ad[2];
								}
							}
							double d11 = Math.sqrt((d4 - (double) af1[0]) * (d4 - (double) af1[0]) + (d6 - (double) af1[1])
									* (d6 - (double) af1[1]) + (d8 - (double) af1[2]) * (d8 - (double) af1[2]));
							float f61 = f;
							if (flag)
								f61 *= Config.collideEdgeMul;
							if (d11 < (double) f61) {
								flag5 = true;
								break;
							}
							j4++;
						} while (true);
					}
					if (flag5) {
						addPolygonID(k2);
						double d22 = (double) f - d20;
						flag1 = true;
						af1[0] += d * d22;
						af1[1] += d1 * d22;
						af1[2] += d2 * d22;
					}
				}

			} while (flag4 && i2 < j);
		}

		if (!isMainWorld) {
			float f22 = matrix.mat[0][0];
			float f25 = matrix.mat[1][0];
			float f28 = matrix.mat[1][1];
			float f31 = matrix.mat[2][1];
			float f34 = matrix.mat[2][0];
			float f37 = matrix.mat[0][1];
			float f40 = matrix.mat[2][2];
			float f41 = matrix.mat[1][2];
			float f42 = matrix.mat[0][2];
			float f43 = matrix.mat[3][0];
			float f45 = matrix.mat[3][1];
			float f47 = matrix.mat[3][2];
			float f3 = af1[0] * f22 + af1[1] * f25 + af1[2] * f34 + f43;
			float f5 = af1[0] * f37 + af1[1] * f28 + af1[2] * f31 + f45;
			float f7 = af1[0] * f42 + af1[1] * f41 + af1[2] * f40 + f47;
			af1[0] = f3;
			af1[1] = f5;
			af1[2] = f7;
		}
		aflag[0] |= flag1;
		return af1;
	}

	final void collideEllipsoid(CollisionInfo collisioninfo, float f) {
		Matrix matrix = null;
		if (collisioninfo.addTransMat != null)
			matrix = collisioninfo.addTransMat.cloneMatrix();
		Matrix matrix1 = null;
		Matrix matrix2 = null;
		boolean flag = false;
		float f1 = 0.0F;
		float f2 = 0.0F;
		float f3 = 0.0F;
		float f4 = 0.0F;
		float f5 = 0.0F;
		float f6 = 0.0F;
		float f7 = 0.0F;
		float f8 = 0.0F;
		float f9 = 0.0F;
		float f10 = 0.0F;
		float f11 = 0.0F;
		float f12 = 0.0F;
		int ai[] = null;
		int i = 0;
		Matrix matrix3 = null;
		if (matrix != null) {
			flag = true;
			matrix1 = matrix.invert();
			Matrix matrix4 = getWorldTransformation();
			matrix4.matMul(matrix1);
			matrix2 = collisioninfo.addRotMat.invert3x3();
			f1 = matrix4.mat[0][0];
			f2 = matrix4.mat[1][0];
			f3 = matrix4.mat[1][1];
			f4 = matrix4.mat[2][1];
			f5 = matrix4.mat[2][0];
			f6 = matrix4.mat[0][1];
			f7 = matrix4.mat[2][2];
			f8 = matrix4.mat[1][2];
			f9 = matrix4.mat[0][2];
			f10 = matrix4.mat[3][0];
			f11 = matrix4.mat[3][1];
			f12 = matrix4.mat[3][2];
		}
		SimpleVector simplevector = new SimpleVector(collisioninfo.r3Pos);
		SimpleVector simplevector1 = new SimpleVector(simplevector);
		SimpleVector simplevector2 = new SimpleVector(collisioninfo.r3Velocity);
		SimpleVector simplevector3 = new SimpleVector();
		SimpleVector simplevector4 = new SimpleVector();
		Object obj = null;
		SimpleVector simplevector6 = null;
		SimpleVector simplevector7 = null;
		boolean flag1 = ocTree != null && !Config.doPortalHsr && ocTree.getCollisionUse();
		if (!isMainWorld || flag) {
			Object obj1 = null;
			Object obj2 = null;
			if (!flag || flag1) {
				Matrix amatrix[] = getInverseWorldTransformation();
				matrix3 = amatrix[0];
				Matrix matrix5 = amatrix[1];
				float f13 = matrix5.mat[0][0];
				float f16 = matrix5.mat[1][0];
				float f18 = matrix5.mat[1][1];
				float f20 = matrix5.mat[2][1];
				float f23 = matrix5.mat[2][0];
				float f26 = matrix5.mat[0][1];
				float f29 = matrix5.mat[2][2];
				float f32 = matrix5.mat[1][2];
				float f35 = matrix5.mat[0][2];
				float f38 = matrix5.mat[3][0];
				float f41 = matrix5.mat[3][1];
				float f44 = matrix5.mat[3][2];
				simplevector1.x = simplevector.x * f13 + simplevector.y * f16 + simplevector.z * f23 + f38;
				simplevector1.y = simplevector.x * f26 + simplevector.y * f18 + simplevector.z * f20 + f41;
				simplevector1.z = simplevector.x * f35 + simplevector.y * f32 + simplevector.z * f29 + f44;
				float f47 = simplevector2.x * f13 + simplevector2.y * f16 + simplevector2.z * f23;
				float f49 = simplevector2.x * f26 + simplevector2.y * f18 + simplevector2.z * f20;
				float f51 = simplevector2.x * f35 + simplevector2.y * f32 + simplevector2.z * f29;
				simplevector2.x = f47;
				simplevector2.y = f49;
				simplevector2.z = f51;
				if (flag) {
					simplevector6 = new SimpleVector(simplevector1);
					simplevector7 = new SimpleVector(simplevector2);
				} else {
					simplevector6 = simplevector1;
					simplevector7 = simplevector2;
				}
			}
			if (flag) {
				Matrix matrix6 = matrix1;
				simplevector2 = new SimpleVector(collisioninfo.r3Velocity);
				float f14 = matrix6.mat[0][0];
				float f17 = matrix6.mat[1][0];
				float f19 = matrix6.mat[1][1];
				float f21 = matrix6.mat[2][1];
				float f24 = matrix6.mat[2][0];
				float f27 = matrix6.mat[0][1];
				float f30 = matrix6.mat[2][2];
				float f33 = matrix6.mat[1][2];
				float f36 = matrix6.mat[0][2];
				float f39 = matrix6.mat[3][0];
				float f42 = matrix6.mat[3][1];
				float f45 = matrix6.mat[3][2];
				simplevector1.x = simplevector.x * f14 + simplevector.y * f17 + simplevector.z * f24 + f39;
				simplevector1.y = simplevector.x * f27 + simplevector.y * f19 + simplevector.z * f21 + f42;
				simplevector1.z = simplevector.x * f36 + simplevector.y * f33 + simplevector.z * f30 + f45;
				simplevector2.matMul(matrix2);
			}
			if (!isMainWorld && Config.doPortalHsr && dynSectorDetect && !hasPortals
					&& objMesh.obbEnd - objMesh.obbStart == 7) {
				float af[] = new float[8];
				float af1[] = new float[8];
				float af2[] = new float[8];
				float f22 = matrix3.mat[0][0];
				float f25 = matrix3.mat[1][0];
				float f28 = matrix3.mat[1][1];
				float f31 = matrix3.mat[2][1];
				float f34 = matrix3.mat[2][0];
				float f37 = matrix3.mat[0][1];
				float f40 = matrix3.mat[2][2];
				float f43 = matrix3.mat[1][2];
				float f46 = matrix3.mat[0][2];
				float f48 = matrix3.mat[3][0];
				float f50 = matrix3.mat[3][1];
				float f52 = matrix3.mat[3][2];
				for (int k2 = 0; k2 < 8; k2++) {
					int i3 = k2 + objMesh.obbStart;
					float f53 = objMesh.zOrg[i3];
					float f54 = objMesh.xOrg[i3];
					float f55 = objMesh.yOrg[i3];
					af[k2] = f54 * f22 + f55 * f25 + f53 * f34 + f48;
					af1[k2] = f54 * f37 + f55 * f28 + f53 * f31 + f50;
					af2[k2] = f54 * f46 + f55 * f43 + f53 * f40 + f52;
				}

				ai = new int[Config.maxPortals];
				ai = myWorld.portals.detectAllCoveredSectors(ai, af, af1, af2);
				i = ai[0];
			}
		} else {
			simplevector1.x = simplevector.x;
			simplevector1.y = simplevector.y;
			simplevector1.z = simplevector.z;
			simplevector6 = simplevector1;
			simplevector7 = simplevector2;
		}
		int j = 0;
		Object aobj[] = null;
		if (flag1) {
			float f15 = (float) Math.sqrt(simplevector7.x * simplevector7.x + simplevector7.y * simplevector7.y
					+ simplevector7.z * simplevector7.z);
			aobj = ocTree.getColliderLeafs(simplevector6.x, simplevector6.y, simplevector6.z,
					(collisioninfo.getMaxRadius() + f15) * ocTree.getRadiusMultiplier());
			j = ((Integer) aobj[0]).intValue();
			if (j == 0)
				return;
		}
		boolean flag2 = false;
		boolean flag3 = false;
		int ai1[] = new int[Math.max(Config.maxPortals, 1)];
		int i1 = 0;
		if (myWorld != null && Config.doPortalHsr) {
			for (int j1 = 1; j1 <= myWorld.portals.anzSectors; j1++)
				if (f == 1E+012F || simplevector.x >= myWorld.portals.bounding[j1][0] - f
						&& simplevector.x <= myWorld.portals.bounding[j1][1] + f
						&& simplevector.y >= myWorld.portals.bounding[j1][2] - f
						&& simplevector.y <= myWorld.portals.bounding[j1][3] + f
						&& simplevector.z >= myWorld.portals.bounding[j1][4] - f
						&& simplevector.z <= myWorld.portals.bounding[j1][5] + f) {
					ai1[i1] = j1;
					i1++;
				}

		}
		if (i1 == 0) {
			i1 = 1;
			ai1[0] = 0;
		}
		for (int k1 = 0; k1 < i1; k1++) {
			int k = 0;
			int l = 0;
			if (isMainWorld || !oneSectorOnly) {
				k = sectorStartPoly[ai1[k1]];
				l = sectorEndPoly[ai1[k1]] + 1;
				if (k + 1 == l) {
					l = 0;
					k = 0;
				}
			} else if (!dynSectorDetect && ai1[k1] == singleSectorNumber || singleSectorNumber == 0) {
				k = 0;
				l = objMesh.anzTri;
				k1 = i1;
			} else if (dynSectorDetect)
				if (i == 1) {
					if (ai[1] == ai1[k1]) {
						k = 0;
						l = objMesh.anzTri;
						k1 = i1;
					}
				} else {
					int l1 = 1;
					do {
						if (l1 > i)
							break;
						if (ai[l1] == ai1[k1]) {
							k = 0;
							l = objMesh.anzTri;
							k1 = i1;
							break;
						}
						l1++;
					} while (true);
				}
			OcTreeNode aoctreenode[] = null;
			int ai2[] = null;
			int i2 = 0;
			if (flag1)
				aoctreenode = (OcTreeNode[]) aobj[1];
			simplevector4.x = simplevector1.x * collisioninfo.invERadius.x;
			simplevector4.y = simplevector1.y * collisioninfo.invERadius.y;
			simplevector4.z = simplevector1.z * collisioninfo.invERadius.z;
			simplevector3.x = simplevector2.x * collisioninfo.invERadius.x;
			simplevector3.y = simplevector2.y * collisioninfo.invERadius.y;
			simplevector3.z = simplevector2.z * collisioninfo.invERadius.z;
			SimpleVector simplevector5 = simplevector3.normalize();
			SimpleVector simplevector8 = new SimpleVector();
			SimpleVector simplevector9 = new SimpleVector();
			SimpleVector simplevector10 = new SimpleVector();
			SimpleVector simplevector11 = new SimpleVector();
			Plane plane = new Plane();
			float af3[] = new float[1];
			do {
				if (flag1) {
					ai2 = aoctreenode[i2].getPolygons();
					k = 0;
					l = aoctreenode[i2].getPolyCount();
					i2++;
				}
				for (int j2 = k; j2 < l; j2++) {
					int l2 = j2;
					if (flag1)
						l2 = ai2[j2];
					int j3 = objMesh.coords[objMesh.points[l2][0]];
					int k3 = objMesh.coords[objMesh.points[l2][1]];
					int l3 = objMesh.coords[objMesh.points[l2][2]];
					if (flag) {
						float f56 = objMesh.xOrg[j3];
						float f57 = objMesh.yOrg[j3];
						float f59 = objMesh.zOrg[j3];
						float f63 = f56 * f1 + f57 * f2 + f59 * f5 + f10;
						float f64 = f56 * f6 + f57 * f3 + f59 * f4 + f11;
						float f66 = f56 * f9 + f57 * f8 + f59 * f7 + f12;
						simplevector8.x = f63 * collisioninfo.invERadius.x;
						simplevector8.y = f64 * collisioninfo.invERadius.y;
						simplevector8.z = f66 * collisioninfo.invERadius.z;
						f56 = objMesh.xOrg[k3];
						f57 = objMesh.yOrg[k3];
						f59 = objMesh.zOrg[k3];
						f63 = f56 * f1 + f57 * f2 + f59 * f5 + f10;
						f64 = f56 * f6 + f57 * f3 + f59 * f4 + f11;
						f66 = f56 * f9 + f57 * f8 + f59 * f7 + f12;
						simplevector9.x = f63 * collisioninfo.invERadius.x;
						simplevector9.y = f64 * collisioninfo.invERadius.y;
						simplevector9.z = f66 * collisioninfo.invERadius.z;
						f56 = objMesh.xOrg[l3];
						f57 = objMesh.yOrg[l3];
						f59 = objMesh.zOrg[l3];
						f63 = f56 * f1 + f57 * f2 + f59 * f5 + f10;
						f64 = f56 * f6 + f57 * f3 + f59 * f4 + f11;
						f66 = f56 * f9 + f57 * f8 + f59 * f7 + f12;
						simplevector10.x = f63 * collisioninfo.invERadius.x;
						simplevector10.y = f64 * collisioninfo.invERadius.y;
						simplevector10.z = f66 * collisioninfo.invERadius.z;
					} else {
						simplevector8.x = objMesh.xOrg[j3] * collisioninfo.invERadius.x;
						simplevector8.y = objMesh.yOrg[j3] * collisioninfo.invERadius.y;
						simplevector8.z = objMesh.zOrg[j3] * collisioninfo.invERadius.z;
						simplevector9.x = objMesh.xOrg[k3] * collisioninfo.invERadius.x;
						simplevector9.y = objMesh.yOrg[k3] * collisioninfo.invERadius.y;
						simplevector9.z = objMesh.zOrg[k3] * collisioninfo.invERadius.z;
						simplevector10.x = objMesh.xOrg[l3] * collisioninfo.invERadius.x;
						simplevector10.y = objMesh.yOrg[l3] * collisioninfo.invERadius.y;
						simplevector10.z = objMesh.zOrg[l3] * collisioninfo.invERadius.z;
					}
					plane.setTo(simplevector8, simplevector9, simplevector10);
					boolean flag4 = true;
					if (!plane.isFrontFacingTo(simplevector5))
						continue;
					float f58 = 0.0F;
					float f60 = 0.0F;
					boolean flag5 = false;
					float f65 = plane.distanceTo(simplevector4);
					SimpleVector simplevector12 = plane.normal;
					float f67 = simplevector12.x * simplevector3.x + simplevector12.y * simplevector3.y + simplevector12.z
							* simplevector3.z;
					if (f67 == 0.0F) {
						if (Math.abs(f65) >= 1.0F) {
							flag4 = false;
						} else {
							flag5 = true;
							f58 = 0.0F;
							float f61 = 1.0F;
						}
					} else {
						f58 = (-1F - f65) / f67;
						float f62 = (1.0F - f65) / f67;
						if (f58 > f62) {
							float f68 = f58;
							f58 = f62;
							f62 = f68;
						}
						if (f58 > 1.0F || f62 < 0.0F)
							flag4 = false;
						if (f58 < -1F)
							f58 = 0.0F;
						if (f62 < 0.0F)
							f62 = 0.0F;
						if (f58 > 1.0F)
							f58 = 1.0F;
						if (f62 > 1.0F)
							f62 = 1.0F;
					}
					if (!flag4)
						continue;
					SimpleVector simplevector13 = null;
					boolean flag6 = false;
					float f69 = 1.0F;
					if (!flag5) {
						SimpleVector simplevector14 = new SimpleVector(simplevector3);
						simplevector14.scalarMul(f58);
						SimpleVector simplevector16 = simplevector4.calcSub(plane.normal);
						simplevector16.add(simplevector14);
						SimpleVector simplevector17 = simplevector16.calcSub(simplevector4);
						if (checkPointInTriangle2(simplevector17, simplevector4, new SimpleVector[] { simplevector8, simplevector9,
								simplevector10 })) {
							flag6 = true;
							f69 = f58;
							simplevector13 = simplevector16;
						}
					}
					if (!flag6) {
						SimpleVector simplevector15 = simplevector4;
						float f71 = simplevector3.x * simplevector3.x + simplevector3.y * simplevector3.y + simplevector3.z
								* simplevector3.z;
						float f72 = 0.0F;
						float f75 = 0.0F;
						float f78 = 0.0F;
						float f81 = 0.0F;
						float f82 = 0.0F;
						float f83 = 0.0F;
						SimpleVector simplevector18 = null;
						for (int i4 = 0; i4 < 3; i4++) {
							switch (i4) {
							case 0: // '\0'
								f81 = simplevector8.x;
								f82 = simplevector8.y;
								f83 = simplevector8.z;
								simplevector18 = simplevector8;
								break;

							case 1: // '\001'
								f81 = simplevector9.x;
								f82 = simplevector9.y;
								f83 = simplevector9.z;
								simplevector18 = simplevector9;
								break;

							case 2: // '\002'
								f81 = simplevector10.x;
								f82 = simplevector10.y;
								f83 = simplevector10.z;
								simplevector18 = simplevector10;
								break;
							}
							simplevector11.x = simplevector15.x - f81;
							simplevector11.y = simplevector15.y - f82;
							simplevector11.z = simplevector15.z - f83;
							float f73 = f71;
							float f76 = 2.0F * (simplevector3.x * simplevector11.x + simplevector3.y * simplevector11.y + simplevector3.z
									* simplevector11.z);
							simplevector11.x = f81 - simplevector15.x;
							simplevector11.y = f82 - simplevector15.y;
							simplevector11.z = f83 - simplevector15.z;
							float f79 = (simplevector11.x * simplevector11.x + simplevector11.y * simplevector11.y + simplevector11.z
									* simplevector11.z) - 1.0F;
							if (getLowestRoot(f73, f76, f79, f69, af3)) {
								f69 = af3[0];
								flag6 = true;
								simplevector13 = simplevector18;
							}
						}

						for (int j4 = 0; j4 < 3; j4++) {
							switch (j4) {
							case 0: // '\0'
								simplevector11.x = simplevector9.x - simplevector8.x;
								simplevector11.y = simplevector9.y - simplevector8.y;
								simplevector11.z = simplevector9.z - simplevector8.z;
								simplevector18 = simplevector8;
								break;

							case 1: // '\001'
								simplevector11.x = simplevector10.x - simplevector9.x;
								simplevector11.y = simplevector10.y - simplevector9.y;
								simplevector11.z = simplevector10.z - simplevector9.z;
								simplevector18 = simplevector9;
								break;

							case 2: // '\002'
								simplevector11.x = simplevector8.x - simplevector10.x;
								simplevector11.y = simplevector8.y - simplevector10.y;
								simplevector11.z = simplevector8.z - simplevector10.z;
								simplevector18 = simplevector10;
								break;
							}
							SimpleVector simplevector19 = simplevector11;
							SimpleVector simplevector20 = simplevector18.calcSub(simplevector15);
							float f84 = simplevector19.x * simplevector19.x + simplevector19.y * simplevector19.y + simplevector19.z
									* simplevector19.z;
							float f85 = simplevector19.x * simplevector3.x + simplevector19.y * simplevector3.y + simplevector19.z
									* simplevector3.z;
							float f86 = simplevector19.x * simplevector20.x + simplevector19.y * simplevector20.y + simplevector19.z
									* simplevector20.z;
							float f74 = f84 * -f71 + f85 * f85;
							float f77 = f84
									* (2.0F * (simplevector3.x * simplevector20.x + simplevector3.y * simplevector20.y + simplevector3.z
											* simplevector20.z)) - 2.0F * f85 * f86;
							float f80 = f84
									* (1.0F - (simplevector20.x * simplevector20.x + simplevector20.y * simplevector20.y + simplevector20.z
											* simplevector20.z)) + f86 * f86;
							if (!getLowestRoot(f74, f77, f80, f69, af3))
								continue;
							float f87 = (f85 * af3[0] - f86) / f84;
							if (f87 >= 0.0F && f87 <= 1.0F) {
								f69 = af3[0];
								flag6 = true;
								simplevector13 = new SimpleVector(simplevector19);
								simplevector13.scalarMul(f87);
								simplevector13.add(simplevector18);
							}
						}

					}
					if (!flag6)
						continue;
					addPolygonID(l2);
					float f70 = f69;
					if (!collisioninfo.foundCollision || f70 <= collisioninfo.nearestDistance) {
						collisioninfo.nearestDistance = f70;
						collisioninfo.intersectionPoint = simplevector13;
						collisioninfo.foundCollision = true;
						collisioninfo.collision = true;
						collisioninfo.eSpaceBasePoint = simplevector4;
						collisioninfo.eSpaceVelocity = simplevector3;
						collisioninfo.collisionObject = this;
						collisioninfo.isPartOfCollision = true;
					}
				}

			} while (flag1 && i2 < j);
		}

	}

	private final boolean checkPointInTriangle2(SimpleVector simplevector, SimpleVector simplevector1,
			SimpleVector asimplevector[]) {
		float af[] = new float[3];
		float af1[] = new float[3];
		float af2[] = new float[3];
		float af3[] = new float[3];
		float f = asimplevector[0].x;
		float f1 = asimplevector[0].y;
		float f2 = asimplevector[0].z;
		af[0] = asimplevector[1].x - f;
		af[1] = asimplevector[1].y - f1;
		af[2] = asimplevector[1].z - f2;
		af1[0] = asimplevector[2].x - f;
		af1[1] = asimplevector[2].y - f1;
		af1[2] = asimplevector[2].z - f2;
		af3[0] = simplevector.x;
		af3[1] = simplevector.y;
		af3[2] = simplevector.z;
		Vectors.calcCross(af2, af3, af1);
		float f3 = Vectors.calcDot(af, af2);
		if (f3 >= 1E-017F) {
			float f4 = 1.0F / f3;
			af1[0] = simplevector1.x - f;
			af1[1] = simplevector1.y - f1;
			af1[2] = simplevector1.z - f2;
			float f5 = Vectors.calcDot(af1, af2) * f4;
			if ((double) f5 >= 0.0D && f5 <= 1.0F) {
				Vectors.calcCross(af2, af1, af);
				float f6 = Vectors.calcDot(af3, af2) * f4;
				if ((double) f6 >= 0.0D && (double) (f5 + f6) <= 1.0D)
					return true;
			}
		}
		return false;
	}

	private final boolean getLowestRoot(float f, float f1, float f2, float f3, float af[]) {
		float f4 = f1 * f1 - 4F * f * f2;
		if (f4 < 0.0F)
			return false;
		float f5 = (float) Math.sqrt(f4);
		float f6 = (-f1 - f5) / (2.0F * f);
		float f7 = (-f1 + f5) / (2.0F * f);
		if (f6 > f7) {
			float f8 = f7;
			f7 = f6;
			f6 = f8;
		}
		if (f6 > 0.0F && f6 < f3) {
			af[0] = f6;
			return true;
		}
		if (f7 > 0.0F && f7 < f3) {
			af[0] = f7;
			return true;
		} else {
			return false;
		}
	}

	final SimpleVector reverseTransform(SimpleVector simplevector, boolean flag) {
		Matrix matrix = getWorldTransformation();
		if (!isMainWorld) {
			float f = matrix.mat[0][0];
			float f1 = matrix.mat[1][0];
			float f2 = matrix.mat[1][1];
			float f3 = matrix.mat[2][1];
			float f4 = matrix.mat[2][0];
			float f5 = matrix.mat[0][1];
			float f6 = matrix.mat[2][2];
			float f7 = matrix.mat[1][2];
			float f8 = matrix.mat[0][2];
			float f9 = matrix.mat[3][0];
			float f10 = matrix.mat[3][1];
			float f11 = matrix.mat[3][2];
			float f12 = simplevector.x * f + simplevector.y * f1 + simplevector.z * f4;
			float f13 = simplevector.x * f5 + simplevector.y * f2 + simplevector.z * f3;
			float f14 = simplevector.x * f8 + simplevector.y * f7 + simplevector.z * f6;
			if (flag) {
				f12 += f9;
				f13 += f10;
				f14 += f11;
			}
			return new SimpleVector(f12, f13, f14);
		} else {
			return new SimpleVector(simplevector);
		}
	}

	public int addTriangle(SimpleVector simplevector, SimpleVector simplevector1, SimpleVector simplevector2) {
		return addTriangle(simplevector.x, simplevector.y, simplevector.z, 0.0F, 0.0F, simplevector1.x, simplevector1.y,
				simplevector1.z, 0.0F, 0.0F, simplevector2.x, simplevector2.y, simplevector2.z, 0.0F, 0.0F, -1, 0, false);
	}

	final int addTriangle(float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
		return addTriangle(f, f1, f2, 0.0F, 0.0F, f3, f4, f5, 0.0F, 0.0F, f6, f7, f8, 0.0F, 0.0F, -1, 0, false);
	}

	public int addTriangle(SimpleVector simplevector, float f, float f1, SimpleVector simplevector1, float f2, float f3,
			SimpleVector simplevector2, float f4, float f5) {
		return addTriangle(simplevector.x, simplevector.y, simplevector.z, f, f1, simplevector1.x, simplevector1.y,
				simplevector1.z, f2, f3, simplevector2.x, simplevector2.y, simplevector2.z, f4, f5, -1, 0, false);
	}

	final int addTriangle(float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8,
			float f9, float f10, float f11, float f12, float f13, float f14) {
		return addTriangle(f, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, -1, 0, false);
	}

	public int addTriangle(SimpleVector simplevector, float f, float f1, SimpleVector simplevector1, float f2, float f3,
			SimpleVector simplevector2, float f4, float f5, int i) {
		return addTriangle(simplevector.x, simplevector.y, simplevector.z, f, f1, simplevector1.x, simplevector1.y,
				simplevector1.z, f2, f3, simplevector2.x, simplevector2.y, simplevector2.z, f4, f5, i, 0, false);
	}

	public int addTriangle(SimpleVector simplevector, float f, float f1, SimpleVector simplevector1, float f2, float f3,
			SimpleVector simplevector2, float f4, float f5, int i, SimpleVector simplevector3) {
		return addTriangle(simplevector.x, simplevector.y, simplevector.z, f, f1, simplevector1.x, simplevector1.y,
				simplevector1.z, f2, f3, simplevector2.x, simplevector2.y, simplevector2.z, f4, f5, i, 0, false, null, null,
				simplevector3.x, simplevector3.y, simplevector3.z);
	}

	final int addTriangle(float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8,
			float f9, float f10, float f11, float f12, float f13, float f14, int i) {
		return addTriangle(f, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, i, 0, false);
	}

	public int addTriangle(SimpleVector simplevector, float f, float f1, SimpleVector simplevector1, float f2, float f3,
			SimpleVector simplevector2, float f4, float f5, int i, int j) {
		return addTriangle(simplevector.x, simplevector.y, simplevector.z, f, f1, simplevector1.x, simplevector1.y,
				simplevector1.z, f2, f3, simplevector2.x, simplevector2.y, simplevector2.z, f4, f5, i, j, true);
	}

	public int addTriangle(SimpleVector simplevector, SimpleVector simplevector1, SimpleVector simplevector2,
			TextureInfo textureinfo, SimpleVector simplevector3) {
		return addTriangle(simplevector.x, simplevector.y, simplevector.z, 0.0F, 0.0F, simplevector1.x, simplevector1.y,
				simplevector1.z, 0.0F, 0.0F, simplevector2.x, simplevector2.y, simplevector2.z, 0.0F, 0.0F, 0, 0, false, null,
				textureinfo, simplevector3.x, simplevector3.y, simplevector3.z);
	}

	public int addTriangle(SimpleVector simplevector, SimpleVector simplevector1, SimpleVector simplevector2,
			TextureInfo textureinfo) {
		return addTriangle(simplevector.x, simplevector.y, simplevector.z, 0.0F, 0.0F, simplevector1.x, simplevector1.y,
				simplevector1.z, 0.0F, 0.0F, simplevector2.x, simplevector2.y, simplevector2.z, 0.0F, 0.0F, 0, 0, false, null,
				textureinfo);
	}

	public int addTriangle(SimpleVector simplevector, SimpleVector simplevector1, SimpleVector simplevector2,
			TextureInfo textureinfo, int i) {
		return addTriangle(simplevector.x, simplevector.y, simplevector.z, 0.0F, 0.0F, simplevector1.x, simplevector1.y,
				simplevector1.z, 0.0F, 0.0F, simplevector2.x, simplevector2.y, simplevector2.z, 0.0F, 0.0F, 0, i, true, null,
				textureinfo);
	}

	final int addTriangle(float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8,
			float f9, float f10, float f11, float f12, float f13, float f14, int i, int j, boolean flag) {
		return addTriangle(f, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, i, j, flag, null);
	}

	final int addTriangle(float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8,
			float f9, float f10, float f11, float f12, float f13, float f14, int i, int j, boolean flag, int ai[]) {
		return addTriangle(f, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, i, j, flag, ai, null);
	}

	final int addTriangle(float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8,
			float f9, float f10, float f11, float f12, float f13, float f14, int i, int j, boolean flag, int ai[],
			TextureInfo textureinfo) {
		return addTriangle(f, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, i, j, flag, ai, textureinfo,
				-1F, -1F, -1F);
	}

	final int addTriangle(float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8,
			float f9, float f10, float f11, float f12, float f13, float f14, int i, int j, boolean flag, int ai[],
			TextureInfo textureinfo, float f15, float f16, float f17) {
		if (f15 != -1F)
			objVectors.createAlpha();
		if (textureinfo != null) {
			if (multiTex == null && textureinfo.stageCnt > 1) {
				multiTex = new int[Config.maxTextureLayers - 1][texture.length];
				multiMode = new int[Config.maxTextureLayers - 1][texture.length];
				for (int k = 0; k < texture.length; k++) {
					for (int l = 0; l < Config.maxTextureLayers - 1; l++)
						multiTex[l][k] = -1;

				}

				objVectors.createMultiCoords();
				usesMultiTexturing = true;
			}
			f3 = textureinfo.u0[0];
			f4 = textureinfo.v0[0];
			f8 = textureinfo.u1[0];
			f9 = textureinfo.v1[0];
			f13 = textureinfo.u2[0];
			f14 = textureinfo.v2[0];
			i = textureinfo.textures[0];
			if (maxStagesUsed < textureinfo.stageCnt)
				maxStagesUsed = textureinfo.stageCnt;
		}
		boolean flag1 = usesMultiTexturing && textureinfo != null;
		TextureManager texturemanager = TextureManager.getInstance();
		if (flag && sector == null)
			sector = new int[texture.length];
		float f18 = 2.0F;
		float f19 = 2.0F;
		if (i != -1) {
			f18 = texturemanager.textures[i].width;
			f19 = texturemanager.textures[i].height;
		} else {
			i = 0;
		}
		float f20 = f3;
		float f21 = f4;
		float f22 = f8;
		float f23 = f9;
		float f24 = f13;
		float f25 = f14;
		f3 *= f18;
		f4 *= f19;
		f8 *= f18;
		f9 *= f19;
		f13 *= f18;
		f14 *= f19;
		if (objMesh.anzVectors + 3 < objMesh.maxVectors) {
			int i1 = objMesh.anzVectors;
			int j1 = objMesh.anzTri;
			int k1 = 0;
			int l1 = 0;
			int i2 = 0;
			k1 = -1;
			if (!neverOptimize)
				k1 = objVectors.checkCoords(f, f1, f2, j);
			else if (ai != null)
				k1 = ai[0];
			if (k1 == -1)
				k1 = objVectors.addVertex(f, f1, f2, j);
			i2 = k1;
			objMesh.coords[i1] = k1;
			objVectors.uOrg[i1] = f3;
			objVectors.vOrg[i1] = f4;
			if (flag1) {
				for (int j2 = 0; j2 < textureinfo.stageCnt - 1; j2++) {
					objVectors.uMul[j2][i1] = textureinfo.u0[j2 + 1];
					objVectors.vMul[j2][i1] = textureinfo.v0[j2 + 1];
				}

			}
			objVectors.buOrg[i1] = f3;
			objVectors.bvOrg[i1] = f4;
			objVectors.nuOrg[i1] = f20;
			objVectors.nvOrg[i1] = f21;
			if (f15 != -1F)
				objVectors.alpha[i1] = f15;
			objMesh.anzVectors++;
			objMesh.points[j1][0] = i1;
			i1 = objMesh.anzVectors;
			k1 = -1;
			if (!neverOptimize)
				k1 = objVectors.checkCoords(f5, f6, f7, j);
			else if (ai != null)
				k1 = ai[1];
			if (k1 == -1)
				k1 = objVectors.addVertex(f5, f6, f7, j);
			l1 = k1;
			objMesh.coords[i1] = k1;
			objVectors.uOrg[i1] = f8;
			objVectors.vOrg[i1] = f9;
			if (flag1) {
				for (int k2 = 0; k2 < textureinfo.stageCnt - 1; k2++) {
					objVectors.uMul[k2][i1] = textureinfo.u1[k2 + 1];
					objVectors.vMul[k2][i1] = textureinfo.v1[k2 + 1];
				}

			}
			objVectors.buOrg[i1] = f8;
			objVectors.bvOrg[i1] = f9;
			objVectors.nuOrg[i1] = f22;
			objVectors.nvOrg[i1] = f23;
			if (f16 != -1F)
				objVectors.alpha[i1] = f16;
			objMesh.anzVectors++;
			objMesh.points[j1][1] = i1;
			i1 = objMesh.anzVectors;
			k1 = -1;
			if (!neverOptimize)
				k1 = objVectors.checkCoords(f10, f11, f12, j);
			else if (ai != null)
				k1 = ai[2];
			if (k1 == -1)
				k1 = objVectors.addVertex(f10, f11, f12, j);
			objMesh.coords[i1] = k1;
			objVectors.uOrg[i1] = f13;
			objVectors.vOrg[i1] = f14;
			if (flag1) {
				for (int l2 = 0; l2 < textureinfo.stageCnt - 1; l2++) {
					objVectors.uMul[l2][i1] = textureinfo.u2[l2 + 1];
					objVectors.vMul[l2][i1] = textureinfo.v2[l2 + 1];
				}

			}
			objVectors.buOrg[i1] = f13;
			objVectors.bvOrg[i1] = f14;
			objVectors.nuOrg[i1] = f24;
			objVectors.nvOrg[i1] = f25;
			if (f16 != -1F)
				objVectors.alpha[i1] = f17;
			objMesh.anzVectors++;
			if (ai != null) {
				ai[0] = i2;
				ai[1] = l1;
				ai[2] = k1;
			}
			if (k1 != i2 && k1 != l1 && l1 != i2) {
				if (k1 < lowestPos)
					lowestPos = k1;
				if (l1 < lowestPos)
					lowestPos = l1;
				if (i2 < lowestPos)
					lowestPos = i2;
				if (k1 > highestPos)
					highestPos = k1;
				if (l1 > highestPos)
					highestPos = l1;
				if (i2 > highestPos)
					highestPos = i2;
				objMesh.points[j1][2] = i1;
				if (sector != null)
					sector[j1] = j;
				texture[j1] = i;
				basemap[j1] = i;
				if (bumpmap != null)
					bumpmap[j1] = i;
				if (flag1) {
					for (int i3 = 0; i3 < textureinfo.stageCnt - 1; i3++) {
						multiTex[i3][j1] = textureinfo.textures[i3 + 1];
						multiMode[i3][j1] = textureinfo.mode[i3 + 1];
					}

				}
				objMesh.anzTri++;
			}
		} else {
			Logger.log("Polygon index out of range - object is too large!", 0);
		}
		return objMesh.anzTri - 1;
	}

	final int addMD2Triangle(int i, float f, float f1, int j, float f2, float f3, int k, float f4, float f5) {
		TextureManager texturemanager = TextureManager.getInstance();
		int l = 0;
		byte byte0 = -1;
		float f6 = 2.0F;
		float f7 = 2.0F;
		if (byte0 != -1) {
			f6 = texturemanager.textures[byte0].width;
			f7 = texturemanager.textures[byte0].height;
		} else {
			byte0 = 0;
		}
		float f8 = f;
		float f9 = f1;
		float f10 = f2;
		float f11 = f3;
		float f12 = f4;
		float f13 = f5;
		f *= f6;
		f1 *= f7;
		f2 *= f6;
		f3 *= f7;
		f4 *= f6;
		f5 *= f7;
		if (objMesh.anzVectors + 3 < objMesh.maxVectors) {
			int i1 = objMesh.anzVectors;
			int j1 = objMesh.anzTri;
			int k1 = 0;
			int l1 = 0;
			int i2 = 0;
			k1 = i;
			i2 = k1;
			objMesh.coords[i1] = k1;
			objVectors.uOrg[i1] = f;
			objVectors.vOrg[i1] = f1;
			objVectors.buOrg[i1] = f;
			objVectors.bvOrg[i1] = f1;
			objVectors.nuOrg[i1] = f8;
			objVectors.nvOrg[i1] = f9;
			objMesh.anzVectors++;
			objMesh.points[j1][0] = i1;
			i1 = objMesh.anzVectors;
			k1 = j;
			l1 = i1;
			objMesh.coords[i1] = k1;
			objVectors.uOrg[i1] = f2;
			objVectors.vOrg[i1] = f3;
			objVectors.buOrg[i1] = f2;
			objVectors.bvOrg[i1] = f3;
			objVectors.nuOrg[i1] = f10;
			objVectors.nvOrg[i1] = f11;
			objMesh.anzVectors++;
			objMesh.points[j1][1] = i1;
			i1 = objMesh.anzVectors;
			k1 = k;
			objMesh.coords[i1] = k1;
			objVectors.uOrg[i1] = f4;
			objVectors.vOrg[i1] = f5;
			objVectors.buOrg[i1] = f4;
			objVectors.bvOrg[i1] = f5;
			objVectors.nuOrg[i1] = f12;
			objVectors.nvOrg[i1] = f13;
			objMesh.anzVectors++;
			if (k1 < lowestPos)
				lowestPos = k1;
			if (l1 < lowestPos)
				lowestPos = l1;
			if (i2 < lowestPos)
				lowestPos = i2;
			if (k1 > highestPos)
				highestPos = k1;
			if (l1 > highestPos)
				highestPos = l1;
			if (i2 > highestPos)
				highestPos = i2;
			objMesh.points[j1][2] = i1;
			if (sector != null)
				sector[j1] = l;
			texture[j1] = byte0;
			basemap[j1] = byte0;
			if (bumpmap != null)
				bumpmap[j1] = byte0;
			objMesh.anzTri++;
		} else {
			Logger.log("Polygon index out of range - object is too large!", 0);
		}
		return objMesh.anzTri - 1;
	}

	private final Matrix recurseObjectsBillboarded(Matrix matrix) {
		Matrix matrix1 = matrix.cloneMatrix();
		matrix = recurseObjects(matrix);
		float f = recurseScaling(getScale());
		Matrix matrix2 = matrix.cloneMatrix().invert3x3();
		matrix2.mat[3][0] = 0.0F;
		matrix2.mat[3][1] = 0.0F;
		matrix2.mat[3][2] = 0.0F;
		matrix2.mat[3][3] = 1.0F;
		Matrix matrix3 = new Matrix();
		matrix3.mat[3][0] = -matrix.mat[3][0];
		matrix3.mat[3][1] = -matrix.mat[3][1];
		matrix3.mat[3][2] = -matrix.mat[3][2];
		matrix.matMul(matrix3);
		matrix.matMul(matrix2);
		matrix1.mat[3][0] = 0.0F;
		matrix1.mat[3][1] = 0.0F;
		matrix1.mat[3][2] = 0.0F;
		matrix1.mat[3][3] = 1.0F;
		matrix.matMul(matrix1);
		matrix3.mat[3][0] = -matrix3.mat[3][0];
		matrix3.mat[3][1] = -matrix3.mat[3][1];
		matrix3.mat[3][2] = -matrix3.mat[3][2];
		matrix.matMul(matrix3);
		matrix.scalarMul(1.0F / (f * getScale()));
		return matrix;
	}

	private final Matrix recurseObjects(Matrix matrix) {
		Matrix matrix1 = getCachedMatrix(1);
		for (int i = 0; i < parentCnt; i++) {
			Object3D object3d = parent[i];
			matrix1.setIdentity();
			float f = object3d.originMatrix.mat[3][0];
			float f1 = object3d.originMatrix.mat[3][1];
			float f2 = object3d.originMatrix.mat[3][2];
			matrix1.mat[3][0] = -object3d.xRotationCenter - f;
			matrix1.mat[3][1] = -object3d.yRotationCenter - f1;
			matrix1.mat[3][2] = -object3d.zRotationCenter - f2;
			matrix.matMul(matrix1);
			matrix.matMul(object3d.rotationMatrix);
			matrix1.mat[3][0] = object3d.xRotationCenter + object3d.translationMatrix.mat[3][0] + f;
			matrix1.mat[3][1] = object3d.yRotationCenter + object3d.translationMatrix.mat[3][1] + f1;
			matrix1.mat[3][2] = object3d.zRotationCenter + object3d.translationMatrix.mat[3][2] + f2;
			matrix.matMul(matrix1);
			if (object3d.parentCnt != 0)
				matrix = object3d.recurseObjects(matrix);
		}

		return matrix;
	}

	private final float recurseScaling(float f) {
		for (int i = 0; i < parentCnt; i++) {
			Object3D object3d = parent[i];
			f *= object3d.getScale();
			if (object3d.parentCnt != 0)
				f = object3d.recurseScaling(f);
		}

		return f;
	}

	final void resetTransformCache() {
		float af[] = myWorld.lightCacheR;
		for (int i = 0; i < objMesh.anzCoords; i++)
			af[i] = 1.234567E+007F;

	}

	final boolean transformVertices(FrameBuffer framebuffer) {
		if (Config.autoBuild && !hasBeenBuild) {
			Logger.log("Auto building object '" + getName() + "'!", 2);
			build();
		}
		float f = 0.0F;
		float f4 = 0.0F;
		float f8 = 0.0F;
		someSectorVisible = true;
		boolean flag = framebuffer.hasRenderTarget && framebuffer.renderTarget.isShadowMap;
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = myWorld.portals.viewSector;
		float f36 = myWorld.camera.divx;
		float f37 = myWorld.camera.divy;
		float f38 = myWorld.camera.scaleX;
		float f39 = myWorld.camera.scaleY;
		float f40 = Config.nearPlane;
		float f41 = 0.0F;
		float f43 = framebuffer.middleX;
		float f44 = framebuffer.middleY;
		if (Config.useLocking)
			myWorld.lockMatrices();
		mat2 = ((BufferedMatrix) (myWorld.camera)).frontMatrix;
		mat3.setIdentity();
		mat3.mat[3][0] = -((BufferedMatrix) (myWorld.camera)).frontBx;
		mat3.mat[3][1] = -((BufferedMatrix) (myWorld.camera)).frontBy;
		mat3.mat[3][2] = -((BufferedMatrix) (myWorld.camera)).frontBz;
		visComplete = false;
		float af[] = objMesh.zOrg;
		float af1[] = objMesh.xOrg;
		float af2[] = objMesh.yOrg;
		float af3[] = objVectors.xTr;
		float af4[] = objVectors.yTr;
		float af5[] = objVectors.zTr;
		float af6[] = objVectors.sz;
		float af7[] = objMesh.nzOrg;
		float af8[] = objMesh.nxOrg;
		float af9[] = objMesh.nyOrg;
		float af10[] = myWorld.nxTr;
		float af11[] = myWorld.nyTr;
		float af12[] = myWorld.nzTr;
		float af13[] = myWorld.nxWs;
		float af14[] = myWorld.nyWs;
		float af15[] = myWorld.nzWs;
		if (lazyTransforms && !isBillBoard) {
			getWorldTransformationInternal(mat5);
		} else {
			mat5.setIdentity();
			mat6.setIdentity();
			mat5.mat[3][0] = -xRotationCenter;
			mat5.mat[3][1] = -yRotationCenter;
			mat5.mat[3][2] = -zRotationCenter;
			mat6.mat[3][0] = xRotationCenter + translationMatrix.mat[3][0] + originMatrix.mat[3][0];
			mat6.mat[3][1] = yRotationCenter + translationMatrix.mat[3][1] + originMatrix.mat[3][1];
			mat6.mat[3][2] = zRotationCenter + translationMatrix.mat[3][2] + originMatrix.mat[3][2];
			if (!isBillBoard) {
				mat5.matMul(rotationMatrix);
			} else {
				Matrix matrix = mat2.invert();
				matrix.scalarMul(scaleFactor);
				mat5.matMul(matrix);
			}
			mat5.matMul(mat6);
			if (parentCnt != 0)
				if (isBillBoard && !Config.oldStyleBillBoarding)
					mat5 = recurseObjectsBillboarded(mat5);
				else
					mat5 = recurseObjects(mat5);
		}
		transBuffer = new Matrix(mat5);
		if (Config.useLocking)
			myWorld.unlockMatrices();
		float f21 = mat5.mat[0][0];
		float f22 = mat5.mat[1][0];
		float f25 = mat5.mat[1][1];
		float f26 = mat5.mat[2][1];
		float f23 = mat5.mat[2][0];
		float f24 = mat5.mat[0][1];
		float f29 = mat5.mat[2][2];
		float f28 = mat5.mat[1][2];
		float f27 = mat5.mat[0][2];
		float f34 = mat5.mat[3][0];
		float f35 = mat5.mat[3][1];
		float f33 = mat5.mat[3][2];
		mat5.matMul(mat3);
		mat5.matMul(mat2);
		float f12 = mat5.mat[0][0];
		float f13 = mat5.mat[1][0];
		float f16 = mat5.mat[1][1];
		float f17 = mat5.mat[2][1];
		float f20 = mat5.mat[2][2];
		float f19 = mat5.mat[1][2];
		float f14 = mat5.mat[2][0];
		float f18 = mat5.mat[0][2];
		float f15 = mat5.mat[0][1];
		float f31 = mat5.mat[3][0];
		float f32 = mat5.mat[3][1];
		float f30 = mat5.mat[3][2];
		boolean flag1 = true;
		boolean flag2 = false;
		boolean flag3 = false;
		int l2 = 0;
		float af16[] = myWorld.lightCacheR;
		boolean flag4 = ocTree != null && !Config.doPortalHsr && ocTree.getRenderingUse();
		if (flag4) {
			l2 = ocTree.getVisibleLeafs(mat5, f36, f37);
			if (l2 == 0)
				return true;
			if (!isCompiled())
				if (l2 == ocTree.getTotalLeafs()) {
					for (int i3 = 0; i3 < objMesh.anzCoords; i3++)
						af16[i3] = -1F;

				} else {
					OcTreeNode aoctreenode[] = ocTree.getLeafList();
					int k3 = ocTree.getLeafCount();
					for (int i4 = 0; i4 < k3; i4++) {
						OcTreeNode octreenode = aoctreenode[i4];
						int l5 = octreenode.getPointCount();
						int ai1[] = octreenode.getPoints();
						for (int k6 = 0; k6 < l5; k6++)
							af16[ai1[k6]] = -1F;

					}

				}
		}
		int j3 = myWorld.portals.anzVisSectors;
		if (oneSectorOnly || hasPortals || !Config.doPortalHsr)
			j3 = 1;
		if (Config.doPortalHsr && dynSectorDetect && !hasPortals && objMesh.obbEnd - objMesh.obbStart == 7) {
			for (int l3 = 0; l3 < 8; l3++) {
				int j4 = l3 + objMesh.obbStart;
				float f5 = af[j4];
				float f1 = af1[j4];
				float f9 = af2[j4];
				xWs[l3] = f1 * f21 + f9 * f22 + f5 * f23 + f34;
				yWs[l3] = f1 * f24 + f9 * f25 + f5 * f26 + f35;
				zWs[l3] = f1 * f27 + f9 * f28 + f5 * f29 + f33;
			}

			dynSectorList = myWorld.portals.detectAllCoveredSectors(dynSectorList, xWs, yWs, zWs);
			sectorCnt = dynSectorList[0];
			if (sectorCnt != 0) {
				boolean flag5 = false;
				for (int k4 = 1; k4 <= sectorCnt; k4++) {
					if (myWorld.portals.viewSector == dynSectorList[k4]) {
						flag5 = true;
						someSectorVisible = true;
						j3 = 1;
						k4 = sectorCnt + 1;
						continue;
					}
					for (int i5 = 0; i5 < myWorld.portals.anzVisSectors; i5++)
						if (myWorld.portals.visSectors[i5] == dynSectorList[k4]) {
							flag5 = true;
							someSectorVisible = true;
							j3 = 1;
							k4 = sectorCnt + 1;
							i5 = myWorld.portals.anzVisSectors;
						}

				}

				if (!flag5) {
					someSectorVisible = false;
					j3 = 0;
				}
			} else {
				someSectorVisible = false;
				j3 = 0;
			}
		}
		boolean flag6 = false;
		for (int l4 = 0; l4 < j3; l4++) {
			int j2;
			int k2;
			if (!oneSectorOnly && !hasPortals && Config.doPortalHsr && !dynSectorDetect) {
				int i2 = myWorld.portals.visSectors[l4];
				k2 = sectorStartPoint[i2];
				j2 = sectorEndPoint[i2] + 1;
				if (k2 + 1 == j2) {
					j2 = 0;
					k2 = 0;
				}
			} else {
				k2 = 0;
				j2 = objMesh.anzCoords;
			}
			if (Config.useBB && hasBoundingBox && Config.useFrustumCulling || flag4 && hasBoundingBox) {
				for (int j5 = objMesh.obbStart; j5 <= objMesh.obbEnd; j5++) {
					float f6 = af[j5];
					float f2 = af1[j5];
					float f10 = af2[j5];
					float f45 = f2 * f12 + f10 * f13 + f6 * f14 + f31;
					float f46 = f2 * f15 + f10 * f16 + f6 * f17 + f32;
					float f48 = f2 * f18 + f10 * f19 + f6 * f20 + f30;
					af3[j5] = f45;
					af4[j5] = f46;
					af5[j5] = f48;
					if (!Config.useFrustumCulling)
						continue;
					if (dynSectorDetect) {
						if (sectorCnt == 1 && dynSectorList[1] == l1)
							k1++;
					} else if (!oneSectorOnly) {
						if (objVectors.vertexSector[j5] == l1)
							k1++;
					} else if (singleSectorNumber == l1 || singleSectorNumber == 0)
						k1++;
					if (f48 < f40)
						i1++;
					if (f48 > Config.farPlane)
						j1++;
					float f42 = f48 * f36;
					if (f45 < -f42) {
						j++;
						continue;
					}
					if (f45 > f42) {
						i++;
						continue;
					}
					f42 = f48 * f37;
					if (f46 < -f42) {
						k++;
						continue;
					}
					if (f46 > f42)
						l++;
				}

				int k5 = (objMesh.obbEnd + 1) - objMesh.obbStart;
				if (l == k5 || i == k5 || k == k5 || j == k5 || i1 == k5 || j1 == k5) {
					flag6 = true;
				} else {
					if (Config.doPortalHsr && k1 != k5)
						flag6 = !myWorld.portals.testObbAgainstPortals(this, f43, f44, f38, f39);
					else
						flag6 = false;
					if (!flag6 && l == 0 && i == 0 && k == 0 && j == 0 && i1 == 0 && j1 == 0)
						visComplete = true;
				}
				if (objMesh.obbEnd + 1 == j2)
					j2 = objMesh.obbStart;
			}
			if (flag6)
				continue;
			if (isCompiled()) {
				Object obj = null;
				if (flag4) {
					if (sectors == null)
						sectors = new Hashtable();
					sectors.clear();
					OcTreeNode aoctreenode1[] = ocTree.getLeafList();
					for (int i6 = 0; i6 < l2; i6++) {
						Integer integer = IntegerC.valueOf(aoctreenode1[i6].getID());
						sectors.put(integer, integer);
					}

				}
				SimpleVector simplevector = getTransformedCenter();
				cachedTC = simplevector;
				float f47 = simplevector.x * f18 + simplevector.y * f19 + simplevector.z * f20 + f30;
				for (int l6 = 0; l6 < compiled.size(); l6++) {
					ICompiledInstance icompiledinstance = (ICompiledInstance) compiled.elementAt(l6);
					boolean flag9 = true;
					if (flag4 && icompiledinstance.getTreeID() != -1)
						flag9 = sectors.containsKey(IntegerC.valueOf(icompiledinstance.getTreeID()));
					if (flag9) {
						int j7 = icompiledinstance.getPolyIndex();
						myWorld.visList.addToList(this, this, j7, j7, f47, l6, icompiledinstance.getStageCount() - 1, false);
					}
				}

				return false;
			}
			OcTreeNode aoctreenode2[] = null;
			int ai[] = null;
			int j6 = 0;
			boolean flag7 = false;
			if (flag4) {
				aoctreenode2 = ocTree.getLeafList();
				flag7 = true;
			}
			boolean flag8 = isEnvmapped && !useCSEnvmapping;
			if (flag8) {
				myWorld.createWSNormals();
				af13 = myWorld.nxWs;
				af14 = myWorld.nyWs;
				af15 = myWorld.nzWs;
			}
			do {
				if (flag7) {
					ai = aoctreenode2[j6].getPoints();
					k2 = 0;
					j2 = aoctreenode2[j6].getPointCount();
					j6++;
				}
				for (int i7 = k2; i7 < j2; i7++) {
					int k7 = i7;
					if (flag7)
						k7 = ai[i7];
					if (flag7 && af16[k7] != -1F)
						continue;
					float f7 = af[k7];
					float f3 = af1[k7];
					float f11 = af2[k7];
					af3[k7] = f3 * f12 + f11 * f13 + f7 * f14 + f31;
					af4[k7] = f3 * f15 + f11 * f16 + f7 * f17 + f32;
					if (flag)
						af5[k7] = f3 * f18 + f11 * f19 + f7 * f20 + f30 + Config.glShadowZBias;
					else
						af5[k7] = f3 * f18 + f11 * f19 + f7 * f20 + f30;
					af6[k7] = -1.01F;
					f7 = af7[k7];
					f3 = af8[k7];
					f11 = af9[k7];
					af10[k7] = f3 * f12 + f11 * f13 + f7 * f14;
					af11[k7] = f3 * f15 + f11 * f16 + f7 * f17;
					af12[k7] = f3 * f18 + f11 * f19 + f7 * f20;
					if (flag8) {
						af13[k7] = f3 * f21 + f11 * f22 + f7 * f23;
						af14[k7] = f3 * f24 + f11 * f25 + f7 * f26;
						af15[k7] = f3 * f27 + f11 * f28 + f7 * f29;
					}
					if (reNormalize) {
						float f49 = (float) Math.sqrt(af10[k7] * af10[k7] + af11[k7] * af11[k7] + af12[k7] * af12[k7]);
						if (f49 != 0.0F) {
							af10[k7] /= f49;
							af11[k7] /= f49;
							af12[k7] /= f49;
						}
						if (flag8) {
							float f50 = (float) Math.sqrt(af13[k7] * af13[k7] + af14[k7] * af14[k7] + af15[k7] * af15[k7]);
							if (f50 != 0.0F) {
								af13[k7] /= f50;
								af14[k7] /= f50;
								af15[k7] /= f50;
							}
						}
					}
					af16[k7] = 1.234567E+007F;
				}

			} while (flag7 && j6 < l2);
		}

		return flag6;
	}

	final void addClippedPoly(int i, float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7,
			float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15, float f16, float f17,
			float f18, float f19, float f20, float f21, float f22, float f23, float f24, float f25, float f26, float f27,
			float f28, float f29) {
		Object3D object3d = myWorld.clippedPolys;
		if (object3d.objMesh.anzVectors + 2 + 8 >= object3d.objMesh.maxVectors)
			myWorld.rescaleClippedPolys();
		Vectors vectors = object3d.objVectors;
		Mesh mesh = object3d.objMesh;
		int j = mesh.anzVectors;
		int k = mesh.anzCoords;
		object3d.texture[mesh.anzTri] = texture[i];
		object3d.basemap[mesh.anzTri] = basemap[i];
		if (bumpmap != null)
			object3d.bumpmap[mesh.anzTri] = bumpmap[i];
		vectors.sx[k] = f;
		vectors.sy[k] = f1;
		vectors.sz[k] = f2;
		mesh.coords[j] = k;
		vectors.su[k] = f3;
		vectors.sv[k] = f4;
		vectors.sb[k] = f7;
		vectors.sr[k] = f5;
		vectors.sg[k] = f6;
		if (vectors.bsu != null) {
			vectors.bsu[k] = f24;
			vectors.bsv[k] = f25;
		}
		mesh.points[mesh.anzTri][0] = k;
		k++;
		j++;
		vectors.sx[k] = f8;
		vectors.sy[k] = f9;
		vectors.sz[k] = f10;
		mesh.coords[j] = k;
		vectors.su[k] = f11;
		vectors.sv[k] = f12;
		vectors.sb[k] = f15;
		vectors.sr[k] = f13;
		vectors.sg[k] = f14;
		if (vectors.bsu != null) {
			vectors.bsu[k] = f26;
			vectors.bsv[k] = f27;
		}
		mesh.points[mesh.anzTri][1] = k;
		k++;
		j++;
		vectors.sx[k] = f16;
		vectors.sy[k] = f17;
		vectors.sz[k] = f18;
		mesh.coords[j] = k;
		vectors.su[k] = f19;
		vectors.sv[k] = f20;
		vectors.sb[k] = f23;
		vectors.sr[k] = f21;
		vectors.sg[k] = f22;
		if (vectors.bsu != null) {
			vectors.bsu[k] = f28;
			vectors.bsv[k] = f29;
		}
		mesh.points[mesh.anzTri][2] = k;
		k++;
		j++;
		mesh.anzCoords = k;
		mesh.anzVectors = j;
		mesh.anzTri++;
	}

	final void addCompiled(ICompiledInstance icompiledinstance) {
		synchronized (compiled) {
			compiled.addElement(icompiledinstance);
		}
	}

	final void render(boolean flag, float f, float f1, float f2, float f3, float f4, float f5, boolean flag1) {
		Lights lights;
		if (!isLit)
			lights = DUMMY_LIGHTS;
		else
			lights = myWorld.lights;
		int i = lights.lightCnt;
		VisList vislist = myWorld.visList;
		if (isCompiled()) {
			if (nearestLights == null)
				nearestLights = new float[8][7];
			for (int j = 0; j < 8; j++)
				nearestLights[j][0] = -9999F;

			if (isLit) {
				SimpleVector simplevector = cachedTC;
				if (simplevector == null)
					simplevector = getTransformedCenter();
				cachedTC = null;
				Vector vector = new Vector(i);
				for (int l = 0; l < i; l++) {
					if (!lights.isVisible[l])
						continue;
					float f7 = simplevector.calcSub(new SimpleVector(lights.xOrg[l], lights.yOrg[l], lights.zOrg[l])).length();
					if (f7 > lights.discardDistance[l]
							&& (lights.discardDistance[l] >= 0.0F || Config.lightDiscardDistance >= 0.0F
									&& Config.lightDiscardDistance <= f7))
						continue;
					boolean flag2 = false;
					if (i > 8) {
						int l1 = 0;
						do {
							if (l1 >= vector.size())
								break;
							float af[] = (float[]) vector.get(l1);
							if (f7 < af[0]) {
								vector.insertElementAt(new float[] { f7, (float) l }, l1);
								flag2 = true;
								break;
							}
							l1++;
						} while (true);
					}
					if (!flag2)
						vector.addElement(new float[] { f7, (float) l });
				}

				for (int i1 = 0; i1 < 8 && i1 < vector.size(); i1++) {
					int j1 = (int) ((float[]) vector.elementAt(i1))[1];
					float f9 = lights.getAttenuation(j1);
					if (f9 != -1F && f9 < 0.0F && Config.fadeoutLight)
						f9 = Config.linearDiv;
					nearestLights[i1][0] = f9;
					nearestLights[i1][1] = lights.xTr[j1];
					nearestLights[i1][2] = -lights.yTr[j1];
					nearestLights[i1][3] = -lights.zTr[j1];
					nearestLights[i1][4] = lights.rOrg[j1] / 255F;
					nearestLights[i1][5] = lights.gOrg[j1] / 255F;
					nearestLights[i1][6] = lights.bOrg[j1] / 255F;
				}

			}
			if (dynamic && modified)
				vislist.addToFill(this);
			return;
		}
		if (!flag1)
			objVectors.createScreenColors();
		TextureManager texturemanager = TextureManager.getInstance();
		if (isBumpmapped && bumpmap != null && !texturemanager.textures[bumpmap[0]].isBumpmap)
			texturemanager.textures[bumpmap[0]].createBumpmap();
		int k = myWorld.lights.maxLightValue;
		float f6 = Config.farPlane;
		float f8 = Config.nearPlane;
		if (f8 < 1.0F)
			f8 = 1.0F;
		int k1 = myWorld.portals.viewSector;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = -1;
		int j3 = 0;
		int k3 = 0;
		int l3 = ((int) f << 1) - 1;
		int i4 = ((int) f1 << 1) - 1;
		float f10 = f + f * 2.0F * Config.viewportOffsetX;
		float f11 = f1 + f1 * 2.0F * Config.viewportOffsetY;
		float f12 = 0.0F;
		float f13 = 0.0F;
		float f14 = 0.0F;
		float f16 = 0.0F;
		float f18 = 0.0F;
		float f20 = 0.0F;
		float f22 = 0.0F;
		float f24 = 0.0F;
		float f26 = 0.0F;
		float f28 = 0.0F;
		float f30 = 0.0F;
		float f44 = 0.0F;
		float f45 = 0.0F;
		float f46 = 0.0F;
		float f47 = 0.0F;
		float f48 = 0.0F;
		float f50 = 0.0F;
		float f52 = 0.0F;
		float f54 = 0.0F;
		float f56 = 0.0F;
		float f73 = 1.0F / Config.linearDiv;
		float f74 = Config.lightDiscardDistance;
		float f75 = f74 * f74;
		boolean flag3 = false;
		float f76 = 0.0001F;
		if (!flag3) {
			object3DRendered = true;
			float f78 = myWorld.ambientRed + addColorR;
			float f79 = myWorld.ambientGreen + addColorG;
			float f80 = myWorld.ambientBlue + addColorB;
			boolean flag6 = !Config.doPortalHsr && ocTree != null && ocTree.getRenderingUse();
			int j4 = 0;
			int k4 = 0;
			int l4 = 0;
			int ai[] = null;
			if (flag6 && ocTree.getLeafCount() == 0)
				return;
			int ai1[][] = objMesh.points;
			int ai2[] = objMesh.coords;
			float af1[] = objVectors.xTr;
			float af2[] = objVectors.yTr;
			float af3[] = objVectors.zTr;
			float af4[] = objVectors.sz;
			float af5[] = objVectors.sx;
			float af6[] = objVectors.sy;
			float af7[] = objVectors.su;
			float af8[] = objVectors.sv;
			float af9[] = objVectors.bsu;
			float af10[] = objVectors.bsv;
			if (af9 == null) {
				af9 = objVectors.su;
				af10 = objVectors.sv;
			}
			float af11[] = objVectors.uOrg;
			float af12[] = objVectors.vOrg;
			float af13[] = objVectors.sg;
			float af14[] = objVectors.sr;
			float af15[] = objVectors.sb;
			float af16[] = objVectors.sgOrg;
			float af17[] = objVectors.srOrg;
			float af18[] = objVectors.sbOrg;
			float af19[] = myWorld.nxTr;
			float af20[] = myWorld.nyTr;
			float af21[] = myWorld.nzTr;
			float af22[] = myWorld.nxWs;
			float af23[] = myWorld.nyWs;
			float af24[] = myWorld.nzWs;
			do {
				boolean flag7 = visComplete;
				if (flag6) {
					OcTreeNode aoctreenode[] = ocTree.getLeafList();
					if (ocTree.isCompletelyVisible(j4))
						flag7 = true;
					ai = aoctreenode[j4].getPolygons();
					l4 = aoctreenode[j4].getPolyCount();
					k4 = 0;
					j4++;
				}
				do {
					if (flag6) {
						j3 = ai[k4];
						k4++;
					}
					boolean flag8 = true;
					if (!oneSectorOnly && sector != null && i3 != sector[j3] && Config.doPortalHsr) {
						flag8 = false;
						int i5 = 1;
						boolean flag9 = false;
						do {
							if (j3 >= objMesh.anzTri || flag8)
								break;
							if (!flag9)
								flag8 = myWorld.portals.isSectorVisible(this, sector[j3]);
							flag9 = false;
							if (!flag8) {
								int l5 = j3;
								int j6 = sectorEndPoly[sector[j3] + i5];
								j3 = sectorStartPoly[sector[j3] + i5];
								if (j3 == j6) {
									j3 = l5;
									i5++;
									flag9 = true;
									if (sector[j3] + i5 > myWorld.portals.anzSectors) {
										j3 = objMesh.anzTri;
										flag8 = false;
									}
								}
							}
						} while (true);
					}
					if (j3 >= objMesh.anzTri)
						flag8 = false;
					else if (!oneSectorOnly && sector != null)
						i3 = sector[j3];
					else
						i3 = singleSectorNumber;
					if (flag8) {
						boolean flag4 = false;
						int j5 = ai2[ai1[j3][0]];
						int k5 = ai2[ai1[j3][1]];
						int i6 = ai2[ai1[j3][2]];
						float f15 = af1[i6];
						float f17 = af2[i6];
						float f19 = af3[i6];
						float f21 = af1[k5];
						float f23 = af2[k5];
						float f25 = af3[k5];
						float f27 = af1[j5];
						float f29 = af2[j5];
						float f31 = af3[j5];
						if (f19 < f8 && f25 < f8 && f31 < f8 || f19 > f6 && f25 > f6 && f31 > f6) {
							flag4 = true;
							f47 = 1.0F;
						} else if (Config.useFrustumCulling && !flag7) {
							float f32 = f19 * f4;
							float f34 = f25 * f4;
							float f36 = f31 * f4;
							if (f15 < -f32 && f21 < -f34 && f27 < -f36 || f15 > f32 && f21 > f34 && f27 > f36) {
								flag4 = true;
								f47 = 1.0F;
								k3++;
							} else {
								float f33 = f19 * f5;
								float f35 = f25 * f5;
								float f37 = f31 * f5;
								if (f17 < -f33 && f23 < -f35 && f29 < -f37 || f17 > f33 && f23 > f35 && f29 > f37) {
									flag4 = true;
									f47 = 1.0F;
									k3++;
								}
							}
						}
						if (!flag4)
							if (doCulling) {
								float f38 = f27 - f15;
								float f39 = f29 - f17;
								float f40 = f31 - f19;
								float f41 = f21 - f15;
								float f42 = f23 - f17;
								float f43 = f25 - f19;
								f44 = f39 * f43 - f40 * f42;
								f45 = f40 * f41 - f38 * f43;
								f46 = f38 * f42 - f39 * f41;
								f47 = f44 * f15 + f45 * f17 + f46 * f19;
								if (reverseCulling)
									f47 *= -1F;
							} else {
								f47 = -1F;
							}
						if (f47 <= 0.0F) {
							float f77 = (2.5F - (float) (j3 & 5)) * 0.0001F;
							if (isFlatShaded) {
								float f81 = (float) Math.sqrt(f44 * f44 + f45 * f45 + f46 * f46);
								f44 /= f81;
								f45 /= f81;
								f46 /= f81;
							}
							if (!flag4) {
								int k6 = 0;
								int i7 = -2;
								i2 = 0;
								j2 = 0;
								k2 = 0;
								l2 = 0;
								int l7 = texturemanager.textures[texture[j3]].width;
								int k8 = texturemanager.textures[texture[j3]].height;
								int j9 = l7 >> 1;
								int l9 = k8 >> 1;
								l7--;
								k8--;
								for (int j10 = 0; j10 < 3; j10++) {
									int i11 = ai1[j3][j10];
									int l11 = ai2[i11];
									if (!isFlatShaded || j10 == 0) {
										if (myWorld.lightCacheR[l11] == 1.234567E+007F || isFlatShaded) {
											float f82 = af1[l11];
											float f84 = af2[l11];
											float f86 = af3[l11];
											float f70;
											float f71;
											float f72;
											if (!isFlatShaded) {
												if (invScaleFactor != 1.0F) {
													f70 = af19[l11] * invScaleFactor;
													f71 = af20[l11] * invScaleFactor;
													f72 = af21[l11] * invScaleFactor;
												} else {
													f70 = af19[l11];
													f71 = af20[l11];
													f72 = af21[l11];
												}
											} else {
												f70 = f44;
												f71 = f45;
												f72 = f46;
												int k14 = ai2[ai1[j3][0]];
												f82 = af1[k14];
												f84 = af2[k14];
												f86 = af3[k14];
											}
											float f88 = 0.0F;
											float f89 = 0.0F;
											float f90 = f78;
											float f91 = f79;
											float f94 = f80;
											for (int j16 = 0; j16 < i; j16++) {
												boolean flag5 = false;
												if (!lights.isVisible[j16])
													continue;
												if (Config.gouraud) {
													boolean flag10 = Config.fadeoutLight;
													float f105 = lights.attenuation[j16];
													float f110 = f73;
													if (f105 == -1F)
														flag10 = false;
													else if (f105 != -2F) {
														flag10 = true;
														f110 = 1.0F / f105;
													}
													float f115 = lights.discardDistance[j16];
													float f120 = 0.0F;
													if (f115 == -2F) {
														f115 = f74;
														f120 = f75;
													} else if (f115 != -1F)
														f120 = f115 * f115;
													float f125 = lights.xTr[j16] - f82;
													float f130 = lights.yTr[j16] - f84;
													float f135 = lights.zTr[j16] - f86;
													float f49 = f125 * f125 + f130 * f130 + f135 * f135;
													if (f115 == -1F || f49 <= f120) {
														f49 = (float) Math.sqrt(f49);
														if (doSpecularLighting) {
															float f53 = lights.xTr[j16] * 0.5F;
															float f55 = lights.yTr[j16] * 0.5F;
															float f57 = lights.zTr[j16] * 0.5F;
															float f51 = f53 * f53 + f55 * f55 + f57 * f57;
															if (f51 != 0.0F)
																f88 = (float) ((double) (f53 * f70 + f55 * f71 + f57 * f72) / Math.sqrt(f51));
															else
																f88 = -1F;
															if (f88 < 0.0F) {
																f88 = 0.0F;
															} else {
																if (!Config.useFastSpecular)
																	f88 = Config.specTerm * (float) Math.pow(f88, Config.specPow);
																else
																	f88 = Config.specTerm * (f88 / ((Config.specPow - Config.specPow * f88) + f88));
																if (flag10)
																	f88 -= f49 * f110;
																if (f88 < 0.0F)
																	f88 = 0.0F;
															}
														} else {
															f88 = 0.0F;
														}
														f89 = ((float) Config.lightMul * (f70 * f125 + f71 * f130 + f72 * f135)) / f49;
														if (flag10)
															f89 -= f49 * f110;
														if (f89 < 0.0F)
															f89 = 0.0F;
													} else {
														flag5 = true;
													}
												} else {
													f89 = 0.0F;
												}
												if (!flag5) {
													f89 += f88;
													f90 += lights.rOrg[j16] * f89;
													f91 += lights.gOrg[j16] * f89;
													f94 += lights.bOrg[j16] * f89;
												}
											}

											if (f94 > (float) k)
												f94 = k;
											if (f90 > (float) k)
												f90 = k;
											if (f91 > (float) k)
												f91 = k;
											if (myWorld.useFogging && !myWorld.perPixelFogging) {
												float f97 = f86 / myWorld.fogDistance;
												if (f97 > 1.0F)
													f97 = 1.0F;
												else if (f97 < 0.0F)
													f97 = 0.0F;
												float f100 = 1.0F - f97;
												f90 = f90 * f100 + myWorld.fogColorR * f97;
												f91 = f91 * f100 + myWorld.fogColorG * f97;
												f94 = f94 * f100 + myWorld.fogColorB * f97;
											}
											myWorld.lightCacheB[l11] = f94;
											myWorld.lightCacheR[l11] = f90;
											myWorld.lightCacheG[l11] = f91;
											objVectors.sbOrg[i11] = f94;
											objVectors.srOrg[i11] = f90;
											objVectors.sgOrg[i11] = f91;
										} else {
											objVectors.sbOrg[i11] = myWorld.lightCacheB[l11];
											objVectors.srOrg[i11] = myWorld.lightCacheR[l11];
											objVectors.sgOrg[i11] = myWorld.lightCacheG[l11];
										}
									} else {
										int k12 = objMesh.points[j3][0];
										int j13 = objMesh.points[j3][1];
										objVectors.sbOrg[j13] = objVectors.sbOrg[k12];
										objVectors.srOrg[j13] = objVectors.srOrg[k12];
										objVectors.sgOrg[j13] = objVectors.sgOrg[k12];
										j13 = objMesh.points[j3][2];
										objVectors.sbOrg[j13] = objVectors.sbOrg[k12];
										objVectors.srOrg[j13] = objVectors.srOrg[k12];
										objVectors.sgOrg[j13] = objVectors.sgOrg[k12];
									}
									float f83 = objVectors.sz[l11];
									if (objVectors.zTr[l11] > f8) {
										if (f83 == -1.01F) {
											f83 = 1.0F / objVectors.zTr[l11];
											af5[l11] = f2 * (af1[l11] * f83) + f10;
											af6[l11] = f3 * (af2[l11] * f83) + f11;
											af4[l11] = f83;
										}
										if (af5[l11] < 0.0F)
											i2++;
										else if (af5[l11] > (float) l3)
											j2++;
										if (af6[l11] < 0.0F)
											k2++;
										else if (af6[l11] > (float) i4)
											l2++;
										if (i7 == -2)
											i7 = -1;
									} else {
										k6++;
										if (i7 == -1)
											i7 = j10;
									}
									if (!isEnvmapped || isBlended && flag1) {
										if (!flag1 && af3[l11] > f8) {
											af7[i11] = af11[i11] * f83;
											af8[i11] = af12[i11] * f83;
										}
									} else {
										if (!useCSEnvmapping)
											switch (envMapDir) {
											case 1: // '\001'
												f12 = af22[l11];
												f13 = af23[l11];
												break;

											case 2: // '\002'
												f12 = af22[l11];
												f13 = af24[l11];
												break;

											case 3: // '\003'
												f12 = af23[l11];
												f13 = af24[l11];
												break;
											}
										else
											switch (envMapDir) {
											case 1: // '\001'
												f12 = af19[l11];
												f13 = af20[l11];
												break;

											case 2: // '\002'
												f12 = af19[l11];
												f13 = af21[l11];
												break;

											case 3: // '\003'
												f12 = af20[l11];
												f13 = af21[l11];
												break;
											}
										float f85 = (float) j9 + f12 * invScaleFactor * (float) j9;
										float f87 = (float) l9 + f13 * invScaleFactor * (float) l9;
										if (f85 > (float) l7)
											f85 = l7;
										else if (f85 < 0.0F)
											f85 = 0.0F;
										if (f87 > (float) k8)
											f87 = k8;
										else if (f87 < 0.0F)
											f87 = 0.0F;
										if (af3[l11] > f8) {
											af7[i11] = f85 * f83;
											af8[i11] = f87 * f83;
											if (objVectors.bsu == null)
												objVectors.createBumpmapCoords();
											objVectors.bsu[i11] = objVectors.buOrg[i11] * f83;
											objVectors.bsv[i11] = objVectors.bvOrg[i11] * f83;
										}
										if (objVectors.eu == null)
											objVectors.createEnvmapCoords();
										objVectors.eu[i11] = f85;
										objVectors.ev[i11] = f87;
									}
									if (!flag1) {
										af15[i11] = af18[i11] * f83;
										af14[i11] = af17[i11] * f83;
										af13[i11] = af16[i11] * f83;
									}
								}

								if (flag1 && k6 != 0 && k6 < 3)
									k6 = 0;
								if (k6 != 0 && i7 == -1)
									i7 = 0;
								if (k6 == 2) {
									int k10 = i7;
									int j11 = k10 - 1;
									if (j11 == -1)
										j11 = 2;
									int i12 = k10 + 1;
									if (i12 == 3)
										i12 = 0;
									int l12 = ai1[j3][j11];
									int k13 = ai2[l12];
									int i14 = ai1[j3][k10];
									int l14 = ai2[i14];
									int j15 = ai1[j3][i12];
									int l15 = ai2[j15];
									float f58;
									float f60;
									float f62;
									float f64;
									float f66;
									float f68;
									if (isEnvmapped) {
										f58 = objVectors.eu[l12];
										f60 = objVectors.ev[l12];
										f62 = objVectors.eu[i14];
										f64 = objVectors.ev[i14];
										f66 = objVectors.eu[j15];
										f68 = objVectors.ev[j15];
									} else {
										f58 = af11[l12];
										f60 = af12[l12];
										f62 = af11[i14];
										f64 = af12[i14];
										f66 = af11[j15];
										f68 = af12[j15];
									}
									float f92 = (f8 - af3[k13]) / (af3[l14] - af3[k13]);
									float f95 = af1[k13] + f92 * (af1[l14] - af1[k13]);
									float f98 = af2[k13] + f92 * (af2[l14] - af2[k13]);
									float f101 = f58 + f92 * (f62 - f58);
									float f106 = f60 + f92 * (f64 - f60);
									float f111 = objVectors.buOrg[l12] + f92 * (objVectors.buOrg[i14] - objVectors.buOrg[l12]);
									float f116 = objVectors.bvOrg[l12] + f92 * (objVectors.bvOrg[i14] - objVectors.bvOrg[l12]);
									float f121 = af18[l12] + f92 * (af18[i14] - af18[l12]);
									float f126 = af16[l12] + f92 * (af16[i14] - af16[l12]);
									float f131 = af17[l12] + f92 * (af17[i14] - af17[l12]);
									float f136 = 1.0F / f8;
									float f138 = f2 * (f95 * f136) + f10;
									float f140 = f3 * (f98 * f136) + f11;
									float f142 = f101 * f136;
									float f144 = f106 * f136;
									float f146 = f111 * f136;
									float f148 = f116 * f136;
									float f150 = f121 * f136;
									float f152 = f126 * f136;
									float f154 = f131 * f136;
									f92 = (f8 - af3[k13]) / (af3[l15] - af3[k13]);
									f95 = af1[k13] + f92 * (af1[l15] - af1[k13]);
									f98 = af2[k13] + f92 * (af2[l15] - af2[k13]);
									float f156 = 1.0F / f8;
									float f157 = f2 * (f95 * f156) + f10;
									float f158 = f3 * (f98 * f156) + f11;
									int j17 = 0;
									int l17 = 0;
									int i18 = 0;
									int j18 = 0;
									if (af5[k13] < 0.0F)
										j17++;
									else if (af5[k13] > (float) l3)
										l17++;
									if (af6[k13] < 0.0F)
										i18++;
									else if (af6[k13] > (float) i4)
										j18++;
									if (f138 < 0.0F)
										j17++;
									else if (f138 > (float) l3)
										l17++;
									if (f140 < 0.0F)
										i18++;
									else if (f140 > (float) i4)
										j18++;
									if (f157 < 0.0F)
										j17++;
									else if (f157 > (float) l3)
										l17++;
									if (f158 < 0.0F)
										i18++;
									else if (f158 > (float) i4)
										j18++;
									if (j17 < 3 && i18 < 3 && l17 < 3 && j18 < 3) {
										float f102 = f58 + f92 * (f66 - f58);
										float f107 = f60 + f92 * (f68 - f60);
										float f112 = objVectors.buOrg[l12] + f92 * (objVectors.buOrg[j15] - objVectors.buOrg[l12]);
										float f117 = objVectors.bvOrg[l12] + f92 * (objVectors.bvOrg[j15] - objVectors.bvOrg[l12]);
										float f122 = af18[l12] + f92 * (af18[j15] - af18[l12]);
										float f127 = af16[l12] + f92 * (af16[j15] - af16[l12]);
										float f132 = af17[l12] + f92 * (af17[j15] - af17[l12]);
										float f162 = f102 * f156;
										float f164 = f107 * f156;
										float f166 = f112 * f156;
										float f168 = f117 * f156;
										float f170 = f122 * f156;
										float f172 = f127 * f156;
										float f174 = f132 * f156;
										vislist.addToList(myWorld.clippedPolys, this, myWorld.clippedPolys.objMesh.anzTri, j3, (f19 + f25
												+ f31 + f77) * 0.3333333F, 0x5f5e0ff, flag);
										addClippedPoly(j3, af5[k13], af6[k13], af4[k13], af7[l12], af8[l12], af14[l12], af13[l12],
												af15[l12], f138, f140, f136, f142, f144, f154, f152, f150, f157, f158, f156, f162, f164, f174,
												f172, f170, af9[l12], af10[l12], f146, f148, f166, f168);
									}
									flag4 = true;
								} else if (k6 == 1) {
									int l10 = i7;
									int k11 = l10 - 1;
									if (k11 == -1)
										k11 = 2;
									int j12 = l10 + 1;
									if (j12 == 3)
										j12 = 0;
									int i13 = ai1[j3][k11];
									int l13 = ai2[i13];
									int j14 = ai1[j3][l10];
									int i15 = ai2[j14];
									int k15 = ai1[j3][j12];
									int i16 = ai2[k15];
									float f59;
									float f61;
									float f63;
									float f65;
									float f67;
									float f69;
									if (isEnvmapped) {
										f59 = objVectors.eu[i13];
										f61 = objVectors.ev[i13];
										f63 = objVectors.eu[j14];
										f65 = objVectors.ev[j14];
										f67 = objVectors.eu[k15];
										f69 = objVectors.ev[k15];
									} else {
										f59 = af11[i13];
										f61 = af12[i13];
										f63 = af11[j14];
										f65 = af12[j14];
										f67 = af11[k15];
										f69 = af12[k15];
									}
									float f93 = (f8 - af3[l13]) / (af3[i15] - af3[l13]);
									float f96 = af1[l13] + f93 * (af1[i15] - af1[l13]);
									float f99 = af2[l13] + f93 * (af2[i15] - af2[l13]);
									float f103 = f59 + f93 * (f63 - f59);
									float f108 = f61 + f93 * (f65 - f61);
									float f113 = objVectors.buOrg[i13] + f93 * (objVectors.buOrg[j14] - objVectors.buOrg[i13]);
									float f118 = objVectors.bvOrg[i13] + f93 * (objVectors.bvOrg[j14] - objVectors.bvOrg[i13]);
									float f123 = af18[i13] + f93 * (af18[j14] - af18[i13]);
									float f128 = af16[i13] + f93 * (af16[j14] - af16[i13]);
									float f133 = af17[i13] + f93 * (af17[j14] - af17[i13]);
									float f137 = 1.0F / f8;
									float f139 = f2 * (f96 * f137) + f10;
									float f141 = f3 * (f99 * f137) + f11;
									float f143 = f103 * f137;
									float f145 = f108 * f137;
									float f147 = f113 * f137;
									float f149 = f118 * f137;
									float f151 = f123 * f137;
									float f153 = f128 * f137;
									float f155 = f133 * f137;
									int k16 = 0;
									int l16 = 0;
									int i17 = 0;
									int k17 = 0;
									if (objVectors.sx[l13] < 0.0F)
										k16++;
									else if (objVectors.sx[l13] > (float) l3)
										l16++;
									if (objVectors.sy[l13] < 0.0F)
										i17++;
									else if (objVectors.sy[l13] > (float) i4)
										k17++;
									if (f139 < 0.0F)
										k16++;
									else if (f139 > (float) l3)
										l16++;
									if (f141 < 0.0F)
										i17++;
									else if (f141 > (float) i4)
										k17++;
									if (objVectors.sx[i16] < 0.0F)
										k16++;
									else if (objVectors.sx[i16] > (float) l3)
										l16++;
									if (objVectors.sy[i16] < 0.0F)
										i17++;
									else if (objVectors.sy[i16] > (float) i4)
										k17++;
									if (k16 < 3 && i17 < 3 && l16 < 3 && k17 < 3) {
										vislist.addToList(myWorld.clippedPolys, this, myWorld.clippedPolys.objMesh.anzTri, j3, (f19 + f25
												+ f31 + f77) * 0.3333333F, 0x5f5e0ff, flag);
										addClippedPoly(j3, af5[l13], af6[l13], af4[l13], af7[i13], af8[i13], af14[i13], af13[i13],
												af15[i13], f139, f141, f137, f143, f145, f155, f153, f151, af5[i16], af6[i16], af4[i16],
												af7[k15], af8[k15], af14[k15], af13[k15], af15[k15], af9[i13], af10[i13], f147, f149, af9[k15],
												af10[k15]);
									}
									f93 = (f8 - af3[i16]) / (af3[i15] - af3[i16]);
									f96 = objVectors.xTr[i16] + f93 * (af1[i15] - af1[i16]);
									f99 = objVectors.yTr[i16] + f93 * (af2[i15] - af2[i16]);
									float f159 = f2 * (f96 * f137) + f10;
									float f160 = f3 * (f99 * f137) + f11;
									k16 = 0;
									l16 = 0;
									i17 = 0;
									k17 = 0;
									if (f159 < 0.0F)
										k16++;
									else if (f159 > (float) l3)
										l16++;
									if (f160 < 0.0F)
										i17++;
									else if (f160 > (float) i4)
										k17++;
									if (f139 < 0.0F)
										k16++;
									else if (f139 > (float) l3)
										l16++;
									if (f141 < 0.0F)
										i17++;
									else if (f141 > (float) i4)
										k17++;
									if (af5[i16] < 0.0F)
										k16++;
									else if (af5[i16] > (float) l3)
										l16++;
									if (af6[i16] < 0.0F)
										i17++;
									else if (af6[i16] > (float) i4)
										k17++;
									if (k16 < 3 && i17 < 3 && l16 < 3 && k17 < 3) {
										float f104 = f67 + f93 * (f63 - f67);
										float f109 = f69 + f93 * (f65 - f69);
										float f114 = objVectors.buOrg[k15] + f93 * (objVectors.buOrg[j14] - objVectors.buOrg[k15]);
										float f119 = objVectors.bvOrg[k15] + f93 * (objVectors.bvOrg[j14] - objVectors.bvOrg[k15]);
										float f124 = af18[k15] + f93 * (af18[j14] - af18[k15]);
										float f129 = objVectors.sgOrg[k15] + f93 * (af16[j14] - af16[k15]);
										float f134 = objVectors.srOrg[k15] + f93 * (af17[j14] - af17[k15]);
										float f161 = f104 * f137;
										float f163 = f109 * f137;
										float f165 = f114 * f137;
										float f167 = f119 * f137;
										float f169 = f124 * f137;
										float f171 = f129 * f137;
										float f173 = f134 * f137;
										vislist.addToList(myWorld.clippedPolys, this, myWorld.clippedPolys.objMesh.anzTri, j3, (f19 + f25
												+ f31 + f77) * 0.3333333F, 0x5f5e0ff, flag);
										addClippedPoly(j3, f139, f141, f137, f143, f145, f155, f153, f151, f159, f160, f137, f161, f163,
												f173, f171, f169, af5[i16], af6[i16], af4[i16], af7[k15], af8[k15], af14[k15], af13[k15],
												af15[k15], f147, f149, f165, f167, af9[k15], af10[k15]);
									}
									flag4 = true;
								} else if (k6 != 0)
									flag4 = true;
							}
							if (!flag4 && i2 < 3 && k2 < 3 && j2 < 3 && l2 < 3) {
								int l6 = 0;
								if (!dynSectorDetect) {
									if (!oneSectorOnly && sector != null)
										l6 = sector[j3];
									else
										l6 = singleSectorNumber;
								} else if (sectorCnt == 1) {
									l6 = dynSectorList[1];
								} else {
									int j7 = 1;
									do {
										if (j7 > sectorCnt)
											break;
										if (dynSectorList[j7] == k1) {
											l6 = k1;
											break;
										}
										j7++;
									} while (true);
								}
								int k7 = 0x5f5e0ff;
								if (Config.doPortalHsr && l6 != k1 && l6 != 0)
									k7 = myWorld.portals.testAgainstPortals(this, j5, k5, i6, j3);
								if (k7 != -1)
									if (!flag1)
										vislist.addToList(this, this, j3, j3, (f19 + f25 + f31 + f77) * 0.3333333F, k7, 0, flag);
									else if (isTrans && !isBumpmapped) {
										int i8 = 0;
										if (usesMultiTexturing) {
											for (int l8 = 0; l8 < maxStagesUsed - 1 && multiTex[l8][j3] != -1; l8++)
												i8++;

										}
										vislist.addToList(this, this, j3, j3, (f19 + f25 + f31 + f77) * 0.3333333F, k7, i8, flag);
									} else {
										int j8 = texture[j3];
										int i9 = 0;
										if (usesMultiTexturing) {
											int k9 = 0;
											do {
												if (k9 >= maxStagesUsed - 1)
													break;
												int i10 = multiTex[k9][j3];
												if (i10 == -1)
													break;
												j8 += i10 << multiMode[k9][j3];
												i9++;
												k9++;
											} while (true);
										}
										vislist.addToList(this, this, j3, j3, 0x1869f - j8, k7, i9, flag);
									}
							}
						}
					}
					j3++;
				} while (!flag6 && j3 < objMesh.anzTri || flag6 && k4 < l4);
			} while (flag6 && j4 < ocTree.getLeafCount());
		}
	}

	boolean getLazyTransformationState() {
		return lazyTransforms;
	}

	final Matrix[] getInverseWorldTransformation() {
		Matrix matrix1 = getWorldTransformation();
		Matrix matrix;
		if (!lazyTransforms || invCache == null) {
			matrix = matrix1.invert();
			if (lazyTransforms)
				invCache = matrix.cloneMatrix();
		} else {
			matrix = invCache.cloneMatrix();
		}
		return (new Matrix[] { matrix1, matrix });
	}

	final void setThisAsMain() {
		if (myWorld != null) {
			for (int i = 0; i < myWorld.objectList.size(); i++) {
				myWorld.objectList.elementAt(i).isMainWorld = false;
				myWorld.objectList.elementAt(i).oneSectorOnly = true;
			}

			isMainWorld = true;
			oneSectorOnly = false;
			singleSectorNumber = -1;
		} else {
			Logger
					.log(
							"Error: An object that isn't part of the world can't be assigned as main. Add the object to the world before performing this operation.",
							0);
		}
	}

	private final void appendToObject(Object3D object3d) {
		Object3D object3d1 = object3d;
		int i = object3d1.objMesh.anzVectors;
		int j = object3d1.objMesh.anzCoords;
		int k = object3d1.objMesh.anzTri;
		int l = object3d1.sectorCnt;
		if (objVectors != null && objVectors.uMul != null) {
			object3d1.usesMultiTexturing = true;
			object3d1.maxStagesUsed = Math.max(object3d1.maxStagesUsed, maxStagesUsed);
			object3d1.objVectors.createMultiCoords();
			for (int i1 = 0; i1 < Config.maxTextureLayers - 1; i1++) {
				for (int j2 = 0; j2 < objMesh.anzVectors; j2++) {
					int k3 = j2 + i;
					object3d1.objVectors.uMul[i1][k3] = objVectors.uMul[i1][j2];
					object3d1.objVectors.vMul[i1][k3] = objVectors.vMul[i1][j2];
				}

			}

		}
		if (multiTex != null) {
			for (int j1 = 0; j1 < Config.maxTextureLayers - 1; j1++) {
				if (object3d1.multiTex == null) {
					object3d1.multiTex = new int[Config.maxTextureLayers - 1][object3d1.texture.length];
					object3d1.multiMode = new int[Config.maxTextureLayers - 1][object3d1.texture.length];
				}
				System.arraycopy(multiTex[j1], 0, object3d1.multiTex[j1], k, objMesh.anzTri);
				System.arraycopy(multiMode[j1], 0, object3d1.multiMode[j1], k, objMesh.anzTri);
			}

		}
		if (objMesh.anzVectors > 0 && objVectors.eu != null)
			object3d1.objVectors.createEnvmapCoords();
		for (int k1 = 0; k1 < objMesh.anzVectors; k1++) {
			int k2 = k1 + i;
			object3d1.objVectors.sbOrg[k2] = objVectors.sbOrg[k1];
			object3d1.objVectors.srOrg[k2] = objVectors.srOrg[k1];
			object3d1.objVectors.sgOrg[k2] = objVectors.sgOrg[k1];
			object3d1.objVectors.nuOrg[k2] = objVectors.nuOrg[k1];
			object3d1.objVectors.nvOrg[k2] = objVectors.nvOrg[k1];
			object3d1.objVectors.uOrg[k2] = objVectors.uOrg[k1];
			object3d1.objVectors.vOrg[k2] = objVectors.vOrg[k1];
			if (objVectors.eu != null) {
				object3d1.objVectors.eu[k2] = objVectors.eu[k1];
				object3d1.objVectors.ev[k2] = objVectors.ev[k1];
			}
			if (objVectors.alpha != null)
				object3d1.objVectors.alpha[k2] = objVectors.alpha[k1];
			object3d1.objVectors.buOrg[k2] = objVectors.buOrg[k1];
			object3d1.objVectors.bvOrg[k2] = objVectors.bvOrg[k1];
			object3d1.objVectors.vertexSector[k2] = objVectors.vertexSector[k1];
			object3d1.objMesh.coords[k2] = objMesh.coords[k1] + j;
		}

		for (int l1 = 0; l1 < objMesh.anzTri; l1++) {
			int l2 = l1 + k;
			object3d1.objMesh.points[l2][0] = objMesh.points[l1][0] + i;
			object3d1.objMesh.points[l2][1] = objMesh.points[l1][1] + i;
			object3d1.objMesh.points[l2][2] = objMesh.points[l1][2] + i;
		}

		int i2 = objMesh.anzCoords;
		if (objMesh.obbStart != 0)
			i2 = objMesh.obbStart;
		for (int i3 = 0; i3 < i2; i3++) {
			int l3 = i3 + j;
			object3d1.objMesh.xOrg[l3] = objMesh.xOrg[i3];
			object3d1.objMesh.yOrg[l3] = objMesh.yOrg[i3];
			object3d1.objMesh.zOrg[l3] = objMesh.zOrg[i3];
			object3d1.objMesh.nxOrg[l3] = objMesh.nxOrg[i3];
			object3d1.objMesh.nyOrg[l3] = objMesh.nyOrg[i3];
			object3d1.objMesh.nzOrg[l3] = objMesh.nzOrg[i3];
		}

		int j3 = objMesh.anzTri;
		if (texture != null)
			System.arraycopy(texture, 0, object3d1.texture, k, j3);
		if (sector != null)
			System.arraycopy(sector, 0, object3d1.sector, k, j3);
		if (bumpmap != null)
			System.arraycopy(bumpmap, 0, object3d1.bumpmap, k, j3);
		if (basemap != null)
			System.arraycopy(basemap, 0, object3d1.basemap, k, j3);
		if (sectorStartPoint != null)
			System.arraycopy(sectorStartPoint, 0, object3d1.sectorStartPoint, l, sectorCnt);
		if (sectorEndPoint != null)
			System.arraycopy(sectorEndPoint, 0, object3d1.sectorEndPoint, l, sectorCnt);
		if (sectorStartPoly != null)
			System.arraycopy(sectorStartPoly, 0, object3d1.sectorStartPoly, l, sectorCnt);
		if (sectorEndPoly != null)
			System.arraycopy(sectorEndPoly, 0, object3d1.sectorEndPoly, l, sectorCnt);
		object3d1.objMesh.anzTri += objMesh.anzTri;
		object3d1.objMesh.anzCoords += i2;
		object3d1.objMesh.anzVectors += objMesh.anzVectors;
	}

	private final void switchTriangles(int i, int j) {
		if (sector != null) {
			int k = sector[i];
			sector[i] = sector[j];
			sector[j] = k;
		}
		int l = texture[i];
		texture[i] = texture[j];
		texture[j] = l;
		if (usesMultiTexturing) {
			for (int j1 = 0; j1 < maxStagesUsed - 1; j1++) {
				l = multiTex[j1][i];
				multiTex[j1][i] = multiTex[j1][j];
				multiTex[j1][j] = l;
				l = multiMode[j1][i];
				multiMode[j1][i] = multiMode[j1][j];
				multiMode[j1][j] = l;
			}

		}
		l = basemap[i];
		basemap[i] = basemap[j];
		basemap[j] = l;
		if (bumpmap != null) {
			int i1 = bumpmap[i];
			bumpmap[i] = bumpmap[j];
			bumpmap[j] = i1;
		}
		int ai[] = objMesh.points[i];
		objMesh.points[i] = objMesh.points[j];
		objMesh.points[j] = ai;
	}

	final void getProjectedPoint(float f, float f1, float f2, SimpleVector simplevector, float af[]) {
		Matrix matrix = getWorldTransformation();
		float f3 = matrix.mat[0][0];
		float f4 = matrix.mat[1][0];
		float f5 = matrix.mat[1][1];
		float f6 = matrix.mat[2][1];
		float f7 = matrix.mat[2][0];
		float f8 = matrix.mat[0][1];
		float f9 = matrix.mat[2][2];
		float f10 = matrix.mat[1][2];
		float f11 = matrix.mat[0][2];
		float f12 = matrix.mat[3][0];
		float f13 = matrix.mat[3][1];
		float f14 = matrix.mat[3][2];
		float f15 = f * f3 + f1 * f4 + f2 * f7 + f12;
		float f16 = f * f8 + f1 * f5 + f2 * f6 + f13;
		float f17 = f * f11 + f1 * f10 + f2 * f9 + f14;
		if (simplevector != null) {
			simplevector.x = f15;
			simplevector.y = f16;
			simplevector.z = f17;
		}
		if (af != null) {
			af[0] = f15;
			af[1] = f16;
			af[2] = f17;
		}
	}

	void notifyCollisionListeners(int i, int j, Object3D aobject3d[]) {
		notifyCollisionListeners(null, i, j, aobject3d);
	}

	void notifyCollisionListeners(Object3D object3d, int i, int j, Object3D aobject3d[]) {
		if (collisionListener == null || disableListeners)
			return;
		CollisionEvent collisionevent = new CollisionEvent(this, object3d, i, j, aobject3d);
		int k = collisionListener.size();
		for (int l = 0; l < k; l++) {
			CollisionListener collisionlistener = (CollisionListener) collisionListener.elementAt(l);
			if (collisionlistener.requiresPolygonIDs() && collisionevent.getPolygonIDs() == null)
				collisionevent.setPolygonIDs(polygonIDs, pIDCount);
			collisionlistener.collision(collisionevent);
		}

	}

	void reallyStrip() {
		stripped = true;
		toStrip = false;
		objVectors.strip();
		objMesh.strongStrip(myWorld, this);
	}

	void resetPolygonIDCount() {
		pIDCount = 0;
		lastAddedID = -1;
	}

	private void addPolygonID(int i) {
		if (collisionListener == null || disableListeners)
			return;
		if (polygonIDs == null) {
			polygonIDs = new int[Config.polygonIDLimit];
			pIDCount = 0;
		}
		if (pIDCount < polygonIDs.length) {
			if (i == lastAddedID)
				return;
			for (int j = 0; j < pIDCount; j++)
				if (polygonIDs[j] == i)
					return;

			polygonIDs[pIDCount] = i;
			lastAddedID = i;
			pIDCount++;
		}
	}

	private Matrix getCachedMatrix(int i) {
		if (!useMatrixCache)
			return new Matrix();
		Matrix amatrix[] = (Matrix[]) matrixCache.get(Thread.currentThread());
		if (amatrix == null) {
			amatrix = new Matrix[2];
			amatrix[0] = new Matrix();
			amatrix[1] = new Matrix();
			matrixCache.put(Thread.currentThread(), amatrix);
		}
		if (matrixCache.size() > 50)
			matrixCache.clear();
		amatrix[i].setIdentity();
		return amatrix[i];
	}

	private void checkBumpmap() {
		if (bumpmap == null)
			bumpmap = new int[texture.length];
	}

	private void compileInternal() {
		if (compiled == null)
			compiled = new Vector();
		if (!hasBeenBuild && Config.autoBuild)
			build();
		Config.lightMul = 1;
		Config.glVertexArrays = true;
	}

	private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
		objectinputstream.defaultReadObject();
		mat2 = new Matrix();
		mat3 = new Matrix();
		mat5 = new Matrix();
		mat6 = new Matrix();
		matrixCache = new Hashtable(3);
		transBuffer = new Matrix();
		xWs = new float[8];
		yWs = new float[8];
		zWs = new float[8];
	}

	private static final long serialVersionUID = 6L;
	public static final boolean ENVMAP_WORLDSPACE = false;
	public static final boolean ENVMAP_CAMERASPACE = true;
	public static final boolean ENVMAP_ENABLED = true;
	public static final boolean ENVMAP_DISABLED = false;
	public static final boolean BUMPMAPPING_ENABLED = true;
	public static final boolean BUMPMAPPING_DISABLED = false;
	public static final boolean BLENDING_ENABLED = true;
	public static final boolean BLENDING_DISABLED = false;
	public static final int TRANSPARENCY_MODE_DEFAULT = 0;
	public static final int TRANSPARENCY_MODE_ADD = 1;
	public static final boolean BILLBOARDING_ENABLED = true;
	public static final boolean BILLBOARDING_DISABLED = false;
	public static final boolean MOUSE_SELECTABLE = true;
	public static final boolean MOUSE_UNSELECTABLE = false;
	public static final boolean FILTERING_ENABLED = true;
	public static final boolean FILTERING_DISABLED = false;
	public static final boolean CULLING_ENABLED = true;
	public static final boolean CULLING_DISABLED = false;
	public static final boolean SPECULAR_ENABLED = true;
	public static final boolean SPECULAR_DISABLED = false;
	public static final int FINALIZE_DEFAULT = 1;
	public static final int FINALIZE_PRESORTZ = 2;
	public static final int FINALIZE_PRESORTY = 3;
	public static final int FINALIZE_PRESORTX = 4;
	public static final int ENVMAP_XY = 1;
	public static final int ENVMAP_XZ = 2;
	public static final int ENVMAP_YZ = 3;
	public static final boolean OBJ_VISIBLE = true;
	public static final boolean OBJ_INVISIBLE = false;
	public static final boolean SECTOR_AUTODETECT = true;
	public static final boolean SECTOR_PRECALCULATED = false;
	public static final int COLLISION_CHECK_NONE = 0;
	public static final int COLLISION_CHECK_OTHERS = 1;
	public static final int COLLISION_CHECK_SELF = 2;
	public static final float COLLISION_NONE = 1E+012F;
	public static final float RAY_MISSES_BOX = 1E+012F;
	public static final boolean COLLISION_DETECTION_OPTIMIZED = true;
	public static final boolean COLLISION_DETECTION_NOT_OPTIMIZED = false;
	public static final int SHADING_GOURAUD = 0;
	public static final int SHADING_FAKED_FLAT = 1;
	public static final int LIGHTING_ALL_ENABLED = 0;
	public static final int LIGHTING_NO_LIGHTS = 1;
	public static final int ELLIPSOID_ALIGNED = 0;
	public static final int ELLIPSOID_TRANSFORMED = 1;
	public static final int UNKNOWN_OBJECTSIZE = -1;
	public static final int NO_OBJECT = -100;
	private static final Lights DUMMY_LIGHTS = new Lights(0);
	private static final double INSIDE_POLYGON_CONST = 6.2203534541077907D;
	private static final float EPSILON = 1E-009F;
	private static final float DIVER = 0.3333333F;
	static int nextID = 0;
	static int globalListenerCount = 0;
	boolean isTrans;
	int transMode;
	boolean isEnvmapped;
	boolean useCSEnvmapping;
	int envMapDir;
	boolean isBlended;
	boolean isBumpmapped;
	boolean isVisible;
	boolean isLit;
	boolean isPotentialCollider;
	boolean mayCollide;
	boolean dynSectorDetect;
	boolean wasCollider;
	float sortOffset;
	boolean isSelectable;
	boolean someSectorVisible;
	Vectors objVectors;
	Mesh objMesh;
	World myWorld;
	int number;
	String name;
	int clipAtPortal;
	int texture[];
	int multiTex[][];
	int multiMode[][];
	int maxStagesUsed;
	boolean usesMultiTexturing;
	int sector[];
	Object3D parent[];
	int parentCnt;
	int sectorStartPoint[];
	int sectorEndPoint[];
	int sectorStartPoly[];
	int sectorEndPoly[];
	int bumpmap[];
	int basemap[];
	int dynSectorList[];
	int sectorCnt;
	int transValue;
	boolean isMainWorld;
	boolean oneSectorOnly;
	int singleSectorNumber;
	boolean hasPortals;
	boolean alwaysFilter;
	float centerX;
	float centerY;
	float centerZ;
	boolean hasBoundingBox;
	boolean isFlatShaded;
	boolean object3DRendered;
	transient Vector compiled;
	boolean dynamic;
	boolean modified;
	boolean indexed;
	int batchSize;
	boolean preferDL;
	boolean staticUV;
	transient float nearestLights[][];
	boolean toStrip;
	transient IRenderHook renderHook;
	boolean isSharingSource;
	boolean sharing;
	Object3D shareWith;
	transient Matrix transBuffer;
	private boolean stripped;
	private Vector collisionListener;
	private boolean disableListeners;
	private int polygonIDs[];
	private int pIDCount;
	private int lastAddedID;
	private int lowestPos;
	private int highestPos;
	private float xRotationCenter;
	private float yRotationCenter;
	private float zRotationCenter;
	private int addColorR;
	private int addColorG;
	private int addColorB;
	private Matrix rotationMatrix;
	private Matrix translationMatrix;
	private Matrix originMatrix;
	private transient Matrix mat2;
	private transient Matrix mat3;
	private transient Matrix mat5;
	private transient Matrix mat6;
	private transient float xWs[];
	private transient float yWs[];
	private transient float zWs[];
	boolean doCulling;
	boolean doSpecularLighting;
	boolean writeToZbuffer;
	private Animation anim;
	private boolean neverOptimize;
	private float scaleFactor;
	private float invScaleFactor;
	private boolean isBillBoard;
	private OcTree ocTree;
	private boolean lazyTransforms;
	private transient Matrix transCache;
	private transient Matrix invCache;
	private boolean visComplete;
	private boolean optimizeColDet;
	private float largestPolygonSize;
	private transient PolygonManager polyManager;
	private int ellipsoidMode;
	private Object userObj;
	boolean reverseCulling;
	private boolean hasBeenBuild;
	private boolean useMatrixCache;
	private transient Hashtable matrixCache;
	private Color addColorInstance;
	private transient Hashtable sectors;
	private transient SimpleVector cachedTC;
	private boolean reNormalize;

}