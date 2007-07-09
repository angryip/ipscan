/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;

/**
 * The holder of scanning results.
 *
 * @author anton
 */
public class ScanningResultList implements Iterable<ScanningResult> {

	private static final int RESULT_LIST_INITIAL_SIZE = 1024;
	
	private FetcherRegistry fetcherRegistry;
	// selected fetchers are cached here, because the may be changed in the registry already
	private List<Fetcher> selectedFetchers;

	private List<ScanningResult> resultList = new ArrayList<ScanningResult>(RESULT_LIST_INITIAL_SIZE);
	private Map<InetAddress, Integer> resultIndexes = new HashMap<InetAddress, Integer>(RESULT_LIST_INITIAL_SIZE);
	
	private ResultsComparator resultsComparator = new ResultsComparator();

	public ScanningResultList(FetcherRegistry fetcherRegistry) {
		this.fetcherRegistry = fetcherRegistry;
		clear();
	}

	/**
	 * @return selected fetchers that were used for the last scan
	 * Note: they may be different from {@link FetcherRegistry#getSelectedFetchers()}
	 */
	public List<Fetcher> getFetchers() {
		return selectedFetchers;
	}
	
	/**
	 * Adds the new scanned IP address
	 * @param address
	 * @return the index of the added address, can be used in calls to other methods
	 */
	public synchronized int add(InetAddress address) {
		Integer index = resultIndexes.get(address);
		if (index == null) {
			index = resultList.size();
			resultList.add(new ScanningResult(address, fetcherRegistry.getSelectedFetchers().size()));
			resultIndexes.put(address, index);
		}
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
		
		ScanningResult scanningResult = resultList.get(index);
		StringBuffer details = new StringBuffer(1024);
		Iterator<Object> iterator = scanningResult.getValues().iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			String fetcherName = Labels.getLabel(selectedFetchers.get(i).getLabel());
			details.append(fetcherName).append(":\t");
			Object value = iterator.next(); 
			details.append(value != null ? value : "");
			details.append(newLine);
		}
		return details.toString();	
	}

	/**
	 * Clears previous scanning results, prepares for a new scan.
	 */
	public synchronized void clear() {
		// clear the results
		resultList.clear();
		resultIndexes.clear();
		// reload currently selected fetchers
		selectedFetchers = new ArrayList<Fetcher>(fetcherRegistry.getSelectedFetchers());
	}
	
	/**
	 * @return an Iterator of scanning results
	 * 
	 * Note: the returned Iterator is not synchronized
	 */
	public synchronized Iterator<ScanningResult> iterator() {
		return resultList.iterator();
	}

	/**
	 * @param index
	 * @return the results of the IP adress, corresponding to an index
	 */
	public synchronized ScanningResult getResult(int index) {
		return resultList.get(index);
	}

	/**
	 * Removes the elements by the provided indices
	 * Note: old indices returned by {@link #add(InetAddress)} are no longer valid
	 * @param indices a sorted list of indices to remove
	 */
	public synchronized void remove(int[] indices) {
		// this rebuild is faster then a number of calls to remove()
		// however, a further speedup may be obtained by using a Set instead of binarySearch()
		List<ScanningResult> newList = new ArrayList<ScanningResult>(RESULT_LIST_INITIAL_SIZE);
		Map<InetAddress, Integer> newMap = new HashMap<InetAddress, Integer>(RESULT_LIST_INITIAL_SIZE);
		for (int i = 0; i < resultList.size(); i++) {
			if (Arrays.binarySearch(indices, i) < 0) {
				newList.add(resultList.get(i));
				newMap.put(resultList.get(i).getAddress(), newList.size()-1);
			}
		}
		resultList = newList;
		resultIndexes = newMap;
	}
	
	/**
	 * Sorts by the specified column index.
	 * Note: old indices returned by {@link #add(InetAddress)} are no longer valid
	 * @param columnIndex
	 */
	public synchronized void sort(int columnIndex) {
		resultsComparator.index = columnIndex;
		Collections.sort(resultList, resultsComparator);
		// now rebuild indexes
		resultIndexes = new HashMap<InetAddress, Integer>(RESULT_LIST_INITIAL_SIZE);
		for (int i = 0; i < resultList.size(); i++) {
			resultIndexes.put(resultList.get(i).getAddress(), i);
		}
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
			
			for (Object value : scanningResult.getValues()) {				
				// TODO: case-insensitive search
				if (value != null && value.toString().indexOf(text) >= 0) {						
					return i;
				}
			}
		}
		// not found
		return -1;
	}
	
	static class ResultsComparator implements Comparator<ScanningResult> {
		
		private int index;

		public int compare(ScanningResult r1, ScanningResult r2) {
			Object val1 = r1.getValues().get(index);
			Object val2 = r2.getValues().get(index);
			
			return val1.toString().compareTo(val2.toString());
		}
	}

}
