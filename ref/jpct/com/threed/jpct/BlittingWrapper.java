// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import com.threed.jpct.util.Overlay;
import java.awt.Color;

// Referenced classes of package com.threed.jpct:
//            World, FrameBuffer, TextureManager, Config, 
//            Texture

class BlittingWrapper {

	BlittingWrapper(FrameBuffer framebuffer) {
		world = null;
		overlay = null;
		buffer = null;
		intBuffer = null;
		world = new World();
		buffer = framebuffer;
		if (framebuffer.getSamplingMode() > 0 && framebuffer.getSamplingMode() < 4) {
			intBuffer = new FrameBuffer(framebuffer.getOutputWidth(), framebuffer.getOutputHeight(), 0);
			intBuffer.pixels = framebuffer.getPixels();
		} else {
			intBuffer = framebuffer;
		}
		overlay = new Overlay(world, buffer, null);
	}

	void blit(Texture texture, int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, boolean flag,
			Color color) {
		TextureManager texturemanager = TextureManager.getInstance();
		if (!texturemanager.containsTexture("--*--BlittingWrapper_internal--*--"))
			texturemanager.addTexture("--*--BlittingWrapper_internal--*--");
		texturemanager.replaceTexture("--*--BlittingWrapper_internal--*--", texture);
		if (color != null)
			overlay.setColor(color);
		else
			overlay.setColor(Color.white);
		overlay.setTexture("--*--BlittingWrapper_internal--*--");
		overlay.setNewCoordinates(k, l, k + k1, l + l1);
		overlay.setSourceCoordinates(i, j, i + i1, j + j1);
		overlay.setTransparency(i2);
		if (flag)
			overlay.setTransparencyMode(1);
		else
			overlay.setTransparencyMode(0);
		overlay.setDepth(Config.nearPlane + 0.001F);
		FrameBuffer framebuffer = intBuffer;
		if (buffer.blittingTarget != 0 && intBuffer != buffer)
			framebuffer = buffer;
		world.renderScene(framebuffer);
		world.draw(framebuffer);
		framebuffer.update();
	}

	void dispose() {
		TextureManager texturemanager = TextureManager.getInstance();
		texturemanager.replaceTexture("--*--BlittingWrapper_internal--*--", texturemanager.getDummyTexture());
		texturemanager.removeTexture("--*--BlittingWrapper_internal--*--");
		buffer = null;
		intBuffer = null;
		overlay = null;
		world = null;
	}

	private World world;
	private Overlay overlay;
	private FrameBuffer buffer;
	private FrameBuffer intBuffer;
	private static final String TEXTURE_NAME = "--*--BlittingWrapper_internal--*--";
}
