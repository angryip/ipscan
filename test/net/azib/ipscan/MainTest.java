package net.azib.ipscan;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import junit.framework.TestCase;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.feeders.FeederException;

public class MainTest extends TestCase {

	public void testGetLocalizedMessage() {
		// unknown exception
		final boolean wasStackTraceLogged[] = {false};
		Throwable e = new Exception("hello, test!");
		Logger.global.setUseParentHandlers(false);
		Logger.global.addHandler(new Handler() {
			public void close() throws SecurityException {
			}
			public void flush() {
			}
			public void publish(LogRecord record) {
				wasStackTraceLogged[0] = true;
			}
		});
		assertEquals(e.toString(), Main.getLocalizedMessage(e));
		assertTrue(wasStackTraceLogged[0]);
		
		// localized exception
		assertEquals(Labels.getInstance().getString("exception.FeederException.range.greaterThan"), 
				Main.getLocalizedMessage(new FeederException("range.greaterThan")));
		
		// message-less localized exception
		assertEquals(Labels.getInstance().getString("exception.OutOfMemoryError"), 
				Main.getLocalizedMessage(new OutOfMemoryError()));
	}
	
	public void testClassShortName() {
		assertEquals("String", Main.getClassShortName(String.class));
		assertEquals("MainTest", Main.getClassShortName(MainTest.class));
	}

}
