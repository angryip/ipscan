/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.HostnameFetcher;
import net.azib.ipscan.fetchers.IPFetcher;
import net.azib.ipscan.fetchers.PingFetcher;
import org.eclipse.swt.graphics.Point;

import java.util.prefs.Preferences;

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
	
	public Point mainWindowSize;
	public boolean isMainWindowMaximized;
	
	public Point detailsWindowSize;
	
	public enum DisplayMethod {ALL, ALIVE, PORTS}

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

		isMainWindowMaximized = preferences.getBoolean("windowMaximized", false);
		mainWindowSize = new Point(
			preferences.getInt("windowWidth", 600),
			preferences.getInt("windowHeight", 360));
		
		detailsWindowSize = new Point(
			preferences.getInt("detailsWidth", 300),
			preferences.getInt("detailsHeight", 200));
	}

	public void store() {
		preferences.putBoolean("firstRun", isFirstRun);
		preferences.putInt("activeFeeder", activeFeeder);
		preferences.put("displayMethod", displayMethod.toString());
		preferences.putBoolean("showScanStats", showScanStats);
		preferences.putBoolean("askScanConfirmation", askScanConfirmation);

		preferences.putBoolean("windowMaximized", isMainWindowMaximized);
		if (!isMainWindowMaximized) {
			preferences.putInt("windowWidth", mainWindowSize.x);
			preferences.putInt("windowHeight", mainWindowSize.y);
		}
		
		preferences.putInt("detailsWidth", detailsWindowSize.x);
		preferences.putInt("detailsHeight", detailsWindowSize.y);
	}

	public Point getMainWindowSize() {
		return mainWindowSize;
	}

	public void setMainWindowSize(Point size, boolean isMaximized) {
		if (!isMaximized) {
			mainWindowSize = size;
		}
		isMainWindowMaximized = isMaximized;
	}

	/**
	 * @param fetcher
	 * @return column width corresponding to a fetcher
	 */
	public int getColumnWidth(Fetcher fetcher) {
		int width = preferences.getInt("columnWidth." + fetcher.getId(), 0);
		if (width == 0) {
			// use different default widths
			if (fetcher instanceof IPFetcher || fetcher instanceof HostnameFetcher)
				width = 140;
			else
			if (fetcher instanceof PingFetcher)
				width = 60;
			else
				width = 90;
		}
		return width;
	}
	
	/**
	 * Persist the width of a column corresponding to a fetcher
	 * @param fetcher
	 * @param width
	 */
	public void setColumnWidth(Fetcher fetcher, int width) {
		preferences.putInt("columnWidth." + fetcher.getId(), width);
	}

}
