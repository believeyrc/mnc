// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct.util;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Vector;

public final class XMLNode implements Serializable {

	protected XMLNode() {
		level = -1;
		name = null;
		data = null;
		level = -1;
		attributes = null;
		subNodes = null;
	}

	public String getData() {
		if (data != null)
			return data;
		else
			return "";
	}

	public Vector getSubNodes() {
		return subNodes;
	}

	public int getLevel() {
		return level;
	}

	public String getName() {
		return name;
	}

	public String getAttributeValue(String s) {
		String s1 = "";
		if (attributes != null) {
			int i = 0;
			do {
				if (i >= attributes.size())
					break;
				if (((String[]) attributes.elementAt(i))[0].equals(s)) {
					s1 = ((String[]) attributes.elementAt(i))[1];
					break;
				}
				i++;
			} while (true);
		}
		return s1;
	}

	public Vector getAttributes() {
		return attributes;
	}

	protected void setData(String s) {
		if (s != null && s.startsWith("<![CDATA[")) {
			s = s.substring(9);
			if (s.endsWith("]]>"))
				s = s.substring(0, s.length() - 3);
		}
		data = s;
	}

	protected void setLevel(int i) {
		level = i;
	}

	protected void setName(String s) {
		name = s;
	}

	protected void addAttribute(String s, String s1) {
		if (attributes == null)
			attributes = new Vector(3);
		attributes.addElement(new String[] { s, s1 });
	}

	protected void setAttributes(Vector vector) {
		attributes = vector;
	}

	protected void removeSubNode(int i) {
		if (subNodes != null)
			if (i >= 0 && i < subNodes.size())
				subNodes.removeElementAt(i);
			else
				System.out.println("Index out of range: " + i);
	}

	protected void removeAttribute(int i) {
		if (attributes != null)
			if (i >= 0 && i < attributes.size())
				attributes.removeElementAt(i);
			else
				System.out.println("Index out of range: " + i);
	}

	protected void addSubNode(XMLNode xmlnode) {
		if (subNodes == null)
			subNodes = new Vector(5);
		subNodes.addElement(xmlnode);
	}

	protected void setSubNodes(Vector vector) {
		subNodes = vector;
	}

	private static final long serialVersionUID = 1L;
	private String name;
	private Vector attributes;
	private Vector subNodes;
	private String data;
	private int level;
}
