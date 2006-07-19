/**
 * 
 */
package net.azib.ipscan.gui.actions;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import net.azib.ipscan.gui.OptionsWindow;

/**
 * ToolsActions
 * 
 * @author anton
 */
public class ToolsActions {

	public static class Options implements Listener {
		public void handleEvent(Event event) {
			new OptionsWindow();
		}
	}

}
