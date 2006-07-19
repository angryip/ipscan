/**
 * 
 */
package net.azib.ipscan.fetchers;
import java.util.SortedSet;

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

	public String scan(ScanningSubject subject) {
		SortedSet portsList = scanPorts(subject);
		return portsList.size() > 0 ? "no filtered info: " +  portListToRange(portsList, displayAsRanges) : null;
	}

}
