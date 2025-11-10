package net.azib.ipscan.gui.feeders;

import net.azib.ipscan.config.Labels;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FeederGUIRegistryTest {
	private FeederArea parent;
	private FeederGUIRegistry registry;
	private FeederSelectionCombo feederSelectionCombo;
	private RangeFeederGUI feederGUI;
	
	@Before
	public void createRegistry() {
		parent = new FeederArea(new Shell());
		
		feederSelectionCombo = mock(FeederSelectionCombo.class);
		
		feederGUI = new RangeFeederGUI(parent);
		feederGUI.initialize();
		registry = new FeederGUIRegistry(Collections.singletonList(feederGUI), feederSelectionCombo, null);
	}
	
	@After
	public void dispose() {
		parent.dispose();
	}
	
	@Test
	public void addFeederNamesToTheCombo() throws Exception {
		reset(feederSelectionCombo);
		new FeederGUIRegistry(Collections.singletonList(feederGUI), feederSelectionCombo, null);
        verify(feederSelectionCombo).add(Labels.getLabel(feederGUI.getFeederId()));
	}

	@Test
	public void lastFeederIsNeverNull() throws Exception {
		assertNotNull(registry.lastFeeder);
		assertNotNull(registry.lastFeeder.toString());
	}

	@Test
	public void createFeederRemembersTheLastOne() throws Exception {
		var lastFeeder = registry.createFeeder();
		assertSame(lastFeeder, registry.lastFeeder);
		assertNotSame(lastFeeder, registry.createFeeder());
	}

	@Test
	public void createRescanFeederGetsOriginalFeeder() throws Exception {
		var lastFeeder = registry.createFeeder();
		var rescanFeeder = registry.createRescanFeeder(new TableItem[] {mock(TableItem.class)});
		assertEquals(lastFeeder.getId(), rescanFeeder.getId());
	}
}
