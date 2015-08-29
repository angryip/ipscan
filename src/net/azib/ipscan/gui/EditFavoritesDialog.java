/**
 * 
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.FavoritesConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.gui.util.LayoutHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.*;

/**
 * EditFavoritesDialog
 *
 * @author Anton Keks
 */
public class EditFavoritesDialog extends AbstractModalDialog {

	private final FavoritesConfig favoritesConfig;
	private List favoritesList;
	
	public EditFavoritesDialog(FavoritesConfig favoritesConfig) {
		this.favoritesConfig = favoritesConfig;
	}

	@Override
	protected void populateShell() {
		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

		shell.setText(Labels.getLabel("title.favorite.edit"));
		shell.setLayout(LayoutHelper.formLayout(10, 10, 4));		
		
		Label messageLabel = new Label(shell, SWT.NONE);
		messageLabel.setText(Labels.getLabel("text.favorite.edit"));
		
		favoritesList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		favoritesList.setLayoutData(LayoutHelper.formData(330, 200, new FormAttachment(0), null, new FormAttachment(messageLabel), null));
		for (String name : favoritesConfig) {
			favoritesList.add(name);
		}
		
		Button upButton = new Button(shell, SWT.NONE);
		upButton.setText(Labels.getLabel("button.up"));		
		upButton.addListener(SWT.Selection, new UpButtonListener(favoritesList));
		
		Button downButton = new Button(shell, SWT.NONE);
		downButton.setText(Labels.getLabel("button.down"));
		downButton.addListener(SWT.Selection, new DownButtonListener(favoritesList));
		
		Button renameButton = new Button(shell, SWT.NONE);
		renameButton.setText(Labels.getLabel("button.rename"));
		Listener renameListener = new RenameListener();
		renameButton.addListener(SWT.Selection, renameListener);
		favoritesList.addListener(SWT.MouseDoubleClick, renameListener);

		Button deleteButton = new Button(shell, SWT.NONE);
		deleteButton.setText(Labels.getLabel("button.delete"));
		deleteButton.addListener(SWT.Selection, new DeleteListener());
		
		upButton.setLayoutData(LayoutHelper.formData(new FormAttachment(favoritesList), new FormAttachment(renameButton, 0, SWT.RIGHT), new FormAttachment(messageLabel), null));
		downButton.setLayoutData(LayoutHelper.formData(new FormAttachment(favoritesList), new FormAttachment(renameButton, 0, SWT.RIGHT), new FormAttachment(upButton), null));
		renameButton.setLayoutData(LayoutHelper.formData(new FormAttachment(favoritesList), null, new FormAttachment(downButton, 10), null));
		deleteButton.setLayoutData(LayoutHelper.formData(new FormAttachment(favoritesList), new FormAttachment(renameButton, 0, SWT.RIGHT), new FormAttachment(renameButton), null));
		
		Button okButton = new Button(shell, SWT.NONE);
		okButton.setText(Labels.getLabel("button.OK"));		
		
		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(Labels.getLabel("button.cancel"));		

		positionButtonsInFormLayout(okButton, cancelButton, favoritesList);
		
		shell.pack();
		
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				saveFavorites();
				shell.close();
			}
		});
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				shell.close();
			}
		});
	}
	
	private void saveFavorites() {
		favoritesConfig.update(favoritesList.getItems());
		favoritesConfig.store();
	}
	
	class DeleteListener implements Listener {
		public void handleEvent(Event event) {
			favoritesList.remove(favoritesList.getSelectionIndices());			
		}
	}

	class RenameListener implements Listener {
		public void handleEvent(Event event) {
			int index = favoritesList.getSelectionIndex();
			InputDialog prompt = new InputDialog(Labels.getLabel("title.rename"), "");
			String oldName = favoritesList.getItem(index);
			String newName = prompt.open(oldName);
			if (newName != null) {
				favoritesConfig.add(newName, favoritesConfig.remove(oldName));
				favoritesList.setItem(index, newName);
				// saving will rebuild favorites in correct order
			}
			favoritesList.forceFocus();
		}
	}
}
