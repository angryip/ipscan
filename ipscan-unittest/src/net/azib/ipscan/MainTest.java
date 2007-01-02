package net.azib.ipscan;

import static org.junit.Assert.*;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.junit.Test;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.feeders.FeederException;

public class MainTest {

	@Test
	public void getLocalizedMessage() {
		// unknown exception
		final boolean wasStackTraceLogged[] = {false};
		Throwable e = new Exception("hello, test!");
		Main.LOG.setUseParentHandlers(false);
		Main.LOG.addHandler(new Handler() {
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
		assertEquals(Labels.getLabel("exception.FeederException.range.greaterThan"), 
				Main.getLocalizedMessage(new FeederException("range.greaterThan")));
		
		// message-less localized exception
		assertEquals(Labels.getLabel("exception.OutOfMemoryError"), 
				Main.getLocalizedMessage(new OutOfMemoryError()));
	}
	
	@Test
	public void classShortName() {
		assertEquals("String", Main.getClassShortName(String.class));
		assertEquals("MainTest", Main.getClassShortName(MainTest.class));
	}

}
