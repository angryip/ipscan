/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.feeders;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.util.InetAddressUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.logging.Level.WARNING;
import static net.azib.ipscan.util.IOUtils.closeQuietly;

/**
 * Feeder, taking IP addresses from text files in any format.
 * It uses regular expressions for matching of IP addresses.
 *
 * @author Anton Keks
 */
public class FileFeeder extends AbstractFeeder {
	
	private static final Pattern PORT_REGEX = Pattern.compile("\\d{1,5}\\b");
	
	static final Logger LOG = LoggerFactory.getLogger();
	
	/** Found IP address Strings are put here */
	private Map<String, ScanningSubject> foundHosts;
	private Iterator<ScanningSubject> foundIPAddressesIterator;
	
	private int currentIndex;

	public String getId() {
		return "feeder.file";
	}
	
	public FileFeeder() {
	}
	
	public FileFeeder(String fileName) {
		try {
			readAddresses(new FileReader(fileName));
		}
		catch (FileNotFoundException e) {
			throw new FeederException("file.notExists");
		}
	}
	
	public FileFeeder(Reader reader) {
		readAddresses(reader);
	}
	
	private void readAddresses(Reader reader) {
		BufferedReader fileReader = new BufferedReader(reader);
		
		currentIndex = 0;
		foundHosts = new LinkedHashMap<String, ScanningSubject>();
		try {
			String fileLine;
			while ((fileLine = fileReader.readLine()) != null) {
				Matcher matcher = InetAddressUtils.HOSTNAME_REGEX.matcher(fileLine);
				while (matcher.find()) {
					try {
						String address = matcher.group();						
						ScanningSubject subject = foundHosts.get(address);
						if (subject == null)
							subject = new ScanningSubject(InetAddress.getByName(address));
						
						if (!matcher.hitEnd() && fileLine.charAt(matcher.end()) == ':') {
							// see if any valid port is requested
							Matcher portMatcher = PORT_REGEX.matcher(fileLine.substring(matcher.end()+1));
							if (portMatcher.lookingAt()) {
								subject.addRequestedPort(Integer.valueOf(portMatcher.group()));
							}
						}
						
						foundHosts.put(address, subject);
					}
					catch (UnknownHostException e) {
						LOG.log(WARNING, e.toString());
					}
				}
			}
			if (foundHosts.isEmpty()) {
				throw new FeederException("file.nothingFound");
			}
		}
		catch (IOException e) {
			throw new FeederException("file.errorWhileReading");
		}
		finally {
			closeQuietly(fileReader);
		}
		
		foundIPAddressesIterator = foundHosts.values().iterator();
	}
	
	public int percentageComplete() {
		return Math.round((float) currentIndex * 100 / foundHosts.size());
	}

	public boolean hasNext() {
		return foundIPAddressesIterator.hasNext();
	}

	public ScanningSubject next() {
		currentIndex++;
		return foundIPAddressesIterator.next();
	}

	public String getInfo() {
		// let's return the number of found addresses
		return Integer.toString(foundHosts.size());
	}
	
}
