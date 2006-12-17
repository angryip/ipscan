/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 */
package net.azib.ipscan.fetchers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.core.IntegerWithUnit;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.PingResult;
import net.azib.ipscan.core.net.Pinger;
import net.azib.ipscan.core.net.PingerRegistry;

/**
 * PingFetcher is able to ping IP addresses.
 * It returns the average round trip time of all pings sent.
 * 
 * @author anton
 */
public class PingFetcher implements Fetcher {
	
	public static final String PARAMETER_PINGER = "pinger";

	/** The shared pinger - this one must be static, because PingTTLFetcher will use it as well */
	private static Pinger pinger;
	
	/** The registry used for creation of Pinger instances */
	private PingerRegistry pingerRegistry;
	
	public PingFetcher(PingerRegistry pingerRegistry) {
		this.pingerRegistry = pingerRegistry;
	}

	public String getLabel() {
		return "fetcher.ping";
	}
	
	protected PingResult executePing(ScanningSubject subject) {
		
		PingResult result = null;
		
		if (subject.hasParameter(PARAMETER_PINGER)) {
			result = (PingResult) subject.getParameter(PARAMETER_PINGER);
		}
		else {
			try {
				result = pinger.ping(subject.getIPAddress(), Config.getGlobal().pingCount);
			}
			catch (IOException e) {
				// if this is not a timeout
				Logger.global.log(Level.WARNING, "Pinging failed", e);
				// return an empty ping result
				result = new PingResult(subject.getIPAddress());
			}
			// remember the result for other fetchers to use
			subject.setParameter(PARAMETER_PINGER, result);
		}
		return result;
	}

	public Object scan(ScanningSubject subject) {
		PingResult result = executePing(subject);
		subject.setResultType(result.isAlive() ? ScanningSubject.RESULT_TYPE_ALIVE : ScanningSubject.RESULT_TYPE_DEAD);
		
		if (!result.isAlive() && !Config.getGlobal().scanDeadHosts) {
			// the host is dead, we are not going to continue...
			subject.abortScanning();
		}
		
		return result.isAlive() ? new IntegerWithUnit(result.getAverageTime(), "fetcher.value.ms") : null;
	}

	public void init() {
		try {
			if (pinger == null) {
				pinger = pingerRegistry.createPinger(Config.getGlobal().selectedPinger, Config.getGlobal().pingTimeout);
			}
		}
		catch (Exception e) {
			throw new FetcherException(e);
		}
	}

	public void cleanup() {
		try {
			if (pinger != null) {
				pinger.close();
			}
		}
		catch (IOException e) {
			throw new FetcherException(e);
		}
		pinger = null;
	}


}
