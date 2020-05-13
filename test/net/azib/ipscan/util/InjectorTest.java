package net.azib.ipscan.util;

import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Named;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InjectorTest {
	private Injector injector = new Injector();

	@Test
	public void inject() {
		assertTrue(injector.inject(Dummy.class) instanceof Dummy);
		assertTrue(injector.inject(WithDeps.class) instanceof WithDeps);
	}

	@Test
	public void injectWithNamed() {
		injector.registerNamed(String.class, "name", "mega-name");
		assertEquals("mega-name", injector.inject(WithNamedDeps.class).name);
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
}