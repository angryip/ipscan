package net.azib.ipscan.core.net;

import net.azib.ipscan.config.Platform;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assume.assumeTrue;

public class UDPPingerTest extends AbstractPingerTest {
	public UDPPingerTest() {
		super(new UDPPinger(10));
	}

	@Test @Override
	public void pingAlive() throws IOException {
		assumeTrue(Platform.LINUX);
		super.pingAlive();
	}
}
