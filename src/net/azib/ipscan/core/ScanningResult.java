/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

/**
 * The holder of scanning result for a single IP address.
 *
 * @author Anton Keks
 */
public class ScanningResult {
	
	public enum ResultType {UNKNOWN, DEAD, ALIVE, WITH_PORTS}

	/** The scanned IP address */
	private InetAddress address;
	/** Scanning results, result of each Fetcher is an element */
	private Object[] values;
	/** Scanning result type */ 
	private ResultType type;
	
	/**
	 * Creates a new instance, initializing the first value to the 
	 * provided address
	 * @param address
	 * @param numberOfFetchers the number of currently available fetchers
	 */
	ScanningResult(InetAddress address, int numberOfFetchers) {
		this.address = address;
		values = new Object[numberOfFetchers];
		values[0] = address.getHostAddress();
		type = ResultType.UNKNOWN;
	}
	
	/**
	 * Resets scanned data: returns the result to the "just created" state. 
	 * Used for rescanning.
	 */
	public void reset() {
		values = new Object[values.length];
		values[0] = address.getHostAddress();
		type = ResultType.UNKNOWN;
	}

	public InetAddress getAddress() {
		return address;
	}
	
	/**
	 * @return true if the result is ready (completely scanned)
	 */
	public boolean isReady() {
		return type != ResultType.UNKNOWN;
	}
	
	/**
	 * @return the scanning results as an unmodifiable List, result of each Fetcher is an element
	 */
	public List<Object> getValues() {
		return Arrays.asList(values);
	}
	
	/**
	 * Sets scanning result type
	 */	
	void setType(ResultType type) {
		this.type = type;
	}

	/**
	 * @return the scanning result type
	 */
	public ResultType getType() {
		return type;
	}

	/**
	 * Sets the value returned by the specified fetcher
	 * @param fetcherIndex
	 * @param value
	 */
	public void setValue(int fetcherIndex, Object value) {
		values[fetcherIndex] = value;
	}
	
}
