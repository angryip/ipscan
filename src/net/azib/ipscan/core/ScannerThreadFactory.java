/**
 * 
 */
package net.azib.ipscan.core;

import net.azib.ipscan.config.GlobalConfig;
import net.azib.ipscan.feeders.Feeder;

/**
 * ScannerThreadFactory
 *
 * @author anton
 */
public class ScannerThreadFactory {
	
	private ScanningResultList scanningResults;
	private Scanner scanner;
	private GlobalConfig globalConfig;

	public ScannerThreadFactory(ScanningResultList scanningResults, Scanner scanner, GlobalConfig globalConfig) {
		this.scanningResults = scanningResults;
		this.scanner = scanner;
		this.globalConfig = globalConfig;
	}

	public ScannerThread createScannerThread(Feeder feeder) {
		return new ScannerThread(feeder, scanner, scanningResults, globalConfig);
	}

}
