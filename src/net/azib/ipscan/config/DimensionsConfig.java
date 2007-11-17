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
public class DimensionsConfig {

	private Preferences preferences;
	
	public int windowHeight;
	public int windowWidth;
	public int windowTop;
	public int windowLeft;
	public boolean isWindowMaximized;
	/** this one is not saved, just a globally accessed parameter */
	public int standardButtonHeight = 22;
	
	// package local constructor
	DimensionsConfig(Preferences preferences) {
		this.preferences = preferences;
		load();
	}
	
	private void load() {
		windowHeight = preferences.getInt("windowHeight", 350);
		windowWidth = preferences.getInt("windowWidth", 560);
		windowTop = preferences.getInt("windowTop", 100);
		windowLeft = preferences.getInt("windowLeft", 100);
		isWindowMaximized = preferences.getBoolean("windowMaximized", false);		
	}

	public void store() {
		if (!isWindowMaximized) {
			preferences.putInt("windowHeight", windowHeight);
			preferences.putInt("windowWidth", windowWidth);
			preferences.putInt("windowTop", windowTop);
			preferences.putInt("windowLeft", windowLeft);
		}
		preferences.putBoolean("windowMaximized", isWindowMaximized);
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
