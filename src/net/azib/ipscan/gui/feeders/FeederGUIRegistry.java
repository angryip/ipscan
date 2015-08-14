/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.feeders;

import net.azib.ipscan.config.GUIConfig;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.feeders.FeederException;
import net.azib.ipscan.feeders.FeederRegistry;
import net.azib.ipscan.feeders.RescanFeeder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.TableItem;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Iterator;
import java.util.List;

/**
 * FeederGUIRegistry
 *
 * @author Anton Keks
 */
@Singleton
public class FeederGUIRegistry implements FeederRegistry<AbstractFeederGUI> {
	
	private final List<AbstractFeederGUI> feederGUIList;
	private final Combo feederSelectionCombo;
	private final GUIConfig guiConfig;
	
	Feeder lastScanFeeder;
	private AbstractFeederGUI currentFeederGUI;
	
	@Inject public FeederGUIRegistry(List<AbstractFeederGUI> allTheFeeders, @Named("feederSelectionCombo") Combo feederSelectionCombo, GUIConfig guiConfig) {
		this.feederGUIList = allTheFeeders;
		this.feederSelectionCombo = feederSelectionCombo;
		for (AbstractFeederGUI feederGUI : feederGUIList) {
			feederSelectionCombo.add(feederGUI.getFeederName());	
		}
		this.guiConfig = guiConfig;
		this.currentFeederGUI = allTheFeeders.get(0);
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
	public void select(String feederId) {
		for (int i = 0; i < feederGUIList.size(); i++) {
			AbstractFeederGUI guiFeeder = feederGUIList.get(i);
			if (guiFeeder.getFeederId().equals(feederId) || guiFeeder.getFeederName().equals(feederId)) {
				// select the feeder if found
				feederSelectionCombo.select(i);
				feederSelectionCombo.notifyListeners(SWT.Selection, null);
				return;
			}
		}
		// if not found
		throw new FeederException("Feeder not found: " + feederId);
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
