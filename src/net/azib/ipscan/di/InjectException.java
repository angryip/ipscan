package net.azib.ipscan.di;

public class InjectException extends RuntimeException {
	public InjectException(String message, Throwable e) {
		super(message, e);
	}

	public InjectException(String message) {
		super(message);
	}
}
