package net.azib.ipscan.di;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InjectorTest {
	private Injector injector = new Injector();

	@Test
	public void require() {
		assertNotNull(injector.require(Dummy.class));
		assertNotNull(injector.require(WithDeps.class).dummy);
	}

	@Test
	public void namedRequire() {
		injector.register(String.class, "mega-name");
		assertEquals("mega-name", injector.require(WithNamedDeps.class).name);
	}

	@Test
	public void requireAll() {
		injector.register(LocalDate.class, LocalDate.now());
		injector.register(LocalTime.class, LocalTime.now());
		injector.register(LocalDateTime.class, LocalDateTime.now());

		List<Temporal> temporals = injector.requireAll(Temporal.class);
		assertEquals(3, temporals.size());
		assertEquals(temporals, injector.require(WithListDeps.class).list);
	}

	static class Dummy {
		public Dummy() {}
	}

	static class WithDeps {
		Dummy dummy;
		public WithDeps() {}
		public WithDeps(Dummy dummy) { this.dummy = dummy; }
	}

	static class WithNamedDeps {
		String name;
		public WithNamedDeps(Dummy dummy, String name) {
			this.name = name;
		}
	}

	static class WithListDeps {
		List<Temporal> list;
		public WithListDeps(List<Temporal> list, Dummy dummy) {
			this.list = list;
		}
	}
}
