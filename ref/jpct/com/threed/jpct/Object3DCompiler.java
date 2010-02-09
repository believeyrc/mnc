// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.util.*;

// Referenced classes of package com.threed.jpct:
//            CompiledInstance, GLBase, AWTGLRenderer, OcTreeNode, 
//            Object3D, Logger, IRenderer, IThreadBuffer, 
//            Config, Mesh, OcTree

class Object3DCompiler {

	Object3DCompiler() {
	}

	synchronized void compile(Object3D object3d, IRenderer irenderer) {
		if (object3d.compiled == null || object3d.compiled.size() != 0)
			return;
		if (object3d.shareWith != null) {
			int i = object3d.shareWith.compiled.size();
			for (int j = 0; j < i; j++)
				object3d.addCompiled(new CompiledInstance(object3d, j, -1));

			Logger.log("Object " + object3d.getID() + "/" + object3d.getName() + " precompiled!", 2);
			return;
		}
		long l = System.currentTimeMillis();
		GLBase glbase = null;
		if (irenderer instanceof GLBase)
			glbase = (GLBase) irenderer;
		Object obj = new Object();
		if (irenderer instanceof AWTGLRenderer) {
			Object obj1 = ((AWTGLRenderer) irenderer).getLock();
			try {
				for (int k = 0; !irenderer.isInitialized() && k < 50; k++) {
					((AWTGLRenderer) irenderer).canvas.repaint();
					Logger.log("Waiting for renderer to initialize..." + k, 2);
					Thread.sleep(50L);
				}

			} catch (Exception exception) {
			}
			if (obj1 != null)
				obj = obj1;
		}
		synchronized (obj) {
			if (!object3d.isCompiled() && object3d.compiled != null) {
				HashMap hashmap = new HashMap();
				synchronized (object3d.compiled) {
					int i1 = Config.glBatchSize;
					if (object3d.dynamic)
						i1 = Config.glDynamicBatchSize;
					if (object3d.batchSize != -1)
						i1 = object3d.batchSize;
					int j1 = object3d.getMesh().anzTri;
					HashMap hashmap1 = new HashMap();
					Vector vector1 = null;
					boolean flag = false;
					if (object3d.getOcTree() != null && object3d.getOcTree().getRenderingUse()) {
						vector1 = object3d.getOcTree().getFilledLeafs();
						flag = true;
					}
					int k1 = -1;
					for (int l1 = 0; l1 < j1; l1++) {
						String s = String.valueOf(object3d.texture[l1]);
						if (object3d.multiTex != null) {
							for (int i2 = 0; i2 < object3d.multiTex.length; i2++)
								s = s + "_" + object3d.multiTex[i2][l1] + "/" + object3d.multiMode[i2][l1];

						}
						int j2 = -1;
						if (flag) {
							if (k1 != -1) {
								int ai[] = ((OcTreeNode) vector1.get(k1)).getPolygons();
								int l2 = 0;
								do {
									if (l2 >= ai.length)
										break;
									if (l1 == ai[l2]) {
										j2 = ((OcTreeNode) vector1.get(k1)).getID();
										break;
									}
									l2++;
								} while (true);
							}
							if (j2 == -1) {
								label0: for (int k2 = 0; k2 < vector1.size(); k2++) {
									int ai2[] = ((OcTreeNode) vector1.get(k2)).getPolygons();
									int i3 = 0;
									do {
										if (i3 >= ai2.length)
											continue label0;
										if (l1 == ai2[i3]) {
											j2 = ((OcTreeNode) vector1.get(k2)).getID();
											k1 = k2;
											k2 = vector1.size();
											continue label0;
										}
										i3++;
									} while (true);
								}

							}
							s = s + "_oc_" + j2;
						}
						int ai1[] = (int[]) hashmap1.get(s);
						if (ai1 == null) {
							ai1 = (new int[] { 0 });
							hashmap1.put(s, ai1);
						}
						ai1[0]++;
						s = s + "_" + ai1[0] / i1;
						CompiledInstance compiledinstance1 = (CompiledInstance) hashmap.get(s);
						if (compiledinstance1 == null) {
							compiledinstance1 = new CompiledInstance(object3d, l1, j2);
							hashmap.put(s, compiledinstance1);
						}
						compiledinstance1.add(l1);
					}

					ArrayList arraylist = new ArrayList(hashmap.keySet());
					Collections.sort(arraylist);
					Iterator iterator = arraylist.iterator();
					do {
						if (!iterator.hasNext())
							break;
						Object obj3 = iterator.next();
						CompiledInstance compiledinstance = (CompiledInstance) hashmap.get(obj3);
						compiledinstance.fill();
						compiledinstance.setKey(obj3.toString());
						object3d.addCompiled(compiledinstance);
						if (glbase != null && compiledinstance.useDL)
							glbase.addToCompile(compiledinstance);
					} while (true);
				}
				Logger.log("Object " + object3d.getID() + "/" + object3d.getName() + " compiled to " + hashmap.size()
						+ " subobjects in " + (System.currentTimeMillis() - l) + "ms!", 2);
			} else if (object3d.compiled != null)
				Logger.log("Object " + object3d.getID() + "/" + object3d.getName() + " already compiled!", 0);
		}
	}
}
