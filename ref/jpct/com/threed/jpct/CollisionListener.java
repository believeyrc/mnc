// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            CollisionEvent

public interface CollisionListener extends Serializable {

	public abstract void collision(CollisionEvent collisionevent);

	public abstract boolean requiresPolygonIDs();
}
