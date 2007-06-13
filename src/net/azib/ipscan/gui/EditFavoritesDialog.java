/**
 * 
 */
package net.azib.ipscan.gui;

import java.util.Iterator;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.NamedListConfig;
import net.azib.ipscan.gui.util.LayoutHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * EditFavoritesDialog
 *
 * @author anton
 */
public class EditFavoritesDialog extends AbstractModalDialog {

	private List favoritesList;
	
	public EditFavoritesDialog() {
		createShell();
	}
	
	/**
	 * This method initializes shell
	 */
	private void createShell() {
		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

		shell.setText(Labels.getLabel("title.favorite.edit"));
		shell.setLayout(LayoutHelper.formLayout(10, 10, 4));		
		
		Label messageLabel = new Label(shell, SWT.NONE);
		messageLabel.setText(Labels.getLabel("text.favorite.edit"));
		
		favoritesList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		favoritesList.setLayoutData(LayoutHelper.formData(330, 200, new FormAttachment(0), null, new FormAttachment(messageLabel), null));
		for (Iterator i = Config.getFavoritesConfig().iterateNames(); i.hasNext();) {
			String name = (String) i.next();
			favoritesList.add(name);
		}
		
		Button upButton = new Button(shell, SWT.NONE);
		upButton.setText(Labels.getLabel("button.up"));		
		upButton.addListener(SWT.Selection, new UpButtonListener(favoritesList));
		
		Button downButton = new Button(shell, SWT.NONE);
		downButton.setText(Labels.getLabel("button.down"));
		downButton.addListener(SWT.Selection, new DownButtonListener(favoritesList));
		
		Button deleteButton = new Button(shell, SWT.NONE);
		deleteButton.setText(Labels.getLabel("button.delete"));
		deleteButton.addListener(SWT.Selection, new DeleteButtonListener());
		
		upButton.setLayoutData(LayoutHelper.formData(new FormAttachment(favoritesList), new FormAttachment(deleteButton, 0, SWT.RIGHT), new FormAttachment(messageLabel), null));
		downButton.setLayoutData(LayoutHelper.formData(new FormAttachment(favoritesList), new FormAttachment(deleteButton, 0, SWT.RIGHT), new FormAttachment(upButton), null));
		deleteButton.setLayoutData(LayoutHelper.formData(new FormAttachment(favoritesList), null, new FormAttachment(downButton, 10), null));
		
		Button okButton = new Button(shell, SWT.NONE);
		okButton.setText(Labels.getLabel("button.OK"));		
		
		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(Labels.getLabel("button.cancel"));		

		positionButtonsInFormLayout(okButton, cancelButton, favoritesList);
		
		shell.pack();
		
		okButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				saveFavorites();
				shell.close();
			}
		});
		cancelButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				shell.close();
			}
		});
		
	}
	
	private void saveFavorites() {
		NamedListConfig favoritesConfig = Config.getFavoritesConfig();
		favoritesConfig.update(favoritesList.getItems());
		favoritesConfig.store();
	}
	
	private class DeleteButtonListener implements Listener {
		public void handleEvent(Event event) {
			favoritesList.remove(favoritesList.getSelectionIndices());			
		}
	}

}
