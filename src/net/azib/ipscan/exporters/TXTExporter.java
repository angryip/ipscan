/**
 * 
 */
package net.azib.ipscan.exporters;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.Date;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Version;

/**
 * TXT Exporter
 *
 * @author anton
 */
public class TXTExporter implements Exporter {
	
	/** Newline character */
	static final String NEWLINE = System.getProperty("line.separator");
	
	private Writer output;
	private boolean isAppend;
	int[] padLengths;

	/**
	 * @see net.azib.ipscan.exporters.Exporter#getLabel()
	 */
	public String getLabel() {
		return "exporter.txt";
	}

	/**
	 * @see net.azib.ipscan.exporters.Exporter#getFilenameExtension()
	 */
	public String getFilenameExtension() {
		return "txt";
	}
	
	/**
	 * @see net.azib.ipscan.exporters.Exporter#setAppend(boolean)
	 */
	public void setAppend(boolean append) {
		isAppend = append;
	}

	/**
	 * @see net.azib.ipscan.exporters.Exporter#start(java.io.OutputStream, String)
	 */
	public void start(OutputStream outputStream, String feederInfo) throws IOException {
		output = new OutputStreamWriter(outputStream);
		if (!isAppend) {
			output.write(Labels.getInstance().getString("exporter.txt.generated"));
			println(Version.FULL_NAME);
			println(Version.WEBSITE);
			output.write(NEWLINE);
			
			String scanned = Labels.getInstance().getString("exporter.txt.scanned");
			scanned = scanned.replaceFirst("%INFO", feederInfo);  
			println(scanned);
			println(DateFormat.getDateTimeInstance().format(new Date()));
			output.write(NEWLINE);
		}
	}

	/**
	 * @see net.azib.ipscan.exporters.Exporter#end()
	 */
	public void end() throws IOException {
		output.flush();
	}
	
	/**
	 * @throws IOException 
	 * @see net.azib.ipscan.exporters.Exporter#setFetchers(String[])
	 */
	public void setFetchers(String[] fetcherNames) throws IOException {
		padLengths = new int[fetcherNames.length];
		for (int i = 0; i < fetcherNames.length; i++) {
			padLengths[i] = fetcherNames[i].length() * 3;
			if (!isAppend) {
				output.write(pad(fetcherNames[i], padLengths[i]));
			}
		}
		if (!isAppend) {
			output.write(NEWLINE);
		}
	}

	/**
	 * @see net.azib.ipscan.exporters.Exporter#nextAdressResults(InetAddress, Object[])
	 */
	public void nextAdressResults(Object[] results) throws IOException {
		output.write(pad(results[0], padLengths[0]));
		for (int i = 1; i < results.length; i++) {
			Object result = results[i];
			output.write(pad(result, padLengths[i]));			
		}
		output.write(NEWLINE);
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
		return s + "                                                                       ".
			substring(0, length - s.length());
	}
	
	void println(String s) throws IOException {
		output.write(s);
		output.write(NEWLINE);
	}

	/**
	 * @see net.azib.ipscan.exporters.Exporter#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
