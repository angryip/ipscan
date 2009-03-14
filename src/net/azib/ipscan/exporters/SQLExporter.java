/**
 * 
 */
package net.azib.ipscan.exporters;

import java.io.IOException;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.PortIterator;
import net.azib.ipscan.core.values.NumericRangeList;
import net.azib.ipscan.fetchers.IPFetcher;
import net.azib.ipscan.fetchers.PortsFetcher;

/**
 * SQL Exporter
 * <p/>
 * Exports results as an SQL inserts, suitable for sqlite, mysql, etc,
 * optionally preceding by a 'create table'.
 * TODO: implement SQLExporter
 *
 * @author Anton Keks
 */
public class SQLExporter extends AbstractExporter {

	static final char DELIMETER = ':';
	
	public String getId() {
		return "exporter.sql";
	}

	public String getFilenameExtension() {
		return "sql";
	}
	
	public void setFetchers(String[] fetcherNames) throws IOException {
	}
	
	public void nextAdressResults(Object[] results) throws IOException {
	
	}
}
