/**
 * 
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.OpenersConfig;
import net.azib.ipscan.config.OpenersConfig.Opener;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.gui.util.LayoutHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;

import static net.azib.ipscan.gui.util.LayoutHelper.formData;

/**
 * EditOpenersDialog
 *
 * @author Anton Keks
 */
public class EditOpenersDialog extends AbstractModalDialog {
	private final FetcherRegistry fetcherRegistry;
	private final OpenersConfig openersConfig;

	private List openersList;
	private Group editFieldsGroup;
	private Text openerNameText;
	private Text openerStringText;
	private Text workingDirText;
	private Button isInTerminalCheckbox;
	private int currentSelectionIndex;
	
	public EditOpenersDialog(FetcherRegistry fetcherRegistry, OpenersConfig openersConfig) {
		this.fetcherRegistry = fetcherRegistry;
		this.openersConfig = openersConfig;
	}

	@Override
	protected void populateShell() {
		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

		shell.setText(Labels.getLabel("title.openers.edit"));
		shell.setLayout(LayoutHelper.formLayout(10, 10, 4));		
		
		Label messageLabel = new Label(shell, SWT.NONE);
		messageLabel.setText(Labels.getLabel("text.openers.edit"));
		
		openersList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		editFieldsGroup = new Group(shell, SWT.NONE);

		openersList.setLayoutData(formData(135, 200, null, null, new FormAttachment(messageLabel, 10), new FormAttachment(editFieldsGroup, 0, SWT.BOTTOM)));
		for (String name : openersConfig) {
			openersList.add(name);
		}
		openersList.addListener(SWT.Selection, new ItemSelectListener());
		
		Button upButton = new Button(shell, SWT.NONE);
		upButton.setText(Labels.getLabel("button.up"));
		upButton.addListener(SWT.Selection, new UpButtonListener(openersList) {
			@Override public void handleEvent(Event event) {
				super.handleEvent(event);
				currentSelectionIndex = openersList.getSelectionIndex();
			}
		});
		
		Button downButton = new Button(shell, SWT.NONE);
		downButton.setText(Labels.getLabel("button.down"));	
		downButton.addListener(SWT.Selection, new DownButtonListener(openersList) {
			@Override public void handleEvent(Event event) {
				super.handleEvent(event);
				currentSelectionIndex = openersList.getSelectionIndex();
			}
		});
		
		Button addButton = new Button(shell, SWT.NONE);
		addButton.setText(Labels.getLabel("button.add"));
		addButton.addListener(SWT.Selection, new AddButtonListener());

		Button deleteButton = new Button(shell, SWT.NONE);
		deleteButton.setText(Labels.getLabel("button.delete"));
		deleteButton.addListener(SWT.Selection, new DeleteButtonListener());

		upButton.setLayoutData(formData(new FormAttachment(openersList), new FormAttachment(deleteButton, 0, SWT.RIGHT), new FormAttachment(messageLabel, 10), null));
		downButton.setLayoutData(formData(new FormAttachment(openersList), new FormAttachment(deleteButton, 0, SWT.RIGHT), new FormAttachment(upButton), null));
		addButton.setLayoutData(formData(new FormAttachment(openersList), new FormAttachment(deleteButton, 0, SWT.RIGHT), new FormAttachment(downButton, 16), null));
		deleteButton.setLayoutData(formData(new FormAttachment(openersList), null, new FormAttachment(addButton), null));
						
		editFieldsGroup.setLayoutData(formData(new FormAttachment(upButton, 10), null, new FormAttachment(messageLabel, 10), null));
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.fill = true;
		rowLayout.justify = true; 
		rowLayout.marginTop = 13;
		editFieldsGroup.setLayout(rowLayout);
		
		Label openerNameLabel = new Label(editFieldsGroup, SWT.NONE);
		openerNameLabel.setText(Labels.getLabel("text.openers.name"));
		openerNameLabel.setSize(SWT.DEFAULT, 18);
		openerNameText = new Text(editFieldsGroup, SWT.BORDER);
		openerNameText.setSize(SWT.DEFAULT, 22);
		openerNameText.addListener(SWT.KeyUp, new OpenerNameChange());

		isInTerminalCheckbox = new Button(editFieldsGroup, SWT.CHECK);
		isInTerminalCheckbox.setText(Labels.getLabel("text.openers.inTerminal"));
		isInTerminalCheckbox.setSize(SWT.DEFAULT, 18);

		Label openerStringLabel = new Label(editFieldsGroup, SWT.NONE);
		openerStringLabel.setText(Labels.getLabel("text.openers.string"));
		openerStringLabel.setSize(SWT.DEFAULT, 18);
		openerStringText = new Text(editFieldsGroup, SWT.BORDER);
		openerStringText.setSize(SWT.DEFAULT, 22);
		
		Button hintButton = new Button(editFieldsGroup, SWT.NONE);
		hintButton.setText(Labels.getLabel("text.openers.hint"));
		hintButton.addListener(SWT.Selection, new HintButtonListener());
		
		Label openerDirLabel = new Label(editFieldsGroup, SWT.NONE);
		openerDirLabel.setText(Labels.getLabel("text.openers.directory"));
		openerDirLabel.setSize(SWT.DEFAULT, 18);
		workingDirText = new Text(editFieldsGroup, SWT.BORDER);
		workingDirText.setSize(SWT.DEFAULT, 22);
				
		editFieldsGroup.layout();
		editFieldsGroup.pack();

		Button okButton = new Button(shell, SWT.NONE);
		okButton.setText(Labels.getLabel("button.OK"));

		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(Labels.getLabel("button.cancel"));

		positionButtonsInFormLayout(okButton, cancelButton, editFieldsGroup);

		shell.pack();

		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				saveOpeners();
				shell.close();
			}
		});
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				shell.close();
			}
		});

		openersList.select(0);
		loadFieldsForSelection();
	}
	
	private void saveOpeners() {
		// save any possible changes in text boxes
		saveCurrentFields();			

		// now save everything else (order, etc)
		openersConfig.update(openersList.getItems());
		openersConfig.store();
	}

	private void saveCurrentFields() {
		if (currentSelectionIndex < 0) return;

		String openerName = openerNameText.getText();
		File workingDir = workingDirText.getText().length() > 0 ? new File(workingDirText.getText()) : null;
		openersConfig.add(openerName, new OpenersConfig.Opener(openerStringText.getText(), isInTerminalCheckbox.getSelection(), workingDir));
		openersList.setItem(currentSelectionIndex, openerName);
	}
	
	private void loadFieldsForSelection() {
		currentSelectionIndex = openersList.getSelectionIndex();
		if (currentSelectionIndex < 0) return;
    
		String openerName = openersList.getItem(currentSelectionIndex);
		editFieldsGroup.setText(openerName);
		Opener opener = openersConfig.getOpener(openerName);
		openerNameText.setText(openerName);
		openerStringText.setText(opener.execString);
		workingDirText.setText(opener.workingDir != null ? opener.workingDir.toString() : "");
		isInTerminalCheckbox.setSelection(opener.inTerminal);
	}

	class HintButtonListener implements Listener {
		public void handleEvent(Event event) {
			// compose the message with all available fetchers
			StringBuilder message = new StringBuilder(Labels.getLabel("text.openers.hintText"));
			for (Fetcher fetcher : fetcherRegistry.getSelectedFetchers()) {
				message.append("${").append(fetcher.getId()).append("}   - ").append(fetcher.getName()).append('\n');
			}
			
			MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
			mb.setText(Labels.getLabel("title.openers.edit"));
			mb.setMessage(message.toString());
			mb.open();
		}
	}
	
	class DeleteButtonListener implements Listener {
		public void handleEvent(Event event) {
			int oldIndex = openersList.getSelectionIndex();
			openersList.remove(openersList.getSelectionIndices());
			if (oldIndex >= openersList.getItemCount()) oldIndex = openersList.getItemCount()-1;
			openersList.setSelection(oldIndex);
			loadFieldsForSelection();
		}
	}

	class AddButtonListener implements Listener {
		public void handleEvent(Event event) {
			saveCurrentFields();

			currentSelectionIndex = openersList.getSelectionIndex();
			if (currentSelectionIndex < 0) {
				currentSelectionIndex = openersList.getItemCount();
			}
			String newName = Labels.getLabel("text.openers.new");
			openersList.add(newName, currentSelectionIndex);
			openersList.setSelection(currentSelectionIndex);

			// reset fields
			editFieldsGroup.setText(newName);
			openerNameText.setText(newName);
			openerStringText.setText("${fetcher.ip}");
			workingDirText.setText("");
			isInTerminalCheckbox.setSelection(false);

			openerNameText.forceFocus();
			openerNameText.setSelection(0, newName.length());
		}
	}

	class ItemSelectListener implements Listener {
		public void handleEvent(Event event) {
			if (openersList.getSelectionCount() == 0)
				return;

			saveCurrentFields();
			loadFieldsForSelection();
		}
	}
	
	class OpenerNameChange implements Listener {
		public void handleEvent(Event event) {
			if (currentSelectionIndex < 0) return;
			String name = openerNameText.getText();
			editFieldsGroup.setText(name);
			openersList.setItem(currentSelectionIndex, name);
		}
	}
}
