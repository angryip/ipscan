/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.net.PingResult;

import org.junit.Before;
import org.junit.Test;

/**
 * ScanningSubjectTest
 *
 * @author Anton Keks
 */
public class ScanningSubjectTest {
	
	private ScanningSubject subject;
	private ScannerConfig config;
	private PingResult pingResult;
	
	@Before
	public void initTest() {
		config = mock(ScannerConfig.class);
		subject = new ScanningSubject(null);
		subject.config = config;
		config.portTimeout = 1000;
		config.adaptPortTimeout = true;
		pingResult = new PingResult(null,0);
		subject.setParameter(ScanningSubject.PARAMETER_PING_RESULT, pingResult);
	}
	
	@Test
		public void adaptedPortTimeoutUsingReplies() throws Exception {
		subject.setParameter(ScanningSubject.PARAMETER_PING_RESULT, null);
		// no ping replies yet
		assertEquals(config.portTimeout, subject.getAdaptedPortTimeout());

		subject.setParameter(ScanningSubject.PARAMETER_PING_RESULT, pingResult);
		// no ping replies yet
		assertEquals(config.portTimeout, subject.getAdaptedPortTimeout());
		
		pingResult.addReply(100);
		// too few replies yet
		assertEquals(config.portTimeout, subject.getAdaptedPortTimeout());
		
		pingResult.addReply(200);
		// too few replies yet
		assertEquals(config.portTimeout, subject.getAdaptedPortTimeout());
		
		pingResult.addReply(300);
		// now we can shorten the timeout
		assertEquals(300 * 3, subject.getAdaptedPortTimeout());
		
		pingResult.addReply(500);
		// adapted timeout is not recalculated
		assertEquals(300 * 3, subject.getAdaptedPortTimeout());
		
		subject.adaptedPortTimeout = -1;
		// adapted timeout is now recalculated and longer than configured one
		assertEquals(config.portTimeout, subject.getAdaptedPortTimeout());
	}
	
	@Test
	public void forcedAdaptedTimeout() throws Exception {
		pingResult.enableTimeoutAdaptation();
		// cannot force yet - no replies
		assertEquals(config.portTimeout, subject.getAdaptedPortTimeout());
		
		pingResult.addReply(100);
		pingResult.enableTimeoutAdaptation();
		assertEquals(100 * 3, subject.getAdaptedPortTimeout());		
	}
	
	@Test
	public void adaptedTimeoutTooShort() throws Exception {
		config.minPortTimeout = 100;
		pingResult.addReply(0);
		pingResult.enableTimeoutAdaptation();
		assertEquals(config.minPortTimeout, subject.getAdaptedPortTimeout());
	}
}
