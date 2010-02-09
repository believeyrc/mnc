// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

// Referenced classes of package com.threed.jpct:
//            SimpleVector, Object3D, Matrix

final class CollisionInfo {

	CollisionInfo() {
		collisionObject = null;
		foundCollision = false;
		collision = false;
		isPartOfCollision = false;
		addTransMat = null;
		addRotMat = null;
	}

	float getMaxRadius() {
		return Math.max(Math.max(eRadius.x, eRadius.y), eRadius.z);
	}

	void calculateInverseAndDest() {
		if (eRadius != null) {
			invERadius = new SimpleVector();
			invERadius.x = 1.0F / eRadius.x;
			invERadius.y = 1.0F / eRadius.y;
			invERadius.z = 1.0F / eRadius.z;
		}
		recalcDest();
	}

	void recalcDest() {
		if (r3Pos != null && r3Velocity != null) {
			r3Dest = new SimpleVector(r3Pos);
			r3Dest.add(r3Velocity);
		}
	}

	SimpleVector eRadius;
	SimpleVector invERadius;
	SimpleVector r3Velocity;
	SimpleVector r3Pos;
	SimpleVector r3Dest;
	SimpleVector intersectionPoint;
	SimpleVector eSpaceVelocity;
	SimpleVector eSpaceBasePoint;
	Object3D collisionObject;
	boolean foundCollision;
	boolean collision;
	boolean isPartOfCollision;
	float nearestDistance;
	Matrix addTransMat;
	Matrix addRotMat;
}
