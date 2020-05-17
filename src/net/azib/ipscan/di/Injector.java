package net.azib.ipscan.di;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
			.filter(c -> c.isAnnotationPresent(Inject.class)).findAny()
			.orElseThrow(() -> new InjectException(type.getName() + " has no constructors annotated with @Inject"));
		try {
			return constructor.newInstance(resolveDeps(constructor));
		}
		catch (Exception e) {
			throw new InjectException("Cannot create " + type.getName() + ", deps: " + Arrays.toString(constructor.getGenericParameterTypes()), e);
		}
	}

	private Object[] resolveDeps(Constructor<?> constructor) {
		Type[] types = constructor.getGenericParameterTypes();
		Annotation[][] ans = constructor.getParameterAnnotations();
 		return range(0, types.length).mapToObj(i -> isCollection(types[i]) ?
			requireAll(getParamClass(types[i])) :
			require(new Key<>(toClass(types[i]), findName(ans[i])))
		).toArray();
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

	private String findName(Annotation[] ans) {
		return stream(ans).filter(a -> a.annotationType() == Named.class).findAny().map(a -> ((Named) a).value()).orElse(null);
	}
}
