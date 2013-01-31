/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.gui.fetchers.PortTextFetcherPrefs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PortTextFetcher - generic configurable fetcher to read some particular information from a port.
 *
 * @author Anton Keks
 */
public abstract class PortTextFetcher extends AbstractFetcher {
	private static final Logger LOG = LoggerFactory.getLogger();
	
	private ScannerConfig scannerConfig;

	private int defaultPort;
	protected String textToSend;
	protected Pattern matchingRegexp;
	protected int extractGroup;
	
	public PortTextFetcher(ScannerConfig scannerConfig, int defaultPort, String textToSend, String matchingRegexp) {
		this.scannerConfig = scannerConfig;
		this.defaultPort = defaultPort;
		this.textToSend = getPreferences().get("textToSend", textToSend);
		this.matchingRegexp = Pattern.compile(getPreferences().get("matchingRegexp", matchingRegexp));
		this.extractGroup = getPreferences().getInt("extractGroup", 1);
	}

	public Object scan(ScanningSubject subject) {
		Iterator<Integer> portIterator = subject.isAnyPortRequested() ? subject.requestedPortsIterator() : Collections.singleton(defaultPort).iterator();

		while (portIterator.hasNext() && !Thread.currentThread().isInterrupted()) {
			Socket socket = new Socket();
			try {
				socket.connect(new InetSocketAddress(subject.getAddress(), portIterator.next()), subject.getAdaptedPortTimeout());
				socket.setTcpNoDelay(true);
				socket.setSoTimeout(scannerConfig.portTimeout*2);
				socket.setSoLinger(true, 0);
				
				socket.getOutputStream().write(textToSend.getBytes());
				
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String line;
				while ((line = in.readLine()) != null) {
					Matcher matcher = matchingRegexp.matcher(line);
					if (matcher.find()) {
						// mark that additional info is available
						subject.setResultType(ResultType.WITH_PORTS);
						// return the required contents
						return matcher.group(extractGroup);
					}
				}
			}
			catch (ConnectException e) {
				// no connection
			}
			catch (SocketTimeoutException e) {
				// no information
			}
			catch (SocketException e) {
				// connection reset
			}
			catch (IOException e) {
				LOG.log(Level.FINE, subject.getAddress().toString(), e);
			}
			finally {
				try {
					socket.close();
				}
				catch (IOException e) {}
			}
		}
		return null;
	}
	
	@Override
	public Class<? extends FetcherPrefs> getPreferencesClass() {
		return PortTextFetcherPrefs.class;
	}
	
	public String getTextToSend() {
		return textToSend;
	}

	public void setTextToSend(String textToSend) {
		this.textToSend = textToSend;
	}

	public Pattern getMatchingRegexp() {
		return matchingRegexp;
	}

	public void setMatchingRegexp(Pattern matchingRegexp) {
		this.matchingRegexp = matchingRegexp;
	}

	public int getExtractGroup() {
		return extractGroup;
	}

	public void setExtractGroup(int extractGroup) {
		this.extractGroup = extractGroup;
	}
}
