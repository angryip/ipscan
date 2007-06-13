/**
 * 
 */
package net.azib.ipscan.exporters;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.PortIterator;

/**
 * IP List Exporter
 * <p/>
 * Exports only IP:port info, outputting each distinct IP:port pair on separate line.
 *
 * @author anton
 */
public class IPListExporter implements Exporter {

	/* CSV delimeter character */
	static final char DELIMETER = ':';
	
	private int ipFetcherIndex;
	private int portsFetcherIndex;
	private PrintWriter output;

	/*
	 * @see net.azib.ipscan.exporters.Exporter#getLabel()
	 */
	public String getLabel() {
		return "exporter.ipList";
	}

	/*
	 * @see net.azib.ipscan.exporters.Exporter#getFilenameExtension()
	 */
	public String getFilenameExtension() {
		return "lst";
	}

	/*
	 * @see net.azib.ipscan.exporters.Exporter#setAppend(boolean)
	 */
	public void setAppend(boolean append) {
		// no difference in this fetcher
	}

	/*
	 * @see net.azib.ipscan.exporters.Exporter#start(java.io.OutputStream, String)
	 */
	public void start(OutputStream outputStream, String feederInfo) {
		output = new PrintWriter(new OutputStreamWriter(outputStream));
	}

	/*
	 * @see net.azib.ipscan.exporters.Exporter#end()
	 */
	public void end() throws IOException {
		if (output.checkError()) {
			throw new IOException();
		}
	}
	
	/*
	 * @see net.azib.ipscan.exporters.Exporter#setFetchers(String[])
	 */
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

	/*
	 * @see net.azib.ipscan.exporters.Exporter#nextAdressResults(InetAddress, Object[])
	 */
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

	/*
	 * @see net.azib.ipscan.exporters.Exporter#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
