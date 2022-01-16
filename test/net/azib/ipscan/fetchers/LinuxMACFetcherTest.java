package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.Platform;
import org.junit.Test;

import static net.azib.ipscan.util.InetAddressUtils.getLocalInterface;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

public class LinuxMACFetcherTest {
	@Test
	public void resolve() {
		assumeTrue(Platform.LINUX);
		assertEquals(17, new LinuxMACFetcher().resolveMAC(getLocalInterface().getAddress()).length());
	}
}
