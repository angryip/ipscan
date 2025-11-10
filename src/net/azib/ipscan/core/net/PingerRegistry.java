/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.di.InjectException;
import net.azib.ipscan.di.Injector;
import net.azib.ipscan.fetchers.FetcherException;
import net.azib.ipscan.fetchers.MACFetcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

/**
 * PingerRegistry
 *
 * @author Anton Keks
 */
public class PingerRegistry {
	private static final Logger LOG = LoggerFactory.getLogger();
	
	private ScannerConfig scannerConfig;
	private Injector injector;
	
	/** All available Pinger implementations */
	Map<String, Class<? extends Pinger>> pingers;

	@SuppressWarnings("unchecked")
	public PingerRegistry(ScannerConfig scannerConfig, Injector injector) throws ClassNotFoundException {
		this.scannerConfig = scannerConfig;
		this.injector = injector;

		pingers = new LinkedHashMap<>();
		if (Platform.WINDOWS)
			pingers.put("pinger.windows", (Class<Pinger>) Class.forName(getClass().getPackage().getName() + ".WindowsPinger"));
		pingers.put(UDPPinger.ID, UDPPinger.class);
		pingers.put(TCPPinger.ID, TCPPinger.class);
		pingers.put(CombinedUnprivilegedPinger.ID, CombinedUnprivilegedPinger.class);
		pingers.put(JavaPinger.ID, JavaPinger.class);
		pingers.put(ARPPinger.ID, ARPPinger.class);

		// Add already registered plugin pingers
		injector.requireAll(Pinger.class).forEach(p -> pingers.put(p.getId(), p.getClass()));
	}

	public String[] getRegisteredNames() {
		return pingers.keySet().toArray(new String[pingers.size()]);
	}
	
	/**
	 * Creates the configured pinger with configured timeout
	 */
	public Pinger createPinger(boolean isLAN) throws FetcherException {
		Class<? extends Pinger> pingerClass = pingers.get(scannerConfig.selectedPinger);
		if (pingerClass == null) {
			Map.Entry<String, Class<? extends Pinger>> first = pingers.entrySet().iterator().next();
			scannerConfig.selectedPinger = first.getKey();
			pingerClass = first.getValue();
		}
		Pinger mainPinger = createPinger(pingerClass, scannerConfig.pingTimeout);
		if (isLAN) return new ARPPinger(injector.require(MACFetcher.class), mainPinger);
		return mainPinger;
	}

	Pinger createPinger(String pingerName, int timeout) throws FetcherException {
		return createPinger(pingers.get(pingerName), timeout);
	}

	/**
	 * Creates a specified pinger with specified timeout
	 */
	Pinger createPinger(Class<? extends Pinger> pingerClass, int timeout) throws FetcherException {
		try {
			return injector.require(pingerClass);
		}
		catch (InjectException ie) {
			try {
				Constructor<? extends Pinger> constructor = pingerClass.getConstructor(int.class);
				Pinger pinger = constructor.newInstance(timeout);
				injector.register((Class<Pinger>) pingerClass, pinger);
				return pinger;
			}
			catch (Exception e) {
				Throwable t = e instanceof InvocationTargetException ? e.getCause() : e;
				String message = "Unable to create pinger: " + pingerClass.getSimpleName();
				LOG.log(SEVERE, message, t);
				if (t instanceof RuntimeException) throw (RuntimeException) t;
				throw new FetcherException("pingerCreateFailure");
			}
		}
	}
}
