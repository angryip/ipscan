/**
 * 
 */
package net.azib.ipscan.exporters;

import java.io.IOException;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.PortIterator;

/**
 * IP List Exporter
 * <p/>
 * Exports only IP:port info, outputting each distinct IP:port pair on separate line.
 *
 * @author Anton Keks
 */
public class IPListExporter extends AbstractExporter {

	/* CSV delimiter character */
	static final char DELIMETER = ':';
	
	private int ipFetcherIndex;
	private int portsFetcherIndex;

	public String getId() {
		return "exporter.ipList";
	}

	public String getFilenameExtension() {
		return "lst";
	}
	
	public void setFetchers(String[] fetcherNames) throws IOException {
		ipFetcherIndex = findFetcherByLabel("fetcher.ip", fetcherNames);
		portsFetcherIndex = findFetcherByLabel("fetcher.ports", fetcherNames);
	}
	
	/**
	 * Searches for the needed fetcher by name.
	 * 
	 * @param label
	 * @param fetcherNames
	 * @return fetcher's index
	 * @throws ExporterException in case fetcher is not found
	 */
	static int findFetcherByLabel(String label, String[] fetcherNames) {
		String fetcherName = Labels.getLabel(label);
		for (int i = 0; i < fetcherNames.length; i++) {
			if (fetcherName.equals(fetcherNames[i])) {
				return i;
			}
		}
		throw new ExporterException("fetcher.notFound");
	}

	public void nextAdressResults(Object[] results) throws IOException {
		String address = results[ipFetcherIndex].toString(); 
		String portList;
		try {
			portList = results[portsFetcherIndex].toString();
		}
		catch (Exception e) {
			// ignore empty results
			return;
		}
		
		if (portList != null) {
			for (PortIterator i = new PortIterator(portList); i.hasNext(); ) {
				output.println(address + DELIMETER + i.next());
			}
		}
	}
}
