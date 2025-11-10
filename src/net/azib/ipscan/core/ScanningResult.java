/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * The holder of scanning result for a single IP address.
 *
 * @author Anton Keks
 */
public class ScanningResult {
	public enum ResultType {
		UNKNOWN, DEAD, ALIVE, WITH_PORTS;

		public boolean matches(ResultType that) {
			if (this.ordinal() <= DEAD.ordinal())
				return that.ordinal() <= DEAD.ordinal();
			return this.ordinal() <= that.ordinal();
		}
	}

	private InetAddress address;
	private String mac;

	/** Scanning results, result of each Fetcher is an element */
	private Object[] values;
	/** Scanning result type */ 
	private ResultType type;
	
	/** reference to the containing list */
	ScanningResultList resultList;
	
	/**
	 * Creates a new instance, initializing the first value to the 
	 * provided address
	 * @param numberOfFetchers the number of currently available fetchers
	 */
	public ScanningResult(InetAddress address, int numberOfFetchers) {
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
	
	public void setValues(Object[] values) {
		this.values = values;
	}

	/**
	 * Sets scanning result type
	 */	
	public void setType(ResultType type) {
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
	 */
	public void setValue(int fetcherIndex, Object value) {
		values[fetcherIndex] = value;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getMac() {
		return mac;
	}

	/**
	 * Returns all results for this IP address as a String.
	 * This is used in showing the IP Details dialog box.
	 * @return human-friendly text representation of results
	 */
	public String toString() {
		// cross-platform newline :-)
		var newLine = System.getProperty("line.separator");

		var details = new StringBuilder(1024);
		Iterator<?> iterator = getValues().iterator();
		var fetchers = resultList.getFetchers();
		for (var i = 0; iterator.hasNext(); i++) {
			var fetcherName = fetchers.get(i).getName();
			details.append(fetcherName).append(":\t");
			var value = iterator.next();
			details.append(value != null ? value : "");
			details.append(newLine);
		}
		return details.toString();	
	}
}
