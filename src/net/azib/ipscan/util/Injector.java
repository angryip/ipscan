package net.azib.ipscan.util;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.*;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

@SuppressWarnings("unchecked")
public class Injector {
	private final Map<Key<?>, Object> instances = new LinkedHashMap<>();

	public <T> void register(Class<T> type, String name, T impl) {
		instances.put(new Key<>(type, name), impl);
	}

	public <T> void register(Class<T> type, T impl) {
		register(type, null, impl);
	}

	<T> T require(Key<T> key) {
		return (T) instances.computeIfAbsent(key, k -> createInstance(key.type));
	}

	public <T> T require(Class<T> type) {
		return require(new Key<>(type, null));
	}

	public void register(Class<?> ... types) {
		stream(types).forEach(this::require);
	}

	public <T> List<T> requireAll(Class<T> type) {
		return instances.entrySet().stream().filter(e -> type.isAssignableFrom(e.getKey().type)).map(e -> (T) e.getValue()).collect(toList());
	}

	private <T> T createInstance(Class<T> type) {
		Constructor<T> constructor = (Constructor<T>) stream(type.getConstructors())
			.filter(c -> c.isAnnotationPresent(Inject.class)).findAny()
			.orElseThrow(() -> new InjectException(type.getName() + " has no constructors annotated with @Inject"));
		try {
			return constructor.newInstance(deps(constructor));
		}
		catch (Exception e) {
			throw new InjectException("Cannot create " + type.getName() + ", deps: " + Arrays.toString(constructor.getGenericParameterTypes()), e);
		}
	}

	private Object[] deps(Constructor<?> constructor) {
		Type[] types = constructor.getGenericParameterTypes();
		Annotation[][] ans = constructor.getParameterAnnotations();
		return range(0, types.length).mapToObj(i -> types[i] instanceof ParameterizedType ?
			requireAll(getParamClass((ParameterizedType) types[i])) :
			require(new Key<>((Class<?>) types[i], findName(ans[i])))
		).toArray();
	}

	private Class<?> getParamClass(ParameterizedType type) {
		Type t = type.getActualTypeArguments()[0];
		return (Class<?>) (t instanceof WildcardType ? ((WildcardType) t).getUpperBounds()[0] : t);
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
