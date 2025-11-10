package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.Platform;
import net.azib.ipscan.core.ScanningSubject;
import org.junit.Test;

import static net.azib.ipscan.util.InetAddressUtils.getLocalInterface;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

public class LinuxMACFetcherTest {
	@Test
	public void resolve() {
		assumeTrue(Platform.LINUX);
		var subject = new ScanningSubject(getLocalInterface().getAddress());
		assertEquals(17, new LinuxMACFetcher().resolveMAC(subject).length());
	}
}
