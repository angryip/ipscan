package net.azib.ipscan.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

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
		// unfortunately, HashMap.computeIfAbsent() doesn't put values properly in a recursive scenario
		T value = (T) instances.get(key);
		if (value == null) instances.put(key, value = createInstance(key.type));
		return value;
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
				.max(comparing(Constructor::getParameterCount))
				.orElseThrow(() -> new InjectException("No public constructors"));
		try {
			return constructor.newInstance(resolveDeps(constructor));
		}
		catch (Throwable e) {
			throw new InjectException("Cannot create " + type.getName() + ", deps: " + Arrays.toString(constructor.getGenericParameterTypes()), e);
		}
	}

	private Object[] resolveDeps(Constructor<?> constructor) {
		return stream(constructor.getGenericParameterTypes()).map(t -> isCollection(t) ?
			requireAll(getParamClass(t)) : require(new Key<>(toClass(t), null))).toArray();
	}

	private Class<?> toClass(Type type) {
		if (type instanceof Class) return (Class<?>) type;
		else if (type instanceof ParameterizedType) return (Class<?>) ((ParameterizedType) type).getRawType();
		else throw new InjectException(type + " is not supported");
	}

	private boolean isCollection(Type type) {
		return type instanceof ParameterizedType && Collection.class.isAssignableFrom(toClass(type));
	}

	private Class<?> getParamClass(Type type) {
		return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
	}
}
