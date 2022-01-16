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
	private final Map<Class<?>, Object> instances = new LinkedHashMap<>();
	{ register(Injector.class, this); }

	public <T> void register(Class<T> type, T impl) {
		instances.put(type, impl);
	}

	public <T> T require(Class<T> type) {
		// unfortunately, HashMap.computeIfAbsent() doesn't put values properly in a recursive scenario
		T value = (T) instances.get(type);
		if (value == null) instances.put(type, value = createInstance(type));
		return value;
	}

	public void register(Class<?> ... types) {
		stream(types).forEach(this::require);
	}

	public <T> List<T> requireAll(Class<T> type) {
		return instances.entrySet().stream().filter(e -> type.isAssignableFrom(e.getKey())).map(e -> (T) e.getValue()).collect(toList());
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
			requireAll(collectionItemType(t)) : require(toClass(t))).toArray();
	}

	private Class<?> toClass(Type type) {
		if (type instanceof Class) return (Class<?>) type;
		else if (type instanceof ParameterizedType) return (Class<?>) ((ParameterizedType) type).getRawType();
		else throw new InjectException(type + " is not supported");
	}

	private boolean isCollection(Type type) {
		return type instanceof ParameterizedType && Collection.class.isAssignableFrom(toClass(type));
	}

	private Class<?> collectionItemType(Type type) {
		return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
	}
}
