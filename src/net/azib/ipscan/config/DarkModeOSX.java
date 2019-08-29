package net.azib.ipscan.config;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * (c) 2019 Michael Padilla <github.com/zwilla>
 * Detect DarkMode at OSX.
 * just import net.azib.ipscan.config.DarkModeOSX;
 * if (DarkModeOSX.check()) {}
 */

public class DarkModeOSX {
	public static boolean check() {
		try {
			final Process proc = Runtime.getRuntime().exec(new String[]{"defaults", "read", "-g", "AppleInterfaceStyle"});
			proc.waitFor(115, TimeUnit.MILLISECONDS);
			return proc.exitValue() == 0;
		}
		catch (IOException | InterruptedException | IllegalThreadStateException ex) {
			return false;
		}
	}
}
