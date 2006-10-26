/**
 * 
 */
package net.azib.ipscan.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	private ResultsComparator resultsComparator = new ResultsComparator();

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

	/**
	 * @param indices
	 */
	public void remove(int[] indices) {
		// this removal is probably O(n^2)...
		for (int i = 0; i < indices.length; i++) {
			scanningResults.remove(i);	
		}
	}
	
	/**
	 * Sorts by the specified column index.
	 * @param columnIndex
	 */
	public void sort(int columnIndex) {
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
