/**
 * 
 */
package net.azib.ipscan.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.fetchers.Fetcher;

/**
 * The holder of scanning results.
 * TODO: synchronization
 * TODO: tests
 * TODO: javadocs 
 *
 * @author anton
 */
public class ScanningResultList {

	private List fetchers;
	private List scanningResults = new ArrayList(1024);

	public synchronized int add(String name) {
		int index = scanningResults.size();
		scanningResults.add(name);
		return index;
	}

	public synchronized void update(int index, ScanningResult result) {
		scanningResults.set(index, result);
	}

	/**
	 * Returns all results for a particular IP address as a String.
	 * This is used in showing the IP Details dialog box.
	 * 
	 * @param index
	 * @return
	 */
	public synchronized String getResultsAsString(int index) {
		// TODO: what if a String is retrieved???
		ScanningResult scanningResult = (ScanningResult) scanningResults.get(index);
		StringBuffer details = new StringBuffer(1024);
		Iterator iterator = scanningResult.getValues().iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			String fetcherName = Labels.getInstance().getString(((Fetcher)fetchers.get(i)).getLabel());
			details.append(fetcherName).append(":\t");
			Object value = iterator.next(); 
			details.append(value != null ? value : "");
			details.append("\n--------------------------------------------------------------------------------------\n");
		}
		return details.toString();	
	}

	public void setFetchers(List fetchers) {
		this.fetchers = fetchers;
	}

	public List getFetchers() {
		return fetchers;
	}

	public synchronized void clear() {
		scanningResults.clear();
	}
	
	/**
	 * @return an Iterator of scanning results
	 */
	public synchronized Iterator iterator() {
		return scanningResults.iterator();
	}

	public synchronized boolean isReady(int tableIndex) {
		return scanningResults.get(tableIndex) instanceof ScanningResult;
	}

	/**
	 * @param tableIndex
	 * @return a results of a single IP adress, corresponding to an index
	 */
	public synchronized ScanningResult getResult(int tableIndex) {
		// TODO: error handling
		return (ScanningResult) scanningResults.get(tableIndex);
	}

	public synchronized String getName(int tableIndex) {
		// TODO: error handling
		return (String) scanningResults.get(tableIndex);
	}

}
