package net.azib.ipscan.gui.feeders;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.feeders.Feeder;
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
	public void lastFeederIsNeverNull() throws Exception {
		assertNotNull(registry.lastFeeder);
		assertNotNull(registry.lastFeeder.toString());
	}
}
