/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

/**
 * This class provides constants for distingushing between various platforms.
 * However, platform-specific behaviour must be kept at minimum.
 *
 * @author Anton Keks
 */
public class Platform {
	
	private static final String OS_NAME = System.getProperty("os.name");
	
	/** Mac OS detection :-) */
	public static final boolean MAC_OS = System.getProperty("mrj.version") != null;
	
	/** Linux */
	public static final boolean LINUX = OS_NAME.indexOf("Linux") >= 0;
	
	/** Any Windows version */
	public static final boolean WINDOWS = OS_NAME.startsWith("Windows");
	
	/** Crippled-down version of Windows (no RawSockets, TCP rate limiting, etc */
	public static final boolean CRIPPLED_WINDOWS = WINDOWS && OS_NAME.indexOf("Server") < 0 && Double.parseDouble(System.getProperty("os.version").substring(0, 3)) >= 5.1;
}
