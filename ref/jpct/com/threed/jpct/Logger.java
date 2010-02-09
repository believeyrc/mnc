// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct;

import java.io.PrintStream;
import java.util.Date;
import java.util.Vector;

public class Logger {

	public Logger() {
	}

	public static void setPrintStream(PrintStream printstream) {
		myOut = printstream;
	}

	public static void setOutputBuffering(int i) {
		if (i < 1)
			i = 1;
		writeCnt = i;
		currentCnt = 0;
	}

	public static void setMessageBuffer(boolean flag) {
		saveLog = flag;
	}

	public static void setOnError(int i) {
		mode = i;
	}

	public static String[] getMessageBuffer() {
		String as[] = new String[messages.size()];
		for (int i = 0; i < messages.size(); i++)
			as[i] = (String) messages.elementAt(i);

		return as;
	}

	public static void setLogLevel(int i) {
		logLevel = i;
	}

	public static synchronized void log(String s, int i) {
		if (i <= logLevel) {
			try {
				if (i < 0 || i > 2)
					i = 2;
				if (i != 2)
					s = "[ " + new Date() + " ] - " + HEAD[i] + s;
				messages.addElement(s);
				currentCnt++;
				if (currentCnt >= writeCnt) {
					currentCnt = 0;
					int j = messages.size();
					for (int k = j - writeCnt; k < j; k++)
						myOut.println((String) messages.elementAt(k));

					if (!saveLog)
						messages.removeAllElements();
				}
			} catch (Exception exception) {
				System.err.println("ERROR: Error while handling log messages...!");
			}
			if (i == 0)
				if (mode == 1)
					System.exit(-99);
				else if (mode == 2)
					throw new RuntimeException(s);
		}
	}

	public static final int ERROR = 0;
	public static final int WARNING = 1;
	public static final int MESSAGE = 2;
	public static final int ON_ERROR_THROW_EXCEPTION = 2;
	public static final int ON_ERROR_EXIT = 1;
	public static final int ON_ERROR_RESUME_NEXT = 0;
	public static final boolean STORE_MESSAGES = true;
	public static final boolean DISCARD_MESSAGES = false;
	public static final int LL_ONLY_ERRORS = 0;
	public static final int LL_ERRORS_AND_WARNINGS = 1;
	public static final int LL_VERBOSE = 2;
	private static Vector messages = new Vector();
	private static int writeCnt = 1;
	private static int mode = 0;
	private static int logLevel = 2;
	private static boolean saveLog = false;
	private static int currentCnt = 0;
	private static PrintStream myOut;
	private static final String HEAD[] = { "ERROR: ", "WARNING: ", "MESSAGE: " };

	static {
		myOut = System.out;
	}
}
