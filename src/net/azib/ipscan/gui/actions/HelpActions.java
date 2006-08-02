/**
 * 
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.Version;
import net.azib.ipscan.gui.AboutWindow;
import net.azib.ipscan.gui.GettingStartedWindow;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;

/**
 * HelpActions
 *
 * @author anton
 */
public class HelpActions {
	
	public static class GettingStarted implements Listener {
		public void handleEvent(Event event) {
			new GettingStartedWindow().open();
		}
	}

	public static class About implements Listener { 		
		public void handleEvent(Event event) { 
			new AboutWindow().open(); 
		}
	}

	public static class Website implements Listener { 		
		public void handleEvent(Event event) { 
			MessageBox messageBox = new MessageBox(event.display.getActiveShell());
			messageBox.setMessage(Version.WEBSITE);
			messageBox.open();
		}
	}

}
