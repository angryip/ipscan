/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.util.NetBIOSResolver;

import javax.inject.Inject;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Logger;

import static java.util.logging.Level.WARNING;

/**
 * NetBIOSInfoFetcher - gathers NetBIOS info about Windows machines.
 * Provided for feature-compatibility with version 2.x
 *
 * @author Anton Keks
 */
public class NetBIOSInfoFetcher extends AbstractFetcher {
	@Inject public NetBIOSInfoFetcher() {}

	private static final Logger LOG = LoggerFactory.getLogger();

	public String getId() {
		return "fetcher.netbios";
	}

	public Object scan(ScanningSubject subject) {
		NetBIOSResolver netbios = null;
		try {
			netbios = new NetBIOSResolver(subject.getAdaptedPortTimeout());
			String[] names = netbios.resolve(subject.getAddress());
			if (names == null) return null;

			String computerName = names[0];
			String userName = names[1];
			String groupName = names[2];
			String macAddress = names[3];

			return (groupName != null ? groupName + "\\" : "") +
					(userName != null ? userName + "@" : "") +
					computerName + " [" + macAddress + "]";
		}
		catch (SocketTimeoutException e) {
			// this is not a derivative of SocketException
			return null;
		}
		catch (SocketException e) {
			// this includes PortUnreachableException
			return null;
		}
		catch (Exception e) {
			// bugs?
			LOG.log(WARNING, null, e);
			return null;
		}
		finally {
			if (netbios != null) netbios.close();
		}
	}
}
