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
		
		private OptionsWindow optionsWindow;
		
		public Options(OptionsWindow optionsWindow) {
			this.optionsWindow = optionsWindow;
		}

		public void handleEvent(Event event) {
			optionsWindow.open();
		}
	}

}
