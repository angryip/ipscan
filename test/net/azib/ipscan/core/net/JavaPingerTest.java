package net.azib.ipscan.core.net;

public class JavaPingerTest extends AbstractPingerTest {
	public JavaPingerTest() {
		super(new JavaPinger(10));
	}
}
