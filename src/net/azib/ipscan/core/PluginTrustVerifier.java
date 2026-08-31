package net.azib.ipscan.core;

import java.io.File;

/**
 * Callback interface for verifying plugin trust before loading.
 */
@FunctionalInterface
public interface PluginTrustVerifier {
	/**
	 * Called when a new plugin JAR is discovered.
	 * @param jarFile the plugin JAR file
	 * @param classNames comma-separated list of plugin class names from the manifest
	 * @return true if the plugin should be loaded, false to skip it
	 */
	boolean isTrusted(File jarFile, String classNames);
}
