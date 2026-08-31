package net.azib.ipscan.exporters;

import java.io.IOException;

/**
 * CSV Exporter
 *
 * @author Anton Keks
 */
public class CSVExporter extends AbstractExporter {

	/* CSV delimiter character */
	static final char DELIMETER = ',';

	public CSVExporter() {}

	public String getId() {
		return "exporter.csv";
	}

	public String getFilenameExtension() {
		return "csv";
	}
	
	public void setFetchers(String[] fetcherNames) throws IOException {
		if (!append) {
			output.write(csvSafeString(fetcherNames[0]));
			for (var i = 1; i < fetcherNames.length; i++) {
				output.write(DELIMETER);
				output.write(csvSafeString(fetcherNames[i]));			
			}
			output.println();
		}
	}

	public void nextAddressResults(Object[] results) throws IOException {
		output.write(csvSafeString(results[0]));
		for (var i = 1; i < results.length; i++) {
			var result = results[i];
			output.write(DELIMETER);
			output.write(csvSafeString(result));
		}
		output.println();
	}

	/**
	 * @return a safe string to be output in CSV format, using standard RFC 4180 quoting
	 */
	String csvSafeString(Object o) {
		if (o == null)
			return "";
		var s = o.toString();
		if (s.indexOf(DELIMETER) >= 0 || s.indexOf('"') >= 0 || s.indexOf('\n') >= 0 || s.indexOf('\r') >= 0) {
			return "\"" + s.replace("\"", "\"\"") + "\"";
		}
		return s;
	}
}
