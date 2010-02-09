// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            VideoMode, IRenderer, TextureManager, Logger, 
//            IPaintListener, VisList, Object3D, World, 
//            Texture, FrameBuffer, Portals, Mesh, 
//            Vectors, Config

public final class LegacyRenderer implements IRenderer, Serializable {

	LegacyRenderer() {
		listener = null;
		listenerActive = true;
	}

	public void init(int i, int j, int k, int l, int i1) {
		Logger.log("Software renderer (legacy mode) initialized", 2);
	}

	public void setPaintListener(IPaintListener ipaintlistener) {
		listener = ipaintlistener;
	}

	public void dispose() {
		listener = null;
		Logger.log("Software renderer disposed", 2);
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

	public void drawVertexArray(VisList vislist, int i, int j, FrameBuffer framebuffer, World world) {
		for (int k = i; k <= j; k++)
			drawPolygon(vislist, k, framebuffer, world);

	}

	public void drawPolygon(VisList vislist, int i, FrameBuffer framebuffer, World world) {
		int j = vislist.vnum[i];
		Object3D object3d = vislist.vobj[i];
		if (vislist.stageCnt[i] > 0)
			return;
		Texture texture = texMan.textures[object3d.texture[j]];
		Texture texture1 = texMan.textures[object3d.basemap[j]];
		Texture texture2 = null;
		texture1.updateUsage(world.drawCnt);
		texture.updateUsage(world.drawCnt);
		int k = vislist.vorg[i].transValue;
		boolean flag = (vislist.mode[i] & 2) == 2;
		boolean flag1 = (vislist.mode[i] & 1) == 1;
		boolean flag2 = (vislist.mode[i] & 4) == 4;
		boolean flag3 = (vislist.mode[i] & 8) == 8;
		i = vislist.portalNum[i];
		float f = 0.0F;
		float f1 = framebuffer.width;
		float f2 = 0.0F;
		float f3 = framebuffer.height;
		if (i != 0x5f5e0ff) {
			f = world.portals.lowx[i];
			f1 = world.portals.highx[i] + 1.0F;
			f2 = world.portals.lowy[i];
			f3 = world.portals.highy[i] + 1.0F;
		}
		int l = (int) f2;
		if (flag3 && object3d.bumpmap != null) {
			texture2 = texMan.textures[object3d.bumpmap[j]];
			texture2.updateUsage(world.drawCnt);
		}
		int i1 = 0;
		float f4 = 0.0F;
		float f5 = 0.0F;
		float f6 = 0.0F;
		float f7 = 0.0F;
		float f8 = 0.0F;
		float f9 = 0.0F;
		float f10 = 0.0F;
		float f11 = 0.0F;
		float f18 = 0.0F;
		float f19 = 0.0F;
		float f20 = 0.0F;
		float f21 = 0.0F;
		float f12 = 0.0F;
		float f13 = 0.0F;
		float f14 = 0.0F;
		float f15 = 0.0F;
		float f16 = 0.0F;
		float f17 = 0.0F;
		p[0] = object3d.objMesh.points[j][0];
		p[1] = object3d.objMesh.points[j][1];
		p[2] = object3d.objMesh.points[j][2];
		int j2 = 0x1869f;
		int k2 = 0;
		int l2 = 0x1869f;
		int i3 = 0;
		boolean flag4 = object3d.alwaysFilter;
		if (framebuffer.useBb && framebuffer.getType() == 0) {
			for (int j3 = 0; j3 < 3; j3++) {
				if (object3d.objVectors.sy[object3d.objMesh.coords[p[j3]]] < (float) j2) {
					j2 = (int) object3d.objVectors.sy[object3d.objMesh.coords[p[j3]]];
					i1 = j3;
				}
				if (object3d.objVectors.sy[object3d.objMesh.coords[p[j3]]] > (float) k2)
					k2 = (int) object3d.objVectors.sy[object3d.objMesh.coords[p[j3]]];
				if (object3d.objVectors.sx[object3d.objMesh.coords[p[j3]]] < (float) l2)
					l2 = (int) object3d.objVectors.sx[object3d.objMesh.coords[p[j3]]];
				if (object3d.objVectors.sx[object3d.objMesh.coords[p[j3]]] > (float) i3)
					i3 = (int) object3d.objVectors.sx[object3d.objMesh.coords[p[j3]]];
				if (object3d.objVectors.sy[object3d.objMesh.coords[p[j3]]] < object3d.objVectors.sy[object3d.objMesh.coords[p[i1]]])
					i1 = j3;
			}

			if (k2 > framebuffer.bbYu)
				framebuffer.bbYu = k2;
			if (j2 < framebuffer.bbYo)
				framebuffer.bbYo = j2;
			if (l2 < framebuffer.bbXl)
				framebuffer.bbXl = l2;
			if (i3 > framebuffer.bbXr)
				framebuffer.bbXr = i3;
		} else {
			for (int k3 = 1; k3 < 3; k3++)
				if (object3d.objVectors.sy[object3d.objMesh.coords[p[k3]]] < object3d.objVectors.sy[object3d.objMesh.coords[p[i1]]])
					i1 = k3;

		}
		int j1 = i1 + 1;
		int k1 = i1 - 1;
		if (j1 > 2)
			j1 = 0;
		if (k1 < 0)
			k1 = 2;
		float f22 = object3d.objVectors.sy[object3d.objMesh.coords[p[i1]]];
		int l3 = (int) f22;
		float f27 = object3d.objVectors.sx[object3d.objMesh.coords[p[i1]]];
		float f28 = f27;
		float f29 = object3d.objVectors.su[p[i1]];
		float f30 = f29;
		float f31 = object3d.objVectors.sv[p[i1]];
		float f32 = f31;
		float f33 = object3d.objVectors.sz[object3d.objMesh.coords[p[i1]]];
		float f34 = f33;
		float f35 = 0.0F;
		float f36 = 0.0F;
		float f37 = 0.0F;
		float f38 = 0.0F;
		if (object3d.objVectors.bsu != null) {
			f35 = object3d.objVectors.bsu[p[i1]];
			f36 = f35;
			f37 = object3d.objVectors.bsv[p[i1]];
			f38 = f37;
		}
		float f39 = object3d.objVectors.sr[p[i1]];
		float f40 = f39;
		float f41 = object3d.objVectors.sg[p[i1]];
		float f42 = f41;
		float f43 = object3d.objVectors.sb[p[i1]];
		float f44 = f43;
		float f45 = object3d.objVectors.sy[object3d.objMesh.coords[p[j1]]];
		float f46 = object3d.objVectors.sy[object3d.objMesh.coords[p[k1]]];
		int l1 = (int) f45;
		int i2 = (int) f46;
		float f47 = (f45 - f22) + 1.0F;
		float f50 = (f46 - f22) + 1.0F;
		float f53 = (float) l3 - f22;
		if (f47 != 0.0F) {
			float f56 = 1.0F / f47;
			Vectors vectors = object3d.objVectors;
			int i4 = p[j1];
			f4 = (vectors.sx[object3d.objMesh.coords[i4]] - f27) * f56;
			f6 = (vectors.su[i4] - f29) * f56;
			f8 = (vectors.sv[i4] - f31) * f56;
			f10 = (vectors.sz[object3d.objMesh.coords[i4]] - f33) * f56;
			if (vectors.bsu != null) {
				f18 = (vectors.bsu[i4] - f35) * f56;
				f20 = (vectors.bsv[i4] - f37) * f56;
			}
			f14 = (vectors.sr[i4] - f39) * f56;
			f16 = (vectors.sg[i4] - f41) * f56;
			f12 = (vectors.sb[i4] - f43) * f56;
		}
		if (f50 != 0.0F) {
			float f57 = 1.0F / f50;
			Vectors vectors1 = object3d.objVectors;
			int j4 = p[k1];
			f5 = (vectors1.sx[object3d.objMesh.coords[j4]] - f27) * f57;
			f7 = (vectors1.su[j4] - f29) * f57;
			f9 = (vectors1.sv[j4] - f31) * f57;
			f11 = (vectors1.sz[object3d.objMesh.coords[j4]] - f33) * f57;
			if (vectors1.bsu != null) {
				f19 = (vectors1.bsu[j4] - f35) * f57;
				f21 = (vectors1.bsv[j4] - f37) * f57;
			}
			f15 = (vectors1.sr[j4] - f39) * f57;
			f17 = (vectors1.sg[j4] - f41) * f57;
			f13 = (vectors1.sb[j4] - f43) * f57;
		}
		byte byte0 = 2;
		byte byte1 = 0;
		float f58 = 0.0F;
		float f59 = 0.0F;
		float f60 = 0.0F;
		if ((float) l3 >= f3)
			byte0 = 0;
		else if ((float) l3 < f2) {
			int k4 = l - l3;
			if ((float) l1 >= f2 && (float) i2 >= f2) {
				f27 += f4 * (float) k4;
				f28 += f5 * (float) k4;
				f29 += f6 * (float) k4;
				f30 += f7 * (float) k4;
				f31 += f8 * (float) k4;
				f32 += f9 * (float) k4;
				f33 += f10 * (float) k4;
				f34 += f11 * (float) k4;
				f35 += f18 * (float) k4;
				f36 += f19 * (float) k4;
				f37 += f20 * (float) k4;
				f38 += f21 * (float) k4;
				f43 += f12 * (float) k4;
				f44 += f13 * (float) k4;
				f39 += f14 * (float) k4;
				f40 += f15 * (float) k4;
				f41 += f16 * (float) k4;
				f42 += f17 * (float) k4;
				l3 = l;
			} else if ((float) l1 < f2 && (float) i2 < f2)
				byte0 = 0;
			else if ((float) l1 < f2) {
				byte0 = 1;
				int i5 = j1 + 1;
				if (i5 > 2)
					i5 = 0;
				Vectors vectors2 = object3d.objVectors;
				Mesh mesh = object3d.objMesh;
				int i6 = p[j1];
				int i7 = p[i5];
				l1 = (int) vectors2.sy[mesh.coords[i7]];
				float f48 = (vectors2.sy[mesh.coords[i7]] - vectors2.sy[mesh.coords[i6]]) + 1.0F;
				if (f48 != 0.0F) {
					f4 = vectors2.sx[mesh.coords[i7]] - vectors2.sx[mesh.coords[i6]];
					f6 = (vectors2.su[p[i5]] - vectors2.su[i6]) / f48;
					f8 = (vectors2.sv[p[i5]] - vectors2.sv[i6]) / f48;
					f10 = (vectors2.sz[mesh.coords[i7]] - vectors2.sz[mesh.coords[i6]]) / f48;
					if (vectors2.bsu != null) {
						f18 = (vectors2.bsu[i7] - vectors2.bsu[i6]) / f48;
						f20 = (vectors2.bsv[i7] - vectors2.bsv[i6]) / f48;
					}
					f14 = (vectors2.sr[i7] - vectors2.sr[i6]) / f48;
					f16 = (vectors2.sg[i7] - vectors2.sg[i6]) / f48;
					f12 = (vectors2.sb[i7] - vectors2.sb[i6]) / f48;
					f4 /= f48;
				}
				f27 = vectors2.sx[mesh.coords[i6]];
				f29 = vectors2.su[i6];
				f31 = vectors2.sv[i6];
				f33 = vectors2.sz[mesh.coords[i6]];
				if (vectors2.bsu != null) {
					f35 = vectors2.bsu[i6];
					f37 = vectors2.bsv[i6];
				}
				f39 = vectors2.sr[i6];
				f41 = vectors2.sg[i6];
				f43 = vectors2.sb[i6];
				byte1 = 1;
				if (f2 == 0.0F) {
					f58 = 0.0F;
				} else {
					float f23 = f2;
					int i8 = (int) f2;
					f58 = (float) i8 - f23;
				}
				f28 += f5 * (float) k4;
				f30 += f7 * (float) k4;
				f32 += f9 * (float) k4;
				f34 += f11 * (float) k4;
				f36 += f19 * (float) k4;
				f38 += f21 * (float) k4;
				f44 += f13 * (float) k4;
				f40 += f15 * (float) k4;
				f42 += f17 * (float) k4;
				float f61 = f2 - vectors2.sy[mesh.coords[i6]];
				j1 = i5;
				l3 = l;
				f27 += f4 * f61;
				f29 += f6 * f61;
				f31 += f8 * f61;
				f33 += f10 * f61;
				f35 += f18 * f61;
				f37 += f20 * f61;
				f39 += f14 * f61;
				f41 += f16 * f61;
				f43 += f12 * f61;
			} else {
				byte0 = 1;
				int j5 = k1 - 1;
				if (j5 < 0)
					j5 = 2;
				Vectors vectors3 = object3d.objVectors;
				Mesh mesh1 = object3d.objMesh;
				int j6 = p[k1];
				int j7 = p[j5];
				i2 = (int) vectors3.sy[mesh1.coords[j7]];
				float f51 = (vectors3.sy[mesh1.coords[j7]] - vectors3.sy[mesh1.coords[j6]]) + 1.0F;
				if (f51 != 0.0F) {
					f5 = vectors3.sx[mesh1.coords[j7]] - vectors3.sx[mesh1.coords[j6]];
					f7 = (vectors3.su[j7] - vectors3.su[j6]) / f51;
					f9 = (vectors3.sv[j7] - vectors3.sv[j6]) / f51;
					f11 = (vectors3.sz[mesh1.coords[j7]] - vectors3.sz[mesh1.coords[j6]]) / f51;
					if (vectors3.bsu != null) {
						f19 = (vectors3.bsu[j7] - vectors3.bsu[j6]) / f51;
						f21 = (vectors3.bsv[j7] - vectors3.bsv[j6]) / f51;
					}
					f15 = (vectors3.sr[j7] - vectors3.sr[j6]) / f51;
					f17 = (vectors3.sg[j7] - vectors3.sg[j6]) / f51;
					f13 = (vectors3.sb[j7] - vectors3.sb[j6]) / f51;
					f5 /= f51;
				}
				f28 = vectors3.sx[mesh1.coords[j6]];
				f30 = vectors3.su[j6];
				f32 = vectors3.sv[j6];
				f34 = vectors3.sz[mesh1.coords[j6]];
				if (vectors3.bsu != null) {
					f36 = vectors3.bsu[j6];
					f38 = vectors3.bsv[j6];
				}
				f40 = vectors3.sr[j6];
				f42 = vectors3.sg[j6];
				f44 = vectors3.sb[j6];
				byte1 = 2;
				if (f2 == 0.0F) {
					f59 = 0.0F;
				} else {
					float f24 = f2;
					int j8 = (int) f2;
					f59 = (float) j8 - f24;
				}
				f27 += f4 * (float) k4;
				f29 += f6 * (float) k4;
				f31 += f8 * (float) k4;
				f33 += f10 * (float) k4;
				f35 += f18 * (float) k4;
				f37 += f20 * (float) k4;
				f39 += f14 * (float) k4;
				f41 += f16 * (float) k4;
				f43 += f12 * (float) k4;
				float f62 = f2 - vectors3.sy[mesh1.coords[j6]];
				k1 = j5;
				l3 = l;
				f28 += f5 * f62;
				f30 += f7 * f62;
				f32 += f9 * f62;
				f34 += f11 * f62;
				f36 += f19 * f62;
				f38 += f21 * f62;
				f40 += f15 * f62;
				f42 += f17 * f62;
				f44 += f13 * f62;
			}
		}
		switch (byte1) {
		case 0: // '\0'
			f27 += f4 * f53 + 0.5F;
			f33 += f10 * f53;
			f29 += f6 * f53;
			f31 += f8 * f53;
			f39 += f14 * f53;
			f41 += f16 * f53;
			f43 += f12 * f53;
			f35 += f18 * f53;
			f37 += f20 * f53;
			f28 += f5 * f53 + 0.5F;
			f34 += f11 * f53;
			f30 += f7 * f53;
			f32 += f9 * f53;
			f40 += f15 * f53;
			f42 += f17 * f53;
			f44 += f13 * f53;
			f36 += f19 * f53;
			f38 += f21 * f53;
			break;

		case 1: // '\001'
			f27 += f4 * f58 + 0.5F;
			f28 += f5 * f53 + 0.5F;
			f33 += f10 * f58;
			f34 += f11 * f53;
			f29 += f6 * f58;
			f30 += f7 * f53;
			f31 += f8 * f58;
			f32 += f9 * f53;
			f39 += f14 * f58;
			f40 += f15 * f53;
			f41 += f16 * f58;
			f42 += f17 * f53;
			f43 += f12 * f58;
			f44 += f13 * f53;
			f35 += f18 * f58;
			f37 += f20 * f58;
			f36 += f19 * f53;
			f38 += f21 * f53;
			break;

		case 2: // '\002'
			f27 += f4 * f53 + 0.5F;
			f28 += f5 * f59 + 0.5F;
			f33 += f10 * f53;
			f34 += f11 * f59;
			f29 += f6 * f53;
			f30 += f7 * f59;
			f31 += f8 * f53;
			f32 += f9 * f59;
			f39 += f14 * f53;
			f40 += f15 * f59;
			f41 += f16 * f53;
			f42 += f17 * f59;
			f43 += f12 * f53;
			f44 += f13 * f59;
			f35 += f18 * f53;
			f37 += f20 * f53;
			f36 += f19 * f59;
			f38 += f21 * f59;
			break;
		}
		boolean flag5 = false;
		boolean flag6 = Config.texelFilter & (!object3d.alwaysFilter);
		int k5 = byte0;
		do {
			if (k5 <= 0)
				break;
			int l4;
			if (l1 < i2)
				l4 = l1;
			else
				l4 = i2;
			int l5 = (int) (f3 - 1.0F);
			do {
				if (l3 >= l4 && l3 != l5)
					break;
				f27 += f4;
				f28 += f5;
				f29 += f6;
				f31 += f8;
				f33 += f10;
				f35 += f18;
				f37 += f20;
				f39 += f14;
				f41 += f16;
				f43 += f12;
				f30 += f7;
				f32 += f9;
				f34 += f11;
				f36 += f19;
				f38 += f21;
				f40 += f15;
				f42 += f17;
				f44 += f13;
				if (flag6) {
					float f63 = 1.0F / f33;
					float f64 = f6 * f63;
					if (f64 < 0.0F)
						f64 = -f64;
					float f65 = f8 * f63;
					if (f65 < 0.0F)
						f65 = -f65;
					if (f64 <= 0.75F && f65 <= 0.75F) {
						flag4 = true;
					} else {
						float f66 = 1.0F / f34;
						float f69 = f7 * f66;
						if (f69 < 0.0F)
							f69 = -f69;
						float f70 = f9 * f66;
						if (f70 < 0.0F)
							f70 = -f70;
						if (f69 <= 0.75F && f70 <= 0.75F)
							flag4 = true;
						else
							flag4 = false;
					}
				}
				if ((f27 >= 0.0F || f28 >= 0.0F) && (f27 < f1 || f28 < f1)) {
					if (!flag3 || !flag1) {
						if (flag)
							drawShadedZbufferedFilteredTransparentScanline(f39, f40, f41, f42, f43, f44, f27, f28, f29, f30, f31,
									f32, f33, f34, l3, f, f1, framebuffer, texture, flag4, k);
						else
							drawShadedZbufferedFilteredScanline(f39, f40, f41, f42, f43, f44, f27, f28, f29, f30, f31, f32, f33, f34,
									l3, f, f1, framebuffer, texture, flag4);
					} else if (flag2)
						drawShadedZbufferedFilteredBumpmappedBlendedScanline(f39, f40, f41, f42, f43, f44, f27, f28, f29, f30, f31,
								f32, f33, f34, l3, f, f1, framebuffer, texture, flag4, f35, f36, f37, f38, texture2, texture1);
					else
						drawShadedZbufferedFilteredBumpmappedScanline(f39, f40, f41, f42, f43, f44, f27, f28, f29, f30, f31, f32,
								f33, f34, l3, f, f1, framebuffer, texture, flag4, f35, f36, f37, f38, texture2);
				} else if (f27 < 0.0F && f27 < f28 && f5 <= 0.0F) {
					k5 = 0;
					l3 = 0x5f5e0ff;
				} else if (f27 < 0.0F && f28 < f27 && f4 <= 0.0F) {
					k5 = 0;
					l3 = 0x5f5e0ff;
				} else if (f28 >= f1 && f27 < f28 && f4 >= 0.0F) {
					k5 = 0;
					l3 = 0x5f5e0ff;
				} else if (f28 >= f1 && f28 < f27 && f5 >= 0.0F) {
					k5 = 0;
					l3 = 0x5f5e0ff;
				}
				if ((float) (++l3) >= f3) {
					k5 = 0;
					l3 = 0x5f5e0fe;
					l1 = 0x5f5e0ff;
					i2 = 0x5f5e0ff;
				}
			} while (true);
			if (l3 >= l1) {
				int k6 = j1 + 1;
				if (k6 > 2)
					k6 = 0;
				if (k5 == byte0) {
					l1 = (int) object3d.objVectors.sy[object3d.objMesh.coords[p[k6]]];
					float f49 = (object3d.objVectors.sy[object3d.objMesh.coords[p[k6]]] - object3d.objVectors.sy[object3d.objMesh.coords[p[j1]]]) + 1.0F;
					int k7 = p[j1];
					int k8 = p[k6];
					if (f49 != 0.0F) {
						float f67 = 1.0F / f49;
						f4 = (object3d.objVectors.sx[object3d.objMesh.coords[k8]] - object3d.objVectors.sx[object3d.objMesh.coords[k7]])
								* f67;
						f6 = (object3d.objVectors.su[k8] - object3d.objVectors.su[k7]) * f67;
						f8 = (object3d.objVectors.sv[k8] - object3d.objVectors.sv[k7]) * f67;
						f10 = (object3d.objVectors.sz[object3d.objMesh.coords[k8]] - object3d.objVectors.sz[object3d.objMesh.coords[k7]])
								* f67;
						if (object3d.objVectors.bsu != null) {
							f18 = (object3d.objVectors.bsu[k8] - object3d.objVectors.bsu[k7]) * f67;
							f20 = (object3d.objVectors.bsv[k8] - object3d.objVectors.bsv[k7]) * f67;
						}
						f14 = (object3d.objVectors.sr[k8] - object3d.objVectors.sr[k7]) * f67;
						f16 = (object3d.objVectors.sg[k8] - object3d.objVectors.sg[k7]) * f67;
						f12 = (object3d.objVectors.sb[k8] - object3d.objVectors.sb[k7]) * f67;
					}
					f27 = object3d.objVectors.sx[object3d.objMesh.coords[k7]];
					f29 = object3d.objVectors.su[k7];
					f31 = object3d.objVectors.sv[k7];
					f33 = object3d.objVectors.sz[object3d.objMesh.coords[k7]];
					if (object3d.objVectors.bsu != null) {
						f35 = object3d.objVectors.bsu[k7];
						f37 = object3d.objVectors.bsv[k7];
					}
					f39 = object3d.objVectors.sr[k7];
					f41 = object3d.objVectors.sg[k7];
					f43 = object3d.objVectors.sb[k7];
					float f25 = object3d.objVectors.sy[object3d.objMesh.coords[k7]];
					float f54 = (float) l3 - f25;
					f27 += f4 * f54 + 0.5F;
					f33 += f10 * f54;
					f29 += f6 * f54;
					f31 += f8 * f54;
					f39 += f14 * f54;
					f41 += f16 * f54;
					f43 += f12 * f54;
					f35 += f18 * f54;
					f37 += f20 * f54;
				}
				k5--;
				j1 = k6;
			}
			if (l3 >= i2) {
				int l6 = k1 - 1;
				if (l6 < 0)
					l6 = 2;
				if (k5 == byte0) {
					i2 = (int) object3d.objVectors.sy[object3d.objMesh.coords[p[l6]]];
					float f52 = (object3d.objVectors.sy[object3d.objMesh.coords[p[l6]]] - object3d.objVectors.sy[object3d.objMesh.coords[p[k1]]]) + 1.0F;
					int l7 = p[k1];
					int l8 = p[l6];
					if (f52 != 0.0F) {
						float f68 = 1.0F / f52;
						f5 = (object3d.objVectors.sx[object3d.objMesh.coords[l8]] - object3d.objVectors.sx[object3d.objMesh.coords[l7]])
								* f68;
						f7 = (object3d.objVectors.su[l8] - object3d.objVectors.su[l7]) * f68;
						f9 = (object3d.objVectors.sv[l8] - object3d.objVectors.sv[l7]) * f68;
						f11 = (object3d.objVectors.sz[object3d.objMesh.coords[l8]] - object3d.objVectors.sz[object3d.objMesh.coords[l7]])
								* f68;
						if (object3d.objVectors.bsu != null) {
							f19 = (object3d.objVectors.bsu[l8] - object3d.objVectors.bsu[l7]) * f68;
							f21 = (object3d.objVectors.bsv[l8] - object3d.objVectors.bsv[l7]) * f68;
						}
						f15 = (object3d.objVectors.sr[l8] - object3d.objVectors.sr[l7]) * f68;
						f17 = (object3d.objVectors.sg[l8] - object3d.objVectors.sg[l7]) * f68;
						f13 = (object3d.objVectors.sb[l8] - object3d.objVectors.sb[l7]) * f68;
					}
					f28 = object3d.objVectors.sx[object3d.objMesh.coords[l7]];
					f30 = object3d.objVectors.su[l7];
					f32 = object3d.objVectors.sv[l7];
					f34 = object3d.objVectors.sz[object3d.objMesh.coords[l7]];
					if (object3d.objVectors.bsu != null) {
						f36 = object3d.objVectors.bsu[l7];
						f38 = object3d.objVectors.bsv[l7];
					}
					f40 = object3d.objVectors.sr[l7];
					f42 = object3d.objVectors.sg[l7];
					f44 = object3d.objVectors.sb[l7];
					float f26 = object3d.objVectors.sy[object3d.objMesh.coords[l7]];
					float f55 = (float) l3 - f26;
					f28 += f5 * f55 + 0.5F;
					f34 += f11 * f55;
					f30 += f7 * f55;
					f32 += f9 * f55;
					f40 += f15 * f55;
					f42 += f17 * f55;
					f44 += f13 * f55;
					f36 += f19 * f55;
					f38 += f21 * f55;
				}
				k5--;
				k1 = l6;
			}
		} while (true);
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

	private void drawShadedZbufferedFilteredScanline(float f, float f1, float f2, float f3, float f4, float f5, float f6,
			float f7, float f8, float f9, float f10, float f11, float f12, float f13, int i, float f14, float f15,
			FrameBuffer framebuffer, Texture texture, boolean flag) {
		int ai[] = texture.texels;
		int ai1[] = framebuffer.pixels;
		int ai2[] = framebuffer.zbuffer;
		int j = framebuffer.xstart[i];
		int k = framebuffer.xend[i];
		float f16 = framebuffer.zhigh[i];
		int l = framebuffer.exXstart[i];
		int i1 = framebuffer.exXend[i];
		float f17 = framebuffer.exZlow[i];
		int j1 = framebuffer.exXstart2[i];
		int k1 = framebuffer.exXend2[i];
		float f18 = framebuffer.exZlow2[i];
		if (f7 < f6) {
			float f19 = f6;
			f6 = f7;
			f7 = f19;
			f19 = f8;
			f8 = f9;
			f9 = f19;
			f19 = f10;
			f10 = f11;
			f11 = f19;
			f19 = f12;
			f12 = f13;
			f13 = f19;
			f19 = f;
			f = f1;
			f1 = f19;
			f19 = f2;
			f2 = f3;
			f3 = f19;
			f19 = f4;
			f4 = f5;
			f5 = f19;
		}
		f7--;
		if (f6 < f15 && f7 >= f14) {
			if (i1 <= k1) {
				if (i1 >= j1) {
					i1 = k1;
					framebuffer.exXend[i] = framebuffer.exXend2[i];
					if (j1 < l) {
						l = j1;
						framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					}
					if (f18 < f17) {
						f17 = f18;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
					framebuffer.exXstart2[i] = 0x5f5e0ff;
					j1 = 0x5f5e0ff;
					framebuffer.exXend2[i] = -1;
					k1 = -1;
					framebuffer.exZlow2[i] = 2.147484E+009F;
					f18 = 2.147484E+009F;
				}
			} else if (k1 >= l) {
				if (j1 < l) {
					l = j1;
					framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					if (f18 < f17) {
						f17 = f18;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
				}
				framebuffer.exXstart2[i] = 0x5f5e0ff;
				j1 = 0x5f5e0ff;
				framebuffer.exXend2[i] = -1;
				k1 = -1;
				framebuffer.exZlow2[i] = 2.147484E+009F;
				f18 = 2.147484E+009F;
			}
			if ((!Config.spanBasedHsr || (float) l > f6 || (float) i1 < f7 || f17 < f12 || f17 < f13)
					&& ((float) j1 > f6 || (float) k1 < f7 || f18 < f12 || f18 < f13)) {
				if (f12 > 1.0F)
					f12 = 1.0F;
				if (f13 > 1.0F)
					f13 = 1.0F;
				float f20 = f12;
				float f21 = f7 - f6;
				if (f21 < 1.0F) {
					f1 = f;
					f3 = f2;
					f5 = f4;
				}
				float f22;
				float f23;
				float f24;
				float f25;
				float f26;
				float f27;
				if (f21 != 0.0F) {
					float f37 = 1.0F / f21;
					f22 = (f9 - f8) * f37;
					f23 = (f11 - f10) * f37;
					f24 = (f13 - f12) * f37;
					f26 = (f1 - f) * f37;
					f27 = (f3 - f2) * f37;
					f25 = (f5 - f4) * f37;
				} else {
					f22 = 0.0F;
					f23 = 0.0F;
					f24 = 0.0F;
					f25 = 0.0F;
					f26 = 0.0F;
					f27 = 0.0F;
				}
				if (f7 >= f15)
					f7 = f15 - 1.0F;
				if (f6 < f14) {
					f6 = f14 - f6;
					f += f6 * f26;
					f2 += f6 * f27;
					f4 += f6 * f25;
					f8 += f6 * f22;
					f10 += f6 * f23;
					f12 += f6 * f24;
					f6 = f14;
				}
				int l1 = i * framebuffer.width;
				int i2 = (int) f6;
				int k2 = i2;
				int l2 = i2 - 1;
				int i3 = (int) f7;
				int j3 = 16;
				float f28 = 16384F;
				float f29 = 16384F;
				float f30;
				if (Config.zTrick)
					f30 = 1.342177E+008F;
				else
					f30 = 2.684355E+008F;
				float f31 = 16F * f22;
				float f32 = 16F * f23;
				float f33 = 16F * f24;
				float f34 = 16F * f26;
				float f35 = 16F * f27;
				float f36 = 16F * f25;
				float f38 = 1.0F / f12;
				float f40 = f8 * f38;
				float f41 = f10 * f38;
				float f42 = f * f38;
				float f43 = f2 * f38;
				float f44 = f4 * f38;
				byte byte0 = texture.shifter;
				int i4 = (int) ((double) f42 * 262144D);
				int j4 = (int) ((double) f43 * 262144D);
				int k4 = (int) ((double) f44 * 262144D);
				int l4 = 0;
				boolean flag1 = false;
				if (Config.zTrick) {
					l4 = (int) (f12 * 2.147484E+009F);
					if ((framebuffer.frameCount & 1L) == 1L) {
						l4 = -l4;
						flag1 = true;
					}
				} else {
					l4 = (int) (f12 * 4.294967E+009F + -2.147484E+009F);
				}
				if (f40 > texture.xend)
					f40 = texture.xend;
				else if (f40 < 0.0F)
					f40 = 0.0F;
				if (f41 > texture.yend)
					f41 = texture.yend;
				else if (f41 < 0.0F)
					f41 = 0.0F;
				int k3 = (int) ((double) f40 * 262144D);
				int l3 = (int) ((double) f41 * 262144D);
				boolean flag2 = false;
				boolean flag3 = false;
				boolean flag4 = false;
				int l5 = i & 1;
				int j6 = 1 - l5 << 17;
				int k6 = l5 << 17;
				do {
					int j2 = l2 + 1;
					l2 += j3;
					if (l2 > i3) {
						j3 -= l2 - i3;
						l2 = i3;
						f31 = (float) j3 * f22;
						f32 = (float) j3 * f23;
						f33 = (float) j3 * f24;
						f34 = (float) j3 * f26;
						f35 = (float) j3 * f27;
						f36 = (float) j3 * f25;
						if (j3 == 0)
							j3 = 1;
						f29 = 262144F / (float) j3;
						f28 = f29;
						if (Config.zTrick)
							f30 = 2.147484E+009F / (float) j3;
						else
							f30 = 4.294967E+009F / (float) j3;
					}
					if (l2 == i3 && f7 < f15 - 1.0F) {
						f31 = f9 - f8;
						f32 = f11 - f10;
						f33 = f13 - f12;
					}
					float f45 = f12;
					f8 += f31;
					f10 += f32;
					f12 += f33;
					f += f34;
					f2 += f35;
					f4 += f36;
					float f39 = 1.0F / f12;
					float f46 = f8 * f39;
					float f47 = f10 * f39;
					float f48 = f * f39;
					float f49 = f2 * f39;
					float f50 = f4 * f39;
					if (f46 > texture.xend)
						f46 = texture.xend;
					else if (f46 < 0.0F)
						f46 = 0.0F;
					if (f47 > texture.yend)
						f47 = texture.yend;
					else if (f47 < 0.0F)
						f47 = 0.0F;
					int l6 = (int) (f30 * (f12 - f45));
					if (flag1)
						l6 = -l6;
					int i7 = (int) (f28 * (f46 - f40));
					int j7 = (int) (f28 * (f47 - f41));
					int i5 = (int) (f29 * (f48 - f42));
					int j5 = (int) (f29 * (f49 - f43));
					int k5 = (int) (f29 * (f50 - f44));
					if (Config.spanBasedHsr
							&& (l <= j2 && i1 >= l2 && f17 >= f45 && f17 >= f12 || j1 <= j2 && k1 >= l2 && f18 >= f45 && f18 >= f12)) {
						if (l2 != i3) {
							l4 += j3 * l6;
							k3 += j3 * i7;
							l3 += j3 * j7;
							i4 += j3 * i5;
							j4 += j3 * j5;
							k4 += j3 * k5;
						}
					} else {
						int k7 = l2 + l1;
						boolean flag7 = false;
						boolean flag8 = false;
						boolean flag9 = false;
						boolean flag10 = false;
						if (Config.texelFilter) {
							if (!flag && i7 < 0x30000 && i7 > 0xfffd0000 && j7 <= 0x30000 && j7 >= 0xfffd0000)
								flag = true;
						} else {
							flag = false;
						}
						if (flag) {
							int i6;
							if ((j2 & 1) != 0)
								i6 = 0x10000 + j6;
							else
								i6 = k6;
							if (Config.optiZ && (j > l2 || k < j2 || f45 >= f16 && f12 >= f16)) {
								for (int l13 = j2 + l1; l13 <= k7; l13++) {
									int l7 = ai[(k3 + i6 >> 18) + ((l3 + (i6 ^ 0x10000) >> 18) << byte0)];
									i6 ^= 0x30000;
									int j12 = (l7 >> 16 & 0xff) + (i4 >> 18);
									int l10 = (l7 >> 8 & 0xff) + (j4 >> 18);
									int j9 = (l7 & 0xff) + (k4 >> 18);
									if ((j12 & 0xffffff00) != 0)
										j12 = 255 >> (j12 >> 28 & 8);
									if ((l10 & 0xffffff00) != 0)
										l10 = 255 >> (l10 >> 28 & 8);
									if ((j9 & 0xffffff00) != 0)
										j9 = 255 >> (j9 >> 28 & 8);
									ai1[l13] = j9 | l10 << 8 | j12 << 16 | 0xff000000;
									ai2[l13] = l4;
									k3 += i7;
									l3 += j7;
									i4 += i5;
									j4 += j5;
									k4 += k5;
									l4 += l6;
								}

							} else if (flag1) {
								for (int i14 = j2 + l1; i14 <= k7; i14++) {
									if (ai2[i14] > l4) {
										int i8 = ai[(k3 + i6 >> 18) + ((l3 + (i6 ^ 0x10000) >> 18) << byte0)];
										i6 ^= 0x30000;
										int k12 = (i8 >> 16 & 0xff) + (i4 >> 18);
										int i11 = (i8 >> 8 & 0xff) + (j4 >> 18);
										int k9 = (i8 & 0xff) + (k4 >> 18);
										if ((k12 & 0xffffff00) != 0)
											k12 = 255 >> (k12 >> 28 & 8);
										if ((i11 & 0xffffff00) != 0)
											i11 = 255 >> (i11 >> 28 & 8);
										if ((k9 & 0xffffff00) != 0)
											k9 = 255 >> (k9 >> 28 & 8);
										ai1[i14] = k9 | i11 << 8 | k12 << 16 | 0xff000000;
										ai2[i14] = l4;
									}
									k3 += i7;
									l3 += j7;
									i4 += i5;
									j4 += j5;
									k4 += k5;
									l4 += l6;
								}

							} else {
								for (int j14 = j2 + l1; j14 <= k7; j14++) {
									if (ai2[j14] < l4) {
										int j8 = ai[(k3 + i6 >> 18) + ((l3 + (i6 ^ 0x10000) >> 18) << byte0)];
										i6 ^= 0x30000;
										int l12 = (j8 >> 16 & 0xff) + (i4 >> 18);
										int j11 = (j8 >> 8 & 0xff) + (j4 >> 18);
										int l9 = (j8 & 0xff) + (k4 >> 18);
										if ((l12 & 0xffffff00) != 0)
											l12 = 255 >> (l12 >> 28 & 8);
										if ((j11 & 0xffffff00) != 0)
											j11 = 255 >> (j11 >> 28 & 8);
										if ((l9 & 0xffffff00) != 0)
											l9 = 255 >> (l9 >> 28 & 8);
										ai1[j14] = l9 | j11 << 8 | l12 << 16 | 0xff000000;
										ai2[j14] = l4;
									}
									k3 += i7;
									l3 += j7;
									i4 += i5;
									j4 += j5;
									k4 += k5;
									l4 += l6;
								}

							}
						} else if (Config.optiZ && (j > l2 || k < j2 || f45 >= f16 && f12 >= f16)) {
							for (int k14 = j2 + l1; k14 <= k7; k14++) {
								int k8 = ai[(k3 >> 18) + ((l3 >> 18) << byte0)];
								int i13 = (k8 >> 16 & 0xff) + (i4 >> 18);
								int k11 = (k8 >> 8 & 0xff) + (j4 >> 18);
								int i10 = (k8 & 0xff) + (k4 >> 18);
								if ((i13 & 0xffffff00) != 0)
									i13 = 255 >> (byte) (i13 >> 28 & 8);
								if ((k11 & 0xffffff00) != 0)
									k11 = 255 >> (byte) (k11 >> 28 & 8);
								if ((i10 & 0xffffff00) != 0)
									i10 = 255 >> (byte) (i10 >> 28 & 8);
								ai1[k14] = i10 | k11 << 8 | i13 << 16 | 0xff000000;
								ai2[k14] = l4;
								k3 += i7;
								l3 += j7;
								i4 += i5;
								j4 += j5;
								k4 += k5;
								l4 += l6;
							}

						} else if (flag1) {
							for (int l14 = j2 + l1; l14 <= k7; l14++) {
								if (ai2[l14] > l4) {
									int l8 = ai[(k3 >> 18) + ((l3 >> 18) << byte0)];
									int j13 = (l8 >> 16 & 0xff) + (i4 >> 18);
									int l11 = (l8 >> 8 & 0xff) + (j4 >> 18);
									int j10 = (l8 & 0xff) + (k4 >> 18);
									if ((j13 & 0xffffff00) != 0)
										j13 = 255 >> (byte) (j13 >> 28 & 8);
									if ((l11 & 0xffffff00) != 0)
										l11 = 255 >> (byte) (l11 >> 28 & 8);
									if ((j10 & 0xffffff00) != 0)
										j10 = 255 >> (byte) (j10 >> 28 & 8);
									ai1[l14] = j10 | l11 << 8 | j13 << 16 | 0xff000000;
									ai2[l14] = l4;
								}
								k3 += i7;
								l3 += j7;
								i4 += i5;
								j4 += j5;
								k4 += k5;
								l4 += l6;
							}

						} else {
							for (int i15 = j2 + l1; i15 <= k7; i15++) {
								if (ai2[i15] < l4) {
									int i9 = ai[(k3 >> 18) + ((l3 >> 18) << byte0)];
									int k13 = (i9 >> 16 & 0xff) + (i4 >> 18);
									int i12 = (i9 >> 8 & 0xff) + (j4 >> 18);
									int k10 = (i9 & 0xff) + (k4 >> 18);
									if ((k13 & 0xffffff00) != 0)
										k13 = 255 >> (byte) (k13 >> 28 & 8);
									if ((i12 & 0xffffff00) != 0)
										i12 = 255 >> (byte) (i12 >> 28 & 8);
									if ((k10 & 0xffffff00) != 0)
										k10 = 255 >> (byte) (k10 >> 28 & 8);
									ai1[i15] = k10 | i12 << 8 | k13 << 16 | 0xff000000;
									ai2[i15] = l4;
								}
								k3 += i7;
								l3 += j7;
								i4 += i5;
								j4 += j5;
								k4 += k5;
								l4 += l6;
							}

						}
					}
					f40 = f46;
					f41 = f47;
					f42 = f48;
					f43 = f49;
					f44 = f50;
				} while (l2 < i3);
				boolean flag5 = false;
				if (k2 < l && i3 >= l || l == 0x5f5e0ff) {
					framebuffer.exXstart[i] = k2;
					if (f17 > f20)
						framebuffer.exZlow[i] = f20;
					if (framebuffer.exZlow[i] > f13)
						framebuffer.exZlow[i] = f13;
					flag5 = true;
				}
				if (i3 > i1 && f6 <= (float) i1 || i1 == -1) {
					framebuffer.exXend[i] = i3;
					if (framebuffer.exZlow[i] > f13)
						framebuffer.exZlow[i] = f13;
					if (framebuffer.exZlow[i] > f20)
						framebuffer.exZlow[i] = f20;
					flag5 = true;
				}
				if (!flag5) {
					if (k2 < j1 && i3 >= j1 || j1 == 0x5f5e0ff) {
						framebuffer.exXstart2[i] = k2;
						if (f18 > f20)
							framebuffer.exZlow2[i] = f20;
						if (framebuffer.exZlow2[i] > f13)
							framebuffer.exZlow2[i] = f13;
					}
					if (i3 > k1 && f6 <= (float) k1 || k1 == -1) {
						framebuffer.exXend2[i] = i3;
						if (framebuffer.exZlow2[i] > f13)
							framebuffer.exZlow2[i] = f13;
						if (framebuffer.exZlow2[i] > f20)
							framebuffer.exZlow2[i] = f20;
						boolean flag6 = true;
					}
				}
				if (f6 < (float) j)
					framebuffer.xstart[i] = k2;
				if (i3 > k)
					framebuffer.xend[i] = i3;
				if (f13 < f20)
					f13 = f20;
				if (f13 > f16)
					framebuffer.zhigh[i] = f13;
			}
		}
	}

	private void drawShadedZbufferedFilteredBumpmappedBlendedScanline(float f, float f1, float f2, float f3, float f4,
			float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, int i, float f14,
			float f15, FrameBuffer framebuffer, Texture texture, boolean flag, float f16, float f17, float f18, float f19,
			Texture texture1, Texture texture2) {
		int ai[] = texture.texels;
		int ai1[] = texture1.texels;
		int ai2[] = texture2.texels;
		int ai3[] = framebuffer.pixels;
		int ai4[] = framebuffer.zbuffer;
		int j = framebuffer.xstart[i];
		int k = framebuffer.xend[i];
		float f20 = framebuffer.zhigh[i];
		int l = framebuffer.exXstart[i];
		int i1 = framebuffer.exXend[i];
		float f21 = framebuffer.exZlow[i];
		int j1 = framebuffer.exXstart2[i];
		int k1 = framebuffer.exXend2[i];
		float f22 = framebuffer.exZlow2[i];
		if (f7 < f6) {
			float f23 = f6;
			f6 = f7;
			f7 = f23;
			f23 = f8;
			f8 = f9;
			f9 = f23;
			f23 = f10;
			f10 = f11;
			f11 = f23;
			f23 = f12;
			f12 = f13;
			f13 = f23;
			f23 = f16;
			f16 = f17;
			f17 = f23;
			f23 = f18;
			f18 = f19;
			f19 = f23;
			f23 = f;
			f = f1;
			f1 = f23;
			f23 = f2;
			f2 = f3;
			f3 = f23;
			f23 = f4;
			f4 = f5;
			f5 = f23;
		}
		f7--;
		if (f6 < f15 && f7 >= f14) {
			if (i1 <= k1) {
				if (i1 >= j1) {
					i1 = k1;
					framebuffer.exXend[i] = framebuffer.exXend2[i];
					if (j1 < l) {
						l = j1;
						framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					}
					if (f22 < f21) {
						f21 = f22;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
					framebuffer.exXstart2[i] = 0x5f5e0ff;
					j1 = 0x5f5e0ff;
					framebuffer.exXend2[i] = -1;
					k1 = -1;
					framebuffer.exZlow2[i] = 2.147484E+009F;
					f22 = 2.147484E+009F;
				}
			} else if (k1 >= l) {
				if (j1 < l) {
					l = j1;
					framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					if (f22 < f21) {
						f21 = f22;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
				}
				framebuffer.exXstart2[i] = 0x5f5e0ff;
				j1 = 0x5f5e0ff;
				framebuffer.exXend2[i] = -1;
				k1 = -1;
				framebuffer.exZlow2[i] = 2.147484E+009F;
				f22 = 2.147484E+009F;
			}
			if ((!Config.spanBasedHsr || (float) l > f6 || (float) i1 < f7 || f21 < f12 || f21 < f13)
					&& ((float) j1 > f6 || (float) k1 < f7 || f22 < f12 || f22 < f13)) {
				if (f12 > 1.0F)
					f12 = 1.0F;
				if (f13 > 1.0F)
					f13 = 1.0F;
				float f24 = f7 - f6;
				float f34 = f12;
				if (f24 < 1.0F) {
					f1 = f;
					f3 = f2;
					f5 = f4;
				}
				float f25;
				float f26;
				float f27;
				float f28;
				float f29;
				float f30;
				float f32;
				float f33;
				if (f24 != 0.0F) {
					float f35 = 1.0F / f24;
					f25 = (f9 - f8) * f35;
					f26 = (f11 - f10) * f35;
					f32 = (f17 - f16) * f35;
					f33 = (f19 - f18) * f35;
					f27 = (f13 - f12) * f35;
					f29 = (f1 - f) * f35;
					f30 = (f3 - f2) * f35;
					f28 = (f5 - f4) * f35;
				} else {
					f25 = 0.0F;
					f26 = 0.0F;
					f27 = 0.0F;
					f28 = 0.0F;
					f29 = 0.0F;
					f30 = 0.0F;
					f32 = 0.0F;
					f33 = 0.0F;
				}
				if (f7 >= f15)
					f7 = f15 - 1.0F;
				if (f6 < f14) {
					f6 = f14 - f6;
					f += f6 * f29;
					f2 += f6 * f30;
					f4 += f6 * f28;
					f8 += f6 * f25;
					f10 += f6 * f26;
					f16 += f6 * f32;
					f18 += f6 * f33;
					f12 += f6 * f27;
					f6 = f14;
				}
				int l1 = i * framebuffer.width;
				int i2 = (int) f6;
				int k2 = i2;
				int l2 = i2 - 1;
				int i3 = (int) f7;
				int j3 = 16;
				float f36 = 16384F;
				float f37 = 16384F;
				float f31;
				if (Config.zTrick)
					f31 = 1.342177E+008F;
				else
					f31 = 2.684355E+008F;
				float f38 = 16F * f25;
				float f39 = 16F * f26;
				float f40 = 16F * f27;
				float f41 = 16F * f29;
				float f42 = 16F * f30;
				float f43 = 16F * f28;
				float f44 = 16F * f32;
				float f45 = 16F * f33;
				float f46 = 1.0F / f12;
				float f48 = f8 * f46;
				float f49 = f10 * f46;
				float f50 = f16 * f46;
				float f51 = f18 * f46;
				float f52 = f * f46;
				float f53 = f2 * f46;
				float f54 = f4 * f46;
				byte byte0 = texture.shifter;
				byte byte1 = texture1.shifter;
				int i4 = (int) (f52 * 262144F);
				int j4 = (int) (f53 * 262144F);
				int k4 = (int) (f54 * 262144F);
				int l4 = 0;
				boolean flag1 = false;
				if (Config.zTrick) {
					l4 = (int) (f12 * 2.147484E+009F);
					if ((framebuffer.frameCount & 1L) == 1L) {
						l4 = -l4;
						flag1 = true;
					}
				} else {
					l4 = (int) (f12 * 4.294967E+009F + -2.147484E+009F);
				}
				if (f48 > texture.xend)
					f48 = texture.xend;
				else if (f48 < 0.0F)
					f48 = 0.0F;
				if (f49 > texture.yend)
					f49 = texture.yend;
				else if (f49 < 0.0F)
					f49 = 0.0F;
				int k3 = (int) (f48 * 262144F);
				int l3 = (int) (f49 * 262144F);
				if (f50 > texture1.xend)
					f50 = texture1.xend;
				else if (f50 < 0.0F)
					f50 = 0.0F;
				if (f51 > texture1.yend)
					f51 = texture1.yend;
				else if (f51 < 0.0F)
					f51 = 0.0F;
				int i5 = (int) (f50 * 262144F);
				int j5 = (int) (f51 * 262144F);
				boolean flag2 = false;
				boolean flag3 = false;
				boolean flag4 = false;
				int j6 = i & 1;
				int l6 = 1 - j6 << 17;
				int i7 = j6 << 17;
				do {
					int j2 = l2 + 1;
					l2 += j3;
					if (l2 > i3) {
						j3 -= l2 - i3;
						l2 = i3;
						f38 = (float) j3 * f25;
						f39 = (float) j3 * f26;
						f44 = (float) j3 * f32;
						f45 = (float) j3 * f33;
						f40 = (float) j3 * f27;
						f41 = (float) j3 * f29;
						f42 = (float) j3 * f30;
						f43 = (float) j3 * f28;
						if (j3 == 0)
							j3 = 1;
						f37 = 0x40000 / j3;
						f36 = f37;
						if (Config.zTrick)
							f31 = 2.147484E+009F / (float) j3;
						else
							f31 = 4.294967E+009F / (float) j3;
					}
					if (l2 == i3 && f7 < f15 - 1.0F) {
						f38 = f9 - f8;
						f39 = f11 - f10;
						f40 = f13 - f12;
						f44 = f17 - f16;
						f45 = f19 - f18;
					}
					float f55 = f12;
					f8 += f38;
					f10 += f39;
					f16 += f44;
					f18 += f45;
					f12 += f40;
					f += f41;
					f2 += f42;
					f4 += f43;
					float f47 = 1.0F / f12;
					float f56 = f8 * f47;
					float f57 = f10 * f47;
					float f58 = f16 * f47;
					float f59 = f18 * f47;
					float f60 = f * f47;
					float f61 = f2 * f47;
					float f62 = f4 * f47;
					if (f56 > texture.xend)
						f56 = texture.xend;
					else if (f56 < 0.0F)
						f56 = 0.0F;
					if (f57 > texture.yend)
						f57 = texture.yend;
					else if (f57 < 0.0F)
						f57 = 0.0F;
					if (f58 > texture1.xend)
						f58 = texture1.xend;
					else if (f58 < 0.0F)
						f58 = 0.0F;
					if (f59 > texture1.yend)
						f59 = texture1.yend;
					else if (f59 < 0.0F)
						f59 = 0.0F;
					int j7 = (int) (f31 * (f12 - f55));
					if (flag1)
						j7 = -j7;
					int k7 = (int) (f36 * (f56 - f48));
					int l7 = (int) (f36 * (f57 - f49));
					int i8 = (int) (f36 * (f58 - f50));
					int j8 = (int) (f36 * (f59 - f51));
					int k5 = (int) (f37 * (f60 - f52));
					int l5 = (int) (f37 * (f61 - f53));
					int i6 = (int) (f37 * (f62 - f54));
					if (Config.spanBasedHsr
							&& (l <= j2 && i1 >= l2 && f21 >= f55 && f21 >= f12 || j1 <= j2 && k1 >= l2 && f22 >= f55 && f22 >= f12)) {
						if (l2 != i3) {
							l4 += j3 * j7;
							k3 += j3 * k7;
							l3 += j3 * l7;
							i5 += j3 * i8;
							j5 += j3 * j8;
							i4 += j3 * k5;
							j4 += j3 * l5;
							k4 += j3 * i6;
						}
					} else {
						int k8 = l2 + l1;
						boolean flag7 = false;
						boolean flag8 = false;
						boolean flag9 = false;
						boolean flag10 = false;
						if (Config.texelFilter) {
							if (!flag && k7 < 0x30000 && k7 > 0xfffd0000 && l7 <= 0x30000 && l7 >= 0xfffd0000)
								flag = true;
							if (!flag && i8 < 0x30000 && i8 > 0xfffd0000 && j8 <= 0x30000 && j8 >= 0xfffd0000)
								flag = true;
						} else {
							flag = false;
						}
						if (flag) {
							int k6;
							if ((j2 & 1) != 0)
								k6 = 0x10000 + l6;
							else
								k6 = i7;
							if (Config.optiZ && (j > l2 || k < j2 || f55 >= f20 && f12 >= f20)) {
								for (int l14 = j2 + l1; l14 <= k8; l14++) {
									int j16 = (i5 + k6 >> 18) + ((j5 + (k6 ^ 0x10000) >> 18) << byte1);
									int l17 = ai1[j16];
									int j19 = ai2[j16];
									int l20 = ((k3 + k6 >> 18) - 128) + (l17 >> 8)
											+ (((l3 + (k6 ^ 0x10000) >> 18) - 128) + (l17 & 0xff) << byte0);
									k6 ^= 0x30000;
									if (l20 < 0)
										l20 = 0;
									else if (l20 >= texture.intSize)
										l20 = texture.intSize - 1;
									int l8 = ai[l20];
									int j22 = j19 >> 16 & 0xff;
									int l23 = j19 >> 8 & 0xff;
									int j25 = j19 & 0xff;
									int j13 = (l8 >> 16 & 0xff) + (i4 >> 18) + j22;
									int l11 = (l8 >> 8 & 0xff) + (j4 >> 18) + l23;
									int j10 = (l8 & 0xff) + (k4 >> 18) + j25;
									if ((j13 & 0xffffff00) != 0)
										j13 = 255 >> (j13 >> 28 & 8);
									if ((l11 & 0xffffff00) != 0)
										l11 = 255 >> (l11 >> 28 & 8);
									if ((j10 & 0xffffff00) != 0)
										j10 = 255 >> (j10 >> 28 & 8);
									ai3[l14] = j10 | l11 << 8 | j13 << 16 | 0xff000000;
									ai4[l14] = l4;
									k3 += k7;
									l3 += l7;
									i5 += i8;
									j5 += j8;
									i4 += k5;
									j4 += l5;
									k4 += i6;
									l4 += j7;
								}

							} else if (!flag1) {
								for (int i15 = j2 + l1; i15 <= k8; i15++) {
									if (ai4[i15] < l4) {
										int k16 = (i5 + k6 >> 18) + ((j5 + (k6 ^ 0x10000) >> 18) << byte1);
										int i18 = ai1[k16];
										int k19 = ai2[k16];
										int i21 = ((k3 + k6 >> 18) - 128) + (i18 >> 8)
												+ (((l3 + (k6 ^ 0x10000) >> 18) - 128) + (i18 & 0xff) << byte0);
										k6 ^= 0x30000;
										if (i21 < 0)
											i21 = 0;
										else if (i21 >= texture.intSize)
											i21 = texture.intSize - 1;
										int i9 = ai[i21];
										int k22 = k19 >> 16 & 0xff;
										int i24 = k19 >> 8 & 0xff;
										int k25 = k19 & 0xff;
										int k13 = (i9 >> 16 & 0xff) + (i4 >> 18) + k22;
										int i12 = (i9 >> 8 & 0xff) + (j4 >> 18) + i24;
										int k10 = (i9 & 0xff) + (k4 >> 18) + k25;
										if ((k13 & 0xffffff00) != 0)
											k13 = 255 >> (k13 >> 28 & 8);
										if ((i12 & 0xffffff00) != 0)
											i12 = 255 >> (i12 >> 28 & 8);
										if ((k10 & 0xffffff00) != 0)
											k10 = 255 >> (k10 >> 28 & 8);
										ai3[i15] = k10 | i12 << 8 | k13 << 16 | 0xff000000;
										ai4[i15] = l4;
									}
									k3 += k7;
									l3 += l7;
									i5 += i8;
									j5 += j8;
									i4 += k5;
									j4 += l5;
									k4 += i6;
									l4 += j7;
								}

							} else {
								for (int j15 = j2 + l1; j15 <= k8; j15++) {
									if (ai4[j15] > l4) {
										int l16 = (i5 + k6 >> 18) + ((j5 + (k6 ^ 0x10000) >> 18) << byte1);
										int j18 = ai1[l16];
										int l19 = ai2[l16];
										int j21 = ((k3 + k6 >> 18) - 128) + (j18 >> 8)
												+ (((l3 + (k6 ^ 0x10000) >> 18) - 128) + (j18 & 0xff) << byte0);
										k6 ^= 0x30000;
										if (j21 < 0)
											j21 = 0;
										else if (j21 >= texture.intSize)
											j21 = texture.intSize - 1;
										int j9 = ai[j21];
										int l22 = l19 >> 16 & 0xff;
										int j24 = l19 >> 8 & 0xff;
										int l25 = l19 & 0xff;
										int l13 = (j9 >> 16 & 0xff) + (i4 >> 18) + l22;
										int j12 = (j9 >> 8 & 0xff) + (j4 >> 18) + j24;
										int l10 = (j9 & 0xff) + (k4 >> 18) + l25;
										if ((l13 & 0xffffff00) != 0)
											l13 = 255 >> (l13 >> 28 & 8);
										if ((j12 & 0xffffff00) != 0)
											j12 = 255 >> (j12 >> 28 & 8);
										if ((l10 & 0xffffff00) != 0)
											l10 = 255 >> (l10 >> 28 & 8);
										ai3[j15] = l10 | j12 << 8 | l13 << 16 | 0xff000000;
										ai4[j15] = l4;
									}
									k3 += k7;
									l3 += l7;
									i5 += i8;
									j5 += j8;
									i4 += k5;
									j4 += l5;
									k4 += i6;
									l4 += j7;
								}

							}
						} else if (Config.optiZ && (j > l2 || k < j2 || f55 >= f20 && f12 >= f20)) {
							for (int k15 = j2 + l1; k15 <= k8; k15++) {
								int i17 = (i5 >> 18) + ((j5 >> 18) << byte1);
								int k18 = ai1[i17];
								int i20 = ai2[i17];
								int k21 = ((k3 >> 18) - 128) + (k18 >> 8) + (((l3 >> 18) - 128) + (k18 & 0xff) << byte0);
								if (k21 >= texture.intSize)
									k21 = texture.intSize - 1;
								else if (k21 < 0)
									k21 = 0;
								int k9 = ai[k21];
								int i23 = i20 >> 16 & 0xff;
								int k24 = i20 >> 8 & 0xff;
								int i26 = i20 & 0xff;
								int i14 = (k9 >> 16 & 0xff) + (i4 >> 18) + i23;
								int k12 = (k9 >> 8 & 0xff) + (j4 >> 18) + k24;
								int i11 = (k9 & 0xff) + (k4 >> 18) + i26;
								if ((i14 & 0xffffff00) != 0)
									i14 = 255 >> (byte) (i14 >> 28 & 8);
								if ((k12 & 0xffffff00) != 0)
									k12 = 255 >> (byte) (k12 >> 28 & 8);
								if ((i11 & 0xffffff00) != 0)
									i11 = 255 >> (byte) (i11 >> 28 & 8);
								ai3[k15] = i11 | k12 << 8 | i14 << 16 | 0xff000000;
								ai4[k15] = l4;
								k3 += k7;
								l3 += l7;
								i5 += i8;
								j5 += j8;
								i4 += k5;
								j4 += l5;
								k4 += i6;
								l4 += j7;
							}

						} else if (!flag1) {
							for (int l15 = j2 + l1; l15 <= k8; l15++) {
								if (ai4[l15] < l4) {
									int j17 = (i5 >> 18) + ((j5 >> 18) << byte1);
									int l18 = ai1[j17];
									int j20 = ai2[j17];
									int l21 = ((k3 >> 18) - 128) + (l18 >> 8) + (((l3 >> 18) - 128) + (l18 & 0xff) << byte0);
									if (l21 >= texture.intSize)
										l21 = texture.intSize - 1;
									else if (l21 < 0)
										l21 = 0;
									int l9 = ai[l21];
									int j23 = j20 >> 16 & 0xff;
									int l24 = j20 >> 8 & 0xff;
									int j26 = j20 & 0xff;
									int j14 = (l9 >> 16 & 0xff) + (i4 >> 18) + j23;
									int l12 = (l9 >> 8 & 0xff) + (j4 >> 18) + l24;
									int j11 = (l9 & 0xff) + (k4 >> 18) + j26;
									if ((j14 & 0xffffff00) != 0)
										j14 = 255 >> (byte) (j14 >> 28 & 8);
									if ((l12 & 0xffffff00) != 0)
										l12 = 255 >> (byte) (l12 >> 28 & 8);
									if ((j11 & 0xffffff00) != 0)
										j11 = 255 >> (byte) (j11 >> 28 & 8);
									ai3[l15] = j11 | l12 << 8 | j14 << 16 | 0xff000000;
									ai4[l15] = l4;
								}
								k3 += k7;
								l3 += l7;
								i5 += i8;
								j5 += j8;
								i4 += k5;
								j4 += l5;
								k4 += i6;
								l4 += j7;
							}

						} else {
							for (int i16 = j2 + l1; i16 <= k8; i16++) {
								if (ai4[i16] > l4) {
									int k17 = (i5 >> 18) + ((j5 >> 18) << byte1);
									int i19 = ai1[k17];
									int k20 = ai2[k17];
									int i22 = ((k3 >> 18) - 128) + (i19 >> 8) + (((l3 >> 18) - 128) + (i19 & 0xff) << byte0);
									if (i22 >= texture.intSize)
										i22 = texture.intSize - 1;
									else if (i22 < 0)
										i22 = 0;
									int i10 = ai[i22];
									int k23 = k20 >> 16 & 0xff;
									int i25 = k20 >> 8 & 0xff;
									int k26 = k20 & 0xff;
									int k14 = (i10 >> 16 & 0xff) + (i4 >> 18) + k23;
									int i13 = (i10 >> 8 & 0xff) + (j4 >> 18) + i25;
									int k11 = (i10 & 0xff) + (k4 >> 18) + k26;
									if ((k14 & 0xffffff00) != 0)
										k14 = 255 >> (byte) (k14 >> 28 & 8);
									if ((i13 & 0xffffff00) != 0)
										i13 = 255 >> (byte) (i13 >> 28 & 8);
									if ((k11 & 0xffffff00) != 0)
										k11 = 255 >> (byte) (k11 >> 28 & 8);
									ai3[i16] = k11 | i13 << 8 | k14 << 16 | 0xff000000;
									ai4[i16] = l4;
								}
								k3 += k7;
								l3 += l7;
								i5 += i8;
								j5 += j8;
								i4 += k5;
								j4 += l5;
								k4 += i6;
								l4 += j7;
							}

						}
					}
					f48 = f56;
					f49 = f57;
					f50 = f58;
					f51 = f59;
					f52 = f60;
					f53 = f61;
					f54 = f62;
				} while (l2 < i3);
				boolean flag5 = false;
				if (k2 < l && i3 >= l || l == 0x5f5e0ff) {
					framebuffer.exXstart[i] = k2;
					if (f21 > f34)
						framebuffer.exZlow[i] = f34;
					if (framebuffer.exZlow[i] > f13)
						framebuffer.exZlow[i] = f13;
					flag5 = true;
				}
				if (i3 > i1 && f6 <= (float) i1 || i1 == -1) {
					framebuffer.exXend[i] = i3;
					if (framebuffer.exZlow[i] > f13)
						framebuffer.exZlow[i] = f13;
					if (framebuffer.exZlow[i] > f34)
						framebuffer.exZlow[i] = f34;
					flag5 = true;
				}
				if (!flag5) {
					if (k2 < j1 && i3 >= j1 || j1 == 0x5f5e0ff) {
						framebuffer.exXstart2[i] = k2;
						if (f22 > f34)
							framebuffer.exZlow2[i] = f34;
						if (framebuffer.exZlow2[i] > f13)
							framebuffer.exZlow2[i] = f13;
					}
					if (i3 > k1 && f6 <= (float) k1 || k1 == -1) {
						framebuffer.exXend2[i] = i3;
						if (framebuffer.exZlow2[i] > f13)
							framebuffer.exZlow2[i] = f13;
						if (framebuffer.exZlow2[i] > f34)
							framebuffer.exZlow2[i] = f34;
						boolean flag6 = true;
					}
				}
				if (f6 < (float) j)
					framebuffer.xstart[i] = k2;
				if (i3 > k)
					framebuffer.xend[i] = i3;
				if (f13 < f34)
					f13 = f34;
				if (f13 > f20)
					framebuffer.zhigh[i] = f13;
			}
		}
	}

	private void drawShadedZbufferedFilteredBumpmappedScanline(float f, float f1, float f2, float f3, float f4, float f5,
			float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, int i, float f14, float f15,
			FrameBuffer framebuffer, Texture texture, boolean flag, float f16, float f17, float f18, float f19,
			Texture texture1) {
		int ai[] = texture.texels;
		int ai1[] = texture1.texels;
		int ai2[] = framebuffer.pixels;
		int ai3[] = framebuffer.zbuffer;
		int j = framebuffer.xstart[i];
		int k = framebuffer.xend[i];
		float f20 = framebuffer.zhigh[i];
		int l = framebuffer.exXstart[i];
		int i1 = framebuffer.exXend[i];
		float f21 = framebuffer.exZlow[i];
		int j1 = framebuffer.exXstart2[i];
		int k1 = framebuffer.exXend2[i];
		float f22 = framebuffer.exZlow2[i];
		if (f7 < f6) {
			float f23 = f6;
			f6 = f7;
			f7 = f23;
			f23 = f8;
			f8 = f9;
			f9 = f23;
			f23 = f10;
			f10 = f11;
			f11 = f23;
			f23 = f12;
			f12 = f13;
			f13 = f23;
			f23 = f16;
			f16 = f17;
			f17 = f23;
			f23 = f18;
			f18 = f19;
			f19 = f23;
			f23 = f;
			f = f1;
			f1 = f23;
			f23 = f2;
			f2 = f3;
			f3 = f23;
			f23 = f4;
			f4 = f5;
			f5 = f23;
		}
		f7--;
		if (f6 < f15 && f7 >= f14) {
			if (i1 <= k1) {
				if (i1 >= j1) {
					i1 = k1;
					framebuffer.exXend[i] = framebuffer.exXend2[i];
					if (j1 < l) {
						l = j1;
						framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					}
					if (f22 < f21) {
						f21 = f22;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
					framebuffer.exXstart2[i] = 0x5f5e0ff;
					j1 = 0x5f5e0ff;
					framebuffer.exXend2[i] = -1;
					k1 = -1;
					framebuffer.exZlow2[i] = 2.147484E+009F;
					f22 = 2.147484E+009F;
				}
			} else if (k1 >= l) {
				if (j1 < l) {
					l = j1;
					framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					if (f22 < f21) {
						f21 = f22;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
				}
				framebuffer.exXstart2[i] = 0x5f5e0ff;
				j1 = 0x5f5e0ff;
				framebuffer.exXend2[i] = -1;
				k1 = -1;
				framebuffer.exZlow2[i] = 2.147484E+009F;
				f22 = 2.147484E+009F;
			}
			if ((!Config.spanBasedHsr || (float) l > f6 || (float) i1 < f7 || f21 < f12 || f21 < f13)
					&& ((float) j1 > f6 || (float) k1 < f7 || f22 < f12 || f22 < f13)) {
				if (f12 > 1.0F)
					f12 = 1.0F;
				if (f13 > 1.0F)
					f13 = 1.0F;
				float f24 = f7 - f6;
				float f34 = f12;
				if (f24 < 1.0F) {
					f1 = f;
					f3 = f2;
					f5 = f4;
				}
				float f25;
				float f26;
				float f27;
				float f28;
				float f29;
				float f30;
				float f32;
				float f33;
				if (f24 != 0.0F) {
					float f35 = 1.0F / f24;
					f25 = (f9 - f8) * f35;
					f26 = (f11 - f10) * f35;
					f32 = (f17 - f16) * f35;
					f33 = (f19 - f18) * f35;
					f27 = (f13 - f12) * f35;
					f29 = (f1 - f) * f35;
					f30 = (f3 - f2) * f35;
					f28 = (f5 - f4) * f35;
				} else {
					f25 = 0.0F;
					f26 = 0.0F;
					f27 = 0.0F;
					f28 = 0.0F;
					f29 = 0.0F;
					f30 = 0.0F;
					f32 = 0.0F;
					f33 = 0.0F;
				}
				if (f7 >= f15)
					f7 = f15 - 1.0F;
				if (f6 < f14) {
					f6 = f14 - f6;
					f += f6 * f29;
					f2 += f6 * f30;
					f4 += f6 * f28;
					f8 += f6 * f25;
					f10 += f6 * f26;
					f16 += f6 * f32;
					f18 += f6 * f33;
					f12 += f6 * f27;
					f6 = f14;
				}
				int l1 = i * framebuffer.width;
				int i2 = (int) f6;
				int k2 = i2;
				int l2 = i2 - 1;
				int i3 = (int) f7;
				int j3 = 16;
				float f36 = 16384F;
				float f37 = 16384F;
				float f31;
				if (Config.zTrick)
					f31 = 1.342177E+008F;
				else
					f31 = 2.684355E+008F;
				float f38 = 16F * f25;
				float f39 = 16F * f26;
				float f40 = 16F * f27;
				float f41 = 16F * f29;
				float f42 = 16F * f30;
				float f43 = 16F * f28;
				float f44 = 16F * f32;
				float f45 = 16F * f33;
				float f46 = 1.0F / f12;
				float f48 = f8 * f46;
				float f49 = f10 * f46;
				float f50 = f16 * f46;
				float f51 = f18 * f46;
				float f52 = f * f46;
				float f53 = f2 * f46;
				float f54 = f4 * f46;
				byte byte0 = texture.shifter;
				byte byte1 = texture1.shifter;
				int i4 = (int) (f52 * 262144F);
				int j4 = (int) (f53 * 262144F);
				int k4 = (int) (f54 * 262144F);
				int l4 = 0;
				boolean flag1 = false;
				if (Config.zTrick) {
					l4 = (int) (f12 * 2.147484E+009F);
					if ((framebuffer.frameCount & 1L) == 1L) {
						l4 = -l4;
						flag1 = true;
					}
				} else {
					l4 = (int) (f12 * 4.294967E+009F + -2.147484E+009F);
				}
				if (f48 > texture.xend)
					f48 = texture.xend;
				else if (f48 < 0.0F)
					f48 = 0.0F;
				if (f49 > texture.yend)
					f49 = texture.yend;
				else if (f49 < 0.0F)
					f49 = 0.0F;
				int k3 = (int) (f48 * 262144F);
				int l3 = (int) (f49 * 262144F);
				if (f50 > texture1.xend)
					f50 = texture1.xend;
				else if (f50 < 0.0F)
					f50 = 0.0F;
				if (f51 > texture1.yend)
					f51 = texture1.yend;
				else if (f51 < 0.0F)
					f51 = 0.0F;
				int i5 = (int) (f50 * 262144F);
				int j5 = (int) (f51 * 262144F);
				boolean flag2 = false;
				boolean flag3 = false;
				boolean flag4 = false;
				int j6 = i & 1;
				int l6 = 1 - j6 << 17;
				int i7 = j6 << 17;
				do {
					int j2 = l2 + 1;
					l2 += j3;
					if (l2 > i3) {
						j3 -= l2 - i3;
						l2 = i3;
						f38 = (float) j3 * f25;
						f39 = (float) j3 * f26;
						f44 = (float) j3 * f32;
						f45 = (float) j3 * f33;
						f40 = (float) j3 * f27;
						f41 = (float) j3 * f29;
						f42 = (float) j3 * f30;
						f43 = (float) j3 * f28;
						if (j3 == 0)
							j3 = 1;
						f37 = 0x40000 / j3;
						f36 = f37;
						if (Config.zTrick)
							f31 = 2.147484E+009F / (float) j3;
						else
							f31 = 4.294967E+009F / (float) j3;
					}
					float f55 = f12;
					if (l2 == i3 && f7 < f15 - 1.0F) {
						f38 = f9 - f8;
						f39 = f11 - f10;
						f40 = f13 - f12;
						f44 = f17 - f16;
						f45 = f19 - f18;
					}
					f8 += f38;
					f10 += f39;
					f16 += f44;
					f18 += f45;
					f12 += f40;
					f += f41;
					f2 += f42;
					f4 += f43;
					float f47 = 1.0F / f12;
					float f56 = f8 * f47;
					float f57 = f10 * f47;
					float f58 = f16 * f47;
					float f59 = f18 * f47;
					float f60 = f * f47;
					float f61 = f2 * f47;
					float f62 = f4 * f47;
					if (f56 > texture.xend)
						f56 = texture.xend;
					else if (f56 < 0.0F)
						f56 = 0.0F;
					if (f57 > texture.yend)
						f57 = texture.yend;
					else if (f57 < 0.0F)
						f57 = 0.0F;
					if (f58 > texture1.xend)
						f58 = texture1.xend;
					else if (f58 < 0.0F)
						f58 = 0.0F;
					if (f59 > texture1.yend)
						f59 = texture1.yend;
					else if (f59 < 0.0F)
						f59 = 0.0F;
					int j7 = (int) (f31 * (f12 - f55));
					if (flag1)
						j7 = -j7;
					int k7 = (int) (f36 * (f56 - f48));
					int l7 = (int) (f36 * (f57 - f49));
					int i8 = (int) (f36 * (f58 - f50));
					int j8 = (int) (f36 * (f59 - f51));
					int k5 = (int) (f37 * (f60 - f52));
					int l5 = (int) (f37 * (f61 - f53));
					int i6 = (int) (f37 * (f62 - f54));
					if (Config.spanBasedHsr
							&& (l <= j2 && i1 >= l2 && f21 >= f55 && f21 >= f12 || j1 <= j2 && k1 >= l2 && f22 >= f55 && f22 >= f12)) {
						if (l2 != i3) {
							l4 += j3 * j7;
							k3 += j3 * k7;
							l3 += j3 * l7;
							i5 += j3 * i8;
							j5 += j3 * j8;
							i4 += j3 * k5;
							j4 += j3 * l5;
							k4 += j3 * i6;
						}
					} else {
						int k8 = l2 + l1;
						boolean flag7 = false;
						boolean flag8 = false;
						boolean flag9 = false;
						boolean flag10 = false;
						if (Config.texelFilter) {
							if (!flag && k7 < 0x30000 && k7 > 0xfffd0000 && l7 <= 0x30000 && l7 >= 0xfffd0000)
								flag = true;
							if (!flag && i8 < 0x30000 && i8 > 0xfffd0000 && j8 <= 0x30000 && j8 >= 0xfffd0000)
								flag = true;
						} else {
							flag = false;
						}
						if (flag) {
							int k6;
							if ((j2 & 1) != 0)
								k6 = 0x10000 + l6;
							else
								k6 = i7;
							if (Config.optiZ && (j > l2 || k < j2 || f55 >= f20 && f12 >= f20)) {
								for (int l14 = j2 + l1; l14 <= k8; l14++) {
									int j16 = ai1[(i5 + k6 >> 18) + ((j5 + (k6 ^ 0x10000) >> 18) << byte1)];
									int l17 = ((k3 + k6 >> 18) - 128) + (j16 >> 8)
											+ (((l3 + (k6 ^ 0x10000) >> 18) - 128) + (j16 & 0xff) << byte0);
									k6 ^= 0x30000;
									if (l17 < 0)
										l17 = 0;
									else if (l17 >= texture.intSize)
										l17 = texture.intSize - 1;
									int l8 = ai[l17];
									int j13 = (l8 >> 16 & 0xff) + (i4 >> 18);
									int l11 = (l8 >> 8 & 0xff) + (j4 >> 18);
									int j10 = (l8 & 0xff) + (k4 >> 18);
									if ((j13 & 0xffffff00) != 0)
										j13 = 255 >> (j13 >> 28 & 8);
									if ((l11 & 0xffffff00) != 0)
										l11 = 255 >> (l11 >> 28 & 8);
									if ((j10 & 0xffffff00) != 0)
										j10 = 255 >> (j10 >> 28 & 8);
									ai2[l14] = j10 | l11 << 8 | j13 << 16 | 0xff000000;
									ai3[l14] = l4;
									k3 += k7;
									l3 += l7;
									i5 += i8;
									j5 += j8;
									i4 += k5;
									j4 += l5;
									k4 += i6;
									l4 += j7;
								}

							} else if (!flag1) {
								for (int i15 = j2 + l1; i15 <= k8; i15++) {
									if (ai3[i15] < l4) {
										int k16 = ai1[(i5 + k6 >> 18) + ((j5 + (k6 ^ 0x10000) >> 18) << byte1)];
										int i18 = ((k3 + k6 >> 18) - 128) + (k16 >> 8)
												+ (((l3 + (k6 ^ 0x10000) >> 18) - 128) + (k16 & 0xff) << byte0);
										k6 ^= 0x30000;
										if (i18 < 0)
											i18 = 0;
										else if (i18 >= texture.intSize)
											i18 = texture.intSize - 1;
										int i9 = ai[i18];
										int k13 = (i9 >> 16 & 0xff) + (i4 >> 18);
										int i12 = (i9 >> 8 & 0xff) + (j4 >> 18);
										int k10 = (i9 & 0xff) + (k4 >> 18);
										if ((k13 & 0xffffff00) != 0)
											k13 = 255 >> (k13 >> 28 & 8);
										if ((i12 & 0xffffff00) != 0)
											i12 = 255 >> (i12 >> 28 & 8);
										if ((k10 & 0xffffff00) != 0)
											k10 = 255 >> (k10 >> 28 & 8);
										ai2[i15] = k10 | i12 << 8 | k13 << 16 | 0xff000000;
										ai3[i15] = l4;
									}
									k3 += k7;
									l3 += l7;
									i5 += i8;
									j5 += j8;
									i4 += k5;
									j4 += l5;
									k4 += i6;
									l4 += j7;
								}

							} else {
								for (int j15 = j2 + l1; j15 <= k8; j15++) {
									if (ai3[j15] > l4) {
										int l16 = ai1[(i5 + k6 >> 18) + ((j5 + (k6 ^ 0x10000) >> 18) << byte1)];
										int j18 = ((k3 + k6 >> 18) - 128) + (l16 >> 8)
												+ (((l3 + (k6 ^ 0x10000) >> 18) - 128) + (l16 & 0xff) << byte0);
										k6 ^= 0x30000;
										if (j18 < 0)
											j18 = 0;
										else if (j18 >= texture.intSize)
											j18 = texture.intSize - 1;
										int j9 = ai[j18];
										int l13 = (j9 >> 16 & 0xff) + (i4 >> 18);
										int j12 = (j9 >> 8 & 0xff) + (j4 >> 18);
										int l10 = (j9 & 0xff) + (k4 >> 18);
										if ((l13 & 0xffffff00) != 0)
											l13 = 255 >> (l13 >> 28 & 8);
										if ((j12 & 0xffffff00) != 0)
											j12 = 255 >> (j12 >> 28 & 8);
										if ((l10 & 0xffffff00) != 0)
											l10 = 255 >> (l10 >> 28 & 8);
										ai2[j15] = l10 | j12 << 8 | l13 << 16 | 0xff000000;
										ai3[j15] = l4;
									}
									k3 += k7;
									l3 += l7;
									i5 += i8;
									j5 += j8;
									i4 += k5;
									j4 += l5;
									k4 += i6;
									l4 += j7;
								}

							}
						} else if (Config.optiZ && (j > l2 || k < j2 || f55 >= f20 && f12 >= f20)) {
							for (int k15 = j2 + l1; k15 <= k8; k15++) {
								int i17 = ai1[(i5 >> 18) + ((j5 >> 18) << byte1)];
								int k18 = ((k3 >> 18) - 128) + (i17 >> 8) + (((l3 >> 18) - 128) + (i17 & 0xff) << byte0);
								if (k18 >= texture.intSize)
									k18 = texture.intSize - 1;
								else if (k18 < 0)
									k18 = 0;
								int k9 = ai[k18];
								int i14 = (k9 >> 16 & 0xff) + (i4 >> 18);
								int k12 = (k9 >> 8 & 0xff) + (j4 >> 18);
								int i11 = (k9 & 0xff) + (k4 >> 18);
								if ((i14 & 0xffffff00) != 0)
									i14 = 255 >> (byte) (i14 >> 28 & 8);
								if ((k12 & 0xffffff00) != 0)
									k12 = 255 >> (byte) (k12 >> 28 & 8);
								if ((i11 & 0xffffff00) != 0)
									i11 = 255 >> (byte) (i11 >> 28 & 8);
								ai2[k15] = i11 | k12 << 8 | i14 << 16 | 0xff000000;
								ai3[k15] = l4;
								k3 += k7;
								l3 += l7;
								i5 += i8;
								j5 += j8;
								i4 += k5;
								j4 += l5;
								k4 += i6;
								l4 += j7;
							}

						} else if (!flag1) {
							for (int l15 = j2 + l1; l15 <= k8; l15++) {
								if (framebuffer.zbuffer[l15] < l4) {
									int j17 = ai1[(i5 >> 18) + ((j5 >> 18) << byte1)];
									int l18 = ((k3 >> 18) - 128) + (j17 >> 8) + (((l3 >> 18) - 128) + (j17 & 0xff) << byte0);
									if (l18 >= texture.intSize)
										l18 = texture.intSize - 1;
									else if (l18 < 0)
										l18 = 0;
									int l9 = ai[l18];
									int j14 = (l9 >> 16 & 0xff) + (i4 >> 18);
									int l12 = (l9 >> 8 & 0xff) + (j4 >> 18);
									int j11 = (l9 & 0xff) + (k4 >> 18);
									if ((j14 & 0xffffff00) != 0)
										j14 = 255 >> (byte) (j14 >> 28 & 8);
									if ((l12 & 0xffffff00) != 0)
										l12 = 255 >> (byte) (l12 >> 28 & 8);
									if ((j11 & 0xffffff00) != 0)
										j11 = 255 >> (byte) (j11 >> 28 & 8);
									ai2[l15] = j11 | l12 << 8 | j14 << 16 | 0xff000000;
									ai3[l15] = l4;
								}
								k3 += k7;
								l3 += l7;
								i5 += i8;
								j5 += j8;
								i4 += k5;
								j4 += l5;
								k4 += i6;
								l4 += j7;
							}

						} else {
							for (int i16 = j2 + l1; i16 <= k8; i16++) {
								if (framebuffer.zbuffer[i16] > l4) {
									int k17 = ai1[(i5 >> 18) + ((j5 >> 18) << byte1)];
									int i19 = ((k3 >> 18) - 128) + (k17 >> 8) + (((l3 >> 18) - 128) + (k17 & 0xff) << byte0);
									if (i19 >= texture.intSize)
										i19 = texture.intSize - 1;
									else if (i19 < 0)
										i19 = 0;
									int i10 = ai[i19];
									int k14 = (i10 >> 16 & 0xff) + (i4 >> 18);
									int i13 = (i10 >> 8 & 0xff) + (j4 >> 18);
									int k11 = (i10 & 0xff) + (k4 >> 18);
									if ((k14 & 0xffffff00) != 0)
										k14 = 255 >> (byte) (k14 >> 28 & 8);
									if ((i13 & 0xffffff00) != 0)
										i13 = 255 >> (byte) (i13 >> 28 & 8);
									if ((k11 & 0xffffff00) != 0)
										k11 = 255 >> (byte) (k11 >> 28 & 8);
									ai2[i16] = k11 | i13 << 8 | k14 << 16 | 0xff000000;
									ai3[i16] = l4;
								}
								k3 += k7;
								l3 += l7;
								i5 += i8;
								j5 += j8;
								i4 += k5;
								j4 += l5;
								k4 += i6;
								l4 += j7;
							}

						}
					}
					f48 = f56;
					f49 = f57;
					f50 = f58;
					f51 = f59;
					f52 = f60;
					f53 = f61;
					f54 = f62;
				} while (l2 < i3);
				boolean flag5 = false;
				if (k2 < l && i3 >= l || l == 0x5f5e0ff) {
					framebuffer.exXstart[i] = k2;
					if (f21 > f34)
						framebuffer.exZlow[i] = f34;
					if (framebuffer.exZlow[i] > f13)
						framebuffer.exZlow[i] = f13;
					flag5 = true;
				}
				if (i3 > i1 && f6 <= (float) i1 || i1 == -1) {
					framebuffer.exXend[i] = i3;
					if (framebuffer.exZlow[i] > f13)
						framebuffer.exZlow[i] = f13;
					if (framebuffer.exZlow[i] > f34)
						framebuffer.exZlow[i] = f34;
					flag5 = true;
				}
				if (!flag5) {
					if (k2 < j1 && i3 >= j1 || j1 == 0x5f5e0ff) {
						framebuffer.exXstart2[i] = k2;
						if (f22 > f34)
							framebuffer.exZlow2[i] = f34;
						if (framebuffer.exZlow2[i] > f13)
							framebuffer.exZlow2[i] = f13;
					}
					if (i3 > k1 && f6 <= (float) k1 || k1 == -1) {
						framebuffer.exXend2[i] = i3;
						if (framebuffer.exZlow2[i] > f13)
							framebuffer.exZlow2[i] = f13;
						if (framebuffer.exZlow2[i] > f34)
							framebuffer.exZlow2[i] = f34;
						boolean flag6 = true;
					}
				}
				if (f6 < (float) j)
					framebuffer.xstart[i] = k2;
				if (i3 > k)
					framebuffer.xend[i] = i3;
				if (f13 < f34)
					f13 = f34;
				if (f13 > f20)
					framebuffer.zhigh[i] = f13;
			}
		}
	}

	private void drawShadedZbufferedFilteredTransparentScanline(float f, float f1, float f2, float f3, float f4,
			float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, int i, float f14,
			float f15, FrameBuffer framebuffer, Texture texture, boolean flag, int j) {
		int ai[] = texture.texels;
		int ai1[] = framebuffer.pixels;
		int ai2[] = framebuffer.zbuffer;
		boolean flag1 = false;
		int l = framebuffer.xstart[i];
		int i1 = framebuffer.xend[i];
		float f16 = framebuffer.zhigh[i];
		int j1 = framebuffer.exXstart[i];
		int k1 = framebuffer.exXend[i];
		float f17 = framebuffer.exZlow[i];
		int l1 = framebuffer.exXstart2[i];
		int i2 = framebuffer.exXend2[i];
		float f18 = framebuffer.exZlow2[i];
		if (f7 < f6) {
			float f19 = f6;
			f6 = f7;
			f7 = f19;
			f19 = f8;
			f8 = f9;
			f9 = f19;
			f19 = f10;
			f10 = f11;
			f11 = f19;
			f19 = f12;
			f12 = f13;
			f13 = f19;
			f19 = f;
			f = f1;
			f1 = f19;
			f19 = f2;
			f2 = f3;
			f3 = f19;
			f19 = f4;
			f4 = f5;
			f5 = f19;
		}
		f7--;
		if (f6 < f15 && f7 >= f14) {
			if (k1 <= i2) {
				if (k1 >= l1) {
					k1 = i2;
					framebuffer.exXend[i] = framebuffer.exXend2[i];
					if (l1 < j1) {
						j1 = l1;
						framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					}
					if (f18 < f17) {
						f17 = f18;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
					framebuffer.exXstart2[i] = 0x5f5e0ff;
					l1 = 0x5f5e0ff;
					framebuffer.exXend2[i] = -1;
					i2 = -1;
					framebuffer.exZlow2[i] = 2.147484E+009F;
					f18 = 2.147484E+009F;
				}
			} else if (i2 >= j1) {
				if (l1 < j1) {
					j1 = l1;
					framebuffer.exXstart[i] = framebuffer.exXstart2[i];
					if (f18 < f17) {
						f17 = f18;
						framebuffer.exZlow[i] = framebuffer.exZlow2[i];
					}
				}
				framebuffer.exXstart2[i] = 0x5f5e0ff;
				l1 = 0x5f5e0ff;
				framebuffer.exXend2[i] = -1;
				i2 = -1;
				framebuffer.exZlow2[i] = 2.147484E+009F;
				f18 = 2.147484E+009F;
			}
			if ((!Config.spanBasedHsr || (float) j1 > f6 || (float) k1 < f7 || f17 < f12 || f17 < f13)
					&& ((float) l1 > f6 || (float) i2 < f7 || f18 < f12 || f18 < f13)) {
				if (f12 > 1.0F)
					f12 = 1.0F;
				if (f13 > 1.0F)
					f13 = 1.0F;
				float f20 = f12;
				float f21 = f7 - f6;
				if (f21 < 1.0F) {
					f1 = f;
					f3 = f2;
					f5 = f4;
				}
				float f22;
				float f23;
				float f24;
				float f25;
				float f26;
				float f27;
				if (f21 != 0.0F) {
					float f29 = 1.0F / f21;
					f22 = (f9 - f8) * f29;
					f23 = (f11 - f10) * f29;
					f24 = (f13 - f12) * f29;
					f26 = (f1 - f) * f29;
					f27 = (f3 - f2) * f29;
					f25 = (f5 - f4) * f29;
				} else {
					f22 = 0.0F;
					f23 = 0.0F;
					f24 = 0.0F;
					f25 = 0.0F;
					f26 = 0.0F;
					f27 = 0.0F;
				}
				if (f7 >= f15)
					f7 = f15 - 1.0F;
				if (f6 < f14) {
					f6 = f14 - f6;
					f += f6 * f26;
					f2 += f6 * f27;
					f4 += f6 * f25;
					f8 += f6 * f22;
					f10 += f6 * f23;
					f12 += f6 * f24;
					f6 = f14;
				}
				int j2 = i * framebuffer.width;
				int k2 = (int) f6;
				int i3 = k2 - 1;
				int j3 = (int) f7;
				int k3 = 16;
				float f30 = 16384F;
				float f31 = 16384F;
				float f28;
				if (Config.zTrick)
					f28 = 1.342177E+008F;
				else
					f28 = 2.684355E+008F;
				float f32 = 16F * f22;
				float f33 = 16F * f23;
				float f34 = 16F * f24;
				float f35 = 16F * f26;
				float f36 = 16F * f27;
				float f37 = 16F * f25;
				float f38 = 1.0F / f12;
				float f40 = f8 * f38;
				float f41 = f10 * f38;
				float f42 = f * f38;
				float f43 = f2 * f38;
				float f44 = f4 * f38;
				byte byte0 = texture.shifter;
				int j4 = (int) (f42 * 262144F);
				int k4 = (int) (f43 * 262144F);
				int l4 = (int) (f44 * 262144F);
				int i5 = 0;
				boolean flag2 = false;
				if (Config.zTrick) {
					i5 = (int) (f12 * 2.147484E+009F);
					if ((framebuffer.frameCount & 1L) == 1L) {
						i5 = -i5;
						flag2 = true;
					}
				} else {
					i5 = (int) (f12 * 4.294967E+009F + -2.147484E+009F);
				}
				if (f40 > texture.xend)
					f40 = texture.xend;
				else if (f40 < 0.0F)
					f40 = 0.0F;
				if (f41 > texture.yend)
					f41 = texture.yend;
				else if (f41 < 0.0F)
					f41 = 0.0F;
				int l3 = (int) (f40 * 262144F);
				int i4 = (int) (f41 * 262144F);
				boolean flag3 = false;
				boolean flag4 = false;
				boolean flag5 = false;
				int i6 = i & 1;
				int k6 = 1 - i6 << 17;
				int l6 = i6 << 17;
				do {
					int l2 = i3 + 1;
					i3 += k3;
					if (i3 > j3) {
						k3 -= i3 - j3;
						i3 = j3;
						f32 = (float) k3 * f22;
						f33 = (float) k3 * f23;
						f34 = (float) k3 * f24;
						f35 = (float) k3 * f26;
						f36 = (float) k3 * f27;
						f37 = (float) k3 * f25;
						if (k3 == 0)
							k3 = 1;
						f31 = 0x40000 / k3;
						f30 = f31;
						if (Config.zTrick)
							f28 = 2.147484E+009F / (float) k3;
						else
							f28 = 4.294967E+009F / (float) k3;
					}
					float f45 = f12;
					if (i3 == j3 && f7 < f15 - 1.0F) {
						f32 = f9 - f8;
						f33 = f11 - f10;
						f34 = f13 - f12;
					}
					f8 += f32;
					f10 += f33;
					f12 += f34;
					f += f35;
					f2 += f36;
					f4 += f37;
					float f39 = 1.0F / f12;
					float f46 = f8 * f39;
					float f47 = f10 * f39;
					float f48 = f * f39;
					float f49 = f2 * f39;
					float f50 = f4 * f39;
					if (f46 > texture.xend)
						f46 = texture.xend;
					else if (f46 < 0.0F)
						f46 = 0.0F;
					if (f47 > texture.yend)
						f47 = texture.yend;
					else if (f47 < 0.0F)
						f47 = 0.0F;
					int i7 = (int) (f28 * (f12 - f45));
					if (flag2)
						i7 = -i7;
					int j7 = (int) (f30 * (f46 - f40));
					int k7 = (int) (f30 * (f47 - f41));
					int j5 = (int) (f31 * (f48 - f42));
					int k5 = (int) (f31 * (f49 - f43));
					int l5 = (int) (f31 * (f50 - f44));
					if (Config.spanBasedHsr
							&& (j1 <= l2 && k1 >= i3 && f17 >= f45 && f17 >= f12 || l1 <= l2 && i2 >= i3 && f18 >= f45 && f18 >= f12)) {
						if (i3 != j3) {
							i5 += k3 * i7;
							l3 += k3 * j7;
							i4 += k3 * k7;
							j4 += k3 * j5;
							k4 += k3 * k5;
							l4 += k3 * l5;
						}
					} else {
						int k = i3 + j2;
						boolean flag6 = false;
						boolean flag7 = false;
						boolean flag8 = false;
						boolean flag9 = false;
						if (Config.texelFilter) {
							if (!flag && j7 < 0x30000 && j7 > 0xfffd0000 && k7 <= 0x30000 && k7 >= 0xfffd0000)
								flag = true;
						} else {
							flag = false;
						}
						if (flag) {
							int j6;
							if ((l2 & 1) != 0)
								j6 = 0x10000 + k6;
							else
								j6 = l6;
							if (!flag2) {
								for (int l11 = l2 + j2; l11 <= k; l11++) {
									if (i5 > framebuffer.zbuffer[l11]) {
										int l7 = ai[(l3 + j6 >> 18) + ((i4 + (j6 ^ 0x10000) >> 18) << byte0)];
										j6 ^= 0x30000;
										if ((l7 & 0xf0f0f0) != 0) {
											int l12 = ai1[l11];
											int l10 = (l7 >> 15 & 0x1fe) + (j4 >> 17) + ((l12 >> 16 & 0xff) >> j) >> 1;
											int l9 = (l7 >> 7 & 0x1fe) + (k4 >> 17) + ((l12 >> 8 & 0xff) >> j) >> 1;
											int l8 = ((l7 & 0xff) + (l4 >> 18) << 1) + ((l12 & 0xff) >> j) >> 1;
											if ((l10 & 0xffffff00) != 0)
												l10 = 255 >> (l10 >> 28 & 8);
											if ((l9 & 0xffffff00) != 0)
												l9 = 255 >> (l9 >> 28 & 8);
											if ((l8 & 0xffffff00) != 0)
												l8 = 255 >> (l8 >> 28 & 8);
											ai1[l11] = l8 | l9 << 8 | l10 << 16 | 0xff000000;
										}
									}
									l3 += j7;
									i4 += k7;
									j4 += j5;
									k4 += k5;
									l4 += l5;
									i5 += i7;
								}

							} else {
								for (int i12 = l2 + j2; i12 <= k; i12++) {
									if (i5 < framebuffer.zbuffer[i12]) {
										int i8 = ai[(l3 + j6 >> 18) + ((i4 + (j6 ^ 0x10000) >> 18) << byte0)];
										j6 ^= 0x30000;
										if ((i8 & 0xf0f0f0) != 0) {
											int i13 = ai1[i12];
											int i11 = (i8 >> 15 & 0x1fe) + (j4 >> 17) + ((i13 >> 16 & 0xff) >> j) >> 1;
											int i10 = (i8 >> 7 & 0x1fe) + (k4 >> 17) + ((i13 >> 8 & 0xff) >> j) >> 1;
											int i9 = ((i8 & 0xff) + (l4 >> 18) << 1) + ((i13 & 0xff) >> j) >> 1;
											if ((i11 & 0xffffff00) != 0)
												i11 = 255 >> (i11 >> 28 & 8);
											if ((i10 & 0xffffff00) != 0)
												i10 = 255 >> (i10 >> 28 & 8);
											if ((i9 & 0xffffff00) != 0)
												i9 = 255 >> (i9 >> 28 & 8);
											ai1[i12] = i9 | i10 << 8 | i11 << 16 | 0xff000000;
										}
									}
									l3 += j7;
									i4 += k7;
									j4 += j5;
									k4 += k5;
									l4 += l5;
									i5 += i7;
								}

							}
						} else if (!flag2) {
							for (int j12 = l2 + j2; j12 <= k; j12++) {
								if (ai2[j12] < i5) {
									int j8 = ai[(l3 >> 18) + ((i4 >> 18) << byte0)];
									if ((j8 & 0xf0f0f0) != 0) {
										int j13 = ai1[j12];
										int j11 = (j8 >> 15 & 0x1fe) + (j4 >> 17) + ((j13 >> 16 & 0xff) >> j) >> 1;
										int j10 = (j8 >> 7 & 0x1fe) + (k4 >> 17) + ((j13 >> 8 & 0xff) >> j) >> 1;
										int j9 = ((j8 & 0xff) + (l4 >> 18) << 1) + ((j13 & 0xff) >> j) >> 1;
										if ((j11 & 0xffffff00) != 0)
											j11 = 255 >> (byte) (j11 >> 28 & 8);
										if ((j10 & 0xffffff00) != 0)
											j10 = 255 >> (byte) (j10 >> 28 & 8);
										if ((j9 & 0xffffff00) != 0)
											j9 = 255 >> (byte) (j9 >> 28 & 8);
										ai1[j12] = j9 | j10 << 8 | j11 << 16 | 0xff000000;
									}
								}
								l3 += j7;
								i4 += k7;
								j4 += j5;
								k4 += k5;
								l4 += l5;
								i5 += i7;
							}

						} else {
							for (int k12 = l2 + j2; k12 <= k; k12++) {
								if (ai2[k12] > i5) {
									int k8 = ai[(l3 >> 18) + ((i4 >> 18) << byte0)];
									if ((k8 & 0xf0f0f0) != 0) {
										int k13 = ai1[k12];
										int k11 = (k8 >> 15 & 0x1fe) + (j4 >> 17) + ((k13 >> 16 & 0xff) >> j) >> 1;
										int k10 = (k8 >> 7 & 0x1fe) + (k4 >> 17) + ((k13 >> 8 & 0xff) >> j) >> 1;
										int k9 = ((k8 & 0xff) + (l4 >> 18) << 1) + ((k13 & 0xff) >> j) >> 1;
										if ((k11 & 0xffffff00) != 0)
											k11 = 255 >> (byte) (k11 >> 28 & 8);
										if ((k10 & 0xffffff00) != 0)
											k10 = 255 >> (byte) (k10 >> 28 & 8);
										if ((k9 & 0xffffff00) != 0)
											k9 = 255 >> (byte) (k9 >> 28 & 8);
										ai1[k12] = k9 | k10 << 8 | k11 << 16 | 0xff000000;
									}
								}
								l3 += j7;
								i4 += k7;
								j4 += j5;
								k4 += k5;
								l4 += l5;
								i5 += i7;
							}

						}
					}
					f40 = f46;
					f41 = f47;
					f42 = f48;
					f43 = f49;
					f44 = f50;
				} while (i3 < j3);
				if (f6 < (float) l)
					framebuffer.xstart[i] = (int) f6;
				if (j3 > i1)
					framebuffer.xend[i] = j3;
				if (f13 < f20)
					f13 = f20;
				if (f13 > f16)
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
	private static final int XORROR = 0x30000;
	private final int p[] = new int[3];
	private final TextureManager texMan = TextureManager.getInstance();
	private IPaintListener listener;
	private boolean listenerActive;
}
