package net.azib.ipscan.di;

import java.util.Objects;

class Key<T> {
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
