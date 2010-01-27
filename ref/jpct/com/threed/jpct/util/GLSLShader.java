// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct.util;

import com.threed.jpct.*;
import java.nio.*;
import java.util.*;
import org.lwjgl.opengl.*;

// Referenced classes of package com.threed.jpct.util:
//            ShadowHelper

public class GLSLShader implements IRenderHook {

	public GLSLShader(String s, String s1) {
		fragSource = null;
		vertexSource = null;
		prg = 0;
		fragShade = 0;
		vertShade = 0;
		init = false;
		failure = false;
		excludeShadowProcess = true;
		helper = null;
		newUniforms = false;
		uniforms = new HashMap();
		toRemove = null;
		minShaderModel = -1;
		fragSource = s1;
		vertexSource = s;
	}

	public void setMinShaderModel(int i) {
		minShaderModel = i;
	}

	public synchronized void beforeRendering(int i) {
		if (!init)
			init();
		if (helper != null && excludeShadowProcess && helper.isRenderingShadowMap())
			return;
		if (!failure) {
			if (newUniforms) {
				Iterator iterator = uniforms.keySet().iterator();
				do {
					if (!iterator.hasNext())
						break;
					String s = (String) iterator.next();
					Object aobj1[] = (Object[]) uniforms.get(s);
					if (aobj1[1] == null) {
						int k = getLocation(s);
						aobj1[1] = new Integer(k);
					}
				} while (true);
				newUniforms = false;
			}
			ARBShaderObjects.glUseProgramObjectARB(prg);
			Iterator iterator1 = uniforms.values().iterator();
			do {
				if (!iterator1.hasNext())
					break;
				Object aobj[] = (Object[]) iterator1.next();
				int j = ((Integer) aobj[1]).intValue();
				Object obj = aobj[0];
				if (obj instanceof Integer)
					ARBShaderObjects.glUniform1iARB(j, ((Integer) obj).intValue());
				else if (obj instanceof Float)
					ARBShaderObjects.glUniform1fARB(j, ((Float) obj).floatValue());
				else if (obj instanceof SimpleVector) {
					SimpleVector simplevector = (SimpleVector) obj;
					ARBShaderObjects.glUniform3fARB(j, simplevector.x, simplevector.y, simplevector.z);
				} else if (obj instanceof float[]) {
					float af[] = (float[]) obj;
					switch (af.length) {
					case 1: // '\001'
						ARBShaderObjects.glUniform1fARB(j, af[0]);
						break;

					case 2: // '\002'
						ARBShaderObjects.glUniform2fARB(j, af[0], af[1]);
						break;

					case 3: // '\003'
						ARBShaderObjects.glUniform3fARB(j, af[0], af[1], af[2]);
						break;

					case 4: // '\004'
						ARBShaderObjects.glUniform4fARB(j, af[0], af[1], af[2], af[3]);
						break;
					}
				} else if (obj instanceof FloatBuffer) {
					FloatBuffer floatbuffer = (FloatBuffer) obj;
					floatbuffer.rewind();
					ARBShaderObjects.glUniformMatrix4ARB(j, false, floatbuffer);
				}
				if (aobj[2] != null) {
					if (toRemove == null)
						toRemove = new ArrayList();
					toRemove.add(aobj[2]);
				}
			} while (true);
			if (toRemove != null) {
				for (Iterator iterator2 = toRemove.iterator(); iterator2.hasNext(); uniforms.remove(iterator2.next()))
					;
				toRemove = null;
			}
		}
	}

	public void afterRendering(int i) {
		if (!failure && init)
			ARBShaderObjects.glUseProgramObjectARB(0);
	}

	public void onDispose() {
		if (!failure && init) {
			ARBShaderObjects.glDeleteObjectARB(fragShade);
			ARBShaderObjects.glDeleteObjectARB(vertShade);
			ARBShaderObjects.glDeleteObjectARB(prg);
			init = false;
		}
	}

	public boolean repeatRendering() {
		return false;
	}

	public void setShadowHelper(ShadowHelper shadowhelper) {
		helper = shadowhelper;
	}

	public void excludeShadowMap(boolean flag) {
		excludeShadowProcess = flag;
	}

	public void setStaticUniform(String s, int i) {
		setStatic(s, new Integer(i));
	}

	public void setStaticUniform(String s, float f) {
		setStatic(s, new Float(f));
	}

	public void setStaticUniform(String s, float af[]) {
		setStatic(s, af);
	}

	public void setStaticUniform(String s, SimpleVector simplevector) {
		setStatic(s, simplevector);
	}

	public void setStaticUniform(String s, Matrix matrix) {
		setStatic(s, toFloatBuffer(matrix));
	}

	public void setUniform(String s, int i) {
		set(s, new Integer(i));
	}

	public void setUniform(String s, float f) {
		set(s, new Float(f));
	}

	public void setUniform(String s, SimpleVector simplevector) {
		set(s, new SimpleVector(simplevector));
	}

	public void setUniform(String s, float af[]) {
		set(s, af);
	}

	public void setUniform(String s, Matrix matrix) {
		set(s, toFloatBuffer(matrix));
	}

	private FloatBuffer toFloatBuffer(Matrix matrix) {
		FloatBuffer floatbuffer = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asFloatBuffer();
		Matrix matrix1 = matrix.cloneMatrix();
		matrix1.rotateX(3.141593F);
		float af[] = matrix1.getDump();
		floatbuffer.put(af);
		floatbuffer.rewind();
		return floatbuffer;
	}

	private synchronized void set(String s, Object obj) {
		Object aobj[] = (Object[]) uniforms.get(s);
		if (aobj == null) {
			aobj = new Object[3];
			newUniforms = true;
			uniforms.put(s, ((Object) (aobj)));
		}
		aobj[0] = obj;
	}

	private synchronized void setStatic(String s, Object obj) {
		Object aobj[] = (Object[]) uniforms.get(s);
		if (aobj == null) {
			Object aobj1[] = new Object[3];
			aobj1[2] = s;
			newUniforms = true;
			uniforms.put(s, ((Object) (aobj1)));
			aobj1[0] = obj;
		}
	}

	private void init() {
		if (!GLContext.getCapabilities().GL_ARB_fragment_program) {
			Logger.log("This hardware/driver doesn't support shaders(1)!", 1);
			init = true;
			failure = true;
			return;
		}
		if (fragSource == null && vertexSource == null) {
			Logger.log("No shader specified!", 1);
			init = true;
			failure = true;
			return;
		}
		int i = guessShaderModel();
		if (minShaderModel > i && i != 9999 && minShaderModel != -1) {
			Logger.log("Shadermodel " + minShaderModel + " not supported on this platform!", 1);
			init = true;
			failure = true;
			return;
		}
		try {
			prg = ARBShaderObjects.glCreateProgramObjectARB();
		} catch (Throwable throwable) {
			Logger.log("This hardware/driver doesn't support shaders(2)!", 1);
			init = true;
			failure = true;
			return;
		}
		if (fragSource != null) {
			fragShade = ARBShaderObjects.glCreateShaderObjectARB(35632);
			ARBShaderObjects.glShaderSourceARB(fragShade, getSource(fragSource));
			ARBShaderObjects.glCompileShaderARB(fragShade);
			ARBShaderObjects.glAttachObjectARB(prg, fragShade);
			fragSource = null;
		}
		if (vertexSource != null) {
			vertShade = ARBShaderObjects.glCreateShaderObjectARB(35633);
			ARBShaderObjects.glShaderSourceARB(vertShade, getSource(vertexSource));
			ARBShaderObjects.glCompileShaderARB(vertShade);
			ARBShaderObjects.glAttachObjectARB(prg, vertShade);
			vertexSource = null;
		}
		ARBShaderObjects.glLinkProgramARB(prg);
		Logger.log("Shader compiled!", 2);
		String s = getLog();
		if (s != null)
			Logger.log(s, 0);
		init = true;
	}

	private ByteBuffer getSource(String s) {
		byte abyte0[] = s.getBytes();
		ByteBuffer bytebuffer = createByteBuffer(abyte0.length);
		bytebuffer.put(abyte0);
		bytebuffer.flip();
		return bytebuffer;
	}

	private int getLocation(String s) {
		byte abyte0[] = s.getBytes();
		ByteBuffer bytebuffer = createByteBuffer(abyte0.length + 1);
		bytebuffer.put(abyte0);
		bytebuffer.put((byte) 0);
		bytebuffer.flip();
		return ARBShaderObjects.glGetUniformLocationARB(prg, bytebuffer);
	}

	private String getLog() {
		IntBuffer intbuffer = createByteBuffer(4).asIntBuffer();
		String s = null;
		ARBShaderObjects.glGetObjectParameterARB(prg, 35714, intbuffer);
		int i = intbuffer.get();
		if (i == 0) {
			intbuffer.flip();
			ByteBuffer bytebuffer = createByteBuffer(5000);
			ARBShaderObjects.glGetInfoLogARB(prg, intbuffer, bytebuffer);
			int j = intbuffer.get();
			byte abyte0[] = new byte[j];
			bytebuffer.get(abyte0, 0, j);
			s = new String(abyte0, 0, j);
			failure = true;
		}
		return s;
	}

	private ByteBuffer createByteBuffer(int i) {
		return ByteBuffer.allocateDirect(i).order(ByteOrder.nativeOrder());
	}

	private int guessShaderModel() {
		ContextCapabilities contextcapabilities = GLContext.getCapabilities();
		char c = '\u270F';
		if (contextcapabilities.GL_NV_vertex_program)
			c = '\001';
		if (contextcapabilities.GL_NV_vertex_program2)
			c = '\002';
		if (contextcapabilities.GL_NV_vertex_program3)
			c = '\003';
		if (contextcapabilities.GL_NV_vertex_program4)
			c = '\004';
		if (contextcapabilities.GL_ATI_shader_texture_lod)
			c = '\003';
		if (contextcapabilities.GL_EXT_gpu_shader4)
			c = '\004';
		return c;
	}

	private String fragSource;
	private String vertexSource;
	private int prg;
	private int fragShade;
	private int vertShade;
	private boolean init;
	private boolean failure;
	private boolean excludeShadowProcess;
	private ShadowHelper helper;
	private boolean newUniforms;
	private Map uniforms;
	private List toRemove;
	private int minShaderModel;
}
