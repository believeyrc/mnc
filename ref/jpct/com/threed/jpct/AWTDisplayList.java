// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            VisList, Config, Logger

class AWTDisplayList implements Serializable {

	AWTDisplayList() {
		col = 0xffffff;
		count = 0;
		max = 0;
		renderTarget = false;
		cleanupCount = 0;
		int i = Config.glAWTCommandQueueSize;
		vl = new VisList[i];
		start = new int[i];
		end = new int[i];
		mode = new int[i];
		params = new Object[i];
		command = new int[i];
		max = i;
	}

	void enlarge() {
		if (max == count) {
			max += Config.glAWTCommandQueueSize;
			Logger.log("Expanding command queue to " + max + " entries!", 2);
			VisList avislist[] = new VisList[max];
			System.arraycopy(vl, 0, avislist, 0, vl.length);
			vl = avislist;
			Object aobj[] = new Object[max];
			System.arraycopy(((Object) (params)), 0, ((Object) (aobj)), 0, params.length);
			params = aobj;
			int ai[] = new int[max];
			System.arraycopy(start, 0, ai, 0, start.length);
			start = ai;
			int ai1[] = new int[max];
			System.arraycopy(end, 0, ai1, 0, end.length);
			end = ai1;
			int ai2[] = new int[max];
			System.arraycopy(mode, 0, ai2, 0, mode.length);
			mode = ai2;
			int ai3[] = new int[max];
			System.arraycopy(command, 0, ai3, 0, command.length);
			command = ai3;
		}
	}

	void reset() {
		count = 0;
		renderTarget = false;
	}

	void setColor(int i) {
		col = i;
	}

	int getColor() {
		return col;
	}

	void fillInstances() {
		for (int i = 0; i < count; i++)
			if (vl[i] != null)
				vl[i].fillInstances();

	}

	void switchBuffers() {
		for (int i = 0; i < count; i++)
			if (vl[i] != null)
				vl[i].switchBuffers();

		cleanupCount++;
		if (cleanupCount >= Config.glAWTCommandQueueCleanup) {
			cleanupCount = 0;
			for (int j = count; j < max; j++) {
				vl[j] = null;
				params[j] = null;
			}

		}
	}

	void addOnce(int i, Object obj) {
		enlarge();
		params[count] = obj;
		command[count] = i;
		vl[count] = null;
		mode[count] = 4;
		count++;
	}

	void enableRenderTarget() {
		renderTarget = true;
	}

	void disableRenderTarget() {
		renderTarget = false;
	}

	boolean hasRenderTarget() {
		return renderTarget;
	}

	void add(int i, Object obj) {
		enlarge();
		params[count] = obj;
		command[count] = i;
		vl[count] = null;
		mode[count] = 3;
		count++;
	}

	void delete(int i) {
		mode[i] = -1;
		vl[i] = null;
		params[i] = null;
		for (int j = 0; j < count; j++)
			if (mode[j] != -1)
				return;

		count = 0;
	}

	void add(VisList vislist, int i, int j, int k) {
		enlarge();
		vislist.extract();
		mode[count] = k;
		vl[count] = vislist;
		start[count] = i;
		end[count] = j;
		count++;
	}

	private static final long serialVersionUID = 1L;
	static final int NONE = -1;
	static final int POLYGON = 0;
	static final int STRIP = 1;
	static final int ARRAY = 2;
	static final int EXECUTE = 3;
	static final int EXECUTE_ONCE = 4;
	static final int WIREFRAME = 5;
	VisList vl[];
	int col;
	int start[];
	int end[];
	int mode[];
	int command[];
	Object params[];
	int count;
	int max;
	boolean renderTarget;
	private int cleanupCount;
}
