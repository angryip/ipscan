/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.values.NotScanned;
import net.azib.ipscan.core.values.NumericRangeList;

import javax.inject.Inject;
import java.util.SortedSet;

/**
 * FilteredPortsFetcher uses the scanning results of PortsFetcher to display filtered ports.
 *
 * @author Anton Keks
 */
public class FilteredPortsFetcher extends PortsFetcher {

	@Inject public FilteredPortsFetcher(ScannerConfig scannerConfig) {
		super(scannerConfig);
	}

	public String getId() {
		return "fetcher.ports.filtered";
	}

	public Object scan(ScanningSubject subject) {
		boolean portsScanned = scanPorts(subject);
		if (!portsScanned)
			return NotScanned.VALUE;

		SortedSet<Integer> filteredPorts = getFilteredPorts(subject);
		return filteredPorts.size() > 0 ? new NumericRangeList(filteredPorts, displayAsRanges) : null;
	}
}
