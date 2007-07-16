/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 */
package net.azib.ipscan.fetchers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.azib.ipscan.config.GlobalConfig;
import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.net.PingResult;
import net.azib.ipscan.core.net.Pinger;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.core.values.IntegerWithUnit;

/**
 * PingFetcher is able to ping IP addresses.
 * It returns the average round trip time of all pings sent.
 * 
 * @author Anton Keks
 */
public class PingFetcher implements Fetcher {
	
	static final Logger LOG = LoggerFactory.getLogger();
	
	public static final String PARAMETER_PINGER = "pinger";
	
	private GlobalConfig config;

	/** The shared pinger - this one must be static, because PingTTLFetcher will use it as well */
	private static Pinger pinger;
	
	/** The registry used for creation of Pinger instances */
	private PingerRegistry pingerRegistry;
	
	public PingFetcher(PingerRegistry pingerRegistry, GlobalConfig globalConfig) {
		this.pingerRegistry = pingerRegistry;
		this.config = globalConfig;
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
				result = pinger.ping(subject.getAddress(), config.pingCount);
			}
			catch (IOException e) {
				// if this is not a timeout
				LOG.log(Level.WARNING, "Pinging failed", e);
				// return an empty ping result
				result = new PingResult(subject.getAddress());
			}
			// remember the result for other fetchers to use
			subject.setParameter(PARAMETER_PINGER, result);
		}
		return result;
	}

	public Object scan(ScanningSubject subject) {
		PingResult result = executePing(subject);
		subject.setResultType(result.isAlive() ? ResultType.ALIVE : ResultType.DEAD);
		
		if (!result.isAlive() && !config.scanDeadHosts) {
			// the host is dead, we are not going to continue...
			subject.abortAddressScanning();
		}
		
		return result.isAlive() ? new IntegerWithUnit(result.getAverageTime(), "fetcher.value.ms") : null;
	}

	public void init() {
		try {
			if (pinger == null) {
				pinger = pingerRegistry.createPinger(config.selectedPinger, config.pingTimeout);
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
