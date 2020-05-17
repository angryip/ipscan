package net.azib.ipscan.di;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InjectorTest {
	private Injector injector = new Injector();

	@Test
	public void require() {
		assertTrue(injector.require(Dummy.class) instanceof Dummy);
		assertTrue(injector.require(WithDeps.class) instanceof WithDeps);
	}

	@Test
	public void namedRequire() {
		injector.register(String.class, "name", "mega-name");
		assertEquals("mega-name", injector.require(WithNamedDeps.class).name);
	}

	@Test
	public void requireAll() {
		injector.register(String.class, "name1", "name1");
		injector.register(String.class, "name2", "name2");
		injector.register(String.class, "name3", "name3");
		assertEquals(asList("name1", "name2", "name3"), injector.requireAll(String.class));
		assertEquals(asList("name1", "name2", "name3"), injector.require(WithListDeps.class).list);
	}

	static class Dummy {
		@Inject public Dummy() {}
	}

	static class WithDeps {
		@Inject public WithDeps(Dummy dummy) {}
	}

	static class WithNamedDeps {
		String name;
		@Inject public WithNamedDeps(Dummy dummy, @Named("name") String name) {
			this.name = name;
		}
	}

	static class WithListDeps {
		List<String> list;
		@Inject public WithListDeps(List<String> list, List<? extends String> list2, Dummy dummy) {
			this.list = list;
		}
	}
}
