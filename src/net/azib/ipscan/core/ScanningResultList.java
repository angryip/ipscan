/**
 * 
 */
package net.azib.ipscan.core;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.fetchers.Fetcher;

/**
 * The holder of scanning results.
 *
 * @author anton
 */
public class ScanningResultList {

	private List fetchers;
	private List scanningResults = new ArrayList(1024);
	private ResultsComparator resultsComparator = new ResultsComparator();

	// TODO: provide fetchers via DI here!
	public void setFetchers(List fetchers) {
		this.fetchers = fetchers;
	}

	public List getFetchers() {
		return fetchers;
	}
	
	/**
	 * Adds the new scanned IP address
	 * @param address
	 * @return the index of the added address, can be used in calls to other methods
	 */
	public synchronized int add(InetAddress address) {
		int index = scanningResults.size();
		scanningResults.add(new ScanningResult(address));
		return index;
	}

	/**
	 * Returns all results for a particular IP address as a String.
	 * This is used in showing the IP Details dialog box.
	 * TODO: write tests!
	 * 
	 * @param index
	 * @return
	 */
	public synchronized String getResultsAsString(int index) {
		// cross-platform newline :-)
		String newLine = System.getProperty("line.separator");
		// TODO: what if a String is retrieved???
		ScanningResult scanningResult = (ScanningResult) scanningResults.get(index);
		StringBuffer details = new StringBuffer(1024);
		Iterator iterator = scanningResult.getValues().iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			String fetcherName = Labels.getLabel(((Fetcher)fetchers.get(i)).getLabel());
			details.append(fetcherName).append(":\t");
			Object value = iterator.next(); 
			details.append(value != null ? value : "");
			details.append(newLine).append("--------------------------------------------------------------------------------------").append(newLine);
		}
		return details.toString();	
	}

	public synchronized void clear() {
		scanningResults.clear();
	}
	
	/**
	 * @return an Iterator of scanning results
	 * 
	 * Note: the returned Iterator is not synchronized
	 */
	public synchronized Iterator iterator() {
		return scanningResults.iterator();
	}

	/**
	 * @param index
	 * @return the results of the IP adress, corresponding to an index
	 */
	public synchronized ScanningResult getResult(int index) {
		return (ScanningResult) scanningResults.get(index);
	}

	/**
	 * Removes some elements by the provided indices
	 * @param indices
	 * 
	 * Note: old indices returned by {@link #add(InetAddress)} are no longer valid
	 */
	public synchronized void remove(int[] indices) {
		// TODO: this removal is probably O(n^2)...
		for (int i = 0; i < indices.length; i++) {
			scanningResults.remove(i);	
		}
	}
	
	/**
	 * Sorts by the specified column index.
	 * @param columnIndex
	 * 
	 * Note: old indices returned by {@link #add(InetAddress)} are no longer valid
	 */
	public synchronized void sort(int columnIndex) {
		resultsComparator.index = columnIndex;
		Collections.sort(scanningResults, resultsComparator);
	}
	
	private static class ResultsComparator implements Comparator {
		
		private int index;

		public int compare(Object o1, Object o2) {
			if (!(o1 instanceof ScanningResult))
				return -1;
			if (!(o2 instanceof ScanningResult))
				return 1;
			
			Object val1 = ((ScanningResult)o1).getValues().get(index);
			Object val2 = ((ScanningResult)o2).getValues().get(index);
			
			return val1.toString().compareTo(val2.toString());
		}
	}

}
