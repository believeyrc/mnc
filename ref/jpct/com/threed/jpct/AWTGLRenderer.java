// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;
import org.lwjgl.opengl.*;

// Referenced classes of package com.threed.jpct:
//            GLBase, Camera, AWTJPCTCanvas, GLThreadBuffer, 
//            World, IPostProcessor, Projector, Texture, 
//            FrameBuffer, CompiledInstance, Matrix, IRenderer, 
//            IThreadBuffer, GLHelper, Logger, AWTDisplayList, 
//            Config, IntegerC, IPaintListener, VisList, 
//            AWTBufferList, TextureManager, BufferedMatrix

public class AWTGLRenderer extends GLBase implements IRenderer {

	AWTGLRenderer() {
		canvas = null;
		forceFOV = false;
		aboutToDispose = false;
		cam = new Camera();
		ambient = new int[3];
		realCanvas = true;
	}

	public Object getLock() {
		if (canvas != null)
			return canvas.getLock();
		else
			return null;
	}

	public void init(int i, int j, int k, int l, int i1) {
		super.init = false;
		try {
			if (realCanvas) {
				PixelFormat pixelformat = GLHelper.getPixelFormatFromConfig();
				if (pixelformat == null && i1 > 0) {
					DisplayMode displaymode = GLHelper.findMode(i, j, k, l);
					if (displaymode != null)
						try {
							pixelformat = new PixelFormat(displaymode.getBitsPerPixel(), 0, l, 0, i1);
						} catch (Exception exception1) {
							Logger.log("Number of samples not supported or incorrect video mode!", 0);
							i1 = 0;
						}
					else
						Logger.log("Can't set videomode - try different settings!", 0);
				}
				if (pixelformat == null)
					canvas = new AWTJPCTCanvas(this);
				else
					canvas = new AWTJPCTCanvas(this, pixelformat);
			} else {
				canvas = new GLThreadBuffer(this);
			}
			canvas.setBounds(0, 0, i, j);
			canvas.setSamples(i1);
			super.xp = i;
			super.yp = j;
		} catch (Exception exception) {
			Logger.log("Can't initialize canvas!", 0);
			exception.printStackTrace();
		}
	}

	public void dispose() {
		aboutToDispose = true;
	}

	private void reallyDispose() {
		aboutToDispose = false;
		super.dispose();
		canvas.dispose();
		Logger.log("OpenGL (AWTGLCanvas) renderer disposed", 2);
	}

	Object[] executeGL(AWTDisplayList awtdisplaylist, int i) {
		if (super.init) {
			int j = awtdisplaylist.command[i];
			Object aobj[] = (Object[]) awtdisplaylist.params[i];
			if (j == 4 || j == 7)
				enableBlitting(aobj);
			else
				disableBlitting();
			switch (j) {
			default:
				break;

			case 23: // '\027'
				canvas.fillInstances();
				break;

			case 0: // '\0'
				super.myWorld = (World) aobj[0];
				setFrustum(aobj);
				setLightsAndFog(super.myWorld);
				unloadTextures();
				removeLists();
				break;

			case 2: // '\002'
				clear(aobj);
				break;

			case 4: // '\004'
				blit(aobj, super.scaleX, super.scaleY);
				break;

			case 5: // '\005'
				GL11.glFlush();
				break;

			case 6: // '\006'
				grabScreen(aobj);
				return aobj;

			case 7: // '\007'
				prepareForBlitting(aobj);
				Object aobj1[] = new Object[aobj.length];
				System.arraycopy(((Object) (aobj)), 1, ((Object) (aobj1)), 1, aobj1.length - 1);
				aobj1[0] = super.blitBuffer;
				blit(aobj1, super.scaleX, super.scaleY);
				break;

			case 8: // '\b'
				endState();
				break;

			case 9: // '\t'
				setBufferViewport(aobj);
				break;

			case 10: // '\n'
				if (aobj[0] instanceof String)
					aobj[0] = Config.booleanValueOf(supportsExtension((String) aobj[0]));
				return aobj;

			case 26: // '\032'
				float af[] = (float[]) aobj[1];
				Integer integer1 = (Integer) aobj[0];
				addClippingPlane(integer1.intValue(), af);
				break;

			case 27: // '\033'
				Integer integer = (Integer) aobj[0];
				removeClippingPlane(integer.intValue());
				break;

			case 25: // '\031'
				if (aobj[0] instanceof String)
					aobj[0] = IntegerC.valueOf(getTextureSize());
				return aobj;

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

			case 1000:
				renderToTarget();
				break;

			case 21: // '\025'
				cam = (Camera) aobj[0];
				ambient = (int[]) aobj[1];
				break;

			case 22: // '\026'
				setTextureProjector((Projector) aobj[0], (Texture) aobj[1]);
				break;

			case 24: // '\030'
				super.listenerActive = ((Boolean) aobj[0]).booleanValue();
				break;
			}
		}
		return null;
	}

	public void execute(int i, Object aobj[]) {
		switch (i) {
		case 19: // '\023'
		case 20: // '\024'
		case 21: // '\025'
		case 22: // '\026'
		default:
			break;

		case 0: // '\0'
			canvas.add(i, ((Object) (createFrustum(aobj))));
			break;

		case 23: // '\027'
			canvas.add(i, null);
			break;

		case 1: // '\001'
			bufferTextureProjectors();
			if (!canvas.hasRenderTarget()) {
				canvas.switchList();
				Thread.yield();
			} else {
				canvas.add(1000, null);
			}
			break;

		case 2: // '\002'
			canvas.add(i, ((Object) (aobj)));
			break;

		case 3: // '\003'
			Logger.log("State changes: " + super.stateChanges, 2);
			super.stateChanges = 0;
			break;

		case 4: // '\004'
			canvas.add(i, ((Object) (aobj)));
			break;

		case 5: // '\005'
			canvas.add(i, null);
			break;

		case 6: // '\006'
			canvas.addOnce(i, ((Object) (aobj)));
			waitForPainting(aobj);
			break;

		case 7: // '\007'
			canvas.add(i, ((Object) (aobj)));
			break;

		case 26: // '\032'
			canvas.add(i, ((Object) (aobj)));
			break;

		case 27: // '\033'
			canvas.add(i, ((Object) (aobj)));
			break;

		case 8: // '\b'
			canvas.add(i, null);
			break;

		case 9: // '\t'
			canvas.add(i, ((Object) (aobj)));
			break;

		case 10: // '\n'
			canvas.addOnce(i, ((Object) (aobj)));
			waitForPainting(aobj);
			break;

		case 25: // '\031'
			canvas.addOnce(i, ((Object) (aobj)));
			waitForPainting(aobj);
			break;

		case 11: // '\013'
			aobj[0] = canvas;
			break;

		case 12: // '\f'
			if (super.listener != null && super.listenerActive)
				super.listener.startPainting();
			break;

		case 13: // '\r'
			endState();
			resetTextureStates();
			if (super.listener != null && super.listenerActive)
				super.listener.finishedPainting();
			if (aboutToDispose)
				reallyDispose();
			break;

		case 14: // '\016'
			realCanvas = ((Boolean) aobj[0]).booleanValue();
			break;

		case 15: // '\017'
			canvas.add(i, null);
			break;

		case 16: // '\020'
			canvas.add(i, ((Object) (aobj)));
			break;

		case 17: // '\021'
			canvas.addOnce(i, ((Object) (aobj)));
			waitForPainting(aobj);
			break;

		case 18: // '\022'
			forceFOV = true;
			if (aobj[0] == null)
				canvas.disableRenderTarget();
			else
				canvas.enableRenderTarget();
			canvas.add(i, ((Object) (aobj)));
			break;

		case 24: // '\030'
			canvas.add(i, ((Object) (aobj)));
			break;
		}
	}

	void init() throws Exception {
		init(true, super.xp, super.yp);
	}

	private void waitForPainting(Object aobj[]) {
		canvas.observePainting();
		canvas.repaint();
		while (!canvas.hasBeenPainted())
			try {
				Thread.sleep(1L);
			} catch (Exception exception) {
			}
		Object aobj1[] = canvas.getPaintResults();
		if (aobj1 != null && aobj1.length >= aobj.length)
			System.arraycopy(((Object) (aobj1)), 0, ((Object) (aobj)), 0, aobj.length);
	}

	private Object[] createFrustum(Object aobj[]) {
		World world = (World) aobj[0];
		Camera camera = world.getCamera();
		float f = camera.getFOV();
		FrameBuffer framebuffer = (FrameBuffer) aobj[1];
		canvas.add(camera, world.getAmbientLight());
		if (forceFOV || f != super.lastFOV || Config.farPlane != super.lastFarPlane || !Config.glIgnoreNearPlane
				&& Config.nearPlane != super.lastNearPlane || Config.autoMaintainAspectRatio != super.lastFOVMode) {
			forceFOV = false;
			float f1 = 0.0F;
			aobj = new Object[11];
			if (Config.autoMaintainAspectRatio)
				f1 = f * ((float) framebuffer.getOutputHeight() / (float) framebuffer.getOutputWidth());
			else
				f1 = f;
			if (camera.getYFOV() != -1F)
				f1 = camera.getYFOV();
			float f2 = Config.farPlane;
			float f3 = 1.0F;
			if (!Config.glIgnoreNearPlane)
				f3 = Config.nearPlane;
			float f4 = f1 * 0.5F;
			float f5 = -f1 * 0.5F;
			float f6 = -f * 0.5F;
			float f7 = f * 0.5F;
			aobj[1] = new Float(f6);
			aobj[2] = new Float(f7);
			aobj[3] = new Float(f5);
			aobj[4] = new Float(f4);
			aobj[5] = new Float(f3);
			aobj[6] = new Float(f2);
			aobj[7] = new Float(f + 100F * f1);
			aobj[8] = new Float(camera.scaleX);
			aobj[9] = new Float(camera.scaleY);
			aobj[10] = Boolean.valueOf(Config.autoMaintainAspectRatio);
			aobj[0] = world;
		}
		return aobj;
	}

	private void setFrustum(Object aobj[]) {
		if (aobj.length != 2) {
			GL11.glMatrixMode(5889);
			GL11.glLoadIdentity();
			GL11.glFrustum(((Float) aobj[1]).floatValue(), ((Float) aobj[2]).floatValue(), ((Float) aobj[3]).floatValue(),
					((Float) aobj[4]).floatValue(), ((Float) aobj[5]).floatValue(), ((Float) aobj[6]).floatValue());
			super.lastFOV = ((Float) aobj[7]).floatValue();
			super.lastFarPlane = ((Float) aobj[6]).floatValue();
			super.lastNearPlane = ((Float) aobj[5]).floatValue();
			super.lastFOVMode = ((Boolean) aobj[10]).booleanValue();
			setScale(((Float) aobj[8]).floatValue(), ((Float) aobj[9]).floatValue());
		}
	}

	public void drawStrip(VisList vislist, int i, int j, FrameBuffer framebuffer, World world) {
		if (super.init)
			canvas.add(vislist, i, j, 1);
	}

	void drawStrip(AWTDisplayList awtdisplaylist, int i) {
		if (super.init) {
			endState();
			AWTBufferList awtbufferlist = awtdisplaylist.vl[i].getFrontBuffer();
			int j = awtdisplaylist.start[i];
			int k = awtdisplaylist.end[i];
			Texture texture = setTextures(awtbufferlist, j, cam);
			disableBlitting();
			boolean flag = texture.projector == null;
			int l = awtbufferlist.stageCnt[j];
			int i1 = super.currentRGBScaling;
			float f6 = awtbufferlist.r1[j];
			float f8 = awtbufferlist.g1[j];
			float f10 = awtbufferlist.b1[j];
			float f12 = 1.0F;
			float f13 = 1.0F;
			float f14 = 1.0F;
			boolean flag1 = awtbufferlist.trans[j];
			boolean flag2 = awtbufferlist.multi[j];
			int j1 = awtbufferlist.maxStages[j];
			if (j1 > super.minDriverAndConfig)
				j1 = super.minDriverAndConfig;
			j1--;
			if (flag1) {
				setBlendingMode(awtbufferlist.transValue[j] >> 16);
				if (l == 0)
					setDepthBuffer();
				if (awtbufferlist.a1 == null || awtbufferlist.a1[j] == -1F) {
					f12 = Config.glTransparencyOffset + (float) (awtbufferlist.transValue[j] & 0xffff) * Config.glTransparencyMul;
					if (f12 > 1.0F)
						f12 = 1.0F;
					f13 = f12;
					f14 = f12;
				} else {
					f12 = awtbufferlist.a1[j];
					f13 = awtbufferlist.a2[j];
					f14 = awtbufferlist.a3[j];
				}
			}
			if (l > 0) {
				int k1 = awtbufferlist.multiMode[0][j];
				GL11.glEnable(3042);
				GL11.glBlendFunc(GLBase.blendSrcMap[k1], GLBase.blendDstMap[k1]);
				if (i1 != 1)
					setRGBScaling(1);
				GL11.glDepthMask(false);
				GL11.glDepthFunc(514);
			}
			GL11.glBegin(5);
			float f15 = awtbufferlist.x1[j];
			float f16 = awtbufferlist.y1[j];
			float f17 = awtbufferlist.z1[j];
			float f18 = awtbufferlist.x2[j];
			float f19 = awtbufferlist.y2[j];
			float f20 = awtbufferlist.z2[j];
			float f21 = awtbufferlist.x3[j];
			float f22 = awtbufferlist.y3[j];
			float f23 = awtbufferlist.z3[j];
			if (flag) {
				float f = awtbufferlist.u1[j];
				float f3 = awtbufferlist.v1[j];
				GL11.glTexCoord2f(f, f3);
			}
			if (flag2) {
				for (int l1 = 0; l1 < j1; l1++)
					if (awtbufferlist.multiTextures[l1][j] != null && awtbufferlist.multiTextures[l1][j].projector == null)
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[l1 + 1], awtbufferlist.u1Mul[l1][j],
								awtbufferlist.v1Mul[l1][j]);

			}
			GL11.glColor4f(f6 * GLBase.COLOR_INV, f8 * GLBase.COLOR_INV, f10 * GLBase.COLOR_INV, f12);
			GL11.glVertex3f(f15, -f16, -f17);
			f6 = awtbufferlist.r2[j];
			f8 = awtbufferlist.g2[j];
			f10 = awtbufferlist.b2[j];
			if (flag) {
				float f1 = awtbufferlist.u2[j];
				float f4 = awtbufferlist.v2[j];
				GL11.glTexCoord2f(f1, f4);
			}
			if (flag2) {
				for (int i2 = 0; i2 < j1; i2++)
					if (awtbufferlist.multiTextures[i2][j] != null && awtbufferlist.multiTextures[i2][j].projector == null)
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[i2 + 1], awtbufferlist.u2Mul[i2][j],
								awtbufferlist.v2Mul[i2][j]);

			}
			GL11.glColor4f(f6 * GLBase.COLOR_INV, f8 * GLBase.COLOR_INV, f10 * GLBase.COLOR_INV, f13);
			GL11.glVertex3f(f18, -f19, -f20);
			f6 = awtbufferlist.r3[j];
			f8 = awtbufferlist.g3[j];
			f10 = awtbufferlist.b3[j];
			if (flag) {
				float f2 = awtbufferlist.u3[j];
				float f5 = awtbufferlist.v3[j];
				GL11.glTexCoord2f(f2, f5);
			}
			if (flag2) {
				for (int j2 = 0; j2 < j1; j2++)
					if (awtbufferlist.multiTextures[j2][j] != null && awtbufferlist.multiTextures[j2][j].projector == null)
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[j2 + 1], awtbufferlist.u3Mul[j2][j],
								awtbufferlist.v3Mul[j2][j]);

			}
			GL11.glColor4f(f6 * GLBase.COLOR_INV, f8 * GLBase.COLOR_INV, f10 * GLBase.COLOR_INV, f14);
			GL11.glVertex3f(f21, -f22, -f23);
			j++;
			for (k += j; j < k; j++) {
				float f7 = awtbufferlist.r3[j];
				float f9 = awtbufferlist.g3[j];
				float f11 = awtbufferlist.b3[j];
				GL11.glTexCoord2f(awtbufferlist.u3[j], awtbufferlist.v3[j]);
				if (flag2) {
					for (int k2 = 0; k2 < j1; k2++)
						if (awtbufferlist.multiTextures[k2][j] != null)
							ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[k2 + 1], awtbufferlist.u3Mul[k2][j],
									awtbufferlist.v3Mul[k2][j]);

				}
				if (awtbufferlist.a3 != null && awtbufferlist.a3[j] != -1F)
					f12 = awtbufferlist.a3[j];
				GL11.glColor4f(f7 * GLBase.COLOR_INV, f9 * GLBase.COLOR_INV, f11 * GLBase.COLOR_INV, f12);
				GL11.glVertex3f(awtbufferlist.x3[j], -awtbufferlist.y3[j], -awtbufferlist.z3[j]);
			}

			GL11.glEnd();
			if (flag1 || l > 0) {
				unsetBlendingMode();
				GL11.glDepthMask(true);
				if (l > 0) {
					GL11.glDepthFunc(515);
					if (i1 != 1)
						setRGBScaling(i1);
				}
			}
		}
	}

	public void drawPolygon(VisList vislist, int i, FrameBuffer framebuffer, World world) {
		if (super.init)
			canvas.add(vislist, i, i, 0);
	}

	void drawPolygon(AWTDisplayList awtdisplaylist, int i) {
		if (super.init) {
			AWTBufferList awtbufferlist = awtdisplaylist.vl[i].getFrontBuffer();
			int j = awtdisplaylist.start[i];
			Texture texture = setTextures(awtbufferlist, j, cam);
			disableBlitting();
			boolean flag = texture.projector == null;
			if (texture == null) {
				Logger.log("Fatal error!", 0);
				return;
			}
			float f = 1.0F;
			float f1 = 1.0F;
			float f2 = 1.0F;
			int k = awtbufferlist.stageCnt[j];
			boolean flag1 = awtbufferlist.trans[j];
			boolean flag2 = awtbufferlist.multi[j];
			int l = awtbufferlist.maxStages[j];
			if (l > super.minDriverAndConfig)
				l = super.minDriverAndConfig;
			l--;
			if (flag1) {
				endState();
				setBlendingMode(awtbufferlist.transValue[j] >> 16);
				if (k == 0)
					setDepthBuffer();
				if (awtbufferlist.a1 == null || awtbufferlist.a1[j] == -1F) {
					f = Config.glTransparencyOffset + (float) (awtbufferlist.transValue[j] & 0xffff) * Config.glTransparencyMul;
					if (f > 1.0F)
						f = 1.0F;
					f1 = f;
					f2 = f;
				} else {
					f = awtbufferlist.a1[j];
					f1 = awtbufferlist.a2[j];
					f2 = awtbufferlist.a3[j];
				}
			}
			int i1 = super.currentRGBScaling;
			if (k > 0) {
				int j1 = awtbufferlist.multiMode[0][j];
				endState();
				GL11.glEnable(3042);
				GL11.glBlendFunc(GLBase.blendSrcMap[j1], GLBase.blendDstMap[j1]);
				if (i1 != 1)
					setRGBScaling(1);
				GL11.glDepthMask(false);
				GL11.glDepthFunc(514);
			}
			beginState(4);
			if (flag)
				GL11.glTexCoord2f(awtbufferlist.u1[j], awtbufferlist.v1[j]);
			if (flag2) {
				for (int k1 = 0; k1 < l; k1++)
					if (awtbufferlist.multiTextures[k1][j] != null && awtbufferlist.multiTextures[k1][j].projector == null)
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[k1 + 1], awtbufferlist.u1Mul[k1][j],
								awtbufferlist.v1Mul[k1][j]);

			}
			GL11.glColor4f(awtbufferlist.r1[j] * GLBase.COLOR_INV, awtbufferlist.g1[j] * GLBase.COLOR_INV,
					awtbufferlist.b1[j] * GLBase.COLOR_INV, f);
			GL11.glVertex3f(awtbufferlist.x1[j], -awtbufferlist.y1[j], -awtbufferlist.z1[j]);
			if (flag)
				GL11.glTexCoord2f(awtbufferlist.u2[j], awtbufferlist.v2[j]);
			if (flag2) {
				for (int l1 = 0; l1 < l; l1++)
					if (awtbufferlist.multiTextures[l1][j] != null && awtbufferlist.multiTextures[l1][j].projector == null)
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[l1 + 1], awtbufferlist.u2Mul[l1][j],
								awtbufferlist.v2Mul[l1][j]);

			}
			GL11.glColor4f(awtbufferlist.r2[j] * GLBase.COLOR_INV, awtbufferlist.g2[j] * GLBase.COLOR_INV,
					awtbufferlist.b2[j] * GLBase.COLOR_INV, f1);
			GL11.glVertex3f(awtbufferlist.x2[j], -awtbufferlist.y2[j], -awtbufferlist.z2[j]);
			if (flag)
				GL11.glTexCoord2f(awtbufferlist.u3[j], awtbufferlist.v3[j]);
			if (flag2) {
				for (int i2 = 0; i2 < l; i2++)
					if (awtbufferlist.multiTextures[i2][j] != null && awtbufferlist.multiTextures[i2][j].projector == null)
						ARBMultitexture.glMultiTexCoord2fARB(GLBase.stageMap[i2 + 1], awtbufferlist.u3Mul[i2][j],
								awtbufferlist.v3Mul[i2][j]);

			}
			GL11.glColor4f(awtbufferlist.r3[j] * GLBase.COLOR_INV, awtbufferlist.g3[j] * GLBase.COLOR_INV,
					awtbufferlist.b3[j] * GLBase.COLOR_INV, f2);
			GL11.glVertex3f(awtbufferlist.x3[j], -awtbufferlist.y3[j], -awtbufferlist.z3[j]);
			if (flag1 || k > 0) {
				endState();
				unsetBlendingMode();
				GL11.glDepthMask(true);
				if (k > 0) {
					GL11.glDepthFunc(515);
					if (i1 != 1)
						setRGBScaling(i1);
				}
			}
		}
	}

	public void drawVertexArray(VisList vislist, int i, int j, FrameBuffer framebuffer, World world) {
		if (super.init)
			canvas.add(vislist, i, j, 2);
	}

	void drawVertexArray(AWTDisplayList awtdisplaylist, int i) {
		endState();
		disableBlitting();
		compileDLs();
		CompiledInstance.lastObj = null;
		super.curPos = 0;
		super.colPos = 0;
		super.texPos = 0;
		super.vertPos = 0;
		if (super.mtTexPos == null)
			super.mtTexPos = new int[super.minDriverAndConfig];
		for (int j = 0; j < super.mtTexPos.length; j++)
			super.mtTexPos[j] = 0;

		boolean flag = false;
		if (super.init) {
			for (int k = 0; k < super.minDriverAndConfig; k++) {
				super.lastIDs[k] = -9999;
				super.lastMultiTextures[k] = -9999;
				super.lastMultiModes[k] = -9999;
			}

			char c = '\uD8F1';
			byte byte0 = -1;
			if (!super.vertexArraysInitialized)
				initializeVertexArrays();
			AWTBufferList awtbufferlist = awtdisplaylist.vl[i].getFrontBuffer();
			super.wasTransparent = false;
			super.lastTransMode = 0;
			super.lastTexture = -9999;
			boolean flag1 = false;
			int k1 = 0;
			int l1 = awtdisplaylist.end[i];
			Object obj = null;
			float af[] = awtbufferlist.r1;
			float af1[] = awtbufferlist.r2;
			float af2[] = awtbufferlist.r3;
			float af3[] = awtbufferlist.g1;
			float af4[] = awtbufferlist.g2;
			float af5[] = awtbufferlist.g3;
			float af6[] = awtbufferlist.b1;
			float af7[] = awtbufferlist.b2;
			float af8[] = awtbufferlist.b3;
			float af9[] = awtbufferlist.x1;
			float af10[] = awtbufferlist.x2;
			float af11[] = awtbufferlist.x3;
			float af12[] = awtbufferlist.y1;
			float af13[] = awtbufferlist.y2;
			float af14[] = awtbufferlist.y3;
			float af15[] = awtbufferlist.z1;
			float af16[] = awtbufferlist.z2;
			float af17[] = awtbufferlist.z3;
			float af18[] = awtbufferlist.v1;
			float af19[] = awtbufferlist.v2;
			float af20[] = awtbufferlist.v3;
			float af21[] = awtbufferlist.u1;
			float af22[] = awtbufferlist.u2;
			float af23[] = awtbufferlist.u3;
			float af24[] = awtbufferlist.a1;
			float af25[] = awtbufferlist.a2;
			float af26[] = awtbufferlist.a3;
			Texture atexture[] = awtbufferlist.texture;
			boolean aflag[] = awtbufferlist.trans;
			boolean aflag1[] = awtbufferlist.multi;
			int ai[] = awtbufferlist.maxStages;
			int ai1[] = awtbufferlist.stageCnt;
			int i2 = super.currentRGBScaling;
			int ai2[] = ambient;
			float af27[] = new float[3];
			float af28[] = new float[4];
			for (int j2 = 0; j2 < 3; j2++) {
				af27[j2] = (float) ai2[j2] * GLBase.COLOR_INV;
				if (af27[j2] < 0.0F)
					af27[j2] = 0.0F;
			}

			super.ambientBuffer.rewind();
			super.ambientBuffer.put(af27);
			super.ambientBuffer.put(1.0F);
			Object3D object3d = null;
			Matrix matrix = null;
			super.blending = false;
			for (int k2 = 0; k2 <= l1; k2++) {
				boolean flag2 = false;
				boolean flag3 = false;
				boolean flag4 = false;
				int j1 = k1;
				k1 = ai1[k2];
				if (awtbufferlist.r3[k2] == -1000000F)
					flag4 = true;
				int i1 = k2 - 1;
				int l = i1;
				Texture texture = atexture[k2];
				boolean flag5 = texture.projector == null;
				float f = 1.0F;
				float f1 = 1.0F;
				float f2 = 1.0F;
				boolean flag6 = aflag[k2];
				int l2 = awtbufferlist.transValue[k2] >> 16;
				boolean flag7 = aflag1[k2];
				int i3 = ai[k2];
				if (i3 > super.minDriverAndConfig)
					i3 = super.minDriverAndConfig;
				if (flag6)
					if (af24 == null || af24[k2] == -1F) {
						f = Config.glTransparencyOffset + (float) (awtbufferlist.transValue[k2] & 0xffff)
								* Config.glTransparencyMul;
						if (f > 1.0F)
							f = 1.0F;
						f1 = f;
						f2 = f;
					} else {
						f = af24[k2];
						f1 = af25[k2];
						f2 = af26[k2];
					}
				boolean flag8 = texture.getOpenGLID(super.myID) != super.lastTexture;
				if (super.lastCoords != super.veryLastCoords)
					enableVertexArrays(super.lastCoords, super.veryLastCoords);
				super.veryLastCoords = super.lastCoords;
				super.lastCoords = 1;
				if (flag7) {
					for (int j3 = 1; j3 < i3 && awtbufferlist.multiTextures[j3 - 1][k2] != null; j3++)
						super.lastCoords++;

				}
				if (!flag8) {
					flag8 |= texture.getOpenGLID(super.myID) == 0
							|| texture.getMarker(super.myID) == Texture.MARKER_DELETE_AND_UPLOAD;
					flag8 |= super.lastCoords != super.veryLastCoords;
					flag8 |= i1 != -1 && i3 != ai[i1];
					flag8 |= j1 != k1;
				}
				if (!flag8 && flag7) {
					for (int k3 = 1; k3 < i3; k3++) {
						Texture texture1 = awtbufferlist.multiTextures[k3 - 1][k2];
						if (texture1 != null) {
							if (texture1.getOpenGLID(super.myID) == super.lastMultiTextures[k3]
									&& awtbufferlist.multiMode[k3 - 1][k2] == super.lastMultiModes[k3]
									&& texture1.getOpenGLID(super.myID) != 0
									&& texture1.getMarker(super.myID) != Texture.MARKER_DELETE_AND_UPLOAD)
								continue;
							flag8 = true;
							break;
						}
						if (super.lastMultiTextures[k3] == -1 || super.lastMultiTextures[k3] == -9999)
							continue;
						flag8 = true;
						break;
					}

				}
				if (flag4 && super.curPos != 0 || flag6 != super.wasTransparent || l2 != super.lastTransMode || flag8
						|| super.curPos >= 997) {
					if (k2 != 0) {
						if (flag) {
							disableCompiledPipeline();
							renableVertexArrays();
							flag = false;
						}
						if (super.wasTransparent) {
							setBlendingMode(super.lastTransMode);
							if (j1 == 0)
								setDepthBuffer();
							flag2 = true;
						}
						if (j1 > 0) {
							int l3 = super.lastMultiModes[1];
							GL11.glEnable(3042);
							GL11.glBlendFunc(GLBase.blendSrcMap[l3], GLBase.blendDstMap[l3]);
							if (i2 != 1)
								setRGBScaling(1);
							GL11.glDepthMask(false);
							GL11.glDepthFunc(514);
							flag3 = true;
						}
						setTextures(awtbufferlist, l, cam);
						renderVertexArray(super.curPos);
					}
					super.wasTransparent = flag6;
					super.lastTransMode = l2;
					super.curPos = 0;
					super.colPos = 0;
					super.texPos = 0;
					super.vertPos = 0;
					for (int i4 = 0; i4 < super.mtTexPos.length; i4++)
						super.mtTexPos[i4] = 0;

					if (k1 > 0)
						super.lastMultiModes[1] = awtbufferlist.multiMode[0][k2];
					super.lastTexture = texture.getOpenGLID(super.myID);
					for (int j4 = 1; j4 < i3; j4++) {
						Texture texture2 = awtbufferlist.multiTextures[j4 - 1][k2];
						if (texture2 != null) {
							super.lastMultiTextures[j4] = texture2.getOpenGLID(super.myID);
							super.lastMultiModes[j4] = awtbufferlist.multiMode[j4 - 1][k2];
						} else {
							super.lastMultiTextures[j4] = -9999;
							super.lastMultiModes[j4] = -9999;
						}
					}

				}
				if (flag4) {
					if (flag2 || flag3) {
						super.blending = false;
						unsetBlendingMode();
						GL11.glDepthMask(true);
						flag2 = false;
						if (flag3) {
							flag3 = false;
							GL11.glDepthFunc(515);
							if (i2 != 1)
								setRGBScaling(i2);
						}
					}
					if (!flag)
						enableCompiledPipeline();
					if (flag6) {
						if (!super.blending)
							setBlendingMode(l2);
						flag2 = true;
						if (k1 == 0) {
							if (!super.blending)
								setDepthBuffer();
							if (k2 < l1 && awtbufferlist.r3[k2 + 1] == -1000000F && aflag[k2 + 1] == flag6
									&& awtbufferlist.transValue[k2 + 1] >> 16 == l2) {
								flag2 = false;
								super.blending = true;
							}
						}
					}
					if (k1 > 0) {
						int k4 = awtbufferlist.multiMode[0][k2];
						GL11.glEnable(3042);
						GL11.glBlendFunc(GLBase.blendSrcMap[k4], GLBase.blendDstMap[k4]);
						if (i2 != 1)
							setRGBScaling(1);
						GL11.glDepthMask(false);
						GL11.glDepthFunc(514);
						flag3 = true;
					}
					setTextures(awtbufferlist, k2, cam);
					af28[0] = awtbufferlist.r1[k2] * GLBase.COLOR_INV;
					af28[1] = awtbufferlist.g1[k2] * GLBase.COLOR_INV;
					af28[2] = awtbufferlist.b1[k2] * GLBase.COLOR_INV;
					af28[3] = f;
					flag = true;
					CompiledInstance compiledinstance = (CompiledInstance) awtbufferlist.cis.get((int) awtbufferlist.r2[k2]);
					Object3D object3d1 = compiledinstance.obj;
					if (object3d1 != object3d) {
						object3d = object3d1;
						matrix = (Matrix) awtbufferlist.obj2Matrix.get(object3d1);
					}
					compiledinstance.render(super.myID, this, super.ambientBuffer, af28, super.renderTarget != null
							&& super.renderTarget.isShadowMap, cam, (float[][]) awtbufferlist.lights[k2], false, matrix);
					super.curPos = 0;
					super.colPos = 0;
					super.texPos = 0;
					for (int l5 = 0; l5 < super.mtTexPos.length; l5++)
						super.mtTexPos[l5] = 0;

					super.vertPos = 0;
				} else {
					super.colors.put(super.colPos, af[k2] * GLBase.COLOR_INV);
					super.colors.put(super.colPos + 1, af3[k2] * GLBase.COLOR_INV);
					super.colors.put(super.colPos + 2, af6[k2] * GLBase.COLOR_INV);
					super.colors.put(super.colPos + 3, f);
					super.colPos += 4;
					super.vertices.put(super.vertPos, af9[k2]);
					super.vertices.put(super.vertPos + 1, -af12[k2]);
					super.vertices.put(super.vertPos + 2, -af15[k2]);
					super.vertPos += 3;
					if (flag5) {
						super.textures.put(super.texPos, af21[k2]);
						super.textures.put(super.texPos + 1, af18[k2]);
						super.texPos += 2;
					}
					if (flag7) {
						for (int l4 = 1; l4 < i3; l4++) {
							int k5 = l4 - 1;
							if (awtbufferlist.multiTextures[k5][k2] != null && awtbufferlist.multiTextures[k5][k2].projector == null) {
								FloatBuffer floatbuffer = super.multiTextures[l4];
								int i6 = super.mtTexPos[l4];
								floatbuffer.put(i6, awtbufferlist.u1Mul[k5][k2]);
								floatbuffer.put(i6 + 1, awtbufferlist.v1Mul[k5][k2]);
								floatbuffer.put(i6 + 2, awtbufferlist.u2Mul[k5][k2]);
								floatbuffer.put(i6 + 3, awtbufferlist.v2Mul[k5][k2]);
								floatbuffer.put(i6 + 4, awtbufferlist.u3Mul[k5][k2]);
								floatbuffer.put(i6 + 5, awtbufferlist.v3Mul[k5][k2]);
								super.mtTexPos[l4] += 6;
							}
						}

					}
					super.curPos++;
					super.colors.put(super.colPos, af1[k2] * GLBase.COLOR_INV);
					super.colors.put(super.colPos + 1, af4[k2] * GLBase.COLOR_INV);
					super.colors.put(super.colPos + 2, af7[k2] * GLBase.COLOR_INV);
					super.colors.put(super.colPos + 3, f1);
					super.colPos += 4;
					super.vertices.put(super.vertPos, af10[k2]);
					super.vertices.put(super.vertPos + 1, -af13[k2]);
					super.vertices.put(super.vertPos + 2, -af16[k2]);
					super.vertPos += 3;
					if (flag5) {
						super.textures.put(super.texPos, af22[k2]);
						super.textures.put(super.texPos + 1, af19[k2]);
						super.texPos += 2;
					}
					super.curPos++;
					super.colors.put(super.colPos, af2[k2] * GLBase.COLOR_INV);
					super.colors.put(super.colPos + 1, af5[k2] * GLBase.COLOR_INV);
					super.colors.put(super.colPos + 2, af8[k2] * GLBase.COLOR_INV);
					super.colors.put(super.colPos + 3, f2);
					super.colPos += 4;
					super.vertices.put(super.vertPos, af11[k2]);
					super.vertices.put(super.vertPos + 1, -af14[k2]);
					super.vertices.put(super.vertPos + 2, -af17[k2]);
					super.vertPos += 3;
					if (flag5) {
						super.textures.put(super.texPos, af23[k2]);
						super.textures.put(super.texPos + 1, af20[k2]);
						super.texPos += 2;
					}
					super.curPos++;
					if (l1 == k2) {
						if (flag) {
							disableCompiledPipeline();
							renableVertexArrays();
							flag = false;
						}
						if (flag6) {
							setBlendingMode(l2);
							if (k1 == 0)
								setDepthBuffer();
							super.wasTransparent = flag6;
							super.lastTransMode = l2;
							flag2 = true;
						}
						if (k1 > 0) {
							int i5 = awtbufferlist.multiMode[0][k2];
							GL11.glEnable(3042);
							GL11.glBlendFunc(GLBase.blendSrcMap[i5], GLBase.blendDstMap[i5]);
							if (i2 != 1)
								setRGBScaling(1);
							GL11.glDepthMask(false);
							GL11.glDepthFunc(514);
							flag3 = true;
						}
						setTextures(awtbufferlist, k2, cam);
						renderVertexArray(super.curPos);
						super.curPos = 0;
						super.colPos = 0;
						super.texPos = 0;
						super.vertPos = 0;
						for (int j5 = 0; j5 < super.mtTexPos.length; j5++)
							super.mtTexPos[j5] = 0;

					}
				}
				if (!flag2 && !flag3)
					continue;
				super.blending = false;
				unsetBlendingMode();
				GL11.glDepthMask(true);
				if (!flag3)
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
			canvas.add(vislist, i, i, 5);
			canvas.setColor(j);
		}
	}

	void drawWireframe(AWTDisplayList awtdisplaylist, int i) {
		if (super.init) {
			endState();
			disableBlitting();
			GL11.glPolygonMode(1032, 6913);
			GL11.glDisable(2929);
			GL11.glDisable(3553);
			disableAllHigherStages();
			beginState(4);
			int j = awtdisplaylist.getColor();
			int k = j >> 16 & 0xff;
			int l = j >> 8 & 0xff;
			int i1 = j & 0xff;
			int j1 = awtdisplaylist.start[i];
			AWTBufferList awtbufferlist = awtdisplaylist.vl[i].getFrontBuffer();
			GL11.glColor3f((float) k / 255F, (float) l / 255F, (float) i1 / 255F);
			int k1 = awtbufferlist.stageCnt[j1];
			boolean flag = false;
			if (awtbufferlist.r3[j1] == -1000000F)
				flag = true;
			if (!flag) {
				GL11.glVertex3f(awtbufferlist.x1[j1], -awtbufferlist.y1[j1], -awtbufferlist.z1[j1]);
				GL11.glVertex3f(awtbufferlist.x2[j1], -awtbufferlist.y2[j1], -awtbufferlist.z2[j1]);
				GL11.glVertex3f(awtbufferlist.x3[j1], -awtbufferlist.y3[j1], -awtbufferlist.z3[j1]);
			} else {
				CompiledInstance compiledinstance = (CompiledInstance) awtbufferlist.cis.get((int) awtbufferlist.r2[j1]);
				compiledinstance.render(super.myID, this, null, null, false, cam, (float[][]) null, true,
						(Matrix) awtbufferlist.obj2Matrix.get(compiledinstance.obj));
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

	private void setScale(float f, float f1) {
		super.scaleX = f;
		super.scaleY = f1;
	}

	private void setTextureProjector(Projector projector, Texture texture) {
		texture.projectorBuffer = projector;
	}

	private void bufferTextureProjectors() {
		Texture atexture[] = super.texMan.getTextures();
		for (int i = 0; i < atexture.length; i++) {
			Texture texture = atexture[i];
			if (texture == null)
				continue;
			Projector projector = texture.projector;
			if (projector != null && texture.enabled) {
				Projector projector1 = new Projector();
				projector1.setBack(projector.getBack().cloneMatrix());
				projector1.setPosition(projector.getPosition());
				projector1.setFOVLimits(projector.getMinFOV(), projector.getMaxFOV());
				projector1.setFOV(projector.getFOV());
				projector1.setYFOV(projector.getYFOV());
				Object aobj[] = new Object[2];
				aobj[0] = projector1;
				aobj[1] = texture;
				canvas.add(22, ((Object) (aobj)));
				continue;
			}
			if (projector == null)
				texture.projectorBuffer = null;
		}

	}

	private Texture setTextures(AWTBufferList awtbufferlist, int i, Camera camera) {
		boolean flag = false;
		Texture texture = awtbufferlist.texture[i];
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
		bindAndProject(0, texture, camera);
		if (awtbufferlist.multi[i]) {
			if (super.maxStages < awtbufferlist.maxStages[i]) {
				super.maxStages = awtbufferlist.maxStages[i];
				if (super.maxStages > super.minDriverAndConfig)
					super.maxStages = super.minDriverAndConfig;
			}
			for (int j = 1; j < super.maxStages; j++) {
				int k = j - 1;
				Texture texture1 = awtbufferlist.multiTextures[k][i];
				if (texture1 != null) {
					flag |= texture1.isShadowMap;
					int l = GLBase.modeMap[awtbufferlist.multiMode[k][i]];
					if (!super.stageInitialized[j])
						initTextureStage(j, l);
					else
						switchTextureMode(j, l);
					int i1 = texture1.getOpenGLID(super.myID);
					if (i1 == 0 || texture1.getMarker(super.myID) == Texture.MARKER_DELETE_AND_UPLOAD) {
						texture1.setMarker(super.myID, Texture.MARKER_NOTHING);
						endState();
						if (texture1 != super.renderTarget) {
							if (i1 != 0)
								removeTexture(texture1);
							convertTexture(texture1);
						}
						super.lastTextures[j] = -1;
					}
					bindAndProject(j, texture1, camera);
				} else {
					disableStage(j);
				}
			}

		} else {
			disableUnusedStages();
		}
		if (!flag)
			disableGlobalAlphaTest();
		return texture;
	}

	protected IThreadBuffer canvas;
	private boolean realCanvas;
	private boolean forceFOV;
	private boolean aboutToDispose;
	private Camera cam;
	private int ambient[];
}
