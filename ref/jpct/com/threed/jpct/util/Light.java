// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct.util;

import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import java.io.Serializable;

public class Light implements Serializable {

	public Light(World world1) {
		light = -1;
		on = false;
		world = null;
		world = world1;
		light = world1.addLight(new SimpleVector(), 255F, 255F, 255F);
		on = true;
	}

	public void enable() {
		on = true;
		world.setLightVisibility(light, true);
	}

	public void disable() {
		on = false;
		world.setLightVisibility(light, false);
	}

	public boolean isEnabled() {
		return on;
	}

	public void setIntensity(SimpleVector simplevector) {
		setIntensity(simplevector.x, simplevector.y, simplevector.z);
	}

	public void setIntensity(float f, float f1, float f2) {
		world.setLightIntensity(light, f, f1, f2);
	}

	public SimpleVector getIntensity() {
		return world.getLightIntensity(light);
	}

	public void setAttenuation(float f) {
		world.setLightAttenuation(light, f);
	}

	public float getAttenuation() {
		return world.getLightAttenuation(light);
	}

	public void setDiscardDistance(float f) {
		if (f < 0.0F)
			f = -1F;
		world.setLightDiscardDistance(light, f);
	}

	public float getDiscardDistance() {
		return world.getLightDiscardDistance(light);
	}

	public void setPosition(SimpleVector simplevector) {
		world.setLightPosition(light, simplevector);
	}

	public SimpleVector getPosition() {
		return world.getLightPosition(light);
	}

	public void rotate(SimpleVector simplevector, SimpleVector simplevector1) {
		SimpleVector simplevector2 = world.getLightPosition(light).calcSub(simplevector1);
		simplevector2.rotateX(simplevector.x);
		simplevector2.rotateY(simplevector.y);
		simplevector2.rotateZ(simplevector.z);
		simplevector2.add(simplevector1);
		setPosition(simplevector2);
	}

	private static final long serialVersionUID = 1L;
	private int light;
	private boolean on;
	private World world;
}
