package net.azib.ipscan.gui;

import net.azib.ipscan.config.FavoritesConfig;
import net.azib.ipscan.config.Labels;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.*;

import static net.azib.ipscan.gui.util.LayoutHelper.formData;
import static net.azib.ipscan.gui.util.LayoutHelper.formLayout;

public class EditFavoritesDialog extends AbstractModalDialog {

	private final FavoritesConfig favoritesConfig;
	private List favoritesList;
	
	public EditFavoritesDialog(FavoritesConfig favoritesConfig) {
		this.favoritesConfig = favoritesConfig;
	}

	@Override
	protected void populateShell() {
		var currentDisplay = Display.getCurrent();
		var parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

		shell.setText(Labels.getLabel("title.favorite.edit"));
		shell.setLayout(formLayout(10, 10, 4));

		var messageLabel = new Label(shell, SWT.NONE);
		messageLabel.setText(Labels.getLabel("text.favorite.edit"));
		
		favoritesList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		favoritesList.setLayoutData(formData(330, 200, new FormAttachment(0), null, new FormAttachment(messageLabel), null));
		for (var name : favoritesConfig) {
			favoritesList.add(name);
		}

		var upButton = new Button(shell, SWT.NONE);
		upButton.setText(Labels.getLabel("button.up"));		
		upButton.addListener(SWT.Selection, new UpButtonListener(favoritesList));

		var downButton = new Button(shell, SWT.NONE);
		downButton.setText(Labels.getLabel("button.down"));
		downButton.addListener(SWT.Selection, new DownButtonListener(favoritesList));

		var renameButton = new Button(shell, SWT.NONE);
		renameButton.setText(Labels.getLabel("button.rename"));
		Listener renameListener = new RenameListener();
		renameButton.addListener(SWT.Selection, renameListener);
		favoritesList.addListener(SWT.MouseDoubleClick, renameListener);

		var deleteButton = new Button(shell, SWT.NONE);
		deleteButton.setText(Labels.getLabel("button.delete"));
		deleteButton.addListener(SWT.Selection, new DeleteListener());
		
		upButton.setLayoutData(formData(new FormAttachment(favoritesList), new FormAttachment(renameButton, 0, SWT.RIGHT), new FormAttachment(messageLabel), null));
		downButton.setLayoutData(formData(new FormAttachment(favoritesList), new FormAttachment(renameButton, 0, SWT.RIGHT), new FormAttachment(upButton), null));
		renameButton.setLayoutData(formData(new FormAttachment(favoritesList), null, new FormAttachment(downButton, 10), null));
		deleteButton.setLayoutData(formData(new FormAttachment(favoritesList), new FormAttachment(renameButton, 0, SWT.RIGHT), new FormAttachment(renameButton), null));

		var okButton = new Button(shell, SWT.NONE);
		okButton.setText(Labels.getLabel("button.OK"));

		var cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(Labels.getLabel("button.cancel"));		

		positionButtonsInFormLayout(okButton, cancelButton, favoritesList);
		
		shell.pack();
		
		okButton.addListener(SWT.Selection, e -> {
			saveFavorites();
			close();
		});
		cancelButton.addListener(SWT.Selection, e -> close());
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
			if (favoritesConfig.size() == 0) return;
			var index = Math.max(favoritesList.getSelectionIndex(), 0);

			var prompt = new InputDialog(Labels.getLabel("title.rename"), "");
			var oldName = favoritesList.getItem(index);
			var newName = prompt.open(oldName);
			if (newName != null) {
				favoritesConfig.add(newName, favoritesConfig.remove(oldName));
				favoritesList.setItem(index, newName);
				// saving will rebuild favorites in correct order
			}
			favoritesList.forceFocus();
		}
	}
}
