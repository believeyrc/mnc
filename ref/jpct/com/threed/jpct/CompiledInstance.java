// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.threed.jpct;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL11;

// Referenced classes of package com.threed.jpct:
//            Matrix, GLBase, IRenderer, ICompiledInstance,
//            Object3D, Logger, IRenderHook, World,
//            Lights, BufferedMatrix, Config, TextureManager,
//            Texture, Mesh, Vectors, Camera

class CompiledInstance implements ICompiledInstance {

	CompiledInstance(Object3D object3d, int i, int j) {
		useDL = true;
		dynamic = false;
		floatBuffer64 = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asFloatBuffer();
		floatBuffer16_2 = ByteBuffer.allocateDirect(16).order(ByteOrder.nativeOrder()).asFloatBuffer();
		floatBuffer16_3 = ByteBuffer.allocateDirect(16).order(ByteOrder.nativeOrder()).asFloatBuffer();
		ZEROS_ONE.put(0.0F);
		ZEROS_ONE.put(0.0F);
		ZEROS_ONE.put(0.0F);
		ZEROS_ONE.put(1.0F);
		obj = null;
		normals = null;
		vertices = null;
		colors = null;
		indices = null;
		multiTextures = null;
		tris = new ArrayList();
		cnt = 0;
		polyIndex = 0;
		endStage = 0;
		indexed = true;
		staticUV = true;
		treeID = -1;
		m3 = new Matrix();
		mat = new Matrix();
		mo = new Matrix();
		key = null;
		listID = 0;
		tex0 = -1;
		tex1 = -1;
		vertex2index = null;
		rendererID = 0xc4653601;
		lastRenderer = null;
		filled = false;
		m3mat = m3.mat[3];
		obj = object3d;
		polyIndex = i;
		treeID = j;
		dynamic = object3d.dynamic;
		useDL = !dynamic && object3d.preferDL;
		indexed = object3d.indexed;
		staticUV = object3d.staticUV;
	}

	void setKey(String s) {
		key = s;
	}

	public String getKey() {
		return key;
	}

	public int getTreeID() {
		return treeID;
	}

	public int getPolyIndex() {
		return polyIndex;
	}

	public int getStageCount() {
		return endStage;
	}

	private void dispose(IRenderer irenderer) {
		synchronized (SYNC) {
			if (listID != 0)
				try {
					if (irenderer instanceof GLBase) {
						((GLBase) irenderer).remove(listID);
						listID = 0;
					}
				} catch (Throwable throwable) {
					Logger.log("Failed to delete display list: " + listID, 1);
				}
			if (obj.renderHook != null)
				obj.renderHook.onDispose();
		}
	}

	void copy(CompiledInstance compiledinstance) {
		listID = compiledinstance.listID;
		colors = compiledinstance.colors;
		tris = compiledinstance.tris;
		normals = compiledinstance.normals;
		indices = compiledinstance.indices;
		vertices = compiledinstance.vertices;
		multiTextures = compiledinstance.multiTextures;
		vertex2index = compiledinstance.vertex2index;
		polyIndex = compiledinstance.polyIndex;
		useDL = compiledinstance.useDL;
		dynamic = compiledinstance.dynamic;
		cnt = compiledinstance.cnt;
		endStage = compiledinstance.endStage;
		indexed = compiledinstance.indexed;
		staticUV = compiledinstance.staticUV;
		treeID = compiledinstance.treeID;
		key = compiledinstance.key;
	}

	void render(int paramInt, GLBase paramGLBase, FloatBuffer paramFloatBuffer, float[] paramArrayOfFloat,
			boolean paramBoolean1, Camera paramCamera, float[][] paramArrayOfFloat1, boolean paramBoolean2, Matrix paramMatrix) {
		synchronized (SYNC) {
			if (paramInt != lastGlobalRenderer) {
				lastObj = null;
				lastVertexBuffer = null;
				chkSum = 0.0D;
				lastLightCnt = 0;
				lastGlobalRenderer = paramInt;
			}
			if ((this.obj.shareWith != null) && (!(this.obj.sharing))) {
				this.obj.sharing = true;
				if (this.obj.compiled.size() != this.obj.shareWith.compiled.size()) {
					Logger.log("Number of compiled instances don't match...can't share data!", 0);
					return;
				}
				for (int i = 0; i < this.obj.compiled.size(); ++i) {
					CompiledInstance localCompiledInstance1 = (CompiledInstance) this.obj.compiled.get(i);
					CompiledInstance localCompiledInstance2 = (CompiledInstance) this.obj.shareWith.compiled.get(i);
					localCompiledInstance1.copy(localCompiledInstance2);
				}
				this.floatBuffer16_3.put(ALL_ONES);
				this.floatBuffer16_3.put(1.0F);
				this.floatBuffer16_3.rewind();
				this.filled = true;
				Logger.log("Object '" + this.obj.getName() + "' shares compiled data with object '"
						+ this.obj.shareWith.getName() + "'", 2);
			}
			if (!(this.filled)) {
				new RuntimeException().printStackTrace();
				Logger.log("Internal error, please report: render() called on an uncompiled object (" + this.obj.getName()
						+ ")!", 0);
				return;
			}
			World localWorld = this.obj.myWorld;
			if ((localWorld == null) || ((this.useDL) && (this.listID == 0)))
				return;
			int j = localWorld.lights.lightCnt;
			boolean bool = this.obj.hasVertexAlpha();
			paramGLBase.endState();
			if (paramFloatBuffer != null)
				paramFloatBuffer.rewind();
			if (this.rendererID == -999999999) {
				this.rendererID = paramInt;
			} else if ((this.rendererID != paramInt) && (this.useDL)) {
				if ((this.lastRenderer != null) && (!(this.obj.sharing)))
					dispose(this.lastRenderer);
				this.listID = 0;
				Logger.log("OpenGL context has changed...trying to recover...", 1);
			}
			this.lastRenderer = ((IRenderer) paramGLBase);
			float[] arrayOfFloat1 = null;
			int k = (this.obj.compiled.size() > 1) ? 1 : 0;
			if (k != 0)
				arrayOfFloat1 = (float[]) paramGLBase.matrixCache.get(this.obj);
			if (arrayOfFloat1 == null) {
				this.m3mat[0] = (-paramCamera.backBx);
				this.m3mat[1] = (-paramCamera.backBy);
				this.m3mat[2] = (-paramCamera.backBz);
				if (paramMatrix != null)
					this.mo.setTo(paramMatrix);
				else
					this.mo.setTo(this.obj.transBuffer);
				this.mat.setTo(paramCamera.getBack());
				this.mat.rotateX(mpi);
				this.mo.matMul(this.m3);
				this.mo.matMul(this.mat);
				arrayOfFloat1 = this.mo.getDump();
				if (k != 0)
					paramGLBase.matrixCache.put(this.obj, arrayOfFloat1);
			}
			this.floatBuffer64.put(arrayOfFloat1);
			this.floatBuffer64.rewind();
			GL11.glMatrixMode(5888);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			int l = 0;
			if (this.obj.doCulling)
				if (this.obj.reverseCulling) {
					GL11.glCullFace(1028);
					l = 1;
				} else
					GL11.glDisable(2884);
			int i3;
			if (!(paramBoolean2)) {
				if (bool)
					paramArrayOfFloat[3] = 1.0F;
				this.floatBuffer16_2.put(paramArrayOfFloat);
				this.floatBuffer16_2.rewind();
				GL11.glLightModel(2899, paramFloatBuffer);
				GL11.glMaterial(1032, 5632, this.floatBuffer16_2);
				double d = 0.0D;
				float[] arrayOfFloat2;
				for (i3 = 0; (i3 < j) && (i3 < 8); ++i3) {
					arrayOfFloat2 = paramArrayOfFloat1[i3];
					if (arrayOfFloat2[0] == -9999.0F)
						break;
					d += (i3 + 0.7181000113487244D) * arrayOfFloat2[0];
					d += (i3 + 1.100000023841858D) * arrayOfFloat2[1];
					d += (i3 + 2.240000009536743D) * arrayOfFloat2[2];
					d += (i3 + 3.566999912261963D) * arrayOfFloat2[3];
					d += (i3 + 9.727199554443359D) * (arrayOfFloat2[4] + 1.4F * arrayOfFloat2[5] + 4.56F * arrayOfFloat2[6]);
				}
				if (bool) {
					GL11.glColorMaterial(1032, 4609);
					GL11.glEnable(2903);
				}
				if ((chkSum != d) || (lastObj != this.obj)) {
					GL11.glMaterial(1032, 4608, this.floatBuffer16_3);
					this.floatBuffer16_2.put(ALL_ONES);
					this.floatBuffer16_2.rewind();
					GL11.glMaterial(1032, 4609, this.floatBuffer16_2);
					GL11.glMaterial(1032, 4610, this.floatBuffer16_2);
					GL11.glLightModeli(2897, 1);
					if (j < lastLightCnt)
						for (i3 = 0; i3 < lastLightCnt; ++i3)
							GL11.glDisable(LIGHTS[i3]);
					lastLightCnt = 0;
					for (i3 = 0; (i3 < j) && (i3 < 8); ++i3) {
						arrayOfFloat2 = paramArrayOfFloat1[i3];
						int i5 = LIGHTS[i3];
						if (arrayOfFloat2[0] == -9999.0F) {
							GL11.glDisable(i5);
						} else {
							lastLightCnt += 1;
							int i6 = ((chkSum != d) || (lastObj == null)) ? 1 : 0;
							if (i6 != 0) {
								GL11.glEnable(i5);
								this.floatBuffer16_2.put(arrayOfFloat2[1]);
								this.floatBuffer16_2.put(arrayOfFloat2[2]);
								this.floatBuffer16_2.put(arrayOfFloat2[3]);
								this.floatBuffer16_2.put(1.0F);
								this.floatBuffer16_2.rewind();
								GL11.glLight(i5, 4611, this.floatBuffer16_2);
								this.floatBuffer16_2.put(arrayOfFloat2[4]);
								this.floatBuffer16_2.put(arrayOfFloat2[5]);
								this.floatBuffer16_2.put(arrayOfFloat2[6]);
								this.floatBuffer16_2.put(1.0F);
								this.floatBuffer16_2.rewind();
								GL11.glLight(i5, 4609, this.floatBuffer16_2);
								this.ZEROS_ONE.rewind();
								GL11.glLight(i5, 4608, this.ZEROS_ONE);
								if (arrayOfFloat2[0] >= 0.0F) {
									float f = arrayOfFloat2[0];
									if (f == 0.0F)
										f = 0.001F;
									GL11.glLightf(i5, 4616, 4.0F / f);
								} else {
									GL11.glLightf(i5, 4616, 0.0F);
								}
							}
							if (!(this.obj.doSpecularLighting)) {
								this.ZEROS_ONE.rewind();
								GL11.glLight(i5, 4610, this.ZEROS_ONE);
							} else {
								GL11.glMateriali(1032, 5633, (int) (Config.specPow / 2.0F));
								if (i6 == 0) {
									this.floatBuffer16_2.put(arrayOfFloat2[4]);
									this.floatBuffer16_2.put(arrayOfFloat2[5]);
									this.floatBuffer16_2.put(arrayOfFloat2[6]);
									this.floatBuffer16_2.put(1.0F);
									this.floatBuffer16_2.rewind();
								}
								GL11.glLight(i5, 4610, this.floatBuffer16_2);
							}
						}
					}
					chkSum = d;
					lastObj = this.obj;
				}
				if (this.obj.isFlatShaded)
					GL11.glShadeModel(7424);
			} else {
				GL11.glDisable(2896);
			}
			GL11.glLoadMatrix(this.floatBuffer64);
			if (paramBoolean1) {
				GL11.glMatrixMode(5889);
				GL11.glPushMatrix();
				GL11.glTranslatef(0.0F, 0.0F, -Config.glShadowZBias);
			}
			int i1 = 0;
			if ((!(this.useDL)) || (this.listID == 0)) {
				if (lastVertexBuffer != this.vertices) {
					lastVertexBuffer = this.vertices;
					GL11.glNormalPointer(12, this.normals);
					GL11.glVertexPointer(3, 12, this.vertices);
					GL11.glEnableClientState(32885);
					GL11.glEnableClientState(32884);
					if (!(bool)) {
						GL11.glDisableClientState(32886);
						i1 = 1;
					} else {
						GL11.glColorPointer(4, 16, this.colors);
						GL11.glEnableClientState(32886);
						i1 = 0;
					}
					for (int i2 = 0;; ++i2) {
						if (i2 >= this.endStage)
							break;// /oooo
						ARBMultitexture.glClientActiveTextureARB(stageMap[i2]);
						GL11.glEnableClientState(32888);
						GL11.glTexCoordPointer(2, 8, this.multiTextures[i2]);
					}
				}
				if (!(bool)) {
					GL11.glDisableClientState(32886);
					i1 = 1;
				} else {
					GL11.glEnableClientState(32886);
					i1 = 0;
				}
			}
			if ((!(paramBoolean2)) && (this.obj.isEnvmapped)
					&& (((!(Config.glForceEnvMapToSecondStage)) || (this.endStage > 1)))) {
				TextureManager localObject1 = TextureManager.getInstance();
				if (this.tex0 == -1)
					this.tex0 = ((TextureManager) localObject1).getTextureByID(this.obj.texture[this.polyIndex]).getOpenGLID(
							paramInt);
				if ((Config.glForceEnvMapToSecondStage) && (this.tex1 == -1) && (this.endStage > 1))
					this.tex1 = ((TextureManager) localObject1).getTextureByID(this.obj.multiTex[0][this.polyIndex]).getOpenGLID(
							paramInt);
				i3 = this.tex0;
				int i4 = 0;
				if (Config.glForceEnvMapToSecondStage) {
					i3 = this.tex1;
					i4 = 1;
				}
				paramGLBase.bindTexture(i4, i3, false);
				GL11.glTexGeni(8192, 9472, 9218);
				GL11.glTexGeni(8193, 9472, 9218);
				GL11.glEnable(3168);
				GL11.glEnable(3169);
			}
			Object localObject1 = this.obj.renderHook;
			if (localObject1 != null)
				((IRenderHook) localObject1).beforeRendering(this.polyIndex);
			do
				if ((this.useDL) && (this.listID != 0))
					GL11.glCallList(this.listID);
				else if (this.indexed)
					GL11.glDrawElements(4, this.indices);
				else
					GL11.glDrawArrays(4, 0, this.cnt);
			while ((localObject1 != null) && (((IRenderHook) localObject1).repeatRendering()));
			if (localObject1 != null)
				((IRenderHook) localObject1).afterRendering(this.polyIndex);
			GL11.glMatrixMode(5888);
			GL11.glPopMatrix();
			if (paramBoolean1) {
				GL11.glMatrixMode(5889);
				GL11.glPopMatrix();
			}
			if (bool)
				GL11.glDisable(2903);
			if (i1 != 0)
				GL11.glEnableClientState(32886);
			if (l != 0)
				GL11.glCullFace(1029);
			if (this.obj.isEnvmapped) {
				GL11.glDisable(3168);
				GL11.glDisable(3169);
			}
			if (this.obj.isFlatShaded)
				GL11.glShadeModel(7425);
			if (paramBoolean2)
				GL11.glEnable(2896);
			if (!(this.obj.doCulling))
				GL11.glEnable(2884);
		}
	}

	void add(int i) {
		tris.add(new Integer(i));
	}

	public void error(Exception exception) {
		Logger.log("Error while compiling instance!", 0);
		exception.printStackTrace();
	}

	public void compileToDL() {
		synchronized (SYNC) {
			GL11.glNormalPointer(12, normals);
			GL11.glVertexPointer(3, 12, vertices);
			GL11.glEnableClientState(32885);
			GL11.glEnableClientState(32884);
			if (!obj.hasVertexAlpha()) {
				GL11.glDisableClientState(32886);
			} else {
				GL11.glColorPointer(4, 16, colors);
				GL11.glEnableClientState(32886);
			}
			for (int i = 0; i < endStage; i++) {
				ARBMultitexture.glClientActiveTextureARB(stageMap[i]);
				GL11.glEnableClientState(32888);
				GL11.glTexCoordPointer(2, 8, multiTextures[i]);
			}

			GL11.glFlush();
			GL11.glFinish();
			int j = GL11.glGenLists(1);
			GL11.glNewList(j, 4864);
			if (indexed)
				GL11.glDrawElements(4, indices);
			else
				GL11.glDrawArrays(4, 0, cnt);
			GL11.glEndList();
			GL11.glFlush();
			GL11.glFinish();
			listID = j;
			normals = null;
			vertices = null;
			colors = null;
			multiTextures = null;
			indices = null;
			GL11.glEnableClientState(32886);
		}
	}

	public void fill() {
		if (obj.shareWith != null)
			return;
		synchronized (SYNC) {
			long l = System.currentTimeMillis();
			if (tris != null) {
				int i = tris.size() * 3;
				boolean flag = false;
				if (normals == null) {
					flag = true;
					normals = ByteBuffer.allocateDirect(i * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
					vertices = ByteBuffer.allocateDirect(i * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
					if (obj.hasVertexAlpha())
						colors = ByteBuffer.allocateDirect(i * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
					if (indexed) {
						indices = ByteBuffer.allocateDirect(i * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
						vertex2index = new HashMap();
					}
					int j = 1;
					if (obj.multiTex != null) {
						for (int k = 0; k < obj.multiTex.length && obj.multiTex[k][polyIndex] != -1; k++)
							j++;

					}
					endStage = j;
					if (Config.glOverrideStageCount != -1 && Config.glOverrideStageCount < endStage)
						endStage = Config.glOverrideStageCount;
					if (endStage > Config.glStageCount)
						endStage = Config.glStageCount;
					multiTextures = new FloatBuffer[endStage];
					for (int i1 = 0; i1 < endStage; i1++)
						multiTextures[i1] = ByteBuffer.allocateDirect(i * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

				}
				Vectors vectors = obj.objVectors;
				Mesh mesh = obj.objMesh;
				int ai[][] = mesh.points;
				float af[] = mesh.xOrg;
				float af1[] = mesh.yOrg;
				float af2[] = mesh.zOrg;
				float af3[] = mesh.nxOrg;
				float af4[] = mesh.nyOrg;
				float af5[] = mesh.nzOrg;
				float af6[] = vectors.nuOrg;
				float af13[] = vectors.nvOrg;
				cnt = 0;
				if (vertex2index != null)
					vertex2index.clear();
				for (int j1 = 0; j1 < tris.size(); j1++) {
					int l1 = ((Integer) tris.get(j1)).intValue();
					label0: for (int j2 = 0; j2 < 3; j2++) {
						int l2 = ai[l1][j2];
						int k3 = mesh.coords[l2];
						if (!indexed) {
							vertices.put(af[k3]);
							vertices.put(af1[k3]);
							vertices.put(af2[k3]);
							normals.put(af3[k3]);
							normals.put(af4[k3]);
							normals.put(af5[k3]);
							if (colors != null) {
								colors.put(0.0F);
								colors.put(0.0F);
								colors.put(0.0F);
								colors.put(vectors.alpha[l2]);
							}
							if (!flag && staticUV)
								continue;
							int l3 = 0;
							do {
								if (l3 >= endStage)
									continue label0;
								if (l3 == 0) {
									float af7[] = vectors.nuOrg;
									float af14[] = vectors.nvOrg;
									multiTextures[l3].put(af7[l2]);
									multiTextures[l3].put(af14[l2]);
								} else if (obj.maxStagesUsed > 1) {
									float af8[] = vectors.uMul[l3 - 1];
									float af15[] = vectors.vMul[l3 - 1];
									multiTextures[l3].put(af8[l2]);
									multiTextures[l3].put(af15[l2]);
								}
								l3++;
							} while (true);
						}
						String s = af[k3] + "_" + af1[k3] + "_" + af2[k3] + "_";
						for (int i4 = 0; i4 < endStage; i4++) {
							if (i4 == 0) {
								float af9[] = vectors.nuOrg;
								float af16[] = vectors.nvOrg;
								s = s + af9[l2] + "_" + af16[l2] + "_";
								continue;
							}
							if (obj.maxStagesUsed > 1) {
								float af10[] = vectors.uMul[i4 - 1];
								float af17[] = vectors.vMul[i4 - 1];
								s = s + af10[l2] + "_" + af17[l2] + "_";
							}
						}

						if (colors != null)
							s = s + vectors.alpha[l2] + "_";
						Integer integer = (Integer) vertex2index.get(s);
						if (integer == null) {
							vertices.put(af[k3]);
							vertices.put(af1[k3]);
							vertices.put(af2[k3]);
							normals.put(af3[k3]);
							normals.put(af4[k3]);
							normals.put(af5[k3]);
							if (colors != null) {
								colors.put(0.0F);
								colors.put(0.0F);
								colors.put(0.0F);
								colors.put(vectors.alpha[l2]);
							}
							if (flag || !staticUV) {
								for (int j4 = 0; j4 < endStage; j4++) {
									if (j4 == 0) {
										float af11[] = vectors.nuOrg;
										float af18[] = vectors.nvOrg;
										multiTextures[j4].put(af11[l2]);
										multiTextures[j4].put(af18[l2]);
										continue;
									}
									if (obj.maxStagesUsed > 1) {
										float af12[] = vectors.uMul[j4 - 1];
										float af19[] = vectors.vMul[j4 - 1];
										multiTextures[j4].put(af12[l2]);
										multiTextures[j4].put(af19[l2]);
									}
								}

							}
							int k4 = (vertices.position() - 3) / 3;
							vertex2index.put(s, new Integer(k4));
							indices.put(k4);
						} else {
							indices.put(integer.intValue());
						}
					}

					cnt += 3;
				}

				int k1 = vertices.position();
				int i2 = multiTextures[0].position();
				int k2 = 0;
				if (colors != null) {
					k2 = colors.position();
					colors.rewind();
				}
				vertices.rewind();
				normals.rewind();
				floatBuffer16_3.put(ALL_ONES);
				floatBuffer16_3.put(1.0F);
				floatBuffer16_3.rewind();
				for (int i3 = 0; i3 < endStage; i3++)
					multiTextures[i3].rewind();

				if (indices != null)
					indices.rewind();
				if (flag) {
					if (!dynamic) {
						tris = null;
						vertex2index = null;
						if (indexed) {
							normals = copy(normals, k1);
							vertices = copy(vertices, k1);
							if (colors != null)
								colors = copy(colors, k2);
							for (int j3 = 0; j3 < endStage; j3++)
								multiTextures[j3] = copy(multiTextures[j3], i2);

						}
					}
					Logger.log("Subobject of object " + obj.getID() + "/" + obj.getName() + " compiled using " + cnt / 3
							+ " vertices in " + (System.currentTimeMillis() - l) + "ms!", 2);
				}
			}
			filled = true;
		}
	}

	public boolean isFilled() {
		synchronized (SYNC) {
			return this.filled;
		}
	}

	private FloatBuffer copy(FloatBuffer floatbuffer, int i) {
		FloatBuffer floatbuffer1 = ByteBuffer.allocateDirect(i * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		floatbuffer.rewind();
		for (int j = 0; j < i; j++)
			floatbuffer1.put(floatbuffer.get());

		floatbuffer1.rewind();
		return floatbuffer1;
	}

	public void finalize() {
		if (lastRenderer != null && !obj.sharing)
			dispose(lastRenderer);
	}

	private static final Object SYNC = new Object();
	private static final long serialVersionUID = 1L;
	public static Object3D lastObj = null;
	public static FloatBuffer lastVertexBuffer = null;
	public static double chkSum = 0.0D;
	public static int lastLightCnt = 0;
	public static int lastGlobalRenderer = -1;
	public boolean useDL;
	public boolean dynamic;
	protected static int stageMap[] = { 33984, 33985, 33986, 33987 };
	private static final float ALL_ONES[] = { 1.0F, 1.0F, 1.0F };
	private static final int LIGHTS[] = { 16384, 16385, 16386, 16387, 16388, 16389, 16390, 16391 };
	private static float mpi = 3.141593F;
	private FloatBuffer floatBuffer64;
	private FloatBuffer floatBuffer16_2;
	private FloatBuffer floatBuffer16_3;
	private final FloatBuffer ZEROS_ONE = ByteBuffer.allocateDirect(16).order(ByteOrder.nativeOrder()).asFloatBuffer();
	Object3D obj;
	private FloatBuffer normals;
	private FloatBuffer vertices;
	private FloatBuffer colors;
	private IntBuffer indices;
	private FloatBuffer multiTextures[];
	private List tris;
	private int cnt;
	private int polyIndex;
	private int endStage;
	private boolean indexed;
	private boolean staticUV;
	private int treeID;
	private Matrix m3;
	private Matrix mat;
	private Matrix mo;
	private String key;
	private int listID;
	private int tex0;
	private int tex1;
	private Map vertex2index;
	private int rendererID;
	private IRenderer lastRenderer;
	private boolean filled;
	private float m3mat[];

}
