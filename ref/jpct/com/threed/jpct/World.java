// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import com.threed.jpct.util.Overlay;
import java.awt.Color;
import java.io.Serializable;
import java.util.Enumeration;

// Referenced classes of package com.threed.jpct:
//            VisListManager, Object3DList, VisList, Object3D, 
//            Portals, Camera, Lights, GLRenderer, 
//            SimpleVector, CollisionInfo, Matrix, Plane, 
//            Config, Vectors, Logger, BufferedMatrix, 
//            Mesh, FrameBuffer, IRenderer, VersionHelper, 
//            TextureManager, IntegerC

public class World implements Serializable {

	public World() {
		drawCnt = 0L;
		mainWorld = -1;
		useFogging = false;
		fogModeChanged = 0;
		perPixelFogging = false;
		fogStart = 1.0F;
		fogDistance = 150F;
		fogColorR = 0.0F;
		fogColorG = 0.0F;
		fogColorB = 0.0F;
		ambientRed = 100;
		ambientGreen = 100;
		ambientBlue = 100;
		vlManager = new VisListManager();
		targets = null;
		disposed = false;
		nearPlane = -1F;
		farPlane = -1F;
		nearPlaneOld = Config.nearPlane;
		farPlaneOld = Config.farPlane;
		objectList = new Object3DList();
		int i = Config.polygonBufferSize;
		if (i == -1)
			i = Config.maxPolysVisible;
		if (!Config.shareVisibilityList) {
			visList = new VisList(Config.maxPolysVisible);
			clippedPolys = new Object3D(i);
		} else {
			if (staticVisList == null) {
				staticVisList = new VisList(Config.maxPolysVisible);
				staticClippedPolys = new Object3D(i);
			}
			visList = staticVisList;
			clippedPolys = staticClippedPolys;
		}
		clippedPolys.objVectors.createScreenColors();
		addObject(clippedPolys);
		portals = new Portals();
		portals.portalsObj.hasPortals = true;
		addObject(portals.portalsObj);
		camera = new Camera();
		lights = new Lights(Config.maxLights);
		lock = FALSE_LOCK;
		wasLocked = 0;
	}

	public void dispose() {
		disposed = true;
		if (vlManager != null)
			vlManager.dispose();
	}

	public synchronized void lockMatrices() {
		while (lock.booleanValue()) {
			wasLocked++;
			try {
				if (Config.lockingTimer > 0)
					Thread.sleep(Config.lockingTimer);
				else
					Thread.yield();
			} catch (Exception exception) {
			}
		}
		lock = TRUE_LOCK;
	}

	public void unlockMatrices() {
		if (!lock.booleanValue())
			Logger.log("Tried to unlock already unlocked matrices!", 0);
		lock = FALSE_LOCK;
	}

	public Lights getLights() {
		return lights;
	}

	public int getSize() {
		return objectList.size() - 2;
	}

	public void setMainObjectID(int i) {
		Object3D object3d = getObject(i);
		object3d.setThisAsMain();
		mainWorld = i;
	}

	public int getMainObjectID() {
		return mainWorld;
	}

	public Object3D getMainObject() {
		if (mainWorld != -1)
			return getObject(mainWorld);
		else
			return null;
	}

	public Camera getCamera() {
		return camera;
	}

	public Portals getPortals() {
		return portals;
	}

	public VisList getVisibilityList() {
		return visList;
	}

	public void decoupleVisibilityList() {
		visList = new VisList(Config.maxPolysVisible);
		int i = Config.polygonBufferSize;
		if (i == -1)
			i = Config.maxPolysVisible;
		clippedPolys = new Object3D(i);
	}

	public void newCamera() {
		camera = new Camera();
	}

	public void setCameraTo(Camera camera1) {
		camera = camera1;
	}

	public int getCameraSector() {
		return portals.getCurrentSector(getMainObject(), ((BufferedMatrix) (camera)).frontBx,
				((BufferedMatrix) (camera)).frontBy, ((BufferedMatrix) (camera)).frontBz);
	}

	public void removeObject(int i) {
		boolean flag = false;
		i += 2;
		int j = 0;
		do {
			if (j >= objectList.size())
				break;
			if (objectList.elementAt(j).number == i) {
				flag = true;
				objectList.elementAt(j).myWorld = null;
				objectList.removeElementAt(j);
				break;
			}
			j++;
		} while (true);
		if (!flag)
			Logger.log("Can't remove object #" + (i - 2) + "!", 0);
	}

	public void removeObject(Object3D object3d) {
		if (!objectList.removeElement(object3d))
			Logger.log("Can't remove object #" + object3d.getID() + "!", 0);
		else
			object3d.myWorld = null;
	}

	public Object3D getObject(int i) {
		i += 2;
		for (int j = 0; j < objectList.size(); j++)
			if (objectList.elementAt(j).number == i)
				return objectList.elementAt(j);

		Logger.log("Can't retrieve object #" + (i - 2) + "!", 0);
		return null;
	}

	public Object3D getObjectByName(String s) {
		Object3D object3d = getInternalObjectByName(s);
		if (object3d == null)
			Logger.log("Can't retrieve an object named '" + s + "'!", 0);
		return object3d;
	}

	public int addObject(Object3D object3d) {
		if (object3d == null) {
			Logger.log("Can't assign 'null' to a World!", 0);
			return -100;
		}
		int i = object3d.getMesh().getVertexCount() + 8;
		if (lightCacheR == null || lightCacheR.length < i) {
			lightCacheR = new float[i];
			lightCacheG = new float[i];
			lightCacheB = new float[i];
			nxTr = new float[i];
			nyTr = new float[i];
			nzTr = new float[i];
			if (nxWs != null) {
				nxWs = new float[i];
				nyWs = new float[i];
				nzWs = new float[i];
			}
		}
		objectList.addElement(object3d);
		object3d.myWorld = this;
		return object3d.getID();
	}

	public void addObjects(Object3D aobject3d[]) {
		for (int i = 0; i < aobject3d.length; i++)
			addObject(aobject3d[i]);

	}

	public int addLight(SimpleVector simplevector, float f, float f1, float f2) {
		return lights.addLight(simplevector.x, simplevector.y, simplevector.z, f, f1, f2);
	}

	public int addLight(SimpleVector simplevector, Color color) {
		return lights.addLight(simplevector.x, simplevector.y, simplevector.z, color.getRed(), color.getGreen(), color
				.getBlue());
	}

	public void setLightRotation(int i, SimpleVector simplevector, float f, float f1, float f2) {
		lights.setAutoRotation(i, simplevector.x, simplevector.y, simplevector.z, f, f1, f2);
	}

	public void setLightRotation(int i, float f, float f1, float f2) {
		lights.setAutoRotation(i, f, f1, f2);
	}

	public void setLightPosition(int i, SimpleVector simplevector) {
		lights.setPosition(i, simplevector.x, simplevector.y, simplevector.z);
	}

	public void setLightVisibility(int i, boolean flag) {
		lights.setVisibility(i, flag);
	}

	public void setLightDiscardDistance(int i, float f) {
		lights.setDiscardDistance(i, f);
	}

	public void setLightAttenuation(int i, float f) {
		lights.setAttenuation(i, f);
	}

	public void setLightIntensity(int i, float f, float f1, float f2) {
		lights.setLightIntensity(i, f, f1, f2);
	}

	public float getLightAttenuation(int i) {
		return lights.getAttenuation(i);
	}

	public float getLightDiscardDistance(int i) {
		return lights.discardDistance[i];
	}

	public SimpleVector getLightPosition(int i) {
		return lights.getPosition(i);
	}

	public SimpleVector getLightIntensity(int i) {
		return lights.getIntensity(i);
	}

	public void setAmbientLight(int i, int j, int k) {
		ambientRed = i;
		ambientGreen = j;
		ambientBlue = k;
	}

	public int[] getAmbientLight() {
		return (new int[] { ambientRed, ambientGreen, ambientBlue });
	}

	public void setFogging(int i) {
		useFogging = i == 1;
		if (perPixelFogging)
			if (useFogging)
				fogModeChanged = 1;
			else
				fogModeChanged = 2;
	}

	public void setFoggingMode(int i) {
		if (i == 1) {
			if (useFogging)
				fogModeChanged = 1;
			perPixelFogging = true;
		} else {
			if (perPixelFogging)
				fogModeChanged = 2;
			perPixelFogging = false;
		}
	}

	public int getFogging() {
		return !useFogging ? 0 : 1;
	}

	public int getFoggingMode() {
		return !perPixelFogging ? 2 : 1;
	}

	public void setFogParameters(float f, float f1, float f2, float f3) {
		setFogParameters(-999F, f, f1, f2, f3);
	}

	public void setFogParameters(float f, float f1, float f2, float f3, float f4) {
		if (perPixelFogging && useFogging)
			fogModeChanged = 1;
		if (f != -999F)
			fogStart = f;
		fogDistance = Math.max(f1, 1.0F);
		fogColorR = f2;
		fogColorG = f3;
		fogColorB = f4;
	}

	public void setClippingPlanes(float f, float f1) {
		nearPlane = Math.max(f, 1.0F);
		farPlane = Math.max(f1, 1.0F);
	}

	void setPlanes(boolean flag) {
		if (nearPlane != -1F)
			if (!flag) {
				nearPlaneOld = Config.nearPlane;
				farPlaneOld = Config.farPlane;
				Config.nearPlane = nearPlane;
				Config.farPlane = farPlane;
			} else {
				Config.nearPlane = nearPlaneOld;
				Config.farPlane = farPlaneOld;
			}
	}

	public int checkCollision(SimpleVector simplevector, SimpleVector simplevector1, float f) {
		return checkSomeCollision(simplevector.toArray(), simplevector1.normalize().toArray(), f, null);
	}

	public SimpleVector checkCollisionSpherical(SimpleVector simplevector, SimpleVector simplevector1, float f) {
		return checkSomeCollisionSpherical(simplevector.toArray(), simplevector1.toArray(), f, null);
	}

	public SimpleVector checkCollisionEllipsoid(SimpleVector simplevector, SimpleVector simplevector1,
			SimpleVector simplevector2, int i) {
		if (i < 1)
			i = 1;
		return checkSomeCollisionEllipsoid(simplevector, simplevector1, simplevector2, null, i);
	}

	public boolean checkCameraCollision(int i, float f) {
		return checkCameraCollision(null, i, f, 3F, true);
	}

	public boolean checkCameraCollision(int i, float f, boolean flag) {
		return checkCameraCollision(null, i, f, 3F, flag);
	}

	public boolean checkCameraCollision(int i, float f, float f1, boolean flag) {
		return checkCameraCollision(null, i, f, f1, flag);
	}

	public boolean checkCameraCollision(SimpleVector simplevector, float f, float f1, boolean flag) {
		return checkCameraCollision(simplevector, -1, f, f1, flag);
	}

	public Object[] calcMinDistanceAndObject3D(SimpleVector simplevector, SimpleVector simplevector1, float f) {
		Object obj = null;
		Object3D object3d1 = null;
		float f1 = 3.402823E+038F;
		for (int i = 2; i < objectList.size(); i++) {
			Object3D object3d = objectList.elementAt(i);
			if (!object3d.isPotentialCollider || !object3d.isMainWorld && object3d.oneSectorOnly
					&& Config.useFastCollisionDetection && object3d.hasBoundingBox
					&& object3d.rayIntersectsAABB(simplevector, simplevector1, true) >= f)
				continue;
			float f2 = object3d.calcMinDistance(simplevector, simplevector1, f, false);
			if (f2 < f1) {
				f1 = f2;
				object3d1 = object3d;
			}
		}

		if (f1 != 3.402823E+038F) {
			object3d1.notifyCollisionListeners(0, 0, new Object3D[] { object3d1 });
			object3d1.wasCollider = true;
			return (new Object[] { new Float(f1), object3d1 });
		} else {
			return (new Object[] { new Float(1E+012F), null });
		}
	}

	public float calcMinDistance(SimpleVector simplevector, SimpleVector simplevector1, float f) {
		Object aobj[] = calcMinDistanceAndObject3D(simplevector, simplevector1, f);
		if (aobj != null && aobj.length > 0)
			return ((Float) aobj[0]).floatValue();
		else
			return 1E+012F;
	}

	public boolean checkCameraCollisionSpherical(int i, float f, float f1, boolean flag) {
		return checkCameraCollisionSpherical(null, i, f, f1, flag);
	}

	public boolean checkCameraCollisionSpherical(SimpleVector simplevector, float f, float f1, boolean flag) {
		return checkCameraCollisionSpherical(simplevector, -1, f, f1, flag);
	}

	public boolean checkCameraCollisionEllipsoid(int i, SimpleVector simplevector, float f, int j) {
		if (j < 1)
			j = 1;
		return checkCameraCollisionEllipsoid(null, i, simplevector, f, j);
	}

	public boolean checkCameraCollisionEllipsoid(SimpleVector simplevector, SimpleVector simplevector1, float f, int i) {
		if (i < 1)
			i = 1;
		return checkCameraCollisionEllipsoid(simplevector, -1, simplevector1, f, i);
	}

	public void buildAllObjects() {
		int i = objectList.size();
		for (int j = 2; j < i; j++)
			objectList.elementAt(j).build();

	}

	public void createTriangleStrips() {
		for (int i = 2; i < objectList.size(); i++)
			objectList.elementAt(i).createTriangleStrips();

	}

	public void renderScene(FrameBuffer framebuffer) {
		setPlanes(false);
		boolean flag = framebuffer.usesRenderer(1);
		Object obj = framebuffer;
		if (framebuffer.proxy != null)
			obj = framebuffer.proxy;
		if (Config.useLocking)
			lockMatrices();
		camera.prepareTransform();
		if (Config.useLocking)
			unlockMatrices();
		if (Config.doPortalHsr && mainWorld == -1)
			Logger.log("Can't do portal rendering without a main world being defined!", 0);
		if (vlManager == null)
			vlManager = new VisListManager();
		visList = vlManager.getVisList(framebuffer, visList);
		visList.clearList();
		float f = ((FrameBuffer) (obj)).middleX;
		float f1 = ((FrameBuffer) (obj)).middleY;
		boolean flag1 = false;
		boolean flag2 = false;
		camera.calcFOV(((FrameBuffer) (obj)).width, ((FrameBuffer) (obj)).height);
		if (framebuffer.usesRenderer(2)) {
			Object aobj[] = new Object[2];
			aobj[0] = this;
			aobj[1] = framebuffer;
			framebuffer.glRend.execute(0, aobj);
			if (!framebuffer.usesRenderer(1))
				flag1 = true;
		}
		clippedPolys.clearObject();
		lights.transformLights(camera);
		byte byte0;
		if (!Config.doPortalHsr) {
			byte0 = 2;
		} else {
			byte0 = 1;
			portals.getCurrentSector(getObject(mainWorld), ((BufferedMatrix) (camera)).frontBx,
					((BufferedMatrix) (camera)).frontBy, ((BufferedMatrix) (camera)).frontBz);
		}
		int j = objectList.size();
		boolean flag3 = framebuffer.usesRenderer(2);
		for (int k = byte0; k < j; k++) {
			Object3D object3d = objectList.elementAt(k);
			object3d.object3DRendered = false;
			if (flag3 && object3d.compiled != null && !object3d.isCompiled() && object3d.getMesh().anzTri > 0) {
				if (object3d.shareWith != null) {
					Object3D object3d1 = object3d.shareWith;
					if (object3d1.compiled != null && !object3d1.isCompiled() && object3d1.getMesh().anzTri > 0) {
						Logger.log("Compiling source object...", 2);
						framebuffer.versionHelper.compile(object3d1, framebuffer.glRend);
					}
				}
				framebuffer.versionHelper.compile(object3d, framebuffer.glRend);
				Config.glVertexArrays = true;
			}
			Object obj1 = object3d.getUserObject();
			if (obj1 != null && (obj1 instanceof Overlay))
				((Overlay) obj1).update(framebuffer);
			if (!object3d.isVisible)
				continue;
			int i;
			if (!object3d.oneSectorOnly && object3d.sector != null)
				i = object3d.sector[0];
			else
				i = object3d.singleSectorNumber;
			if (object3d.hasPortals) {
				object3d.transformVertices(framebuffer);
				if (Config.doPortalHsr)
					portals.processPortals(f, f1, camera.scaleX, camera.scaleY);
				continue;
			}
			if (Config.doPortalHsr && !object3d.dynSectorDetect && object3d.oneSectorOnly
					&& !portals.isSectorVisible(object3d, i))
				continue;
			boolean flag4 = object3d.transformVertices(framebuffer);
			if (flag4 || object3d.objMesh.anzTri <= 0 || !object3d.someSectorVisible && object3d.dynSectorDetect)
				continue;
			if (object3d.isTrans && (!object3d.isEnvmapped || !object3d.isBumpmapped))
				flag2 = true;
			object3d.render(flag, f, f1, camera.scaleX, camera.scaleY, camera.divx, camera.divy, flag1);
		}

		if (framebuffer.glRend instanceof GLRenderer)
			visList.fillInstances();
		if (Config.doSorting && !flag1 || flag2 || Config.alwaysSort || Config.glMultiPassSorting) {
			if (Config.glMultiPassSorting)
				visList.splitForMultiPass();
			visList.sort(0, visList.anzpoly, flag);
		}
		if (!Config.glMultiPassSorting)
			visList.splitForMultiPass();
		setPlanes(true);
	}

	public void draw(FrameBuffer framebuffer) {
		draw(framebuffer, false, 0);
	}

	public void draw(FrameBuffer framebuffer, int i, int j) {
		if (i < 0)
			i = 0;
		if (j > visList.anzpoly)
			j = visList.anzpoly;
		draw(framebuffer, false, 0, i, j);
	}

	public void drawWireframe(FrameBuffer framebuffer, Color color) {
		draw(framebuffer, true, color.getRGB());
		framebuffer.expandBoundingBox();
	}

	public long getFrameCounter() {
		return drawCnt;
	}

	public Enumeration getObjects() {
		Enumeration enumeration = objectList.elements();
		if (objectList.size() > 1) {
			enumeration.nextElement();
			enumeration.nextElement();
		}
		return enumeration;
	}

	public void setObjectsVisibility(boolean flag) {
		for (Enumeration enumeration = objectList.elements(); enumeration.hasMoreElements(); ((Object3D) enumeration
				.nextElement()).setVisibility(flag))
			;
	}

	public void removeAll() {
		removeAllLights();
		removeAllObjects();
	}

	public void removeAllObjects() {
		Object3D object3d = objectList.elementAt(0);
		Object3D object3d1 = objectList.elementAt(1);
		for (int i = 0; i < objectList.size(); i++)
			objectList.elementAt(i).myWorld = null;

		objectList = new Object3DList();
		addObject(object3d);
		addObject(object3d1);
		if (!Config.shareVisibilityList)
			visList = new VisList(Config.maxPolysVisible);
	}

	public void removeAllLights() {
		lights = new Lights(Config.maxLights);
	}

	public static synchronized void setDefaultThread(Thread thread) {
		defaultThread = thread;
	}

	public static synchronized Thread getDefaultThread() {
		return defaultThread;
	}

	public String toXML() {
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append("<jpct>\n");
		stringbuffer.append("<light_list>\n");
		Lights lights1 = getLights();
		for (int i = 0; i < lights1.lightCnt; i++)
			stringbuffer.append("<light x='").append(lights1.xOrg[i]).append("' y='").append(lights1.yOrg[i]).append("' z='")
					.append(lights1.zOrg[i]).append("' r='").append(lights1.rOrg[i]).append("' g='").append(lights1.gOrg[i])
					.append("' b='").append(lights1.bOrg[i]).append("'/>\n");

		stringbuffer.append("</light_list>\n");
		stringbuffer.append("<texture_list>\n");
		TextureManager texturemanager = TextureManager.getInstance();
		Enumeration enumeration = texturemanager.getNames();
		do {
			if (!enumeration.hasMoreElements())
				break;
			String s = (String) enumeration.nextElement();
			if (!s.equals("--dummy--"))
				stringbuffer.append("<texture name='").append(s).append("' id='").append(texturemanager.getTextureID(s))
						.append("'/>\n");
		} while (true);
		stringbuffer.append("</texture_list>\n");
		stringbuffer.append("<object_list>\n");
		for (int j = 2; j < objectList.size(); j++) {
			Object3D object3d = objectList.elementAt(j);
			stringbuffer.append("<object name='").append(object3d.getName()).append("' id='").append(j - 2).append(
					"' triangles='").append(object3d.objMesh.anzTri).append("' main='");
			if (object3d.isMainWorld)
				stringbuffer.append("1'");
			else
				stringbuffer.append("0'");
			if (object3d.oneSectorOnly)
				if (object3d.dynSectorDetect)
					stringbuffer.append(" insector='auto'");
				else if (object3d.singleSectorNumber == 0)
					stringbuffer.append(" insector='all'");
				else
					stringbuffer.append(" insector='" + object3d.singleSectorNumber + "'");
			stringbuffer.append(">\n");
			stringbuffer.append("<attributes>\n");
			SimpleVector simplevector = object3d.getRotationPivot();
			SimpleVector simplevector1 = object3d.getCenter();
			SimpleVector simplevector2 = object3d.getOriginMatrix().getTranslation();
			stringbuffer.append("<pivot x='").append(simplevector.x).append("' y='").append(simplevector.y).append("' z='")
					.append(simplevector.z).append("'/>\n");
			stringbuffer.append("<center x='").append(simplevector1.x).append("' y='").append(simplevector1.y)
					.append("' z='").append(simplevector1.z).append("'/>\n");
			stringbuffer.append("<origin x='").append(simplevector2.x).append("' y='").append(simplevector2.y)
					.append("' z='").append(simplevector2.z).append("'/>\n");
			stringbuffer.append("</attributes>\n");
			int j1 = -1;
			for (int k1 = 0; k1 < object3d.objMesh.anzTri; k1++) {
				int l1 = 0;
				if (object3d.sector != null)
					l1 = object3d.sector[k1];
				if (l1 != j1) {
					if (j1 != -1)
						stringbuffer.append("</triangle_list>\n");
					stringbuffer.append("<triangle_list");
					if (!object3d.oneSectorOnly)
						stringbuffer.append(" sector='" + l1 + "'");
					stringbuffer.append(">\n");
					j1 = l1;
				}
				stringbuffer.append("<triangle>\n");
				for (int i2 = 0; i2 < 3; i2++) {
					int k2 = object3d.objMesh.coords[object3d.objMesh.points[k1][i2]];
					float f3 = object3d.objMesh.xOrg[k2];
					float f4 = object3d.objMesh.yOrg[k2];
					float f5 = object3d.objMesh.zOrg[k2];
					float f6 = object3d.objVectors.nuOrg[object3d.objMesh.points[k1][i2]];
					float f7 = object3d.objVectors.nvOrg[object3d.objMesh.points[k1][i2]];
					stringbuffer.append("<coord x='").append(f3).append("' y='").append(f4).append("' z='").append(f5).append(
							"' u='").append(f6).append("' v='").append(f7).append("'/>\n");
				}

				stringbuffer.append("<textures>\n");
				int j2 = object3d.texture[k1];
				if (object3d.texture[k1] != 0)
					stringbuffer.append("<texturemap texid='").append(j2).append("'/>\n");
				if (object3d.basemap[k1] != 0 && object3d.basemap[k1] != j2)
					stringbuffer.append("<basemap texid='").append(object3d.basemap[k1]).append("'/>\n");
				if (object3d.bumpmap != null && object3d.bumpmap[k1] != 0 && object3d.bumpmap[k1] != j2)
					stringbuffer.append("<bumpmap texid='").append(object3d.bumpmap[k1]).append("'/>\n");
				stringbuffer.append("</textures>\n");
				stringbuffer.append("</triangle>\n");
			}

			stringbuffer.append("</triangle_list>\n");
			stringbuffer.append("</object>\n");
		}

		stringbuffer.append("</object_list>\n");
		stringbuffer.append("<portal_list>\n");
		for (int k = 0; k < portals.anzPortals; k++) {
			stringbuffer.append("<portal from='").append(portals.fromSector[k]).append("' to='").append(portals.toSector[k])
					.append("' type='s'>\n");
			for (int l = 0; l < portals.coordCount[k]; l++) {
				int i1 = portals.coords[k][l];
				Object3D object3d1 = objectList.elementAt(1);
				float f = object3d1.objMesh.xOrg[i1];
				float f1 = object3d1.objMesh.yOrg[i1];
				float f2 = object3d1.objMesh.zOrg[i1];
				stringbuffer.append("<coord x='").append(f).append("' y='").append(f1).append("' z='").append(f2).append(
						"'/>\n");
			}

			stringbuffer.append("</portal>\n");
		}

		stringbuffer.append("</portal_list>\n");
		stringbuffer.append("</jpct>\n");
		return stringbuffer.toString();
	}

	Object3D getInternalObjectByName(String s) {
		for (int i = 0; i < objectList.size(); i++)
			if (objectList.elementAt(i).name.equals(s))
				return objectList.elementAt(i);

		return null;
	}

	void rescaleClippedPolys() {
		Logger.log("Enlarging polygon buffer by " + Config.polygonBufferSize, 2);
		clippedPolys.enlarge(Config.polygonBufferSize);
	}

	final SimpleVector checkObjCollisionSpherical(Object3D object3d, SimpleVector simplevector, float f) {
		if (object3d.mayCollide) {
			float af[] = new float[3];
			float af1[] = new float[3];
			object3d.getProjectedPoint(object3d.centerX, object3d.centerY, object3d.centerZ, null, af);
			af1[0] = simplevector.x;
			af1[1] = simplevector.y;
			af1[2] = simplevector.z;
			return checkSomeCollisionSpherical(af, af1, f, object3d);
		} else {
			return new SimpleVector(simplevector.x, simplevector.y, simplevector.z);
		}
	}

	final SimpleVector checkObjCollisionEllipsoid(Object3D object3d, SimpleVector simplevector,
			SimpleVector simplevector1, int i) {
		if (object3d.mayCollide) {
			SimpleVector simplevector2 = new SimpleVector();
			SimpleVector simplevector3 = new SimpleVector(simplevector);
			object3d.getProjectedPoint(object3d.centerX, object3d.centerY, object3d.centerZ, simplevector2, null);
			return checkSomeCollisionEllipsoid(simplevector2, simplevector3, simplevector1, object3d, i);
		} else {
			return new SimpleVector(simplevector);
		}
	}

	final void createWSNormals() {
		if (nxWs == null && lightCacheR != null) {
			int i = lightCacheR.length;
			nxWs = new float[i];
			nyWs = new float[i];
			nzWs = new float[i];
		}
	}

	final int checkObjCollision(Object3D object3d, SimpleVector simplevector, float f) {
		if (object3d.mayCollide) {
			float af[] = new float[3];
			float af1[] = new float[3];
			object3d.getProjectedPoint(object3d.centerX, object3d.centerY, object3d.centerZ, null, af);
			float f1 = simplevector.x;
			float f2 = simplevector.y;
			float f3 = simplevector.z;
			float f4 = 1.0F / (float) Math.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
			af1[0] = f1 * f4;
			af1[1] = f2 * f4;
			af1[2] = f3 * f4;
			return checkSomeCollision(af, af1, f, object3d);
		} else {
			return -100;
		}
	}

	private final void draw(FrameBuffer framebuffer, boolean flag, int i) {
		draw(framebuffer, flag, i, 0, visList.anzpoly);
	}

	private final void draw(FrameBuffer framebuffer, boolean flag, int i, int j, int k) {
		setPlanes(false);
		Object obj = framebuffer;
		if (framebuffer.proxy != null)
			obj = framebuffer.proxy;
		IRenderer irenderer = ((FrameBuffer) (obj)).softRend;
		IRenderer irenderer1 = framebuffer.glRend;
		int l = 0;
		boolean flag1 = irenderer != null;
		boolean flag2 = irenderer1 != null;
		Object obj1 = null;
		boolean flag3 = false;
		boolean flag4 = flag2 && !framebuffer.canvasMode && !Config.useMultipleThreads;
		drawCnt++;
		int i1 = framebuffer.getOutputWidth();
		int j1 = framebuffer.getOutputHeight();
		if (useFogging && perPixelFogging)
			framebuffer.useFogging(fogDistance, fogColorR, fogColorG, fogColorB);
		else
			framebuffer.dontUseFogging();
		if (j == 0) {
			if (flag1)
				irenderer.execute(12, null);
			if (flag4)
				irenderer1.execute(12, null);
		}
		if (flag2)
			irenderer1.execute(23, null);
		if (visList.anzpoly != -1) {
			if (flag2 && (Config.viewportOffsetX != 0.0F || Config.viewportOffsetY != 0.0F)) {
				Object aobj[] = new Object[4];
				aobj[0] = IntegerC.valueOf((int) (Config.viewportOffsetX * (float) i1));
				aobj[1] = IntegerC.valueOf((int) (-Config.viewportOffsetY * (float) j1));
				aobj[2] = IntegerC.valueOf(i1);
				aobj[3] = IntegerC.valueOf(j1);
				framebuffer.glRend.execute(9, aobj);
				flag3 = true;
			}
			if (flag) {
				for (int k1 = j; k1 <= k; k1++) {
					if (flag2)
						irenderer1.drawWireframe(visList, k1, i, framebuffer, this);
					if (flag1)
						irenderer.drawWireframe(visList, k1, i, ((FrameBuffer) (obj)), this);
				}

			} else {
				if (!Config.glTriangleStrips || flag1 || Config.glVertexArrays) {
					if (Config.glVertexArrays || flag1 && Config.useMultipleThreads) {
						if (flag2)
							irenderer1.drawVertexArray(visList, j, k, framebuffer, this);
						if (flag1)
							irenderer.drawVertexArray(visList, j, k, ((FrameBuffer) (obj)), this);
					} else {
						for (int l1 = j; l1 <= k; l1++) {
							if (flag2)
								irenderer1.drawPolygon(visList, l1, framebuffer, this);
							if (flag1)
								irenderer.drawPolygon(visList, l1, ((FrameBuffer) (obj)), this);
						}

					}
				} else {
					for (int i2 = j; i2 <= k; i2++) {
						int j2 = 0;
						boolean flag5 = true;
						do {
							int k2 = i2 + j2;
							int l2 = k2 + 1;
							Object3D object3d = visList.vorg[k2];
							if (visList.stageCnt[k2] != visList.stageCnt[l2] || visList.stageCnt[k2] >= Config.glStageCount
									|| object3d.objVectors.alpha != null)
								flag5 = false;
							else if (k2 < visList.anzpoly && object3d == visList.vorg[l2]
									&& object3d.texture[visList.vnum[k2]] == object3d.texture[visList.vnum[l2]] && !object3d.isFlatShaded) {
								Mesh mesh = object3d.objMesh;
								int i3 = visList.vnumOrg[k2];
								int j3 = visList.vnumOrg[l2];
								int k3 = mesh.points[i3][0];
								int l3 = mesh.points[i3][1];
								int i4 = mesh.points[i3][2];
								int j4 = mesh.points[j3][0];
								int k4 = mesh.points[j3][1];
								int l4 = mesh.coords[k3];
								int i5 = mesh.coords[l3];
								int j5 = mesh.coords[i4];
								int k5 = mesh.coords[j4];
								int l5 = mesh.coords[k4];
								float f;
								float f2;
								float f4;
								float f7;
								float f10;
								float f12;
								float f14;
								float f16;
								float f18;
								float f20;
								if (!object3d.isEnvmapped || object3d.isBlended) {
									f = object3d.objVectors.nuOrg[k3];
									f2 = object3d.objVectors.nvOrg[k3];
									f4 = object3d.objVectors.nuOrg[l3];
									f7 = object3d.objVectors.nvOrg[l3];
									f10 = object3d.objVectors.nuOrg[i4];
									f12 = object3d.objVectors.nvOrg[i4];
									f14 = object3d.objVectors.nuOrg[j4];
									f16 = object3d.objVectors.nvOrg[j4];
									f18 = object3d.objVectors.nuOrg[k4];
									f20 = object3d.objVectors.nvOrg[k4];
								} else {
									f = 0.0F;
									f2 = 0.0F;
									f4 = 0.0F;
									f7 = 0.0F;
									f10 = 0.0F;
									f12 = 0.0F;
									f14 = 0.0F;
									f16 = 0.0F;
									f18 = 0.0F;
									f20 = 0.0F;
								}
								if ((j2 & 1) == 0 && i5 == l5 && j5 == k5 && f4 == f18 && f7 == f20 && f10 == f14 && f12 == f16) {
									if (object3d.usesMultiTexturing) {
										boolean flag6 = true;
										for (int i6 = 0; i6 < object3d.maxStagesUsed - 1; i6++) {
											int k6 = object3d.multiTex[i6][visList.vnum[k2]];
											int i7 = object3d.multiTex[i6][visList.vnum[l2]];
											int k7 = object3d.multiMode[i6][visList.vnum[k2]];
											int i8 = object3d.multiMode[i6][visList.vnum[l2]];
											if (k6 == i7 && k7 == i8) {
												if (k6 == -1)
													continue;
												f = object3d.objVectors.uMul[i6][k3];
												f2 = object3d.objVectors.vMul[i6][k3];
												float f5 = object3d.objVectors.uMul[i6][l3];
												float f8 = object3d.objVectors.vMul[i6][l3];
												f10 = object3d.objVectors.uMul[i6][i4];
												f12 = object3d.objVectors.vMul[i6][i4];
												f14 = object3d.objVectors.uMul[i6][j4];
												f16 = object3d.objVectors.vMul[i6][j4];
												f18 = object3d.objVectors.uMul[i6][k4];
												f20 = object3d.objVectors.vMul[i6][k4];
												if (f5 == f18 && f8 == f20 && f10 == f14 && f12 == f16)
													continue;
												flag6 = false;
											} else {
												flag6 = false;
											}
											break;
										}

										if (flag6)
											j2++;
										else
											flag5 = false;
									} else {
										j2++;
									}
								} else if ((j2 & 1) == 1 && l4 == k5 && j5 == l5 && f == f14 && f2 == f16 && f10 == f18 && f12 == f20) {
									if (object3d.usesMultiTexturing) {
										boolean flag7 = true;
										for (int j6 = 0; j6 < object3d.maxStagesUsed - 1; j6++) {
											int l6 = object3d.multiTex[j6][visList.vnum[k2]];
											int j7 = object3d.multiTex[j6][visList.vnum[l2]];
											int l7 = object3d.multiMode[j6][visList.vnum[k2]];
											int j8 = object3d.multiMode[j6][visList.vnum[l2]];
											if (l6 == j7 && l7 == j8) {
												if (l6 == -1)
													continue;
												float f1 = object3d.objVectors.uMul[j6][k3];
												float f3 = object3d.objVectors.vMul[j6][k3];
												float f6 = object3d.objVectors.uMul[j6][l3];
												float f9 = object3d.objVectors.vMul[j6][l3];
												float f11 = object3d.objVectors.uMul[j6][i4];
												float f13 = object3d.objVectors.vMul[j6][i4];
												float f15 = object3d.objVectors.uMul[j6][j4];
												float f17 = object3d.objVectors.vMul[j6][j4];
												float f19 = object3d.objVectors.uMul[j6][k4];
												float f21 = object3d.objVectors.vMul[j6][k4];
												if (f1 == f15 && f3 == f17 && f11 == f19 && f13 == f21)
													continue;
												flag7 = false;
											} else {
												flag7 = false;
											}
											break;
										}

										if (flag7)
											j2++;
										else
											flag5 = false;
									} else {
										j2++;
									}
								} else {
									flag5 = false;
								}
							} else {
								flag5 = false;
							}
						} while (flag5);
						if (j2 == 0) {
							irenderer1.drawPolygon(visList, i2, framebuffer, this);
						} else {
							l++;
							irenderer1.drawStrip(visList, i2, j2, framebuffer, this);
							i2 += j2;
						}
					}

				}
				if (flag2) {
					irenderer1.execute(8, null);
					if (flag3) {
						Object aobj1[] = new Object[4];
						aobj1[0] = IntegerC.valueOf(0);
						aobj1[1] = IntegerC.valueOf(0);
						aobj1[2] = IntegerC.valueOf(i1);
						aobj1[3] = IntegerC.valueOf(j1);
						framebuffer.glRend.execute(9, aobj1);
					}
				}
			}
		}
		if (k == visList.anzpoly) {
			if (flag1)
				irenderer.execute(13, null);
			if (flag4)
				irenderer1.execute(13, null);
		}
		setPlanes(true);
	}

	private final boolean checkCameraCollision(SimpleVector simplevector, int i, float f, float f1, boolean flag) {
		if (i == 7)
			i = 1;
		float f2 = 3.402823E+038F;
		float f3 = 3.402823E+038F;
		float f4 = 3.402823E+038F;
		float f5 = 0.0F;
		float f6 = 0.0F;
		float f7 = 0.0F;
		float f8 = 1.0F;
		float f9 = 0.0F;
		float f10 = 0.0F;
		float f11 = 0.0F;
		float af[] = new float[3];
		float af1[] = new float[3];
		float af2[] = new float[3];
		float af3[] = new float[3];
		float f12 = ((BufferedMatrix) (camera)).backBx;
		float f13 = ((BufferedMatrix) (camera)).backBy;
		float f14 = ((BufferedMatrix) (camera)).backBz;
		boolean flag1 = false;
		af[0] = f12;
		af[1] = f13;
		af[2] = f14;
		if (simplevector == null) {
			if ((i & 1) != 1)
				f8 = -1F;
			int j = 2 - ((i + 1) / 2 - 1);
			f9 = ((BufferedMatrix) (camera)).backMatrix.mat[0][j];
			f10 = ((BufferedMatrix) (camera)).backMatrix.mat[1][j];
			f11 = ((BufferedMatrix) (camera)).backMatrix.mat[2][j];
			f5 = f9 * f8;
			f6 = f10 * f8;
			f7 = f11 * f8;
		} else {
			f8 = 1.0F;
			f9 = simplevector.x;
			f10 = simplevector.y;
			f11 = simplevector.z;
			f5 = f9;
			f6 = f10;
			f7 = f11;
		}
		float af4[] = { f5, f6, f7 };
		af1[0] = f5;
		af1[1] = 0.0F;
		af1[2] = 0.0F;
		af2[0] = 0.0F;
		af2[1] = f6;
		af2[2] = 0.0F;
		af3[0] = 0.0F;
		af3[1] = 0.0F;
		af3[2] = f7;
		if (af1[0] < 0.0F)
			af1[0] = -1F;
		else
			af1[0] = 1.0F;
		if (af2[1] < 0.0F)
			af2[1] = -1F;
		else
			af2[1] = 1.0F;
		if (af3[2] < 0.0F)
			af3[2] = -1F;
		else
			af3[2] = 1.0F;
		boolean flag2 = true;
		boolean flag3 = true;
		boolean flag4 = true;
		boolean flag5 = false;
		float f15 = 1E+012F;
		Object3D object3d = null;
		Object3D object3d1 = null;
		Object3D object3d2 = null;
		for (int k = 2; k < objectList.size(); k++) {
			Object3D object3d3 = objectList.elementAt(k);
			object3d3.wasCollider = false;
			object3d3.resetPolygonIDCount();
			if (!object3d3.isPotentialCollider || !object3d3.isVisible || !object3d3.isMainWorld && object3d3.oneSectorOnly
					&& Config.useFastCollisionDetection && object3d3.rayIntersectsAABB(af, af4, false) >= Config.collideOffset)
				continue;
			float f16 = object3d3.collide(af, af1, 3F, Config.collideSectorOffset);
			if (f16 < f2) {
				f2 = f16;
				object3d = object3d3;
			}
			f16 = object3d3.collide(af, af2, 3F, Config.collideSectorOffset);
			if (f16 < f3) {
				f3 = f16;
				object3d1 = object3d3;
			}
			f16 = object3d3.collide(af, af3, 3F, Config.collideSectorOffset);
			if (f16 < f4) {
				f4 = f16;
				object3d2 = object3d3;
			}
		}

		float f17 = f1 * 0.9F;
		if (f2 < f1 && f2 > f17)
			f2 = f1;
		if (f3 < f1 && f3 > f17)
			f3 = f1;
		if (f4 < f1 && f4 > f17)
			f4 = f1;
		float f18 = f9 * f * f8;
		float f19 = Math.abs(f18);
		if (f2 - f19 <= f1) {
			flag5 = true;
			if (f18 >= 0.0F)
				f18 = f2 - f1;
			else
				f18 = -(f2 - f1);
			if (object3d != null)
				object3d.wasCollider = true;
		} else {
			flag2 = false;
		}
		float f20 = f10 * f * f8;
		f19 = Math.abs(f20);
		if (f3 - f19 <= f1) {
			flag5 = true;
			if (f20 >= 0.0F)
				f20 = f3 - f1;
			else
				f20 = -(f3 - f1);
			if (object3d1 != null)
				object3d1.wasCollider = true;
		} else {
			flag3 = false;
		}
		float f21 = f11 * f * f8;
		f19 = Math.abs(f21);
		if (f4 - f19 <= f1) {
			flag5 = true;
			if (f21 >= 0.0F)
				f21 = f4 - f1;
			else
				f21 = -(f4 - f1);
			if (object3d2 != null)
				object3d2.wasCollider = true;
		} else {
			flag4 = false;
		}
		if (object3d != null && object3d.wasCollider)
			object3d.notifyCollisionListeners(0, 0, new Object3D[] { object3d });
		if (object3d1 != null && object3d1.wasCollider && !object3d1.equals(object3d))
			object3d1.notifyCollisionListeners(0, 0, new Object3D[] { object3d1 });
		if (object3d2 != null && object3d2.wasCollider && !object3d2.equals(object3d) && !object3d2.equals(object3d1))
			object3d2.notifyCollisionListeners(0, 0, new Object3D[] { object3d2 });
		if (flag || !flag5) {
			camera.backBx += f18;
			camera.backBy += f20;
			camera.backBz += f21;
		}
		return flag2 | flag3 | flag4;
	}

	private final int checkSomeCollision(float af[], float af1[], float f, Object3D object3d) {
		Object obj = null;
		Object3D object3d2 = null;
		float f1 = 3.402823E+038F;
		float f2 = 1E+012F;
		for (int i = 2; i < objectList.size(); i++) {
			Object3D object3d1 = objectList.elementAt(i);
			object3d1.wasCollider = false;
			object3d1.resetPolygonIDCount();
			if (!object3d1.isPotentialCollider || object3d != null && object3d1 == object3d || !object3d1.isVisible
					|| !object3d1.isMainWorld && object3d1.oneSectorOnly && Config.useFastCollisionDetection
					&& object3d1.rayIntersectsAABB(af, af1, true) >= Config.collideOffset)
				continue;
			float f3 = object3d1.collide(af, af1, f, Config.collideSectorOffset);
			if (f3 >= f1)
				continue;
			f1 = f3;
			if (f1 < f)
				object3d2 = object3d1;
		}

		if (f1 < f && object3d2 != null) {
			object3d2.wasCollider = true;
			for (int j = 2; j < objectList.size(); j++) {
				Object3D object3d3 = objectList.elementAt(j);
				if (object3d3 != object3d2) {
					object3d3.wasCollider = false;
					object3d3.resetPolygonIDCount();
				}
			}

			Object3D aobject3d[] = { object3d2 };
			if (object3d != null)
				object3d.notifyCollisionListeners(1, 0, aobject3d);
			object3d2.notifyCollisionListeners(object3d, 0, 0, aobject3d);
			return object3d2.getID();
		} else {
			return -100;
		}
	}

	private final SimpleVector checkSomeCollisionSpherical(float af[], float af1[], float f, Object3D object3d) {
		Object obj = null;
		float af2[] = new float[3];
		af2[0] = af[0] + af1[0];
		af2[1] = af[1] + af1[1];
		af2[2] = af[2] + af1[2];
		boolean aflag[] = { false };
		for (int i = 2; i < objectList.size(); i++) {
			Object3D object3d1 = objectList.elementAt(i);
			object3d1.wasCollider = false;
			object3d1.resetPolygonIDCount();
			if (!object3d1.isPotentialCollider || object3d != null && object3d1 == object3d || !object3d1.isVisible
					|| !object3d1.isMainWorld && object3d1.oneSectorOnly && object3d1.hasBoundingBox
					&& !object3d1.sphereIntersectsAABB(af2, f))
				continue;
			boolean flag = aflag[0];
			aflag[0] = false;
			af2 = object3d1.collideSpherical(af2, f, Config.collideSectorOffset, aflag, false);
			if (aflag[0])
				object3d1.wasCollider = true;
			aflag[0] |= flag;
		}

		af2[0] -= af[0];
		af2[1] -= af[1];
		af2[2] -= af[2];
		if (aflag[0]) {
			notifyAll(object3d, null, 1);
			return new SimpleVector(af2[0], af2[1], af2[2]);
		} else {
			return new SimpleVector(af1[0], af1[1], af1[2]);
		}
	}

	private final boolean checkCameraCollisionSpherical(SimpleVector simplevector, int i, float f, float f1, boolean flag) {
		float af[] = { ((BufferedMatrix) (camera)).backBx, ((BufferedMatrix) (camera)).backBy,
				((BufferedMatrix) (camera)).backBz };
		if (simplevector == null) {
			if (i != 7) {
				float f2 = -1F;
				if ((i & 1) == 1)
					f2 = 1.0F;
				f2 *= f1;
				int j = 2 - ((i + 1) / 2 - 1);
				af[0] += ((BufferedMatrix) (camera)).backMatrix.mat[0][j] * f2;
				af[1] += ((BufferedMatrix) (camera)).backMatrix.mat[1][j] * f2;
				af[2] += ((BufferedMatrix) (camera)).backMatrix.mat[2][j] * f2;
			}
		} else {
			af[0] += simplevector.x * f1;
			af[1] += simplevector.y * f1;
			af[2] += simplevector.z * f1;
		}
		boolean aflag[] = { false };
		for (int k = 2; k < objectList.size(); k++) {
			Object3D object3d = objectList.elementAt(k);
			object3d.wasCollider = false;
			object3d.resetPolygonIDCount();
			if (!object3d.isPotentialCollider || !object3d.isVisible || !object3d.isMainWorld && object3d.oneSectorOnly
					&& object3d.hasBoundingBox && !object3d.sphereIntersectsAABB(af, f))
				continue;
			boolean flag2 = aflag[0];
			aflag[0] = false;
			af = object3d.collideSpherical(af, f, Config.collideSectorOffset, aflag, true);
			if (aflag[0])
				object3d.wasCollider = true;
			aflag[0] |= flag2;
		}

		boolean flag1 = aflag[0];
		if (flag || !flag1) {
			camera.backBx = af[0];
			camera.backBy = af[1];
			camera.backBz = af[2];
		}
		if (flag1)
			notifyAll(null, null, 1);
		return flag1;
	}

	private final SimpleVector checkSomeCollisionEllipsoid(SimpleVector simplevector, SimpleVector simplevector1,
			SimpleVector simplevector2, Object3D object3d, int i) {
		CollisionInfo collisioninfo = new CollisionInfo();
		collisioninfo.eRadius = simplevector2;
		collisioninfo.r3Pos = new SimpleVector(simplevector);
		collisioninfo.r3Velocity = new SimpleVector(simplevector1);
		collisioninfo.calculateInverseAndDest();
		if (object3d != null && object3d.getEllipsoidMode() == 1) {
			collisioninfo.addTransMat = object3d.getWorldTransformation();
			Matrix matrix = collisioninfo.addTransMat.cloneMatrix();
			matrix.mat[3][0] = 0.0F;
			matrix.mat[3][1] = 0.0F;
			matrix.mat[3][2] = 0.0F;
			collisioninfo.addRotMat = matrix;
		}
		doWorldCollisionEllipsoid(collisioninfo, 0, object3d, i);
		if (Config.collideEllipsoidSmoothing) {
			SimpleVector simplevector3 = collisioninfo.r3Pos.calcSub(simplevector);
			simplevector3.makeEqualLength(simplevector1);
			simplevector3.add(simplevector);
			collisioninfo.r3Pos = simplevector3;
		}
		SimpleVector simplevector4 = collisioninfo.r3Pos;
		if (collisioninfo.collision) {
			simplevector4.x -= simplevector.x;
			simplevector4.y -= simplevector.y;
			simplevector4.z -= simplevector.z;
			return simplevector4;
		} else {
			return simplevector1;
		}
	}

	private final boolean checkCameraCollisionEllipsoid(SimpleVector simplevector, int i, SimpleVector simplevector1,
			float f, int j) {
		float af[] = { ((BufferedMatrix) (camera)).backBx, ((BufferedMatrix) (camera)).backBy,
				((BufferedMatrix) (camera)).backBz };
		float af1[] = new float[3];
		SimpleVector simplevector2 = null;
		if (simplevector == null) {
			if (i != 7) {
				float f1 = -1F;
				if ((i & 1) == 1)
					f1 = 1.0F;
				f1 *= f;
				int k = 2 - ((i + 1) / 2 - 1);
				af1[0] = ((BufferedMatrix) (camera)).backMatrix.mat[0][k] * f1;
				af1[1] = ((BufferedMatrix) (camera)).backMatrix.mat[1][k] * f1;
				af1[2] = ((BufferedMatrix) (camera)).backMatrix.mat[2][k] * f1;
			}
		} else {
			af1[0] = simplevector.x * f;
			af1[1] = simplevector.y * f;
			af1[2] = simplevector.z * f;
		}
		CollisionInfo collisioninfo = new CollisionInfo();
		collisioninfo.eRadius = simplevector1;
		collisioninfo.r3Pos = new SimpleVector(af);
		collisioninfo.r3Velocity = new SimpleVector(af1);
		collisioninfo.calculateInverseAndDest();
		if (camera.getEllipsoidMode() == 1) {
			Matrix matrix = new Matrix();
			matrix.mat[3][0] = ((BufferedMatrix) (camera)).backBx;
			matrix.mat[3][1] = ((BufferedMatrix) (camera)).backBy;
			matrix.mat[3][2] = ((BufferedMatrix) (camera)).backBz;
			Matrix matrix1 = ((BufferedMatrix) (camera)).backMatrix.cloneMatrix();
			matrix1.matMul(matrix);
			collisioninfo.addTransMat = matrix1;
			collisioninfo.addRotMat = ((BufferedMatrix) (camera)).backMatrix.cloneMatrix();
		}
		if (Config.collideEllipsoidSmoothing)
			simplevector2 = new SimpleVector(collisioninfo.r3Pos);
		doWorldCollisionEllipsoid(collisioninfo, 0, null, j);
		if (Config.collideEllipsoidSmoothing) {
			SimpleVector simplevector3 = collisioninfo.r3Pos.calcSub(simplevector2);
			simplevector3.makeEqualLength(new SimpleVector(af1));
			simplevector3.add(simplevector2);
			collisioninfo.r3Pos = simplevector3;
		}
		camera.backBx = collisioninfo.r3Pos.x;
		camera.backBy = collisioninfo.r3Pos.y;
		camera.backBz = collisioninfo.r3Pos.z;
		return collisioninfo.collision;
	}

	private final void doWorldCollisionEllipsoid(CollisionInfo collisioninfo, int i, Object3D object3d, int j) {
		float f = Config.collideEllipsoidThreshold;
		double d = f * f;
		boolean flag = false;
		if (i >= j)
			return;
		collisioninfo.foundCollision = false;
		collisioninfo.intersectionPoint = new SimpleVector();
		collisioninfo.nearestDistance = 1E+011F;
		if (i == 0)
			collisioninfo.collision = false;
		SimpleVector simplevector = collisioninfo.r3Velocity;
		float f1 = simplevector.x * simplevector.x + simplevector.y * simplevector.y + simplevector.z * simplevector.z;
		SimpleVector simplevector1 = null;
		float f2 = collisioninfo.eRadius.x;
		if (f2 > collisioninfo.eRadius.y)
			f2 = collisioninfo.eRadius.y;
		if (f2 > collisioninfo.eRadius.z)
			f2 = collisioninfo.eRadius.z;
		f2 *= 2.0F;
		if (f1 > f2 * f2) {
			simplevector = new SimpleVector(simplevector);
			simplevector.x = Math.abs(simplevector.x);
			simplevector.y = Math.abs(simplevector.y);
			simplevector.z = Math.abs(simplevector.z);
			simplevector.add(collisioninfo.eRadius);
			simplevector1 = new SimpleVector(collisioninfo.r3Velocity);
			simplevector1.scalarMul(0.5F);
			simplevector1.add(collisioninfo.r3Pos);
		}
		for (int k = 2; k < objectList.size(); k++) {
			Object3D object3d1 = objectList.elementAt(k);
			boolean flag1 = object3d1.getLazyTransformationState();
			if (!flag1)
				object3d1.enableLazyTransformations();
			if (i == 0) {
				object3d1.wasCollider = false;
				object3d1.resetPolygonIDCount();
			}
			if (object3d1.isPotentialCollider
					&& (object3d == null || object3d1 != object3d)
					&& object3d1.isVisible
					&& (object3d1.isMainWorld || !object3d1.oneSectorOnly || !object3d1.hasBoundingBox || simplevector1 != null
							&& object3d1.ellipsoidIntersectsAABB(simplevector1, simplevector) || simplevector1 == null
							&& (object3d1.ellipsoidIntersectsAABB(collisioninfo.r3Pos, collisioninfo.eRadius) || object3d1
									.ellipsoidIntersectsAABB(collisioninfo.r3Dest, collisioninfo.eRadius)))) {
				collisioninfo.isPartOfCollision = false;
				object3d1.collideEllipsoid(collisioninfo, Config.collideSectorOffset);
				object3d1.wasCollider |= collisioninfo.isPartOfCollision;
				flag |= collisioninfo.isPartOfCollision;
			}
			if (!flag1)
				object3d1.disableLazyTransformations();
		}

		if (!flag) {
			collisioninfo.r3Pos.add(collisioninfo.r3Velocity);
			return;
		}
		SimpleVector simplevector2 = new SimpleVector(collisioninfo.eSpaceBasePoint);
		simplevector2.add(collisioninfo.eSpaceVelocity);
		SimpleVector simplevector3 = new SimpleVector(collisioninfo.r3Pos);
		SimpleVector simplevector4 = new SimpleVector(collisioninfo.eSpaceBasePoint);
		if (collisioninfo.nearestDistance >= f || collisioninfo.nearestDistance <= 0.0F) {
			SimpleVector simplevector5 = new SimpleVector(collisioninfo.r3Velocity);
			simplevector5.scalarMul(collisioninfo.nearestDistance - f);
			simplevector3.add(simplevector5);
			simplevector5 = new SimpleVector(collisioninfo.eSpaceVelocity);
			simplevector5.scalarMul(collisioninfo.nearestDistance - f);
			simplevector4.add(simplevector5);
			simplevector5 = collisioninfo.eSpaceVelocity.normalize();
			collisioninfo.intersectionPoint = collisioninfo.intersectionPoint.calcSub(new SimpleVector(f * simplevector5.x, f
					* simplevector5.y, f * simplevector5.z));
		}
		SimpleVector simplevector6 = collisioninfo.intersectionPoint;
		SimpleVector simplevector7 = simplevector4.calcSub(collisioninfo.intersectionPoint);
		simplevector7 = simplevector7.normalize();
		Plane plane = new Plane(simplevector6, simplevector7);
		simplevector7.scalarMul(plane.distanceTo(simplevector2));
		SimpleVector simplevector8 = simplevector2.calcSub(simplevector7);
		SimpleVector simplevector9 = simplevector8.calcSub(collisioninfo.intersectionPoint);
		double d1 = simplevector9.x * simplevector9.x + simplevector9.y * simplevector9.y + simplevector9.z
				* simplevector9.z;
		simplevector9.x *= collisioninfo.eRadius.x;
		simplevector9.y *= collisioninfo.eRadius.y;
		simplevector9.z *= collisioninfo.eRadius.z;
		if (collisioninfo.addTransMat == null)
			simplevector9 = collisioninfo.collisionObject.reverseTransform(simplevector9, false);
		else
			simplevector9.matMul(collisioninfo.addRotMat);
		collisioninfo.r3Pos = simplevector3;
		collisioninfo.r3Velocity = simplevector9;
		collisioninfo.recalcDest();
		if (d1 >= d)
			doWorldCollisionEllipsoid(collisioninfo, i + 1, object3d, j);
		if (i == 0)
			notifyAll(object3d, collisioninfo, 2);
	}

	private void notifyAll(Object3D object3d, CollisionInfo collisioninfo, int i) {
		if ((collisioninfo == null || collisioninfo.collision) && Object3D.globalListenerCount > 0) {
			if (targets == null)
				targets = new Object3DList(10);
			else
				targets.clear();
			for (int j = 2; j < objectList.size(); j++) {
				Object3D object3d1 = objectList.elementAt(j);
				if (object3d1.wasCollider)
					targets.addElement(object3d1);
			}

			Object3D aobject3d[] = targets.toArray();
			if (object3d != null)
				object3d.notifyCollisionListeners(1, i, aobject3d);
			for (int k = 0; k < targets.size(); k++) {
				Object3D object3d2 = targets.elementAt(k);
				object3d2.notifyCollisionListeners(object3d, 0, i, aobject3d);
			}

		}
	}

	protected void finalize() {
		if (!disposed)
			dispose();
	}

	private static final long serialVersionUID = 4L;
	public static final int MAIN_OBJECT_NOT_SET = -1;
	public static final int FOGGING_DISABLED = 0;
	public static final int FOGGING_ENABLED = 1;
	public static final int FOGGING_PER_VERTEX = 2;
	public static final int FOGGING_PER_PIXEL = 1;
	private static final Boolean TRUE_LOCK;
	private static final Boolean FALSE_LOCK;
	private static VisList staticVisList = null;
	private static Object3D staticClippedPolys = null;
	static Thread defaultThread = null;
	float lightCacheR[];
	float lightCacheG[];
	float lightCacheB[];
	float nxTr[];
	float nyTr[];
	float nzTr[];
	transient float nxWs[];
	transient float nyWs[];
	transient float nzWs[];
	Object3DList objectList;
	VisList visList;
	Camera camera;
	Lights lights;
	Portals portals;
	Object3D clippedPolys;
	long drawCnt;
	int mainWorld;
	boolean useFogging;
	int fogModeChanged;
	boolean perPixelFogging;
	float fogStart;
	float fogDistance;
	float fogColorR;
	float fogColorG;
	float fogColorB;
	int ambientRed;
	int ambientGreen;
	int ambientBlue;
	private Boolean lock;
	private int wasLocked;
	private transient VisListManager vlManager;
	private transient Object3DList targets;
	private boolean disposed;
	private float nearPlane;
	private float farPlane;
	private float nearPlaneOld;
	private float farPlaneOld;

	static {
		TRUE_LOCK = Boolean.TRUE;
		FALSE_LOCK = Boolean.FALSE;
	}
}
