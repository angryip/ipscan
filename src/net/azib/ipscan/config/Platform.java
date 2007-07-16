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
 * @author Anton Keks Keks
 */
public class Platform {
	/** Mac OS detection :-) */
	public static final boolean MAC_OS = System.getProperty("mrj.version") != null;
	
	/** Linux */
	public static final boolean LINUX = System.getProperty("os.name").indexOf("Linux") >= 0;
	
	/** Any Windows version */
	public static final boolean WINDOWS = System.getProperty("os.name").startsWith("Windows");

}
