/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.feeders;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.azib.ipscan.config.GUIConfig;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.feeders.FeederException;
import net.azib.ipscan.feeders.RescanFeeder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.TableItem;

/**
 * FeederGUIRegistry
 *
 * @author Anton Keks
 */
public class FeederGUIRegistry implements Iterable<AbstractFeederGUI> {
	
	private final List<AbstractFeederGUI> feederGUIList;
	private final Combo feederSelectionCombo;	
	private final GUIConfig guiConfig;
	
	Feeder lastScanFeeder;
	private AbstractFeederGUI currentFeederGUI;
	
	public FeederGUIRegistry(AbstractFeederGUI[] allTheFeeders, Combo feederSelectionCombo, GUIConfig guiConfig) {
		this.feederGUIList = Arrays.asList(allTheFeeders);
		this.feederSelectionCombo = feederSelectionCombo;
		this.guiConfig = guiConfig;
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
		guiConfig.activeFeeder = newActiveFeeder;

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
		throw new FeederException("Feeder not found: " + feederName);
	}

	/**
	 * @return new Feeder initialized using the currently selected Feeder GUI
	 */
	public Feeder createFeeder() {
		lastScanFeeder = current().createFeeder(); 
		return lastScanFeeder;
	}

	/**
	 * @param selection selected table items to derive IP addresses from
	 * @return initialized instance of RescanFeeder
	 */
	public Feeder createRescanFeeder(TableItem[] selection) {
		String[] addresses = new String[selection.length];
		for (int i = 0; i < selection.length; i++) {
			addresses[i] = selection[i].getText();
		}
		return new RescanFeeder(lastScanFeeder, addresses);
	}
}
