// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.threed.jpct.util;

import com.threed.jpct.FrameBuffer;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

// Referenced classes of package com.threed.jpct.util:
//            KeyState

public class KeyMapper implements KeyListener {

	public KeyMapper() {
		bufferSize = 10;
		comp = null;
		created = false;
		init(null);
	}

	public KeyMapper(Component component) {
		bufferSize = 10;
		comp = null;
		created = false;
		init(component);
	}

	public void destroy() {
		if (comp != null) {
			comp.removeKeyListener(this);
			awtKeyState = null;
		} else {
			synchronized (FrameBuffer.SYNCHRONIZER) {
				if (Keyboard.isCreated())
					Keyboard.destroy();
			}
		}
	}

	public synchronized KeyState poll() {
		if (created) {
			if (lwjgl)
				pollLWJGLKeys();
			if (pollCnt != keyCnt && pollCnt < bufferSize) {
				KeyState keystate = keys[pollCnt];
				keys[pollCnt] = null;
				pollCnt++;
				return keystate;
			} else {
				pollCnt = 0;
				keyCnt = 0;
				return KeyState.NONE;
			}
		} else {
			init(null);
			return KeyState.NONE;
		}
	}

	public void keyTyped(KeyEvent keyevent) {
	}

	public void keyPressed(KeyEvent keyevent) {
		int i = keyevent.getKeyCode();
		if (i < awtKeyState.length) {
			if (awtKeyState[i] != 1) {
				awtKeyState[i] = 1;
				processKey(keyevent.getKeyCode(), keyevent.getKeyChar(), true);
			}
		} else {
			processKey(keyevent.getKeyCode(), keyevent.getKeyChar(), true);
		}
	}

	public void keyReleased(KeyEvent keyevent) {
		int i = keyevent.getKeyCode();
		if (i < awtKeyState.length) {
			if (awtKeyState[i] != 0) {
				awtKeyState[i] = 0;
				processKey(keyevent.getKeyCode(), keyevent.getKeyChar(), false);
			}
		} else {
			processKey(keyevent.getKeyCode(), keyevent.getKeyChar(), false);
		}
	}

	private void pollLWJGLKeys() {
		synchronized (FrameBuffer.SYNCHRONIZER) {
			if (Keyboard.isCreated())
				while (Keyboard.next()) {
					int i = Keyboard.getEventKey();
					char c = Keyboard.getEventCharacter();
					boolean flag = Keyboard.getEventKeyState();
					int j = 0;
					while (j < mapping.length) {
						if (mapping[j] == i)
							if (flag && mapping[j + 2] != 1) {
								processKey(mapping[j + 1], c, true);
								mapping[j + 2] = 1;
							} else if (!flag && mapping[j + 2] == 1) {
								processKey(mapping[j + 1], c, false);
								mapping[j + 2] = 0;
							}
						j += 3;
					}
				}
		}
	}

	private void init(Component component) {
		keys = new KeyState[bufferSize];
		keyCnt = 0;
		created = true;
		if (component != null) {
			comp = component;
			awtKeyState = new int[65535];
			component.addKeyListener(this);
			lwjgl = false;
		} else {
			lwjgl = true;
			try {
				synchronized (FrameBuffer.SYNCHRONIZER) {
					if (!Keyboard.isCreated())
						if (Display.isCreated())
							Keyboard.create();
						else
							created = false;
				}
			} catch (Exception exception) {
				created = false;
			}
		}
	}

	private synchronized void processKey(int i, char c, boolean flag) {
		while (keyCnt >= bufferSize)
			resizeBuffer();
		keys[keyCnt] = new KeyState(i, c, flag);
		keyCnt++;
	}

	private void resizeBuffer() {
		KeyState akeystate[] = new KeyState[bufferSize + 10];
		System.arraycopy(keys, 0, akeystate, 0, bufferSize);
		bufferSize += 10;
		keys = akeystate;
	}

	private int bufferSize;
	private KeyState keys[];
	private int awtKeyState[];
	private int keyCnt;
	private int pollCnt;
	private boolean lwjgl;
	private Component comp;
	private boolean created;
	private int mapping[] = { 0, 0, 0, 1, 27, 0, 2, 49, 0, 3, 50, 0, 4, 51, 0, 5, 52, 0, 6, 53, 0, 7, 54, 0, 8, 55, 0, 9,
			56, 0, 10, 57, 0, 11, 48, 0, 12, 45, 0, 13, 61, 0, 14, 8, 0, 15, 9, 0, 16, 81, 0, 17, 87, 0, 18, 69, 0, 19, 82,
			0, 20, 84, 0, 21, 89, 0, 22, 85, 0, 23, 73, 0, 24, 79, 0, 25, 80, 0, 26, 519, 0, 27, 522, 0, 28, 10, 0, 29, 17,
			0, 30, 65, 0, 31, 83, 0, 32, 68, 0, 33, 70, 0, 34, 71, 0, 35, 72, 0, 36, 74, 0, 37, 75, 0, 38, 76, 0, 39, 59, 0,
			40, 513, 0, 41, 128, 0, 42, 16, 0, 43, 92, 0, 44, 90, 0, 45, 88, 0, 46, 67, 0, 47, 86, 0, 48, 66, 0, 49, 78, 0,
			50, 77, 0, 51, 44, 0, 52, 46, 0, 53, 47, 0, 54, 16, 0, 55, 106, 0, 56, 0, 0, 57, 32, 0, 58, 20, 0, 59, 112, 0,
			60, 113, 0, 61, 114, 0, 62, 115, 0, 63, 116, 0, 64, 117, 0, 65, 118, 0, 66, 119, 0, 67, 120, 0, 68, 121, 0, 69,
			144, 0, 70, 145, 0, 71, 103, 0, 72, 104, 0, 73, 105, 0, 74, 109, 0, 75, 100, 0, 76, 101, 0, 77, 102, 0, 78, 107,
			0, 79, 97, 0, 80, 98, 0, 81, 99, 0, 82, 96, 0, 83, 110, 0, 87, 122, 0, 88, 123, 0, 100, 61440, 0, 101, 61441, 0,
			102, 61442, 0, 112, 21, 0, 121, 28, 0, 123, 29, 0, 125, 0, 0, 141, 61, 0, 144, 514, 0, 145, 512, 0, 146, 513, 0,
			147, 523, 0, 148, 25, 0, 149, 65480, 0, 150, 0, 0, 151, 0, 0, 156, 10, 0, 157, 17, 0, 179, 44, 0, 181, 111, 0,
			183, 0, 0, 184, 0, 0, 197, 19, 0, 199, 36, 0, 200, 38, 0, 201, 33, 0, 203, 37, 0, 205, 39, 0, 207, 35, 0, 208,
			40, 0, 209, 34, 0, 210, 155, 0, 211, 127, 0, 219, 0, 0, 220, 0, 0, 221, 0, 0, 222, 0, 0, 223, 0, 0 };
}
