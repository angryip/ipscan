/**
 * 
 */
package net.azib.ipscan.exporters;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Version;

/**
 * TXT Exporter
 *
 * @author Anton Keks
 */
public class TXTExporter extends AbstractExporter {
	
	int[] padLengths;

	public String getId() {
		return "exporter.txt";
	}

	public String getFilenameExtension() {
		return "txt";
	}
	
	public void start(OutputStream outputStream, String feederInfo) throws IOException {
		super.start(outputStream, feederInfo);

		if (!isAppend) {
			output.write(Labels.getLabel("exporter.txt.generated"));
			output.println(Version.getFullName());
			output.println(Version.WEBSITE);
			output.println();
			
			String scanned = Labels.getLabel("exporter.txt.scanned");
			scanned = scanned.replaceFirst("%INFO", feederInfo);  
			output.println(scanned);
			output.println(DateFormat.getDateTimeInstance().format(new Date()));
			output.println();
		}
	}

	public void setFetchers(String[] fetcherNames) throws IOException {
		padLengths = new int[fetcherNames.length];
		for (int i = 0; i < fetcherNames.length; i++) {
			padLengths[i] = fetcherNames[i].length() * 3;
			if (!isAppend) {
				output.write(pad(fetcherNames[i], padLengths[i]));
			}
		}
		if (!isAppend) {
			output.println();
		}
	}

	public void nextAdressResults(Object[] results) throws IOException {
		output.write(pad(results[0], padLengths[0]));
		for (int i = 1; i < results.length; i++) {
			Object result = results[i];
			output.write(pad(result, padLengths[i]));			
		}
		output.println();
	}
	
	/**
	 * Pads the passed string with spaces.
	 * @param s
	 * @param length the total returned length, minimum is 13
	 */
	String pad(Object o, int length) {
		if (length < 16)
			length = 16;
		
		String s;
		if (o == null) 
			s = "";
		else
			s = o.toString();
		
		if (s.length() >= length) {
			return s;
		}
		return s + "                                                                       "
				   .substring(0, length - s.length());
	}
}
