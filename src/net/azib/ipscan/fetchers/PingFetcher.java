/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.PingResult;
import net.azib.ipscan.core.net.Pinger;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.core.values.IntegerWithUnit;
import net.azib.ipscan.gui.fetchers.PingFetcherPrefs;

import javax.inject.Inject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.azib.ipscan.core.ScanningSubject.PARAMETER_PING_RESULT;

/**
 * PingFetcher is able to ping IP addresses.
 * It returns the average round trip time of all pings sent.
 * 
 * @author Anton Keks
 */
public class PingFetcher extends AbstractFetcher {
	public static final String ID = "fetcher.ping";

	private static final Logger LOG = LoggerFactory.getLogger();
	
	private ScannerConfig config;

	/** The shared pinger - this one must be static, because PingTTLFetcher will use it as well */
	private static Pinger pinger;
	
	/** The registry used for creation of Pinger instances */
	private PingerRegistry pingerRegistry;
	
	@Inject public PingFetcher(PingerRegistry pingerRegistry, ScannerConfig scannerConfig) {
		this.pingerRegistry = pingerRegistry;
		this.config = scannerConfig;
	}

	public String getId() {
		return ID;
	}
	
	@Override
	public Class<? extends FetcherPrefs> getPreferencesClass() {
		return PingFetcherPrefs.class;
	}

	protected PingResult executePing(ScanningSubject subject) {
		if (subject.hasParameter(PARAMETER_PING_RESULT))
			return (PingResult) subject.getParameter(PARAMETER_PING_RESULT);

    PingResult result;
    try {
      result = pinger.ping(subject, config.pingCount);
    }
    catch (IOException e) {
      // if this is not a timeout
      LOG.log(Level.WARNING, "Pinging failed", e);
      // return an empty ping result
      result = new PingResult(subject.getAddress());
    }
    // remember the result for other fetchers to use
    subject.setParameter(PARAMETER_PING_RESULT, result);
		return result;
	}

	public Object scan(ScanningSubject subject) {
		PingResult result = executePing(subject);
		subject.setResultType(result.isAlive() ? ResultType.ALIVE : ResultType.DEAD);
		
		if (!result.isAlive() && !config.scanDeadHosts) {
			// the host is dead, we are not going to continue...
			subject.abortAddressScanning();
		}
		
		return result.isAlive() ? new IntegerWithUnit(result.getAverageTime(), "ms") : null;
	}

	public void init() {
		if (pinger == null) {
			pinger = pingerRegistry.createPinger();
		}
	}

	public void cleanup() {
		try {
			if (pinger != null) pinger.close();
		}
		catch (IOException e) {
			throw new FetcherException(e);
		}
		pinger = null;
	}
}
