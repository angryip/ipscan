/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

/**
 * This class provides constants for distinguishing between various platforms.
 * However, platform-specific behaviour must be kept at minimum.
 *
 * @author Anton Keks
 */
public class Platform {
	
	private static final String OS_NAME = System.getProperty("os.name");

    public static final boolean ARCH_64 = System.getProperty("os.arch").contains("64");
	
	/** Mac OS detection :-) */
	public static final boolean MAC_OS = System.getProperty("mrj.version") != null;
	
	/** Linux */
	public static final boolean LINUX = OS_NAME.contains("Linux");
	
	/** Any Windows version */
	public static final boolean WINDOWS = OS_NAME.startsWith("Windows");
	
	/** Crippled-down version of Windows (no RawSockets, TCP rate limiting, etc */
	public static final boolean CRIPPLED_WINDOWS = WINDOWS && !OS_NAME.contains("Server") && Double.parseDouble(System.getProperty("os.version").substring(0, 3)) >= 5.1;
	
	/** GNU Java, based on GIJ/GCC and GNU Classpath projects */
	public static final boolean GNU_JAVA = System.getProperty("java.vm.vendor").contains("Free Software Foundation");
}
