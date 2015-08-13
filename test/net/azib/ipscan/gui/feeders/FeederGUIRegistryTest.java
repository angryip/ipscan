/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.feeders;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.feeders.Feeder;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * FeederGUIRegistryTest
 *
 * @author Anton Keks
 */
public class FeederGUIRegistryTest {
	
	private Composite parent;
	private FeederGUIRegistry registry;
	private Combo feederSelectionCombo;
	private RangeFeederGUI feederGUI;
	
	@Before
	public void createRegistry() {
		parent = new Shell();
		
		feederSelectionCombo = mock(Combo.class);
		
		feederGUI = new RangeFeederGUI(parent);
		feederGUI.initialize();
		registry = new FeederGUIRegistry(Collections.<AbstractFeederGUI>singletonList(feederGUI), feederSelectionCombo, null);
	}
	
	@After
	public void dispose() {
		parent.dispose();
	}
	
	@Test
	public void addFeederNamesToTheCombo() throws Exception {
		reset(feederSelectionCombo);
		new FeederGUIRegistry(Collections.<AbstractFeederGUI>singletonList(feederGUI), feederSelectionCombo, null);
        verify(feederSelectionCombo).add(Labels.getLabel(feederGUI.getFeederId()));
	}

	@Test
	public void createFeederRemembersTheLastOne() throws Exception {
		Feeder lastFeeder = registry.createFeeder();
		assertSame(lastFeeder, registry.lastScanFeeder);
		assertNotSame(lastFeeder, registry.createFeeder());
	}

	@Test
	public void createRescanFeederGetsOriginalFeeder() throws Exception {
		Feeder lastFeeder = registry.createFeeder();
		Feeder rescanFeeder = registry.createRescanFeeder(new TableItem[] {mock(TableItem.class)});
		assertEquals(lastFeeder.getId(), rescanFeeder.getId());
	}
}
