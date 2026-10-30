/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningResultList.ScanInfo;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.feeders.RescanFeeder;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.IPFetcher;
import org.junit.Test;
import org.objenesis.ObjenesisHelper;

import java.net.InetAddress;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ScannerThreadTest
 *
 * @author Anton Keks
 */
public class ScannerDispatcherThreadTest {
	
	@Test
	public void testConstruction() throws Exception {
		var registry = mock(FetcherRegistry.class);
		when(registry.getSelectedFetchers()).thenReturn(Collections.singleton(new IPFetcher()));
		var feeder = mock(Feeder.class);
		when(feeder.getInfo()).thenReturn("info");
		when(feeder.getName()).thenReturn("text.ip");

		var scanningResults = new ScanningResultList(registry);
		scanningResults.info = new ScanInfo(); // initialize info so we can add a dummy result
		scanningResults.registerAtIndex(0, scanningResults.createResult(InetAddress.getLocalHost()));

		var config = mock(ScannerConfig.class);
		config.maxThreads = 10;

		var thread = new ScannerDispatcherThread(feeder, new Scanner(registry), null, null, scanningResults, config, null);

		assertTrue("ScannerThread should not clear the results - otherwise rescanning will not work", 
				    scanningResults.areResultsAvailable());
		
		assertEquals(thread.getClass().getSimpleName(), thread.getName());
		assertTrue(thread.isDaemon());
		assertEquals(config.maxThreads, ((ThreadPoolExecutor)thread.threadPool).getMaximumPoolSize());
		assertEquals(thread, ((ThreadPoolExecutor) thread.threadPool).getThreadFactory());
	}
	
	@Test
	public void stoppingScanCanBeResumedWithMoreAddresses() throws Exception {
		var registry = mock(FetcherRegistry.class);
		when(registry.getSelectedFetchers()).thenReturn(Collections.singleton(new IPFetcher()));

		var stateMachine = new StateMachine() {};
		stateMachine.transitionToNext(); // to STARTING

		var feeder = new RescanFeeder(mockFeeder(), "127.0.0.1");
		var scanningResults = new ScanningResultList(registry);

		var config = mock(ScannerConfig.class);
		config.maxThreads = 0;
		config.threadDelay = 1;

		var scanner = mock(Scanner.class);
		var scanStarted = new CountDownLatch(1);
		var scanFinished = new CountDownLatch(1);
		doAnswer(i -> {
			scanStarted.countDown();
			scanFinished.await();
			return null;
		}).when(scanner).scan(any(), any(), any());

		var thread = new ScannerDispatcherThread(feeder, scanner, stateMachine, mock(ScanningProgressCallback.class),
			scanningResults, config, mock(ScanningResultCallback.class));

		stateMachine.startScanning();
		thread.start();
		try {
			assertTrue("no address was dispatched", scanStarted.await(5, TimeUnit.SECONDS));

			// the only address is being scanned, so the scan is stopping and waiting for it
			waitForState(stateMachine, ScanningState.STOPPING);

			assertEquals(1, feeder.addAddresses("127.0.0.2"));
			assertTrue("stopping scan should be resumable", stateMachine.resumeScanning());
			scanFinished.countDown();

			waitForState(stateMachine, ScanningState.IDLE);
			assertEquals(2, scanningResults.getScanInfo().getHostCount());
		}
		finally {
			scanFinished.countDown();
			thread.join(5000);
		}
	}

	private void waitForState(StateMachine stateMachine, ScanningState state) throws InterruptedException {
		var deadline = System.currentTimeMillis() + 5000;
		while (!stateMachine.inState(state)) {
			assertTrue("timed out waiting for " + state, System.currentTimeMillis() < deadline);
			Thread.sleep(10);
		}
	}

	private Feeder mockFeeder() {
		var feeder = mock(Feeder.class);
		when(feeder.subject(any())).thenAnswer(i -> new ScanningSubject(i.getArgument(0)));
		return feeder;
	}

	@Test
	public void threadFactoryProducesDaemons() throws Exception {
		var thread = ObjenesisHelper.newInstance(ScannerDispatcherThread.class);
		thread.threadGroup = new ThreadGroup("foo");
		var t = thread.newThread(mock(Runnable.class));
		assertTrue(t.isDaemon());
		assertSame(thread.threadGroup, t.getThreadGroup());
	}
}
