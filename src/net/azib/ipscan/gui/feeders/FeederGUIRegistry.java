/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.feeders;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.feeders.FeederException;

/**
 * FeederGUIRegistry
 *
 * @author anton
 */
public class FeederGUIRegistry implements Iterable<AbstractFeederGUI> {
	
	private List<AbstractFeederGUI> feederGUIList;
	private Combo feederSelectionCombo;
	
	private AbstractFeederGUI currentFeederGUI;
	
	public FeederGUIRegistry(AbstractFeederGUI[] allTheFeeders, Combo feederSelectionCombo) {
		this.feederGUIList = Arrays.asList(allTheFeeders);
		this.feederSelectionCombo = feederSelectionCombo;
		this.currentFeederGUI = allTheFeeders[0];
	}
	
	public AbstractFeederGUI current() {
		return currentFeederGUI;
	}

	/**
	 * Select a new indexed feeder GUI
	 */
	public void select(int newActiveFeeder) {
		// hide current feeder
		currentFeederGUI.setVisible(false);

		// get new feeder
		currentFeederGUI = feederGUIList.get(newActiveFeeder);
		Config.getGlobal().activeFeeder = newActiveFeeder;

		// make new feeder visible
		currentFeederGUI.setVisible(true);
	}
	
	public Iterator<AbstractFeederGUI> iterator() {
		return feederGUIList.iterator();
	}

	/**
	 * Select the Feeder GUI by its name, while updating the GUI
	 */
	public void select(String feederName) {
		String[] items = feederSelectionCombo.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].equals(feederName)) {
				// select the feeder if found
				feederSelectionCombo.select(i);
				feederSelectionCombo.notifyListeners(SWT.Selection, null);
				return;
			}
		}
		// if not found
		throw new FeederException("No such feeder found: " + feederName);
	}
}
