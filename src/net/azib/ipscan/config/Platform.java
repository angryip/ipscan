/**
 * 
 */
package net.azib.ipscan.config;

/**
 * This class provides constants for distingushing between various platforms.
 * However, platform-specific behaviour must be kept at minimum.
 *
 * @author Anton Keks
 */
public class Platform {
	/** Mac OS detection :-) */
	public static final boolean MAC_OS = System.getProperty("mrj.version") != null;
	
	/** Any Windows version */
	public static final boolean WINDOWS = System.getProperty("os.name").startsWith("Windows");

}
