package net.azib.ipscan.core.net;

import net.azib.ipscan.core.ScanningSubject;

import java.io.IOException;

public class JavaPinger implements Pinger {
	private int timeout;

	public JavaPinger(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public PingResult ping(ScanningSubject subject, int count) throws IOException {
		PingResult result = new PingResult(subject.getAddress(), count);
		for (int i = 0; i < count; i++) {
			long t = System.currentTimeMillis();
			if (subject.getAddress().isReachable(timeout))
				result.addReply(System.currentTimeMillis() - t);
		}
		return result;
	}

	@Override
	public void close() {
	}
}
