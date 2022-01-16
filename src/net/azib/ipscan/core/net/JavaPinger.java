package net.azib.ipscan.core.net;

import net.azib.ipscan.core.ScanningSubject;

import java.io.IOException;
import java.net.ConnectException;

public class JavaPinger implements Pinger {
	private int timeout;

	public JavaPinger(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public PingResult ping(ScanningSubject subject, int count) throws IOException {
		PingResult result = new PingResult(subject.getAddress(), count);
		for (int i = 0; i < count; i++) {
			try {
				long t = System.currentTimeMillis();
				if (subject.getAddress().isReachable(timeout))
					result.addReply(System.currentTimeMillis() - t);
			}
			catch (ConnectException e) {
				// these happen on Mac
			}
		}
		return result;
	}
}
