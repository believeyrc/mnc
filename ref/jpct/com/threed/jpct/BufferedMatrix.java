// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.Serializable;

// Referenced classes of package com.threed.jpct:
//            Matrix, SimpleVector

public class BufferedMatrix implements Serializable {

	BufferedMatrix() {
		backBx = 0.0F;
		backBz = 0.0F;
		backBy = 0.0F;
		frontBx = 0.0F;
		frontBz = 0.0F;
		frontBy = 0.0F;
		backMatrix = new Matrix();
		frontMatrix = new Matrix();
	}

	public void rotateAxis(SimpleVector simplevector, float f) {
		backMatrix.rotateAxis(simplevector, f);
	}

	public void rotateX(float f) {
		backMatrix.rotateX(f);
	}

	public void rotateY(float f) {
		backMatrix.rotateY(f);
	}

	public void rotateZ(float f) {
		backMatrix.rotateZ(f);
	}

	public void matMul(Matrix matrix) {
		backMatrix.matMul(matrix);
	}

	public void copyBackToFront() {
		prepareTransform();
	}

	public Matrix getFront() {
		return frontMatrix;
	}

	public Matrix getBack() {
		return backMatrix;
	}

	public void setBack(Matrix matrix) {
		backMatrix = matrix;
	}

	void prepareTransform() {
		frontMatrix.mat[0][0] = backMatrix.mat[0][0];
		frontMatrix.mat[1][0] = backMatrix.mat[1][0];
		frontMatrix.mat[2][0] = backMatrix.mat[2][0];
		frontMatrix.mat[3][0] = backMatrix.mat[3][0];
		frontMatrix.mat[0][1] = backMatrix.mat[0][1];
		frontMatrix.mat[1][1] = backMatrix.mat[1][1];
		frontMatrix.mat[2][1] = backMatrix.mat[2][1];
		frontMatrix.mat[3][1] = backMatrix.mat[3][1];
		frontMatrix.mat[0][2] = backMatrix.mat[0][2];
		frontMatrix.mat[1][2] = backMatrix.mat[1][2];
		frontMatrix.mat[2][2] = backMatrix.mat[2][2];
		frontMatrix.mat[3][2] = backMatrix.mat[3][2];
		frontMatrix.mat[0][3] = backMatrix.mat[0][3];
		frontMatrix.mat[1][3] = backMatrix.mat[1][3];
		frontMatrix.mat[2][3] = backMatrix.mat[2][3];
		frontMatrix.mat[3][3] = backMatrix.mat[3][3];
		frontBz = backBz;
		frontBx = backBx;
		frontBy = backBy;
	}

	private static final long serialVersionUID = 1L;
	protected Matrix backMatrix;
	protected float backBx;
	protected float backBy;
	protected float backBz;
	protected Matrix frontMatrix;
	protected float frontBx;
	protected float frontBy;
	protected float frontBz;
}
