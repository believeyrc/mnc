// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.threed.jpct;

import java.awt.Color;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Map;
import java.util.Vector;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL11;

// Referenced classes of package com.threed.jpct:
//            GLBase, World, FrameBuffer, IPostProcessor,
//            CompiledInstance, IRenderer, GLHelper, Logger,
//            Config, IntegerC, IPaintListener, Camera,
//            VisList, Texture, Object3D, Mesh,
//            Vectors, TextureManager

public final class GLRenderer extends GLBase implements IRenderer {

	GLRenderer() {
	}

	public void init(int i, int j, int k, int l, int i1) {
		super.init = false;
		init(GLHelper.init(i, j, k, l, i1), i, j);
	}

	public void dispose() {
		super.dispose();
		GLHelper.dispose();
		Logger.log("OpenGL renderer disposed", 2);
	}

	public void execute(int i, Object aobj[]) {
		if (super.init) {
			if (i == 4 || i == 7)
				enableBlitting(aobj);
			else
				disableBlitting();
			switch (i) {
			case 14: // '\016'
			case 19: // '\023'
			case 20: // '\024'
			case 21: // '\025'
			case 22: // '\026'
			default:
				break;

			case 0: // '\0'
				super.myWorld = (World) aobj[0];
				FrameBuffer framebuffer = (FrameBuffer) aobj[1];
				setFrustum(super.myWorld, framebuffer);
				setLightsAndFog(super.myWorld);
				unloadTextures();
				break;

			case 1: // '\001'
				endState();
				if (!renderToTarget()) {
					GLHelper.swap();
					removeLists();
				}
				break;

			case 2: // '\002'
				clear(aobj);
				break;

			case 3: // '\003'
				Logger.log("State changes: " + super.stateChanges, 2);
				super.stateChanges = 0;
				break;

			case 4: // '\004'
				blit(aobj);
				break;

			case 5: // '\005'
				GL11.glFlush();
				break;

			case 6: // '\006'
				grabScreen(aobj);
				break;

			case 7: // '\007'
				prepareForBlitting(aobj);
				aobj[0] = super.blitBuffer;
				blit(aobj);
				break;

			case 8: // '\b'
				endState();
				break;

			case 9: // '\t'
				setBufferViewport(aobj);
				break;

			case 10: // '\n'
				Boolean boolean1 = Config.booleanValueOf(supportsExtension((String) aobj[0]));
				aobj[0] = boolean1;
				break;

			case 26: // '\032'
				float af[] = (float[]) aobj[1];
				Integer integer2 = (Integer) aobj[0];
				addClippingPlane(integer2.intValue(), af);
				break;

			case 27: // '\033'
				Integer integer = (Integer) aobj[0];
				removeClippingPlane(integer.intValue());
				break;

			case 25: // '\031'
				Integer integer1 = IntegerC.valueOf(getTextureSize());
				aobj[0] = integer1;
				break;

			case 11: // '\013'
				aobj[0] = null;
				break;

			case 12: // '\f'
				if (super.listener != null && super.listenerActive)
					super.listener.startPainting();
				break;

			case 13: // '\r'
				resetTextureStates();
				if (super.listener != null && super.listenerActive)
					super.listener.finishedPainting();
				break;

			case 15: // '\017'
				clearZBufferOnly();
				break;

			case 16: // '\020'
				doPostProcessing(aobj);
				break;

			case 17: // '\021'
				IPostProcessor ipostprocessor = (IPostProcessor) aobj[1];
				ipostprocessor.dispose();
				break;

			case 18: // '\022'
				setRenderTarget(aobj);
				break;

			case 23: // '\027'
				reInit();
				break;

			case 24: // '\030'
				boolean flag = ((Boolean) aobj[0]).booleanValue();
				super.listenerActive = flag;
				break;
			}
		}
	}

	private void blit(Object aobj[]) {
		if (super.myWorld != null) {
			Camera camera = super.myWorld.getCamera();
			blit(aobj, camera.scaleX, camera.scaleY);
		}
	}

	private void setFrustum(World world, FrameBuffer framebuffer) {
		Camera camera = world.getCamera();
		float f = camera.getFOV();
		float f1 = 0.0F;
		if (f != super.lastFOV || Config.farPlane != super.lastFarPlane || !Config.glIgnoreNearPlane
				&& Config.nearPlane != super.lastNearPlane || Config.autoMaintainAspectRatio != super.lastFOVMode) {
			GL11.glMatrixMode(5889);
			GL11.glLoadIdentity();
			float f2;
			if (Config.autoMaintainAspectRatio)
				f2 = f * ((float) framebuffer.getOutputHeight() / (float) framebuffer.getOutputWidth());
			else
				f2 = f;
			if (camera.getYFOV() != -1F)
				f2 = camera.getYFOV();
			float f3 = Config.farPlane;
			float f4 = 1.0F;
			if (!Config.glIgnoreNearPlane)
				f4 = Config.nearPlane;
			float f5 = f2 * 0.5F;
			float f6 = -f2 * 0.5F;
			float f7 = -f * 0.5F;
			float f8 = f * 0.5F;
			GL11.glFrustum(f7, f8, f6, f5, f4, f3);
			super.lastFOV = f + 100F * f2;
			super.lastFarPlane = Config.farPlane;
			super.lastNearPlane = Config.nearPlane;
			super.lastFOVMode = Config.autoMaintainAspectRatio;
		}
	}

	public void drawStrip(VisList vislist, int i, int j, FrameBuffer framebuffer, World world) {
		if (super.init) {
			endState();
			Object3D object3d = vislist.vorg[i];
			int k = vislist.vnum[i];
			int l = vislist.stageCnt[i];
			if (!vislist.splitted)
				l = 0;
			Texture texture = setTextures(object3d, k, l, framebuffer, world);
			disableBlitting();
			int i1 = texture.width;
			int j1 = texture.height;
			boolean flag = texture.projector == null;
			float f17 = 1.0F / (float) i1;
			float f18 = 1.0F / (float) j1;
			Vectors vectors = object3d.objVectors;
			Mesh mesh = object3d.objMesh;
			int ai[][] = mesh.points;
			float af[] = vectors.nuOrg;
			float af1[] = vectors.nvOrg;
			float af2[] = vectors.srOrg;
			float af3[] = vectors.sgOrg;
			float af4[] = vectors.sbOrg;
			int k1 = ai[vislist.vnumOrg[i]][0];
			int l1 = ai[vislist.vnumOrg[i]][1];
			int j2 = ai[vislist.vnumOrg[i]][2];
			float f19 = 1.0F;
			float f20 = 1.0F;
			float f21 = 1.0F;
			boolean flag1 = l < 1 && object3d.isTrans && (!object3d.isBumpmapped || !object3d.isEnvmapped);
			boolean flag2 = object3d.usesMultiTexturing && l == 0;
			if (flag2 && object3d.multiTex[0][k] == -1)
				flag2 = false;
			boolean flag3 = object3d.isEnvmapped && !object3d.isBlended && l <= 0;
			boolean flag4 = flag3 && (!Config.glForceEnvMapToSecondStage || !flag2);
			flag3 = flag3 && Config.glForceEnvMapToSecondStage;
			int k2 = super.currentRGBScaling;
			float f8;
			float f9;
			float f10;
			float f11;
			float f12;
			float f13;
			float f14;
			float f15;
			float f16;
			if (l > 0) {
				int l2 = (l & 0xffff) - 1;
				int j3 = object3d.multiMode[l2][k];
				GL11.glEnable(3042);
				GL11.glBlendFunc(GLBase.blendSrcMap[j3], GLBase.blendDstMap[j3]);
				if (k2 != 1)
					setRGBScaling(1);
				GL11.glDepthMask(false);
				GL11.glDepthFunc(514);
				f8 = 1.0F;
				f11 = 1.0F;
				f14 = 1.0F;
				f9 = 1.0F;
				f12 = 1.0F;
				f15 = 1.0F;
				f10 = 1.0F;
				f13 = 1.0F;
				f16 = 1.0F;
				af = vectors.uMul[l2];
				af1 = vectors.vMul[l2];
			} else {
				f8 = af2[k1] * GLBase.COLOR_INV;
				f9 = af3[k1] * GLBase.COLOR_INV;
				f10 = af4[k1] * GLBase.COLOR_INV;
				f11 = af2[l1] * GLBase.COLOR_INV;
				f12 = af3[l1] * GLBase.COLOR_INV;
				f13 = af4[l1] * GLBase.COLOR_INV;
				f14 = af2[j2] * GLBase.COLOR_INV;
				f15 = af3[j2] * GLBase.COLOR_INV;
				f16 = af4[j2] * GLBase.COLOR_INV;
			}
			if (flag1) {
				setBlendingMode(object3d.transMode);
				if (l == 0)
					setDepthBuffer();
				if (vectors.alpha == null) {
					f19 = Config.glTransparencyOffset + (float) object3d.transValue * Config.glTransparencyMul;
					if (f19 > 1.0F)
						f19 = 1.0F;
					f20 = f19;
					f21 = f19;
				} else {
					f19 = vectors.alpha[k1];
					f20 = vectors.alpha[l1];
					f21 = vectors.alpha[j2];
				}
			}
			GL11.glBegin(5);
			int i3 = mesh.coords[k1];
			int k3 = mesh.coords[l1];
			int i4 = mesh.coords[j2];
			float af5[] = vectors.xTr;
			float af6[] = vectors.yTr;
			float af7[] = vectors.zTr;
			float f22 = af5[i3];
			float f23 = af6[i3];
			float f24 = af7[i3];
			float f25 = af5[k3];
			float f26 = af6[k3];
			float f27 = af7[k3];
			float f28 = af5[i4];
			float f29 = af6[i4];
			float f30 = af7[i4];
			int j4 = object3d.maxStagesUsed;
			if (j4 > super.minDriverAndConfig)
				j4 = super.minDriverAndConfig;
			if (flag) {
				float f;
				float f4;
				if (flag4) {
					f = vectors.eu[k1] * f17;
					f4 = vectors.ev[k1] * f18;
				} else {
					f = af[k1];
					f4 = af1[k1];
				}
				GL11.glTexCoord2f(f, f4);
			}
			if (flag2) {
				for (int k4 = 0; k4 < j4 - 1; k4++) {
					int k5 = object3d.multiTex[k4][k];
					if (k5 == -1 || super.texMan.textures[k5].projector != null)
						continue;
					if (flag3)
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[k4 + 1], vectors.eu[k1] * f17, vectors.ev[k1] * f18);
					else
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[k4 + 1], vectors.uMul[k4][k1], vectors.vMul[k4][k1]);
				}

			}
			GL11.glColor4f(f8, f9, f10, f19);
			GL11.glVertex3f(f22, -f23, -f24);
			if (flag) {
				float f1;
				float f5;
				if (flag4) {
					f1 = vectors.eu[l1] * f17;
					f5 = vectors.ev[l1] * f18;
				} else {
					f1 = af[l1];
					f5 = af1[l1];
				}
				GL11.glTexCoord2f(f1, f5);
			}
			if (flag2) {
				for (int l4 = 0; l4 < j4 - 1; l4++) {
					int l5 = object3d.multiTex[l4][k];
					if (l5 == -1 || super.texMan.textures[l5].projector != null)
						continue;
					if (flag3)
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[l4 + 1], vectors.eu[l1] * f17, vectors.ev[l1] * f18);
					else
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[l4 + 1], vectors.uMul[l4][l1], vectors.vMul[l4][l1]);
				}

			}
			GL11.glColor4f(f11, f12, f13, f20);
			GL11.glVertex3f(f25, -f26, -f27);
			if (flag) {
				float f2;
				float f6;
				if (flag4) {
					f2 = vectors.eu[j2] * f17;
					f6 = vectors.ev[j2] * f18;
				} else {
					f2 = af[j2];
					f6 = af1[j2];
				}
				GL11.glTexCoord2f(f2, f6);
			}
			if (flag2) {
				for (int i5 = 0; i5 < j4 - 1; i5++) {
					int i6 = object3d.multiTex[i5][k];
					if (i6 == -1 || super.texMan.textures[i6].projector != null)
						continue;
					if (flag3)
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[i5 + 1], vectors.eu[j2] * f17, vectors.ev[j2] * f18);
					else
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[i5 + 1], vectors.uMul[i5][j2], vectors.vMul[i5][j2]);
				}

			}
			GL11.glColor4f(f14, f15, f16, f21);
			GL11.glVertex3f(f28, -f29, -f30);
			i++;
			j += i;
			f8 = 1.0F;
			f9 = 1.0F;
			f10 = 1.0F;
			for (int j5 = i; j5 < j; j5++) {
				int i2 = ai[vislist.vnumOrg[j5]][2];
				if (l <= 0) {
					f8 = af2[i2] * GLBase.COLOR_INV;
					f9 = af3[i2] * GLBase.COLOR_INV;
					f10 = af4[i2] * GLBase.COLOR_INV;
				}
				if (flag) {
					float f3;
					float f7;
					if (flag4) {
						f3 = vectors.eu[i2] * f17;
						f7 = vectors.ev[i2] * f18;
					} else {
						f3 = af[i2];
						f7 = af1[i2];
					}
					GL11.glTexCoord2f(f3, f7);
				}
				int l3 = mesh.coords[i2];
				if (flag2) {
					for (int j6 = 0; j6 < j4 - 1; j6++) {
						int k6 = object3d.multiTex[j6][k];
						if (k6 == -1 || super.texMan.textures[k6].projector != null)
							continue;
						if (flag3)
							ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[j6 + 1], vectors.eu[i2] * f17, vectors.ev[i2] * f18);
						else
							ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[j6 + 1], vectors.uMul[j6][i2], vectors.vMul[j6][i2]);
					}

				}
				if (vectors.alpha != null)
					f19 = vectors.alpha[i2];
				GL11.glColor4f(f8, f9, f10, f19);
				GL11.glVertex3f(af5[l3], -af6[l3], -af7[l3]);
			}

			GL11.glEnd();
			if (flag1 || l > 0) {
				unsetBlendingMode();
				GL11.glDepthMask(true);
				if (l > 0) {
					GL11.glDepthFunc(515);
					if (k2 != 1)
						setRGBScaling(k2);
				}
			}
		}
	}

	public void drawPolygon(VisList vislist, int i, FrameBuffer framebuffer, World world) {
		if (super.init) {
			Object3D object3d = vislist.vorg[i];
			int j = vislist.vnum[i];
			Vectors vectors = object3d.objVectors;
			Mesh mesh = object3d.objMesh;
			int ai[][] = mesh.points;
			int k = ai[vislist.vnumOrg[i]][0];
			int l = ai[vislist.vnumOrg[i]][1];
			int i1 = ai[vislist.vnumOrg[i]][2];
			int j1 = mesh.coords[k];
			int k1 = mesh.coords[l];
			int l1 = mesh.coords[i1];
			float af[] = vectors.xTr;
			float af1[] = vectors.yTr;
			float af2[] = vectors.zTr;
			int i2 = vislist.stageCnt[i];
			if (!vislist.splitted)
				i2 = 0;
			Texture texture = setTextures(object3d, j, i2, framebuffer, world);
			disableBlitting();
			boolean flag = texture.projector == null;
			int j2 = texture.width;
			int k2 = texture.height;
			float f15 = 1.0F / (float) j2;
			float f16 = 1.0F / (float) k2;
			float f17 = 1.0F;
			float f18 = 1.0F;
			float f19 = 1.0F;
			float af3[] = vectors.nuOrg;
			float af4[] = vectors.nvOrg;
			boolean flag1 = (i2 < 1) & object3d.isTrans & (!object3d.isBumpmapped || !object3d.isEnvmapped);
			boolean flag2 = object3d.usesMultiTexturing & (i2 == 0);
			if (flag2 && object3d.multiTex[0][j] == -1)
				flag2 = false;
			int l2 = super.currentRGBScaling;
			float f6;
			float f7;
			float f8;
			float f9;
			float f10;
			float f11;
			float f12;
			float f13;
			float f14;
			if (i2 > 0) {
				int i3 = (i2 & 0xffff) - 1;
				int k3 = object3d.multiMode[i3][j];
				endState();
				GL11.glEnable(3042);
				GL11.glBlendFunc(GLBase.blendSrcMap[k3], GLBase.blendDstMap[k3]);
				if (l2 != 1)
					setRGBScaling(1);
				GL11.glDepthMask(false);
				GL11.glDepthFunc(514);
				f6 = 1.0F;
				f9 = 1.0F;
				f12 = 1.0F;
				f7 = 1.0F;
				f10 = 1.0F;
				f13 = 1.0F;
				f8 = 1.0F;
				f11 = 1.0F;
				f14 = 1.0F;
				af3 = vectors.uMul[i3];
				af4 = vectors.vMul[i3];
			} else {
				f6 = vectors.srOrg[k] * GLBase.COLOR_INV;
				f7 = vectors.sgOrg[k] * GLBase.COLOR_INV;
				f8 = vectors.sbOrg[k] * GLBase.COLOR_INV;
				f9 = vectors.srOrg[l] * GLBase.COLOR_INV;
				f10 = vectors.sgOrg[l] * GLBase.COLOR_INV;
				f11 = vectors.sbOrg[l] * GLBase.COLOR_INV;
				f12 = vectors.srOrg[i1] * GLBase.COLOR_INV;
				f13 = vectors.sgOrg[i1] * GLBase.COLOR_INV;
				f14 = vectors.sbOrg[i1] * GLBase.COLOR_INV;
			}
			if (flag1) {
				endState();
				setBlendingMode(object3d.transMode);
				if (i2 == 0)
					setDepthBuffer();
				if (vectors.alpha == null) {
					f17 = Config.glTransparencyOffset + (float) object3d.transValue * Config.glTransparencyMul;
					if (f17 > 1.0F)
						f17 = 1.0F;
					f18 = f17;
					f19 = f17;
				} else {
					f17 = vectors.alpha[k];
					f18 = vectors.alpha[l];
					f19 = vectors.alpha[i1];
				}
			}
			int j3 = object3d.maxStagesUsed;
			if (j3 > super.minDriverAndConfig)
				j3 = super.minDriverAndConfig;
			beginState(4);
			boolean flag3 = object3d.isEnvmapped && !object3d.isBlended && i2 <= 0;
			boolean flag4 = flag3 && (!Config.glForceEnvMapToSecondStage || !flag2);
			flag3 = flag3 && Config.glForceEnvMapToSecondStage;
			if (flag) {
				float f;
				float f3;
				if (flag4) {
					f = vectors.eu[k] * f15;
					f3 = vectors.ev[k] * f16;
				} else {
					f = af3[k];
					f3 = af4[k];
				}
				GL11.glTexCoord2f(f, f3);
			}
			if (flag2) {
				for (int l3 = 0; l3 < j3 - 1; l3++) {
					int k4 = object3d.multiTex[l3][j];
					if (k4 == -1 || super.texMan.textures[k4].projector != null)
						continue;
					if (flag3)
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[l3 + 1], vectors.eu[k] * f15, vectors.ev[k] * f16);
					else
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[l3 + 1], vectors.uMul[l3][k], vectors.vMul[l3][k]);
				}

			}
			GL11.glColor4f(f6, f7, f8, f17);
			GL11.glVertex3f(af[j1], -af1[j1], -af2[j1]);
			if (flag) {
				float f1;
				float f4;
				if (flag4) {
					f1 = vectors.eu[l] * f15;
					f4 = vectors.ev[l] * f16;
				} else {
					f1 = af3[l];
					f4 = af4[l];
				}
				GL11.glTexCoord2f(f1, f4);
			}
			if (flag2) {
				for (int i4 = 0; i4 < j3 - 1; i4++) {
					int l4 = object3d.multiTex[i4][j];
					if (l4 == -1 || super.texMan.textures[l4].projector != null)
						continue;
					if (flag3)
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[i4 + 1], vectors.eu[l] * f15, vectors.ev[l] * f16);
					else
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[i4 + 1], vectors.uMul[i4][l], vectors.vMul[i4][l]);
				}

			}
			GL11.glColor4f(f9, f10, f11, f18);
			GL11.glVertex3f(af[k1], -af1[k1], -af2[k1]);
			if (flag) {
				float f2;
				float f5;
				if (flag4) {
					f2 = vectors.eu[i1] * f15;
					f5 = vectors.ev[i1] * f16;
				} else {
					f2 = af3[i1];
					f5 = af4[i1];
				}
				GL11.glTexCoord2f(f2, f5);
			}
			if (flag2) {
				for (int j4 = 0; j4 < j3 - 1; j4++) {
					int i5 = object3d.multiTex[j4][j];
					if (i5 == -1 || super.texMan.textures[i5].projector != null)
						continue;
					if (flag3)
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[j4 + 1], vectors.eu[i1] * f15, vectors.ev[i1] * f16);
					else
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[j4 + 1], vectors.uMul[j4][i1], vectors.vMul[j4][i1]);
				}

			}
			GL11.glColor4f(f12, f13, f14, f19);
			GL11.glVertex3f(af[l1], -af1[l1], -af2[l1]);
			if (flag1 || i2 > 0) {
				endState();
				unsetBlendingMode();
				GL11.glDepthMask(true);
				if (i2 > 0) {
					GL11.glDepthFunc(515);
					if (l2 != 1)
						setRGBScaling(l2);
				}
			}
		}
	}

	public void drawVertexArray(VisList vislist, int i, int j, FrameBuffer framebuffer, World world) {
		float f = 0.0F;
		float f1 = 0.0F;
		float f2 = 0.0F;
		float f3 = 0.0F;
		float f4 = 0.0F;
		float f5 = 0.0F;
		float f6 = 0.0F;
		float f7 = 0.0F;
		float f8 = 0.0F;
		float f9 = 0.0F;
		float f10 = 0.0F;
		endState();
		disableBlitting();
		compileDLs();
		CompiledInstance.lastObj = null;
		super.curPos = 0;
		super.colPos = 0;
		super.texPos = 0;
		super.vertPos = 0;
		boolean flag = false;
		if (super.mtTexPos == null)
			super.mtTexPos = new int[super.minDriverAndConfig];
		if (super.init) {
			for (int k = 0; k < super.minDriverAndConfig; k++) {
				super.lastIDs[k] = -9999;
				super.lastMultiTextures[k] = -9999;
				super.lastMultiModes[k] = -9999;
				super.mtTexPos[k] = 0;
			}

			char c = '\uD8F1';
			Object obj = null;
			if (!super.vertexArraysInitialized)
				initializeVertexArrays();
			super.wasTransparent = false;
			super.lastTransMode = 0;
			super.lastTexture = -9999;
			Object obj1 = null;
			boolean flag1 = false;
			boolean flag2 = false;
			float f11 = 0.0F;
			float f12 = 0.0F;
			int j1 = c;
			Object3D object3d1 = null;
			int k1 = 0;
			boolean flag3 = false;
			int i2 = super.currentRGBScaling;
			int ai[] = world.getAmbientLight();
			float af[] = new float[3];
			float af1[] = new float[4];
			for (int j2 = 0; j2 < 3; j2++) {
				af[j2] = (float) ai[j2] * GLBase.COLOR_INV;
				if (af[j2] < 0.0F)
					af[j2] = 0.0F;
			}

			super.ambientBuffer.rewind();
			super.ambientBuffer.put(af);
			super.ambientBuffer.put(1.0F);
			super.blending = false;
			for (int k2 = i; k2 <= j; k2++) {
				boolean flag4 = false;
				boolean flag5 = false;
				Object3D object3d = object3d1;
				object3d1 = vislist.vorg[k2];
				char c1 = (char) j1;
				j1 = vislist.vnum[k2];
				int l1 = k1;
				k1 = 0;
				if (vislist.splitted)
					k1 = vislist.stageCnt[k2];
				int l2 = object3d1.maxStagesUsed;
				if (l2 > super.minDriverAndConfig)
					l2 = super.minDriverAndConfig;
				Vectors vectors = object3d1.objVectors;
				Mesh mesh = object3d1.objMesh;
				float af2[] = vectors.nuOrg;
				float af3[] = vectors.nvOrg;
				float af4[][] = vectors.uMul;
				float af5[][] = vectors.vMul;
				float af6[] = vectors.xTr;
				float af7[] = vectors.yTr;
				float af8[] = vectors.zTr;
				float af9[] = vectors.srOrg;
				float af10[] = vectors.sgOrg;
				float af11[] = vectors.sbOrg;
				int i3 = mesh.points[vislist.vnumOrg[k2]][0];
				int j3 = mesh.points[vislist.vnumOrg[k2]][1];
				int k3 = mesh.points[vislist.vnumOrg[k2]][2];
				boolean flag6 = object3d1.isCompiled();
				Texture texture;
				if (k1 > 0) {
					int l3 = (k1 & 0xffff) - 1;
					texture = super.texMan.textures[object3d1.multiTex[l3][j1]];
					af2 = vectors.uMul[l3];
					af3 = vectors.vMul[l3];
					f2 = 1.0F;
					f5 = 1.0F;
					f8 = 1.0F;
					f3 = 1.0F;
					f6 = 1.0F;
					f9 = 1.0F;
					f4 = 1.0F;
					f7 = 1.0F;
					f10 = 1.0F;
				} else {
					if (!flag6) {
						f2 = af9[i3] * GLBase.COLOR_INV;
						f3 = af10[i3] * GLBase.COLOR_INV;
						f4 = af11[i3] * GLBase.COLOR_INV;
						f5 = af9[j3] * GLBase.COLOR_INV;
						f6 = af10[j3] * GLBase.COLOR_INV;
						f7 = af11[j3] * GLBase.COLOR_INV;
						f8 = af9[k3] * GLBase.COLOR_INV;
						f9 = af10[k3] * GLBase.COLOR_INV;
						f10 = af11[k3] * GLBase.COLOR_INV;
					}
					texture = super.texMan.textures[object3d1.texture[j1]];
				}
				if (object3d1.isBlended)
					texture = super.texMan.textures[object3d1.basemap[j1]];
				int l = texture.width;
				int i1 = texture.height;
				boolean flag7 = texture.projector == null;
				int i4 = mesh.coords[i3];
				int j4 = mesh.coords[j3];
				int k4 = mesh.coords[k3];
				boolean flag8 = k1 < 1 && object3d1.isTrans && (!object3d1.isBumpmapped || !object3d1.isEnvmapped);
				boolean flag9 = object3d1.usesMultiTexturing && k1 == 0;
				if (flag9 && object3d1.multiTex[0][j1] == -1)
					flag9 = false;
				boolean flag10 = object3d1.isEnvmapped && !object3d1.isBlended && k1 <= 0;
				boolean flag11 = flag10 && (!Config.glForceEnvMapToSecondStage || !flag9);
				flag10 = flag10 && Config.glForceEnvMapToSecondStage;
				if (!flag6)
					if (flag11) {
						f11 = 1.0F / (float) l;
						f12 = 1.0F / (float) i1;
						f = vectors.eu[i3] * f11;
						f1 = vectors.ev[i3] * f12;
					} else {
						f = af2[i3];
						f1 = af3[i3];
					}
				float f13 = 1.0F;
				float f14 = 1.0F;
				float f15 = 1.0F;
				if (flag8)
					if (vectors.alpha == null) {
						f13 = Config.glTransparencyOffset + (float) object3d1.transValue * Config.glTransparencyMul;
						if (f13 > 1.0F)
							f13 = 1.0F;
						f14 = f13;
						f15 = f13;
					} else {
						f13 = vectors.alpha[i3];
						f14 = vectors.alpha[j3];
						f15 = vectors.alpha[k3];
					}
				boolean flag12 = texture.getOpenGLID(super.myID) != super.lastTexture;
				if (super.lastCoords != super.veryLastCoords)
					enableVertexArrays(super.lastCoords, super.veryLastCoords);
				super.veryLastCoords = super.lastCoords;
				super.lastCoords = 1;
				if (flag9) {
					for (int l4 = 1; l4 < l2 && object3d1.multiTex[l4 - 1][j1] != -1; l4++)
						super.lastCoords++;

				}
				if (!flag12) {
					flag12 |= texture.getOpenGLID(super.myID) == 0
							|| texture.getMarker(super.myID) == Texture.MARKER_DELETE_AND_UPLOAD;
					flag12 |= super.lastCoords != super.veryLastCoords;
					flag12 |= object3d != null && object3d1.maxStagesUsed != object3d.maxStagesUsed;
				}
				if (!flag12 && flag9) {
					for (int i5 = 1; i5 < l2; i5++) {
						int i7 = object3d1.multiTex[i5 - 1][j1];
						if (i7 != -1) {
							Texture texture1 = super.texMan.textures[i7];
							if (!texture1.enabled || texture1.getOpenGLID(super.myID) == super.lastMultiTextures[i5]
									&& object3d1.multiMode[i5 - 1][j1] == super.lastMultiModes[i5]
									&& texture1.getOpenGLID(super.myID) != 0
									&& texture1.getMarker(super.myID) != Texture.MARKER_DELETE_AND_UPLOAD)
								continue;
							flag12 = true;
							break;
						}
						if (super.lastMultiTextures[i5] == -1 || super.lastMultiTextures[i5] == -9999)
							continue;
						flag12 = true;
						break;
					}

				}
				if (flag6 && super.curPos != 0 || flag8 != super.wasTransparent || object3d1.transMode != super.lastTransMode
						|| flag12 || super.curPos >= 997 || k1 != l1) {
					if (k2 != i) {
						if (flag) {
							disableCompiledPipeline();
							renableVertexArrays();
							flag = false;
						}
						if (super.wasTransparent) {
							setBlendingMode(super.lastTransMode);
							if (l1 == 0)
								setDepthBuffer();
							flag4 = true;
						}
						if (l1 > 0) {
							int j5 = object3d.multiMode[(l1 & 0xffff) - 1][c1];
							GL11.glEnable(3042);
							GL11.glBlendFunc(GLBase.blendSrcMap[j5], GLBase.blendDstMap[j5]);
							if (i2 != 1)
								setRGBScaling(1);
							GL11.glDepthMask(false);
							GL11.glDepthFunc(514);
							flag5 = true;
						}
						setTextures(object3d, c1, l1, framebuffer, world);
						renderVertexArray(super.curPos);
					}
					super.wasTransparent = flag8;
					super.lastTransMode = object3d1.transMode;
					super.curPos = 0;
					super.colPos = 0;
					super.texPos = 0;
					for (int k5 = 0; k5 < super.mtTexPos.length; k5++)
						super.mtTexPos[k5] = 0;

					super.vertPos = 0;
					super.lastTexture = texture.getOpenGLID(super.myID);
					for (int l5 = 1; l5 < l2; l5++) {
						int j7 = object3d1.multiTex[l5 - 1][j1];
						if (j7 != -1) {
							Texture texture2 = super.texMan.textures[j7];
							super.lastMultiTextures[l5] = texture2.getOpenGLID(super.myID);
							super.lastMultiModes[l5] = object3d1.multiMode[l5 - 1][j1];
						} else {
							super.lastMultiTextures[l5] = -9999;
							super.lastMultiModes[l5] = -9999;
						}
					}

				}
				if (flag6) {
					if (flag4 || flag5) {
						super.blending = false;
						unsetBlendingMode();
						GL11.glDepthMask(true);
						flag4 = false;
						if (flag5) {
							flag5 = false;
							GL11.glDepthFunc(515);
							if (i2 != 1)
								setRGBScaling(i2);
						}
					}
					if (!flag)
						enableCompiledPipeline();
					if (object3d1.isTrans) {
						if (!super.blending)
							setBlendingMode(object3d1.transMode);
						flag4 = true;
						if (k1 == 0) {
							if (!super.blending)
								setDepthBuffer();
							if (k2 < j) {
								Object3D object3d2 = vislist.vorg[k2 + 1];
								if (object3d2.isCompiled() && object3d2.isTrans == object3d1.isTrans
										&& object3d2.transMode == object3d1.transMode) {
									flag4 = false;
									super.blending = true;
								}
							}
						}
					}
					if (k1 > 0) {
						int i6 = object3d1.multiMode[(k1 & 0xffff) - 1][j1];
						GL11.glEnable(3042);
						GL11.glBlendFunc(GLBase.blendSrcMap[i6], GLBase.blendDstMap[i6]);
						if (i2 != 1)
							setRGBScaling(1);
						GL11.glDepthMask(false);
						GL11.glDepthFunc(514);
						flag5 = true;
					}
					setTextures(object3d1, j1, k1, framebuffer, world);
					Color color = object3d1.getAdditionalColor();
					af1[0] = (float) color.getRed() * GLBase.COLOR_INV;
					af1[1] = (float) color.getGreen() * GLBase.COLOR_INV;
					af1[2] = (float) color.getBlue() * GLBase.COLOR_INV;
					float f16 = 1.0F;
					if (flag8) {
						f16 = Config.glTransparencyOffset + (float) object3d1.transValue * Config.glTransparencyMul;
						if (f16 > 1.0F)
							f16 = 1.0F;
					}
					af1[3] = f16;
					CompiledInstance compiledinstance = (CompiledInstance) object3d1.compiled.get(vislist.portalNum[k2]);
					compiledinstance.render(super.myID, this, super.ambientBuffer, af1, super.renderTarget != null
							&& super.renderTarget.isShadowMap, world.camera, object3d1.nearestLights, false, null);
					flag = true;
					super.curPos = 0;
					super.colPos = 0;
					super.texPos = 0;
					for (int i8 = 0; i8 < super.mtTexPos.length; i8++)
						super.mtTexPos[i8] = 0;

					super.vertPos = 0;
				} else {
					super.colors.put(super.colPos, f2);
					super.colors.put(super.colPos + 1, f3);
					super.colors.put(super.colPos + 2, f4);
					super.colors.put(super.colPos + 3, f13);
					super.colPos += 4;
					super.vertices.put(super.vertPos, af6[i4]);
					super.vertices.put(super.vertPos + 1, -af7[i4]);
					super.vertices.put(super.vertPos + 2, -af8[i4]);
					super.vertPos += 3;
					if (flag7) {
						super.textures.put(super.texPos, f);
						super.textures.put(super.texPos + 1, f1);
						super.texPos += 2;
					}
					if (flag9) {
						for (int j6 = 1; j6 < l2; j6++) {
							int k7 = j6 - 1;
							int l7 = object3d1.multiTex[k7][j1];
							if (l7 == -1 || super.texMan.textures[l7].projector != null)
								continue;
							int j8 = super.mtTexPos[j6];
							FloatBuffer floatbuffer = super.multiTextures[j6];
							if (flag10) {
								f11 = 1.0F / (float) l;
								f12 = 1.0F / (float) i1;
								float af12[] = vectors.eu;
								float af14[] = vectors.ev;
								floatbuffer.put(j8, af12[i3] * f11);
								floatbuffer.put(j8 + 1, af14[i3] * f12);
								floatbuffer.put(j8 + 2, af12[j3] * f11);
								floatbuffer.put(j8 + 3, af14[j3] * f12);
								floatbuffer.put(j8 + 4, af12[k3] * f11);
								floatbuffer.put(j8 + 5, af14[k3] * f12);
							} else {
								float af13[] = af4[k7];
								float af15[] = af5[k7];
								floatbuffer.put(j8, af13[i3]);
								floatbuffer.put(j8 + 1, af15[i3]);
								floatbuffer.put(j8 + 2, af13[j3]);
								floatbuffer.put(j8 + 3, af15[j3]);
								floatbuffer.put(j8 + 4, af13[k3]);
								floatbuffer.put(j8 + 5, af15[k3]);
							}
							super.mtTexPos[j6] += 6;
						}

					}
					super.curPos++;
					if (flag11) {
						f = vectors.eu[j3] * f11;
						f1 = vectors.ev[j3] * f12;
					} else {
						f = af2[j3];
						f1 = af3[j3];
					}
					super.colors.put(super.colPos, f5);
					super.colors.put(super.colPos + 1, f6);
					super.colors.put(super.colPos + 2, f7);
					super.colors.put(super.colPos + 3, f14);
					super.colPos += 4;
					super.vertices.put(super.vertPos, af6[j4]);
					super.vertices.put(super.vertPos + 1, -af7[j4]);
					super.vertices.put(super.vertPos + 2, -af8[j4]);
					super.vertPos += 3;
					if (flag7) {
						super.textures.put(super.texPos, f);
						super.textures.put(super.texPos + 1, f1);
						super.texPos += 2;
					}
					super.curPos++;
					if (flag11) {
						f = vectors.eu[k3] * f11;
						f1 = vectors.ev[k3] * f12;
					} else {
						f = af2[k3];
						f1 = af3[k3];
					}
					super.colors.put(super.colPos, f8);
					super.colors.put(super.colPos + 1, f9);
					super.colors.put(super.colPos + 2, f10);
					super.colors.put(super.colPos + 3, f15);
					super.colPos += 4;
					super.vertices.put(super.vertPos, af6[k4]);
					super.vertices.put(super.vertPos + 1, -af7[k4]);
					super.vertices.put(super.vertPos + 2, -af8[k4]);
					super.vertPos += 3;
					if (flag7) {
						super.textures.put(super.texPos, f);
						super.textures.put(super.texPos + 1, f1);
						super.texPos += 2;
					}
					super.curPos++;
					if (j == k2) {
						if (flag) {
							disableCompiledPipeline();
							renableVertexArrays();
							flag = false;
						}
						if (flag8) {
							setBlendingMode(object3d1.transMode);
							if (k1 == 0)
								setDepthBuffer();
							super.wasTransparent = flag8;
							super.lastTransMode = object3d1.transMode;
							flag4 = true;
						}
						if (k1 > 0) {
							int k6 = object3d1.multiMode[(k1 & 0xffff) - 1][j1];
							GL11.glEnable(3042);
							GL11.glBlendFunc(GLBase.blendSrcMap[k6], GLBase.blendDstMap[k6]);
							if (i2 != 1)
								setRGBScaling(1);
							GL11.glDepthMask(false);
							GL11.glDepthFunc(514);
							flag5 = true;
						}
						setTextures(object3d1, j1, k1, framebuffer, world);
						renderVertexArray(super.curPos);
						super.curPos = 0;
						super.colPos = 0;
						super.texPos = 0;
						super.vertPos = 0;
						for (int l6 = 0; l6 < super.mtTexPos.length; l6++)
							super.mtTexPos[l6] = 0;

					}
				}
				if (!flag4 && !flag5)
					continue;
				super.blending = false;
				unsetBlendingMode();
				GL11.glDepthMask(true);
				if (!flag5)
					continue;
				GL11.glDepthFunc(515);
				if (i2 != 1)
					setRGBScaling(i2);
			}

		}
		super.matrixCache.clear();
		if (flag) {
			disableCompiledPipeline();
			renableVertexArrays();
		}
		CompiledInstance.lastObj = null;
	}

	public void drawWireframe(VisList vislist, int i, int j, FrameBuffer framebuffer, World world) {
		if (super.init) {
			endState();
			disableBlitting();
			GL11.glPolygonMode(1032, 6913);
			GL11.glDisable(2929);
			GL11.glDisable(3553);
			disableAllHigherStages();
			Object3D object3d = vislist.vorg[i];
			Vectors vectors = object3d.objVectors;
			Mesh mesh = object3d.objMesh;
			int k = mesh.points[vislist.vnumOrg[i]][0];
			int l = mesh.points[vislist.vnumOrg[i]][1];
			int i1 = mesh.points[vislist.vnumOrg[i]][2];
			int j1 = mesh.coords[k];
			int k1 = mesh.coords[l];
			int l1 = mesh.coords[i1];
			beginState(4);
			int i2 = j >> 16 & 0xff;
			int j2 = j >> 8 & 0xff;
			int k2 = j & 0xff;
			GL11.glColor3f((float) i2 / 255F, (float) j2 / 255F, (float) k2 / 255F);
			if (!object3d.isCompiled()) {
				GL11.glVertex3f(vectors.xTr[j1], -vectors.yTr[j1], -vectors.zTr[j1]);
				GL11.glVertex3f(vectors.xTr[k1], -vectors.yTr[k1], -vectors.zTr[k1]);
				GL11.glVertex3f(vectors.xTr[l1], -vectors.yTr[l1], -vectors.zTr[l1]);
			} else {
				CompiledInstance compiledinstance = (CompiledInstance) object3d.compiled.get(vislist.portalNum[i]);
				compiledinstance.render(super.myID, this, null, null, false, world.camera, (float[][]) null, true, null);
				disableCompiledPipeline();
				renableVertexArrays();
				super.matrixCache.clear();
			}
			endState();
			GL11.glEnable(2929);
			GL11.glEnable(3553);
			GL11.glPolygonMode(1032, 6914);
		}
	}

	private Texture setTextures(Object3D object3d, int i, int j, FrameBuffer framebuffer, World world) {
		boolean flag = false;
		Texture texture = null;
		if (j < 1) {
			texture = super.texMan.textures[object3d.texture[i]];
			if (object3d.isBlended)
				texture = super.texMan.textures[object3d.basemap[i]];
		} else {
			texture = super.texMan.textures[object3d.multiTex[(j & 0xffff) - 1][i]];
		}
		if (texture.getOpenGLID(super.myID) == 0 || texture.getMarker(super.myID) == Texture.MARKER_DELETE_AND_UPLOAD) {
			texture.setMarker(super.myID, Texture.MARKER_NOTHING);
			endState();
			if (texture != super.renderTarget) {
				if (texture.getOpenGLID(super.myID) != 0)
					removeTexture(texture);
				convertTexture(texture);
			}
			super.lastTextures[0] = -1;
		}
		flag = texture.isShadowMap;
		bindAndProject(0, texture, world.getCamera());
		if (object3d.usesMultiTexturing && j == 0) {
			if (super.maxStages < object3d.maxStagesUsed) {
				super.maxStages = object3d.maxStagesUsed;
				if (super.maxStages > super.minDriverAndConfig)
					super.maxStages = super.minDriverAndConfig;
			}
			for (int k = 1; k < super.maxStages; k++) {
				int l = k - 1;
				int i1 = object3d.multiTex[l][i];
				if (i1 != -1) {
					Texture texture1 = super.texMan.textures[i1];
					if (texture1.enabled) {
						flag |= texture1.isShadowMap;
						int j1 = GLBase.modeMap[object3d.multiMode[l][i]];
						if (Config.glRevertADDtoMODULATE && (j1 == 260 || j1 == 34164 || j1 == 34023))
							j1 = 8448;
						if (!super.stageInitialized[k])
							initTextureStage(k, j1);
						else
							switchTextureMode(k, j1);
						if (texture1.getOpenGLID(super.myID) == 0
								|| texture1.getMarker(super.myID) == Texture.MARKER_DELETE_AND_UPLOAD) {
							texture1.setMarker(super.myID, Texture.MARKER_NOTHING);
							endState();
							if (texture1 != super.renderTarget) {
								if (texture1.getOpenGLID(super.myID) != 0)
									removeTexture(texture1);
								convertTexture(texture1);
							}
							super.lastTextures[k] = -1;
						}
						bindAndProject(k, texture1, world.getCamera());
					} else {
						disableStage(k);
					}
				} else {
					disableStage(k);
				}
			}

		} else {
			disableUnusedStages();
		}
		if (!flag)
			disableGlobalAlphaTest();
		return texture;
	}
}
