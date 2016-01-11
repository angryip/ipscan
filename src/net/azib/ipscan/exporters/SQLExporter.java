/**
 * 
 */
package net.azib.ipscan.exporters;

import java.io.IOException;

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
	
	public void nextAddressResults(Object[] results) throws IOException {
	
	}
}
