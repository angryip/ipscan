/**
 * 
 */
package net.azib.ipscan.core;

import java.util.List;

/**
 * The holder of scanning result for a single IP address.
 *
 * @author anton
 */
public class ScanningResult {

	/** Scanning results as List, result of each Fetcher is an element */
	private List values;
	/** Scanning result type, see constants in {@link ScanningSubject} */
	private int type;
	
	/**
	 * Constructor.
	 * @param values values returned by each fetcher (Strings)
	 * @param resultType see constants in {@link ScanningSubject}
	 */
	public ScanningResult(List values, int resultType) {
		this.values = values;
		this.type = resultType;
	}
	
	public List getValues() {
		return values;
	}
	
	public int getType() {
		return type;
	}
	
}
