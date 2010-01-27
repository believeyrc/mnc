// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.threed.jpct;

import com.threed.jpct.util.XMLFactory;
import com.threed.jpct.util.XMLNode;
import java.awt.Color;
import java.io.*;
import java.net.URL;
import java.util.*;

// Referenced classes of package com.threed.jpct:
//            Object3D, TextureInfo, Mesh, Vectors,
//            SimpleVector, Animation, Logger, TextureManager,
//            Texture, Config, World, Portals

public final class Loader {

	private Loader() {
	}

	public static void setVertexOptimization(boolean flag) {
		optimize = flag;
	}

	public static String loadTextFile(String s) {
		return loadTextFile(null, s, null);
	}

	public static String loadTextFile(URL url, String s) {
		return loadTextFile(url, s, null);
	}

	public static String loadTextFile(InputStream inputstream) {
		return loadTextFile(null, null, inputstream);
	}

	private static String loadTextFile(URL url, String s, InputStream inputstream) {
		if (s != null && lastFilename != null && lastFilename.equals(s)) {
			Logger.log("Reading file " + s + " from cache", 2);
			return lastFileData;
		}
		clearCache();
		String as[] = new String[1];
		loadBinaryFile(url, s, inputstream, 0x7a120, as);
		if (as != null && as[0] != null) {
			if (as[0].length() <= 0xf4240 && s != null) {
				lastFilename = s;
				lastFileData = as[0];
			}
			return as[0];
		} else {
			Logger.log("Couldn't load text-file!", 0);
			return "";
		}
	}

	public static Object3D loadMD2(String s, float f) {
		return loadMD2(null, s, null, f);
	}

	public static Object3D loadMD2(URL url, String s, float f) {
		return loadMD2(url, s, null, f);
	}

	public static Object3D loadMD2(InputStream inputstream, float f) {
		return loadMD2(null, null, inputstream, f);
	}

	public static void loadSceneFromXML(String s, World world) {
		loadSceneFromXML(null, s, null, world);
	}

	public static void loadSceneFromXML(URL url, String s, World world) {
		loadSceneFromXML(url, s, null, world);
	}

	public static void loadSceneFromXML(InputStream inputstream, World world) {
		loadSceneFromXML(null, null, inputstream, world);
	}

	public static Object3D[] load3DS(String s, float f) {
		return load3DS(null, s, null, f);
	}

	public static Object3D[] load3DS(URL url, String s, float f) {
		return load3DS(url, s, null, f);
	}

	public static Object3D[] load3DS(InputStream inputstream, float f) {
		return load3DS(null, null, inputstream, f);
	}

	public static String[] readTextureNames3DS(String s) {
		return readTextureNames3DS(null, s, null);
	}

	public static String[] readTextureNames3DS(URL url, String s) {
		return readTextureNames3DS(url, s, null);
	}

	public static String[] readTextureNames3DS(InputStream inputstream) {
		return readTextureNames3DS(null, null, inputstream);
	}

	public static Object3D loadASC(String s, float f, boolean flag) {
		return loadASC(null, s, null, f, flag);
	}

	public static Object3D loadASC(URL url, String s, float f, boolean flag) {
		return loadASC(url, s, null, f, flag);
	}

	public static Object3D loadASC(InputStream inputstream, float f, boolean flag) {
		return loadASC(null, null, inputstream, f, flag);
	}

	public static Object3D[] loadOBJ(String s, String s1, float f) {
		return loadOBJ(null, s, s1, null, null, f);
	}

	public static Object3D[] loadOBJ(URL url, String s, String s1, float f) {
		return loadOBJ(url, s, s1, null, null, f);
	}

	public static Object3D[] loadOBJ(InputStream inputstream, InputStream inputstream1, float f) {
		return loadOBJ(null, null, null, inputstream, inputstream1, f);
	}

	private static Object3D[] loadOBJ(URL url, String s, String s1, InputStream inputstream, InputStream inputstream1,
			float f) {
		String s2 = null;
		if (s1 != null || inputstream1 != null) {
			s2 = loadTextFile(url, s1, inputstream1);
			s2 = s2.replace('\t', ' ');
		} else {
			s2 = "";
		}
		Hashtable hashtable = new Hashtable();
		StringTokenizer stringtokenizer = new StringTokenizer(s2, "\n");
		String s3 = null;
		Color color = null;
		String s4 = null;
		String s5 = null;
		float af[] = new float[3];
		boolean flag = true;
		String s6 = null;
		Float float1 = null;
		TextureManager texturemanager = TextureManager.getInstance();
		int i = 0;
		do {
			if (!stringtokenizer.hasMoreTokens())
				break;
			if (flag)
				s6 = stringtokenizer.nextToken().trim();
			switch (i) {
			case 0: // '\0'
				if (s6.startsWith("newmtl ")) {
					s3 = s6.substring(7).trim();
					i = 1;
					flag = true;
					color = null;
					s4 = null;
					s5 = null;
					float1 = null;
					Logger.log("Processing new material " + s3 + "!", 2);
				}
				break;

			case 1: // '\001'
				String s8 = s6.toLowerCase();
				if (s8.startsWith("kd ")) {
					String s10 = s8.substring(3).trim();
					StringTokenizer stringtokenizer1 = new StringTokenizer(s10, " ");
					for (int l = 0; stringtokenizer1.hasMoreTokens() && l < 3; l++) {
						String s11 = stringtokenizer1.nextToken();
						try {
							af[l] = Float.valueOf(s11).floatValue();
							continue;
						} catch (Exception exception1) {
							af[l] = 1.0F;
						}
						Logger.log("Error in MTL-file near: " + s6, 0);
					}

					color = new Color((int) (af[0] * 255F), (int) (af[1] * 255F), (int) (af[2] * 255F));
				} else if ((s8.startsWith("map_kd") || s8.startsWith("map_ka")) && s6.length() > 7) {
					if (s8.startsWith("map_kd")) {
						s4 = s6.substring(7).trim();
						if (!texturemanager.containsTexture(s4)) {
							texturemanager.addTexture(s4);
							Logger.log("Texture named " + s4 + " added to TextureManager!", 2);
						}
					} else {
						s5 = s6.substring(7).trim();
						if (!texturemanager.containsTexture(s5)) {
							texturemanager.addTexture(s5);
							Logger.log("Texture named " + s5 + " added to TextureManager!", 2);
						}
					}
				} else if (s8.startsWith("d ")) {
					float f1 = -1F;
					try {
						f1 = Float.parseFloat(s6.substring(2).trim());
					} catch (Exception exception) {
					}
					if (f1 != -1F && f1 != 1.0F)
						float1 = new Float(f1);
				} else if (s8.startsWith("newmtl")) {
					i = 0;
					flag = false;
				}
				break;
			}
			if (!flag || !stringtokenizer.hasMoreTokens())
				hashtable.put(s3, ((Object) (new Object[] { color, s4, float1, s5 })));
		} while (true);
		String s9 = loadTextFile(url, s, inputstream);
		s9 = s9.replace('\t', ' ');
		if (s9.indexOf("o ") == -1 && s9.indexOf("g ") == -1 && s9.indexOf("g\n") == -1 && s9.indexOf("o\n") == -1)
			s9 = "o jPCT_generated\n" + s9;
		int j = countOcc(s9, "v ");
		int k = countOcc(s9, "vt ");
		int i1 = countOcc(s9, "f ");
		float af1[][] = new float[j][3];
		float af2[][] = new float[k][2];
		int ai[][][] = new int[i1][4][2];
		String as[] = new String[i1];
		i1 = 0;
		j = 0;
		k = 0;
		int ai1[] = null;
		int ai2[] = null;
		if (!optimize) {
			ai1 = new int[af1.length];
			ai2 = new int[3];
			for (int j1 = 0; j1 < ai1.length; j1++)
				ai1[j1] = -1;

		}
		stringtokenizer = new StringTokenizer(s9, "\n");
		String s12 = null;
		s1 = null;
		int k1 = 0;
		boolean flag2 = false;
		boolean flag3 = false;
		Vector vector = new Vector();
		int l1 = 0;
		do {
			if (!stringtokenizer.hasMoreTokens())
				break;
			String s7 = stringtokenizer.nextToken().trim();
			boolean flag1 = false;
			if (s7.startsWith("v ")) {
				String s13 = s7.substring(2).trim();
				StringTokenizer stringtokenizer2 = new StringTokenizer(s13, " ");
				for (int j2 = 0; stringtokenizer2.hasMoreTokens() && j2 < 3; j2++) {
					String s18 = stringtokenizer2.nextToken();
					try {
						af1[j][j2] = Float.valueOf(s18).floatValue() * f;
					} catch (Exception exception2) {
						af1[j][j2] = 0.0F;
					}
				}

				j++;
			} else if (s7.startsWith("vt ")) {
				String s14 = s7.substring(3).trim();
				StringTokenizer stringtokenizer3 = new StringTokenizer(s14, " ");
				int k2 = 0;
				af2[k][1] = 0.0F;
				for (; stringtokenizer3.hasMoreTokens() && k2 < 2; k2++) {
					String s19 = stringtokenizer3.nextToken();
					try {
						af2[k][k2] = Float.valueOf(s19).floatValue();
						continue;
					} catch (Exception exception3) {
						af2[k][k2] = 0.0F;
					}
					Logger.log("Error in OBJ-file near: " + s7, 0);
				}

				k++;
			} else if (s7.startsWith("usemtl"))
				s1 = s7.substring(7).trim();
			else if (s7.startsWith("f ")) {
				String s15 = s7.substring(2).trim();
				StringTokenizer stringtokenizer4 = new StringTokenizer(s15, " ");
				int l2 = 0;
				do {
					if (!stringtokenizer4.hasMoreTokens() || l2 >= 4)
						break;
					if (l2 == 3)
						k1++;
					String s20 = stringtokenizer4.nextToken();
					int k3 = s20.indexOf("/");
					int l3 = -1;
					if (k3 == -1)
						k3 = s20.length();
					else
						l3 = s20.indexOf("/", k3 + 1);
					if (l3 == -1)
						l3 = s20.length();
					String s21 = s20.substring(0, k3);
					String s22 = null;
					if (k3 + 1 < l3)
						s22 = s20.substring(k3 + 1, l3);
					try {
						ai[i1][l2][0] = Integer.valueOf(s21).intValue();
						if (s22 == null)
							s22 = "1";
						ai[i1][l2][1] = Integer.valueOf(s22).intValue();
					} catch (Exception exception4) {
						ai[i1][l2][0] = 1;
						ai[i1][l2][1] = 1;
						Logger.log("Error in OBJ-file near: " + s7, 0);
					}
					if (++l2 == 4 && stringtokenizer4.hasMoreTokens())
						flag2 = true;
				} while (true);
				for (int i3 = l2; i3 < 4; i3++) {
					ai[i1][l2][0] = -9999;
					ai[i1][l2][1] = -9999;
				}

				as[i1] = s1;
				i1++;
			} else {
				boolean flag4 = s7.startsWith("o");
				if (flag4 || s7.startsWith("g")) {
					String s16 = s12;
					String s17 = s7.substring(1).trim();
					if (s17.length() == 0 && (s12 == null || s12.length() == 0))
						s12 = "noname";
					else if (s17.length() > 0)
						s12 = s17;
					if (flag4) {
						for (int j3 = l1; j3 < i1; j3++)
							as[j3] = null;

					}
					if (i1 - l1 > 0)
						vector.addElement(createOBJObject(hashtable, ai, af1, af2, s16 == null ? s12 : s16, as, i1, l1, k, k1, ai1,
								ai2));
					Logger.log("Processing object from OBJ-file: " + s12, 2);
					l1 = i1;
					k1 = 0;
				} else if (!flag3
						&& (s7.startsWith("p ") || s7.startsWith("l ") || s7.startsWith("curv") || s7.startsWith("surf")))
					flag3 = true;
			}
			if (!stringtokenizer.hasMoreTokens() && i1 - l1 > 0)
				vector.addElement(createOBJObject(hashtable, ai, af1, af2, s12, as, i1, l1, k, k1, ai1, ai2));
		} while (true);
		if (flag3)
			Logger.log("This OBJ-file contains unsupported geometry data. This data has been skipped!", 1);
		if (flag2)
			Logger.log("This OBJ-file contains n-polygons with n>4! These polygons wont be displayed correctly!", 1);
		Object3D aobject3d[] = new Object3D[vector.size()];
		for (int i2 = 0; i2 < vector.size(); i2++)
			aobject3d[i2] = (Object3D) vector.elementAt(i2);

		if (s2 != null)
			clearCache();
		return aobject3d;
	}

	private static Object3D createOBJObject(Hashtable hashtable, int ai[][][], float af[][], float af1[][], String s,
			String as[], int i, int j, int k, int l, int ai1[], int ai2[]) {
		Object3D object3d = new Object3D((i - j) + l);
		if (s == null)
			s = "noname";
		boolean flag = false;
		if (k == 0) {
			flag = true;
			af1 = new float[1][2];
		}
		boolean flag1 = false;
		TextureManager texturemanager = TextureManager.getInstance();
		int i1 = -1;
		int j1 = -1;
		if (!optimize)
			object3d.disableVertexSharing();
		String s1 = "jkkjkljdldld----";
		for (int k1 = j; k1 < i; k1++) {
			String s2 = as[k1];
			if (s2 != null && !s2.equals(s1)) {
				i1 = -1;
				j1 = -1;
				s1 = s2;
				Object aobj[] = (Object[]) hashtable.get(s2);
				Object obj = null;
				if (aobj == null) {
					i1 = texturemanager.getTextureID(s2);
				} else {
					if (aobj[1] != null)
						i1 = texturemanager.getTextureID((String) aobj[1]);
					if (aobj[3] != null)
						j1 = texturemanager.getTextureID((String) aobj[3]);
					if (i1 == -1) {
						Color color = (Color) aobj[0];
						if (color != null) {
							String s3 = "__obj-Color:" + color.getRed() + "/" + color.getGreen() + "/" + color.getBlue();
							if (texturemanager.containsTexture(s3)) {
								i1 = texturemanager.getTextureID(s3);
							} else {
								Texture texture = Texture.createSingleColoredTexture(color);
								texturemanager.addTexture(s3, texture);
								i1 = texturemanager.getTextureID(s3);
							}
						}
					}
					if (aobj[2] != null) {
						flag1 = true;
						object3d.setTransparency((int) (10F * ((Float) aobj[2]).floatValue()));
					} else if (flag1)
						object3d.setTransparency(-1);
				}
			}
			TextureInfo textureinfo = null;
			TextureInfo textureinfo1 = null;
			int l1 = ai[k1][0][0] - 1;
			int i2 = ai[k1][1][0] - 1;
			int j2 = ai[k1][2][0] - 1;
			int k2 = ai[k1][3][0] - 1;
			int l2 = 0;
			int i3 = 0;
			int j3 = 0;
			int k3 = 0;
			if (!flag) {
				l2 = Math.max(0, ai[k1][0][1] - 1);
				i3 = Math.max(0, ai[k1][1][1] - 1);
				j3 = Math.max(0, ai[k1][2][1] - 1);
				k3 = Math.max(0, ai[k1][3][1] - 1);
			}
			if (i1 != -1 && j1 != -1) {
				textureinfo = new TextureInfo(i1, af1[l2][0], 1.0F - af1[l2][1], af1[i3][0], 1.0F - af1[i3][1], af1[j3][0],
						1.0F - af1[j3][1]);
				textureinfo.add(j1, af1[l2][0], 1.0F - af1[l2][1], af1[i3][0], 1.0F - af1[i3][1], af1[j3][0],
						1.0F - af1[j3][1], 1);
				textureinfo1 = new TextureInfo(i1, af1[l2][0], 1.0F - af1[l2][1], af1[j3][0], 1.0F - af1[j3][1], af1[k3][0],
						1.0F - af1[k3][1]);
				textureinfo1.add(j1, af1[l2][0], 1.0F - af1[l2][1], af1[j3][0], 1.0F - af1[j3][1], af1[k3][0],
						1.0F - af1[k3][1], 1);
			}
			if (optimize) {
				object3d.addTriangle(af[l1][0], af[l1][1], af[l1][2], af1[l2][0], 1.0F - af1[l2][1], af[i2][0], af[i2][1],
						af[i2][2], af1[i3][0], 1.0F - af1[i3][1], af[j2][0], af[j2][1], af[j2][2], af1[j3][0], 1.0F - af1[j3][1],
						i1, 0, false, null, textureinfo);
				if (k2 >= 0)
					object3d.addTriangle(af[l1][0], af[l1][1], af[l1][2], af1[l2][0], 1.0F - af1[l2][1], af[j2][0], af[j2][1],
							af[j2][2], af1[j3][0], 1.0F - af1[j3][1], af[k2][0], af[k2][1], af[k2][2], af1[k3][0], 1.0F - af1[k3][1],
							i1, 0, false, null, textureinfo1);
				continue;
			}
			ai2[0] = ai1[l1];
			ai2[1] = ai1[i2];
			ai2[2] = ai1[j2];
			object3d.addTriangle(af[l1][0], af[l1][1], af[l1][2], af1[l2][0], 1.0F - af1[l2][1], af[i2][0], af[i2][1],
					af[i2][2], af1[i3][0], 1.0F - af1[i3][1], af[j2][0], af[j2][1], af[j2][2], af1[j3][0], 1.0F - af1[j3][1], i1,
					0, false, ai2, textureinfo);
			if (ai1[l1] == -1)
				ai1[l1] = ai2[0];
			if (ai1[i2] == -1)
				ai1[i2] = ai2[1];
			if (ai1[j2] == -1)
				ai1[j2] = ai2[2];
			if (k2 < 0)
				continue;
			ai2[0] = ai1[l1];
			ai2[1] = ai1[j2];
			ai2[2] = ai1[k2];
			object3d.addTriangle(af[l1][0], af[l1][1], af[l1][2], af1[l2][0], 1.0F - af1[l2][1], af[j2][0], af[j2][1],
					af[j2][2], af1[j3][0], 1.0F - af1[j3][1], af[k2][0], af[k2][1], af[k2][2], af1[k3][0], 1.0F - af1[k3][1], i1,
					0, false, ai2, textureinfo1);
			if (ai1[l1] == -1)
				ai1[l1] = ai2[0];
			if (ai1[j2] == -1)
				ai1[j2] = ai2[1];
			if (ai1[k2] == -1)
				ai1[k2] = ai2[2];
		}

		object3d.setName(s + "_jPCT" + object3d.getID());
		object3d.getMesh().compress();
		Logger.log("Object '" + object3d.getName() + "' created using " + object3d.getMesh().anzTri + " polygons and "
				+ object3d.getMesh().anzCoords + " vertices.", 2);
		return object3d;
	}

	private static int countOcc(String s, String s1) {
		int i = 0;
		int j = 0;
		int k = s1.length();
		do {
			j = s.indexOf(s1, j + k);
			if (j != -1)
				i++;
		} while (j != -1);
		return i;
	}

	private static Object3D loadASC(URL url, String s, InputStream inputstream, float f, boolean flag) {
		float f1 = 0.0F;
		float f3 = 0.0F;
		float f5 = 0.0F;
		float f7 = 0.0F;
		float f9 = 0.0F;
		float f11 = 0.0F;
		Object3D object3d = new Object3D(-1);
		int i = 0;
		int j = 0;
		boolean flag1 = false;
		float af[] = new float[Config.loadMaxVerticesASC];
		float af1[] = new float[Config.loadMaxVerticesASC];
		float af2[] = new float[Config.loadMaxVerticesASC];
		int ai[] = new int[Config.loadMaxTrianglesASC];
		int ai1[] = new int[Config.loadMaxTrianglesASC];
		int ai2[] = new int[Config.loadMaxTrianglesASC];
		float af3[] = new float[1];
		float af4[] = new float[1];
		int k = 0;
		boolean flag2 = false;
		int i1 = 0;
		boolean flag3 = false;
		boolean flag4 = false;
		int l1 = 1;
		String s1 = "";
		String s2 = "";
		String s3 = "";
		String s6 = loadTextFile(url, s, inputstream);
		if (!s6.endsWith("\n"))
			s6 = s6 + "\n";
		if (!s6.equals("error")) {
			Logger.log("Parsing Objectfile!", 2);
			if (s6.indexOf("U:") != -1) {
				flag1 = true;
				af3 = new float[Config.loadMaxTrianglesASC];
				af4 = new float[Config.loadMaxTrianglesASC];
				Logger.log("Objectfile contains additional Texture coordinates!", 2);
			}
			String s7 = "";
			int i2 = 0;
			int j2 = 0;
			int k2 = s6.indexOf("Vertex list");
			for (k2 = s6.indexOf("\n", k2 + 10); k2 != -1; k2 = s6.indexOf("Vertex list", j2)) {
				int l = k;
				do {
					if (k2 == -1)
						break;
					k2++;
					int l2 = s6.indexOf("\n", k2);
					i2 = l2 + 1;
					j2 = k2;
					String s8 = s6.substring(k2, i2);
					if (s8.indexOf("X:") != -1) {
						int k3 = s8.indexOf("X:", 8);
						k3 += 2;
						k2 = s8.indexOf("Y:", k3);
						String s10 = s8.substring(k3, k2).trim();
						int i4 = s8.indexOf("Z:", k2 + 2);
						String s12 = s8.substring(k2 + 2, i4).trim();
						int k4 = s8.indexOf("U:", i4 + 2);
						flag1 = false;
						String s4;
						if (k4 != -1) {
							s4 = s8.substring(i4 + 2, k4).trim();
							int j1 = s8.indexOf("V:", k4 + 2);
							s2 = s8.substring(k4 + 2, j1).trim();
							int k1 = s8.indexOf("\n", j1 + 2);
							s1 = s8.substring(j1 + 2, k1).trim();
							k4 = k1;
							flag1 = true;
						} else {
							int l4 = s8.indexOf("\n", i4 + 2);
							s4 = s8.substring(i4 + 2, l4).trim();
						}
						if (flag) {
							Float float1 = Float.valueOf(s10);
							Float float3 = Float.valueOf(s12);
							Float float5 = Float.valueOf(s4);
							af[k] = float1.floatValue() * f;
							af1[k] = -float5.floatValue() * f;
							af2[k] = float3.floatValue() * f;
						} else {
							Float float2 = Float.valueOf(s10);
							Float float4 = Float.valueOf(s12);
							Float float6 = Float.valueOf(s4);
							af[k] = float2.floatValue() * f;
							af1[k] = -float4.floatValue() * f;
							af2[k] = -float6.floatValue() * f;
						}
						if (flag1) {
							af3[k] = Float.valueOf(s2).floatValue();
							af4[k] = Float.valueOf(s1).floatValue();
						}
						k++;
						k2 = l2;
					} else {
						k2 = l2;
						if (s8.indexOf("Face list") != -1)
							k2 = -1;
					}
				} while (true);
				k2 = i2 - 1;
				do {
					if (k2 == -1)
						break;
					k2++;
					int i3 = s6.indexOf("\n", k2);
					i2 = i3 + 1;
					j2 = k2;
					String s9 = s6.substring(k2, i2);
					if (s9.indexOf("A:") != -1) {
						int l3 = s9.indexOf("A:", 5);
						l3 += 2;
						k2 = s9.indexOf("B:", l3);
						String s11 = s9.substring(l3, k2).trim();
						int j4 = s9.indexOf("C:", k2 + 2);
						String s13 = s9.substring(k2 + 2, j4).trim();
						int i5 = 0;
						i5 = s9.indexOf("AB:", j4 + 2);
						String s5 = s9.substring(j4 + 2, i5).trim();
						Integer integer = Integer.valueOf(s11);
						Integer integer1 = Integer.valueOf(s13);
						Integer integer2 = Integer.valueOf(s5);
						ai[i1] = integer.intValue() + l;
						ai1[i1] = integer1.intValue() + l;
						ai2[i1] = integer2.intValue() + l;
						i1++;
						k2 = i3;
					} else {
						k2 = i3;
						if (s9.indexOf("Vertex list") != -1)
							k2 = -1;
						if (i2 >= s6.length())
							k2 = -1;
					}
				} while (true);
				Logger.log("Part: " + l1 + " / Faces: " + i1 + " / Vertices: " + k, 2);
				l1++;
			}

			object3d.objMesh = new Mesh((i1 * 2 + 1) * 3 + 8);
			object3d.objVectors = new Vectors((i1 + 1) * 3 + 8, object3d.objMesh);
			object3d.texture = new int[i1 + 1];
			object3d.basemap = new int[i1 + 1];
			if (!optimize)
				object3d.disableVertexSharing();
			if (!Config.saveMemory) {
				object3d.bumpmap = new int[i1 + 1];
				object3d.sector = new int[i1 + 1];
			}
			for (int j3 = 0; j3 < i1; j3++) {
				float f13 = 1.0F;
				float f14 = 1.0F;
				float f15 = f13;
				float f16 = f14;
				float f2;
				float f4;
				float f6;
				float f8;
				float f10;
				float f12;
				if (!flag1) {
					if ((j3 & 1) == 1) {
						f2 = 0.0F;
						f4 = 0.0F;
						f6 = f13;
						f8 = 0.0F;
						f10 = 0.0F;
						f12 = f14;
					} else {
						f2 = f13;
						f4 = 0.0F;
						f6 = f13;
						f8 = f14;
						f10 = 0.0F;
						f12 = f14;
					}
				} else {
					f2 = f15 * af3[ai[j3]];
					f4 = f16 - f16 * af4[ai[j3]];
					f6 = f15 * af3[ai1[j3]];
					f8 = f16 - f16 * af4[ai1[j3]];
					f10 = f15 * af3[ai2[j3]];
					f12 = f16 - f16 * af4[ai2[j3]];
					if (f2 < 0.0F)
						f2 = 0.0F;
					if (f2 > f13)
						f2 = f13;
					if (f4 < 0.0F)
						f4 = 0.0F;
					if (f4 > f14)
						f4 = f14;
					if (f6 < 0.0F)
						f6 = 0.0F;
					if (f6 > f13)
						f6 = f13;
					if (f8 < 0.0F)
						f8 = 0.0F;
					if (f8 > f14)
						f8 = f14;
					if (f10 < 0.0F)
						f10 = 0.0F;
					if (f10 > f13)
						f10 = f13;
					if (f12 < 0.0F)
						f12 = 0.0F;
					if (f12 > f14)
						f12 = f14;
				}
				j = object3d.addTriangle(af[ai[j3]], af1[ai[j3]], af2[ai[j3]], f2, f4, af[ai1[j3]], af1[ai1[j3]], af2[ai1[j3]],
						f6, f8, af[ai2[j3]], af1[ai2[j3]], af2[ai2[j3]], f10, f12, i, 0, false);
			}

			Logger.log("Loaded Object3D: Faces: " + i1 + " / Vertices: " + k, 2);
			Logger.log("Optimized Object3D: Faces: " + (j + 1) + " / Vertices: " + object3d.objMesh.anzCoords, 2);
			return object3d;
		} else {
			return null;
		}
	}

	public static Object3D loadJAW(String s, float f, boolean flag) {
		return loadJAW(null, s, null, f, flag);
	}

	public static Object3D loadJAW(URL url, String s, float f, boolean flag) {
		return loadJAW(url, s, null, f, flag);
	}

	public static Object3D loadJAW(InputStream inputstream, float f, boolean flag) {
		return loadJAW(null, null, inputstream, f, flag);
	}

	public static void clearCache() {
		lastFileData = null;
		lastFilename = null;
	}

	private static Object3D loadJAW(URL url, String s, InputStream inputstream, float f, boolean flag) {
		float f1 = 0.0F;
		float f3 = 0.0F;
		float f5 = 0.0F;
		float f7 = 0.0F;
		float f9 = 0.0F;
		float f11 = 0.0F;
		int i = 0;
		Object3D object3d = new Object3D(-1);
		TextureManager texturemanager = TextureManager.getInstance();
		int j = 0;
		int k = 0;
		String s1 = "";
		String s3 = "";
		String s6 = loadTextFile(url, s, inputstream);
		if (!s6.endsWith("\n"))
			s6 = s6 + "\n";
		int l = getCharCount(s6, ':') * 3;
		int i1 = getCharCount(s6, 't');
		float af[] = new float[l];
		float af1[] = new float[l];
		float af2[] = new float[l];
		int ai[] = new int[i1];
		int ai1[] = new int[i1];
		int ai2[] = new int[i1];
		String as[] = new String[i1];
		boolean flag1 = false;
		if (!s6.equals("error")) {
			Logger.log("Parsing Objectfile!", 2);
			for (int j1 = s6.indexOf(":"); j1 != -1;) {
				while (j1 != -1) {
					int k1 = j1 + 2;
					j1 = s6.indexOf(" ", k1);
					String s7 = s6.substring(k1, j1).trim();
					int k2 = s6.indexOf(" ", j1 + 1);
					String s9 = s6.substring(j1, k2).trim();
					int j3 = s6.indexOf("\n", k2 + 1);
					String s4 = s6.substring(k2, j3).trim();
					j1 = s6.indexOf(":", j3 + 1);
					Float float1 = Float.valueOf(s7);
					Float float2 = Float.valueOf(s9);
					Float float3 = Float.valueOf(s4);
					af[j] = float1.floatValue() * f;
					af1[j] = -float2.floatValue() * f;
					af2[j] = -float3.floatValue() * f;
					j++;
				}
				for (j1 = s6.indexOf("tri"); j1 != -1;) {
					int l1 = j1 + 4;
					j1 = s6.indexOf(",", l1);
					String s8 = s6.substring(l1, j1).trim();
					int l2 = s6.indexOf(",", j1 + 1);
					String s10 = s6.substring(j1 + 1, l2).trim();
					int k3 = 0;
					String s2;
					String s5;
					if (!flag) {
						k3 = s6.indexOf("\n", l2 + 1);
						s5 = s6.substring(l2 + 1, k3).trim();
						s2 = null;
					} else {
						k3 = s6.indexOf(",", l2 + 1);
						s5 = s6.substring(l2 + 1, k3).trim();
						int l3 = s6.indexOf("\n", k3 + 1);
						s2 = s6.substring(k3 + 1, l3).trim();
						k3 = l3;
					}
					j1 = s6.indexOf("tri", k3 + 1);
					Integer integer = Integer.valueOf(s8);
					Integer integer1 = Integer.valueOf(s10);
					Integer integer2 = Integer.valueOf(s5);
					ai[k] = integer.intValue();
					ai1[k] = integer1.intValue();
					ai2[k] = integer2.intValue();
					as[k] = s2;
					k++;
				}

				object3d.objMesh = new Mesh((k * 2 + 1) * 3 + 8);
				object3d.objVectors = new Vectors((k + 1) * 3 + 8, object3d.objMesh);
				object3d.texture = new int[k + 1];
				object3d.basemap = new int[k + 1];
				if (!Config.saveMemory) {
					object3d.bumpmap = new int[k + 1];
					object3d.sector = new int[k + 1];
				}
				int i2 = 0;
				while (i2 < k) {
					int j2 = 0;
					if (as[i2] != null)
						j2 = texturemanager.getTextureID(as[i2]);
					int i3 = j2;
					float f13 = 1.0F;
					float f14 = 1.0F;
					float f2;
					float f4;
					float f6;
					float f8;
					float f10;
					float f12;
					if ((i2 & 1) == 1) {
						f2 = f13;
						f4 = f14;
						f6 = f13;
						f8 = 0.0F;
						f10 = 0.0F;
						f12 = 0.0F;
					} else {
						f2 = 0.0F;
						f4 = f14;
						f6 = f13;
						f8 = f14;
						f10 = 0.0F;
						f12 = 0.0F;
					}
					i = object3d.addTriangle(af[ai[i2]], af1[ai[i2]], af2[ai[i2]], f2, f4, af[ai1[i2]], af1[ai1[i2]],
							af2[ai1[i2]], f6, f8, af[ai2[i2]], af1[ai2[i2]], af2[ai2[i2]], f10, f12, i3, 0, false);
					i2++;
				}
			}

			Logger.log("Loaded Object3D: Faces: " + k + " / Vertices: " + j, 2);
			Logger.log("Optimized Object3D: Faces: " + (i + 1) + " / Vertices: " + object3d.objMesh.anzCoords, 2);
			return object3d;
		} else {
			return null;
		}
	}

	private static int getInt(byte abyte0[], int i) {
		if (i + 3 < abyte0.length) {
			int j = unsignedByteToInt(abyte0[i]);
			int k = unsignedByteToInt(abyte0[i + 1]);
			int l = unsignedByteToInt(abyte0[i + 2]);
			int i1 = unsignedByteToInt(abyte0[i + 3]);
			return j + (k << 8) + (l << 16) + (i1 << 24);
		} else {
			return -1;
		}
	}

	private static int getShortInt(byte abyte0[], int i) {
		if (i + 1 < abyte0.length) {
			int j = unsignedByteToInt(abyte0[i]);
			int k = unsignedByteToInt(abyte0[i + 1]);
			return j + (k << 8);
		} else {
			return -1;
		}
	}

	private static int getUnsignedByte(byte abyte0[], int i) {
		if (i < abyte0.length)
			return unsignedByteToInt(abyte0[i]);
		else
			return -1;
	}

	private static int unsignedByteToInt(byte byte0) {
		return byte0 & 0xff;
	}

	private static String getSequenceName(String s) {
		byte byte0 = 32;
		StringBuffer stringbuffer = new StringBuffer(16);
		s = s.toLowerCase();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 'a' && c <= 'z')
				stringbuffer.append(c);
		}

		return stringbuffer.toString();
	}

	private static byte[] loadBinaryFile(URL url, String s, InputStream inputstream, int i) {
		return loadBinaryFile(url, s, inputstream, i, null);
	}

	private static byte[] loadBinaryFile(URL url, String s, InputStream inputstream, int i, String as[]) {
		byte abyte0[];
		int l;
		int i1;
		int j1;
		abyte0 = new byte[i];
		boolean flag = false;
		l = 0;
		i1 = i;
		j1 = 0;
		if (s == null)
			s = "from InputStream";
		try {
			Logger.log("Loading file " + s, 2);
			if (inputstream == null)
				if (url == null) {
					inputstream = new FileInputStream(new File(s));
				} else {
					URL url1 = new URL(url, s);
					inputstream = url1.openStream();
				}
			if (as == null) {
				int j;
				do {
					j = inputstream.read(abyte0, l, i1 - l);
					if (j > 0)
						l += j;
					if (j != -1 && l >= i1) {
						if (++j1 == 10)
							i *= 10;
						i1 += i;
						byte abyte1[] = new byte[i1];
						System.arraycopy(abyte0, 0, abyte1, 0, i1 - i);
						abyte0 = abyte1;
						Logger.log("Expanding buffers..." + i1 + " bytes", 2);
					}
				} while (j > 0);
				Logger.log("File " + s + " loaded..." + l + " bytes", 2);
			} else {
				StringBuffer stringbuffer = new StringBuffer();
				int k;
				do {
					k = inputstream.read(abyte0, 0, i1);
					if (k > 0)
						stringbuffer.append(new String(abyte0, 0, k));
				} while (k > 0);
				as[0] = stringbuffer.toString();
				Logger.log("Text file " + s + " loaded..." + stringbuffer.length() + " bytes", 2);
			}
		} catch (Exception localException1) {
			byte abyte3[];
			Logger.log("Couldn't read file " + s, 0);
			abyte3 = new byte[0];
			return abyte3;
		} finally {
			if (inputstream != null)
				try {
					inputstream.close();
				} catch (Exception exception2) {
				}
		}
		if (as == null) {
			byte abyte2[] = new byte[l];
			System.arraycopy(abyte0, 0, abyte2, 0, l);
			return abyte2;
		} else {
			return null;
		}
	}

	private static void loadSceneFromXML(URL url, String s, InputStream inputstream, World world) {
		try {
			String s1 = loadTextFile(url, s, inputstream);
			clearCache();
			XMLFactory xmlfactory = XMLFactory.getInstance();
			Logger.log("Building XML-tree...", 2);
			XMLNode xmlnode = xmlfactory.parseXML(s1);
			s1 = null;
			Logger.log("Building scene...", 2);
			Vector vector = xmlfactory.getMatchingNodes("/jpct/light_list/light/", xmlnode);
			for (int i = 0; i < vector.size(); i++) {
				XMLNode xmlnode1 = (XMLNode) vector.elementAt(i);
				float f = Float.valueOf(xmlnode1.getAttributeValue("x")).floatValue();
				float f1 = Float.valueOf(xmlnode1.getAttributeValue("y")).floatValue();
				float f2 = Float.valueOf(xmlnode1.getAttributeValue("z")).floatValue();
				float f3 = Float.valueOf(xmlnode1.getAttributeValue("r")).floatValue();
				float f4 = Float.valueOf(xmlnode1.getAttributeValue("g")).floatValue();
				float f5 = Float.valueOf(xmlnode1.getAttributeValue("b")).floatValue();
				world.addLight(new SimpleVector(f, f1, f2), f3, f4, f5);
			}

			Vector vector1 = xmlfactory.getMatchingNodes("/jpct/texture_list/texture/", xmlnode);
			int j = 0;
			boolean flag = false;
			for (int i1 = 0; i1 < vector1.size(); i1++) {
				XMLNode xmlnode2 = (XMLNode) vector1.elementAt(i1);
				int k = Integer.parseInt(xmlnode2.getAttributeValue("id"));
				if (k > j)
					j = k;
			}

			int ai[] = new int[j + 1];
			TextureManager texturemanager = TextureManager.getInstance();
			for (int j1 = 0; j1 < vector1.size(); j1++) {
				XMLNode xmlnode3 = (XMLNode) vector1.elementAt(j1);
				int l = Integer.parseInt(xmlnode3.getAttributeValue("id"));
				String s2 = xmlnode3.getAttributeValue("name");
				if (s2 == null)
					s2 = "!missing!";
				int l1 = texturemanager.getTextureID(s2);
				if (l1 == -1) {
					texturemanager.addTexture(s2);
					l1 = texturemanager.getTextureID(s2);
					Logger.log("Texture named '" + s2 + "' added to TextureManager!", 2);
				}
				ai[l] = l1;
			}

			vector1 = null;
			vector = null;
			Vector vector2 = xmlfactory.getMatchingNodes("/jpct/object_list/object/", xmlnode);
			for (int k1 = 0; k1 < vector2.size(); k1++) {
				XMLNode xmlnode4 = (XMLNode) vector2.elementAt(k1);
				String s3 = xmlnode4.getAttributeValue("triangles");
				String s4 = xmlnode4.getAttributeValue("name");
				String s5 = xmlnode4.getAttributeValue("insector");
				int k2 = 0;
				if (s3.length() > 0)
					k2 = Integer.parseInt(s3);
				else
					Logger.log("Missing triangle count for object " + k1 + "!", 0);
				boolean flag1 = false;
				s3 = xmlnode4.getAttributeValue("main");
				if (s3.length() > 0) {
					int i3 = Integer.parseInt(s3);
					flag1 = i3 == 1;
				}
				Object3D object3d = new Object3D(k2);
				Vector vector5 = xmlfactory.getMatchingNodes("object/triangle_list/", xmlnode4);
				for (int l3 = 0; l3 < vector5.size(); l3++) {
					XMLNode xmlnode8 = (XMLNode) vector5.elementAt(l3);
					String s7 = xmlnode8.getAttributeValue("sector");
					int k4 = -1;
					if (s7.length() > 0)
						k4 = Integer.parseInt(s7);
					Vector vector7 = xmlfactory.getMatchingNodes("triangle_list/triangle/", xmlnode8);
					for (int l4 = 0; l4 < vector7.size(); l4++) {
						XMLNode xmlnode10 = (XMLNode) vector7.elementAt(l4);
						Vector vector9 = xmlfactory.getMatchingNodes("triangle/coord/", xmlnode10);
						if (vector9.size() == 3) {
							XMLNode xmlnode12 = (XMLNode) vector9.elementAt(0);
							float f14 = Float.valueOf(xmlnode12.getAttributeValue("x")).floatValue();
							float f16 = Float.valueOf(xmlnode12.getAttributeValue("y")).floatValue();
							float f17 = Float.valueOf(xmlnode12.getAttributeValue("z")).floatValue();
							float f18 = Float.valueOf(xmlnode12.getAttributeValue("u")).floatValue();
							float f19 = Float.valueOf(xmlnode12.getAttributeValue("v")).floatValue();
							xmlnode12 = (XMLNode) vector9.elementAt(1);
							float f20 = Float.valueOf(xmlnode12.getAttributeValue("x")).floatValue();
							float f21 = Float.valueOf(xmlnode12.getAttributeValue("y")).floatValue();
							float f22 = Float.valueOf(xmlnode12.getAttributeValue("z")).floatValue();
							float f23 = Float.valueOf(xmlnode12.getAttributeValue("u")).floatValue();
							float f24 = Float.valueOf(xmlnode12.getAttributeValue("v")).floatValue();
							xmlnode12 = (XMLNode) vector9.elementAt(2);
							float f25 = Float.valueOf(xmlnode12.getAttributeValue("x")).floatValue();
							float f26 = Float.valueOf(xmlnode12.getAttributeValue("y")).floatValue();
							float f27 = Float.valueOf(xmlnode12.getAttributeValue("z")).floatValue();
							float f28 = Float.valueOf(xmlnode12.getAttributeValue("u")).floatValue();
							float f29 = Float.valueOf(xmlnode12.getAttributeValue("v")).floatValue();
							int j5 = 0;
							Vector vector10 = xmlfactory.getMatchingNodes("triangle/textures/", xmlnode10);
							if (vector10.size() == 1) {
								XMLNode xmlnode13 = (XMLNode) vector10.elementAt(0);
								Vector vector11 = xmlfactory.getMatchingNodes("textures/texturemap/", xmlnode13);
								if (vector11.size() > 0) {
									XMLNode xmlnode14 = (XMLNode) vector11.elementAt(0);
									j5 = ai[Integer.parseInt(xmlnode14.getAttributeValue("texid"))];
								} else {
									j5 = texturemanager.getTextureID("--dummy--");
								}
							} else {
								if (vector10.size() > 1)
									Logger.log("Only one texture block is allowed per triangle!", 0);
								j5 = texturemanager.getTextureID("--dummy--");
							}
							if (k4 != -1)
								object3d.addTriangle(f14, f16, f17, f18, f19, f20, f21, f22, f23, f24, f25, f26, f27, f28, f29, j5, k4,
										false);
							else
								object3d.addTriangle(f14, f16, f17, f18, f19, f20, f21, f22, f23, f24, f25, f26, f27, f28, f29, j5);
						} else {
							Logger.log("Corrupted triangle found in XML-file!", 0);
						}
					}

				}

				if (s4 != null && s4.length() > 0)
					object3d.setName(s4);
				int i4 = world.addObject(object3d);
				if (flag1)
					world.setMainObjectID(i4);
				if (s5.length() > 0) {
					String s6 = s5.toLowerCase();
					if (s6.equals("auto"))
						object3d.setSectorDetectionMode(true);
					else if (!s6.equals("all"))
						try {
							object3d.setSectorDetectionMode(false);
							object3d.setSector(Integer.parseInt(s5));
						} catch (Exception exception1) {
							Logger.log("Number format error in XML: " + s6, 1);
						}
				}
				object3d.build();
				Vector vector6 = xmlfactory.getMatchingNodes("object/attributes/", xmlnode4);
				for (int j4 = 0; j4 < vector6.size(); j4++) {
					XMLNode xmlnode9 = (XMLNode) vector6.elementAt(j4);
					Vector vector8 = xmlnode9.getSubNodes();
					if (vector8 == null)
						continue;
					for (int i5 = 0; i5 < vector8.size(); i5++) {
						XMLNode xmlnode11 = (XMLNode) vector8.elementAt(i5);
						float f12 = Float.valueOf(xmlnode11.getAttributeValue("x")).floatValue();
						float f13 = Float.valueOf(xmlnode11.getAttributeValue("y")).floatValue();
						float f15 = Float.valueOf(xmlnode11.getAttributeValue("z")).floatValue();
						if (xmlnode11.getName().equals("pivot"))
							object3d.setRotationPivot(new SimpleVector(f12, f13, f15));
						if (xmlnode11.getName().equals("center"))
							object3d.setCenter(new SimpleVector(f12, f13, f15));
						if (xmlnode11.getName().equals("origin"))
							object3d.setOrigin(new SimpleVector(f12, f13, f15));
					}

				}

			}

			Vector vector3 = xmlfactory.getMatchingNodes("/jpct/portal_list/portal/", xmlnode);
			Portals portals = world.getPortals();
			for (int i2 = 0; i2 < vector3.size(); i2++) {
				XMLNode xmlnode5 = (XMLNode) vector3.elementAt(i2);
				int j2 = Integer.parseInt(xmlnode5.getAttributeValue("from"));
				int l2 = Integer.parseInt(xmlnode5.getAttributeValue("to"));
				boolean flag2 = xmlnode5.getAttributeValue("type").charAt(0) == 's';
				Vector vector4 = xmlfactory.getMatchingNodes("portal/coord/", xmlnode5);
				portals.startNewPortal();
				for (int j3 = 0; j3 < vector4.size(); j3++) {
					XMLNode xmlnode6 = (XMLNode) vector4.elementAt(j3);
					float f6 = Float.valueOf(xmlnode6.getAttributeValue("x")).floatValue();
					float f8 = Float.valueOf(xmlnode6.getAttributeValue("y")).floatValue();
					float f10 = Float.valueOf(xmlnode6.getAttributeValue("z")).floatValue();
					portals.addPortalCoord(f6, f8, f10);
				}

				portals.setPortalAttributes(j2, l2);
				portals.completePortal();
				if (!flag2) {
					portals.startNewPortal();
					for (int k3 = vector4.size() - 1; k3 >= 0; k3--) {
						XMLNode xmlnode7 = (XMLNode) vector4.elementAt(k3);
						float f7 = Float.valueOf(xmlnode7.getAttributeValue("x")).floatValue();
						float f9 = Float.valueOf(xmlnode7.getAttributeValue("y")).floatValue();
						float f11 = Float.valueOf(xmlnode7.getAttributeValue("z")).floatValue();
						portals.addPortalCoord(f7, f9, f11);
					}

					portals.setPortalAttributes(l2, j2);
					portals.completePortal();
				}
			}

		} catch (Exception exception) {
			Logger.log("Incorrect XML-file!", 0);
			exception.printStackTrace();
		}
	}

	private static Object3D loadMD2(URL url, String s, InputStream inputstream, float f) {
		Vector avector[] = null;
		boolean flag = false;
		byte abyte0[] = loadBinaryFile(url, s, inputstream, 0x7a120);
		if (abyte0 == null)
			flag = true;
		if (!flag) {
			int i = getInt(abyte0, 0);
			if (i != 0x32504449)
				Logger.log("Not a valid MD2-file!", 0);
			int j = getInt(abyte0, 4);
			int k = getInt(abyte0, 8);
			int l = getInt(abyte0, 12);
			int i1 = getInt(abyte0, 16);
			int j1 = getInt(abyte0, 20);
			int k1 = getInt(abyte0, 24);
			int l1 = getInt(abyte0, 28);
			int i2 = getInt(abyte0, 32);
			int j2 = getInt(abyte0, 36);
			int k2 = getInt(abyte0, 40);
			int l2 = getInt(abyte0, 48);
			int i3 = getInt(abyte0, 52);
			int j3 = getInt(abyte0, 56);
			Logger.log("Magic number: " + i, 2);
			Logger.log("Version: " + j, 2);
			Logger.log("Skin width: " + k, 2);
			Logger.log("Skin height: " + l, 2);
			Logger.log("Frame size: " + i1, 2);
			Logger.log("Number of skins: " + j1, 2);
			Logger.log("Number of Vertices: " + k1, 2);
			Logger.log("Number of Texture coordinates: " + l1, 2);
			Logger.log("Number of triangles: " + i2, 2);
			Logger.log("Number of GL-commands: " + j2, 2);
			Logger.log("Number of Frames: " + k2, 2);
			int ai[][] = new int[l1][2];
			int ai1[][] = new int[i2][3];
			int ai2[][] = new int[i2][3];
			Logger.log("Reading Texture coordinates...", 2);
			int k3 = l2;
			for (int i4 = 0; i4 < l1; i4++) {
				int k4 = getShortInt(abyte0, k3 + i4 * 4);
				int i5 = getShortInt(abyte0, k3 + i4 * 4 + 2);
				ai[i4][0] = k4;
				ai[i4][1] = i5;
			}

			Logger.log("Done!", 2);
			Logger.log("Reading polygonal data...", 2);
			k3 = i3;
			for (int j4 = 0; j4 < i2; j4++) {
				int l4 = k3 + j4 * 12;
				int j5 = getShortInt(abyte0, l4);
				int k5 = getShortInt(abyte0, l4 + 2);
				int l5 = getShortInt(abyte0, l4 + 4);
				int j6 = getShortInt(abyte0, l4 + 6);
				int k6 = getShortInt(abyte0, l4 + 8);
				int l6 = getShortInt(abyte0, l4 + 10);
				ai1[j4][0] = j5;
				ai1[j4][1] = k5;
				ai1[j4][2] = l5;
				ai2[j4][0] = j6;
				ai2[j4][1] = k6;
				ai2[j4][2] = l6;
			}

			Logger.log("Done!", 2);
			float af[][] = new float[k2][3];
			float af1[][] = new float[k2][3];
			String as[] = new String[k2];
			int ai3[][][] = new int[k2][k1][3];
			Logger.log("Reading keyframes...", 2);
			for (int i6 = 0; i6 < k2; i6++) {
				int l3 = i6 * i1 + j3;
				float f1 = Float.intBitsToFloat(getInt(abyte0, l3));
				float f2 = Float.intBitsToFloat(getInt(abyte0, l3 + 4));
				float f3 = Float.intBitsToFloat(getInt(abyte0, l3 + 8));
				float f4 = Float.intBitsToFloat(getInt(abyte0, l3 + 12));
				float f5 = Float.intBitsToFloat(getInt(abyte0, l3 + 16));
				float f6 = Float.intBitsToFloat(getInt(abyte0, l3 + 20));
				String s3 = new String(abyte0, l3 + 24, 16);
				af[i6][0] = f1;
				af[i6][1] = f2;
				af[i6][2] = f3;
				af1[i6][0] = f4;
				af1[i6][1] = f5;
				af1[i6][2] = f6;
				as[i6] = s3;
				l3 += 40;
				for (int j8 = 0; j8 < k1; j8++) {
					int l8 = l3 + j8 * 4;
					int j9 = getUnsignedByte(abyte0, l8);
					int l9 = getUnsignedByte(abyte0, l8 + 1);
					int j10 = getUnsignedByte(abyte0, l8 + 2);
					ai3[i6][j8][0] = j9;
					ai3[i6][j8][1] = l9;
					ai3[i6][j8][2] = j10;
				}

			}

			Logger.log("Done!", 2);
			Logger.log("Coverting MD2-format into jPCT-format...", 2);
			Object3D object3d = new Object3D(i2 + 1);
			Object3D object3d1 = new Object3D(i2 + 1);
			Animation animation = new Animation(k2);
			String s1 = "dummy";
			for (int i7 = 0; i7 < k2; i7++) {
				object3d1.clearObject();
				int j7 = i7;
				for (int k7 = 0; k7 < k1; k7++) {
					float f7 = (float) ai3[j7][k7][0] * af[j7][0] + af1[j7][0];
					float f8 = (float) ai3[j7][k7][1] * af[j7][1] + af1[j7][1];
					float f9 = (float) ai3[j7][k7][2] * af[j7][2] + af1[j7][2];
					f7 *= f;
					f8 *= f;
					f9 *= f;
					object3d1.objVectors.addVertex(f7, -f9, f8);
					if (i7 == 0)
						object3d.objVectors.addVertex(f7, -f9, f8);
				}

				for (int l7 = 0; l7 < i2; l7++) {
					int i8 = ai1[l7][0];
					int k8 = ai1[l7][2];
					int i9 = ai1[l7][1];
					int k9 = ai2[l7][0];
					int i10 = ai2[l7][2];
					int k10 = ai2[l7][1];
					float f10 = (float) ai[k9][0] / (float) k;
					float f11 = (float) ai[k9][1] / (float) l;
					float f12 = (float) ai[i10][0] / (float) k;
					float f13 = (float) ai[i10][1] / (float) l;
					float f14 = (float) ai[k10][0] / (float) k;
					float f15 = (float) ai[k10][1] / (float) l;
					if (i7 == 0)
						object3d.addMD2Triangle(i8, f10, f11, k8, f12, f13, i9, f14, f15);
					object3d1.addMD2Triangle(i8, f10, f11, k8, f12, f13, i9, f14, f15);
				}

				object3d1.calcBoundingBox();
				avector = object3d1.objMesh.calcNormalsMD2(avector);
				String s2 = getSequenceName(as[i7]);
				if (!s2.equals(s1)) {
					Logger.log("Processing: " + s2 + "...", 2);
					s1 = s2;
					animation.createSubSequence(s2);
				}
				animation.addKeyFrame(object3d1.getMesh().cloneMesh(true));
			}

			object3d.calcBoundingBox();
			object3d.setAnimationSequence(animation);
			Logger.log("Done!", 2);
			return object3d;
		} else {
			return null;
		}
	}

	private static String[] readTextureNames3DS(URL url, String s, InputStream inputstream) {
		byte abyte0[] = loadBinaryFile(url, s, inputstream, 0x7a120);
		int ai[] = new int[2];
		int ai1[] = new int[2];
		int i = 0;
		byte byte0 = -1;
		byte byte1 = -1;
		Vector vector = new Vector();
		Object obj = null;
		if (abyte0 != null) {
			getChunkHeader(abyte0, i, ai);
			i += 6;
			int j = ai[0];
			int k = ai[1];
			if (j != 19789) {
				Logger.log("Not a valid 3DS file!", 0);
			} else {
				boolean flag = true;
				do {
					if (j < 0 || i >= abyte0.length)
						break;
					boolean flag1 = false;
					getChunkHeader(abyte0, i, ai);
					i += 6;
					j = ai[0];
					int l = ai[1];
					if (j >= 0 && i < abyte0.length) {
						if (j == 45055) {
							int l1;
							for (int i1 = i; i1 < (i + l) - 6 && i1 < abyte0.length; i1 += l1 - 6) {
								getChunkHeader(abyte0, i1, ai1);
								i1 += 6;
								int k1 = ai1[0];
								l1 = ai1[1];
								if (k1 != 41472)
									continue;
								int k2;
								for (int i2 = i1; i2 < (i1 + l) - 6 && i2 < abyte0.length; i2 += k2 - 6) {
									getChunkHeader(abyte0, i2, ai1);
									i2 += 6;
									int j2 = ai1[0];
									k2 = ai1[1];
									if (j2 != 41728)
										continue;
									StringBuffer stringbuffer = new StringBuffer(40);
									int l2 = 0;
									int i3 = i2;
									do {
										l2 = getUnsignedByte(abyte0, i3);
										i3++;
										if (l2 > 0)
											stringbuffer.append((char) (byte) l2);
										if (i3 >= abyte0.length)
											l2 = -1;
									} while (l2 > 0);
									String s1 = stringbuffer.toString();
									if (!vector.contains(s1))
										vector.addElement(s1);
								}

							}

							flag1 = true;
						}
						if (flag1)
							if ((i + l) - 6 >= abyte0.length)
								i = abyte0.length;
							else
								i += l - 6;
					}
				} while (true);
			}
		}
		String as[] = new String[vector.size()];
		for (int j1 = 0; j1 < as.length; j1++)
			as[j1] = (String) vector.elementAt(j1);

		return as;
	}

	private static Object3D[] load3DS(URL url, String s, InputStream inputstream, float f) {
		int i = Config.loadMaxVertices3DS;
		byte abyte0[] = loadBinaryFile(url, s, inputstream, 0x7a120);
		int ai[] = new int[2];
		int ai1[] = new int[2];
		int j = 0;
		byte byte0 = -1;
		byte byte1 = -1;
		Vector vector = new Vector();
		Object obj = null;
		float af[][] = new float[i][5];
		int ai2[][] = new int[i][3];
		String as[] = new String[i];
		float af1[][] = new float[i][4];
		String as1[] = new String[i];
		String as2[] = new String[i];
		Color acolor[] = new Color[i];
		int ai3[] = new int[i];
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		String s1 = "";
		String s2 = "";
		for (int j2 = 0; j2 < af1.length; j2++) {
			af1[j2][0] = 1.0F;
			af1[j2][1] = 1.0F;
			af1[j2][2] = 0.0F;
			af1[j2][3] = 0.0F;
			ai3[j2] = 1000;
		}

		if (abyte0 != null) {
			getChunkHeader(abyte0, j, ai);
			j += 6;
			int k = ai[0];
			int l = ai[1];
			if (k != 19789) {
				Logger.log("Not a valid 3DS file!", 0);
			} else {
				boolean flag1 = true;
				do {
					if (k < 0 || j >= abyte0.length || j < 0)
						break;
					boolean flag2 = true;
					getChunkHeader(abyte0, j, ai);
					j += 6;
					k = ai[0];
					int i1 = ai[1];
					if (k >= 0 && j < abyte0.length) {
						if (k == 16640) {
							if (l1 != 0)
								vector.addElement(create3DSObject(s2, af, j1, ai2, l1, as2, as1, af1, as, acolor, ai3, i2));
							Logger.log("Processing object from 3DS-file: " + s1, 2);
							j1 = 0;
							k1 = 0;
							l1 = 0;
							flag2 = false;
						}
						if (k == 16384) {
							int k2 = 0;
							StringBuffer stringbuffer = new StringBuffer(40);
							do {
								k2 = getUnsignedByte(abyte0, j);
								j++;
								if (k2 > 0)
									stringbuffer.append((char) (byte) k2);
								if (j >= abyte0.length)
									k2 = -1;
							} while (k2 > 0);
							s2 = s1;
							s1 = stringbuffer.toString();
							flag2 = false;
						}
						if (k == 16656) {
							int l4 = getShortInt(abyte0, j);
							j += 2;
							for (int k6 = 0; k6 < l4; k6++) {
								float f1 = Float.intBitsToFloat(getInt(abyte0, j)) * f;
								j += 4;
								float f3 = Float.intBitsToFloat(getInt(abyte0, j)) * f;
								j += 4;
								float f5 = Float.intBitsToFloat(getInt(abyte0, j)) * f;
								j += 4;
								af[j1][0] = f1;
								af[j1][1] = f3;
								af[j1][2] = f5;
								j1++;
							}

							flag2 = false;
						}
						if (k == 16672) {
							int l2 = j;
							int l6 = getShortInt(abyte0, l2);
							l2 += 2;
							for (int j8 = 0; j8 < l6; j8++) {
								int k3 = getShortInt(abyte0, l2);
								l2 += 2;
								int i4 = getShortInt(abyte0, l2);
								l2 += 2;
								int i5 = getShortInt(abyte0, l2);
								l2 += 2;
								l2 += 2;
								ai2[l1][0] = k3;
								ai2[l1][1] = i4;
								ai2[l1][2] = i5;
								l1++;
							}

							int k9;
							for (; l2 < (j + i1) - 6 && l2 < abyte0.length; l2 += k9 - 6) {
								getChunkHeader(abyte0, l2, ai1);
								l2 += 6;
								int k8 = ai1[0];
								k9 = ai1[1];
								if (k8 != 16688)
									continue;
								int k10 = 0;
								int j11 = l2;
								StringBuffer stringbuffer1 = new StringBuffer(40);
								do {
									k10 = getUnsignedByte(abyte0, j11);
									j11++;
									if (k10 > 0)
										stringbuffer1.append((char) (byte) k10);
									if (j11 >= abyte0.length)
										k10 = -1;
								} while (k10 > 0);
								int k11 = getShortInt(abyte0, j11);
								j11 += 2;
								String s4 = stringbuffer1.toString();
								for (int l10 = 0; l10 < k11; l10++) {
									int l11 = getShortInt(abyte0, j11);
									j11 += 2;
									as[l11] = s4;
								}

							}

							flag2 = true;
						}
						if (k == 16704) {
							int j4 = getShortInt(abyte0, j);
							j += 2;
							for (int j5 = 0; j5 < j4; j5++) {
								float f2 = Float.intBitsToFloat(getInt(abyte0, j));
								j += 4;
								float f4 = Float.intBitsToFloat(getInt(abyte0, j));
								j += 4;
								af[k1][3] = f2;
								af[k1][4] = f4;
								k1++;
							}

							flag2 = false;
						}
						if (k == 45055) {
							int k4;
							for (int i3 = j; i3 < (j + i1) - 6 && i3 < abyte0.length; i3 += k4 - 6) {
								getChunkHeader(abyte0, i3, ai1);
								i3 += 6;
								int l3 = ai1[0];
								k4 = ai1[1];
								if (l3 == 40960) {
									StringBuffer stringbuffer2 = new StringBuffer(40);
									int k5 = 0;
									int i7 = i3;
									do {
										k5 = getUnsignedByte(abyte0, i7);
										i7++;
										if (k5 > 0)
											stringbuffer2.append((char) (byte) k5);
										if (i7 >= abyte0.length)
											k5 = -1;
									} while (k5 > 0);
									as2[i2] = stringbuffer2.toString();
									Logger.log("Processing new material " + as2[i2] + "!", 2);
								}
								if (l3 == 40992) {
									int l5 = i3 + 6;
									if (k4 != 24 || Config.oldStyle3DSLoader) {
										int j7 = getUnsignedByte(abyte0, l5);
										l5++;
										int l8 = getUnsignedByte(abyte0, l5);
										l5++;
										int l9 = getUnsignedByte(abyte0, l5);
										l5++;
										acolor[i2] = new Color(j7, l8, l9);
									} else {
										int k7 = (int) (Float.intBitsToFloat(getInt(abyte0, l5)) * 255F);
										l5 += 4;
										int i9 = (int) (Float.intBitsToFloat(getInt(abyte0, l5)) * 255F);
										l5 += 4;
										int i10 = (int) (Float.intBitsToFloat(getInt(abyte0, l5)) * 255F);
										l5 += 4;
										boolean flag3 = false;
										if (k7 < 0) {
											k7 = 0;
											flag3 = true;
										}
										if (i9 < 0) {
											i9 = 0;
											flag3 = true;
										}
										if (i10 < 0) {
											i10 = 0;
											flag3 = true;
										}
										if (k7 > 255) {
											k7 = 255;
											flag3 = true;
										}
										if (i9 > 255) {
											i9 = 255;
											flag3 = true;
										}
										if (i10 > 255) {
											i10 = 255;
											flag3 = true;
										}
										if (flag3)
											Logger.log("Error reading material's diffuse color...try Config.oldStyle3DSLoader=true!", 1);
										acolor[i2] = new Color(k7, i9, i10);
									}
								}
								if (l3 == 41040) {
									int i6 = i3 + 6;
									int l7 = 100 - getShortInt(abyte0, i6);
									if (l7 < 0)
										l7 = 0;
									if (l7 > 100)
										l7 = 100;
									if (l7 == 100)
										l7 = -1;
									else
										l7 /= 10;
									ai3[i2] = l7;
								}
								if (l3 != 41472)
									continue;
								int j9;
								for (int j6 = i3; j6 < (i3 + i1) - 6 && j6 < abyte0.length; j6 += j9 - 6) {
									getChunkHeader(abyte0, j6, ai1);
									j6 += 6;
									int i8 = ai1[0];
									j9 = ai1[1];
									if (i8 == 41728) {
										StringBuffer stringbuffer3 = new StringBuffer(40);
										int j10 = 0;
										int i11 = j6;
										do {
											j10 = getUnsignedByte(abyte0, i11);
											i11++;
											if (j10 > 0)
												stringbuffer3.append((char) (byte) j10);
											if (i11 >= abyte0.length)
												j10 = -1;
										} while (j10 > 0);
										String s3 = stringbuffer3.toString();
										if (!TextureManager.getInstance().containsTexture(s3)) {
											TextureManager.getInstance().addTexture(s3);
											Logger.log("Texture named " + s3 + " added to TextureManager!", 2);
										}
										as1[i2] = s3;
									}
									if (i8 == 41812)
										af1[i2][0] = Float.intBitsToFloat(getInt(abyte0, j6));
									if (i8 == 41814)
										af1[i2][1] = Float.intBitsToFloat(getInt(abyte0, j6));
									if (i8 == 41816)
										af1[i2][2] = Float.intBitsToFloat(getInt(abyte0, j6));
									if (i8 == 41818)
										af1[i2][3] = Float.intBitsToFloat(getInt(abyte0, j6));
								}

							}

							i2++;
							flag2 = true;
						}
						if (k == 15677)
							flag2 = false;
						if (flag2)
							if ((j + i1) - 6 >= abyte0.length)
								j = abyte0.length;
							else
								j += i1 - 6;
					}
				} while (true);
			}
		}
		if (j1 != 0) {
			if (l1 != 0)
				vector.addElement(create3DSObject(s1, af, j1, ai2, l1, as2, as1, af1, as, acolor, ai3, i2));
			j1 = 0;
			boolean flag = false;
			l1 = 0;
		}
		Object3D aobject3d[] = new Object3D[vector.size()];
		for (int j3 = 0; j3 < aobject3d.length; j3++)
			aobject3d[j3] = (Object3D) vector.elementAt(j3);

		return aobject3d;
	}

	private static Object3D create3DSObject(String s, float af[][], int i, int ai[][], int j, String as[], String as1[],
			float af1[][], String as2[], Color acolor[], int ai1[], int k) {
		int l = j;
		int ai2[] = null;
		int ai3[] = null;
		if (!optimize) {
			ai2 = new int[af.length];
			ai3 = new int[3];
			for (int i1 = 0; i1 < ai2.length; i1++)
				ai2[i1] = -1;

		}
		Object3D object3d = new Object3D(l);
		if (!optimize)
			object3d.disableVertexSharing();
		TextureManager texturemanager = TextureManager.getInstance();
		int j1 = texturemanager.getTextureID("--dummy--");
		int k1 = 100;
		int l1 = -1;
		String s1 = "**hurzigurzi**";
		for (int i2 = 0; i2 < j; i2++) {
			int j2 = ai[i2][0];
			int k2 = ai[i2][1];
			int l2 = ai[i2][2];
			float f = af[j2][0];
			float f1 = af[j2][1];
			float f2 = af[j2][2];
			float f3 = af[k2][0];
			float f4 = af[k2][1];
			float f5 = af[k2][2];
			float f6 = af[l2][0];
			float f7 = af[l2][1];
			float f8 = af[l2][2];
			int i3 = j1;
			float f9 = 1.0F;
			float f10 = 1.0F;
			float f11 = 0.0F;
			float f12 = 0.0F;
			if (as2[i2] != null)
				if (as2[i2].equals(s1)) {
					i3 = texturemanager.getTextureID(as1[l1]);
					f9 = af1[l1][0];
					f10 = af1[l1][1];
					f11 = af1[l1][2];
					f12 = af1[l1][3];
				} else {
					int j3 = 0;
					do {
						if (j3 >= k)
							break;
						if (as[j3] != null && as[j3].equals(as2[i2])) {
							if (ai1[j3] != 1000 && k1 != -1)
								k1 = ai1[j3];
							else
								k1 = -1;
							if (as1[j3] != null) {
								s1 = as[j3];
								l1 = j3;
								i3 = texturemanager.getTextureID(as1[j3]);
								f9 = af1[j3][0];
								f10 = af1[j3][1];
								f11 = af1[j3][2];
								f12 = af1[j3][3];
							} else {
								String s2 = "__3ds-Color:" + acolor[j3].getRed() + "/" + acolor[j3].getGreen() + "/"
										+ acolor[j3].getBlue();
								if (texturemanager.containsTexture(s2)) {
									i3 = texturemanager.getTextureID(s2);
								} else {
									Texture texture = Texture.createSingleColoredTexture(acolor[j3]);
									texturemanager.addTexture(s2, texture);
									i3 = texturemanager.getTextureID(s2);
									as1[j3] = s2;
									l1 = j3;
									s1 = as[j3];
								}
							}
							break;
						}
						j3++;
					} while (true);
				}
			float f13 = af[j2][3] * f9 + f11;
			float f14 = af[j2][4] * f10 + f12;
			float f15 = af[k2][3] * f9 + f11;
			float f16 = af[k2][4] * f10 + f12;
			float f17 = af[l2][3] * f9 + f11;
			float f18 = af[l2][4] * f10 + f12;
			if (optimize) {
				object3d.addTriangle(f, f1, f2, f13, 1.0F - f14, f3, f4, f5, f15, 1.0F - f16, f6, f7, f8, f17, 1.0F - f18, i3);
				continue;
			}
			ai3[0] = ai2[j2];
			ai3[1] = ai2[k2];
			ai3[2] = ai2[l2];
			object3d.addTriangle(f, f1, f2, f13, 1.0F - f14, f3, f4, f5, f15, 1.0F - f16, f6, f7, f8, f17, 1.0F - f18, i3, 0,
					false, ai3);
			if (ai2[j2] == -1)
				ai2[j2] = ai3[0];
			if (ai2[k2] == -1)
				ai2[k2] = ai3[1];
			if (ai2[l2] == -1)
				ai2[l2] = ai3[2];
		}

		object3d.setName(s + "_jPCT" + object3d.getID());
		object3d.getMesh().compress();
		if (k1 != 1000 && k1 != -1)
			object3d.setTransparency(k1);
		Logger.log("Object '" + object3d.name + "' created using " + object3d.getMesh().anzTri + " polygons and "
				+ object3d.getMesh().anzCoords + " vertices.", 2);
		return object3d;
	}

	private static void getChunkHeader(byte abyte0[], int i, int ai[]) {
		ai[0] = getShortInt(abyte0, i);
		ai[1] = getInt(abyte0, i + 2);
	}

	private static int getCharCount(String s, char c) {
		int i = 0;
		for (int j = 0; j < s.length(); j++)
			if (s.charAt(j) == c)
				i++;

		return i;
	}

	private static final int DEFAULT_BUFFER = 0x7a120;
	private static final int MAX_CACHE_SIZE = 0xf4240;
	private static String lastFilename = "";
	private static String lastFileData = "";
	private static boolean optimize = true;

}
