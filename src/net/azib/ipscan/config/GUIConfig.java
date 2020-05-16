/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
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
	public String lastRunVersion;
	public long lastVersionCheck;
	public int activeFeeder;
	public DisplayMethod displayMethod;
	public boolean showScanStats;
	public boolean askScanConfirmation;
	
	public int[] mainWindowSize;
	public boolean isMainWindowMaximized;
	
	public int[] detailsWindowSize;
	
	public enum DisplayMethod {ALL, ALIVE, PORTS}

	GUIConfig(Preferences preferences) {
		this.preferences = preferences;
		load();
	}
	
	private void load() {
		isFirstRun = preferences.getBoolean("firstRun", true);
		lastRunVersion = preferences.get("lastRunVersion", "Unknown");
		lastVersionCheck = preferences.getLong("lastVersionCheck", System.currentTimeMillis());
		activeFeeder = preferences.getInt("activeFeeder", 0);
		displayMethod = DisplayMethod.valueOf(preferences.get("displayMethod", DisplayMethod.ALL.toString()));
		showScanStats = preferences.getBoolean("showScanStats", true);
		askScanConfirmation = preferences.getBoolean("askScanConfirmation", true);

		isMainWindowMaximized = preferences.getBoolean("windowMaximized", false);
		mainWindowSize = new int[] {preferences.getInt("windowWidth", 800), preferences.getInt("windowHeight", 450)};
		detailsWindowSize = new int[] {preferences.getInt("detailsWidth", 400), preferences.getInt("detailsHeight", 300)};
	}

	public void store() {
		preferences.putBoolean("firstRun", isFirstRun);
		preferences.put("lastRunVersion", lastRunVersion);
		preferences.putLong("lastVersionCheck", lastVersionCheck);
		preferences.putInt("activeFeeder", activeFeeder);
		preferences.put("displayMethod", displayMethod.toString());
		preferences.putBoolean("showScanStats", showScanStats);
		preferences.putBoolean("askScanConfirmation", askScanConfirmation);

		preferences.putBoolean("windowMaximized", isMainWindowMaximized);
		if (!isMainWindowMaximized) {
			preferences.putInt("windowWidth", mainWindowSize[0]);
			preferences.putInt("windowHeight", mainWindowSize[1]);
		}
		
		preferences.putInt("detailsWidth", detailsWindowSize[0]);
		preferences.putInt("detailsHeight", detailsWindowSize[1]);
	}

	public Point getDetailsWindowSize() {
		return new Point(detailsWindowSize[0], detailsWindowSize[1]);
	}

	public void setDetailsWindowSize(Point size) {
		detailsWindowSize = new int[] {size.x, size.y};
	}

	public Point getMainWindowSize() {
		return new Point(mainWindowSize[0], mainWindowSize[1]);
	}

	public void setMainWindowSize(Point size, boolean isMaximized) {
		if (!isMaximized) {
			mainWindowSize = new int[] {size.x, size.y};
		}
		isMainWindowMaximized = isMaximized;
	}

	/**
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
	 */
	public void setColumnWidth(Fetcher fetcher, int width) {
		preferences.putInt("columnWidth." + fetcher.getId(), width);
	}
}
