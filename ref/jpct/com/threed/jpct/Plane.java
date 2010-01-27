// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

// Referenced classes of package com.threed.jpct:
//            SimpleVector

public class Plane {

	public Plane() {
		equation3 = 0.0F;
		normal = SimpleVector.ORIGIN;
	}

	public Plane(SimpleVector simplevector, SimpleVector simplevector1) {
		equation3 = 0.0F;
		setTo(simplevector, simplevector1);
	}

	float[] getPlaneEquation() {
		float af[] = new float[4];
		af[0] = normal.x;
		af[1] = -normal.y;
		af[2] = -normal.z;
		af[3] = equation3;
		return af;
	}

	public void setTo(SimpleVector simplevector, SimpleVector simplevector1) {
		normal = new SimpleVector(simplevector1);
		equation3 = -(simplevector1.x * simplevector.x + simplevector1.y * simplevector.y + simplevector1.z
				* simplevector.z);
	}

	public Plane(SimpleVector simplevector, SimpleVector simplevector1, SimpleVector simplevector2) {
		equation3 = 0.0F;
		setTo(simplevector, simplevector1, simplevector2);
	}

	public void setTo(SimpleVector simplevector, SimpleVector simplevector1, SimpleVector simplevector2) {
		float f = simplevector1.x - simplevector.x;
		float f1 = simplevector1.y - simplevector.y;
		float f2 = simplevector1.z - simplevector.z;
		float f3 = simplevector2.x - simplevector.x;
		float f4 = simplevector2.y - simplevector.y;
		float f5 = simplevector2.z - simplevector.z;
		double d = f1 * f5 - f2 * f4;
		double d1 = f2 * f3 - f * f5;
		double d2 = f * f4 - f1 * f3;
		double d3 = Math.sqrt(d * d + d1 * d1 + d2 * d2);
		if (d3 != 0.0D) {
			double d4 = 1.0D / d3;
			normal = new SimpleVector((float) (d * d4), (float) (d1 * d4), (float) (d2 * d4));
		} else {
			normal = new SimpleVector(0.0F, 0.0F, 0.0F);
		}
		equation3 = -(normal.x * simplevector.x + normal.y * simplevector.y + normal.z * simplevector.z);
	}

	public boolean isFrontFacingTo(SimpleVector simplevector) {
		float f = normal.x * simplevector.x + normal.y * simplevector.y + normal.z * simplevector.z;
		return f <= 0.0F;
	}

	public float distanceTo(SimpleVector simplevector) {
		return normal.x * simplevector.x + normal.y * simplevector.y + normal.z * simplevector.z + equation3;
	}

	private float equation3;
	SimpleVector normal;
}
