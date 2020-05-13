package net.azib.ipscan.util;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;

@SuppressWarnings("unchecked")
public class Injector {
	private final Map<Key<?>, Object> instances = new HashMap<>();

	public <T> void registerNamed(Class<T> type, String name, T impl) {
		instances.put(new Key<>(type, name), impl);
	}

	public <T> T inject(Key<T> key) {
		return (T) instances.computeIfAbsent(key, k -> createInstance(key.type));
	}

	public <T> T inject(Class<T> type) {
		return inject(new Key<>(type, null));
	}

	private <T> T createInstance(Class<T> type) {
		Constructor<T> constructor = (Constructor<T>) stream(type.getConstructors())
			.filter(c -> c.isAnnotationPresent(Inject.class)).findAny()
			.orElseThrow(() -> new InjectException(type.getName() + " has no constructors annotated with @Inject"));
		Object[] deps = depsKeys(constructor).map(this::inject).toArray();
		try {
			return constructor.newInstance(deps);
		}
		catch (Exception e) {
			throw new InjectException("Cannot create " + type.getName() + ", deps: " + Arrays.toString(deps), e);
		}
	}

	private <T> Stream<Key<T>> depsKeys(Constructor<T> constructor) {
		Class<?>[] types = constructor.getParameterTypes();
		Annotation[][] ans = constructor.getParameterAnnotations();
		return range(0, types.length).mapToObj(i -> new Key<>((Class<T>) types[i], findName(ans[i])));
	}

	private String findName(Annotation[] ans) {
		return stream(ans).filter(a -> a.annotationType() == Named.class).findAny().map(a -> ((Named) a).value()).orElse(null);
	}

	public static class Key<T> {
		final Class<T> type;
		final String name;

		public Key(Class<T> type, String name) {
			this.type = type;
			this.name = name;
		}

		@Override public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Key<?> key = (Key<?>) o;
			return type.equals(key.type) && Objects.equals(name, key.name);
		}

		@Override public int hashCode() {
			return Objects.hash(type, name);
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
