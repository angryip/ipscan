package net.azib.ipscan.core.net;

public class UDPPingerTest extends AbstractPingerTest {
	public UDPPingerTest() {
		super(new UDPPinger(10));
	}
}
