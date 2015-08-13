/**
 * 
 */
package net.azib.ipscan.exporters;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.PortIterator;
import net.azib.ipscan.core.values.NumericRangeList;
import net.azib.ipscan.fetchers.IPFetcher;
import net.azib.ipscan.fetchers.PortsFetcher;

import javax.inject.Inject;
import java.io.IOException;

/**
 * IP List Exporter
 * <p/>
 * Exports only IP:port info, outputting each distinct IP:port pair on separate line.
 *
 * @author Anton Keks
 */
public class IPListExporter extends AbstractExporter {
	static final char DELIMETER = ':';
	
	private int ipFetcherIndex;
	private int portsFetcherIndex;

	@Inject public IPListExporter() {}

	public String getId() {
		return "exporter.ipList";
	}

	public String getFilenameExtension() {
		return "lst";
	}
	
	public void setFetchers(String[] fetcherNames) throws IOException {
		ipFetcherIndex = findFetcherById(IPFetcher.ID, fetcherNames);
		portsFetcherIndex = findFetcherById(PortsFetcher.ID, fetcherNames);
	}
	
	/**
	 * Searches for the needed fetcher by name.
	 * 
	 * @param fetcherId
	 * @param fetcherNames
	 * @return fetcher's index
	 * @throws ExporterException in case fetcher is not found
	 */
	static int findFetcherById(String fetcherId, String[] fetcherNames) {
		String fetcherName = Labels.getLabel(fetcherId);
		for (int i = 0; i < fetcherNames.length; i++) {
			if (fetcherName.equals(fetcherNames[i])) {
				return i;
			}
		}
		throw new ExporterException("fetcher.notFound");
	}

	public void nextAdressResults(Object[] results) throws IOException {
		String address = results[ipFetcherIndex].toString(); 
		Object ports = results[portsFetcherIndex];
		
		if (ports != null && ports instanceof NumericRangeList) {
			for (PortIterator i = new PortIterator(ports.toString()); i.hasNext(); ) {
				output.println(address + DELIMETER + i.next());
			}
		}
	}
}
