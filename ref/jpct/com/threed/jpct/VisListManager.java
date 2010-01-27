// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

// Referenced classes of package com.threed.jpct:
//            VisList, Config, FrameBuffer, Logger

class VisListManager implements Serializable {

	VisListManager() {
		buf2Vis = new Hashtable();
		isDisposed = false;
	}

	void dispose() {
		isDisposed = true;
	}

	protected void finalize() {
		dispose();
	}

	VisList getVisList(FrameBuffer framebuffer, VisList vislist) {
		if (!Config.shareVisibilityList) {
			Vector vector = (Vector) buf2Vis.get(framebuffer.getID());
			if (vector == null) {
				framebuffer.register(this);
				vector = new Vector(3);
				vector.addElement(vislist);
				buf2Vis.put(framebuffer.getID(), vector);
				vislist.lastCycle = framebuffer.displayCycle;
				return vislist;
			}
			VisList vislist1 = null;
			int i = 0;
			do {
				if (i >= vector.size())
					break;
				VisList vislist2 = (VisList) vector.elementAt(i);
				if (vislist2.lastCycle != framebuffer.displayCycle) {
					vislist1 = vislist2;
					break;
				}
				i++;
			} while (true);
			if (vislist1 == null) {
				vislist1 = new VisList(vislist.getMaxSize());
				vector.addElement(vislist1);
				Logger.log("Additional visibility list (" + vector.size() + ") created with size: " + vislist.getMaxSize(), 2);
			}
			vislist1.lastCycle = framebuffer.displayCycle;
			return vislist1;
		}
		if (framebuffer.hasRenderTarget)
			vislist = new VisList(vislist.getMaxSize());
		vislist.lastCycle = framebuffer.displayCycle;
		return vislist;
	}

	void remove(FrameBuffer framebuffer) {
		buf2Vis.remove(framebuffer.getID());
		Logger.log("Visibility lists disposed!", 2);
	}

	private static final long serialVersionUID = 2L;
	private Hashtable buf2Vis;
	boolean isDisposed;
}
