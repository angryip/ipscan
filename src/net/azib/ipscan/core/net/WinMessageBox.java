package net.azib.ipscan.core.net;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;

/**
 * Shows a native Windows message box without depending on SWT or java.desktop.
 */
public class WinMessageBox {
	private interface User32 extends Library {
		User32 INSTANCE = Native.load("user32", User32.class);
		int MessageBoxW(Pointer hWnd, WString text, WString caption, int type);
	}

	private static final int MB_ICONERROR = 0x10;

	public static void show(String message, String title) {
		User32.INSTANCE.MessageBoxW(null, new WString(message), new WString(title), MB_ICONERROR);
	}
}
