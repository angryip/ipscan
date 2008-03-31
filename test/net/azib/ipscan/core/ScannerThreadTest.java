/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Collections;
import java.util.concurrent.ThreadPoolExecutor;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningResultList.ScanInfo;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.IPFetcher;

import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * ScannerThreadTest
 *
 * @author Anton Keks
 */
public class ScannerThreadTest {
	
	@Test
	public void testConstruction() throws Exception {
		FetcherRegistry registry = createMock(FetcherRegistry.class);
		expect(registry.getSelectedFetchers()).andReturn(Collections.<Fetcher>singleton(new IPFetcher())).anyTimes();
		Feeder feeder = createMock(Feeder.class);
		expect(feeder.getInfo()).andReturn("info");
		expect(feeder.getId()).andReturn("text.ip");
		replay(registry, feeder);
		
		ScanningResultList scanningResults = new ScanningResultList(registry);
		scanningResults.info = new ScanInfo(); // initialize info so we can add a dummy result
		scanningResults.registerAtIndex(0, scanningResults.createResult(InetAddress.getLocalHost()));
		
		ScannerConfig config = createMock(ScannerConfig.class);
		config.maxThreads = 10;
		
		ScannerThread thread = new ScannerThread(feeder, new Scanner(registry), null, null, scanningResults, config, null);

		assertTrue("ScannerThread should not clear the results - otherwise rescanning will not work", 
				    scanningResults.areResultsAvailable());
		
		assertEquals(thread.getClass().getSimpleName(), thread.getName());
		assertTrue(thread.isDaemon());
		assertEquals(config.maxThreads, ((ThreadPoolExecutor)thread.threadPool).getMaximumPoolSize());
		assertEquals(thread, ((ThreadPoolExecutor)thread.threadPool).getThreadFactory());
		
		verify(registry, feeder);
	}
	
	@Test
	public void threadFactoryProducesDaemons() throws Exception {
		ScannerThread thread = createMock(ScannerThread.class, (Method)null);
		thread.threadGroup = new ThreadGroup("foo");
		Thread t = thread.newThread(createMock(Runnable.class));
		assertTrue(t.isDaemon());
		assertSame(thread.threadGroup, t.getThreadGroup());
	}
}
