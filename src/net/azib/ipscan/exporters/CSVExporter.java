/**
 * 
 */
package net.azib.ipscan.exporters;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * CSV Exporter
 *
 * @author anton
 */
public class CSVExporter implements Exporter {

	/* CSV delimeter character */
	static final char DELIMETER = ',';
	/* Delimeter escaping character (if data contains DELIMETER) */
	static final char DELIMETER_ESCAPED = '.';
	/* Newline character */
	static final String NEWLINE = System.getProperty("line.separator");
	
	private Writer output;
	private boolean isAppend;

	/*
	 * @see net.azib.ipscan.exporters.Exporter#getLabel()
	 */
	public String getLabel() {
		return "exporter.csv";
	}

	/*
	 * @see net.azib.ipscan.exporters.Exporter#getFilenameExtension()
	 */
	public String getFilenameExtension() {
		return "csv";
	}

	/*
	 * @see net.azib.ipscan.exporters.Exporter#setAppend(boolean)
	 */
	public void setAppend(boolean append) {
		isAppend = append;
	}

	/*
	 * @see net.azib.ipscan.exporters.Exporter#start(java.io.OutputStream, String)
	 */
	public void start(OutputStream outputStream, String feederInfo) {
		output = new OutputStreamWriter(outputStream);
	}

	/*
	 * @see net.azib.ipscan.exporters.Exporter#end()
	 */
	public void end() throws IOException {
		output.flush();
	}
	
	/*
	 * @see net.azib.ipscan.exporters.Exporter#setFetchers(String[])
	 */
	public void setFetchers(String[] fetcherNames) throws IOException {
		if (!isAppend) {
			output.write(csvSafeString(fetcherNames[0]));
			for (int i = 1; i < fetcherNames.length; i++) {
				output.write(DELIMETER);
				output.write(csvSafeString(fetcherNames[i]));			
			}
			output.write(NEWLINE);
		}
	}

	/*
	 * @see net.azib.ipscan.exporters.Exporter#nextAdressResults(Object[])
	 */
	public void nextAdressResults(Object[] results) throws IOException {
		output.write(csvSafeString(results[0]));
		for (int i = 1; i < results.length; i++) {
			Object result = results[i];
			output.write(DELIMETER);
			output.write(csvSafeString(result));
		}
		output.write(NEWLINE);
	}

	/**
	 * @return a safe string to be outputted in CSV format (it doesn't contain the DELIMETER)
	 */
	String csvSafeString(Object o) {
		if (o == null)
			return "";
		return o.toString().replace(DELIMETER, DELIMETER_ESCAPED);
	}

	/*
	 * @see net.azib.ipscan.exporters.Exporter#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
