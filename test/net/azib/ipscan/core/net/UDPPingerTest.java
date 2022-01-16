package net.azib.ipscan.core.net;

import net.azib.ipscan.config.Platform;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assume.assumeTrue;

public class UDPPingerTest extends AbstractPingerTest {
	public UDPPingerTest() throws Exception {
		super(UDPPinger.class);
	}

	@Test @Override
	public void pingAlive() throws IOException {
		assumeTrue(Platform.LINUX);
		super.pingAlive();
	}
}
