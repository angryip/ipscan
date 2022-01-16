package net.azib.ipscan.core.net;

import net.azib.ipscan.config.Platform;
import org.junit.BeforeClass;

import static org.junit.Assume.assumeTrue;

public class WindowsPingerTest extends AbstractPingerTest {
	public WindowsPingerTest() throws Exception {
		super(WindowsPinger.class);
	}

	@BeforeClass
	public static void beforeClass() {
		assumeTrue(Platform.WINDOWS);
	}
}
