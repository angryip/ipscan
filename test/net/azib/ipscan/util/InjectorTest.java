package net.azib.ipscan.util;

import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertTrue;

public class InjectorTest {
	private Injector injector = new Injector();

	@Test
	public void inject() {
		assertTrue(injector.inject(Dummy.class) instanceof Dummy);
		assertTrue(injector.inject(WithDeps.class) instanceof WithDeps);
	}

	static class Dummy {
		@Inject public Dummy() {}
	}

	static class WithDeps {
		@Inject public WithDeps(Dummy dummy) {}
	}
}