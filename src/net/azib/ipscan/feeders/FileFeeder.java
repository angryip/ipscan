/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.feeders;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.util.InetAddressUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.logging.Level.WARNING;
import static net.azib.ipscan.util.InetAddressUtils.HOSTNAME_REGEX;

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
	private Stream<NetworkInterface> networkInterfaces;
	
	private int currentIndex;

	public String getId() {
		return "feeder.file";
	}
	
	public FileFeeder() {
		try {
			networkInterfaces = NetworkInterface.networkInterfaces();
		}
		catch (SocketException e) {
			LOG.log(WARNING, "", e);
		}
	}
	
	public FileFeeder(String fileName) {
		this();
		try {
			findHosts(new FileReader(fileName));
		}
		catch (FileNotFoundException e) {
			throw new FeederException("file.notExists");
		}
	}

	public FileFeeder(Reader reader) {
		this();
		findHosts(reader);
	}

	private String readMultiLine(BufferedReader fileReader) throws IOException {
		int index = 1;
		StringBuilder sb = new StringBuilder();
		String fileLine;
		while ((fileLine = fileReader.readLine()) != null) {
			sb.append(fileLine);
			index++;

			if (index > 10) {
				break;
			}
		}

		return sb.toString();
	}

	private void findHosts(Reader reader) {
		currentIndex = 0;
		foundHosts = new LinkedHashMap<>();
		long startTime = System.currentTimeMillis();

		System.out.println("Start to deal with the file, time used：" + startTime);
		try (BufferedReader fileReader = new BufferedReader(reader)) {
			String fileLine;
			while (!(fileLine = readMultiLine(fileReader)).equals("")) {
				long lineTime = System.currentTimeMillis();
				Matcher matcher = HOSTNAME_REGEX.matcher(fileLine);
				while (matcher.find()) {
					try {
						String host = matcher.group();
						if (host.equals(Version.OWN_HOST)) continue;
						ScanningSubject subject = foundHosts.get(host);
						if (subject == null) {
							InetAddress address = InetAddress.getByName(host);
							subject = new ScanningSubject(address, InetAddressUtils.getInterface(address, networkInterfaces));
						}
						
						if (!matcher.hitEnd() && fileLine.charAt(matcher.end()) == ':') {
							// see if any valid port is requested
							Matcher portMatcher = PORT_REGEX.matcher(fileLine.substring(matcher.end()+1));
							if (portMatcher.lookingAt()) {
								subject.addRequestedPort(Integer.valueOf(portMatcher.group()));
							}
						}
						
						foundHosts.put(host, subject);
					}
					catch (UnknownHostException e) {
						LOG.log(WARNING, e.toString());
					}
				}
				System.out.println("The time used for one line" + (System.currentTimeMillis() - lineTime) + "ms");
			}
			System.out.println("fini, total time used" + (System.currentTimeMillis() - startTime)+ "ms");
			if (foundHosts.isEmpty()) {
				throw new FeederException("file.nothingFound");
			}
		}
		catch (IOException e) {
			throw new FeederException("file.errorWhileReading");
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
