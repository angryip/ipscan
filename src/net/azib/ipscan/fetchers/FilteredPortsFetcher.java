/**
 * 
 */
package net.azib.ipscan.fetchers;

import java.util.Set;

import net.azib.ipscan.core.ScanningSubject;

/**
 * FilteredPortsFetcher uses the scanning results of PortsFetcher to display filtered ports.
 *
 * @author anton
 */
public class FilteredPortsFetcher extends PortsFetcher {

	public String getLabel() {
		return "fetcher.ports.filtered";
	}

	public Object scan(ScanningSubject subject) {
		scanPorts(subject);
		Set filteredPorts = getFilteredPorts(subject);
		return filteredPorts.size() > 0 ? portListToRange(filteredPorts, displayAsRanges) : null;
	}

}
