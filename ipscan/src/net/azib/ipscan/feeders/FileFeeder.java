/**
 * 
 */
package net.azib.ipscan.feeders;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import net.azib.ipscan.core.InetAddressUtils;

/**
 * Feeder, taking IP addresses from text files in any format.
 * It uses regular expressions for matching of IP addresses.
 * TODO: tests!!! 
 *
 * @author anton
 */
public class FileFeeder implements Feeder {
	
	private static final Logger LOG = Logger.getLogger(FileFeeder.class.getName());
	
	/** Found IP address Strings are put here */
	private List foundIPAddresses;
	private Iterator foundIPAddressesIterator;
	
	/** Total number of found IP addresses. Equivalent to foundIPAddresses.size(), 
	 *  which is very ineffective in case of a LinkedList.
	 */ 
	private int totalAddresses;
	private int currentIndex;

	/**
	 * @see Feeder#getLabel()
	 */
	public String getLabel() {
		return "feeder.file";
	}
	
	/**
	 * Initializes the FileFeeder with required parameters
	 * @see Feeder#initialize(String[])
	 * @param params 1 parameter:
	 * 		params[0] fileName
	 */
	public int initialize(String[] params) {
		initialize(params[0]);
		return 1;
	}

	public void initialize(String fileName) {
		try {
			initialize(new FileReader(fileName));
		}
		catch (FileNotFoundException e) {
			throw new FeederException("file.notExists");
		}
	}
	
	void initialize(Reader reader) {
		BufferedReader fileReader = new BufferedReader(reader);
		
		totalAddresses = 0;
		currentIndex = 0;
		foundIPAddresses = new LinkedList();
		try {
			String fileLine;
			while ((fileLine = fileReader.readLine()) != null) {
				Matcher matcher = InetAddressUtils.IP_ADDRESS_REGEX.matcher(fileLine);
				while (matcher.find()) {
					foundIPAddresses.add(matcher.group());
					totalAddresses++;
				}
			}
			if (totalAddresses == 0) {
				throw new FeederException("file.nothingFound");
			}
		}
		catch (IOException e) {
			throw new FeederException("file.errorWhileReading");
		}
		finally {
			try {
				fileReader.close();
			}
			catch (IOException e) {
				// ignore, what else to do?
			}
		}
		
		foundIPAddressesIterator = foundIPAddresses.iterator();
	}
	
	public int getPercentageComplete() {
		return Math.round((float)currentIndex * 100 / totalAddresses);
	}

	public boolean hasNext() {
		return foundIPAddressesIterator.hasNext();
	}

	public InetAddress next() {
		try {
			currentIndex++;
			return InetAddress.getByName((String) foundIPAddressesIterator.next());
		}
		catch (UnknownHostException e) {
			LOG.log(Level.WARNING, "malformedIP", e);
			throw new FeederException("malformedIP");
		}
	}

	/**
	 * @see net.azib.ipscan.feeders.Feeder#getInfo()
	 */
	public String getInfo() {
		// let's return the number of found addresses
		return Integer.toString(totalAddresses);
	}
	
}
