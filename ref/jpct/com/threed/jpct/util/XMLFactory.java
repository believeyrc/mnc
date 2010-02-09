// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct.util;

import java.util.Vector;

// Referenced classes of package com.threed.jpct.util:
//            XMLNode

public final class XMLFactory {

	private XMLFactory() {
	}

	public static synchronized XMLFactory getInstance() {
		if (instance == null)
			instance = new XMLFactory();
		return instance;
	}

	public XMLNode parseXML(String s) {
		Vector vector = parseXML(s + " ", 0);
		if (vector != null)
			return (XMLNode) vector.elementAt(0);
		else
			return null;
	}

	private Vector parseXML(String s, int i) {
		int j = 0;
		Vector vector = null;
		Object obj = null;
		do {
			j = getNextTag(j, s);
			if (j != -1) {
				j++;
				String s1 = extractTag(j, s);
				if (s1.length() > 0) {
					int k = getEndTag(j, -1, s1, s);
					if (k != -1) {
						XMLNode xmlnode = new XMLNode();
						xmlnode.setName(s1);
						xmlnode.setLevel(i);
						xmlnode.setAttributes(getAttributes(j, s));
						Vector vector1 = parseXML(s.substring(j + s1.length(), k), i + 1);
						if (vector1 != null)
							xmlnode.setSubNodes(vector1);
						if (vector1 == null || vector1.size() == 0)
							xmlnode.setData(grabData(j, k, s));
						if (vector == null)
							vector = new Vector(5);
						vector.addElement(xmlnode);
					}
					j = k;
				}
			}
		} while (j != -1);
		return vector;
	}

	public void filter(XMLNode xmlnode, XMLNode xmlnode1) {
		filter(xmlnode, xmlnode, xmlnode1, xmlnode.getName());
	}

	private void filter(XMLNode xmlnode, XMLNode xmlnode1, XMLNode xmlnode2, String s) {
		Vector vector = xmlnode1.getSubNodes();
		if (vector != null) {
			int ai[] = new int[vector.size()];
			int i = 0;
			for (int j = 0; j < vector.size(); j++) {
				XMLNode xmlnode3 = (XMLNode) vector.elementAt(j);
				String s1 = s + "/" + xmlnode3.getName();
				if (getMatchingNodes(s1, xmlnode2).size() == 0) {
					ai[i] = j;
					i++;
				} else {
					filter(xmlnode, xmlnode3, xmlnode2, s1);
				}
			}

			for (int k = 0; k < i; k++)
				xmlnode1.removeSubNode(ai[k] - k);

		}
	}

	public Vector getMatchingNodes(String s, XMLNode xmlnode) {
		if (s.charAt(s.length() - 1) != '/')
			s = s + "/";
		if (s.charAt(0) == '/')
			s = s.substring(1);
		Vector vector = new Vector();
		getMatchingNodes(s, xmlnode, "", vector);
		return vector;
	}

	private void getMatchingNodes(String s, XMLNode xmlnode, String s1, Vector vector) {
		String s2 = s1 + xmlnode.getName() + "/";
		if (!s.equals(s2)) {
			if (s.startsWith(s2)) {
				Vector vector1 = xmlnode.getSubNodes();
				if (vector1 != null) {
					for (int i = 0; i < vector1.size(); i++) {
						XMLNode xmlnode1 = (XMLNode) vector1.elementAt(i);
						getMatchingNodes(s, xmlnode1, s2, vector);
					}

				}
			}
		} else {
			vector.addElement(xmlnode);
		}
	}

	private Vector getAttributes(int i, String s) {
		Vector vector = new Vector(3);
		int j = s.indexOf("/>", i);
		int k = s.indexOf(">", i);
		if (j == -1)
			j = k;
		else if (k < j)
			j = k;
		if (j != -1) {
			int l = s.indexOf(" ", i);
			if (l != -1 && l < j) {
				String s1 = s.substring(l, j) + " ";
				l = 0;
				int i1 = 0;
				Object obj = null;
				Object obj1 = null;
				do {
					l++;
					i1 = s1.indexOf("=", l);
					if (i1 != -1) {
						String s2 = s1.substring(l, i1).trim();
						i1++;
						l = s1.indexOf(" ", i1);
						if (s1.charAt(i1) == '"') {
							int j1 = s1.indexOf("\"", i1 + 1);
							if (j1 != -1 && j1 > l)
								l = j1;
						} else if (s1.charAt(i1) == '\'') {
							int k1 = s1.indexOf("'", i1 + 1);
							if (k1 != -1 && k1 > l)
								l = k1;
						}
						if (l != -1 && s2.length() > 0) {
							String s3 = s1.substring(i1, l).trim();
							if (s3.charAt(0) == '"')
								s3 = s3.substring(1);
							else if (s3.charAt(0) == '\'')
								s3 = s3.substring(1);
							if (s3.charAt(s3.length() - 1) == '"')
								s3 = s3.substring(0, s3.length() - 1);
							else if (s3.charAt(s3.length() - 1) == '\'')
								s3 = s3.substring(0, s3.length() - 1);
							vector.addElement(new String[] { s2, s3.trim() });
						}
					}
				} while (i1 != -1 && l != -1);
			}
		}
		return vector;
	}

	private int getNextTag(int i, String s) {
		boolean flag = true;
		int j;
		do {
			flag = true;
			j = s.indexOf("<", i);
			if (j != -1) {
				char c = s.charAt(j + 1);
				if (c == '?' || c == '!') {
					if (s.indexOf("<![CDATA[", i) != -1) {
						int k = s.indexOf("]]>", i + 10);
						if (k != -1)
							i = k + 3;
					} else {
						i = j + 1;
						int l = s.indexOf(">", i);
						if (l != -1)
							i = l + 1;
					}
					flag = false;
				}
			}
		} while (j != -1 && !flag);
		return j;
	}

	private String extractTag(int i, String s) {
		int j = s.indexOf(">", i);
		String s1 = "";
		if (j != -1) {
			String s2 = s.substring(i, j);
			for (int k = 0; k < s2.length(); k++) {
				char c = s2.charAt(k);
				if (c == ' ' || c == '/')
					break;
				if (c != '<')
					s1 = s1 + c;
			}

		}
		return s1;
	}

	private boolean checkIfWellFormed(String s, int i, int j, String s1) {
		boolean flag = true;
		int k = 0;
		int l = 0;
		int i1 = i - 1;
		String s2 = "<" + s;
		int j1 = s2.length();
		do {
			i1 = s1.indexOf(s2, i1);
			if (i1 != -1 && i1 <= j) {
				char c = s1.charAt(i1 + j1);
				if (c != ' ' && c != '/' && c != '>')
					i1 = -1;
			}
			if (i1 != -1 && i1 <= j) {
				int k1 = s1.indexOf(">", i1);
				int l1 = s1.indexOf("<", i1 + 1);
				if (l1 == -1)
					l1 = 0x3b9ac9ff;
				if (k1 != -1 && s1.charAt(k1 - 1) == '/' && l1 > k1) {
					k++;
					l++;
				} else {
					k++;
				}
				i1++;
			}
		} while (i1 <= j && i1 != -1);
		i1 = i - 1;
		s2 = "</" + s + ">";
		do {
			i1 = s1.indexOf(s2, i1);
			if (i1 != -1 && i1 <= j) {
				l++;
				i1++;
			}
		} while (i1 <= j && i1 != -1);
		flag = l - k == 0;
		return flag;
	}

	private int getEndTag(int i, int j, String s, String s1) {
		int k = i;
		if (j == -1)
			j = i;
		int l = s1.indexOf(">", j);
		int i1 = s1.indexOf("<", j + 1);
		if (i1 == -1)
			i1 = 0x3b9ac9ff;
		if (l != -1 && s1.charAt(l - 1) == '/' && i1 > l)
			return l;
		if (l != -1) {
			String s2 = "</" + s + ">";
			l = s1.indexOf(s2, l);
			if (l != -1) {
				boolean flag = checkIfWellFormed(s, k, l, s1);
				if (flag)
					return l;
				else
					return getEndTag(k, l + 2, s, s1);
			}
		}
		return -1;
	}

	private String grabData(int i, int j, String s) {
		int k = s.indexOf(">", i);
		if (k != -1 && k < j)
			return s.substring(k + 1, j);
		else
			return null;
	}

	private static XMLFactory instance = null;

}
