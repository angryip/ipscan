package net.azib.ipscan.core.net;

import net.azib.ipscan.config.Platform;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assume.assumeFalse;

public class UDPPingerTest extends AbstractPingerTest {
	public UDPPingerTest() throws Exception {
		super(UDPPinger.class);
	}

	@Test @Override
	public void pingAlive() throws IOException {
		assumeFalse(Platform.WINDOWS);
		super.pingAlive();
	}
}
