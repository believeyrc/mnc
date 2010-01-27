// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct.procs;

import com.threed.jpct.*;
import java.nio.*;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

// Referenced classes of package com.threed.jpct.procs:
//            ProcsHelper

public class BloomGLProcessor implements IPostProcessor {

	public BloomGLProcessor() {
		screenTexture = 0;
		blurTexture = 0;
		w = 0;
		h = 0;
		wq = 0;
		hq = 0;
		steps = 4;
		blurSteps = 4;
		strength = 2;
		quality = 1;
		hasGL = false;
		disposed = false;
		isInitialized = false;
		message = false;
		comb = true;
	}

	public BloomGLProcessor(int i, int j, int k, int l) {
		screenTexture = 0;
		blurTexture = 0;
		w = 0;
		h = 0;
		wq = 0;
		hq = 0;
		steps = 4;
		blurSteps = 4;
		strength = 2;
		quality = 1;
		hasGL = false;
		disposed = false;
		isInitialized = false;
		message = false;
		comb = true;
		steps = Math.max(0, i);
		blurSteps = Math.max(0, j);
		strength = Math.max(0, k);
		quality = Math.max(0, l);
	}

	public void init(FrameBuffer framebuffer) {
		if (!isInitialized)
			try {
				hasGL = framebuffer.usesRenderer(2) && (supports("EXT_texture_rectangle") || supports("NV_texture_rectangle"));
				if (hasGL) {
					IntBuffer intbuffer = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
					GL11.glGetInteger(34040, intbuffer);
					int i = intbuffer.get(0);
					intbuffer = null;
					disposed = false;
					w = framebuffer.getOutputWidth();
					h = framebuffer.getOutputHeight();
					if (w <= i && h <= i) {
						wq = w >> quality;
						hq = h >> quality;
						int j = w * h;
						ByteBuffer bytebuffer = ByteBuffer.allocateDirect(j << 2);
						bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
						IntBuffer intbuffer1 = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
						GL11.glGenTextures(intbuffer1);
						screenTexture = intbuffer1.get(0);
						GL11.glBindTexture(34037, screenTexture);
						GL11.glTexParameteri(34037, 10241, 9729);
						GL11.glTexParameteri(34037, 10240, 9729);
						GL11.glTexImage2D(34037, 0, 32849, w, h, 0, 6407, 5121, bytebuffer);
						GL11.glGenTextures(intbuffer1);
						blurTexture = intbuffer1.get(0);
						GL11.glBindTexture(34037, blurTexture);
						GL11.glTexParameteri(34037, 10241, 9729);
						GL11.glTexParameteri(34037, 10240, 9729);
						GL11.glTexImage2D(34037, 0, 32849, wq, hq, 0, 6407, 5121, bytebuffer);
						Logger.log("Post processing textures created!", 2);
						isInitialized = true;
					} else {
						Logger.log("Framebuffer dimensions exceed maximum resolution for arbitrary textures (" + i + ")", 1);
					}
				} else if (!message) {
					if (framebuffer.usesRenderer(2))
						Logger.log("This graphics card doesn't support arbitrary textures!", 1);
					else
						Logger.log("There's no bloom processor for the software renderer available!", 1);
					message = true;
				}
			} catch (Exception exception) {
				Logger.log("Unable to initialize post processing textures due to: " + exception.getMessage(), 1);
				exception.printStackTrace();
			}
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void setCombining(boolean flag) {
		comb = flag;
	}

	public void process() {
		if (hasGL) {
			GL11.glEnable(34037);
			GL11.glColor3f(1.0F, 1.0F, 1.0F);
			GL11.glDisable(2929);
			GL11.glBindTexture(34037, screenTexture);
			GL11.glCopyTexImage2D(34037, 0, 6407, 0, 0, w, h, 0);
			ProcsHelper.toOrtho();
			GL11.glViewport(0, 0, wq, hq);
			GL11.glBindTexture(34037, screenTexture);
			GL11.glBlendFunc(774, 0);
			GL11.glDisable(3042);
			GL11.glBegin(7);
			drawQuad(w, h, 0, 0);
			GL11.glEnd();
			GL11.glEnable(3042);
			GL11.glBegin(7);
			for (int i = 0; i < steps; i++)
				drawQuad(w, h, 0, 0);

			GL11.glEnd();
			GL11.glBindTexture(34037, blurTexture);
			GL11.glCopyTexImage2D(34037, 0, 6407, 0, 0, wq, hq, 0);
			float f = 1.0F / ((float) blurSteps * 2.0F + 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, f);
			GL11.glBlendFunc(770, 0);
			GL11.glBegin(7);
			drawQuad(wq, hq, 0, 0);
			GL11.glEnd();
			GL11.glBlendFunc(770, 1);
			GL11.glBegin(7);
			for (int j = 0; j < blurSteps; j++) {
				drawQuad(wq, hq, -j, 0);
				drawQuad(wq, hq, j, 0);
			}

			GL11.glEnd();
			GL11.glCopyTexImage2D(34037, 0, 6407, 0, 0, wq, hq, 0);
			GL11.glBlendFunc(770, 0);
			GL11.glBegin(7);
			drawQuad(wq, hq, 0, 0);
			GL11.glEnd();
			GL11.glBlendFunc(770, 1);
			GL11.glBegin(7);
			for (int k = 0; k < blurSteps; k++) {
				drawQuad(wq, hq, 0, -k);
				drawQuad(wq, hq, 0, k);
			}

			GL11.glEnd();
			GL11.glCopyTexImage2D(34037, 0, 6407, 0, 0, wq, hq, 0);
			GL11.glViewport(0, 0, w, h);
			GL11.glDisable(3042);
			GL11.glBegin(7);
			drawQuad(wq, hq, 0, 0);
			GL11.glEnd();
			GL11.glEnable(3042);
			GL11.glBlendFunc(1, 1);
			GL11.glBegin(7);
			for (int l = 0; l < strength; l++)
				drawQuad(wq, hq, 0, 0);

			GL11.glEnd();
			if (comb) {
				GL11.glBindTexture(34037, screenTexture);
				GL11.glBegin(7);
				drawQuad(w, h, 0, 0);
				GL11.glEnd();
			}
			ProcsHelper.restoreProjection();
			GL11.glDisable(34037);
			GL11.glEnable(2929);
			GL11.glDisable(3042);
		}
	}

	private boolean supports(String s) {
		String s1 = GL11.glGetString(7939);
		return s1.toLowerCase().indexOf(s.toLowerCase()) != -1;
	}

	private void drawQuad(int i, int j, int k, int l) {
		GL11.glTexCoord2f(k, l);
		GL11.glVertex3f(0.0F, 1.0F, -1F);
		GL11.glTexCoord2f(k, j + l);
		GL11.glVertex3f(0.0F, 0.0F, -1F);
		GL11.glTexCoord2f(i + k, j + l);
		GL11.glVertex3f(1.0F, 0.0F, -1F);
		GL11.glTexCoord2f(i + k, l);
		GL11.glVertex3f(1.0F, 1.0F, -1F);
	}

	public void dispose() {
		if (!disposed && isInitialized) {
			try {
				if (Display.isCreated()) {
					Logger.log("Unloading post processing textures!", 2);
					IntBuffer intbuffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
					intbuffer.put(screenTexture);
					intbuffer.flip();
					GL11.glDeleteTextures(intbuffer);
					intbuffer.put(blurTexture);
					intbuffer.flip();
					GL11.glDeleteTextures(intbuffer);
				}
			} catch (Exception exception) {
				Logger.log("Unable to unload post processing textures - already done?", 1);
			}
			isInitialized = false;
			message = false;
			disposed = true;
		}
	}

	protected void finalize() {
		if (hasGL)
			dispose();
	}

	private int screenTexture;
	private int blurTexture;
	private int w;
	private int h;
	private int wq;
	private int hq;
	private int steps;
	private int blurSteps;
	private int strength;
	private int quality;
	private boolean hasGL;
	private boolean disposed;
	private boolean isInitialized;
	private boolean message;
	private boolean comb;
}
