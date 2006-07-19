/**
 * 
 */
package net.azib.ipscan.gui;

import java.util.Iterator;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.FavoritesConfig;
import net.azib.ipscan.config.Labels;

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
 * EditFavoritesDialog
 *
 * @author anton
 */
public class EditFavoritesDialog {

	private Shell shell = null;
	private List favoritesList;
	
	public EditFavoritesDialog() {
		createShell();
		shell.open();
	}

	/**
	 * This method initializes shell
	 */
	private void createShell() {
		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

		shell.setText(Labels.getInstance().getString("title.favorite.edit"));
		shell.setSize(new Point(405, 295));		
		shell.setLayout(null);		
		
		Label messageLabel = new Label(shell, SWT.NONE);
		messageLabel.setText(Labels.getInstance().getString("text.favorite.edit"));		
		messageLabel.setBounds(new Rectangle(10, 10, 282, 14));
		
		favoritesList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		favoritesList.setBounds(new Rectangle(10, 30, 330, 200));
		for (Iterator i = Config.getFavoritesConfig().iterateNames(); i.hasNext();) {
			String name = (String) i.next();
			favoritesList.add(name);
		}
		
		Button upButton = new Button(shell, SWT.NONE);
		upButton.setText(Labels.getInstance().getString("button.up"));		
		upButton.setBounds(new Rectangle(350, 30, 40, 25));
		upButton.addListener(SWT.Selection, new UpButtonListener());
		
		Button downButton = new Button(shell, SWT.NONE);
		downButton.setText(Labels.getInstance().getString("button.down"));		
		downButton.setBounds(new Rectangle(350, 60, 40, 25));
		downButton.addListener(SWT.Selection, new DownButtonListener());
		
		Button deleteButton = new Button(shell, SWT.NONE);
		deleteButton.setText(Labels.getInstance().getString("button.delete"));		
		deleteButton.setBounds(new Rectangle(350, 105, 40, 25));
		deleteButton.addListener(SWT.Selection, new DeleteButtonListener());
		
		Button okButton = new Button(shell, SWT.NONE);
		okButton.setText(Labels.getInstance().getString("button.OK"));		
		okButton.setBounds(new Rectangle(180, 240, 75, 22));
		shell.setDefaultButton(okButton);
		
		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(Labels.getInstance().getString("button.cancel"));		
		cancelButton.setBounds(new Rectangle(265, 240, 75, 22));		
		
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
		FavoritesConfig favoritesConfig = Config.getFavoritesConfig();
		favoritesConfig.update(favoritesList.getItems());
		favoritesConfig.store();
	}
	
	private class UpButtonListener implements Listener {

		public void handleEvent(Event event) {
			if (favoritesList.isSelected(0)) {
				// do not move anything if the first item is selected
				return;
			}
			
			int[] selectedItems = favoritesList.getSelectionIndices();
			for (int i = 0; i < selectedItems.length; i++) {
				// here, index is always > 0
				int index = selectedItems[i];

				favoritesList.deselect(index);
				String oldItem = favoritesList.getItem(index - 1);
				favoritesList.setItem(index - 1, favoritesList.getItem(index));
				favoritesList.setItem(index, oldItem);
				favoritesList.select(index - 1);
			}
			
			favoritesList.setTopIndex(selectedItems[0] - 2);
		}
	}

	private class DownButtonListener implements Listener {
		
		public void handleEvent(Event event) {
			if (favoritesList.isSelected(favoritesList.getItemCount() - 1)) {
				// do not move anything if the last items is selected
				return;
			}
			
			int[] selectedItems = favoritesList.getSelectionIndices();
			for (int i = selectedItems.length - 1; i >= 0; i--) {
				// here, index is always < getItemCount()
				int index = selectedItems[i];

				favoritesList.deselect(index);
				String oldItem = favoritesList.getItem(index + 1);
				favoritesList.setItem(index + 1, favoritesList.getItem(index));
				favoritesList.setItem(index, oldItem);
				favoritesList.select(index + 1);
			}
			
			favoritesList.setTopIndex(selectedItems[0]);
		}
	}
	
	private class DeleteButtonListener implements Listener {
		
		public void handleEvent(Event event) {
			favoritesList.remove(favoritesList.getSelectionIndices());			
		}
	}

}
