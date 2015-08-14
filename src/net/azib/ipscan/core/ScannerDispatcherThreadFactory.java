/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.feeders.Feeder;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * ScannerThreadFactory.
 * 
 * Note: setter injection is used for this class to avoid cyclic dependency conflicts.
 *
 * @author Anton Keks
 */
@Singleton
public class ScannerDispatcherThreadFactory {
	
	private ScanningResultList scanningResults;
	private Scanner scanner;
	private StateMachine stateMachine;
	private ScannerConfig scannerConfig;

	@Inject public ScannerDispatcherThreadFactory(ScanningResultList scanningResults, Scanner scanner, StateMachine stateMachine, ScannerConfig scannerConfig) {
		this.scanningResults = scanningResults;
		this.scanner = scanner;
		this.stateMachine = stateMachine;
		this.scannerConfig = scannerConfig;
	}

	public ScannerDispatcherThread createScannerThread(Feeder feeder, ScanningProgressCallback progressCallback, ScanningResultCallback resultsCallback) {
		return new ScannerDispatcherThread(feeder, scanner, stateMachine, progressCallback, scanningResults, scannerConfig, resultsCallback);
	}
}
