/**
 * 
 */
package net.azib.ipscan.core;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;

/**
 * The holder of scanning results.
 *
 * @author anton
 */
public class ScanningResultList {

	private static final int RESULT_LIST_INITIAL_SIZE = 1024;
	
	private FetcherRegistry fetcherRegistry;
	// selected fetchers are cached here, because the may be changed in the registry already
	private List selectedFetchers;
	
	private List resultList = new ArrayList(1024);
	private ResultsComparator resultsComparator = new ResultsComparator();

	public ScanningResultList(FetcherRegistry fetcherRegistry) {
		this.fetcherRegistry = fetcherRegistry;
		clear();
	}

	/**
	 * @return selected fetchers that were used for the last scan
	 * Note: they may be different from {@link FetcherRegistry#getSelectedFetchers()}
	 */
	public List getFetchers() {
		return selectedFetchers;
	}
	
	/**
	 * Adds the new scanned IP address
	 * @param address
	 * @return the index of the added address, can be used in calls to other methods
	 */
	public synchronized int add(InetAddress address) {
		int index = resultList.size();
		resultList.add(new ScanningResult(address, fetcherRegistry.getSelectedFetchers().size()));
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
		
		ScanningResult scanningResult = (ScanningResult) resultList.get(index);
		StringBuffer details = new StringBuffer(1024);
		Iterator iterator = scanningResult.getValues().iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			String fetcherName = Labels.getLabel(((Fetcher)selectedFetchers.get(i)).getLabel());
			details.append(fetcherName).append(":\t");
			Object value = iterator.next(); 
			details.append(value != null ? value : "");
			details.append(newLine).append("--------------------------------------------------------------------------------------").append(newLine);
		}
		return details.toString();	
	}

	/**
	 * Clears previous scanning results, prepares for a new scan.
	 */
	public synchronized void clear() {
		// clear the results
		resultList.clear();
		// reload currently selected fetchers
		selectedFetchers = new ArrayList(fetcherRegistry.getSelectedFetchers());
	}
	
	/**
	 * @return an Iterator of scanning results
	 * 
	 * Note: the returned Iterator is not synchronized
	 */
	public synchronized Iterator iterator() {
		return resultList.iterator();
	}

	/**
	 * @param index
	 * @return the results of the IP adress, corresponding to an index
	 */
	public synchronized ScanningResult getResult(int index) {
		return (ScanningResult) resultList.get(index);
	}

	/**
	 * Removes the elements by the provided indices
	 * Note: old indices returned by {@link #add(InetAddress)} are no longer valid
	 * @param indices a sorted list of indices to remove
	 */
	public synchronized void remove(int[] indices) {
		// this rebuild is faster then a number of calls to remove()
		// however, a further speedup can be obtained by using a Set instead of binarySearch()
		List newList = new ArrayList(RESULT_LIST_INITIAL_SIZE);
		for (int i = 0; i < resultList.size(); i++) {
			if (Arrays.binarySearch(indices, i) < 0)
				newList.add(resultList.get(i));
		}
		resultList = newList;
	}
	
	/**
	 * Sorts by the specified column index.
	 * Note: old indices returned by {@link #add(InetAddress)} are no longer valid
	 * @param columnIndex
	 */
	public synchronized void sort(int columnIndex) {
		resultsComparator.index = columnIndex;
		Collections.sort(resultList, resultsComparator);
	}
	
	/**
	 * Finds the text in the result list. 
	 * @param text the text to find
	 * @param startIndex the element to start from
	 * @return the index of found element, or -1
	 */
	public int findText(String text, int startIndex) {
		for (int i = startIndex; i < resultList.size(); i++) {
			ScanningResult scanningResult = getResult(i);
			
			for (Iterator j = scanningResult.getValues().iterator(); j.hasNext();) {
				Object value = j.next();
				
				// TODO: case-insensitive search
				if (value != null && value.toString().indexOf(text) >= 0) {						
					return i;
				}
			}
		}
		// not found
		return -1;
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
