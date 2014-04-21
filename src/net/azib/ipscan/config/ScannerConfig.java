/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import java.util.prefs.Preferences;

/**
 * Scanner configuration holder.
 *
 * @author Anton Keks
 */
public class ScannerConfig {
	
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
	public String language;

	/**
	 * Package local constructor.
	 * It loads all preferences.
	 * @param preferences
	 */
	ScannerConfig(Preferences preferences) {
		this.preferences = preferences;
		
		maxThreads = preferences.getInt("maxThreads", Platform.CRIPPLED_WINDOWS ? 10 : 100);
		threadDelay = preferences.getInt("threadDelay", 20);
		scanDeadHosts = preferences.getBoolean("scanDeadHosts", false);
		selectedPinger = preferences.get("selectedPinger", Platform.WINDOWS ? "pinger.windows" : "pinger.icmp");
		pingTimeout = preferences.getInt("pingTimeout", 2000);
		pingCount = preferences.getInt("pingCount", 3);
		skipBroadcastAddresses = preferences.getBoolean("skipBroadcastAddresses", true);
		portTimeout = preferences.getInt("portTimeout", 2000);
		adaptPortTimeout = preferences.getBoolean("adaptPortTimeout", !Platform.CRIPPLED_WINDOWS);
		minPortTimeout = preferences.getInt("minPortTimeout", 100);
		portString = preferences.get("portString", "");
		useRequestedPorts = preferences.getBoolean("useRequestedPorts", true);
		notAvailableText = preferences.get("notAvailableText", Labels.getLabel("fetcher.value.notAvailable"));
		notScannedText = preferences.get("notScannedText", Labels.getLabel("fetcher.value.notScanned"));
		language = preferences.get("language", "language.english");
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
		preferences.put("language", language);
	}
}
