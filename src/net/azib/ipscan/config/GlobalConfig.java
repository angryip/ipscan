/**
 * 
 */
package net.azib.ipscan.config;

import java.util.prefs.Preferences;

/**
 * Global configuration holder.
 *
 * @author anton
 */
public final class GlobalConfig {
	
	private static final String MAX_THREADS = "maxThreads";
	private static final String THREAD_DELAY = "threadDelay";
	private static final String ACTIVE_FEEDER = "activeFeeder";
	private static final String SCAN_DEAD_HOSTS = "scanDeadHosts";
	private static final String PING_TIMEOUT = "pingTimeout";
	private static final String PING_COUNT = "pingCount";
	private static final String SKIP_BROADCAST_ADDRESSES = "skipBroadcastAddresses";
	private static final String PORT_TIMEOUT = "portTimeout";
	private static final String ADAPT_PORT_TIMEOUT = "adaptPortTimeout";
	private static final String PORT_STRING = "portString";
	
	private Preferences preferences = Config.getPreferences();
	
	public int maxThreads = preferences.getInt(MAX_THREADS, 100);
	public int threadDelay = preferences.getInt(THREAD_DELAY, 20);
	public int activeFeeder = preferences.getInt(ACTIVE_FEEDER, 0);
	public boolean scanDeadHosts = preferences.getBoolean(SCAN_DEAD_HOSTS, false);
	public int pingTimeout = preferences.getInt(PING_TIMEOUT, 3000);
	public int pingCount = preferences.getInt(PING_COUNT, 3);
	public boolean skipBroadcastAddresses = preferences.getBoolean(SKIP_BROADCAST_ADDRESSES, true);
	public int portTimeout = preferences.getInt(PORT_TIMEOUT, 3000);;
	public boolean adaptPortTimeout = preferences.getBoolean(ADAPT_PORT_TIMEOUT, true);
	public String portString = preferences.get(PORT_STRING, "");
	
	/**
	 * Stores all the internal properties to the storage media
	 */
	public void store() {
		preferences.putInt(MAX_THREADS, maxThreads);
		preferences.putInt(THREAD_DELAY, threadDelay);
		preferences.putInt(ACTIVE_FEEDER, activeFeeder);
		preferences.putBoolean(SCAN_DEAD_HOSTS, scanDeadHosts);
		preferences.putInt(PING_TIMEOUT, pingTimeout);
		preferences.putInt(PING_COUNT, pingCount);
		preferences.putBoolean(SKIP_BROADCAST_ADDRESSES, skipBroadcastAddresses);
		preferences.putInt(PORT_TIMEOUT, portTimeout);
		preferences.putBoolean(ADAPT_PORT_TIMEOUT, adaptPortTimeout);
		preferences.put(PORT_STRING, portString);
	}

	// package local constructor
	GlobalConfig() {
	}
}
