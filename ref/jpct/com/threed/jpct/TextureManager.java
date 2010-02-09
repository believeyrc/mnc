// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.util.*;

// Referenced classes of package com.threed.jpct:
//            Texture, GLBase, Logger, IntegerC, 
//            FrameBuffer, Config

public final class TextureManager {

	private TextureManager() {
		lastID = -1;
		lastName = null;
		flush();
	}

	public static synchronized TextureManager getInstance() {
		if (myInstance == null)
			myInstance = new TextureManager();
		return myInstance;
	}

	public Vector getState() {
		Vector vector = new Vector();
		for (int i = 0; i < textureCount; i++) {
			Texture texture = textures[i];
			String s = getNameByID(i);
			Object aobj[] = new Object[2];
			aobj[0] = s;
			aobj[1] = texture;
			vector.addElement(((Object) (aobj)));
		}

		return vector;
	}

	public void setState(Vector vector) {
		flush();
		try {
			for (int i = 0; i < vector.size(); i++) {
				Object aobj[] = (Object[]) vector.elementAt(i);
				String s = (String) aobj[0];
				Texture texture = (Texture) aobj[1];
				if (!s.equals("--dummy--"))
					addTexture(s, texture);
			}

		} catch (Exception exception) {
			Logger.log("Not a valid dump!", 0);
		}
	}

	public long getMemoryUsage(boolean flag) {
		long l = 0L;
		for (int i = 0; i < textureCount; i++) {
			int ai[] = textures[i].texels;
			int ai1[] = textures[i].alpha;
			if (ai != null)
				l += ai.length << 2;
			if (ai1 != null)
				l += ai1.length << 2;
			if (!flag && ai == null)
				l += textures[i].width * textures[i].height << 2;
		}

		return l;
	}

	public void addTexture(String s) {
		addTexture(s, dummy);
	}

	public synchronized void addTexture(String s, Texture texture) {
		if (textureCount >= textures.length) {
			Texture atexture[] = new Texture[textures.length * 2];
			System.arraycopy(textures, 0, atexture, 0, textures.length);
			textures = atexture;
		}
		if (!textureList.containsKey(s)) {
			textures[textureCount] = texture;
			textureList.put(s, IntegerC.valueOf(textureCount));
			textureCount++;
		} else {
			Logger.log("A texture with the name '" + s + "' has been declared twice!", 0);
		}
	}

	public void unloadTexture(FrameBuffer framebuffer, Texture texture) {
		if (framebuffer.glRend != null)
			((GLBase) framebuffer.glRend).addForUnload(texture);
	}

	public synchronized void removeTexture(String s) {
		Texture texture = getTexture(s);
		for (int i = 0; i < textureCount; i++) {
			if (texture != textures[i])
				continue;
			textureList.remove(s);
			textures[i] = dummy;
			if (i == textureCount - 1)
				textureCount--;
		}

	}

	public void removeAndUnload(String s, FrameBuffer framebuffer) {
		unloadTexture(framebuffer, getTexture(s));
		removeTexture(s);
	}

	Texture[] getTextures() {
		return textures;
	}

	public synchronized void replaceTexture(String s, Texture texture) {
		int i = getTextureID(s);
		if (i != -1)
			textures[i] = texture;
		else
			Logger.log("Texture '" + s + "' not found!", 0);
	}

	public synchronized void replaceTexture(int i, Texture texture) {
		if (i != -1 && i < textures.length)
			textures[i] = texture;
		else
			Logger.log("Texture '" + i + "' not found!", 0);
	}

	public void flush() {
		textureList = new Hashtable();
		textures = new Texture[Config.maxTextures];
		dummy = new Texture();
		textureCount = 0;
		addTexture("--dummy--", dummy);
	}

	public void preWarm(FrameBuffer framebuffer) {
		if (!framebuffer.usesRenderer(1))
			return;
		for (int i = 0; i < textureCount; i++) {
			Texture texture = textures[i];
			Texture texture1;
			if (texture != null)
				texture1 = texture.getMipMappedTexture(1);
		}

	}

	public void setDummyTexture(Texture texture) {
		if (texture != null) {
			dummy = texture;
			replaceTexture("--dummy--", texture);
		} else {
			Logger.log("Texture can't be null!", 0);
		}
	}

	public Texture getDummyTexture() {
		return dummy;
	}

	public boolean containsTexture(String s) {
		return textureList.containsKey(s);
	}

	public int getTextureCount() {
		return textureList.size();
	}

	public synchronized int getTextureID(String s) {
		if (s.equals(lastName))
			return lastID;
		Object obj = textureList.get(s);
		if (obj != null) {
			lastID = ((Integer) obj).intValue();
			lastName = s;
			return lastID;
		} else {
			return -1;
		}
	}

	public Texture getTextureByID(int i) {
		return getTexture(getNameByID(i));
	}

	public synchronized Texture getTexture(String s) {
		if (s != null) {
			int i = getTextureID(s);
			if (i != -1)
				return textures[i];
		}
		Logger.log("Requested texture (" + s + ") not found!", 0);
		return null;
	}

	public Enumeration getNames() {
		return textureList.keys();
	}

	synchronized void flushOpenGLIDs(int i) {
		for (Enumeration enumeration = textureList.keys(); enumeration.hasMoreElements();) {
			String s = (String) enumeration.nextElement();
			Texture texture = getTexture(s);
			if (texture != null)
				texture.clearIDs(i);
			else
				Logger.log("Texture '" + s + "' is supposed to be there, but couldn't be found!?", 1);
		}

	}

	public synchronized String getNameByID(int i) {
		for (Enumeration enumeration = textureList.keys(); enumeration.hasMoreElements();) {
			String s = (String) enumeration.nextElement();
			int j = ((Integer) textureList.get(s)).intValue();
			if (j == i)
				return s;
		}

		return null;
	}

	public static final int TEXTURE_NOTFOUND = -1;
	static final String DUMMY_NAME = "--dummy--";
	Texture textures[];
	private Texture dummy;
	private static TextureManager myInstance = null;
	private int textureCount;
	private Hashtable textureList;
	private int lastID;
	private String lastName;

}
