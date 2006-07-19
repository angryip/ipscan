/**
 * 
 */
package net.azib.ipscan.gui.feeders;

import org.eclipse.swt.widgets.Shell;

import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.feeders.RangeFeeder;
import junit.framework.TestCase;

/**
 * AbstractFeederGUITest
 *
 * @author anton
 */
public class AbstractFeederGUITest extends TestCase {
	
	private AbstractFeederGUI feederGUI;
	
	protected void setUp() throws Exception {
		feederGUI = new AbstractFeederGUI(new Shell()) {

			protected void initialize() {
			}
			
			public String getFeederName() {
				return "Mega Feeder";
			}
			
			public Feeder getFeeder() {
				feeder = new RangeFeeder();
				feeder.initialize(new String[] {"127.0.0.1", "127.0.0.2"});
				return feeder;
			}
			
			public String serialize() {
				return "";
			}

			public void unserialize(String serialized) {
			}
			
		};
	}

	public void testGetInfo() {
		assertEquals("Mega Feeder: 127.0.0.1 - 127.0.0.2", feederGUI.getInfo());
	}

}
