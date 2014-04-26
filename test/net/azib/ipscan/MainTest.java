package net.azib.ipscan;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.feeders.FeederException;
import org.junit.Test;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
		assertEquals(Labels.getLabel("exception.FeederException.malformedIP"),
				Main.getLocalizedMessage(new FeederException("malformedIP")));
		
		// message-less localized exception
		assertEquals(Labels.getLabel("exception.OutOfMemoryError"), 
				Main.getLocalizedMessage(new OutOfMemoryError()));
	}
}
