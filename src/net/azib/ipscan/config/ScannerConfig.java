/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import net.azib.ipscan.core.PortIterator;

import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * Scanner configuration holder.
 *
 * @author Anton Keks
 */
public class ScannerConfig {
	static final String DEFAULT_PORT_STRING = "80,443,8080";
	/** Older versions used this as the default maximum number of threads */
	static final int LEGACY_DEFAULT_MAX_THREADS = 100;

	private static final Logger LOG = LoggerFactory.getLogger();

	private Preferences preferences;

	public int maxThreads;
	public int threadDelay;
	public boolean scanDeadHosts;
	public String selectedPinger;
	public int pingTimeout;
	public int pingCount;
	public boolean skipBroadcastAddresses;
	public int portTimeout;
	public boolean adaptPortTimeout;
	public int minPortTimeout;
	public String portString;
	public boolean useRequestedPorts;
	public String notAvailableText;
	public String notScannedText;

	/**
	 * Package local constructor.
	 * It loads all preferences.
	 */
	ScannerConfig(Preferences preferences) {
		this.preferences = preferences;
		
		maxThreads = preferences.getInt("maxThreads", 0);
		if (maxThreads == LEGACY_DEFAULT_MAX_THREADS) {
			// the old default is indistinguishable from an explicit setting, so migrate to virtual threads
			maxThreads = 0;
		}
		threadDelay = preferences.getInt("threadDelay", 20);
		scanDeadHosts = preferences.getBoolean("scanDeadHosts", false);
		selectedPinger = preferences.get("selectedPinger", Platform.WINDOWS ? "pinger.windows" : "pinger.java");
		pingTimeout = preferences.getInt("pingTimeout", 2000);
		pingCount = preferences.getInt("pingCount", 3);
		skipBroadcastAddresses = preferences.getBoolean("skipBroadcastAddresses", true);
		portTimeout = preferences.getInt("portTimeout", 2000);
		adaptPortTimeout = preferences.getBoolean("adaptPortTimeout", !Platform.CRIPPLED_WINDOWS);
		minPortTimeout = preferences.getInt("minPortTimeout", 100);
		portString = preferences.get("portString", DEFAULT_PORT_STRING);
		if (!isValidPortString(portString)) {
			// older versions allowed port 0, so an invalid value may be persisted
			LOG.warning("Ignoring invalid port string in preferences: " + portString);
			portString = DEFAULT_PORT_STRING;
		}
		useRequestedPorts = preferences.getBoolean("useRequestedPorts", true);
		notAvailableText = preferences.get("notAvailableText", Labels.getLabel("fetcher.value.notAvailable"));
		notScannedText = preferences.get("notScannedText", Labels.getLabel("fetcher.value.notScanned"));
	}

	private static boolean isValidPortString(String portString) {
		try {
			new PortIterator(portString);
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
		
	/**
	 * Stores all the internal properties to the storage media
	 */
	public void store() {
		preferences.putInt("maxThreads", maxThreads);
		preferences.putInt("threadDelay", threadDelay);
		preferences.putBoolean("scanDeadHosts", scanDeadHosts);
		preferences.put("selectedPinger", selectedPinger);
		preferences.putInt("pingTimeout", pingTimeout);
		preferences.putInt("pingCount", pingCount);
		preferences.putBoolean("skipBroadcastAddresses", skipBroadcastAddresses);
		preferences.putInt("portTimeout", portTimeout);
		preferences.putBoolean("adaptPortTimeout", adaptPortTimeout);
		preferences.putInt("minPortTimeout", minPortTimeout);
		preferences.put("portString", portString);
		preferences.putBoolean("useRequestedPorts", useRequestedPorts);
		preferences.put("notAvailableText", notAvailableText);
		preferences.put("notScannedText", notScannedText);
	}
}
