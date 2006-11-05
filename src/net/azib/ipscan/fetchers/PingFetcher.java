/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 */
package net.azib.ipscan.fetchers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.Pinger;

/**
 * PingFetcher is able to ping IP addresses.
 * It returns the average round trip time of all pings sent.
 * 
 * @author anton
 */
public class PingFetcher implements Fetcher {

	public static final String PARAMETER_PINGER = "pinger";
	
	private int timeout = Config.getGlobal().pingTimeout; 
	private int count = Config.getGlobal().pingCount; 
	
	public PingFetcher() {
	}

	public String getLabel() {
		return "fetcher.ping";
	}
	
	protected Pinger executePing(ScanningSubject subject) {
		
		// TODO: share a single Pinger (and therefore, a single RAW socket)
		// because all the received packets are copied to all the open raw sockets
		// which makes it very ineffective
		
		Pinger pinger = null;
		if (subject.hasParameter(PARAMETER_PINGER)) {
			pinger = (Pinger) subject.getParameter(PARAMETER_PINGER);
		}
		else {
			try {
				pinger = new Pinger(subject.getIPAddress(), timeout);
				pinger.ping(count); 
				pinger.close();
			}
			catch (IOException e) {
				// if this is not a timeout
				Logger.global.log(Level.WARNING, "Pinging failed", e);
				try {
					if (pinger != null)
						pinger.close();
				}
				catch (IOException e2) {}
				pinger = null;
			}
			// remember the ready-made pinger (or null) for other fetchers to use
			subject.setParameter(PARAMETER_PINGER, pinger);
		}
		return pinger;
	}

	public String scan(ScanningSubject subject) {
		Pinger pinger = executePing(subject);
		boolean isAlive = pinger != null && !pinger.isTimeout();
		subject.setResultType(isAlive ? ScanningSubject.RESULT_TYPE_ALIVE : ScanningSubject.RESULT_TYPE_DEAD);
		
		if (!isAlive && !Config.getGlobal().scanDeadHosts) {
			// the host is dead, we are not going to continue...
			subject.abortScanning();
		}
		
		return isAlive ? Integer.toString(pinger.getAverageTime()) + Labels.getInstance().getString("fetcher.value.ms") : null;
	}

}
