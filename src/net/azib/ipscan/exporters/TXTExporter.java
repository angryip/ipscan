package net.azib.ipscan.exporters;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.fetchers.IPFetcher;
import net.azib.ipscan.fetchers.PingFetcher;
import net.azib.ipscan.fetchers.PortsFetcher;
import net.azib.ipscan.gui.feeders.AbstractFeederGUI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static net.azib.ipscan.core.ScanningResult.ResultType.*;
import static net.azib.ipscan.util.IOUtils.closeQuietly;
import static net.azib.ipscan.util.InetAddressUtils.increment;

/**
 * TXT Exporter
 *
 * @author Anton Keks
 */
public class TXTExporter extends AbstractExporter {
	int[] padLengths;

	public TXTExporter() {}

	public String getId() {
		return "exporter.txt";
	}

	public String getFilenameExtension() {
		return "txt";
	}
	
	public void start(OutputStream outputStream, String feederInfo) throws IOException {
		super.start(outputStream, feederInfo);

		if (!append) {
			output.print(Labels.getLabel("exporter.txt.generated"));
			output.print(' ');
			output.println(Version.getFullName());
			output.println(Version.WEBSITE);
			output.println();

			output.print(Labels.getLabel("exporter.txt.scanned"));
			output.print(' ');
			output.println(feederInfo);
			output.println(DateFormat.getDateTimeInstance().format(new Date()));
			output.println();
		}
	}

	public void setFetchers(String[] fetcherNames) throws IOException {
		padLengths = new int[fetcherNames.length];
		for (int i = 0; i < fetcherNames.length; i++) {
			padLengths[i] = fetcherNames[i].length() * 3;
			if (!append) {
				output.write(pad(fetcherNames[i], padLengths[i]));
			}
		}
		if (!append) {
			output.println();
		}
	}

	public void nextAddressResults(Object[] results) throws IOException {
		output.write(pad(results[0], padLengths[0]));
		for (int i = 1; i < results.length; i++) {
			Object result = results[i];
			output.write(pad(result, padLengths[i]));			
		}
		output.println();
	}
	
	/**
	 * Pads the passed string with spaces.
	 * @param length the total returned length, minimum is 13
	 */
	String pad(Object o, int length) {
		int minLength = 16;
		if (length < minLength)
			length = minLength;
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

	public List<ScanningResult> importResults(String fileName, AbstractFeederGUI feeder) throws IOException {
		List<ScanningResult> results = new ArrayList<>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));

			String startIP = null;
			String endIP = null;
			String lastLoadedIP = null;
			List<String> columns = emptyList();

			int ipIndex = 0, pingIndex = 1, portsIndex = 3;

			String[] lookingFor = {
					Labels.getLabel("exporter.txt.scanned"),
					Labels.getLabel(IPFetcher.ID)
			};
			int lookingForIndex = 0;

			String line;
			while ((line = reader.readLine()) != null) {
				String[] sp;

				if (lookingForIndex < lookingFor.length) {
					sp = line.split("\\s");

					if (lookingFor[lookingForIndex].equals(sp[0])) {
						if (lookingForIndex == 0) {
							startIP = sp[1];
							endIP = sp[3];
							lookingForIndex++;
						}
						else if (lookingForIndex == 1) {
							sp = line.split("\\s{2,}");
							columns = asList(sp);
							pingIndex = columns.indexOf(Labels.getLabel(PingFetcher.ID));
							portsIndex = columns.indexOf(Labels.getLabel(PortsFetcher.ID));
							lookingForIndex++;
						}
					}
					continue;
				}

				sp = line.split("\\s{2,}");
				if (sp.length < columns.size()) continue;

				InetAddress addr = InetAddress.getByName(sp[ipIndex]);
				lastLoadedIP = sp[ipIndex];

				ScanningResult r = new ScanningResult(addr, sp.length);
				if (portsIndex > 0 && sp[portsIndex].matches("\\d.*")) r.setType(WITH_PORTS);
				else if (pingIndex > 0 && sp[pingIndex].matches("\\d.*")) r.setType(ALIVE);
				else r.setType(DEAD);

				r.setValues(sp);
				results.add(r);
			}

			if (lastLoadedIP != null && !lastLoadedIP.equals(endIP)) {
				InetAddress nextStartIP = increment(InetAddress.getByName(lastLoadedIP));
				startIP = nextStartIP.getHostAddress();
			}

			feeder.unserialize(startIP, endIP);
			return results;
		}
		finally {
			closeQuietly(reader);
		}
	}
}
