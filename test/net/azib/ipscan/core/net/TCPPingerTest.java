package net.azib.ipscan.core.net;

public class TCPPingerTest extends AbstractPingerTest {
	public TCPPingerTest() {
		super(new TCPPinger(10));
	}
}
