/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.IPFetcher;
import net.azib.ipscan.gui.util.LayoutHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.*;

import java.util.HashMap;
import java.util.Map;

import static net.azib.ipscan.gui.util.LayoutHelper.*;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

public class SelectFetchersDialog extends AbstractModalDialog {
	private FetcherRegistry fetcherRegistry;
	private List lastFocusList;
	private List selectedFetchersList;
	private List registeredFetchersList;
	Map<String, String> registeredFetcherIdsByNames = new HashMap<>();

	public SelectFetchersDialog(FetcherRegistry fetcherRegistry) {
		this.fetcherRegistry = fetcherRegistry;
	}
	
	@Override protected void populateShell() {
		var currentDisplay = Display.getCurrent();
		var parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

		shell.setText(Labels.getLabel("title.fetchers"));
		shell.setLayout(LayoutHelper.formLayout(10, 10, 4));

		var messageLabel = new Label(shell, SWT.WRAP);
		messageLabel.setText(Labels.getLabel("text.fetchers.select"));

		var selectedLabel = new Label(shell, SWT.NONE);
		selectedLabel.setText(Labels.getLabel("text.fetchers.selectedList"));		
		selectedLabel.setLayoutData(formData(null, null, new FormAttachment(messageLabel, 5), null));
				
		selectedFetchersList = lastFocusList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		selectedFetchersList.setLayoutData(formData(160, 250, new FormAttachment(0), null, new FormAttachment(selectedLabel), null));
		var i = fetcherRegistry.getSelectedFetchers().iterator();
		i.next();	// skip IP
		while (i.hasNext()) {
			var fetcher = i.next();
			selectedFetchersList.add(fetcher.getName());
		}

		var iconFont = iconFont(shell);

		var upButton = new Button(shell, SWT.NONE);
		upButton.setText(Labels.getLabel("button.up"));
		upButton.setToolTipText(Labels.getLabel("button.up.hint"));
		upButton.setFont(iconFont);

		var downButton = new Button(shell, SWT.NONE);
		downButton.setText(Labels.getLabel("button.down"));
		downButton.setToolTipText(Labels.getLabel("button.down.hint"));
		downButton.setFont(iconFont);

		var addButton = new Button(shell, SWT.NONE);
		addButton.setText(Labels.getLabel("button.left"));
		addButton.setToolTipText(Labels.getLabel("button.left.hint"));
		addButton.setFont(iconFont);

		var removeButton = new Button(shell, SWT.NONE);
		removeButton.setText(Labels.getLabel("button.right"));
		removeButton.setToolTipText(Labels.getLabel("button.right.hint"));
		removeButton.setFont(iconFont);

		var prefsButton = new Button(shell, SWT.NONE);
		prefsButton.setImage(icon("buttons/prefs"));
		prefsButton.setToolTipText(Labels.getLabel("text.fetchers.preferences"));
		prefsButton.setFont(iconFont);
		
		upButton.setLayoutData(formData(new FormAttachment(selectedFetchersList), null, new FormAttachment(selectedLabel), null));
		downButton.setLayoutData(formData(new FormAttachment(selectedFetchersList), null, new FormAttachment(upButton), null));
		addButton.setLayoutData(formData(new FormAttachment(selectedFetchersList), null, new FormAttachment(downButton, 16), null));
		removeButton.setLayoutData(formData(new FormAttachment(selectedFetchersList), null, new FormAttachment(addButton), null));
		prefsButton.setLayoutData(formData(new FormAttachment(selectedFetchersList), new FormAttachment(removeButton, 0, SWT.RIGHT), new FormAttachment(removeButton, 16), null));

		var registeredLabel = new Label(shell, SWT.NONE);
		registeredLabel.setText(Labels.getLabel("text.fetchers.availableList"));		
		registeredLabel.setLayoutData(formData(new FormAttachment(upButton, 10), null, new FormAttachment(messageLabel, 5), null));
		
		registeredFetchersList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		registeredFetchersList.setLayoutData(formData(160, 250, new FormAttachment(upButton, 10), null, new FormAttachment(registeredLabel), null));
		i = fetcherRegistry.getRegisteredFetchers().iterator();
		i.next(); // skip IP
		while (i.hasNext()) {
			var fetcher = i.next();
			var fetcherName = fetcher.getName();
			registeredFetcherIdsByNames.put(fetcherName, fetcher.getId());
			if (selectedFetchersList.indexOf(fetcherName) < 0)
				registeredFetchersList.add(fetcherName);
		}

		var okButton = new Button(shell, SWT.NONE);
		okButton.setText(Labels.getLabel("button.OK"));

		var cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(Labels.getLabel("button.cancel"));
		
		positionButtonsInFormLayout(okButton, cancelButton, registeredFetchersList);

		upButton.addListener(SWT.Selection, new UpButtonListener(selectedFetchersList));
		downButton.addListener(SWT.Selection, new DownButtonListener(selectedFetchersList));
		var addButtonListener = new AddRemoveButtonListener(registeredFetchersList, selectedFetchersList);
		addButton.addListener(SWT.Selection, addButtonListener);
		registeredFetchersList.addListener(SWT.MouseDoubleClick, addButtonListener);
		var removeButtonListener = new AddRemoveButtonListener(selectedFetchersList, registeredFetchersList);
		removeButton.addListener(SWT.Selection, removeButtonListener);
		selectedFetchersList.addListener(SWT.MouseDoubleClick, removeButtonListener);
		prefsButton.addListener(SWT.Selection, new PrefsListener());

		registeredFetchersList.addSelectionListener(widgetSelectedAdapter(e -> lastFocusList = (List) e.getSource()));
		selectedFetchersList.addSelectionListener(widgetSelectedAdapter(e -> lastFocusList = (List) e.getSource()));

		// this is a workaround for limitation of FormLayout to remove the extra edge below the form
		shell.layout();
		var bounds = registeredFetchersList.getBounds();
		messageLabel.setLayoutData(formData(bounds.x + bounds.width - 10, SWT.DEFAULT, new FormAttachment(0), null, null, null));
		
		shell.pack();
		
		cancelButton.addListener(SWT.Selection, e -> close());
		okButton.addListener(SWT.Selection, event -> {
			saveFetchersToRegistry(selectedFetchersList.getItems());
			close();
		});
	}

	/**
	 * Saves passed selected fetchers to the fetcher registry.
	 * @param fetchersNamesToSave an array obtained by selectedFetchersList.getItems() 
	 */
	void saveFetchersToRegistry(String[] fetchersNamesToSave) {
		// IPFetcher must be implicitly there
		var fetchersLabelsToRetain = new String[fetchersNamesToSave.length+1];
		fetchersLabelsToRetain[0] = IPFetcher.ID; 

		for (var i = 0; i < fetchersNamesToSave.length; i++) {
			fetchersLabelsToRetain[i+1] = registeredFetcherIdsByNames.get(fetchersNamesToSave[i]);
		}
		fetcherRegistry.updateSelectedFetchers(fetchersLabelsToRetain);
	}

	

	class PrefsListener implements Listener {
		public void handleEvent(Event event) {
			if (lastFocusList.getItemCount() == 0) return;
			var selectionIndex = lastFocusList.getSelectionIndex();
			if (selectionIndex < 0) selectionIndex = 0;
			var fetcherName = lastFocusList.getItem(selectionIndex);
			for (var fetcher : fetcherRegistry.getRegisteredFetchers()) {
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
			var selectedItems = fromList.getSelectionIndices();

			// first, add items back to the registered list
			for (var selectedItem : selectedItems) {
				toList.add(fromList.getItem(selectedItem));
			}
			
			// now, add remove the items
			fromList.remove(selectedItems);
		}
	}
}
