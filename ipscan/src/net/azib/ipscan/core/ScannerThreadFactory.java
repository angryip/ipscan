/**
 * 
 */
package net.azib.ipscan.core;

import net.azib.ipscan.feeders.Feeder;

/**
 * ScannerThreadFactory
 *
 * @author anton
 */
public class ScannerThreadFactory {
	
	private ScanningResultList scanningResults;
	private Scanner scanner;

	public ScannerThreadFactory(ScanningResultList scanningResults, Scanner scanner) {
		this.scanningResults = scanningResults;
		this.scanner = scanner;
	}

	public ScannerThread createScannerThread(Feeder feeder) {
		return new ScannerThread(feeder, scanner, scanningResults);
	}

}
