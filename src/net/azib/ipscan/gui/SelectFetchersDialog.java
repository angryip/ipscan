/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.IPFetcher;
import net.azib.ipscan.gui.util.LayoutHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * SelectFetchersDialog
 *
 * @author Anton Keks
 */
public class SelectFetchersDialog extends AbstractModalDialog {
	
	private FetcherRegistry fetcherRegistry;
	
	private List selectedFetchersList;
	private List registeredFetchersList;
	Map<String, String> registeredFetcherIdsByNames = new HashMap<String, String>();

	@Inject public SelectFetchersDialog(FetcherRegistry fetcherRegistry) {
		this.fetcherRegistry = fetcherRegistry;
	}
	
	@Override protected void populateShell() {
		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

		shell.setText(Labels.getLabel("title.fetchers.select"));
		shell.setLayout(LayoutHelper.formLayout(10, 10, 4));
		
		Label messageLabel = new Label(shell, SWT.WRAP);
		messageLabel.setText(Labels.getLabel("text.fetchers.select"));
		
		Label selectedLabel = new Label(shell, SWT.NONE);
		selectedLabel.setText(Labels.getLabel("text.fetchers.selectedList"));		
		selectedLabel.setLayoutData(LayoutHelper.formData(null, null, new FormAttachment(messageLabel, 5), null));
				
		selectedFetchersList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		selectedFetchersList.setLayoutData(LayoutHelper.formData(140, 200, new FormAttachment(0), new FormAttachment(selectedLabel, 80, SWT.RIGHT), new FormAttachment(selectedLabel), null));
		Iterator<Fetcher> i = fetcherRegistry.getSelectedFetchers().iterator();
		i.next();	// skip IP
		while (i.hasNext()) {
			Fetcher fetcher = i.next();
			selectedFetchersList.add(fetcher.getName());
		}
		
		Button upButton = new Button(shell, SWT.NONE);
		upButton.setText(Labels.getLabel("button.up"));	
		
		Button downButton = new Button(shell, SWT.NONE);
		downButton.setText(Labels.getLabel("button.down"));
		
		Button addButton = new Button(shell, SWT.NONE);
		addButton.setText(Labels.getLabel("button.left"));

		Button removeButton = new Button(shell, SWT.NONE);
		removeButton.setText(Labels.getLabel("button.right"));
		
		Button prefsButton = new Button(shell, SWT.NONE);
		prefsButton.setText(Labels.getLabel("button.fetcherPrefs"));
		prefsButton.setToolTipText(Labels.getLabel("text.fetchers.preferences"));
		
		upButton.setLayoutData(LayoutHelper.formData(new FormAttachment(selectedFetchersList), new FormAttachment(downButton, 0, SWT.RIGHT), new FormAttachment(selectedLabel), null));
		downButton.setLayoutData(LayoutHelper.formData(new FormAttachment(selectedFetchersList), null, new FormAttachment(upButton), null));
		addButton.setLayoutData(LayoutHelper.formData(new FormAttachment(selectedFetchersList), new FormAttachment(downButton, 0, SWT.RIGHT), new FormAttachment(downButton, 16), null));	
		removeButton.setLayoutData(LayoutHelper.formData(new FormAttachment(selectedFetchersList), new FormAttachment(downButton, 0, SWT.RIGHT), new FormAttachment(addButton), null));
		prefsButton.setLayoutData(LayoutHelper.formData(new FormAttachment(selectedFetchersList), new FormAttachment(downButton, 0, SWT.RIGHT), new FormAttachment(removeButton, 16), null));
		
		Label registeredLabel = new Label(shell, SWT.NONE);
		registeredLabel.setText(Labels.getLabel("text.fetchers.availableList"));		
		registeredLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(downButton, 10), null, new FormAttachment(messageLabel, 5), null));
		
		registeredFetchersList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		registeredFetchersList.setLayoutData(LayoutHelper.formData(140, 200, new FormAttachment(downButton, 10), null, new FormAttachment(registeredLabel), null));
		i = fetcherRegistry.getRegisteredFetchers().iterator();
		i.next(); // skip IP
		while (i.hasNext()) {
			Fetcher fetcher = i.next();
			String fetcherName = fetcher.getName();
			registeredFetcherIdsByNames.put(fetcherName, fetcher.getId());
			if (selectedFetchersList.indexOf(fetcherName) < 0)
				registeredFetchersList.add(fetcherName);
		}

		Button okButton = new Button(shell, SWT.NONE);
		okButton.setText(Labels.getLabel("button.OK"));
		
		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(Labels.getLabel("button.cancel"));
		
		positionButtonsInFormLayout(okButton, cancelButton, registeredFetchersList);

		upButton.addListener(SWT.Selection, new UpButtonListener(selectedFetchersList));
		downButton.addListener(SWT.Selection, new DownButtonListener(selectedFetchersList));
		AddRemoveButtonListener addButtonListener = new AddRemoveButtonListener(registeredFetchersList, selectedFetchersList);
		addButton.addListener(SWT.Selection, addButtonListener);
		registeredFetchersList.addListener(SWT.MouseDoubleClick, addButtonListener);
		AddRemoveButtonListener removeButtonListener = new AddRemoveButtonListener(selectedFetchersList, registeredFetchersList);
		removeButton.addListener(SWT.Selection, removeButtonListener);
		selectedFetchersList.addListener(SWT.MouseDoubleClick, removeButtonListener);
		prefsButton.addListener(SWT.Selection, new PrefsListener());

		// this is a workaround for limitation of FormLayout to remove the extra edge below the form
		shell.layout();
		Rectangle bounds = registeredFetchersList.getBounds();
		messageLabel.setLayoutData(LayoutHelper.formData(bounds.x + bounds.width - 10, SWT.DEFAULT, new FormAttachment(0), null, null, null));
		
		shell.pack();
		
		cancelButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				shell.close();
				shell.dispose();
			}
		});
		okButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				saveFetchersToRegistry(selectedFetchersList.getItems());
				shell.close();
				shell.dispose();
			}
		});
	}
	
	/**
	 * Saves passed selected fetchers to the fetcher registry.
	 * @param fetchersNamesToSave an array obtained by selectedFetchersList.getItems() 
	 */
	void saveFetchersToRegistry(String[] fetchersNamesToSave) {
		// IPFetcher must be implicitly there
		String[] fetchersLabelsToRetain = new String[fetchersNamesToSave.length+1];
		fetchersLabelsToRetain[0] = IPFetcher.ID; 

		for (int i = 0; i < fetchersNamesToSave.length; i++) {
			fetchersLabelsToRetain[i+1] = registeredFetcherIdsByNames.get(fetchersNamesToSave[i]);
		}
		fetcherRegistry.updateSelectedFetchers(fetchersLabelsToRetain);
	}

	class PrefsListener implements Listener {
		
		public void handleEvent(Event event) {
			String[] selection = selectedFetchersList.getSelection();
			String fetcherName = selection.length > 0 ? selection[0] : selectedFetchersList.getItem(0);
			for (Fetcher fetcher : fetcherRegistry.getRegisteredFetchers()) {
				if (fetcherName.equals(fetcher.getName())) {
					fetcherRegistry.openPreferencesEditor(fetcher);
					break;
				}
			}
		}
	}

	static class AddRemoveButtonListener implements Listener {
		
		private List fromList;
		private List toList;
		
		public AddRemoveButtonListener(List fromList, List toList) {
			this.fromList = fromList;
			this.toList = toList;
		}

		public void handleEvent(Event event) {
			int[] selectedItems = fromList.getSelectionIndices();

			// first, add items back to the registered list
			for (int selectedItem : selectedItems) {
				toList.add(fromList.getItem(selectedItem));
			}
			
			// now, add remove the items
			fromList.remove(selectedItems);
		}
	}
	
}
