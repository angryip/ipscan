/**
 * 
 */
package net.azib.ipscan.core;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * The holder of scanning result for a single IP address.
 *
 * @author anton
 */
public class ScanningResult {

	/** The scanned IP address */
	private InetAddress address;
	/** Scanning results as List, result of each Fetcher is an element */
	private List values;
	/** Scanning result type, see constants in {@link ScanningSubject} */
	private int type;
	
	/**
	 * Creates a new instance, initializing the first value to the 
	 * provided address
	 * @param address
	 */
	ScanningResult(InetAddress address) {
		this.address = address;
		values = new ArrayList();
		values.add(address.getHostAddress());
		type = ScanningSubject.RESULT_TYPE_UNKNOWN;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	/**
	 * @return the scanning results as List, result of each Fetcher is an element
	 */
	public List getValues() {
		return values;
	}
	
	/**
	 * Sets scanning result type, see constants in {@link ScanningSubject}
	 */	
	void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the scanning result type, see constants in {@link ScanningSubject}
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the value returned by the specified fetcher
	 * @param fetcherIndex
	 * @param value
	 */
	public void setValue(int fetcherIndex, String value) {
		// TODO: make values an array
		if (values.size() <= fetcherIndex)
			values.add(fetcherIndex, value);
		else
			values.set(fetcherIndex, value);
	}
	
}
