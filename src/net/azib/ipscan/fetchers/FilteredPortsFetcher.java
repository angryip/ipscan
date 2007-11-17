/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.fetchers;

import java.util.SortedSet;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.values.NotScannedValue;
import net.azib.ipscan.core.values.NumericListValue;

/**
 * FilteredPortsFetcher uses the scanning results of PortsFetcher to display filtered ports.
 *
 * @author Anton Keks
 */
public class FilteredPortsFetcher extends PortsFetcher {

	public FilteredPortsFetcher(ScannerConfig scannerConfig) {
		super(scannerConfig);
	}

	public String getLabel() {
		return "fetcher.ports.filtered";
	}

	public Object scan(ScanningSubject subject) {
		boolean portsScanned = scanPorts(subject);
		if (!portsScanned)
			return NotScannedValue.INSTANCE;

		SortedSet<Integer> filteredPorts = getFilteredPorts(subject);
		return filteredPorts.size() > 0 ? new NumericListValue(filteredPorts, displayAsRanges) : null;
	}
}
