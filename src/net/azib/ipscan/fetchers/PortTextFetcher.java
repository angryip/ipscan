/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.fetchers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.ScanningResult.ResultType;

/**
 * PortTextFetcher - generic configurable fetcher to read some particular information from a port.
 *
 * @author Anton Keks
 */
public abstract class PortTextFetcher extends AbstractFetcher {
	private static final Logger LOG = LoggerFactory.getLogger();
	
	private ScannerConfig scannerConfig;

	private int port;
	private String textToSend;
	private Pattern matchingRegexp;
	
	public PortTextFetcher(ScannerConfig scannerConfig, int port, String textToSend, String matchingRegexp) {
		this.scannerConfig = scannerConfig;
		this.port = port;
		this.textToSend = textToSend;
		this.matchingRegexp = Pattern.compile(matchingRegexp);
	}

	public Object scan(ScanningSubject subject) {
		Socket socket = new Socket();
		try {
			// TODO: support multiple ports and check them sequentially
			// TODO: use adapted port timeout if it is configured to do so			
			socket.connect(new InetSocketAddress(subject.getAddress(), port), subject.getAdaptedPortTimeout());
			socket.setTcpNoDelay(true);
			socket.setSoTimeout(scannerConfig.portTimeout);
			socket.setSoLinger(true, 0);
			
			socket.getOutputStream().write(textToSend.getBytes());
			
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				Matcher matcher = matchingRegexp.matcher(line);
				if (matcher.matches()) {
					// mark that additional info is available
					subject.setResultType(ResultType.WITH_PORTS);
					// return the required contents
					return matcher.group(1);
				}
			}
		}
		catch (ConnectException e) {
			// no connection
		}
		catch (SocketTimeoutException e) {
			// no information
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
		return null;
	}

}
