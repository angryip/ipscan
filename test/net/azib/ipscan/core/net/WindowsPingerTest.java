package net.azib.ipscan.core.net;

import net.azib.ipscan.config.Platform;
import org.junit.BeforeClass;

import static org.junit.Assume.assumeTrue;

public class WindowsPingerTest extends AbstractPingerTest {
	public WindowsPingerTest() {
		super(new WindowsPinger(10));
	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		assumeTrue(Platform.WINDOWS);
	}
}
