/**
 * 
 */
package net.azib.ipscan.gui;

import java.io.File;
import java.util.Iterator;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.OpenersConfig;
import net.azib.ipscan.config.OpenersConfig.Opener;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * EditOpenersDialog
 *
 * @author anton
 */
public class EditOpenersDialog extends AbstractModalDialog {

	FetcherRegistry fetcherResgitry;
	private List openersList;
	private Group editGroup;
	private Text openerNameText;
	private Text openerStringText;
	private Text openerDirText;
	private Button isCommandlineCheckbox;
	
	private SaveButtonListener saveButtonListener;
	
	public EditOpenersDialog(FetcherRegistry fetcherRegistry) {
		this.fetcherResgitry = fetcherRegistry;
		createShell();
	}
	
	/**
	 * This method initializes shell
	 */
	private void createShell() {
		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

		shell.setText(Labels.getLabel("title.openers.edit"));
		shell.setSize(new Point(405, 297));		
		shell.setLayout(null);		
		
		Label messageLabel = new Label(shell, SWT.NONE);
		messageLabel.setText(Labels.getLabel("text.openers.edit"));		
		messageLabel.setBounds(new Rectangle(10, 10, 282, 14));
		
		openersList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		openersList.setBounds(new Rectangle(10, 30, 135, 200));
		for (Iterator i = Config.getOpenersConfig().iterateNames(); i.hasNext();) {
			String name = (String) i.next();
			openersList.add(name);
		}
		openersList.addListener(SWT.Selection, new ItemSelectListener());
		
		Button upButton = new Button(shell, SWT.NONE);
		upButton.setText(Labels.getLabel("button.up"));		
		upButton.setBounds(new Rectangle(150, 30, 40, 25));
		upButton.addListener(SWT.Selection, new UpButtonListener());
		
		Button downButton = new Button(shell, SWT.NONE);
		downButton.setText(Labels.getLabel("button.down"));		
		downButton.setBounds(new Rectangle(150, 60, 40, 25));
		downButton.addListener(SWT.Selection, new DownButtonListener());
		
		Button insertButton = new Button(shell, SWT.NONE);
		insertButton.setText(Labels.getLabel("button.insert"));		
		insertButton.setBounds(new Rectangle(150, 105, 40, 25));
		insertButton.addListener(SWT.Selection, new InsertButtonListener());

		Button deleteButton = new Button(shell, SWT.NONE);
		deleteButton.setText(Labels.getLabel("button.delete"));		
		deleteButton.setBounds(new Rectangle(150, 135, 40, 25));
		deleteButton.addListener(SWT.Selection, new DeleteButtonListener());

		Button saveButton = new Button(shell, SWT.NONE);
		saveButton.setText(Labels.getLabel("button.save"));		
		saveButton.setBounds(new Rectangle(150, 165, 40, 25));
		saveButtonListener = new SaveButtonListener();
		saveButton.addListener(SWT.Selection, saveButtonListener);
		
		Button okButton = new Button(shell, SWT.NONE);
		okButton.setText(Labels.getLabel("button.OK"));		
		okButton.setBounds(new Rectangle(180, 238, 75, 25));
		shell.setDefaultButton(okButton);
		
		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(Labels.getLabel("button.cancel"));		
		cancelButton.setBounds(new Rectangle(265, 238, 75, 25));
		
		editGroup = new Group(shell, SWT.NONE);
		editGroup.setBounds(205, 30, 185, 200);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.fill = true;
		rowLayout.justify = true; 
		rowLayout.marginTop = 13;
		editGroup.setLayout(rowLayout);
		
		Label openerNameLabel = new Label(editGroup, SWT.NONE);
		openerNameLabel.setText(Labels.getLabel("text.openers.name"));
		openerNameLabel.setSize(SWT.DEFAULT, 18);
		openerNameText = new Text(editGroup, SWT.BORDER);
		openerNameText.setSize(SWT.DEFAULT, 22);

		Label openerStringLabel = new Label(editGroup, SWT.NONE);
		openerStringLabel.setText(Labels.getLabel("text.openers.string"));
		openerStringLabel.setSize(SWT.DEFAULT, 18);
		openerStringText = new Text(editGroup, SWT.BORDER);
		openerStringText.setSize(SWT.DEFAULT, 22);
		
		Button hintButton = new Button(editGroup, SWT.NONE);
		hintButton.setText(Labels.getLabel("text.openers.hint"));
		hintButton.addListener(SWT.Selection, new HintButtonListener());
		
		Label openerDirLabel = new Label(editGroup, SWT.NONE);
		openerDirLabel.setText(Labels.getLabel("text.openers.directory"));
		openerDirLabel.setSize(SWT.DEFAULT, 18);
		openerDirText = new Text(editGroup, SWT.BORDER);
		openerDirText.setSize(SWT.DEFAULT, 22);
		
		isCommandlineCheckbox = new Button(editGroup, SWT.CHECK);
		isCommandlineCheckbox.setText(Labels.getLabel("text.openers.isCommandLine"));
		isCommandlineCheckbox.setSize(SWT.DEFAULT, 18);
		
		editGroup.layout();
		
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				saveOpeners();
				shell.close();
			}
		});
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Config.getOpenersConfig().load();
				shell.close();
			}
		});
	}
	
	private void saveOpeners() {
		// save any possible changes in the text boxes
		saveButtonListener.handleEvent(null);
		// now save everything else
		OpenersConfig openersConfig = Config.getOpenersConfig();
		openersConfig.update(openersList.getItems());
		openersConfig.store();
	}
	
	private class HintButtonListener implements Listener {
		
		public void handleEvent(Event event) {
			// compose the message with all available fetchers
			StringBuffer message = new StringBuffer(Labels.getLabel("text.openers.hintText"));
			for (Iterator i = fetcherResgitry.getRegisteredFetchers().iterator(); i.hasNext(); ) {
				String fetcherLabel = ((Fetcher)i.next()).getLabel();
				message.append("${").append(fetcherLabel).append("}   - ").append(Labels.getLabel(fetcherLabel)).append('\n');
			}
			
			MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
			mb.setText(Labels.getLabel("title.openers.edit"));
			mb.setMessage(message.toString());
			mb.open();
		}
	}
	
	private class UpButtonListener implements Listener {

		public void handleEvent(Event event) {
			if (openersList.isSelected(0)) {
				// do not move anything if the first item is selected
				return;
			}
			
			int[] selectedItems = openersList.getSelectionIndices();
			for (int i = 0; i < selectedItems.length; i++) {
				// here, index is always > 0
				int index = selectedItems[i];

				openersList.deselect(index);
				String oldItem = openersList.getItem(index - 1);
				openersList.setItem(index - 1, openersList.getItem(index));
				openersList.setItem(index, oldItem);
				openersList.select(index - 1);
			}
			
			openersList.setTopIndex(selectedItems[0] - 2);
		}
	}

	private class DownButtonListener implements Listener {
		
		public void handleEvent(Event event) {
			if (openersList.isSelected(openersList.getItemCount() - 1)) {
				// do not move anything if the last items is selected
				return;
			}
			
			int[] selectedItems = openersList.getSelectionIndices();
			for (int i = selectedItems.length - 1; i >= 0; i--) {
				// here, index is always < getItemCount()
				int index = selectedItems[i];

				openersList.deselect(index);
				String oldItem = openersList.getItem(index + 1);
				openersList.setItem(index + 1, openersList.getItem(index));
				openersList.setItem(index, oldItem);
				openersList.select(index + 1);
			}
			
			openersList.setTopIndex(selectedItems[0]);
		}
	}
	
	private class DeleteButtonListener implements Listener {
		
		public void handleEvent(Event event) {
			openersList.remove(openersList.getSelectionIndices());			
		}
	}

	private class SaveButtonListener implements Listener {
		
		public void handleEvent(Event event) {
			String openerName = openerNameText.getText();
			if (openerName.length() == 0)
				return;
			
			int selectedItem = openersList.getSelectionIndex();
			if (selectedItem < 0)
				throw new UserErrorException("opener.edit.noSelection");
			
			Config.getOpenersConfig().add(openerName, new OpenersConfig.Opener(openerStringText.getText(), isCommandlineCheckbox.getSelection(), new File(openerDirText.getText())));
			openersList.setItem(selectedItem, openerName);			
		}
	}

	private class InsertButtonListener implements Listener {
		
		public void handleEvent(Event event) {
			int selectionIndex = openersList.getSelectionIndex();
			if (selectionIndex < 0) {
				selectionIndex = openersList.getItemCount();
			}
			openersList.add("", selectionIndex);
			openersList.setSelection(selectionIndex);
		}
	}

	private class ItemSelectListener implements Listener {
		
		public void handleEvent(Event event) {
			int selectionIndex = openersList.getSelectionIndex();
			String openerName = openersList.getItem(selectionIndex);
			editGroup.setText(openerName);
			Opener opener = Config.getOpenersConfig().getOpener(openerName);
			openerNameText.setText(openerName);
			openerStringText.setText(opener.execString);
			openerDirText.setText(opener.workingDir != null ? opener.workingDir.toString() : "");
			isCommandlineCheckbox.setSelection(opener.inTerminal);
		}
	}

}
