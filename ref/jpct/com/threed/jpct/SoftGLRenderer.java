// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import com.threed.jpct.threading.WorkLoad;
import com.threed.jpct.threading.Worker;
import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            VideoMode, IRenderer, TextureManager, Logger, 
//            IPaintListener, Config, FrameBuffer, VisList, 
//            Object3D, Vectors, Mesh, World, 
//            Portals, Texture, Lights, VersionHelper

public final class SoftGLRenderer implements IRenderer, Serializable {
	private final class MyWorkLoad implements WorkLoad {

		public void set(VisList vislist, int i, int j, FrameBuffer framebuffer, World world1) {
			start = i;
			end = j;
			buffer = framebuffer;
			world = world1;
			visList = vislist;
			float f = (float) framebuffer.height / (float) parts;
			ystart = (int) (f * (float) part);
			yend = (int) (f * ((float) part + 1.0F));
			if (part == parts - 1)
				yend = framebuffer.height;
			if (Config.loadBalancingStrategy > 0) {
				if (framebuffer.sliStart == null) {
					framebuffer.sliStart = new int[parts];
					framebuffer.sliTimes = new long[parts];
					framebuffer.sliStart[part] = ystart;
				}
				if (part != 0 && framebuffer.sliStart[part] == 0)
					framebuffer.sliStart[part] = ystart;
				ystart = framebuffer.sliStart[part];
				if (part < parts - 1 && framebuffer.sliStart[part + 1] != 0)
					yend = framebuffer.sliStart[part + 1];
			}
		}

		public void doWork() {
			for (int i = start; i <= end; i++)
				drawPolygon(visList, i, buffer, world, ystart, yend);

			if (Config.mtDebug) {
				for (int j = 0; j < 100; j++)
					buffer.pixels[ystart * buffer.width + j] = 0xffffff;

			}
			if (Config.loadBalancingStrategy > 0) {
				long l = buffer.versionHelper.getTime();
				long l1 = buffer.sliTimes[part];
				if (l1 == 0L)
					l1 = l;
				buffer.sliTimes[part] = l + l1 >> 1;
			}
		}

		public void done() {
		}

		public void error(Exception exception) {
		}

		private int ystart;
		private int yend;
		private int start;
		private int end;
		private FrameBuffer buffer;
		private World world;
		private VisList visList;
		private int part;
		private int parts;

		public MyWorkLoad(int i, int j) {
			ystart = 0;
			yend = 0;
			start = 0;
			end = 0;
			buffer = null;
			world = null;
			part = 0;
			parts = 1;
			part = i;
			parts = j;
		}
	}

	SoftGLRenderer() {
		listener = null;
		listenerActive = true;
		worker = null;
		workers = null;
	}

	public void init(int i, int j, int k, int l, int i1) {
		Logger.log("Software renderer (OpenGL mode) initialized", 2);
	}

	public void setPaintListener(IPaintListener ipaintlistener) {
		listener = ipaintlistener;
	}

	public void dispose() {
		listener = null;
		if (worker != null)
			worker.dispose();
		Logger.log("Software renderer disposed", 2);
	}

	public void finalize() {
		dispose();
	}

	public void execute(int i, Object aobj[]) {
		if (listener != null)
			if (i == 12 && listenerActive)
				listener.startPainting();
			else if (i == 13 && listenerActive)
				listener.finishedPainting();
		if (i == 24) {
			boolean flag = ((Boolean) aobj[0]).booleanValue();
			listenerActive = flag;
		}
	}

	public void drawStrip(VisList vislist, int i, int j, FrameBuffer framebuffer, World world) {
		for (int k = i; k <= j + i; k++)
			drawPolygon(vislist, k, framebuffer, world);

	}

	public Worker initWorker() {
		if (worker == null) {
			int i = Config.maxNumberOfCores;
			int j = Math.max(i - 1, 2);
			worker = new Worker(j);
		}
		return worker;
	}

	public void drawVertexArray(VisList vislist, int i, int j, FrameBuffer framebuffer, World world) {
		if (Config.useMultipleThreads) {
			if (workers == null) {
				initWorker();
				int k = Config.maxNumberOfCores;
				workers = new MyWorkLoad[k];
				for (int j1 = 0; j1 < workers.length; j1++) {
					MyWorkLoad myworkload = new MyWorkLoad(j1, k);
					workers[j1] = myworkload;
				}

			}
			int l = workers.length;
			for (int k1 = 0; k1 < l; k1++) {
				MyWorkLoad myworkload1 = workers[k1];
				myworkload1.set(vislist, i, j, framebuffer, world);
				if (k1 < l - 1)
					worker.add(myworkload1);
				else
					myworkload1.doWork();
			}

			worker.waitForAll();
			if (Config.loadBalancingStrategy > 0) {
				long l1 = 2L;
				int i2 = (int) ((float) (framebuffer.height / l) * 0.1F);
				for (int j2 = 1; j2 < l; j2++) {
					long l2 = framebuffer.sliTimes[j2 - 1];
					long l3 = framebuffer.sliTimes[j2];
					if (l2 < l3 - l1)
						framebuffer.sliStart[j2] += i2;
					else if (l3 < l2 - l1)
						framebuffer.sliStart[j2] -= i2;
					int k2 = framebuffer.height;
					if (j2 < l - 1)
						k2 = framebuffer.sliStart[j2 + 1];
					if (framebuffer.sliStart[j2] < framebuffer.sliStart[j2 - 1])
						framebuffer.sliStart[j2] += i2 * 2;
					for (; framebuffer.sliStart[j2] >= k2; framebuffer.sliStart[j2] -= i2)
						;
				}

			}
		} else {
			for (int i1 = i; i1 <= j; i1++)
				drawPolygon(vislist, i1, framebuffer, world);

		}
	}

	public void drawPolygon(VisList vislist, int i, FrameBuffer framebuffer, World world) {
		drawPolygon(vislist, i, framebuffer, world, 0, framebuffer.height);
	}

	private void drawPolygon(VisList vislist, int i, FrameBuffer framebuffer, World world, int j, int k) {
		int l = vislist.vnum[i];
		Object3D object3d = vislist.vobj[i];
		int i1 = i;
		if (vislist.stageCnt[i] > 0)
			return;
		int ai[] = p;
		if (Config.useMultipleThreads && j != 0)
			ai = new int[3];
		i = vislist.portalNum[i1];
		float f = 0.0F;
		float f1 = framebuffer.width;
		float f2 = j;
		float f3 = k;
		Mesh mesh = object3d.objMesh;
		Vectors vectors = object3d.objVectors;
		float af[] = vectors.sx;
		float af1[] = vectors.sy;
		float af2[] = vectors.sz;
		float af3[] = vectors.sr;
		float af4[] = vectors.sg;
		float af5[] = vectors.sb;
		float af6[] = vectors.su;
		float af7[] = vectors.sv;
		float af8[] = vectors.nuOrg;
		float af9[] = vectors.nvOrg;
		float af10[] = vectors.bsu;
		float af11[] = vectors.bsv;
		if (af10 == null) {
			af10 = vectors.su;
			af11 = vectors.sv;
		}
		int ai1[] = mesh.coords;
		if (i != 0x5f5e0ff) {
			f = world.portals.lowx[i];
			f1 = world.portals.highx[i] + 1.0F;
			f2 = Math.max(world.portals.lowy[i], j);
			f3 = Math.min(world.portals.highy[i] + 1.0F, k);
		}
		int j1 = (int) f2;
		int k1 = 0;
		ai[0] = mesh.points[l][0];
		ai[1] = mesh.points[l][1];
		ai[2] = mesh.points[l][2];
		int l2 = 0x1869f;
		int i3 = 0;
		int j3 = 0x1869f;
		int k3 = 0;
		boolean flag = object3d.alwaysFilter;
		if (framebuffer.useBb && framebuffer.getType() == 0) {
			for (int l3 = 0; l3 < 3; l3++) {
				int j4 = ai1[ai[l3]];
				if (af1[j4] < (float) l2) {
					l2 = (int) af1[j4];
					k1 = l3;
				}
				if (af1[j4] > (float) i3)
					i3 = (int) af1[j4];
				if (af[j4] < (float) j3)
					j3 = (int) af[j4];
				if (af[j4] > (float) k3)
					k3 = (int) af[j4];
				if (af1[j4] < af1[ai1[ai[k1]]])
					k1 = l3;
			}

			if (i3 > framebuffer.bbYu)
				framebuffer.bbYu = i3;
			if (l2 < framebuffer.bbYo)
				framebuffer.bbYo = l2;
			if (j3 < framebuffer.bbXl)
				framebuffer.bbXl = j3;
			if (k3 > framebuffer.bbXr)
				framebuffer.bbXr = k3;
		} else {
			float f4 = af1[ai1[ai[k1]]];
			float f5 = 0.0F;
			if ((f5 = af1[ai1[ai[1]]]) < f4) {
				k1 = 1;
				f4 = f5;
			}
			if (af1[ai1[ai[2]]] < f4)
				k1 = 2;
		}
		int i4 = 0;
		int k4 = 1;
		if (k1 == i4)
			i4 = 2;
		if (k1 == k4)
			k4 = 2;
		float f6 = af1[ai1[ai[i4]]];
		if (af1[ai1[ai[k4]]] > f6)
			f6 = af1[ai1[ai[k4]]];
		if (f6 < f2)
			return;
		int l1 = k1 + 1;
		int i2 = k1 - 1;
		if (l1 > 2)
			l1 = 0;
		if (i2 < 0)
			i2 = 2;
		int l4 = ai[k1];
		int i5 = ai1[l4];
		int j5 = ai[l1];
		int k5 = ai[i2];
		int l5 = ai1[j5];
		int i6 = ai1[k5];
		float f7 = af1[i5];
		int j6 = (int) f7;
		if ((float) j6 >= f3)
			return;
		int k6 = 0;
		Texture texture2 = texMan.textures[object3d.texture[l]];
		boolean flag1 = Config.mipmap;
		if (flag1) {
			int l6 = ai1[ai[0]];
			int i7 = ai1[ai[1]];
			int k7 = ai1[ai[2]];
			float f12 = (af[l6] * af1[i7] + af[i7] * af1[k7] + af[k7] * af1[l6])
					- (af1[l6] * af[i7] + af1[i7] * af[k7] + af1[k7] * af[l6]);
			f12 = (float) ((double) f12 * 0.5D);
			f12 = f12 >= 0.0F ? f12 : -f12;
			int l7 = framebuffer.getSamplingMode();
			if (l7 == 1)
				f12 /= 4F;
			else if (l7 == 3)
				f12 /= 2.25F;
			else if (l7 == 2)
				f12 *= 4F;
			float f13 = (af8[ai[0]] * af9[ai[1]] + af8[ai[1]] * af9[ai[2]] + af8[ai[2]] * af9[ai[0]])
					- (af9[ai[0]] * af8[ai[1]] + af9[ai[1]] * af8[ai[2]] + af9[ai[2]] * af8[ai[0]]);
			f13 = (float) ((double) f13 * 0.5D);
			f13 = f13 >= 0.0F ? f13 : -f13;
			float f14 = f13 * (float) texture2.width * (float) texture2.height;
			float f15 = f12 / f14;
			int i8 = -1;
			boolean flag8 = false;
			if (texture2.mipMaps == null)
				texture2.getMipMappedTexture(1);
			if (texture2.mipMaps != null) {
				while (!flag8 && ++i8 < texture2.mipMaps.length - 1) {
					flag8 = f15 > 0.5F;
					f15 *= 4F;
				}
				k6 = i8;
			}
		}
		Texture texture;
		Texture texture1;
		if (!flag1) {
			texture = texture2;
			texture1 = texMan.textures[object3d.basemap[l]];
		} else {
			texture = texture2.getMipMappedTexture(k6);
			texture1 = texMan.textures[object3d.basemap[l]].getMipMappedTexture(k6);
		}
		Texture texture3 = null;
		texture1.updateUsage(world.drawCnt);
		texture.updateUsage(world.drawCnt);
		int j7 = vislist.vorg[i1].transValue;
		boolean flag2 = vislist.vorg[i1].transMode == 1;
		boolean flag3 = (vislist.mode[i1] & 2) == 2;
		boolean flag4 = texture.alpha != null;
		boolean flag5 = (vislist.mode[i1] & 1) == 1;
		boolean flag6 = (vislist.mode[i1] & 4) == 4;
		boolean flag7 = (vislist.mode[i1] & 8) == 8;
		float f16 = 1.0F;
		float f17 = 1.0F;
		if (flag7 && object3d.bumpmap != null) {
			if (!flag1) {
				texture3 = texMan.textures[object3d.bumpmap[l]];
			} else {
				texture3 = texMan.textures[object3d.bumpmap[l]].getMipMappedTexture(k6);
				f16 = texture3.xDiv;
				f17 = texture3.yDiv;
			}
			texture3.updateUsage(world.drawCnt);
		}
		int j8 = world.lights.rgbScale;
		float f18 = 0.0F;
		float f19 = 0.0F;
		float f20 = 0.0F;
		float f21 = 0.0F;
		float f22 = 0.0F;
		float f23 = 0.0F;
		float f24 = 0.0F;
		float f25 = 0.0F;
		float f32 = 0.0F;
		float f33 = 0.0F;
		float f34 = 0.0F;
		float f35 = 0.0F;
		float f26 = 0.0F;
		float f27 = 0.0F;
		float f28 = 0.0F;
		float f29 = 0.0F;
		float f30 = 0.0F;
		float f31 = 0.0F;
		float f36 = af[i5];
		float f37 = af6[l4];
		float f38 = af7[l4];
		float f39 = af2[i5];
		float f40 = f37;
		float f41 = f36;
		float f42 = f38;
		float f43 = f39;
		float f44 = af10[l4];
		float f45 = af11[l4];
		float f46 = f45;
		float f47 = f44;
		if (k6 > 0) {
			f37 /= texture.xDiv;
			f38 /= texture.yDiv;
			f40 /= texture.xDiv;
			f42 /= texture.yDiv;
			if (texture3 != null) {
				f44 /= f16;
				f45 /= f17;
				f47 /= f16;
				f46 /= f17;
			}
		}
		float f48 = af3[l4];
		float f49 = af4[l4];
		float f50 = af5[l4];
		float f51 = f50;
		float f52 = f48;
		float f53 = f49;
		float f54 = af1[l5];
		float f55 = af1[i6];
		int j2 = (int) f54;
		int k2 = (int) f55;
		float f56 = (f54 - f7) + 1.0F;
		float f59 = (f55 - f7) + 1.0F;
		float f62 = (float) j6 - f7;
		if (f56 != 0.0F) {
			float f65 = 1.0F / f56;
			f18 = (af[ai1[j5]] - f36) * f65;
			f20 = (af6[j5] / texture.xDiv - f37) * f65;
			f22 = (af7[j5] / texture.yDiv - f38) * f65;
			f24 = (af2[ai1[j5]] - f39) * f65;
			f32 = (af10[j5] / f16 - f44) * f65;
			f34 = (af11[j5] / f17 - f45) * f65;
			f28 = (af3[j5] - f48) * f65;
			f30 = (af4[j5] - f49) * f65;
			f26 = (af5[j5] - f50) * f65;
		}
		if (f59 != 0.0F) {
			float f66 = 1.0F / f59;
			f19 = (af[ai1[k5]] - f36) * f66;
			f21 = (af6[k5] / texture.xDiv - f37) * f66;
			f23 = (af7[k5] / texture.yDiv - f38) * f66;
			f25 = (af2[ai1[k5]] - f39) * f66;
			f33 = (af10[k5] / f16 - f44) * f66;
			f35 = (af11[k5] / f17 - f45) * f66;
			f29 = (af3[k5] - f48) * f66;
			f31 = (af4[k5] - f49) * f66;
			f27 = (af5[k5] - f50) * f66;
		}
		byte byte0 = 2;
		byte byte1 = 0;
		float f67 = 0.0F;
		float f68 = 0.0F;
		float f69 = 0.0F;
		if ((float) j6 < f2) {
			int k8 = j1 - j6;
			if ((float) j2 >= f2 && (float) k2 >= f2) {
				f36 += f18 * (float) k8;
				f41 += f19 * (float) k8;
				f37 += f20 * (float) k8;
				f40 += f21 * (float) k8;
				f38 += f22 * (float) k8;
				f42 += f23 * (float) k8;
				f39 += f24 * (float) k8;
				f43 += f25 * (float) k8;
				f44 += f32 * (float) k8;
				f47 += f33 * (float) k8;
				f45 += f34 * (float) k8;
				f46 += f35 * (float) k8;
				f50 += f26 * (float) k8;
				f51 += f27 * (float) k8;
				f48 += f28 * (float) k8;
				f52 += f29 * (float) k8;
				f49 += f30 * (float) k8;
				f53 += f31 * (float) k8;
				j6 = j1;
			} else {
				if ((float) j2 < f2 && (float) k2 < f2)
					return;
				if ((float) j2 < f2) {
					byte0 = 1;
					int i9 = l1 + 1;
					if (i9 > 2)
						i9 = 0;
					int k9 = ai[i9];
					j2 = (int) af1[ai1[k9]];
					float f57 = (af1[ai1[k9]] - af1[ai1[j5]]) + 1.0F;
					if (f57 != 0.0F) {
						float f72 = 1.0F / f57;
						f18 = af[ai1[k9]] - af[ai1[j5]];
						f20 = ((af6[k9] - af6[j5]) / texture.xDiv) * f72;
						f22 = ((af7[k9] - af7[j5]) / texture.yDiv) * f72;
						f24 = (af2[ai1[k9]] - af2[ai1[j5]]) * f72;
						f32 = ((af10[k9] - af10[j5]) / f16) * f72;
						f34 = ((af11[k9] - af11[j5]) / f17) * f72;
						f28 = (af3[k9] - af3[j5]) * f72;
						f30 = (af4[k9] - af4[j5]) * f72;
						f26 = (af5[k9] - af5[j5]) * f72;
						f18 *= f72;
					}
					f36 = af[ai1[j5]];
					f37 = af6[j5] / texture.xDiv;
					f38 = af7[j5] / texture.yDiv;
					f39 = af2[ai1[j5]];
					if (vectors.bsu != null) {
						f44 = vectors.bsu[j5] / f16;
						f45 = vectors.bsv[j5] / f17;
					}
					f48 = af3[j5];
					f49 = af4[j5];
					f50 = af5[j5];
					byte1 = 1;
					if (f2 == 0.0F) {
						f67 = 0.0F;
					} else {
						float f8 = f2;
						int i10 = (int) f2;
						f67 = (float) i10 - f8;
					}
					f41 += f19 * (float) k8;
					f40 += f21 * (float) k8;
					f42 += f23 * (float) k8;
					f43 += f25 * (float) k8;
					f47 += f33 * (float) k8;
					f46 += f35 * (float) k8;
					f51 += f27 * (float) k8;
					f52 += f29 * (float) k8;
					f53 += f31 * (float) k8;
					float f70 = f2 - af1[ai1[j5]];
					l1 = i9;
					j6 = j1;
					f36 += f18 * f70;
					f37 += f20 * f70;
					f38 += f22 * f70;
					f39 += f24 * f70;
					f44 += f32 * f70;
					f45 += f34 * f70;
					f48 += f28 * f70;
					f49 += f30 * f70;
					f50 += f26 * f70;
				} else {
					byte0 = 1;
					int j9 = i2 - 1;
					if (j9 < 0)
						j9 = 2;
					int l9 = ai[j9];
					k2 = (int) af1[ai1[l9]];
					float f60 = (af1[ai1[l9]] - af1[ai1[k5]]) + 1.0F;
					if (f60 != 0.0F) {
						float f73 = 1.0F / f60;
						f19 = af[ai1[l9]] - af[ai1[k5]];
						f21 = ((af6[l9] - af6[k5]) / texture.xDiv) * f73;
						f23 = ((af7[l9] - af7[k5]) / texture.yDiv) * f73;
						f25 = (af2[ai1[l9]] - af2[ai1[k5]]) * f73;
						f33 = ((af10[l9] - af10[k5]) / f16) * f73;
						f35 = ((af11[l9] - af11[k5]) / f17) * f73;
						f29 = (af3[l9] - af3[k5]) * f73;
						f31 = (af4[l9] - af4[k5]) * f73;
						f27 = (af5[l9] - af5[k5]) * f73;
						f19 *= f73;
					}
					f41 = af[ai1[k5]];
					f40 = af6[k5] / texture.xDiv;
					f42 = af7[k5] / texture.yDiv;
					f43 = af2[ai1[k5]];
					if (vectors.bsu != null) {
						f47 = vectors.bsu[k5] / f16;
						f46 = vectors.bsv[k5] / f17;
					}
					f52 = af3[k5];
					f53 = af4[k5];
					f51 = af5[k5];
					byte1 = 2;
					if (f2 == 0.0F) {
						f68 = 0.0F;
					} else {
						float f9 = f2;
						int j10 = (int) f2;
						f68 = (float) j10 - f9;
					}
					f36 += f18 * (float) k8;
					f37 += f20 * (float) k8;
					f38 += f22 * (float) k8;
					f39 += f24 * (float) k8;
					f44 += f32 * (float) k8;
					f45 += f34 * (float) k8;
					f48 += f28 * (float) k8;
					f49 += f30 * (float) k8;
					f50 += f26 * (float) k8;
					float f71 = f2 - af1[ai1[k5]];
					i2 = j9;
					j6 = j1;
					f41 += f19 * f71;
					f40 += f21 * f71;
					f42 += f23 * f71;
					f43 += f25 * f71;
					f47 += f33 * f71;
					f46 += f35 * f71;
					f52 += f29 * f71;
					f53 += f31 * f71;
					f51 += f27 * f71;
				}
			}
		}
		switch (byte1) {
		case 0: // '\0'
			f36 += f18 * f62 + 0.5F;
			f39 += f24 * f62;
			f37 += f20 * f62;
			f38 += f22 * f62;
			f48 += f28 * f62;
			f49 += f30 * f62;
			f50 += f26 * f62;
			f44 += f32 * f62;
			f45 += f34 * f62;
			f41 += f19 * f62 + 0.5F;
			f43 += f25 * f62;
			f40 += f21 * f62;
			f42 += f23 * f62;
			f52 += f29 * f62;
			f53 += f31 * f62;
			f51 += f27 * f62;
			f47 += f33 * f62;
			f46 += f35 * f62;
			break;

		case 1: // '\001'
			f36 += f18 * f67 + 0.5F;
			f41 += f19 * f62 + 0.5F;
			f39 += f24 * f67;
			f43 += f25 * f62;
			f37 += f20 * f67;
			f40 += f21 * f62;
			f38 += f22 * f67;
			f42 += f23 * f62;
			f48 += f28 * f67;
			f52 += f29 * f62;
			f49 += f30 * f67;
			f53 += f31 * f62;
			f50 += f26 * f67;
			f51 += f27 * f62;
			f44 += f32 * f67;
			f45 += f34 * f67;
			f47 += f33 * f62;
			f46 += f35 * f62;
			break;

		case 2: // '\002'
			f36 += f18 * f62 + 0.5F;
			f41 += f19 * f68 + 0.5F;
			f39 += f24 * f62;
			f43 += f25 * f68;
			f37 += f20 * f62;
			f40 += f21 * f68;
			f38 += f22 * f62;
			f42 += f23 * f68;
			f48 += f28 * f62;
			f52 += f29 * f68;
			f49 += f30 * f62;
			f53 += f31 * f68;
			f50 += f26 * f62;
			f51 += f27 * f68;
			f44 += f32 * f62;
			f45 += f34 * f62;
			f47 += f33 * f68;
			f46 += f35 * f68;
			break;
		}
		boolean flag9 = false;
		boolean flag10 = object3d.writeToZbuffer;
		boolean flag11 = Config.texelFilter & (!object3d.alwaysFilter);
		int k10 = (int) (f3 - 1.0F);
		if (f3 != (float) framebuffer.height)
			k10 = framebuffer.height;
		boolean flag12 = !flag7 || !flag5;
		float f74 = 0.0F;
		if (texture.isUnicolor || k6 > 0) {
			flag11 = false;
			flag = false;
		}
		int l10 = byte0;
		do {
			if (l10 <= 0)
				break;
			int l8;
			if (j2 < k2)
				l8 = j2;
			else
				l8 = k2;
			while (j6 < l8 || j6 == k10) {
				f36 += f18;
				f41 += f19;
				f37 += f20;
				f38 += f22;
				f39 += f24;
				f44 += f32;
				f45 += f34;
				f48 += f28;
				f49 += f30;
				f50 += f26;
				f40 += f21;
				f42 += f23;
				f43 += f25;
				f47 += f33;
				f46 += f35;
				f52 += f29;
				f53 += f31;
				f51 += f27;
				if (flag11) {
					flag = false;
					float f79 = 0.75F * f39;
					float f75;
					if (f20 < 0.0F)
						f75 = -f20;
					else
						f75 = f20;
					if (f75 <= f79) {
						float f76;
						if (f22 < 0.0F)
							f76 = -f22;
						else
							f76 = f22;
						flag = f76 <= f79;
					}
					if (!flag) {
						float f80 = 0.75F * f43;
						float f77;
						if (f21 < 0.0F)
							f77 = -f21;
						else
							f77 = f21;
						if (f77 <= f80) {
							float f78;
							if (f23 < 0.0F)
								f78 = -f23;
							else
								f78 = f23;
							flag = f78 <= f80;
						}
					}
				}
				if ((f36 >= 0.0F || f41 >= 0.0F) && (f36 < f1 || f41 < f1)) {
					if (flag12) {
						if (!flag3)
							drawShadedZbufferedFilteredScanline(f48, f52, f49, f53, f50, f51, f36, f41, f37, f40, f38, f42, f39, f43,
									j6, f, f1, framebuffer, texture, flag, j8);
						else if (!flag2) {
							if (!flag4)
								drawShadedZbufferedFilteredTransparentScanline(f48, f52, f49, f53, f50, f51, f36, f41, f37, f40, f38,
										f42, f39, f43, j6, f, f1, framebuffer, texture, flag, j7, j8, flag10);
							else
								drawShadedZbufferedFilteredAlphaScanline(f48, f52, f49, f53, f50, f51, f36, f41, f37, f40, f38, f42,
										f39, f43, j6, f, f1, framebuffer, texture, flag, j7, j8, flag10);
						} else if (!flag4)
							drawShadedZbufferedFilteredAdditiveTransparentScanline(f48, f52, f49, f53, f50, f51, f36, f41, f37, f40,
									f38, f42, f39, f43, j6, f, f1, framebuffer, texture, flag, j7, j8, flag10);
						else
							drawShadedZbufferedFilteredAdditiveAlphaScanline(f48, f52, f49, f53, f50, f51, f36, f41, f37, f40, f38,
									f42, f39, f43, j6, f, f1, framebuffer, texture, flag, j7, j8, flag10);
					} else if (flag6)
						drawShadedZbufferedFilteredBumpmappedBlendedScanline(f48, f52, f49, f53, f50, f51, f36, f41, f37, f40, f38,
								f42, f39, f43, j6, f, f1, framebuffer, texture, flag, f44, f47, f45, f46, texture3, texture1, j8);
					else
						drawShadedZbufferedFilteredBumpmappedScanline(f48, f52, f49, f53, f50, f51, f36, f41, f37, f40, f38, f42,
								f39, f43, j6, f, f1, framebuffer, texture, flag, f44, f47, f45, f46, texture3, j8);
				} else if (f36 < 0.0F && f36 < f41 && f19 <= 0.0F || f36 < 0.0F && f41 < f36 && f18 <= 0.0F || f41 >= f1
						&& f36 < f41 && f18 >= 0.0F || f41 >= f1 && f41 < f36 && f19 >= 0.0F)
					return;
				if ((float) (++j6) >= f3)
					return;
			}
			if (j6 >= j2) {
				int i11 = l1 + 1;
				if (i11 > 2)
					i11 = 0;
				if (l10 == byte0) {
					int k11 = ai[l1];
					int i12 = ai[i11];
					int k12 = ai1[k11];
					int i13 = ai1[i12];
					j2 = (int) af1[i13];
					float f58 = (af1[i13] - af1[k12]) + 1.0F;
					if (f58 != 0.0F) {
						float f81 = 1.0F / f58;
						f18 = (af[i13] - af[k12]) * f81;
						f20 = ((af6[i12] - af6[k11]) / texture.xDiv) * f81;
						f22 = ((af7[i12] - af7[k11]) / texture.yDiv) * f81;
						f24 = (af2[i13] - af2[k12]) * f81;
						f32 = ((af10[i12] - af10[k11]) / f16) * f81;
						f34 = ((af11[i12] - af11[k11]) / f17) * f81;
						f28 = (af3[i12] - af3[k11]) * f81;
						f30 = (af4[i12] - af4[k11]) * f81;
						f26 = (af5[i12] - af5[k11]) * f81;
					}
					f36 = af[k12];
					f37 = af6[k11] / texture.xDiv;
					f38 = af7[k11] / texture.yDiv;
					f39 = af2[k12];
					if (vectors.bsu != null) {
						f44 = vectors.bsu[k11] / f16;
						f45 = vectors.bsv[k11] / f17;
					}
					f48 = af3[k11];
					f49 = af4[k11];
					f50 = af5[k11];
					float f10 = vectors.sy[k12];
					float f63 = (float) j6 - f10;
					f36 += f18 * f63 + 0.5F;
					f39 += f24 * f63;
					f37 += f20 * f63;
					f38 += f22 * f63;
					f48 += f28 * f63;
					f49 += f30 * f63;
					f50 += f26 * f63;
					f44 += f32 * f63;
					f45 += f34 * f63;
				}
				l10--;
				l1 = i11;
			}
			if (j6 >= k2) {
				int j11 = i2 - 1;
				if (j11 < 0)
					j11 = 2;
				if (l10 == byte0) {
					int l11 = ai[i2];
					int j12 = ai[j11];
					int l12 = ai1[l11];
					int j13 = ai1[j12];
					k2 = (int) af1[j13];
					float f61 = (af1[j13] - af1[l12]) + 1.0F;
					if (f61 != 0.0F) {
						float f82 = 1.0F / f61;
						f19 = (af[j13] - af[l12]) * f82;
						f21 = ((af6[j12] - af6[l11]) / texture.xDiv) * f82;
						f23 = ((af7[j12] - af7[l11]) / texture.yDiv) * f82;
						f25 = (af2[j13] - af2[l12]) * f82;
						f33 = ((af10[j12] - af10[l11]) / f16) * f82;
						f35 = ((af11[j12] - af11[l11]) / f17) * f82;
						f29 = (af3[j12] - af3[l11]) * f82;
						f31 = (af4[j12] - af4[l11]) * f82;
						f27 = (af5[j12] - af5[l11]) * f82;
					}
					f41 = af[l12];
					f40 = af6[l11] / texture.xDiv;
					f42 = af7[l11] / texture.yDiv;
					f43 = af2[l12];
					if (vectors.bsu != null) {
						f47 = vectors.bsu[l11] / f16;
						f46 = vectors.bsv[l11] / f17;
					}
					f52 = af3[l11];
					f53 = af4[l11];
					f51 = af5[l11];
					float f11 = af1[l12];
					float f64 = (float) j6 - f11;
					f41 += f19 * f64 + 0.5F;
					f43 += f25 * f64;
					f40 += f21 * f64;
					f42 += f23 * f64;
					f52 += f29 * f64;
					f53 += f31 * f64;
					f51 += f27 * f64;
					f47 += f33 * f64;
					f46 += f35 * f64;
				}
				l10--;
				i2 = j11;
			}
		} while (true);
	}

	private final void drawShadedZbufferedFilteredScanline(float f, float f1, float f2, float f3, float f4, float f5,
			float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, int i, float f14, float f15,
			FrameBuffer framebuffer, Texture texture, boolean flag, int j) {
		boolean flag1 = Config.texelFilter;
		boolean flag2 = Config.spanBasedHsr;
		boolean flag3 = Config.optiZ;
		if (j != 1) {
			float f16 = j;
			f *= f16;
			f1 *= f16;
			f2 *= f16;
			f3 *= f16;
			f4 *= f16;
			f5 *= f16;
		}
		int ai[] = texture.texels;
		int ai1[] = framebuffer.pixels;
		int ai2[] = framebuffer.zbuffer;
		int k = framebuffer.xstart[i];
		int l = framebuffer.xend[i];
		float f17 = framebuffer.zhigh[i];
		int i1 = framebuffer.exXstart[i];
		int j1 = framebuffer.exXend[i];
		float f18 = framebuffer.exZlow[i];
		int k1 = framebuffer.exXstart2[i];
		int l1 = framebuffer.exXend2[i];
		float f19 = framebuffer.exZlow2[i];
		if (f7 < f6) {
			float f20 = f6;
			f6 = f7;
			f7 = f20;
			f20 = f8;
			f8 = f9;
			f9 = f20;
			f20 = f10;
			f10 = f11;
			f11 = f20;
			f20 = f12;
			f12 = f13;
			f13 = f20;
			f20 = f;
			f = f1;
			f1 = f20;
			f20 = f2;
			f2 = f3;
			f3 = f20;
			f20 = f4;
			f4 = f5;
			f5 = f20;
		}
		f7--;
		if (f6 < f15 && f7 >= f14) {
			if (j1 <= l1) {
				if (j1 >= k1) {
					j1 = l1;
					framebuffer.exXend[i] = framebuffer.exXend2[i];
					if (k1 < i1) {
						i1 = k1;
						framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					}
					if (f19 < f18) {
						f18 = f19;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
					framebuffer.exXstart2[i] = 0x5f5e0ff;
					k1 = 0x5f5e0ff;
					framebuffer.exXend2[i] = -1;
					l1 = -1;
					framebuffer.exZlow2[i] = 2.147484E+009F;
					f19 = 2.147484E+009F;
				}
			} else if (l1 >= i1) {
				if (k1 < i1) {
					i1 = k1;
					framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					if (f19 < f18) {
						f18 = f19;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
				}
				framebuffer.exXstart2[i] = 0x5f5e0ff;
				k1 = 0x5f5e0ff;
				framebuffer.exXend2[i] = -1;
				l1 = -1;
				framebuffer.exZlow2[i] = 2.147484E+009F;
				f19 = 2.147484E+009F;
			}
			if ((!flag2 || (float) i1 > f6 || (float) j1 < f7 || f18 < f12 || f18 < f13)
					&& ((float) k1 > f6 || (float) l1 < f7 || f19 < f12 || f19 < f13)) {
				if (f12 > 1.0F)
					f12 = 1.0F;
				if (f13 > 1.0F)
					f13 = 1.0F;
				float f21 = f12;
				float f22 = f7 - f6;
				if (f22 < 1.0F) {
					f1 = f;
					f3 = f2;
					f5 = f4;
				}
				float f23;
				float f24;
				float f25;
				float f26;
				float f27;
				float f28;
				if (f22 != 0.0F) {
					float f38 = 1.0F / f22;
					f23 = (f9 - f8) * f38;
					f24 = (f11 - f10) * f38;
					f25 = (f13 - f12) * f38;
					f27 = (f1 - f) * f38;
					f28 = (f3 - f2) * f38;
					f26 = (f5 - f4) * f38;
				} else {
					f23 = 0.0F;
					f24 = 0.0F;
					f25 = 0.0F;
					f26 = 0.0F;
					f27 = 0.0F;
					f28 = 0.0F;
				}
				if (f7 >= f15)
					f7 = f15 - 1.0F;
				if (f6 < f14) {
					f6 = f14 - f6;
					f += f6 * f27;
					f2 += f6 * f28;
					f4 += f6 * f26;
					f8 += f6 * f23;
					f10 += f6 * f24;
					f12 += f6 * f25;
					f6 = f14;
				}
				int i2 = i * framebuffer.width;
				int j2 = (int) f6;
				int l2 = j2;
				int i3 = j2 - 1;
				int j3 = (int) f7;
				int k3 = 16;
				float f29 = 16384F;
				float f30 = 16384F;
				float f31;
				if (Config.zTrick)
					f31 = 1.342177E+008F;
				else
					f31 = 2.684355E+008F;
				float f32 = 16F * f23;
				float f33 = 16F * f24;
				float f34 = 16F * f25;
				float f35 = 16F * f27;
				float f36 = 16F * f28;
				float f37 = 16F * f26;
				float f39 = 1.0F / f12;
				float f41 = f8 * f39;
				float f42 = f10 * f39;
				float f43 = f * f39;
				float f44 = f2 * f39;
				float f45 = f4 * f39;
				byte byte0 = texture.shifter;
				int j4 = (int) (f43 * 262144F);
				int k4 = (int) (f44 * 262144F);
				int l4 = (int) (f45 * 262144F);
				int i5 = 0;
				boolean flag4 = false;
				if (Config.zTrick) {
					i5 = (int) (f12 * 2.147484E+009F);
					if ((framebuffer.frameCount & 1L) == 1L) {
						i5 = -i5;
						flag4 = true;
					}
				} else {
					i5 = (int) (f12 * 4.294967E+009F + -2.147484E+009F);
				}
				int l3 = (int) ((double) f41 * 262144D);
				int i4 = (int) ((double) f42 * 262144D);
				boolean flag5 = false;
				boolean flag6 = false;
				boolean flag7 = false;
				int i6 = i & 1;
				int k6 = 1 - i6 << 17;
				int l6 = i6 << 17;
				int i7 = texture.width - 1 << 18;
				int j7 = texture.height - 1 << 18;
				do {
					int k2 = i3 + 1;
					i3 += k3;
					if (i3 > j3) {
						k3 -= i3 - j3;
						i3 = j3;
						if (k3 != 0) {
							f32 = (float) k3 * f23;
							f33 = (float) k3 * f24;
							f34 = (float) k3 * f25;
							f35 = (float) k3 * f27;
							f36 = (float) k3 * f28;
							f37 = (float) k3 * f26;
							f30 = multiLU[k3];
							f29 = f30;
							if (Config.zTrick)
								f31 = multiZTLU[k3];
							else
								f31 = multiZLU[k3];
						} else {
							f32 = 0.0F;
							f33 = 0.0F;
							f34 = 0.0F;
							f35 = 0.0F;
							f36 = 0.0F;
							f37 = 0.0F;
							k3 = 1;
							f30 = 262144F;
							f29 = 262144F;
							if (Config.zTrick)
								f31 = 2.147484E+009F;
							else
								f31 = 4.294967E+009F;
						}
					}
					if (i3 == j3 && f7 < f15 - 1.0F) {
						f32 = f9 - f8;
						f33 = f11 - f10;
						f34 = f13 - f12;
					}
					float f46 = f12;
					f8 += f32;
					f10 += f33;
					f12 += f34;
					f += f35;
					f2 += f36;
					f4 += f37;
					float f40 = 1.0F / f12;
					float f47 = f8 * f40;
					float f48 = f10 * f40;
					float f49 = f * f40;
					float f50 = f2 * f40;
					float f51 = f4 * f40;
					int k7 = (int) (f31 * (f12 - f46));
					if (flag4)
						k7 = -k7;
					int l7 = (int) (f29 * (f47 - f41));
					int i8 = (int) (f29 * (f48 - f42));
					int j5 = (int) (f30 * (f49 - f43));
					int k5 = (int) (f30 * (f50 - f44));
					int l5 = (int) (f30 * (f51 - f45));
					if (flag2
							&& (i1 <= k2 && j1 >= i3 && f18 >= f46 && f18 >= f12 || k1 <= k2 && l1 >= i3 && f19 >= f46 && f19 >= f12)) {
						if (i3 != j3) {
							i5 += k3 * k7;
							l3 += k3 * l7;
							i4 += k3 * i8;
							j4 += k3 * j5;
							k4 += k3 * k5;
							l4 += k3 * l5;
						}
					} else {
						int j8 = i3 + i2;
						boolean flag10 = false;
						boolean flag11 = false;
						boolean flag12 = false;
						boolean flag13 = false;
						if (flag1) {
							if (!flag && l7 < 0x30000 && l7 > 0xfffd0000 && i8 <= 0x30000 && i8 >= 0xfffd0000)
								flag = true;
						} else {
							flag = false;
						}
						if (flag) {
							int j6;
							if ((k2 & 1) != 0)
								j6 = 0x10000 + k6;
							else
								j6 = l6;
							if (flag3 && (k > i3 || l < k2 || f46 >= f17 && f12 >= f17)) {
								for (int k14 = k2 + i2; k14 <= j8; k14++) {
									int k8 = ai[((l3 + j6 & i7) >> 18) + (((i4 + (j6 ^ 0x10000) & j7) >> 18) << byte0)];
									j6 ^= 0x30000;
									int i13 = (k8 >> 16) * (j4 >> 10) >> 16;
									int k11 = (k8 >> 8 & 0xff) * (k4 >> 10) >> 16;
									int i10 = (k8 & 0xff) * (l4 >> 10) >> 16;
									if ((i13 & 0xffffff00) != 0)
										i13 = 255 >> (i13 >> 28 & 8);
									if ((k11 & 0xffffff00) != 0)
										k11 = 255 >> (k11 >> 28 & 8);
									if ((i10 & 0xffffff00) != 0)
										i10 = 255 >> (i10 >> 28 & 8);
									ai1[k14] = i10 | k11 << 8 | i13 << 16 | 0xff000000;
									ai2[k14] = i5;
									l3 += l7;
									i4 += i8;
									j4 += j5;
									k4 += k5;
									l4 += l5;
									i5 += k7;
								}

							} else if (flag4) {
								for (int l14 = k2 + i2; l14 <= j8; l14++) {
									if (ai2[l14] > i5) {
										int l8 = ai[((l3 + j6 & i7) >> 18) + (((i4 + (j6 ^ 0x10000) & j7) >> 18) << byte0)];
										j6 ^= 0x30000;
										int j13 = (l8 >> 16) * (j4 >> 10) >> 16;
										int l11 = (l8 >> 8 & 0xff) * (k4 >> 10) >> 16;
										int j10 = (l8 & 0xff) * (l4 >> 10) >> 16;
										if ((j13 & 0xffffff00) != 0)
											j13 = 255 >> (j13 >> 28 & 8);
										if ((l11 & 0xffffff00) != 0)
											l11 = 255 >> (l11 >> 28 & 8);
										if ((j10 & 0xffffff00) != 0)
											j10 = 255 >> (j10 >> 28 & 8);
										ai1[l14] = j10 | l11 << 8 | j13 << 16 | 0xff000000;
										ai2[l14] = i5;
									}
									l3 += l7;
									i4 += i8;
									j4 += j5;
									k4 += k5;
									l4 += l5;
									i5 += k7;
								}

							} else {
								for (int i15 = k2 + i2; i15 <= j8; i15++) {
									if (ai2[i15] < i5) {
										int i9 = ai[((l3 + j6 & i7) >> 18) + (((i4 + (j6 ^ 0x10000) & j7) >> 18) << byte0)];
										j6 ^= 0x30000;
										int k13 = (i9 >> 16) * (j4 >> 10) >> 16;
										int i12 = (i9 >> 8 & 0xff) * (k4 >> 10) >> 16;
										int k10 = (i9 & 0xff) * (l4 >> 10) >> 16;
										if ((k13 & 0xffffff00) != 0)
											k13 = 255 >> (k13 >> 28 & 8);
										if ((i12 & 0xffffff00) != 0)
											i12 = 255 >> (i12 >> 28 & 8);
										if ((k10 & 0xffffff00) != 0)
											k10 = 255 >> (k10 >> 28 & 8);
										ai1[i15] = k10 | i12 << 8 | k13 << 16 | 0xff000000;
										ai2[i15] = i5;
									}
									l3 += l7;
									i4 += i8;
									j4 += j5;
									k4 += k5;
									l4 += l5;
									i5 += k7;
								}

							}
						} else if (flag3 && (k > i3 || l < k2 || f46 >= f17 && f12 >= f17)) {
							for (int j15 = k2 + i2; j15 <= j8; j15++) {
								int j9 = ai[((l3 & i7) >> 18) + (((i4 & j7) >> 18) << byte0)];
								int l13 = (j9 >> 16) * (j4 >> 10) >> 16;
								int j12 = (j9 >> 8 & 0xff) * (k4 >> 10) >> 16;
								int l10 = (j9 & 0xff) * (l4 >> 10) >> 16;
								if ((l13 & 0xffffff00) != 0)
									l13 = 255 >> (byte) (l13 >> 28 & 8);
								if ((j12 & 0xffffff00) != 0)
									j12 = 255 >> (byte) (j12 >> 28 & 8);
								if ((l10 & 0xffffff00) != 0)
									l10 = 255 >> (byte) (l10 >> 28 & 8);
								ai1[j15] = l10 | j12 << 8 | l13 << 16 | 0xff000000;
								ai2[j15] = i5;
								l3 += l7;
								i4 += i8;
								j4 += j5;
								k4 += k5;
								l4 += l5;
								i5 += k7;
							}

						} else if (flag4) {
							for (int k15 = k2 + i2; k15 <= j8; k15++) {
								if (ai2[k15] > i5) {
									int k9 = ai[((l3 & i7) >> 18) + (((i4 & j7) >> 18) << byte0)];
									int i14 = (k9 >> 16) * (j4 >> 10) >> 16;
									int k12 = (k9 >> 8 & 0xff) * (k4 >> 10) >> 16;
									int i11 = (k9 & 0xff) * (l4 >> 10) >> 16;
									if ((i14 & 0xffffff00) != 0)
										i14 = 255 >> (byte) (i14 >> 28 & 8);
									if ((k12 & 0xffffff00) != 0)
										k12 = 255 >> (byte) (k12 >> 28 & 8);
									if ((i11 & 0xffffff00) != 0)
										i11 = 255 >> (byte) (i11 >> 28 & 8);
									ai1[k15] = i11 | k12 << 8 | i14 << 16 | 0xff000000;
									ai2[k15] = i5;
								}
								l3 += l7;
								i4 += i8;
								j4 += j5;
								k4 += k5;
								l4 += l5;
								i5 += k7;
							}

						} else {
							for (int l15 = k2 + i2; l15 <= j8; l15++) {
								if (ai2[l15] < i5) {
									int l9 = ai[((l3 & i7) >> 18) + (((i4 & j7) >> 18) << byte0)];
									int j14 = (l9 >> 16) * (j4 >> 10) >> 16;
									int l12 = (l9 >> 8 & 0xff) * (k4 >> 10) >> 16;
									int j11 = (l9 & 0xff) * (l4 >> 10) >> 16;
									if ((j14 & 0xffffff00) != 0)
										j14 = 255 >> (byte) (j14 >> 28 & 8);
									if ((l12 & 0xffffff00) != 0)
										l12 = 255 >> (byte) (l12 >> 28 & 8);
									if ((j11 & 0xffffff00) != 0)
										j11 = 255 >> (byte) (j11 >> 28 & 8);
									ai1[l15] = j11 | l12 << 8 | j14 << 16 | 0xff000000;
									ai2[l15] = i5;
								}
								l3 += l7;
								i4 += i8;
								j4 += j5;
								k4 += k5;
								l4 += l5;
								i5 += k7;
							}

						}
					}
					f41 = f47;
					f42 = f48;
					f43 = f49;
					f44 = f50;
					f45 = f51;
				} while (i3 < j3);
				boolean flag8 = false;
				if (l2 < i1 && j3 >= i1 || i1 == 0x5f5e0ff) {
					framebuffer.exXstart[i] = l2;
					if (f18 > f21)
						framebuffer.exZlow[i] = f21;
					if (framebuffer.exZlow[i] > f13)
						framebuffer.exZlow[i] = f13;
					flag8 = true;
				}
				if (j3 > j1 && f6 <= (float) j1 || j1 == -1) {
					framebuffer.exXend[i] = j3;
					if (framebuffer.exZlow[i] > f13)
						framebuffer.exZlow[i] = f13;
					if (framebuffer.exZlow[i] > f21)
						framebuffer.exZlow[i] = f21;
					flag8 = true;
				}
				if (!flag8) {
					if (l2 < k1 && j3 >= k1 || k1 == 0x5f5e0ff) {
						framebuffer.exXstart2[i] = l2;
						if (f19 > f21)
							framebuffer.exZlow2[i] = f21;
						if (framebuffer.exZlow2[i] > f13)
							framebuffer.exZlow2[i] = f13;
					}
					if (j3 > l1 && f6 <= (float) l1 || l1 == -1) {
						framebuffer.exXend2[i] = j3;
						if (framebuffer.exZlow2[i] > f13)
							framebuffer.exZlow2[i] = f13;
						if (framebuffer.exZlow2[i] > f21)
							framebuffer.exZlow2[i] = f21;
						boolean flag9 = true;
					}
				}
				if (f6 < (float) k)
					framebuffer.xstart[i] = l2;
				if (j3 > l)
					framebuffer.xend[i] = j3;
				if (f13 < f21)
					f13 = f21;
				if (f13 > f17)
					framebuffer.zhigh[i] = f13;
			}
		}
	}

	public void drawWireframe(VisList vislist, int i, int j, FrameBuffer framebuffer, World world) {
		int k = vislist.vnum[i];
		Object3D object3d = vislist.vobj[i];
		int l = object3d.objMesh.coords[object3d.objMesh.points[k][0]];
		int i1 = object3d.objMesh.coords[object3d.objMesh.points[k][1]];
		int j1 = object3d.objMesh.coords[object3d.objMesh.points[k][2]];
		float f = object3d.objVectors.sx[l];
		float f1 = object3d.objVectors.sx[i1];
		float f2 = object3d.objVectors.sx[j1];
		float f3 = object3d.objVectors.sy[l];
		float f4 = object3d.objVectors.sy[i1];
		float f5 = object3d.objVectors.sy[j1];
		framebuffer.drawLine(f, f3, f1, f4, j);
		framebuffer.drawLine(f1, f4, f2, f5, j);
		framebuffer.drawLine(f, f3, f2, f5, j);
	}

	private final void drawShadedZbufferedFilteredBumpmappedBlendedScanline(float f, float f1, float f2, float f3,
			float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, int i,
			float f14, float f15, FrameBuffer framebuffer, Texture texture, boolean flag, float f16, float f17, float f18,
			float f19, Texture texture1, Texture texture2, int j) {
		if (j != 1) {
			float f20 = j;
			f *= f20;
			f1 *= f20;
			f2 *= f20;
			f3 *= f20;
			f4 *= f20;
			f5 *= f20;
		}
		int ai[] = texture.texels;
		int ai1[] = texture1.texels;
		int ai2[] = texture2.texels;
		int ai3[] = framebuffer.pixels;
		int ai4[] = framebuffer.zbuffer;
		int k = framebuffer.xstart[i];
		int l = framebuffer.xend[i];
		float f21 = framebuffer.zhigh[i];
		int i1 = framebuffer.exXstart[i];
		int j1 = framebuffer.exXend[i];
		float f22 = framebuffer.exZlow[i];
		int k1 = framebuffer.exXstart2[i];
		int l1 = framebuffer.exXend2[i];
		float f23 = framebuffer.exZlow2[i];
		if (f7 < f6) {
			float f24 = f6;
			f6 = f7;
			f7 = f24;
			f24 = f8;
			f8 = f9;
			f9 = f24;
			f24 = f10;
			f10 = f11;
			f11 = f24;
			f24 = f12;
			f12 = f13;
			f13 = f24;
			f24 = f16;
			f16 = f17;
			f17 = f24;
			f24 = f18;
			f18 = f19;
			f19 = f24;
			f24 = f;
			f = f1;
			f1 = f24;
			f24 = f2;
			f2 = f3;
			f3 = f24;
			f24 = f4;
			f4 = f5;
			f5 = f24;
		}
		f7--;
		if (f6 < f15 && f7 >= f14) {
			if (j1 <= l1) {
				if (j1 >= k1) {
					j1 = l1;
					framebuffer.exXend[i] = framebuffer.exXend2[i];
					if (k1 < i1) {
						i1 = k1;
						framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					}
					if (f23 < f22) {
						f22 = f23;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
					framebuffer.exXstart2[i] = 0x5f5e0ff;
					k1 = 0x5f5e0ff;
					framebuffer.exXend2[i] = -1;
					l1 = -1;
					framebuffer.exZlow2[i] = 2.147484E+009F;
					f23 = 2.147484E+009F;
				}
			} else if (l1 >= i1) {
				if (k1 < i1) {
					i1 = k1;
					framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					if (f23 < f22) {
						f22 = f23;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
				}
				framebuffer.exXstart2[i] = 0x5f5e0ff;
				k1 = 0x5f5e0ff;
				framebuffer.exXend2[i] = -1;
				l1 = -1;
				framebuffer.exZlow2[i] = 2.147484E+009F;
				f23 = 2.147484E+009F;
			}
			if ((!Config.spanBasedHsr || (float) i1 > f6 || (float) j1 < f7 || f22 < f12 || f22 < f13)
					&& ((float) k1 > f6 || (float) l1 < f7 || f23 < f12 || f23 < f13)) {
				if (f12 > 1.0F)
					f12 = 1.0F;
				if (f13 > 1.0F)
					f13 = 1.0F;
				float f25 = f7 - f6;
				float f35 = f12;
				if (f25 < 1.0F) {
					f1 = f;
					f3 = f2;
					f5 = f4;
				}
				float f26;
				float f27;
				float f28;
				float f29;
				float f30;
				float f31;
				float f33;
				float f34;
				if (f25 != 0.0F) {
					float f36 = 1.0F / f25;
					f26 = (f9 - f8) * f36;
					f27 = (f11 - f10) * f36;
					f33 = (f17 - f16) * f36;
					f34 = (f19 - f18) * f36;
					f28 = (f13 - f12) * f36;
					f30 = (f1 - f) * f36;
					f31 = (f3 - f2) * f36;
					f29 = (f5 - f4) * f36;
				} else {
					f26 = 0.0F;
					f27 = 0.0F;
					f28 = 0.0F;
					f29 = 0.0F;
					f30 = 0.0F;
					f31 = 0.0F;
					f33 = 0.0F;
					f34 = 0.0F;
				}
				if (f7 >= f15)
					f7 = f15 - 1.0F;
				if (f6 < f14) {
					f6 = f14 - f6;
					f += f6 * f30;
					f2 += f6 * f31;
					f4 += f6 * f29;
					f8 += f6 * f26;
					f10 += f6 * f27;
					f16 += f6 * f33;
					f18 += f6 * f34;
					f12 += f6 * f28;
					f6 = f14;
				}
				int i2 = i * framebuffer.width;
				int j2 = (int) f6;
				int l2 = j2;
				int i3 = j2 - 1;
				int j3 = (int) f7;
				int k3 = 16;
				float f37 = 16384F;
				float f38 = 16384F;
				float f32;
				if (Config.zTrick)
					f32 = 1.342177E+008F;
				else
					f32 = 2.684355E+008F;
				float f39 = 16F * f26;
				float f40 = 16F * f27;
				float f41 = 16F * f28;
				float f42 = 16F * f30;
				float f43 = 16F * f31;
				float f44 = 16F * f29;
				float f45 = 16F * f33;
				float f46 = 16F * f34;
				float f47 = 1.0F / f12;
				float f49 = f8 * f47;
				float f50 = f10 * f47;
				float f51 = f16 * f47;
				float f52 = f18 * f47;
				float f53 = f * f47;
				float f54 = f2 * f47;
				float f55 = f4 * f47;
				byte byte0 = texture.shifter;
				byte byte1 = texture1.shifter;
				int j4 = (int) (f53 * 262144F);
				int k4 = (int) (f54 * 262144F);
				int l4 = (int) (f55 * 262144F);
				int i5 = 0;
				boolean flag1 = false;
				if (Config.zTrick) {
					i5 = (int) (f12 * 2.147484E+009F);
					if ((framebuffer.frameCount & 1L) == 1L) {
						i5 = -i5;
						flag1 = true;
					}
				} else {
					i5 = (int) (f12 * 4.294967E+009F + -2.147484E+009F);
				}
				int l3 = (int) (f49 * 262144F);
				int i4 = (int) (f50 * 262144F);
				int j5 = (int) (f51 * 262144F);
				int k5 = (int) (f52 * 262144F);
				boolean flag2 = false;
				boolean flag3 = false;
				boolean flag4 = false;
				int k6 = i & 1;
				int i7 = 1 - k6 << 17;
				int j7 = k6 << 17;
				do {
					int k2 = i3 + 1;
					i3 += k3;
					if (i3 > j3) {
						k3 -= i3 - j3;
						i3 = j3;
						if (k3 != 0) {
							f39 = (float) k3 * f26;
							f40 = (float) k3 * f27;
							f45 = (float) k3 * f33;
							f46 = (float) k3 * f34;
							f41 = (float) k3 * f28;
							f42 = (float) k3 * f30;
							f43 = (float) k3 * f31;
							f44 = (float) k3 * f29;
							f38 = multiLU[k3];
							f37 = f38;
							if (Config.zTrick)
								f32 = multiZTLU[k3];
							else
								f32 = multiZLU[k3];
						} else {
							f39 = 0.0F;
							f40 = 0.0F;
							f45 = 0.0F;
							f46 = 0.0F;
							f41 = 0.0F;
							f42 = 0.0F;
							f43 = 0.0F;
							f44 = 0.0F;
							k3 = 1;
							f38 = 262144F;
							f37 = 262144F;
							if (Config.zTrick)
								f32 = 2.147484E+009F;
							else
								f32 = 4.294967E+009F;
						}
					}
					if (i3 == j3 && f7 < f15 - 1.0F) {
						f39 = f9 - f8;
						f40 = f11 - f10;
						f41 = f13 - f12;
						f45 = f17 - f16;
						f46 = f19 - f18;
					}
					float f56 = f12;
					f8 += f39;
					f10 += f40;
					f16 += f45;
					f18 += f46;
					f12 += f41;
					f += f42;
					f2 += f43;
					f4 += f44;
					float f48 = 1.0F / f12;
					float f57 = f8 * f48;
					float f58 = f10 * f48;
					float f59 = f16 * f48;
					float f60 = f18 * f48;
					float f61 = f * f48;
					float f62 = f2 * f48;
					float f63 = f4 * f48;
					int k7 = texture.width - 1 << 18;
					int l7 = texture.height - 1 << 18;
					int i8 = texture1.width - 1 << 18;
					int j8 = texture1.height - 1 << 18;
					int k8 = (int) (f32 * (f12 - f56));
					if (flag1)
						k8 = -k8;
					int l8 = (int) (f37 * (f57 - f49));
					int i9 = (int) (f37 * (f58 - f50));
					int j9 = (int) (f37 * (f59 - f51));
					int k9 = (int) (f37 * (f60 - f52));
					int l5 = (int) (f38 * (f61 - f53));
					int i6 = (int) (f38 * (f62 - f54));
					int j6 = (int) (f38 * (f63 - f55));
					if (Config.spanBasedHsr
							&& (i1 <= k2 && j1 >= i3 && f22 >= f56 && f22 >= f12 || k1 <= k2 && l1 >= i3 && f23 >= f56 && f23 >= f12)) {
						if (i3 != j3) {
							i5 += k3 * k8;
							l3 += k3 * l8;
							i4 += k3 * i9;
							j5 += k3 * j9;
							k5 += k3 * k9;
							j4 += k3 * l5;
							k4 += k3 * i6;
							l4 += k3 * j6;
						}
					} else {
						int l9 = i3 + i2;
						boolean flag7 = false;
						boolean flag8 = false;
						boolean flag9 = false;
						boolean flag10 = false;
						if (Config.texelFilter) {
							if (!flag && l8 < 0x30000 && l8 > 0xfffd0000 && i9 <= 0x30000 && i9 >= 0xfffd0000)
								flag = true;
							if (!flag && j9 < 0x30000 && j9 > 0xfffd0000 && k9 <= 0x30000 && k9 >= 0xfffd0000)
								flag = true;
						} else {
							flag = false;
						}
						if (flag) {
							int l6;
							if ((k2 & 1) != 0)
								l6 = 0x10000 + i7;
							else
								l6 = j7;
							if (Config.optiZ && (k > i3 || l < k2 || f56 >= f21 && f12 >= f21)) {
								for (int i16 = k2 + i2; i16 <= l9; i16++) {
									int k17 = ((j5 + l6 & i8) >> 18) + (((k5 + (l6 ^ 0x10000) & j8) >> 18) << byte1);
									int i19 = ai1[k17];
									int k20 = ai2[k17];
									int i22 = (((l3 + l6 & k7) >> 18) - 128) + (i19 >> 8)
											+ ((((i4 + (l6 ^ 0x10000) & l7) >> 18) - 128) + (i19 & 0xff) << byte0);
									l6 ^= 0x30000;
									if (i22 < 0)
										i22 = 0;
									else if (i22 >= texture.intSize)
										i22 = texture.intSize - 1;
									int i10 = ai[i22];
									int k23 = k20 >> 16;
									int i25 = k20 >> 8 & 0xff;
									int k26 = k20 & 0xff;
									int k14 = (k23 * (j4 >> 10) >> 16) + (i10 >> 16) >> 1;
									int i13 = (i25 * (k4 >> 10) >> 16) + (i10 >> 8 & 0xff) >> 1;
									int k11 = (k26 * (l4 >> 10) >> 16) + (i10 & 0xff) >> 1;
									if ((k14 & 0xffffff00) != 0)
										k14 = 255 >> (k14 >> 28 & 8);
									if ((i13 & 0xffffff00) != 0)
										i13 = 255 >> (i13 >> 28 & 8);
									if ((k11 & 0xffffff00) != 0)
										k11 = 255 >> (k11 >> 28 & 8);
									ai3[i16] = k11 | i13 << 8 | k14 << 16 | 0xff000000;
									ai4[i16] = i5;
									l3 += l8;
									i4 += i9;
									j5 += j9;
									k5 += k9;
									j4 += l5;
									k4 += i6;
									l4 += j6;
									i5 += k8;
								}

							} else if (!flag1) {
								for (int j16 = k2 + i2; j16 <= l9; j16++) {
									if (ai4[j16] < i5) {
										int l17 = ((j5 + l6 & i8) >> 18) + (((k5 + (l6 ^ 0x10000) & j8) >> 18) << byte1);
										int j19 = ai1[l17];
										int l20 = ai2[l17];
										int j22 = (((l3 + l6 & k7) >> 18) - 128) + (j19 >> 8)
												+ ((((i4 + (l6 ^ 0x10000) & l7) >> 18) - 128) + (j19 & 0xff) << byte0);
										l6 ^= 0x30000;
										if (j22 < 0)
											j22 = 0;
										else if (j22 >= texture.intSize)
											j22 = texture.intSize - 1;
										int j10 = ai[j22];
										int l23 = l20 >> 16;
										int j25 = l20 >> 8 & 0xff;
										int l26 = l20 & 0xff;
										int l14 = (l23 * (j4 >> 10) >> 16) + (j10 >> 16) >> 1;
										int j13 = (j25 * (k4 >> 10) >> 16) + (j10 >> 8 & 0xff) >> 1;
										int l11 = (l26 * (l4 >> 10) >> 16) + (j10 & 0xff) >> 1;
										if ((l14 & 0xffffff00) != 0)
											l14 = 255 >> (l14 >> 28 & 8);
										if ((j13 & 0xffffff00) != 0)
											j13 = 255 >> (j13 >> 28 & 8);
										if ((l11 & 0xffffff00) != 0)
											l11 = 255 >> (l11 >> 28 & 8);
										ai3[j16] = l11 | j13 << 8 | l14 << 16 | 0xff000000;
										ai4[j16] = i5;
									}
									l3 += l8;
									i4 += i9;
									j5 += j9;
									k5 += k9;
									j4 += l5;
									k4 += i6;
									l4 += j6;
									i5 += k8;
								}

							} else {
								for (int k16 = k2 + i2; k16 <= l9; k16++) {
									if (ai4[k16] > i5) {
										int i18 = ((j5 + l6 & i8) >> 18) + (((k5 + (l6 ^ 0x10000) & j8) >> 18) << byte1);
										int k19 = ai1[i18];
										int i21 = ai2[i18];
										int k22 = (((l3 + l6 & k7) >> 18) - 128) + (k19 >> 8)
												+ ((((i4 + (l6 ^ 0x10000) & l7) >> 18) - 128) + (k19 & 0xff) << byte0);
										l6 ^= 0x30000;
										if (k22 < 0)
											k22 = 0;
										else if (k22 >= texture.intSize)
											k22 = texture.intSize - 1;
										int k10 = ai[k22];
										int i24 = i21 >> 16;
										int k25 = i21 >> 8 & 0xff;
										int i27 = i21 & 0xff;
										int i15 = (i24 * (j4 >> 10) >> 16) + (k10 >> 16) >> 1;
										int k13 = (k25 * (k4 >> 10) >> 16) + (k10 >> 8 & 0xff) >> 1;
										int i12 = (i27 * (l4 >> 10) >> 16) + (k10 & 0xff) >> 1;
										if ((i15 & 0xffffff00) != 0)
											i15 = 255 >> (i15 >> 28 & 8);
										if ((k13 & 0xffffff00) != 0)
											k13 = 255 >> (k13 >> 28 & 8);
										if ((i12 & 0xffffff00) != 0)
											i12 = 255 >> (i12 >> 28 & 8);
										ai3[k16] = i12 | k13 << 8 | i15 << 16 | 0xff000000;
										ai4[k16] = i5;
									}
									l3 += l8;
									i4 += i9;
									j5 += j9;
									k5 += k9;
									j4 += l5;
									k4 += i6;
									l4 += j6;
									i5 += k8;
								}

							}
						} else if (Config.optiZ && (k > i3 || l < k2 || f56 >= f21 && f12 >= f21)) {
							for (int l16 = k2 + i2; l16 <= l9; l16++) {
								int j18 = ((j5 & i8) >> 18) + (((k5 & j8) >> 18) << byte1);
								int l19 = ai1[j18];
								int j21 = ai2[j18];
								int l22 = (((l3 & k7) >> 18) - 128) + (l19 >> 8) + ((((i4 & l7) >> 18) - 128) + (l19 & 0xff) << byte0);
								if (l22 >= texture.intSize)
									l22 = texture.intSize - 1;
								else if (l22 < 0)
									l22 = 0;
								int l10 = ai[l22];
								int j24 = j21 >> 16;
								int l25 = j21 >> 8 & 0xff;
								int j27 = j21 & 0xff;
								int j15 = (j24 * (j4 >> 10) >> 16) + (l10 >> 16) >> 1;
								int l13 = (l25 * (k4 >> 10) >> 16) + (l10 >> 8 & 0xff) >> 1;
								int j12 = (j27 * (l4 >> 10) >> 16) + (l10 & 0xff) >> 1;
								if ((j15 & 0xffffff00) != 0)
									j15 = 255 >> (byte) (j15 >> 28 & 8);
								if ((l13 & 0xffffff00) != 0)
									l13 = 255 >> (byte) (l13 >> 28 & 8);
								if ((j12 & 0xffffff00) != 0)
									j12 = 255 >> (byte) (j12 >> 28 & 8);
								ai3[l16] = j12 | l13 << 8 | j15 << 16 | 0xff000000;
								ai4[l16] = i5;
								l3 += l8;
								i4 += i9;
								j5 += j9;
								k5 += k9;
								j4 += l5;
								k4 += i6;
								l4 += j6;
								i5 += k8;
							}

						} else if (!flag1) {
							for (int i17 = k2 + i2; i17 <= l9; i17++) {
								if (ai4[i17] < i5) {
									int k18 = ((j5 & i8) >> 18) + (((k5 & j8) >> 18) << byte1);
									int i20 = ai1[k18];
									int k21 = ai2[k18];
									int i23 = (((l3 & k7) >> 18) - 128) + (i20 >> 8)
											+ ((((i4 & l7) >> 18) - 128) + (i20 & 0xff) << byte0);
									if (i23 >= texture.intSize)
										i23 = texture.intSize - 1;
									else if (i23 < 0)
										i23 = 0;
									int i11 = ai[i23];
									int k24 = k21 >> 16;
									int i26 = k21 >> 8 & 0xff;
									int k27 = k21 & 0xff;
									int k15 = (k24 * (j4 >> 10) >> 16) + (i11 >> 16) >> 1;
									int i14 = (i26 * (k4 >> 10) >> 16) + (i11 >> 8 & 0xff) >> 1;
									int k12 = (k27 * (l4 >> 10) >> 16) + (i11 & 0xff) >> 1;
									if ((k15 & 0xffffff00) != 0)
										k15 = 255 >> (byte) (k15 >> 28 & 8);
									if ((i14 & 0xffffff00) != 0)
										i14 = 255 >> (byte) (i14 >> 28 & 8);
									if ((k12 & 0xffffff00) != 0)
										k12 = 255 >> (byte) (k12 >> 28 & 8);
									ai3[i17] = k12 | i14 << 8 | k15 << 16 | 0xff000000;
									ai4[i17] = i5;
								}
								l3 += l8;
								i4 += i9;
								j5 += j9;
								k5 += k9;
								j4 += l5;
								k4 += i6;
								l4 += j6;
								i5 += k8;
							}

						} else {
							for (int j17 = k2 + i2; j17 <= l9; j17++) {
								if (ai4[j17] > i5) {
									int l18 = ((j5 & i8) >> 18) + (((k5 & j8) >> 18) << byte1);
									int j20 = ai1[l18];
									int l21 = ai2[l18];
									int j23 = (((l3 & k7) >> 18) - 128) + (j20 >> 8)
											+ ((((i4 & l7) >> 18) - 128) + (j20 & 0xff) << byte0);
									if (j23 >= texture.intSize)
										j23 = texture.intSize - 1;
									else if (j23 < 0)
										j23 = 0;
									int j11 = ai[j23];
									int l24 = l21 >> 16;
									int j26 = l21 >> 8 & 0xff;
									int l27 = l21 & 0xff;
									int l15 = (l24 * (j4 >> 10) >> 16) + (j11 >> 16) >> 1;
									int j14 = (j26 * (k4 >> 10) >> 16) + (j11 >> 8 & 0xff) >> 1;
									int l12 = (l27 * (l4 >> 10) >> 16) + (j11 & 0xff) >> 1;
									if ((l15 & 0xffffff00) != 0)
										l15 = 255 >> (byte) (l15 >> 28 & 8);
									if ((j14 & 0xffffff00) != 0)
										j14 = 255 >> (byte) (j14 >> 28 & 8);
									if ((l12 & 0xffffff00) != 0)
										l12 = 255 >> (byte) (l12 >> 28 & 8);
									ai3[j17] = l12 | j14 << 8 | l15 << 16 | 0xff000000;
									ai4[j17] = i5;
								}
								l3 += l8;
								i4 += i9;
								j5 += j9;
								k5 += k9;
								j4 += l5;
								k4 += i6;
								l4 += j6;
								i5 += k8;
							}

						}
					}
					f49 = f57;
					f50 = f58;
					f51 = f59;
					f52 = f60;
					f53 = f61;
					f54 = f62;
					f55 = f63;
				} while (i3 < j3);
				boolean flag5 = false;
				if (l2 < i1 && j3 >= i1 || i1 == 0x5f5e0ff) {
					framebuffer.exXstart[i] = l2;
					if (f22 > f35)
						framebuffer.exZlow[i] = f35;
					if (framebuffer.exZlow[i] > f13)
						framebuffer.exZlow[i] = f13;
					flag5 = true;
				}
				if (j3 > j1 && f6 <= (float) j1 || j1 == -1) {
					framebuffer.exXend[i] = j3;
					if (framebuffer.exZlow[i] > f13)
						framebuffer.exZlow[i] = f13;
					if (framebuffer.exZlow[i] > f35)
						framebuffer.exZlow[i] = f35;
					flag5 = true;
				}
				if (!flag5) {
					if (l2 < k1 && j3 >= k1 || k1 == 0x5f5e0ff) {
						framebuffer.exXstart2[i] = l2;
						if (f23 > f35)
							framebuffer.exZlow2[i] = f35;
						if (framebuffer.exZlow2[i] > f13)
							framebuffer.exZlow2[i] = f13;
					}
					if (j3 > l1 && f6 <= (float) l1 || l1 == -1) {
						framebuffer.exXend2[i] = j3;
						if (framebuffer.exZlow2[i] > f13)
							framebuffer.exZlow2[i] = f13;
						if (framebuffer.exZlow2[i] > f35)
							framebuffer.exZlow2[i] = f35;
						boolean flag6 = true;
					}
				}
				if (f6 < (float) k)
					framebuffer.xstart[i] = l2;
				if (j3 > l)
					framebuffer.xend[i] = j3;
				if (f13 < f35)
					f13 = f35;
				if (f13 > f21)
					framebuffer.zhigh[i] = f13;
			}
		}
	}

	private final void drawShadedZbufferedFilteredBumpmappedScanline(float f, float f1, float f2, float f3, float f4,
			float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, int i, float f14,
			float f15, FrameBuffer framebuffer, Texture texture, boolean flag, float f16, float f17, float f18, float f19,
			Texture texture1, int j) {
		if (j != 1) {
			float f20 = j;
			f *= f20;
			f1 *= f20;
			f2 *= f20;
			f3 *= f20;
			f4 *= f20;
			f5 *= f20;
		}
		int ai[] = texture.texels;
		int ai1[] = texture1.texels;
		int ai2[] = framebuffer.pixels;
		int ai3[] = framebuffer.zbuffer;
		int k = framebuffer.xstart[i];
		int l = framebuffer.xend[i];
		float f21 = framebuffer.zhigh[i];
		int i1 = framebuffer.exXstart[i];
		int j1 = framebuffer.exXend[i];
		float f22 = framebuffer.exZlow[i];
		int k1 = framebuffer.exXstart2[i];
		int l1 = framebuffer.exXend2[i];
		float f23 = framebuffer.exZlow2[i];
		if (f7 < f6) {
			float f24 = f6;
			f6 = f7;
			f7 = f24;
			f24 = f8;
			f8 = f9;
			f9 = f24;
			f24 = f10;
			f10 = f11;
			f11 = f24;
			f24 = f12;
			f12 = f13;
			f13 = f24;
			f24 = f16;
			f16 = f17;
			f17 = f24;
			f24 = f18;
			f18 = f19;
			f19 = f24;
			f24 = f;
			f = f1;
			f1 = f24;
			f24 = f2;
			f2 = f3;
			f3 = f24;
			f24 = f4;
			f4 = f5;
			f5 = f24;
		}
		f7--;
		if (f6 < f15 && f7 >= f14) {
			if (j1 <= l1) {
				if (j1 >= k1) {
					j1 = l1;
					framebuffer.exXend[i] = framebuffer.exXend2[i];
					if (k1 < i1) {
						i1 = k1;
						framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					}
					if (f23 < f22) {
						f22 = f23;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
					framebuffer.exXstart2[i] = 0x5f5e0ff;
					k1 = 0x5f5e0ff;
					framebuffer.exXend2[i] = -1;
					l1 = -1;
					framebuffer.exZlow2[i] = 2.147484E+009F;
					f23 = 2.147484E+009F;
				}
			} else if (l1 >= i1) {
				if (k1 < i1) {
					i1 = k1;
					framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					if (f23 < f22) {
						f22 = f23;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
				}
				framebuffer.exXstart2[i] = 0x5f5e0ff;
				k1 = 0x5f5e0ff;
				framebuffer.exXend2[i] = -1;
				l1 = -1;
				framebuffer.exZlow2[i] = 2.147484E+009F;
				f23 = 2.147484E+009F;
			}
			if ((!Config.spanBasedHsr || (float) i1 > f6 || (float) j1 < f7 || f22 < f12 || f22 < f13)
					&& ((float) k1 > f6 || (float) l1 < f7 || f23 < f12 || f23 < f13)) {
				if (f12 > 1.0F)
					f12 = 1.0F;
				if (f13 > 1.0F)
					f13 = 1.0F;
				float f25 = f7 - f6;
				float f35 = f12;
				if (f25 < 1.0F) {
					f1 = f;
					f3 = f2;
					f5 = f4;
				}
				float f26;
				float f27;
				float f28;
				float f29;
				float f30;
				float f31;
				float f33;
				float f34;
				if (f25 != 0.0F) {
					float f36 = 1.0F / f25;
					f26 = (f9 - f8) * f36;
					f27 = (f11 - f10) * f36;
					f33 = (f17 - f16) * f36;
					f34 = (f19 - f18) * f36;
					f28 = (f13 - f12) * f36;
					f30 = (f1 - f) * f36;
					f31 = (f3 - f2) * f36;
					f29 = (f5 - f4) * f36;
				} else {
					f26 = 0.0F;
					f27 = 0.0F;
					f28 = 0.0F;
					f29 = 0.0F;
					f30 = 0.0F;
					f31 = 0.0F;
					f33 = 0.0F;
					f34 = 0.0F;
				}
				if (f7 >= f15)
					f7 = f15 - 1.0F;
				if (f6 < f14) {
					f6 = f14 - f6;
					f += f6 * f30;
					f2 += f6 * f31;
					f4 += f6 * f29;
					f8 += f6 * f26;
					f10 += f6 * f27;
					f16 += f6 * f33;
					f18 += f6 * f34;
					f12 += f6 * f28;
					f6 = f14;
				}
				int i2 = i * framebuffer.width;
				int j2 = (int) f6;
				int l2 = j2;
				int i3 = j2 - 1;
				int j3 = (int) f7;
				int k3 = 16;
				float f37 = 16384F;
				float f38 = 16384F;
				float f32;
				if (Config.zTrick)
					f32 = 1.342177E+008F;
				else
					f32 = 2.684355E+008F;
				float f39 = 16F * f26;
				float f40 = 16F * f27;
				float f41 = 16F * f28;
				float f42 = 16F * f30;
				float f43 = 16F * f31;
				float f44 = 16F * f29;
				float f45 = 16F * f33;
				float f46 = 16F * f34;
				float f47 = 1.0F / f12;
				float f49 = f8 * f47;
				float f50 = f10 * f47;
				float f51 = f16 * f47;
				float f52 = f18 * f47;
				float f53 = f * f47;
				float f54 = f2 * f47;
				float f55 = f4 * f47;
				byte byte0 = texture.shifter;
				byte byte1 = texture1.shifter;
				int j4 = (int) (f53 * 262144F);
				int k4 = (int) (f54 * 262144F);
				int l4 = (int) (f55 * 262144F);
				int i5 = 0;
				boolean flag1 = false;
				if (Config.zTrick) {
					i5 = (int) (f12 * 2.147484E+009F);
					if ((framebuffer.frameCount & 1L) == 1L) {
						i5 = -i5;
						flag1 = true;
					}
				} else {
					i5 = (int) (f12 * 4.294967E+009F + -2.147484E+009F);
				}
				int l3 = (int) (f49 * 262144F);
				int i4 = (int) (f50 * 262144F);
				int j5 = (int) (f51 * 262144F);
				int k5 = (int) (f52 * 262144F);
				boolean flag2 = false;
				boolean flag3 = false;
				boolean flag4 = false;
				int k6 = i & 1;
				int i7 = 1 - k6 << 17;
				int j7 = k6 << 17;
				do {
					int k2 = i3 + 1;
					i3 += k3;
					if (i3 > j3) {
						k3 -= i3 - j3;
						i3 = j3;
						if (k3 != 0) {
							f39 = (float) k3 * f26;
							f40 = (float) k3 * f27;
							f45 = (float) k3 * f33;
							f46 = (float) k3 * f34;
							f41 = (float) k3 * f28;
							f42 = (float) k3 * f30;
							f43 = (float) k3 * f31;
							f44 = (float) k3 * f29;
							f38 = multiLU[k3];
							f37 = f38;
							if (Config.zTrick)
								f32 = multiZTLU[k3];
							else
								f32 = multiZLU[k3];
						} else {
							f39 = 0.0F;
							f40 = 0.0F;
							f45 = 0.0F;
							f46 = 0.0F;
							f41 = 0.0F;
							f42 = 0.0F;
							f43 = 0.0F;
							f44 = 0.0F;
							k3 = 1;
							f38 = 262144F;
							f37 = 262144F;
							if (Config.zTrick)
								f32 = 2.147484E+009F;
							else
								f32 = 4.294967E+009F;
						}
					}
					float f56 = f12;
					if (i3 == j3 && f7 < f15 - 1.0F) {
						f39 = f9 - f8;
						f40 = f11 - f10;
						f41 = f13 - f12;
						f45 = f17 - f16;
						f46 = f19 - f18;
					}
					f8 += f39;
					f10 += f40;
					f16 += f45;
					f18 += f46;
					f12 += f41;
					f += f42;
					f2 += f43;
					f4 += f44;
					float f48 = 1.0F / f12;
					float f57 = f8 * f48;
					float f58 = f10 * f48;
					float f59 = f16 * f48;
					float f60 = f18 * f48;
					float f61 = f * f48;
					float f62 = f2 * f48;
					float f63 = f4 * f48;
					int k7 = texture.width - 1 << 18;
					int l7 = texture.height - 1 << 18;
					int i8 = texture1.width - 1 << 18;
					int j8 = texture1.height - 1 << 18;
					int k8 = (int) (f32 * (f12 - f56));
					if (flag1)
						k8 = -k8;
					int l8 = (int) (f37 * (f57 - f49));
					int i9 = (int) (f37 * (f58 - f50));
					int j9 = (int) (f37 * (f59 - f51));
					int k9 = (int) (f37 * (f60 - f52));
					int l5 = (int) (f38 * (f61 - f53));
					int i6 = (int) (f38 * (f62 - f54));
					int j6 = (int) (f38 * (f63 - f55));
					if (Config.spanBasedHsr
							&& (i1 <= k2 && j1 >= i3 && f22 >= f56 && f22 >= f12 || k1 <= k2 && l1 >= i3 && f23 >= f56 && f23 >= f12)) {
						if (i3 != j3) {
							i5 += k3 * k8;
							l3 += k3 * l8;
							i4 += k3 * i9;
							j5 += k3 * j9;
							k5 += k3 * k9;
							j4 += k3 * l5;
							k4 += k3 * i6;
							l4 += k3 * j6;
						}
					} else {
						int l9 = i3 + i2;
						boolean flag7 = false;
						boolean flag8 = false;
						boolean flag9 = false;
						boolean flag10 = false;
						if (Config.texelFilter) {
							if (!flag && l8 < 0x30000 && l8 > 0xfffd0000 && i9 <= 0x30000 && i9 >= 0xfffd0000)
								flag = true;
							if (!flag && j9 < 0x30000 && j9 > 0xfffd0000 && k9 <= 0x30000 && k9 >= 0xfffd0000)
								flag = true;
						} else {
							flag = false;
						}
						if (flag) {
							int l6;
							if ((k2 & 1) != 0)
								l6 = 0x10000 + i7;
							else
								l6 = j7;
							if (Config.optiZ && (k > i3 || l < k2 || f56 >= f21 && f12 >= f21)) {
								for (int i16 = k2 + i2; i16 <= l9; i16++) {
									int k17 = ai1[((j5 + l6 & i8) >> 18) + (((k5 + (l6 ^ 0x10000) & j8) >> 18) << byte1)];
									int i19 = (((l3 + l6 & k7) >> 18) - 128) + (k17 >> 8)
											+ ((((i4 + (l6 ^ 0x10000) & l7) >> 18) - 128) + (k17 & 0xff) << byte0);
									l6 ^= 0x30000;
									if (i19 < 0)
										i19 = 0;
									else if (i19 >= texture.intSize)
										i19 = texture.intSize - 1;
									int i10 = ai[i19];
									int k14 = (i10 >> 16) * (j4 >> 10) >> 16;
									int i13 = (i10 >> 8 & 0xff) * (k4 >> 10) >> 16;
									int k11 = (i10 & 0xff) * (l4 >> 10) >> 16;
									if ((k14 & 0xffffff00) != 0)
										k14 = 255 >> (k14 >> 28 & 8);
									if ((i13 & 0xffffff00) != 0)
										i13 = 255 >> (i13 >> 28 & 8);
									if ((k11 & 0xffffff00) != 0)
										k11 = 255 >> (k11 >> 28 & 8);
									ai2[i16] = k11 | i13 << 8 | k14 << 16 | 0xff000000;
									ai3[i16] = i5;
									l3 += l8;
									i4 += i9;
									j5 += j9;
									k5 += k9;
									j4 += l5;
									k4 += i6;
									l4 += j6;
									i5 += k8;
								}

							} else if (!flag1) {
								for (int j16 = k2 + i2; j16 <= l9; j16++) {
									if (ai3[j16] < i5) {
										int l17 = ai1[((j5 + l6 & i8) >> 18) + (((k5 + (l6 ^ 0x10000) & j8) >> 18) << byte1)];
										int j19 = (((l3 + l6 & k7) >> 18) - 128) + (l17 >> 8)
												+ ((((i4 + (l6 ^ 0x10000) & l7) >> 18) - 128) + (l17 & 0xff) << byte0);
										l6 ^= 0x30000;
										if (j19 < 0)
											j19 = 0;
										else if (j19 >= texture.intSize)
											j19 = texture.intSize - 1;
										int j10 = ai[j19];
										int l14 = (j10 >> 16) * (j4 >> 10) >> 16;
										int j13 = (j10 >> 8 & 0xff) * (k4 >> 10) >> 16;
										int l11 = (j10 & 0xff) * (l4 >> 10) >> 16;
										if ((l14 & 0xffffff00) != 0)
											l14 = 255 >> (l14 >> 28 & 8);
										if ((j13 & 0xffffff00) != 0)
											j13 = 255 >> (j13 >> 28 & 8);
										if ((l11 & 0xffffff00) != 0)
											l11 = 255 >> (l11 >> 28 & 8);
										ai2[j16] = l11 | j13 << 8 | l14 << 16 | 0xff000000;
										ai3[j16] = i5;
									}
									l3 += l8;
									i4 += i9;
									j5 += j9;
									k5 += k9;
									j4 += l5;
									k4 += i6;
									l4 += j6;
									i5 += k8;
								}

							} else {
								for (int k16 = k2 + i2; k16 <= l9; k16++) {
									if (ai3[k16] > i5) {
										int i18 = ai1[((j5 + l6 & i8) >> 18) + (((k5 + (l6 ^ 0x10000) & j8) >> 18) << byte1)];
										int k19 = (((l3 + l6 & k7) >> 18) - 128) + (i18 >> 8)
												+ ((((i4 + (l6 ^ 0x10000) & l7) >> 18) - 128) + (i18 & 0xff) << byte0);
										l6 ^= 0x30000;
										if (k19 < 0)
											k19 = 0;
										else if (k19 >= texture.intSize)
											k19 = texture.intSize - 1;
										int k10 = ai[k19];
										int i15 = (k10 >> 16) * (j4 >> 10) >> 16;
										int k13 = (k10 >> 8 & 0xff) * (k4 >> 10) >> 16;
										int i12 = (k10 & 0xff) * (l4 >> 10) >> 16;
										if ((i15 & 0xffffff00) != 0)
											i15 = 255 >> (i15 >> 28 & 8);
										if ((k13 & 0xffffff00) != 0)
											k13 = 255 >> (k13 >> 28 & 8);
										if ((i12 & 0xffffff00) != 0)
											i12 = 255 >> (i12 >> 28 & 8);
										ai2[k16] = i12 | k13 << 8 | i15 << 16 | 0xff000000;
										ai3[k16] = i5;
									}
									l3 += l8;
									i4 += i9;
									j5 += j9;
									k5 += k9;
									j4 += l5;
									k4 += i6;
									l4 += j6;
									i5 += k8;
								}

							}
						} else if (Config.optiZ && (k > i3 || l < k2 || f56 >= f21 && f12 >= f21)) {
							for (int l16 = k2 + i2; l16 <= l9; l16++) {
								int j18 = ai1[((j5 & i8) >> 18) + (((k5 & j8) >> 18) << byte1)];
								int l19 = (((l3 & k7) >> 18) - 128) + (j18 >> 8) + ((((i4 & l7) >> 18) - 128) + (j18 & 0xff) << byte0);
								if (l19 >= texture.intSize)
									l19 = texture.intSize - 1;
								else if (l19 < 0)
									l19 = 0;
								int l10 = ai[l19];
								int j15 = (l10 >> 16) * (j4 >> 10) >> 16;
								int l13 = (l10 >> 8 & 0xff) * (k4 >> 10) >> 16;
								int j12 = (l10 & 0xff) * (l4 >> 10) >> 16;
								if ((j15 & 0xffffff00) != 0)
									j15 = 255 >> (byte) (j15 >> 28 & 8);
								if ((l13 & 0xffffff00) != 0)
									l13 = 255 >> (byte) (l13 >> 28 & 8);
								if ((j12 & 0xffffff00) != 0)
									j12 = 255 >> (byte) (j12 >> 28 & 8);
								ai2[l16] = j12 | l13 << 8 | j15 << 16 | 0xff000000;
								ai3[l16] = i5;
								l3 += l8;
								i4 += i9;
								j5 += j9;
								k5 += k9;
								j4 += l5;
								k4 += i6;
								l4 += j6;
								i5 += k8;
							}

						} else if (!flag1) {
							for (int i17 = k2 + i2; i17 <= l9; i17++) {
								if (framebuffer.zbuffer[i17] < i5) {
									int k18 = ai1[((j5 & i8) >> 18) + (((k5 & j8) >> 18) << byte1)];
									int i20 = (((l3 & k7) >> 18) - 128) + (k18 >> 8)
											+ ((((i4 & l7) >> 18) - 128) + (k18 & 0xff) << byte0);
									if (i20 >= texture.intSize)
										i20 = texture.intSize - 1;
									else if (i20 < 0)
										i20 = 0;
									int i11 = ai[i20];
									int k15 = (i11 >> 16) * (j4 >> 10) >> 16;
									int i14 = (i11 >> 8 & 0xff) * (k4 >> 10) >> 16;
									int k12 = (i11 & 0xff) * (l4 >> 10) >> 16;
									if ((k15 & 0xffffff00) != 0)
										k15 = 255 >> (byte) (k15 >> 28 & 8);
									if ((i14 & 0xffffff00) != 0)
										i14 = 255 >> (byte) (i14 >> 28 & 8);
									if ((k12 & 0xffffff00) != 0)
										k12 = 255 >> (byte) (k12 >> 28 & 8);
									ai2[i17] = k12 | i14 << 8 | k15 << 16 | 0xff000000;
									ai3[i17] = i5;
								}
								l3 += l8;
								i4 += i9;
								j5 += j9;
								k5 += k9;
								j4 += l5;
								k4 += i6;
								l4 += j6;
								i5 += k8;
							}

						} else {
							for (int j17 = k2 + i2; j17 <= l9; j17++) {
								if (framebuffer.zbuffer[j17] > i5) {
									int l18 = ai1[((j5 & i8) >> 18) + (((k5 & j8) >> 18) << byte1)];
									int j20 = (((l3 & k7) >> 18) - 128) + (l18 >> 8)
											+ ((((i4 & l7) >> 18) - 128) + (l18 & 0xff) << byte0);
									if (j20 >= texture.intSize)
										j20 = texture.intSize - 1;
									else if (j20 < 0)
										j20 = 0;
									int j11 = ai[j20];
									int l15 = (j11 >> 16) * (j4 >> 10) >> 16;
									int j14 = (j11 >> 8 & 0xff) * (k4 >> 10) >> 16;
									int l12 = (j11 & 0xff) * (l4 >> 10) >> 16;
									if ((l15 & 0xffffff00) != 0)
										l15 = 255 >> (byte) (l15 >> 28 & 8);
									if ((j14 & 0xffffff00) != 0)
										j14 = 255 >> (byte) (j14 >> 28 & 8);
									if ((l12 & 0xffffff00) != 0)
										l12 = 255 >> (byte) (l12 >> 28 & 8);
									ai2[j17] = l12 | j14 << 8 | l15 << 16 | 0xff000000;
									ai3[j17] = i5;
								}
								l3 += l8;
								i4 += i9;
								j5 += j9;
								k5 += k9;
								j4 += l5;
								k4 += i6;
								l4 += j6;
								i5 += k8;
							}

						}
					}
					f49 = f57;
					f50 = f58;
					f51 = f59;
					f52 = f60;
					f53 = f61;
					f54 = f62;
					f55 = f63;
				} while (i3 < j3);
				boolean flag5 = false;
				if (l2 < i1 && j3 >= i1 || i1 == 0x5f5e0ff) {
					framebuffer.exXstart[i] = l2;
					if (f22 > f35)
						framebuffer.exZlow[i] = f35;
					if (framebuffer.exZlow[i] > f13)
						framebuffer.exZlow[i] = f13;
					flag5 = true;
				}
				if (j3 > j1 && f6 <= (float) j1 || j1 == -1) {
					framebuffer.exXend[i] = j3;
					if (framebuffer.exZlow[i] > f13)
						framebuffer.exZlow[i] = f13;
					if (framebuffer.exZlow[i] > f35)
						framebuffer.exZlow[i] = f35;
					flag5 = true;
				}
				if (!flag5) {
					if (l2 < k1 && j3 >= k1 || k1 == 0x5f5e0ff) {
						framebuffer.exXstart2[i] = l2;
						if (f23 > f35)
							framebuffer.exZlow2[i] = f35;
						if (framebuffer.exZlow2[i] > f13)
							framebuffer.exZlow2[i] = f13;
					}
					if (j3 > l1 && f6 <= (float) l1 || l1 == -1) {
						framebuffer.exXend2[i] = j3;
						if (framebuffer.exZlow2[i] > f13)
							framebuffer.exZlow2[i] = f13;
						if (framebuffer.exZlow2[i] > f35)
							framebuffer.exZlow2[i] = f35;
						boolean flag6 = true;
					}
				}
				if (f6 < (float) k)
					framebuffer.xstart[i] = l2;
				if (j3 > l)
					framebuffer.xend[i] = j3;
				if (f13 < f35)
					f13 = f35;
				if (f13 > f21)
					framebuffer.zhigh[i] = f13;
			}
		}
	}

	private final void drawShadedZbufferedFilteredTransparentScanline(float f, float f1, float f2, float f3, float f4,
			float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, int i, float f14,
			float f15, FrameBuffer framebuffer, Texture texture, boolean flag, int j, int k, boolean flag1) {
		if (k != 1) {
			float f16 = k;
			f *= f16;
			f1 *= f16;
			f2 *= f16;
			f3 *= f16;
			f4 *= f16;
			f5 *= f16;
		}
		int ai[] = texture.texels;
		int ai1[] = framebuffer.pixels;
		int ai2[] = framebuffer.zbuffer;
		boolean flag2 = false;
		int i1 = framebuffer.xstart[i];
		int j1 = framebuffer.xend[i];
		float f17 = framebuffer.zhigh[i];
		int k1 = framebuffer.exXstart[i];
		int l1 = framebuffer.exXend[i];
		float f18 = framebuffer.exZlow[i];
		int i2 = framebuffer.exXstart2[i];
		int j2 = framebuffer.exXend2[i];
		float f19 = framebuffer.exZlow2[i];
		if (f7 < f6) {
			float f20 = f6;
			f6 = f7;
			f7 = f20;
			f20 = f8;
			f8 = f9;
			f9 = f20;
			f20 = f10;
			f10 = f11;
			f11 = f20;
			f20 = f12;
			f12 = f13;
			f13 = f20;
			f20 = f;
			f = f1;
			f1 = f20;
			f20 = f2;
			f2 = f3;
			f3 = f20;
			f20 = f4;
			f4 = f5;
			f5 = f20;
		}
		f7--;
		if (f6 < f15 && f7 >= f14) {
			if (l1 <= j2) {
				if (l1 >= i2) {
					l1 = j2;
					framebuffer.exXend[i] = framebuffer.exXend2[i];
					if (i2 < k1) {
						k1 = i2;
						framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					}
					if (f19 < f18) {
						f18 = f19;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
					framebuffer.exXstart2[i] = 0x5f5e0ff;
					i2 = 0x5f5e0ff;
					framebuffer.exXend2[i] = -1;
					j2 = -1;
					framebuffer.exZlow2[i] = 2.147484E+009F;
					f19 = 2.147484E+009F;
				}
			} else if (j2 >= k1) {
				if (i2 < k1) {
					k1 = i2;
					framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					if (f19 < f18) {
						f18 = f19;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
				}
				framebuffer.exXstart2[i] = 0x5f5e0ff;
				i2 = 0x5f5e0ff;
				framebuffer.exXend2[i] = -1;
				j2 = -1;
				framebuffer.exZlow2[i] = 2.147484E+009F;
				f19 = 2.147484E+009F;
			}
			if ((!Config.spanBasedHsr || (float) k1 > f6 || (float) l1 < f7 || f18 < f12 || f18 < f13)
					&& ((float) i2 > f6 || (float) j2 < f7 || f19 < f12 || f19 < f13)) {
				if (f12 > 1.0F)
					f12 = 1.0F;
				if (f13 > 1.0F)
					f13 = 1.0F;
				float f21 = f12;
				float f22 = f7 - f6;
				if (f22 < 1.0F) {
					f1 = f;
					f3 = f2;
					f5 = f4;
				}
				float f23;
				float f24;
				float f25;
				float f26;
				float f27;
				float f28;
				if (f22 != 0.0F) {
					float f30 = 1.0F / f22;
					f23 = (f9 - f8) * f30;
					f24 = (f11 - f10) * f30;
					f25 = (f13 - f12) * f30;
					f27 = (f1 - f) * f30;
					f28 = (f3 - f2) * f30;
					f26 = (f5 - f4) * f30;
				} else {
					f23 = 0.0F;
					f24 = 0.0F;
					f25 = 0.0F;
					f26 = 0.0F;
					f27 = 0.0F;
					f28 = 0.0F;
				}
				if (f7 >= f15)
					f7 = f15 - 1.0F;
				if (f6 < f14) {
					f6 = f14 - f6;
					f += f6 * f27;
					f2 += f6 * f28;
					f4 += f6 * f26;
					f8 += f6 * f23;
					f10 += f6 * f24;
					f12 += f6 * f25;
					f6 = f14;
				}
				int k2 = i * framebuffer.width;
				int l2 = (int) f6;
				int j3 = l2 - 1;
				int k3 = (int) f7;
				int l3 = 16;
				float f31 = 16384F;
				float f32 = 16384F;
				float f29;
				if (Config.zTrick)
					f29 = 1.342177E+008F;
				else
					f29 = 2.684355E+008F;
				float f33 = 16F * f23;
				float f34 = 16F * f24;
				float f35 = 16F * f25;
				float f36 = 16F * f27;
				float f37 = 16F * f28;
				float f38 = 16F * f26;
				float f39 = 1.0F / f12;
				float f41 = f8 * f39;
				float f42 = f10 * f39;
				float f43 = f * f39;
				float f44 = f2 * f39;
				float f45 = f4 * f39;
				byte byte0 = texture.shifter;
				int k4 = (int) (f43 * 262144F);
				int l4 = (int) (f44 * 262144F);
				int i5 = (int) (f45 * 262144F);
				int j5 = 0;
				boolean flag3 = false;
				if (Config.zTrick) {
					j5 = (int) (f12 * 2.147484E+009F);
					if ((framebuffer.frameCount & 1L) == 1L) {
						j5 = -j5;
						flag3 = true;
					}
				} else {
					j5 = (int) (f12 * 4.294967E+009F + -2.147484E+009F);
				}
				int i4 = (int) (f41 * 262144F);
				int j4 = (int) (f42 * 262144F);
				boolean flag4 = false;
				boolean flag5 = false;
				boolean flag6 = false;
				int j6 = i & 1;
				int l6 = 1 - j6 << 17;
				int i7 = j6 << 17;
				do {
					int i3 = j3 + 1;
					j3 += l3;
					if (j3 > k3) {
						l3 -= j3 - k3;
						j3 = k3;
						if (l3 != 0) {
							f33 = (float) l3 * f23;
							f34 = (float) l3 * f24;
							f35 = (float) l3 * f25;
							f36 = (float) l3 * f27;
							f37 = (float) l3 * f28;
							f38 = (float) l3 * f26;
							f32 = multiLU[l3];
							f31 = f32;
							if (Config.zTrick)
								f29 = multiZTLU[l3];
							else
								f29 = multiZLU[l3];
						} else {
							f33 = 0.0F;
							f34 = 0.0F;
							f35 = 0.0F;
							f36 = 0.0F;
							f37 = 0.0F;
							f38 = 0.0F;
							l3 = 1;
							f32 = 262144F;
							f31 = 262144F;
							if (Config.zTrick)
								f29 = 2.147484E+009F;
							else
								f29 = 4.294967E+009F;
						}
					}
					float f46 = f12;
					if (j3 == k3 && f7 < f15 - 1.0F) {
						f33 = f9 - f8;
						f34 = f11 - f10;
						f35 = f13 - f12;
					}
					f8 += f33;
					f10 += f34;
					f12 += f35;
					f += f36;
					f2 += f37;
					f4 += f38;
					float f40 = 1.0F / f12;
					float f47 = f8 * f40;
					float f48 = f10 * f40;
					float f49 = f * f40;
					float f50 = f2 * f40;
					float f51 = f4 * f40;
					int j7 = texture.width - 1 << 18;
					int k7 = texture.height - 1 << 18;
					int l7 = (int) (f29 * (f12 - f46));
					if (flag3)
						l7 = -l7;
					int i8 = (int) (f31 * (f47 - f41));
					int j8 = (int) (f31 * (f48 - f42));
					int k5 = (int) (f32 * (f49 - f43));
					int l5 = (int) (f32 * (f50 - f44));
					int i6 = (int) (f32 * (f51 - f45));
					if (Config.spanBasedHsr
							&& (k1 <= i3 && l1 >= j3 && f18 >= f46 && f18 >= f12 || i2 <= i3 && j2 >= j3 && f19 >= f46 && f19 >= f12)) {
						if (j3 != k3) {
							j5 += l3 * l7;
							i4 += l3 * i8;
							j4 += l3 * j8;
							k4 += l3 * k5;
							l4 += l3 * l5;
							i5 += l3 * i6;
						}
					} else {
						int l = j3 + k2;
						boolean flag7 = false;
						boolean flag8 = false;
						boolean flag9 = false;
						boolean flag10 = false;
						if (Config.texelFilter) {
							if (!flag && i8 < 0x30000 && i8 > 0xfffd0000 && j8 <= 0x30000 && j8 >= 0xfffd0000)
								flag = true;
						} else {
							flag = false;
						}
						if (flag) {
							int k6;
							if ((i3 & 1) != 0)
								k6 = 0x10000 + l6;
							else
								k6 = i7;
							if (!flag3) {
								for (int k12 = i3 + k2; k12 <= l; k12++) {
									if (j5 > framebuffer.zbuffer[k12]) {
										int k8 = ai[((i4 + k6 & j7) >> 18) + (((j4 + (k6 ^ 0x10000) & k7) >> 18) << byte0)];
										k6 ^= 0x30000;
										if ((k8 & 0xf0f0f0) != 0) {
											int k13 = ai1[k12];
											int k11 = ((k8 >> 16) * (k4 >> 10) >> 16) + ((k13 >> 16 & 0xff) >> j) >> 1;
											int k10 = ((k8 >> 8 & 0xff) * (l4 >> 10) >> 16) + ((k13 >> 8 & 0xff) >> j) >> 1;
											int k9 = ((k8 & 0xff) * (i5 >> 10) >> 16) + ((k13 & 0xff) >> j) >> 1;
											if ((k11 & 0xffffff00) != 0)
												k11 = 255 >> (k11 >> 28 & 8);
											if ((k10 & 0xffffff00) != 0)
												k10 = 255 >> (k10 >> 28 & 8);
											if ((k9 & 0xffffff00) != 0)
												k9 = 255 >> (k9 >> 28 & 8);
											ai1[k12] = k9 | k10 << 8 | k11 << 16 | 0xff000000;
											if (flag1)
												ai2[k12] = j5;
										}
									}
									i4 += i8;
									j4 += j8;
									k4 += k5;
									l4 += l5;
									i5 += i6;
									j5 += l7;
								}

							} else {
								for (int l12 = i3 + k2; l12 <= l; l12++) {
									if (j5 < framebuffer.zbuffer[l12]) {
										int l8 = ai[((i4 + k6 & j7) >> 18) + (((j4 + (k6 ^ 0x10000) & k7) >> 18) << byte0)];
										k6 ^= 0x30000;
										if ((l8 & 0xf0f0f0) != 0) {
											int l13 = ai1[l12];
											int l11 = ((l8 >> 16) * (k4 >> 10) >> 16) + ((l13 >> 16 & 0xff) >> j) >> 1;
											int l10 = ((l8 >> 8 & 0xff) * (l4 >> 10) >> 16) + ((l13 >> 8 & 0xff) >> j) >> 1;
											int l9 = ((l8 & 0xff) * (i5 >> 10) >> 16) + ((l13 & 0xff) >> j) >> 1;
											if ((l11 & 0xffffff00) != 0)
												l11 = 255 >> (l11 >> 28 & 8);
											if ((l10 & 0xffffff00) != 0)
												l10 = 255 >> (l10 >> 28 & 8);
											if ((l9 & 0xffffff00) != 0)
												l9 = 255 >> (l9 >> 28 & 8);
											ai1[l12] = l9 | l10 << 8 | l11 << 16 | 0xff000000;
											if (flag1)
												ai2[l12] = j5;
										}
									}
									i4 += i8;
									j4 += j8;
									k4 += k5;
									l4 += l5;
									i5 += i6;
									j5 += l7;
								}

							}
						} else if (!flag3) {
							for (int i13 = i3 + k2; i13 <= l; i13++) {
								if (ai2[i13] < j5) {
									int i9 = ai[((i4 & j7) >> 18) + (((j4 & k7) >> 18) << byte0)];
									if ((i9 & 0xf0f0f0) != 0) {
										int i14 = ai1[i13];
										int i12 = ((i9 >> 16) * (k4 >> 10) >> 16) + ((i14 >> 16 & 0xff) >> j) >> 1;
										int i11 = ((i9 >> 8 & 0xff) * (l4 >> 10) >> 16) + ((i14 >> 8 & 0xff) >> j) >> 1;
										int i10 = ((i9 & 0xff) * (i5 >> 10) >> 16) + ((i14 & 0xff) >> j) >> 1;
										if ((i12 & 0xffffff00) != 0)
											i12 = 255 >> (byte) (i12 >> 28 & 8);
										if ((i11 & 0xffffff00) != 0)
											i11 = 255 >> (byte) (i11 >> 28 & 8);
										if ((i10 & 0xffffff00) != 0)
											i10 = 255 >> (byte) (i10 >> 28 & 8);
										ai1[i13] = i10 | i11 << 8 | i12 << 16 | 0xff000000;
										if (flag1)
											ai2[i13] = j5;
									}
								}
								i4 += i8;
								j4 += j8;
								k4 += k5;
								l4 += l5;
								i5 += i6;
								j5 += l7;
							}

						} else {
							for (int j13 = i3 + k2; j13 <= l; j13++) {
								if (ai2[j13] > j5) {
									int j9 = ai[((i4 & j7) >> 18) + (((j4 & k7) >> 18) << byte0)];
									if ((j9 & 0xf0f0f0) != 0) {
										int j14 = ai1[j13];
										int j12 = ((j9 >> 16) * (k4 >> 10) >> 16) + ((j14 >> 16 & 0xff) >> j) >> 1;
										int j11 = ((j9 >> 8 & 0xff) * (l4 >> 10) >> 16) + ((j14 >> 8 & 0xff) >> j) >> 1;
										int j10 = ((j9 & 0xff) * (i5 >> 10) >> 16) + ((j14 & 0xff) >> j) >> 1;
										if ((j12 & 0xffffff00) != 0)
											j12 = 255 >> (byte) (j12 >> 28 & 8);
										if ((j11 & 0xffffff00) != 0)
											j11 = 255 >> (byte) (j11 >> 28 & 8);
										if ((j10 & 0xffffff00) != 0)
											j10 = 255 >> (byte) (j10 >> 28 & 8);
										ai1[j13] = j10 | j11 << 8 | j12 << 16 | 0xff000000;
										if (flag1)
											ai2[j13] = j5;
									}
								}
								i4 += i8;
								j4 += j8;
								k4 += k5;
								l4 += l5;
								i5 += i6;
								j5 += l7;
							}

						}
					}
					f41 = f47;
					f42 = f48;
					f43 = f49;
					f44 = f50;
					f45 = f51;
				} while (j3 < k3);
				if (f6 < (float) i1)
					framebuffer.xstart[i] = (int) f6;
				if (k3 > j1)
					framebuffer.xend[i] = k3;
				if (f13 < f21)
					f13 = f21;
				if (f13 > f17)
					framebuffer.zhigh[i] = f13;
			}
		}
	}

	private final void drawShadedZbufferedFilteredAdditiveTransparentScanline(float f, float f1, float f2, float f3,
			float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, int i,
			float f14, float f15, FrameBuffer framebuffer, Texture texture, boolean flag, int j, int k, boolean flag1) {
		if (k != 1) {
			float f16 = k;
			f *= f16;
			f1 *= f16;
			f2 *= f16;
			f3 *= f16;
			f4 *= f16;
			f5 *= f16;
		}
		int ai[] = texture.texels;
		int ai1[] = framebuffer.pixels;
		int ai2[] = framebuffer.zbuffer;
		boolean flag2 = false;
		int i1 = framebuffer.xstart[i];
		int j1 = framebuffer.xend[i];
		float f17 = framebuffer.zhigh[i];
		int k1 = framebuffer.exXstart[i];
		int l1 = framebuffer.exXend[i];
		float f18 = framebuffer.exZlow[i];
		int i2 = framebuffer.exXstart2[i];
		int j2 = framebuffer.exXend2[i];
		float f19 = framebuffer.exZlow2[i];
		if (f7 < f6) {
			float f20 = f6;
			f6 = f7;
			f7 = f20;
			f20 = f8;
			f8 = f9;
			f9 = f20;
			f20 = f10;
			f10 = f11;
			f11 = f20;
			f20 = f12;
			f12 = f13;
			f13 = f20;
			f20 = f;
			f = f1;
			f1 = f20;
			f20 = f2;
			f2 = f3;
			f3 = f20;
			f20 = f4;
			f4 = f5;
			f5 = f20;
		}
		f7--;
		if (f6 < f15 && f7 >= f14) {
			if (l1 <= j2) {
				if (l1 >= i2) {
					l1 = j2;
					framebuffer.exXend[i] = framebuffer.exXend2[i];
					if (i2 < k1) {
						k1 = i2;
						framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					}
					if (f19 < f18) {
						f18 = f19;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
					framebuffer.exXstart2[i] = 0x5f5e0ff;
					i2 = 0x5f5e0ff;
					framebuffer.exXend2[i] = -1;
					j2 = -1;
					framebuffer.exZlow2[i] = 2.147484E+009F;
					f19 = 2.147484E+009F;
				}
			} else if (j2 >= k1) {
				if (i2 < k1) {
					k1 = i2;
					framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					if (f19 < f18) {
						f18 = f19;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
				}
				framebuffer.exXstart2[i] = 0x5f5e0ff;
				i2 = 0x5f5e0ff;
				framebuffer.exXend2[i] = -1;
				j2 = -1;
				framebuffer.exZlow2[i] = 2.147484E+009F;
				f19 = 2.147484E+009F;
			}
			if ((!Config.spanBasedHsr || (float) k1 > f6 || (float) l1 < f7 || f18 < f12 || f18 < f13)
					&& ((float) i2 > f6 || (float) j2 < f7 || f19 < f12 || f19 < f13)) {
				if (f12 > 1.0F)
					f12 = 1.0F;
				if (f13 > 1.0F)
					f13 = 1.0F;
				float f21 = f12;
				float f22 = f7 - f6;
				if (f22 < 1.0F) {
					f1 = f;
					f3 = f2;
					f5 = f4;
				}
				float f23;
				float f24;
				float f25;
				float f26;
				float f27;
				float f28;
				if (f22 != 0.0F) {
					float f30 = 1.0F / f22;
					f23 = (f9 - f8) * f30;
					f24 = (f11 - f10) * f30;
					f25 = (f13 - f12) * f30;
					f27 = (f1 - f) * f30;
					f28 = (f3 - f2) * f30;
					f26 = (f5 - f4) * f30;
				} else {
					f23 = 0.0F;
					f24 = 0.0F;
					f25 = 0.0F;
					f26 = 0.0F;
					f27 = 0.0F;
					f28 = 0.0F;
				}
				if (f7 >= f15)
					f7 = f15 - 1.0F;
				if (f6 < f14) {
					f6 = f14 - f6;
					f += f6 * f27;
					f2 += f6 * f28;
					f4 += f6 * f26;
					f8 += f6 * f23;
					f10 += f6 * f24;
					f12 += f6 * f25;
					f6 = f14;
				}
				int k2 = i * framebuffer.width;
				int l2 = (int) f6;
				int j3 = l2 - 1;
				int k3 = (int) f7;
				int l3 = 16;
				float f31 = 16384F;
				float f32 = 16384F;
				float f29;
				if (Config.zTrick)
					f29 = 1.342177E+008F;
				else
					f29 = 2.684355E+008F;
				float f33 = 16F * f23;
				float f34 = 16F * f24;
				float f35 = 16F * f25;
				float f36 = 16F * f27;
				float f37 = 16F * f28;
				float f38 = 16F * f26;
				float f39 = 1.0F / f12;
				float f41 = f8 * f39;
				float f42 = f10 * f39;
				float f43 = f * f39;
				float f44 = f2 * f39;
				float f45 = f4 * f39;
				byte byte0 = texture.shifter;
				int k4 = (int) (f43 * 262144F);
				int l4 = (int) (f44 * 262144F);
				int i5 = (int) (f45 * 262144F);
				int j5 = 0;
				boolean flag3 = false;
				if (Config.zTrick) {
					j5 = (int) (f12 * 2.147484E+009F);
					if ((framebuffer.frameCount & 1L) == 1L) {
						j5 = -j5;
						flag3 = true;
					}
				} else {
					j5 = (int) (f12 * 4.294967E+009F + -2.147484E+009F);
				}
				int i4 = (int) (f41 * 262144F);
				int j4 = (int) (f42 * 262144F);
				boolean flag4 = false;
				boolean flag5 = false;
				boolean flag6 = false;
				int j6 = i & 1;
				int l6 = 1 - j6 << 17;
				int i7 = j6 << 17;
				do {
					int i3 = j3 + 1;
					j3 += l3;
					if (j3 > k3) {
						l3 -= j3 - k3;
						j3 = k3;
						if (l3 != 0) {
							f33 = (float) l3 * f23;
							f34 = (float) l3 * f24;
							f35 = (float) l3 * f25;
							f36 = (float) l3 * f27;
							f37 = (float) l3 * f28;
							f38 = (float) l3 * f26;
							f32 = multiLU[l3];
							f31 = f32;
							if (Config.zTrick)
								f29 = multiZTLU[l3];
							else
								f29 = multiZLU[l3];
						} else {
							f33 = 0.0F;
							f34 = 0.0F;
							f35 = 0.0F;
							f36 = 0.0F;
							f37 = 0.0F;
							f38 = 0.0F;
							l3 = 1;
							f32 = 262144F;
							f31 = 262144F;
							if (Config.zTrick)
								f29 = 2.147484E+009F;
							else
								f29 = 4.294967E+009F;
						}
					}
					float f46 = f12;
					if (j3 == k3 && f7 < f15 - 1.0F) {
						f33 = f9 - f8;
						f34 = f11 - f10;
						f35 = f13 - f12;
					}
					f8 += f33;
					f10 += f34;
					f12 += f35;
					f += f36;
					f2 += f37;
					f4 += f38;
					float f40 = 1.0F / f12;
					float f47 = f8 * f40;
					float f48 = f10 * f40;
					float f49 = f * f40;
					float f50 = f2 * f40;
					float f51 = f4 * f40;
					int j7 = texture.width - 1 << 18;
					int k7 = texture.height - 1 << 18;
					int l7 = (int) (f29 * (f12 - f46));
					if (flag3)
						l7 = -l7;
					int i8 = (int) (f31 * (f47 - f41));
					int j8 = (int) (f31 * (f48 - f42));
					int k5 = (int) (f32 * (f49 - f43));
					int l5 = (int) (f32 * (f50 - f44));
					int i6 = (int) (f32 * (f51 - f45));
					if (Config.spanBasedHsr
							&& (k1 <= i3 && l1 >= j3 && f18 >= f46 && f18 >= f12 || i2 <= i3 && j2 >= j3 && f19 >= f46 && f19 >= f12)) {
						if (j3 != k3) {
							j5 += l3 * l7;
							i4 += l3 * i8;
							j4 += l3 * j8;
							k4 += l3 * k5;
							l4 += l3 * l5;
							i5 += l3 * i6;
						}
					} else {
						int l = j3 + k2;
						boolean flag7 = false;
						boolean flag8 = false;
						boolean flag9 = false;
						boolean flag10 = false;
						if (Config.texelFilter) {
							if (!flag && i8 < 0x30000 && i8 > 0xfffd0000 && j8 <= 0x30000 && j8 >= 0xfffd0000)
								flag = true;
						} else {
							flag = false;
						}
						if (flag) {
							int k6;
							if ((i3 & 1) != 0)
								k6 = 0x10000 + l6;
							else
								k6 = i7;
							if (!flag3) {
								for (int k12 = i3 + k2; k12 <= l; k12++) {
									if (j5 > framebuffer.zbuffer[k12]) {
										int k8 = ai[((i4 + k6 & j7) >> 18) + (((j4 + (k6 ^ 0x10000) & k7) >> 18) << byte0)];
										k6 ^= 0x30000;
										if ((k8 & 0xf0f0f0) != 0) {
											int k13 = ai1[k12];
											int k11 = (k8 >> 16) << j;
											int k10 = (k8 >> 8 & 0xff) << j;
											int k9 = (k8 & 0xff) << j;
											if (k11 > 255)
												k11 = 255;
											if (k10 > 255)
												k10 = 255;
											if (k9 > 255)
												k9 = 255;
											k11 = (k11 * (k4 >> 10) >> 16) + (k13 >> 16 & 0xff);
											k10 = (k10 * (l4 >> 10) >> 16) + (k13 >> 8 & 0xff);
											k9 = (k9 * (i5 >> 10) >> 16) + (k13 & 0xff);
											if ((k11 & 0xffffff00) != 0)
												k11 = 255 >> (k11 >> 28 & 8);
											if ((k10 & 0xffffff00) != 0)
												k10 = 255 >> (k10 >> 28 & 8);
											if ((k9 & 0xffffff00) != 0)
												k9 = 255 >> (k9 >> 28 & 8);
											ai1[k12] = k9 | k10 << 8 | k11 << 16 | 0xff000000;
											if (flag1)
												ai2[k12] = j5;
										}
									}
									i4 += i8;
									j4 += j8;
									k4 += k5;
									l4 += l5;
									i5 += i6;
									j5 += l7;
								}

							} else {
								for (int l12 = i3 + k2; l12 <= l; l12++) {
									if (j5 < framebuffer.zbuffer[l12]) {
										int l8 = ai[((i4 + k6 & j7) >> 18) + (((j4 + (k6 ^ 0x10000) & k7) >> 18) << byte0)];
										k6 ^= 0x30000;
										if ((l8 & 0xf0f0f0) != 0) {
											int l13 = ai1[l12];
											int l11 = (l8 >> 16) << j;
											int l10 = (l8 >> 8 & 0xff) << j;
											int l9 = (l8 & 0xff) << j;
											if (l11 > 255)
												l11 = 255;
											if (l10 > 255)
												l10 = 255;
											if (l9 > 255)
												l9 = 255;
											l11 = (l11 * (k4 >> 10) >> 16) + (l13 >> 16 & 0xff);
											l10 = (l10 * (l4 >> 10) >> 16) + (l13 >> 8 & 0xff);
											l9 = (l9 * (i5 >> 10) >> 16) + (l13 & 0xff);
											if ((l11 & 0xffffff00) != 0)
												l11 = 255 >> (l11 >> 28 & 8);
											if ((l10 & 0xffffff00) != 0)
												l10 = 255 >> (l10 >> 28 & 8);
											if ((l9 & 0xffffff00) != 0)
												l9 = 255 >> (l9 >> 28 & 8);
											ai1[l12] = l9 | l10 << 8 | l11 << 16 | 0xff000000;
											if (flag1)
												ai2[l12] = j5;
										}
									}
									i4 += i8;
									j4 += j8;
									k4 += k5;
									l4 += l5;
									i5 += i6;
									j5 += l7;
								}

							}
						} else if (!flag3) {
							for (int i13 = i3 + k2; i13 <= l; i13++) {
								if (ai2[i13] < j5) {
									int i9 = ai[((i4 & j7) >> 18) + (((j4 & k7) >> 18) << byte0)];
									if ((i9 & 0xf0f0f0) != 0) {
										int i14 = ai1[i13];
										int i12 = (i9 >> 16) << j;
										int i11 = (i9 >> 8 & 0xff) << j;
										int i10 = (i9 & 0xff) << j;
										if (i12 > 255)
											i12 = 255;
										if (i11 > 255)
											i11 = 255;
										if (i10 > 255)
											i10 = 255;
										i12 = (i12 * (k4 >> 10) >> 16) + (i14 >> 16 & 0xff);
										i11 = (i11 * (l4 >> 10) >> 16) + (i14 >> 8 & 0xff);
										i10 = (i10 * (i5 >> 10) >> 16) + (i14 & 0xff);
										if ((i12 & 0xffffff00) != 0)
											i12 = 255 >> (byte) (i12 >> 28 & 8);
										if ((i11 & 0xffffff00) != 0)
											i11 = 255 >> (byte) (i11 >> 28 & 8);
										if ((i10 & 0xffffff00) != 0)
											i10 = 255 >> (byte) (i10 >> 28 & 8);
										ai1[i13] = i10 | i11 << 8 | i12 << 16 | 0xff000000;
										if (flag1)
											ai2[i13] = j5;
									}
								}
								i4 += i8;
								j4 += j8;
								k4 += k5;
								l4 += l5;
								i5 += i6;
								j5 += l7;
							}

						} else {
							for (int j13 = i3 + k2; j13 <= l; j13++) {
								if (ai2[j13] > j5) {
									int j9 = ai[((i4 & j7) >> 18) + (((j4 & k7) >> 18) << byte0)];
									if ((j9 & 0xf0f0f0) != 0) {
										int j14 = ai1[j13];
										int j12 = (j9 >> 16) << j;
										int j11 = (j9 >> 8 & 0xff) << j;
										int j10 = (j9 & 0xff) << j;
										if (j12 > 255)
											j12 = 255;
										if (j11 > 255)
											j11 = 255;
										if (j10 > 255)
											j10 = 255;
										j12 = (j12 * (k4 >> 10) >> 16) + (j14 >> 16 & 0xff);
										j11 = (j11 * (l4 >> 10) >> 16) + (j14 >> 8 & 0xff);
										j10 = (j10 * (i5 >> 10) >> 16) + (j14 & 0xff);
										if ((j12 & 0xffffff00) != 0)
											j12 = 255 >> (byte) (j12 >> 28 & 8);
										if ((j11 & 0xffffff00) != 0)
											j11 = 255 >> (byte) (j11 >> 28 & 8);
										if ((j10 & 0xffffff00) != 0)
											j10 = 255 >> (byte) (j10 >> 28 & 8);
										ai1[j13] = j10 | j11 << 8 | j12 << 16 | 0xff000000;
										if (flag1)
											ai2[j13] = j5;
									}
								}
								i4 += i8;
								j4 += j8;
								k4 += k5;
								l4 += l5;
								i5 += i6;
								j5 += l7;
							}

						}
					}
					f41 = f47;
					f42 = f48;
					f43 = f49;
					f44 = f50;
					f45 = f51;
				} while (j3 < k3);
				if (f6 < (float) i1)
					framebuffer.xstart[i] = (int) f6;
				if (k3 > j1)
					framebuffer.xend[i] = k3;
				if (f13 < f21)
					f13 = f21;
				if (f13 > f17)
					framebuffer.zhigh[i] = f13;
			}
		}
	}

	private final void drawShadedZbufferedFilteredAlphaScanline(float f, float f1, float f2, float f3, float f4,
			float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, int i, float f14,
			float f15, FrameBuffer framebuffer, Texture texture, boolean flag, int j, int k, boolean flag1) {
		if (k != 1) {
			float f16 = k;
			f *= f16;
			f1 *= f16;
			f2 *= f16;
			f3 *= f16;
			f4 *= f16;
			f5 *= f16;
		}
		int ai[] = texture.texels;
		int ai1[] = texture.alpha;
		int ai2[] = framebuffer.pixels;
		int ai3[] = framebuffer.zbuffer;
		boolean flag2 = false;
		int i1 = framebuffer.xstart[i];
		int j1 = framebuffer.xend[i];
		float f17 = framebuffer.zhigh[i];
		int k1 = framebuffer.exXstart[i];
		int l1 = framebuffer.exXend[i];
		float f18 = framebuffer.exZlow[i];
		int i2 = framebuffer.exXstart2[i];
		int j2 = framebuffer.exXend2[i];
		float f19 = framebuffer.exZlow2[i];
		if (f7 < f6) {
			float f20 = f6;
			f6 = f7;
			f7 = f20;
			f20 = f8;
			f8 = f9;
			f9 = f20;
			f20 = f10;
			f10 = f11;
			f11 = f20;
			f20 = f12;
			f12 = f13;
			f13 = f20;
			f20 = f;
			f = f1;
			f1 = f20;
			f20 = f2;
			f2 = f3;
			f3 = f20;
			f20 = f4;
			f4 = f5;
			f5 = f20;
		}
		f7--;
		if (f6 < f15 && f7 >= f14) {
			if (l1 <= j2) {
				if (l1 >= i2) {
					l1 = j2;
					framebuffer.exXend[i] = framebuffer.exXend2[i];
					if (i2 < k1) {
						k1 = i2;
						framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					}
					if (f19 < f18) {
						f18 = f19;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
					framebuffer.exXstart2[i] = 0x5f5e0ff;
					i2 = 0x5f5e0ff;
					framebuffer.exXend2[i] = -1;
					j2 = -1;
					framebuffer.exZlow2[i] = 2.147484E+009F;
					f19 = 2.147484E+009F;
				}
			} else if (j2 >= k1) {
				if (i2 < k1) {
					k1 = i2;
					framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					if (f19 < f18) {
						f18 = f19;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
				}
				framebuffer.exXstart2[i] = 0x5f5e0ff;
				i2 = 0x5f5e0ff;
				framebuffer.exXend2[i] = -1;
				j2 = -1;
				framebuffer.exZlow2[i] = 2.147484E+009F;
				f19 = 2.147484E+009F;
			}
			if ((!Config.spanBasedHsr || (float) k1 > f6 || (float) l1 < f7 || f18 < f12 || f18 < f13)
					&& ((float) i2 > f6 || (float) j2 < f7 || f19 < f12 || f19 < f13)) {
				if (f12 > 1.0F)
					f12 = 1.0F;
				if (f13 > 1.0F)
					f13 = 1.0F;
				float f21 = f12;
				float f22 = f7 - f6;
				if (f22 < 1.0F) {
					f1 = f;
					f3 = f2;
					f5 = f4;
				}
				float f23;
				float f24;
				float f25;
				float f26;
				float f27;
				float f28;
				if (f22 != 0.0F) {
					float f30 = 1.0F / f22;
					f23 = (f9 - f8) * f30;
					f24 = (f11 - f10) * f30;
					f25 = (f13 - f12) * f30;
					f27 = (f1 - f) * f30;
					f28 = (f3 - f2) * f30;
					f26 = (f5 - f4) * f30;
				} else {
					f23 = 0.0F;
					f24 = 0.0F;
					f25 = 0.0F;
					f26 = 0.0F;
					f27 = 0.0F;
					f28 = 0.0F;
				}
				if (f7 >= f15)
					f7 = f15 - 1.0F;
				if (f6 < f14) {
					f6 = f14 - f6;
					f += f6 * f27;
					f2 += f6 * f28;
					f4 += f6 * f26;
					f8 += f6 * f23;
					f10 += f6 * f24;
					f12 += f6 * f25;
					f6 = f14;
				}
				int k2 = i * framebuffer.width;
				int l2 = (int) f6;
				int j3 = l2 - 1;
				int k3 = (int) f7;
				int l3 = 16;
				float f31 = 16384F;
				float f32 = 16384F;
				float f29;
				if (Config.zTrick)
					f29 = 1.342177E+008F;
				else
					f29 = 2.684355E+008F;
				float f33 = 16F * f23;
				float f34 = 16F * f24;
				float f35 = 16F * f25;
				float f36 = 16F * f27;
				float f37 = 16F * f28;
				float f38 = 16F * f26;
				float f39 = 1.0F / f12;
				float f41 = f8 * f39;
				float f42 = f10 * f39;
				float f43 = f * f39;
				float f44 = f2 * f39;
				float f45 = f4 * f39;
				byte byte0 = texture.shifter;
				int k4 = (int) (f43 * 262144F);
				int l4 = (int) (f44 * 262144F);
				int i5 = (int) (f45 * 262144F);
				int j5 = 0;
				boolean flag3 = false;
				if (Config.zTrick) {
					j5 = (int) (f12 * 2.147484E+009F);
					if ((framebuffer.frameCount & 1L) == 1L) {
						j5 = -j5;
						flag3 = true;
					}
				} else {
					j5 = (int) (f12 * 4.294967E+009F + -2.147484E+009F);
				}
				int i4 = (int) (f41 * 262144F);
				int j4 = (int) (f42 * 262144F);
				boolean flag4 = false;
				boolean flag5 = false;
				boolean flag6 = false;
				int j6 = i & 1;
				int l6 = 1 - j6 << 17;
				int i7 = j6 << 17;
				do {
					int i3 = j3 + 1;
					j3 += l3;
					if (j3 > k3) {
						l3 -= j3 - k3;
						j3 = k3;
						if (l3 != 0) {
							f33 = (float) l3 * f23;
							f34 = (float) l3 * f24;
							f35 = (float) l3 * f25;
							f36 = (float) l3 * f27;
							f37 = (float) l3 * f28;
							f38 = (float) l3 * f26;
							f32 = multiLU[l3];
							f31 = f32;
							if (Config.zTrick)
								f29 = multiZTLU[l3];
							else
								f29 = multiZLU[l3];
						} else {
							f33 = 0.0F;
							f34 = 0.0F;
							f35 = 0.0F;
							f36 = 0.0F;
							f37 = 0.0F;
							f38 = 0.0F;
							l3 = 1;
							f32 = 262144F;
							f31 = 262144F;
							if (Config.zTrick)
								f29 = 2.147484E+009F;
							else
								f29 = 4.294967E+009F;
						}
					}
					float f46 = f12;
					if (j3 == k3 && f7 < f15 - 1.0F) {
						f33 = f9 - f8;
						f34 = f11 - f10;
						f35 = f13 - f12;
					}
					f8 += f33;
					f10 += f34;
					f12 += f35;
					f += f36;
					f2 += f37;
					f4 += f38;
					float f40 = 1.0F / f12;
					float f47 = f8 * f40;
					float f48 = f10 * f40;
					float f49 = f * f40;
					float f50 = f2 * f40;
					float f51 = f4 * f40;
					int j7 = texture.width - 1 << 18;
					int k7 = texture.height - 1 << 18;
					int l7 = (int) (f29 * (f12 - f46));
					if (flag3)
						l7 = -l7;
					int i8 = (int) (f31 * (f47 - f41));
					int j8 = (int) (f31 * (f48 - f42));
					int k5 = (int) (f32 * (f49 - f43));
					int l5 = (int) (f32 * (f50 - f44));
					int i6 = (int) (f32 * (f51 - f45));
					if (Config.spanBasedHsr
							&& (k1 <= i3 && l1 >= j3 && f18 >= f46 && f18 >= f12 || i2 <= i3 && j2 >= j3 && f19 >= f46 && f19 >= f12)) {
						if (j3 != k3) {
							j5 += l3 * l7;
							i4 += l3 * i8;
							j4 += l3 * j8;
							k4 += l3 * k5;
							l4 += l3 * l5;
							i5 += l3 * i6;
						}
					} else {
						int l = j3 + k2;
						boolean flag7 = false;
						boolean flag8 = false;
						boolean flag9 = false;
						boolean flag10 = false;
						boolean flag11 = false;
						if (Config.texelFilter) {
							if (!flag && i8 < 0x30000 && i8 > 0xfffd0000 && j8 <= 0x30000 && j8 >= 0xfffd0000)
								flag = true;
						} else {
							flag = false;
						}
						if (flag) {
							int k6;
							if ((i3 & 1) != 0)
								k6 = 0x10000 + l6;
							else
								k6 = i7;
							if (!flag3) {
								for (int k13 = i3 + k2; k13 <= l; k13++) {
									if (j5 > framebuffer.zbuffer[k13]) {
										int k14 = ((i4 + k6 & j7) >> 18) + (((j4 + (k6 ^ 0x10000) & k7) >> 18) << byte0);
										int k8 = ai[k14];
										int k12 = ai1[k14] >>> 24;
										k6 ^= 0x30000;
										if (k12 != 0) {
											int k15 = ai2[k13];
											int k11 = k8 >> 16;
											int k10 = k8 >> 8 & 0xff;
											int k9 = k8 & 0xff;
											if (k11 > 255)
												k11 = 255;
											if (k10 > 255)
												k10 = 255;
											if (k9 > 255)
												k9 = 255;
											k12 <<= j;
											if (k12 > 255)
												k12 = 255;
											int k16 = 255 - k12;
											k11 = k12 * (k11 * (k4 >> 10) >> 16) + k16 * (k15 >> 16 & 0xff) >> 8;
											k10 = k12 * (k10 * (l4 >> 10) >> 16) + k16 * (k15 >> 8 & 0xff) >> 8;
											k9 = k12 * (k9 * (i5 >> 10) >> 16) + k16 * (k15 & 0xff) >> 8;
											k12 = k12 * k12 + k16 * (k15 >>> 24 & 0xff) >> 8;
											if ((k11 & 0xffffff00) != 0)
												k11 = 255 >> (k11 >> 28 & 8);
											if ((k10 & 0xffffff00) != 0)
												k10 = 255 >> (k10 >> 28 & 8);
											if ((k9 & 0xffffff00) != 0)
												k9 = 255 >> (k9 >> 28 & 8);
											if ((k12 & 0xffffff00) != 0)
												k12 = 255 >> (k12 >> 28 & 8);
											ai2[k13] = k9 | k10 << 8 | k11 << 16 | k12 << 24;
											if (flag1)
												ai3[k13] = j5;
										}
									}
									i4 += i8;
									j4 += j8;
									k4 += k5;
									l4 += l5;
									i5 += i6;
									j5 += l7;
								}

							} else {
								for (int l13 = i3 + k2; l13 <= l; l13++) {
									if (j5 < framebuffer.zbuffer[l13]) {
										int l14 = ((i4 + k6 & j7) >> 18) + (((j4 + (k6 ^ 0x10000) & k7) >> 18) << byte0);
										int l8 = ai[l14];
										int l12 = ai1[l14] >>> 24;
										k6 ^= 0x30000;
										if (l12 != 0) {
											int l15 = ai2[l13];
											int l11 = l8 >> 16;
											int l10 = l8 >> 8 & 0xff;
											int l9 = l8 & 0xff;
											if (l11 > 255)
												l11 = 255;
											if (l10 > 255)
												l10 = 255;
											if (l9 > 255)
												l9 = 255;
											l12 <<= j;
											if (l12 > 255)
												l12 = 255;
											int l16 = 255 - l12;
											l11 = l12 * (l11 * (k4 >> 10) >> 16) + l16 * (l15 >> 16 & 0xff) >> 8;
											l10 = l12 * (l10 * (l4 >> 10) >> 16) + l16 * (l15 >> 8 & 0xff) >> 8;
											l9 = l12 * (l9 * (i5 >> 10) >> 16) + l16 * (l15 & 0xff) >> 8;
											l12 = l12 * l12 + l16 * (l15 >>> 24 & 0xff) >> 8;
											if ((l11 & 0xffffff00) != 0)
												l11 = 255 >> (l11 >> 28 & 8);
											if ((l10 & 0xffffff00) != 0)
												l10 = 255 >> (l10 >> 28 & 8);
											if ((l9 & 0xffffff00) != 0)
												l9 = 255 >> (l9 >> 28 & 8);
											if ((l12 & 0xffffff00) != 0)
												l12 = 255 >> (l12 >> 28 & 8);
											ai2[l13] = l9 | l10 << 8 | l11 << 16 | l12 << 24;
											if (flag1)
												ai3[l13] = j5;
										}
									}
									i4 += i8;
									j4 += j8;
									k4 += k5;
									l4 += l5;
									i5 += i6;
									j5 += l7;
								}

							}
						} else if (!flag3) {
							for (int i14 = i3 + k2; i14 <= l; i14++) {
								if (ai3[i14] < j5) {
									int i15 = ((i4 & j7) >> 18) + (((j4 & k7) >> 18) << byte0);
									int i9 = ai[i15];
									int i13 = ai1[i15] >>> 24;
									if (i13 != 0) {
										int i16 = ai2[i14];
										int i12 = i9 >> 16;
										int i11 = i9 >> 8 & 0xff;
										int i10 = i9 & 0xff;
										if (i12 > 255)
											i12 = 255;
										if (i11 > 255)
											i11 = 255;
										if (i10 > 255)
											i10 = 255;
										i13 <<= j;
										if (i13 > 255)
											i13 = 255;
										int i17 = 255 - i13;
										i12 = i13 * (i12 * (k4 >> 10) >> 16) + i17 * (i16 >> 16 & 0xff) >> 8;
										i11 = i13 * (i11 * (l4 >> 10) >> 16) + i17 * (i16 >> 8 & 0xff) >> 8;
										i10 = i13 * (i10 * (i5 >> 10) >> 16) + i17 * (i16 & 0xff) >> 8;
										i13 = i13 * i13 + i17 * (i16 >>> 24 & 0xff) >> 8;
										if ((i12 & 0xffffff00) != 0)
											i12 = 255 >> (i12 >> 28 & 8);
										if ((i11 & 0xffffff00) != 0)
											i11 = 255 >> (i11 >> 28 & 8);
										if ((i10 & 0xffffff00) != 0)
											i10 = 255 >> (i10 >> 28 & 8);
										if ((i13 & 0xffffff00) != 0)
											i13 = 255 >> (i13 >> 28 & 8);
										ai2[i14] = i10 | i11 << 8 | i12 << 16 | i13 << 24;
										if (flag1)
											ai3[i14] = j5;
									}
								}
								i4 += i8;
								j4 += j8;
								k4 += k5;
								l4 += l5;
								i5 += i6;
								j5 += l7;
							}

						} else {
							for (int j14 = i3 + k2; j14 <= l; j14++) {
								if (ai3[j14] > j5) {
									int j15 = ((i4 & j7) >> 18) + (((j4 & k7) >> 18) << byte0);
									int j9 = ai[j15];
									int j13 = ai1[j15] >>> 24;
									if (j13 != 0) {
										int j16 = ai2[j14];
										int j12 = j9 >> 16;
										int j11 = j9 >> 8 & 0xff;
										int j10 = j9 & 0xff;
										if (j12 > 255)
											j12 = 255;
										if (j11 > 255)
											j11 = 255;
										if (j10 > 255)
											j10 = 255;
										j13 <<= j;
										if (j13 > 255)
											j13 = 255;
										int j17 = 255 - j13;
										j12 = j13 * (j12 * (k4 >> 10) >> 16) + j17 * (j16 >> 16 & 0xff) >> 8;
										j11 = j13 * (j11 * (l4 >> 10) >> 16) + j17 * (j16 >> 8 & 0xff) >> 8;
										j10 = j13 * (j10 * (i5 >> 10) >> 16) + j17 * (j16 & 0xff) >> 8;
										j13 = j13 * j13 + j17 * (j16 >>> 24 & 0xff) >> 8;
										if ((j12 & 0xffffff00) != 0)
											j12 = 255 >> (j12 >> 28 & 8);
										if ((j11 & 0xffffff00) != 0)
											j11 = 255 >> (j11 >> 28 & 8);
										if ((j10 & 0xffffff00) != 0)
											j10 = 255 >> (j10 >> 28 & 8);
										if ((j13 & 0xffffff00) != 0)
											j13 = 255 >> (j13 >> 28 & 8);
										ai2[j14] = j10 | j11 << 8 | j12 << 16 | j13 << 24;
										if (flag1)
											ai3[j14] = j5;
									}
								}
								i4 += i8;
								j4 += j8;
								k4 += k5;
								l4 += l5;
								i5 += i6;
								j5 += l7;
							}

						}
					}
					f41 = f47;
					f42 = f48;
					f43 = f49;
					f44 = f50;
					f45 = f51;
				} while (j3 < k3);
				if (f6 < (float) i1)
					framebuffer.xstart[i] = (int) f6;
				if (k3 > j1)
					framebuffer.xend[i] = k3;
				if (f13 < f21)
					f13 = f21;
				if (f13 > f17)
					framebuffer.zhigh[i] = f13;
			}
		}
	}

	private final void drawShadedZbufferedFilteredAdditiveAlphaScanline(float f, float f1, float f2, float f3, float f4,
			float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, int i, float f14,
			float f15, FrameBuffer framebuffer, Texture texture, boolean flag, int j, int k, boolean flag1) {
		if (k != 1) {
			float f16 = k;
			f *= f16;
			f1 *= f16;
			f2 *= f16;
			f3 *= f16;
			f4 *= f16;
			f5 *= f16;
		}
		int ai[] = texture.texels;
		int ai1[] = texture.alpha;
		int ai2[] = framebuffer.pixels;
		int ai3[] = framebuffer.zbuffer;
		boolean flag2 = false;
		int i1 = framebuffer.xstart[i];
		int j1 = framebuffer.xend[i];
		float f17 = framebuffer.zhigh[i];
		int k1 = framebuffer.exXstart[i];
		int l1 = framebuffer.exXend[i];
		float f18 = framebuffer.exZlow[i];
		int i2 = framebuffer.exXstart2[i];
		int j2 = framebuffer.exXend2[i];
		float f19 = framebuffer.exZlow2[i];
		if (f7 < f6) {
			float f20 = f6;
			f6 = f7;
			f7 = f20;
			f20 = f8;
			f8 = f9;
			f9 = f20;
			f20 = f10;
			f10 = f11;
			f11 = f20;
			f20 = f12;
			f12 = f13;
			f13 = f20;
			f20 = f;
			f = f1;
			f1 = f20;
			f20 = f2;
			f2 = f3;
			f3 = f20;
			f20 = f4;
			f4 = f5;
			f5 = f20;
		}
		f7--;
		if (f6 < f15 && f7 >= f14) {
			if (l1 <= j2) {
				if (l1 >= i2) {
					l1 = j2;
					framebuffer.exXend[i] = framebuffer.exXend2[i];
					if (i2 < k1) {
						k1 = i2;
						framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					}
					if (f19 < f18) {
						f18 = f19;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
					framebuffer.exXstart2[i] = 0x5f5e0ff;
					i2 = 0x5f5e0ff;
					framebuffer.exXend2[i] = -1;
					j2 = -1;
					framebuffer.exZlow2[i] = 2.147484E+009F;
					f19 = 2.147484E+009F;
				}
			} else if (j2 >= k1) {
				if (i2 < k1) {
					k1 = i2;
					framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					if (f19 < f18) {
						f18 = f19;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
				}
				framebuffer.exXstart2[i] = 0x5f5e0ff;
				i2 = 0x5f5e0ff;
				framebuffer.exXend2[i] = -1;
				j2 = -1;
				framebuffer.exZlow2[i] = 2.147484E+009F;
				f19 = 2.147484E+009F;
			}
			if ((!Config.spanBasedHsr || (float) k1 > f6 || (float) l1 < f7 || f18 < f12 || f18 < f13)
					&& ((float) i2 > f6 || (float) j2 < f7 || f19 < f12 || f19 < f13)) {
				if (f12 > 1.0F)
					f12 = 1.0F;
				if (f13 > 1.0F)
					f13 = 1.0F;
				float f21 = f12;
				float f22 = f7 - f6;
				if (f22 < 1.0F) {
					f1 = f;
					f3 = f2;
					f5 = f4;
				}
				float f23;
				float f24;
				float f25;
				float f26;
				float f27;
				float f28;
				if (f22 != 0.0F) {
					float f30 = 1.0F / f22;
					f23 = (f9 - f8) * f30;
					f24 = (f11 - f10) * f30;
					f25 = (f13 - f12) * f30;
					f27 = (f1 - f) * f30;
					f28 = (f3 - f2) * f30;
					f26 = (f5 - f4) * f30;
				} else {
					f23 = 0.0F;
					f24 = 0.0F;
					f25 = 0.0F;
					f26 = 0.0F;
					f27 = 0.0F;
					f28 = 0.0F;
				}
				if (f7 >= f15)
					f7 = f15 - 1.0F;
				if (f6 < f14) {
					f6 = f14 - f6;
					f += f6 * f27;
					f2 += f6 * f28;
					f4 += f6 * f26;
					f8 += f6 * f23;
					f10 += f6 * f24;
					f12 += f6 * f25;
					f6 = f14;
				}
				int k2 = i * framebuffer.width;
				int l2 = (int) f6;
				int j3 = l2 - 1;
				int k3 = (int) f7;
				int l3 = 16;
				float f31 = 16384F;
				float f32 = 16384F;
				float f29;
				if (Config.zTrick)
					f29 = 1.342177E+008F;
				else
					f29 = 2.684355E+008F;
				float f33 = 16F * f23;
				float f34 = 16F * f24;
				float f35 = 16F * f25;
				float f36 = 16F * f27;
				float f37 = 16F * f28;
				float f38 = 16F * f26;
				float f39 = 1.0F / f12;
				float f41 = f8 * f39;
				float f42 = f10 * f39;
				float f43 = f * f39;
				float f44 = f2 * f39;
				float f45 = f4 * f39;
				byte byte0 = texture.shifter;
				int k4 = (int) (f43 * 262144F);
				int l4 = (int) (f44 * 262144F);
				int i5 = (int) (f45 * 262144F);
				int j5 = 0;
				boolean flag3 = false;
				if (Config.zTrick) {
					j5 = (int) (f12 * 2.147484E+009F);
					if ((framebuffer.frameCount & 1L) == 1L) {
						j5 = -j5;
						flag3 = true;
					}
				} else {
					j5 = (int) (f12 * 4.294967E+009F + -2.147484E+009F);
				}
				int i4 = (int) (f41 * 262144F);
				int j4 = (int) (f42 * 262144F);
				boolean flag4 = false;
				boolean flag5 = false;
				boolean flag6 = false;
				int j6 = i & 1;
				int l6 = 1 - j6 << 17;
				int i7 = j6 << 17;
				do {
					int i3 = j3 + 1;
					j3 += l3;
					if (j3 > k3) {
						l3 -= j3 - k3;
						j3 = k3;
						if (l3 != 0) {
							f33 = (float) l3 * f23;
							f34 = (float) l3 * f24;
							f35 = (float) l3 * f25;
							f36 = (float) l3 * f27;
							f37 = (float) l3 * f28;
							f38 = (float) l3 * f26;
							f32 = multiLU[l3];
							f31 = f32;
							if (Config.zTrick)
								f29 = multiZTLU[l3];
							else
								f29 = multiZLU[l3];
						} else {
							f33 = 0.0F;
							f34 = 0.0F;
							f35 = 0.0F;
							f36 = 0.0F;
							f37 = 0.0F;
							f38 = 0.0F;
							l3 = 1;
							f32 = 262144F;
							f31 = 262144F;
							if (Config.zTrick)
								f29 = 2.147484E+009F;
							else
								f29 = 4.294967E+009F;
						}
					}
					float f46 = f12;
					if (j3 == k3 && f7 < f15 - 1.0F) {
						f33 = f9 - f8;
						f34 = f11 - f10;
						f35 = f13 - f12;
					}
					f8 += f33;
					f10 += f34;
					f12 += f35;
					f += f36;
					f2 += f37;
					f4 += f38;
					float f40 = 1.0F / f12;
					float f47 = f8 * f40;
					float f48 = f10 * f40;
					float f49 = f * f40;
					float f50 = f2 * f40;
					float f51 = f4 * f40;
					int j7 = texture.width - 1 << 18;
					int k7 = texture.height - 1 << 18;
					int l7 = (int) (f29 * (f12 - f46));
					if (flag3)
						l7 = -l7;
					int i8 = (int) (f31 * (f47 - f41));
					int j8 = (int) (f31 * (f48 - f42));
					int k5 = (int) (f32 * (f49 - f43));
					int l5 = (int) (f32 * (f50 - f44));
					int i6 = (int) (f32 * (f51 - f45));
					if (Config.spanBasedHsr
							&& (k1 <= i3 && l1 >= j3 && f18 >= f46 && f18 >= f12 || i2 <= i3 && j2 >= j3 && f19 >= f46 && f19 >= f12)) {
						if (j3 != k3) {
							j5 += l3 * l7;
							i4 += l3 * i8;
							j4 += l3 * j8;
							k4 += l3 * k5;
							l4 += l3 * l5;
							i5 += l3 * i6;
						}
					} else {
						int l = j3 + k2;
						boolean flag7 = false;
						boolean flag8 = false;
						boolean flag9 = false;
						boolean flag10 = false;
						boolean flag11 = false;
						if (Config.texelFilter) {
							if (!flag && i8 < 0x30000 && i8 > 0xfffd0000 && j8 <= 0x30000 && j8 >= 0xfffd0000)
								flag = true;
						} else {
							flag = false;
						}
						if (flag) {
							int k6;
							if ((i3 & 1) != 0)
								k6 = 0x10000 + l6;
							else
								k6 = i7;
							if (!flag3) {
								for (int k13 = i3 + k2; k13 <= l; k13++) {
									if (j5 > framebuffer.zbuffer[k13]) {
										int k14 = ((i4 + k6 & j7) >> 18) + (((j4 + (k6 ^ 0x10000) & k7) >> 18) << byte0);
										int k8 = ai[k14];
										int k12 = ai1[k14] >>> 24;
										k6 ^= 0x30000;
										if (k12 != 0) {
											int k15 = ai2[k13];
											int k11 = (k8 >> 16) << j;
											int k10 = (k8 >> 8 & 0xff) << j;
											int k9 = (k8 & 0xff) << j;
											if (k11 > 255)
												k11 = 255;
											if (k10 > 255)
												k10 = 255;
											if (k9 > 255)
												k9 = 255;
											k11 = (k12 * (k11 * (k4 >> 10) >> 16) >> 8) + (k15 >> 16 & 0xff);
											k10 = (k12 * (k10 * (l4 >> 10) >> 16) >> 8) + (k15 >> 8 & 0xff);
											k9 = (k12 * (k9 * (i5 >> 10) >> 16) >> 8) + (k15 & 0xff);
											if ((k11 & 0xffffff00) != 0)
												k11 = 255 >> (k11 >> 28 & 8);
											if ((k10 & 0xffffff00) != 0)
												k10 = 255 >> (k10 >> 28 & 8);
											if ((k9 & 0xffffff00) != 0)
												k9 = 255 >> (k9 >> 28 & 8);
											if ((k12 & 0xffffff00) != 0)
												k12 = 255 >> (k12 >> 28 & 8);
											ai2[k13] = k9 | k10 << 8 | k11 << 16 | k12 << 24;
											if (flag1)
												ai3[k13] = j5;
										}
									}
									i4 += i8;
									j4 += j8;
									k4 += k5;
									l4 += l5;
									i5 += i6;
									j5 += l7;
								}

							} else {
								for (int l13 = i3 + k2; l13 <= l; l13++) {
									if (j5 < framebuffer.zbuffer[l13]) {
										int l14 = ((i4 + k6 & j7) >> 18) + (((j4 + (k6 ^ 0x10000) & k7) >> 18) << byte0);
										int l8 = ai[l14];
										int l12 = ai1[l14] >>> 24;
										k6 ^= 0x30000;
										if (l12 != 0) {
											int l15 = ai2[l13];
											int l11 = (l8 >> 16) << j;
											int l10 = (l8 >> 8 & 0xff) << j;
											int l9 = (l8 & 0xff) << j;
											if (l11 > 255)
												l11 = 255;
											if (l10 > 255)
												l10 = 255;
											if (l9 > 255)
												l9 = 255;
											l11 = (l12 * (l11 * (k4 >> 10) >> 16) >> 8) + (l15 >> 16 & 0xff);
											l10 = (l12 * (l10 * (l4 >> 10) >> 16) >> 8) + (l15 >> 8 & 0xff);
											l9 = (l12 * (l9 * (i5 >> 10) >> 16) >> 8) + (l15 & 0xff);
											if ((l11 & 0xffffff00) != 0)
												l11 = 255 >> (l11 >> 28 & 8);
											if ((l10 & 0xffffff00) != 0)
												l10 = 255 >> (l10 >> 28 & 8);
											if ((l9 & 0xffffff00) != 0)
												l9 = 255 >> (l9 >> 28 & 8);
											if ((l12 & 0xffffff00) != 0)
												l12 = 255 >> (l12 >> 28 & 8);
											ai2[l13] = l9 | l10 << 8 | l11 << 16 | l12 << 24;
											if (flag1)
												ai3[l13] = j5;
										}
									}
									i4 += i8;
									j4 += j8;
									k4 += k5;
									l4 += l5;
									i5 += i6;
									j5 += l7;
								}

							}
						} else if (!flag3) {
							for (int i14 = i3 + k2; i14 <= l; i14++) {
								if (ai3[i14] < j5) {
									int i15 = ((i4 & j7) >> 18) + (((j4 & k7) >> 18) << byte0);
									int i9 = ai[i15];
									int i13 = ai1[i15] >>> 24;
									if (i13 != 0) {
										int i16 = ai2[i14];
										int i12 = (i9 >> 16) << j;
										int i11 = (i9 >> 8 & 0xff) << j;
										int i10 = (i9 & 0xff) << j;
										if (i12 > 255)
											i12 = 255;
										if (i11 > 255)
											i11 = 255;
										if (i10 > 255)
											i10 = 255;
										i12 = (i13 * (i12 * (k4 >> 10) >> 16) >> 8) + (i16 >> 16 & 0xff);
										i11 = (i13 * (i11 * (l4 >> 10) >> 16) >> 8) + (i16 >> 8 & 0xff);
										i10 = (i13 * (i10 * (i5 >> 10) >> 16) >> 8) + (i16 & 0xff);
										if ((i12 & 0xffffff00) != 0)
											i12 = 255 >> (i12 >> 28 & 8);
										if ((i11 & 0xffffff00) != 0)
											i11 = 255 >> (i11 >> 28 & 8);
										if ((i10 & 0xffffff00) != 0)
											i10 = 255 >> (i10 >> 28 & 8);
										if ((i13 & 0xffffff00) != 0)
											i13 = 255 >> (i13 >> 28 & 8);
										ai2[i14] = i10 | i11 << 8 | i12 << 16 | i13 << 24;
										if (flag1)
											ai3[i14] = j5;
									}
								}
								i4 += i8;
								j4 += j8;
								k4 += k5;
								l4 += l5;
								i5 += i6;
								j5 += l7;
							}

						} else {
							for (int j14 = i3 + k2; j14 <= l; j14++) {
								if (ai3[j14] > j5) {
									int j15 = ((i4 & j7) >> 18) + (((j4 & k7) >> 18) << byte0);
									int j9 = ai[j15];
									int j13 = ai1[j15] >>> 24;
									if (j13 != 0) {
										int j16 = ai2[j14];
										int j12 = (j9 >> 16) << j;
										int j11 = (j9 >> 8 & 0xff) << j;
										int j10 = (j9 & 0xff) << j;
										if (j12 > 255)
											j12 = 255;
										if (j11 > 255)
											j11 = 255;
										if (j10 > 255)
											j10 = 255;
										j12 = (j13 * (j12 * (k4 >> 10) >> 16) >> 8) + (j16 >> 16 & 0xff);
										j11 = (j13 * (j11 * (l4 >> 10) >> 16) >> 8) + (j16 >> 8 & 0xff);
										j10 = (j13 * (j10 * (i5 >> 10) >> 16) >> 8) + (j16 & 0xff);
										if ((j12 & 0xffffff00) != 0)
											j12 = 255 >> (j12 >> 28 & 8);
										if ((j11 & 0xffffff00) != 0)
											j11 = 255 >> (j11 >> 28 & 8);
										if ((j10 & 0xffffff00) != 0)
											j10 = 255 >> (j10 >> 28 & 8);
										if ((j13 & 0xffffff00) != 0)
											j13 = 255 >> (j13 >> 28 & 8);
										ai2[j14] = j10 | j11 << 8 | j12 << 16 | j13 << 24;
										if (flag1)
											ai3[j14] = j5;
									}
								}
								i4 += i8;
								j4 += j8;
								k4 += k5;
								l4 += l5;
								i5 += i6;
								j5 += l7;
							}

						}
					}
					f41 = f47;
					f42 = f48;
					f43 = f49;
					f44 = f50;
					f45 = f51;
				} while (j3 < k3);
				if (f6 < (float) i1)
					framebuffer.xstart[i] = (int) f6;
				if (k3 > j1)
					framebuffer.xend[i] = k3;
				if (f13 < f21)
					f13 = f21;
				if (f13 > f17)
					framebuffer.zhigh[i] = f13;
			}
		}
	}

	public VideoMode[] getAvailableVideoModes() {
		return new VideoMode[0];
	}

	public boolean isInitialized() {
		return true;
	}

	private static final long serialVersionUID = 1L;
	private static final int ALPHA = 0xff000000;
	private static final int XORRER = 0x30000;
	private final int p[] = new int[3];
	private final TextureManager texMan = TextureManager.getInstance();
	private static float multiLU[];
	private static float multiZLU[];
	private static float multiZTLU[];
	private IPaintListener listener;
	private boolean listenerActive;
	private Worker worker;
	private MyWorkLoad workers[];

	static {
		multiLU = new float[17];
		multiZLU = new float[17];
		multiZTLU = new float[17];
		for (int i = 1; i < 17; i++) {
			multiLU[i] = 262144F / (float) i;
			multiZTLU[i] = 2.147484E+009F / (float) i;
			multiZLU[i] = 4.294967E+009F / (float) i;
		}

	}

}
