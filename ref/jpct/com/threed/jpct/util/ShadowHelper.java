// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.threed.jpct.util;

import com.threed.jpct.*;
import java.awt.Color;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

public class ShadowHelper implements Serializable {

	public ShadowHelper(World world1, FrameBuffer framebuffer, Projector projector, int i) {
		map = null;
		inverts = null;
		lightsOff = null;
		visible = null;
		addColor = null;
		recMap = new Hashtable();
		casMap = new Hashtable();
		world = null;
		buffer = null;
		tid = 0;
		proj = null;
		disableLights = true;
		col = new Color(60, 60, 60);
		name = "";
		revert = true;
		invertCulling = true;
		disableAddColor = false;
		border = 1;
		rendersShadowMap = false;
		rendersShadowPass = false;
		if (!framebuffer.supports("GL_ARB_shadow")) {
			Logger.log("Shadow mapping is not supported by the current hardware!", 0);
			return;
		}
		if (i != 8192 && i != 4096 && i != 2048 && i != 1024 && i != 512 && i != 256 && i != 128 && i != 64 && i != 32
				&& i != 16) {
			i = 256;
			Logger.log("Consider using a power of two as the shadow map's size. Adjusted to 256*256!", 1);
		}
		int j = framebuffer.getMaxTextureSize();
		Logger.log("Hardware supports textures up to " + j + "*" + j + " in size!", 2);
		i = Math.min(i, j);
		world = world1;
		buffer = framebuffer;
		proj = projector;
		int k = framebuffer.getOutputHeight();
		int l = framebuffer.getOutputWidth();
		int i1 = i;
		int j1 = i;
		if (!Config.glUseFBO) {
			for (; i1 > l; i1 /= 2)
				;
			for (; j1 > k; j1 /= 2)
				;
		}
		map = new Texture(i1, j1, null);
		name = "--shadowMap-" + cnt + "-";
		TextureManager texturemanager = TextureManager.getInstance();
		if (texturemanager.containsTexture(name))
			texturemanager.replaceTexture(name, map);
		else
			texturemanager.addTexture(name, map);
		tid = texturemanager.getTextureID(name);
		cnt++;
		map.setGLFiltering(false);
		map.setMipmap(false);
		map.setProjector(proj, true);
		map.setAsShadowMap(true);
	}

	public static void resetTextureCounter() {
		cnt = 0;
	}

	public Projector getLightSource() {
		return proj;
	}

	public void setLightSource(Projector projector) {
		if (map == null) {
			return;
		} else {
			proj = projector;
			map.setProjector(proj, true);
			return;
		}
	}

	public void setBorder(int i) {
		border = i;
	}

	public void setFrameBuffer(FrameBuffer framebuffer) {
		buffer = framebuffer;
	}

	public void setRevertMode(boolean flag) {
		revert = flag;
	}

	public void setCullingMode(boolean flag) {
		invertCulling = flag;
	}

	public void setFiltering(boolean flag) {
		if (map == null) {
			return;
		} else {
			map.setGLFiltering(flag);
			return;
		}
	}

	public void addReceiver(Object3D object3d) {
		if (map == null)
			return;
		if (!recMap.containsKey(object3d)) {
			recMap.put(object3d, object3d);
			PolygonManager polygonmanager = object3d.getPolygonManager();
			int i = polygonmanager.getMaxPolygonID();
			for (int j = 0; j < i; j++)
				polygonmanager.addTexture(j, tid, 1);

		}
	}

	public boolean isInitialized() {
		return world != null;
	}

	public void addCaster(Object3D object3d) {
		if (map == null)
			return;
		if (!casMap.containsKey(object3d))
			casMap.put(object3d, object3d);
	}

	public void removeCaster(Object3D object3d) {
		if (map == null) {
			return;
		} else {
			casMap.remove(object3d);
			return;
		}
	}

	public void setLightMode(boolean flag) {
		disableLights = flag;
	}

	public void setAdditionalColorMode(boolean flag) {
		disableAddColor = flag;
	}

	public void setAmbientLight(Color color) {
		col = color;
	}

	public void drawScene() {
		drawScene(true);
	}

	public synchronized void drawScene(boolean flag) {
		if (world == null)
			return;
		rendersShadowPass = true;
		buffer.setPaintListenerState(false);
		checkArrays();
		int i = 0;
		for (Enumeration enumeration = world.getObjects(); enumeration.hasMoreElements();) {
			Object3D object3d = (Object3D) enumeration.nextElement();
			lightsOff[i] = object3d.getLighting();
			visible[i] = object3d.getVisibility();
			addColor[i] = object3d.getAdditionalColor();
			if (disableLights)
				object3d.setLighting(1);
			if (disableAddColor)
				object3d.setAdditionalColor(Color.black);
			if (!recMap.containsKey(object3d))
				object3d.setVisibility(false);
			i++;
		}

		map.setEnabled(false);
		int ai[] = world.getAmbientLight();
		world.setAmbientLight(col.getRed(), col.getRed(), col.getBlue());
		boolean flag1 = Config.glRevertADDtoMODULATE;
		Config.glRevertADDtoMODULATE = revert;
		world.renderScene(buffer);
		world.draw(buffer);
		Config.glRevertADDtoMODULATE = flag1;
		map.setEnabled(true);
		i = 0;
		for (Enumeration enumeration1 = world.getObjects(); enumeration1.hasMoreElements();) {
			Object3D object3d1 = (Object3D) enumeration1.nextElement();
			object3d1.setLighting(lightsOff[i]);
			object3d1.setVisibility(visible[i]);
			object3d1.setAdditionalColor(addColor[i]);
			i++;
		}

		buffer.setPaintListenerState(true);
		rendersShadowPass = false;
		world.setAmbientLight(ai[0], ai[1], ai[2]);
		world.renderScene(buffer);
		if (flag)
			world.draw(buffer);
	}

	public synchronized boolean isRenderingShadowMap() {
		return rendersShadowMap;
	}

	public synchronized boolean isRenderingShadowPass() {
		return rendersShadowPass;
	}

	public int getReceiverCount() {
		return recMap.size();
	}

	public int getCasterCount() {
		return casMap.size();
	}

	public synchronized void updateShadowMap() {
		if (world == null)
			return;
		rendersShadowMap = true;
		buffer.setPaintListenerState(false);
		com.threed.jpct.Camera camera = world.getCamera();
		world.setCameraTo(proj);
		checkArrays();
		int i = 0;
		for (Enumeration enumeration = world.getObjects(); enumeration.hasMoreElements();) {
			Object3D object3d = (Object3D) enumeration.nextElement();
			inverts[i] = object3d.cullingIsInverted();
			lightsOff[i] = object3d.getLighting();
			visible[i] = object3d.getVisibility();
			if (invertCulling)
				object3d.invertCulling(true);
			if (!casMap.containsKey(object3d))
				object3d.setVisibility(false);
			object3d.setLighting(1);
			i++;
		}

		buffer.setRenderTarget(map, border, border, border, border, true);
		buffer.clearZBufferOnly();
		map.setEnabled(false);
		world.renderScene(buffer);
		world.draw(buffer);
		map.setEnabled(true);
		buffer.update();
		buffer.displayGLOnly();
		world.setCameraTo(camera);
		if (!Config.glUseFBO)
			buffer.clearZBufferOnly();
		buffer.removeRenderTarget();
		i = 0;
		for (Enumeration enumeration1 = world.getObjects(); enumeration1.hasMoreElements();) {
			Object3D object3d1 = (Object3D) enumeration1.nextElement();
			object3d1.invertCulling(inverts[i]);
			object3d1.setLighting(lightsOff[i]);
			object3d1.setVisibility(visible[i]);
			i++;
		}

		buffer.setPaintListenerState(true);
		rendersShadowMap = false;
	}

	private void checkArrays() {
		if (inverts == null || world.getSize() + 10 > inverts.length) {
			int i = world.getSize() + 8;
			inverts = new boolean[i];
			lightsOff = new int[i];
			visible = new boolean[i];
			addColor = new Color[i];
		}
	}

	public void dispose() {
		synchronized (this.map) {
			if (this.map == null)
				return;
			TextureManager localTextureManager = TextureManager.getInstance();
			localTextureManager.removeAndUnload(this.name, this.buffer);
			this.map = null;
		}
	}

	public void finalize() {
		dispose();
	}

	private static final long serialVersionUID = 1L;
	private static int cnt = 0;
	private Texture map;
	private boolean inverts[];
	private int lightsOff[];
	private boolean visible[];
	private Color addColor[];
	private Hashtable recMap;
	private Hashtable casMap;
	private World world;
	private FrameBuffer buffer;
	private int tid;
	private Projector proj;
	private boolean disableLights;
	private Color col;
	private String name;
	private boolean revert;
	private boolean invertCulling;
	private boolean disableAddColor;
	private int border;
	private boolean rendersShadowMap;
	private boolean rendersShadowPass;

}
