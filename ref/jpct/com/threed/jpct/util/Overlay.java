// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct.util;

import com.threed.jpct.*;
import java.awt.Color;
import java.io.Serializable;

public class Overlay implements Serializable {
	private static class MyController extends GenericVertexController {

		public void apply() {
			SimpleVector asimplevector[] = getDestinationMesh();
			for (int i = 0; i < 4; i++)
				asimplevector[i] = poss[i];

		}

		public void setNewBounds(SimpleVector simplevector, SimpleVector simplevector1, SimpleVector simplevector2,
				SimpleVector simplevector3) {
			poss[0] = simplevector;
			poss[1] = simplevector1;
			poss[2] = simplevector2;
			poss[3] = simplevector3;
		}

		private static final long serialVersionUID = 1L;
		private SimpleVector poss[];

		private MyController() {
			poss = new SimpleVector[4];
		}

	}

	public Overlay(World world1, FrameBuffer framebuffer, String s) {
		this(world1, 0, 0, framebuffer.getOutputWidth(), framebuffer.getOutputHeight(), s);
	}

	public Overlay(World world1, int i, int j, int k, int l, String s) {
		adjuster = null;
		depth = Config.nearPlane + 1.0F;
		disposed = false;
		uvChange = false;
		rotation = 0.0F;
		rotMode = false;
		world = world1;
		upperLeftX = i;
		upperLeftY = j;
		lowerRightX = k;
		lowerRightY = l;
		plane = Primitives.getPlane(1, 1.0F);
		if (s != null)
			plane.setTexture(s);
		plane.build();
		plane.setAdditionalColor(Color.white);
		plane.setLighting(1);
		plane.setUserObject(this);
		plane.setSelectable(false);
		world1.addObject(plane);
		plane.build();
		adjuster = new MyController();
		plane.getMesh().setVertexController(adjuster, false);
	}

	public void setDepth(float f) {
		if (f < Config.nearPlane)
			f = Config.nearPlane + 0.1F;
		depth = f;
	}

	public void setSelectable(boolean flag) {
		plane.setSelectable(flag);
	}

	public void setTransparency(int i) {
		plane.setTransparency(i);
	}

	public void setColor(Color color) {
		plane.setAdditionalColor(color);
	}

	public void setTexture(String s) {
		plane.setTexture(s);
		plane.recreateTextureCoords();
	}

	public void setTexture(TextureInfo textureinfo) {
		plane.setTexture(textureinfo);
		plane.recreateTextureCoords();
	}

	public void setVisibility(boolean flag) {
		plane.setVisibility(flag);
	}

	public void setTransparencyMode(int i) {
		plane.setTransparencyMode(i);
	}

	public void setNewCoordinates(int i, int j, int k, int l) {
		upperLeftX = i;
		upperLeftY = j;
		lowerRightX = k;
		lowerRightY = l;
	}

	public void setSourceCoordinates(int i, int j, int k, int l) {
		upperLeftU = i;
		upperLeftV = j;
		lowerRightU = k;
		lowerRightV = l;
		uvChange = true;
	}

	public void setRotation(float f) {
		rotation = f;
		if (f != 0.0F)
			rotMode = true;
	}

	public synchronized void dispose() {
		if (!disposed) {
			disposed = true;
			world.removeObject(plane);
			plane.setUserObject(null);
			world = null;
			plane = null;
			adjuster = null;
		}
	}

	public void unlink() {
		plane.setUserObject(null);
	}

	public void update(FrameBuffer framebuffer) {
		if (plane.getVisibility() && !disposed) {
			if (uvChange) {
				PolygonManager polygonmanager = plane.getPolygonManager();
				int i = polygonmanager.getPolygonTexture(0);
				Texture texture = TextureManager.getInstance().getTextureByID(i);
				float f = (float) upperLeftU / (float) texture.getWidth();
				float f1 = (float) upperLeftV / (float) texture.getHeight();
				float f2 = (float) lowerRightU / (float) texture.getWidth();
				float f3 = (float) lowerRightV / (float) texture.getHeight();
				TextureInfo textureinfo = new TextureInfo(i, f, f1, f, f3, f2, f1);
				polygonmanager.setPolygonTexture(0, textureinfo);
				textureinfo = new TextureInfo(i, f, f3, f2, f3, f2, f1);
				polygonmanager.setPolygonTexture(1, textureinfo);
				plane.recreateTextureCoords();
			}
			SimpleVector simplevector = Interact2D.reproject2D3D(world.getCamera(), framebuffer, upperLeftX, upperLeftY,
					depth);
			SimpleVector simplevector1 = Interact2D.reproject2D3D(world.getCamera(), framebuffer, upperLeftX, lowerRightY,
					depth);
			SimpleVector simplevector2 = Interact2D.reproject2D3D(world.getCamera(), framebuffer, lowerRightX, lowerRightY,
					depth);
			SimpleVector simplevector3 = Interact2D.reproject2D3D(world.getCamera(), framebuffer, lowerRightX, upperLeftY,
					depth);
			Matrix matrix = world.getCamera().getBack().cloneMatrix();
			SimpleVector simplevector4 = world.getCamera().getPosition();
			simplevector4.matMul(matrix);
			simplevector.add(simplevector4);
			simplevector1.add(simplevector4);
			simplevector2.add(simplevector4);
			simplevector3.add(simplevector4);
			Matrix matrix1 = matrix.invert3x3();
			simplevector.matMul(matrix1);
			simplevector1.matMul(matrix1);
			simplevector2.matMul(matrix1);
			simplevector3.matMul(matrix1);
			adjuster.setNewBounds(simplevector, simplevector1, simplevector3, simplevector2);
			plane.getMesh().applyVertexController();
			if (rotMode) {
				plane.getRotationMatrix().setIdentity();
				SimpleVector simplevector5 = new SimpleVector();
				simplevector5.add(simplevector);
				simplevector5.add(simplevector1);
				simplevector5.add(simplevector3);
				simplevector5.add(simplevector2);
				simplevector5.scalarMul(0.25F);
				plane.setRotationPivot(simplevector5);
				plane.rotateAxis(matrix1.getZAxis(), rotation);
			}
		}
	}

	protected void finalize() {
		dispose();
	}

	private static final long serialVersionUID = 2L;
	private World world;
	private Object3D plane;
	private MyController adjuster;
	private int upperLeftX;
	private int upperLeftY;
	private int lowerRightX;
	private int lowerRightY;
	private int upperLeftU;
	private int upperLeftV;
	private int lowerRightU;
	private int lowerRightV;
	private float depth;
	private boolean disposed;
	private boolean uvChange;
	private float rotation;
	private boolean rotMode;
}
