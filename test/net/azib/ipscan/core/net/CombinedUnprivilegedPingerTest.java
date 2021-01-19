package net.azib.ipscan.core.net;

public class CombinedUnprivilegedPingerTest extends AbstractPingerTest {
	public CombinedUnprivilegedPingerTest() {
		super(new CombinedUnprivilegedPinger(10));
	}
}
