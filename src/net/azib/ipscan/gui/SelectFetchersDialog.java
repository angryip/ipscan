/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.IPFetcher;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * SelectFetchersDialog
 *
 * @author Anton Keks
 */
public class SelectFetchersDialog extends AbstractModalDialog {
	
	private FetcherRegistry fetcherRegistry;
	
	private List selectedFetchersList;
	private List registeredFetchersList;
	Map registeredFetcherLabelsByNames = new HashMap();

	public SelectFetchersDialog(FetcherRegistry fetcherRegistry) {
		this.fetcherRegistry = fetcherRegistry;
	}
	
	public void open() {
		// create controls on demand
		createShell();
		super.open();
	}

	/**
	 * This method initializes shell
	 */
	private void createShell() {
		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

		shell.setText(Labels.getLabel("title.fetchers.select"));
		shell.setSize(new Point(420, 332));		
		shell.setLayout(null);		
		
		Label messageLabel = new Label(shell, SWT.WRAP);
		messageLabel.setText(Labels.getLabel("text.fetchers.select"));
		messageLabel.setSize(messageLabel.computeSize(420, SWT.DEFAULT));
		messageLabel.setLocation(10, 10);
		Rectangle messageLabelBounds = messageLabel.getBounds();
		int topLocation = messageLabelBounds.y + messageLabelBounds.height + 10;
		
		Label selectedLabel = new Label(shell, SWT.NONE);
		selectedLabel.setText(Labels.getLabel("text.fetchers.selectedList"));		
		selectedLabel.setBounds(new Rectangle(10, topLocation, 155, 14));
		
		Button okButton = new Button(shell, SWT.NONE);
		okButton.setText(Labels.getLabel("button.OK"));		
		
		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(Labels.getLabel("button.cancel"));		
		
		positionButtons(okButton, cancelButton);
		
		selectedFetchersList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		selectedFetchersList.setBounds(new Rectangle(10, topLocation + 20, 155, okButton.getLocation().y - 25 - topLocation));
		Iterator i = fetcherRegistry.getSelectedFetchers().iterator();
		i.next();	// skip IP
		while (i.hasNext()) {
			Fetcher fetcher = (Fetcher) i.next();
			String fetcherName = Labels.getLabel(fetcher.getLabel());
			selectedFetchersList.add(fetcherName);
		}
		
		Button upButton = new Button(shell, SWT.NONE);
		upButton.setText(Labels.getLabel("button.up"));	
		upButton.pack();
		upButton.setLocation(170, topLocation + 20);
		
		Button downButton = new Button(shell, SWT.NONE);
		downButton.setText(Labels.getLabel("button.down"));
		downButton.pack();
		downButton.setLocation(170, topLocation + 50);
		
		Button addButton = new Button(shell, SWT.NONE);
		addButton.setText(Labels.getLabel("button.left"));
		addButton.pack();
		addButton.setLocation(170, topLocation + 95);

		Button removeButton = new Button(shell, SWT.NONE);
		removeButton.setText(Labels.getLabel("button.right"));
		removeButton.pack();
		removeButton.setLocation(170, topLocation + 125);
		
		Label registeredLabel = new Label(shell, SWT.NONE);
		registeredLabel.setText(Labels.getLabel("text.fetchers.availableList"));		
		registeredLabel.setBounds(new Rectangle(245, topLocation, 155, 14));
		
		registeredFetchersList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		registeredFetchersList.setBounds(new Rectangle(245, topLocation + 20, 160, okButton.getLocation().y - 25 - topLocation));
		i = fetcherRegistry.getRegisteredFetchers().iterator();
		i.next(); // skip IP
		while (i.hasNext()) {
			Fetcher fetcher = (Fetcher) i.next();
			String fetcherName = Labels.getLabel(fetcher.getLabel());
			registeredFetcherLabelsByNames.put(fetcherName, fetcher.getLabel());
			if (selectedFetchersList.indexOf(fetcherName) < 0)
				registeredFetchersList.add(fetcherName);
		}

		upButton.addListener(SWT.Selection, new UpButtonListener(selectedFetchersList));
		downButton.addListener(SWT.Selection, new DownButtonListener(selectedFetchersList));
		AddRemoveButtonListener addButtonListener = new AddRemoveButtonListener(registeredFetchersList, selectedFetchersList);
		addButton.addListener(SWT.Selection, addButtonListener);
		registeredFetchersList.addListener(SWT.MouseDoubleClick, addButtonListener);
		AddRemoveButtonListener removeButtonListener = new AddRemoveButtonListener(selectedFetchersList, registeredFetchersList);
		removeButton.addListener(SWT.Selection, removeButtonListener);
		selectedFetchersList.addListener(SWT.MouseDoubleClick, removeButtonListener);
		
		cancelButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				shell.close();
			}
		});
		okButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				saveFetchersToRegistry(selectedFetchersList.getItems());
				shell.close();
			}
		});
		shell.setDefaultButton(okButton);
	}
	
	/**
	 * Saves passed selected fetchers to the fetcher registry.
	 * @param fetchersNamesToSave an array obtained by selectedFetchersList.getItems() 
	 */
	void saveFetchersToRegistry(String[] fetchersNamesToSave) {
		// IPFetcher must be implicitly there
		String[] fetchersLabelsToRetain = new String[fetchersNamesToSave.length+1];
		fetchersLabelsToRetain[0] = IPFetcher.LABEL; 

		for (int i = 0; i < fetchersNamesToSave.length; i++) {
			fetchersLabelsToRetain[i+1] = (String) registeredFetcherLabelsByNames.get(fetchersNamesToSave[i]);
		}
		fetcherRegistry.updateSelectedFetchers(fetchersLabelsToRetain);
	}

	private static class AddRemoveButtonListener implements Listener {
		
		private List fromList;
		private List toList;
		
		public AddRemoveButtonListener(List fromList, List toList) {
			this.fromList = fromList;
			this.toList = toList;
		}

		public void handleEvent(Event event) {
			int[] selectedItems = fromList.getSelectionIndices();

			// first, add items back to the registered list
			for (int i = 0; i < selectedItems.length; i++) {
				toList.add(fromList.getItem(selectedItems[i]));
			}
			
			// now, add remove the items
			fromList.remove(selectedItems);
		}
	}

}
