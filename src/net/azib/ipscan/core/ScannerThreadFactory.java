/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import net.azib.ipscan.config.GlobalConfig;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.feeders.Feeder;

/**
 * ScannerThreadFactory.
 * 
 * Note: setter injection is used for this class to avoid cyclic dependency conflicts.
 *
 * @author Anton Keks
 */
public class ScannerThreadFactory {
	
	private ScanningResultList scanningResults;
	private Scanner scanner;
	private StateMachine stateMachine;
	private GlobalConfig globalConfig;
	
	public ScannerThreadFactory(ScanningResultList scanningResults, Scanner scanner, StateMachine stateMachine, GlobalConfig globalConfig) {
		this.scanningResults = scanningResults;
		this.scanner = scanner;
		this.stateMachine = stateMachine;
		this.globalConfig = globalConfig;
	}

	public ScannerThread createScannerThread(Feeder feeder, ScanningProgressCallback progressCallback, ScanningResultsCallback resultsCallback) {
		return new ScannerThread(feeder, scanner, stateMachine, progressCallback, scanningResults, globalConfig, resultsCallback);
	}
}
