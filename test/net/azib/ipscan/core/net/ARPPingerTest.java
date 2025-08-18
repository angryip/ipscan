package net.azib.ipscan.core.net;

import net.azib.ipscan.config.Platform;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assume.assumeFalse;

public class ARPPingerTest extends AbstractPingerTest {
	public ARPPingerTest() throws Exception {
		super(ARPPinger.class);
	}

	@Test @Override
	public void pingAlive() throws IOException {
		assumeFalse(Platform.MAC_OS);
		super.pingAlive();
	}
}
