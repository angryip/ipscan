/**
 * 
 */
package net.azib.ipscan.gui;

import java.net.InetAddress;

import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultsCallback;

/**
 * 
 * @author anton
 */
public class ScanningResultsConsumer implements ScanningResultsCallback {
	
	private ResultTable resultTable;

	public ScanningResultsConsumer(ResultTable resultTable) {
		this.resultTable = resultTable;
	}

	/**
	 * @see net.azib.ipscan.core.ScanningResultsCallback#prepareForResults(InetAddress)
	 */
	public int prepareForResults(InetAddress address) {
		return resultTable.addResultsRow(address.getHostAddress());
	}

	/**
	 * @see net.azib.ipscan.core.ScanningResultsCallback#consumeResults(int, ScanningResult)
	 */
	public void consumeResults(int preparationNumber, ScanningResult results) {
		resultTable.populateResults(preparationNumber, results);
	}

}
