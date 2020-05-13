package net.azib.ipscan.util;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;

public class Injector {
	private final Map<Class<?>, Object> instances = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T> T inject(Class<T> type) {
		return (T) instances.computeIfAbsent(type, this::createInstance);
	}

	@SuppressWarnings("unchecked")
	private <T> T createInstance(Class<T> type) {
		Constructor<?> constructor = stream(type.getConstructors()).filter(c -> c.isAnnotationPresent(Inject.class))
				.findAny().orElseThrow(() -> new InjectException(type.getName() + " has no constructors annotated with @Inject"));
		Object[] deps = stream(constructor.getParameterTypes()).map(this::inject).toArray();
		try {
			return (T) constructor.newInstance(deps);
		}
		catch (Exception e) {
			throw new InjectException("Cannot create " + type.getName() + ", depending on " + Arrays.toString(deps), e);
		}
	}

	public static class InjectException extends RuntimeException {
		public InjectException(String message, Exception e) {
			super(message, e);
		}

		public InjectException(String message) {
			super(message);
		}
	}
}
