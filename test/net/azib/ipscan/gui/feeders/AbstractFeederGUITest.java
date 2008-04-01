/**
 * 
 */
package net.azib.ipscan.gui.feeders;

import static org.junit.Assert.*;

import org.eclipse.swt.widgets.Shell;

import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.feeders.RangeFeeder;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * AbstractFeederGUITest
 *
 * @author Anton Keks
 */
public class AbstractFeederGUITest {
	
	private AbstractFeederGUI feederGUI;
	
	@BeforeClass
	public static void globalSetUp() {
		System.setProperty("java.library.path", "../swt/lib");
	}
	
	@Before
	public void setUp() throws Exception {
		feederGUI = new AbstractFeederGUI(new Shell()) {

			protected void initialize() {
			}
			
			public String getFeederName() {
				return "Mega Feeder";
			}
			
			public Feeder createFeeder() {
				feeder = new RangeFeeder("127.0.0.1", "127.0.0.2");
				return feeder;
			}
			
			public String serialize() {
				return "";
			}

			public void unserialize(String serialized) {
			}
			
		};
	}

	@Test
	public void testGetInfo() {
		assertEquals("Mega Feeder: 127.0.0.1 - 127.0.0.2", feederGUI.getInfo());
	}

}
