package net.azib.ipscan.core.net;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface WinKernel32 extends Library {
	WinKernel32 dll = Loader.load();
	class Loader {
		public static WinKernel32 load() {
			return Native.loadLibrary("kernel32", WinKernel32.class);
		}
	}

	int GetLastError();
}
