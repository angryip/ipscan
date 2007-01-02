/**
 * 
 */
package net.azib.ipscan.fetchers;

import java.util.SortedSet;

import net.azib.ipscan.config.GlobalConfig;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.values.NumericListValue;

/**
 * FilteredPortsFetcher uses the scanning results of PortsFetcher to display filtered ports.
 *
 * @author anton
 */
public class FilteredPortsFetcher extends PortsFetcher {

	public FilteredPortsFetcher(GlobalConfig globalConfig) {
		super(globalConfig);
	}

	public String getLabel() {
		return "fetcher.ports.filtered";
	}

	public Object scan(ScanningSubject subject) {
		scanPorts(subject);
		SortedSet filteredPorts = getFilteredPorts(subject);
		return filteredPorts.size() > 0 ? new NumericListValue(filteredPorts, displayAsRanges) : null;
	}
}
