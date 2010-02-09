// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

// Referenced classes of package com.threed.jpct:
//            FrameBuffer, Texture, IRenderer

class ProxyFrameBuffer extends FrameBuffer {

	static ProxyFrameBuffer getInstance(Texture texture, FrameBuffer framebuffer) {
		if (last == null || texture.getWidth() != last.getOutputWidth() || texture.getHeight() != last.getOutputHeight()
				|| framebuffer.softRend != last.renderer)
			last = new ProxyFrameBuffer(texture, framebuffer);
		last.tex = texture;
		last.renderer = framebuffer.softRend;
		return last;
	}

	private ProxyFrameBuffer(Texture texture, FrameBuffer framebuffer) {
		super(texture.getWidth(), texture.getHeight(), 0, texture);
		tex = null;
		renderer = null;
		super.softRend = framebuffer.softRend;
	}

	void copy() {
		int i = tex.getWidth() * tex.getHeight();
		for (int j = 0; j < i; j++)
			tex.texels[j] = super.pixels[j] & 0xffffff;

	}

	private static final long serialVersionUID = 1L;
	private static transient ProxyFrameBuffer last = null;
	private transient Texture tex;
	private transient IRenderer renderer;

}
