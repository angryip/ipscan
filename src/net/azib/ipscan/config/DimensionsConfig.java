/**
 * 
 */
package net.azib.ipscan.config;

import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.Rectangle;

/**
 * DimensionsConfig
 *
 * @author anton
 */
public class DimensionsConfig {

	private Preferences preferences = Config.getPreferences(); 
	
	public int windowHeight = preferences.getInt("windowHeight", 350);
	public int windowWidth = preferences.getInt("windowWidth", 560);
	public int windowTop = preferences.getInt("windowTop", 100);
	public int windowLeft = preferences.getInt("windowLeft", 100);
	public boolean isWindowMaximized = preferences.getBoolean("windowMaximized", false);
	
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
		windowTop = bounds.y;
		windowLeft = bounds.x;
		windowHeight = bounds.height;
		windowWidth = bounds.width;
		isWindowMaximized = isMaximized;
	}

}
