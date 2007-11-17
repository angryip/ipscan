/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.Rectangle;

/**
 * DimensionsConfig
 *
 * @author Anton Keks
 */
public class GUIConfig {

	private Preferences preferences;
	
	public boolean isFirstRun;
	public int activeFeeder;
	public DisplayMethod displayMethod;
	public boolean showScanStats;
	public boolean askScanConfirmation;
	
	public int windowHeight;
	public int windowWidth;
	public int windowTop;
	public int windowLeft;
	public boolean isWindowMaximized;
	/** this one is not saved, just a globally accessed parameter */
	public int standardButtonHeight = 22;
	
	public static enum DisplayMethod {ALL, ALIVE, PORTS}

	// package local constructor
	GUIConfig(Preferences preferences) {
		this.preferences = preferences;
		load();
	}
	
	private void load() {
		isFirstRun = preferences.getBoolean("firstRun", true);
		activeFeeder = preferences.getInt("activeFeeder", 0);
		displayMethod = DisplayMethod.valueOf(preferences.get("displayMethod", DisplayMethod.ALL.toString()));
		showScanStats = preferences.getBoolean("showScanStats", true);
		askScanConfirmation = preferences.getBoolean("askScanConfirmation", true);

		isWindowMaximized = preferences.getBoolean("windowMaximized", false);		
		windowHeight = preferences.getInt("windowHeight", 350);
		windowWidth = preferences.getInt("windowWidth", 560);
		windowTop = preferences.getInt("windowTop", 100);
		windowLeft = preferences.getInt("windowLeft", 100);
	}

	public void store() {
		preferences.putBoolean("firstRun", isFirstRun);
		preferences.putInt("activeFeeder", activeFeeder);
		preferences.put("displayMethod", displayMethod.toString());
		preferences.putBoolean("showScanStats", showScanStats);
		preferences.putBoolean("askScanConfirmation", askScanConfirmation);

		preferences.putBoolean("windowMaximized", isWindowMaximized);
		if (!isWindowMaximized) {
			preferences.putInt("windowHeight", windowHeight);
			preferences.putInt("windowWidth", windowWidth);
			preferences.putInt("windowTop", windowTop);
			preferences.putInt("windowLeft", windowLeft);
		}
	}

	/**
	 * @return
	 */
	public Rectangle getWindowBounds() {
		return new Rectangle(windowLeft, windowTop, windowWidth, windowHeight);
	}

	/**
	 * @param bounds
	 * @param isMaximized 
	 */
	public void setWindowBounds(Rectangle bounds, boolean isMaximized) {
		if (!isMaximized) {
			windowTop = bounds.y;
			windowLeft = bounds.x;
			windowHeight = bounds.height;
			windowWidth = bounds.width;
		}
		isWindowMaximized = isMaximized;
	}

	/**
	 * @param fetcherName
	 * @return column width corresponding to a fetcher
	 */
	public int getColumnWidth(String fetcherName) {
		return preferences.getInt("columnWidth." + fetcherName, 90);
	}
	
	/**
	 * Persist the width of a column corresponding to a fetcher
	 * @param fetcherName
	 * @param width
	 */
	public void setColumnWidth(String fetcherName, int width) {
		preferences.putInt("columnWidth." + fetcherName, width);
	}

}
